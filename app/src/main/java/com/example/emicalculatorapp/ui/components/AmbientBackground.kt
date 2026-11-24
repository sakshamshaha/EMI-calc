package com.example.emicalculatorapp.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.emicalculatorapp.data.settings.EffectsLevel
import com.example.emicalculatorapp.ui.components.fx.ParallaxCalculator
import com.example.emicalculatorapp.ui.theme.CanvasColor
import com.example.emicalculatorapp.ui.theme.ElectricViolet
import com.example.emicalculatorapp.ui.theme.HotPink
import com.example.emicalculatorapp.ui.theme.LocalEffectsLevel
import com.example.emicalculatorapp.ui.theme.LocalReduceMotion
import com.example.emicalculatorapp.ui.theme.LuminousCyan

/**
 * Animated Ambient Cosmic Background with soft drifting photon orbs and pre-built grid mesh.
 * Reads animated pulse states lazily inside onDrawBehind to prevent recompositions.
 * Respects animation budget:
 * - Full: 2 background pulse animations + parallax
 * - Balanced: 1 background pulse animation + parallax
 * - Lite / Reduced Motion: 0 animations (static cosmic background)
 */
@Composable
fun AmbientBackground(
    modifier: Modifier = Modifier,
    enableAnimation: Boolean = true,
    scrollOffsetProvider: () -> Float = { 0f }
) {
    val effectsLevel = LocalEffectsLevel.current
    val reduceMotion = LocalReduceMotion.current
    val shouldAnimate = enableAnimation && !reduceMotion && effectsLevel != EffectsLevel.LITE
    val isFull = effectsLevel == EffectsLevel.FULL

    val transition = if (shouldAnimate) {
        rememberInfiniteTransition(label = "AmbientTransition")
    } else null

    // Balanced runs 1 animation (Orb1Pulse); Full runs 2 (Orb1Pulse & Orb2Pulse); Lite runs 0.
    val pulse1State: State<Float>? = transition?.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Orb1Pulse"
    )

    val pulse2State: State<Float>? = if (shouldAnimate && isFull) {
        transition?.animateFloat(
            initialValue = 1.10f,
            targetValue = 0.90f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 5500, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "Orb2Pulse"
        )
    } else null

    val isParallaxAllowed = !reduceMotion && effectsLevel != EffectsLevel.LITE

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasColor)
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .drawWithCache {
                    val w = size.width
                    val h = size.height
                    val step = 48.dp.toPx()

                    // Prebuild grid path once per size change (not on every frame)
                    val gridPath = Path().apply {
                        var x = 0f
                        while (x < w) {
                            moveTo(x, 0f)
                            lineTo(x, h)
                            x += step
                        }
                        var y = 0f
                        while (y < h) {
                            moveTo(0f, y)
                            lineTo(w, y)
                            y += step
                        }
                    }
                    val gridStroke = Stroke(width = 1f)
                    val gridColor = Color.White.copy(alpha = 0.025f)

                    onDrawBehind {
                        val p1 = pulse1State?.value ?: 1f
                        val p2 = pulse2State?.value ?: 1f

                        val currentOffset = scrollOffsetProvider()
                        val parallaxOrb1 = if (isParallaxAllowed) ParallaxCalculator.calculateParallaxOffset(currentOffset, 0.10f) else 0f
                        val parallaxOrb2 = if (isParallaxAllowed) ParallaxCalculator.calculateParallaxOffset(currentOffset, 0.20f) else 0f
                        val parallaxOrb3 = if (isParallaxAllowed) ParallaxCalculator.calculateParallaxOffset(currentOffset, 0.15f) else 0f

                        val orb1Center = Offset(w * 0.15f, h * 0.1f + parallaxOrb1)
                        val orb2Center = Offset(w * 0.85f, h * 0.45f + parallaxOrb2)
                        val orb3Center = Offset(w * 0.1f, h * 0.85f + parallaxOrb3)
                        val orb3Radius = w * 0.38f

                        // Orb 1: Violet Top-Left
                        drawCircle(
                            color = ElectricViolet.copy(alpha = 0.15f),
                            radius = (w * 0.45f) * p1,
                            center = orb1Center
                        )

                        // Orb 2: Cyan Middle-Right
                        drawCircle(
                            color = LuminousCyan.copy(alpha = 0.12f),
                            radius = (w * 0.40f) * p2,
                            center = orb2Center
                        )

                        // Orb 3: Hot Pink Bottom-Left
                        drawCircle(
                            color = HotPink.copy(alpha = 0.10f),
                            radius = orb3Radius,
                            center = orb3Center
                        )

                        // Single drawPath call for the entire grid
                        drawPath(
                            path = gridPath,
                            color = gridColor,
                            style = gridStroke
                        )
                    }
                }
        )
    }
}
