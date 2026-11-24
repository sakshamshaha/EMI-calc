package com.example.emicalculatorapp.ui.components.fx

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.example.emicalculatorapp.data.settings.EffectsLevel
import com.example.emicalculatorapp.ui.theme.LocalEffectsLevel
import com.example.emicalculatorapp.ui.theme.LocalReduceMotion
import com.example.emicalculatorapp.ui.theme.SurfaceContainerHigh

/**
 * Sweeping shimmer skeleton loading placeholder.
 * Rebuilt natively from Uiverse.io skeleton/shimmer patterns.
 * Reads animated progress lazily inside onDrawWithContent to prevent recompositions.
 */
@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    baseColor: Color = SurfaceContainerHigh.copy(alpha = 0.5f),
    highlightColor: Color = Color.White.copy(alpha = 0.15f)
) {
    val effectsLevel = LocalEffectsLevel.current
    val reduceMotion = LocalReduceMotion.current
    val shouldAnimate = !reduceMotion && effectsLevel != EffectsLevel.LITE

    val progressState: State<Float>? = if (shouldAnimate) {
        val infiniteTransition = rememberInfiniteTransition(label = "ShimmerTransition")
        infiniteTransition.animateFloat(
            initialValue = -1f,
            targetValue = 2f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1400, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "ShimmerProgress"
        )
    } else null

    Box(
        modifier = modifier
            .clip(shape)
            .background(baseColor)
            .drawWithCache {
                val width = size.width
                val height = size.height

                onDrawWithContent {
                    drawContent()
                    if (shouldAnimate && progressState != null) {
                        val currentX = width * progressState.value
                        val brush = Brush.linearGradient(
                            colors = listOf(baseColor, highlightColor, baseColor),
                            start = Offset(currentX - 100f, 0f),
                            end = Offset(currentX + 100f, height)
                        )
                        drawRect(brush = brush)
                    }
                }
            }
    )
}
