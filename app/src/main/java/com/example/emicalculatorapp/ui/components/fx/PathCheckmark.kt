package com.example.emicalculatorapp.ui.components.fx

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.emicalculatorapp.data.settings.EffectsLevel
import com.example.emicalculatorapp.ui.theme.LocalEffectsLevel
import com.example.emicalculatorapp.ui.theme.LocalReduceMotion
import com.example.emicalculatorapp.ui.theme.LuminousCyan

/**
 * Animated path-drawing vector checkmark.
 * Rebuilt natively from Uiverse.io path-draw success state patterns.
 * Draws dynamically from start to finish using PathMeasure.
 */
@Composable
fun PathCheckmark(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    strokeWidth: Dp = 4.dp,
    color: Color = LuminousCyan,
    onComplete: (() -> Unit)? = null
) {
    val effectsLevel = LocalEffectsLevel.current
    val reduceMotion = LocalReduceMotion.current
    val progress = remember { Animatable(if (reduceMotion || effectsLevel == EffectsLevel.LITE) 1f else 0f) }

    LaunchedEffect(Unit) {
        if (!reduceMotion && effectsLevel != EffectsLevel.LITE) {
            progress.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
            onComplete?.invoke()
        }
    }

    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Checkmark geometry points: start at left, dip to bottom center, rise up right
        val fullPath = Path().apply {
            moveTo(w * 0.22f, h * 0.52f)
            lineTo(w * 0.42f, h * 0.72f)
            lineTo(w * 0.78f, h * 0.32f)
        }

        val pathMeasure = PathMeasure()
        pathMeasure.setPath(fullPath, false)
        val length = pathMeasure.length

        val segmentPath = Path()
        pathMeasure.getSegment(0f, length * progress.value, segmentPath, true)

        drawPath(
            path = segmentPath,
            color = color,
            style = Stroke(
                width = strokeWidth.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}
