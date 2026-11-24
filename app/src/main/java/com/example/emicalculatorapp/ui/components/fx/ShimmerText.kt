package com.example.emicalculatorapp.ui.components.fx

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import com.example.emicalculatorapp.data.settings.EffectsLevel
import com.example.emicalculatorapp.ui.theme.ElectricViolet
import com.example.emicalculatorapp.ui.theme.LocalEffectsLevel
import com.example.emicalculatorapp.ui.theme.LocalReduceMotion
import com.example.emicalculatorapp.ui.theme.LuminousCyan
import com.example.emicalculatorapp.ui.theme.TextPrimary
import kotlinx.coroutines.delay

/**
 * Text component with an periodic gradient shimmer sweep across key headings.
 * Runs on Full effects level only (one sweep every few seconds); static on Balanced & Lite.
 * Evaluates gradient position inside drawWithContent to prevent recomposition.
 */
@Composable
fun ShimmerText(
    text: String,
    modifier: Modifier = Modifier,
    fontFamily: FontFamily? = null,
    fontWeight: FontWeight? = null,
    fontSize: TextUnit = TextUnit.Unspecified,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    baseColor: Color = TextPrimary,
    shimmerColor: Color = LuminousCyan
) {
    val effectsLevel = LocalEffectsLevel.current
    val reduceMotion = LocalReduceMotion.current
    val shouldSweep = !reduceMotion && effectsLevel == EffectsLevel.FULL

    if (!shouldSweep) {
        Text(
            text = text,
            fontFamily = fontFamily,
            fontWeight = fontWeight,
            fontSize = fontSize,
            letterSpacing = letterSpacing,
            color = baseColor,
            modifier = modifier
        )
        return
    }

    val sweepProgress = remember { Animatable(-0.5f) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3500L) // Wait interval between sweeps
            sweepProgress.snapTo(-0.5f)
            sweepProgress.animateTo(
                targetValue = 1.5f,
                animationSpec = tween(durationMillis = 900, easing = LinearEasing)
            )
        }
    }

    Box(
        modifier = modifier
            .graphicsLayer { alpha = 0.99f } // Needed for SrcIn blend mode
            .drawWithContent {
                drawContent()
                val currentP = sweepProgress.value
                val w = size.width
                val h = size.height

                if (currentP in -0.5f..1.5f) {
                    val startX = w * currentP - 60f
                    val endX = w * currentP + 60f
                    val sweepBrush = Brush.linearGradient(
                        colors = listOf(Color.Transparent, shimmerColor.copy(alpha = 0.65f), Color.Transparent),
                        start = Offset(startX, 0f),
                        end = Offset(endX, h)
                    )
                    drawRect(
                        brush = sweepBrush,
                        blendMode = BlendMode.SrcIn
                    )
                }
            }
    ) {
        Text(
            text = text,
            fontFamily = fontFamily,
            fontWeight = fontWeight,
            fontSize = fontSize,
            letterSpacing = letterSpacing,
            color = baseColor
        )
    }
}
