package com.example.emicalculatorapp.ui.help

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emicalculatorapp.data.settings.EffectsLevel
import com.example.emicalculatorapp.ui.theme.ElectricViolet
import com.example.emicalculatorapp.ui.theme.InterFamily
import com.example.emicalculatorapp.ui.theme.LocalEffectsLevel
import com.example.emicalculatorapp.ui.theme.LocalReduceMotion
import com.example.emicalculatorapp.ui.theme.LuminousCyan
import com.example.emicalculatorapp.ui.theme.SpaceGroteskFamily
import com.example.emicalculatorapp.ui.theme.TextPrimary
import com.example.emicalculatorapp.ui.theme.TextSecondary

@Composable
fun HelpCenterEmptyState(
    query: String,
    modifier: Modifier = Modifier
) {
    val reduceMotion = LocalReduceMotion.current
    val effectsLevel = LocalEffectsLevel.current
    val shouldAnimate = !reduceMotion && effectsLevel != EffectsLevel.LITE

    val infiniteTransition = rememberInfiniteTransition(label = "empty_pulse")
    val pulseFraction by if (shouldAnimate) {
        infiniteTransition.animateFloat(
            initialValue = 0.7f,
            targetValue = 1.3f,
            animationSpec = infiniteRepeatable(
                animation = tween(1800, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse_fraction"
        )
    } else {
        rememberInfiniteTransition(label = "static").animateFloat(
            initialValue = 1f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(1000)),
            label = "static_fraction"
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp, horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Canvas(modifier = Modifier.size(80.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val baseRadius = size.minDimension / 4f

            // Outer pulsing ring
            drawCircle(
                color = ElectricViolet.copy(alpha = 0.25f),
                radius = baseRadius * pulseFraction * 1.3f,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            // Inner cyan ring
            drawCircle(
                color = LuminousCyan.copy(alpha = 0.5f),
                radius = baseRadius * pulseFraction,
                center = center,
                style = Stroke(width = 3.dp.toPx())
            )

            // Search Glass circle
            val glassRadius = baseRadius * 0.7f
            val glassCenter = Offset(center.x - 4.dp.toPx(), center.y - 4.dp.toPx())
            drawCircle(
                color = LuminousCyan,
                radius = glassRadius,
                center = glassCenter,
                style = Stroke(width = 3.5.dp.toPx())
            )

            // Handle
            drawLine(
                color = ElectricViolet,
                start = Offset(glassCenter.x + glassRadius * 0.7f, glassCenter.y + glassRadius * 0.7f),
                end = Offset(glassCenter.x + glassRadius * 1.8f, glassCenter.y + glassRadius * 1.8f),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No answers found for \"$query\"",
            fontFamily = SpaceGroteskFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Try searching with broader terms or submit a question directly using 'Report a problem' below.",
            fontFamily = InterFamily,
            fontSize = 12.sp,
            color = TextSecondary,
            lineHeight = 17.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
