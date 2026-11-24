package com.example.emicalculatorapp.ui.components.fx

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.emicalculatorapp.data.settings.EffectsLevel
import com.example.emicalculatorapp.ui.theme.ElectricViolet
import com.example.emicalculatorapp.ui.theme.HotPink
import com.example.emicalculatorapp.ui.theme.LocalEffectsLevel
import com.example.emicalculatorapp.ui.theme.LocalReduceMotion
import com.example.emicalculatorapp.ui.theme.LuminousCyan
import com.example.emicalculatorapp.ui.theme.SurfaceColor

import androidx.compose.ui.graphics.drawscope.rotate

/**
 * Cyber-luxe card with a rotating sweep gradient perimeter beam.
 * Rebuilt natively from Uiverse.io border beam patterns.
 * Respects LocalEffectsLevel and LocalReduceMotion:
 * - LITE / ReduceMotion: Static subtle border.
 * - BALANCED / FULL: Silky rotating laser beam.
 * Reads animated angle lazily inside onDrawWithContent to avoid recompositions.
 */
@Composable
fun SweepBorderCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    borderWidth: Dp = 1.5.dp,
    backgroundColor: Color = SurfaceColor.copy(alpha = 0.85f),
    gradientColors: List<Color> = listOf(
        LuminousCyan,
        ElectricViolet,
        HotPink,
        LuminousCyan
    ),
    durationMillis: Int = 4000,
    content: @Composable BoxScope.() -> Unit
) {
    val effectsLevel = LocalEffectsLevel.current
    val reduceMotion = LocalReduceMotion.current
    val shouldAnimate = !reduceMotion && effectsLevel != EffectsLevel.LITE

    val animatedAngleState = if (shouldAnimate) {
        val infiniteTransition = rememberInfiniteTransition(label = "SweepBorderInfinite")
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = durationMillis, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "SweepBorderAngle"
        )
    } else null

    Box(
        modifier = modifier
            .drawWithCache {
                val strokePx = borderWidth.toPx()
                val sweepBrush = if (shouldAnimate) {
                    Brush.sweepGradient(colors = gradientColors)
                } else {
                    Brush.linearGradient(
                        colors = listOf(
                            LuminousCyan.copy(alpha = 0.4f),
                            ElectricViolet.copy(alpha = 0.4f)
                        )
                    )
                }

                onDrawWithContent {
                    drawContent()
                    val currentAngle = animatedAngleState?.value ?: 45f
                    // Draw outer rotating border stroke
                    rotate(currentAngle) {
                        drawRoundRect(
                            brush = sweepBrush,
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                                20.dp.toPx(),
                                20.dp.toPx()
                            ),
                            style = Stroke(width = strokePx, cap = StrokeCap.Round)
                        )
                    }
                }
            }
            .clip(shape)
            .background(backgroundColor)
            .padding(borderWidth)
    ) {
        content()
    }
}
