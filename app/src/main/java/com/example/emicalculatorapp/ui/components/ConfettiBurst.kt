package com.example.emicalculatorapp.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.emicalculatorapp.ui.theme.HotPink
import com.example.emicalculatorapp.ui.theme.LocalReduceMotion
import com.example.emicalculatorapp.ui.theme.LuminousCyan
import com.example.emicalculatorapp.ui.theme.PrimaryLight
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class ConfettiParticle(
    val color: Color,
    val size: Float,
    val angle: Double,
    val distance: Float
)

/**
 * Neon Confetti Particle Burst for Save & Share action.
 * Emits ~24 particles in violet, cyan, pink, and white outward from center.
 * Skips animation if LocalReduceMotion is enabled.
 */
@Composable
fun ConfettiBurst(
    trigger: Boolean,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!trigger) return

    val reduceMotion = LocalReduceMotion.current

    if (reduceMotion) {
        LaunchedEffect(Unit) {
            onComplete()
        }
        return
    }

    val particles = remember {
        val colors = listOf(PrimaryLight, LuminousCyan, HotPink, Color.White)
        List(24) {
            ConfettiParticle(
                color = colors[Random.nextInt(colors.size)],
                size = Random.nextFloat() * 6f + 4f,
                angle = Random.nextDouble() * 2 * Math.PI,
                distance = Random.nextFloat() * 120f + 40f
            )
        }
    }

    val progress = remember { Animatable(0f) }

    LaunchedEffect(trigger) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
        )
        onComplete()
    }

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val currentProgress = progress.value
            val alpha = (1f - currentProgress).coerceIn(0f, 1f)
            val center = Offset(size.width / 2f, size.height / 2f)

            particles.forEach { p ->
                val currentDist = p.distance * currentProgress
                val x = center.x + (currentDist * cos(p.angle)).toFloat()
                val y = center.y + (currentDist * sin(p.angle)).toFloat()
                val currentSize = p.size * (1f - currentProgress * 0.7f)

                drawCircle(
                    color = p.color.copy(alpha = alpha),
                    radius = currentSize,
                    center = Offset(x, y)
                )
            }
        }
    }
}
