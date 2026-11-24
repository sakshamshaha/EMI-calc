package com.example.emicalculatorapp.ui.saved

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
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emicalculatorapp.data.settings.EffectsLevel
import com.example.emicalculatorapp.ui.components.fx.ShineButton
import com.example.emicalculatorapp.ui.theme.ElectricViolet
import com.example.emicalculatorapp.ui.theme.InterFamily
import com.example.emicalculatorapp.ui.theme.LocalEffectsLevel
import com.example.emicalculatorapp.ui.theme.LocalReduceMotion
import com.example.emicalculatorapp.ui.theme.LuminousCyan
import com.example.emicalculatorapp.ui.theme.MintGreen
import com.example.emicalculatorapp.ui.theme.SpaceGroteskFamily
import com.example.emicalculatorapp.ui.theme.TextPrimary
import com.example.emicalculatorapp.ui.theme.TextSecondary
import kotlin.math.cos
import kotlin.math.sin

/**
 * Animated native Canvas illustration of a geometric quantum holographic emblem.
 * Rebuilt purely via Canvas draw primitives without static raster assets.
 */
@Composable
fun SavedQuotesEmptyState(
    onNavigateToCalculate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val effectsLevel = LocalEffectsLevel.current
    val reduceMotion = LocalReduceMotion.current
    val shouldAnimate = !reduceMotion && effectsLevel == EffectsLevel.FULL

    val angleState: State<Float> = if (shouldAnimate) {
        val transition = rememberInfiniteTransition(label = "EmblemRotation")
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(8000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "Angle"
        )
    } else {
        remember { mutableFloatStateOf(45f) }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Holographic Emblem Canvas Animation
        Canvas(modifier = Modifier.size(160.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val outerRadius = size.width * 0.42f
            val innerRadius = size.width * 0.26f
            val rotationDegrees = angleState.value

            // Outer Dashed Orbital Ring
            drawCircle(
                color = ElectricViolet.copy(alpha = 0.25f),
                radius = outerRadius,
                center = center,
                style = Stroke(width = 3f)
            )

            // Inner Accent Ring
            drawCircle(
                color = LuminousCyan.copy(alpha = 0.35f),
                radius = innerRadius,
                center = center,
                style = Stroke(width = 2.5f)
            )

            // Center Core Dot
            drawCircle(
                color = MintGreen,
                radius = 6f,
                center = center
            )

            // Rotating Orbital Nodes
            val rad1 = Math.toRadians(rotationDegrees.toDouble())
            val rad2 = Math.toRadians((rotationDegrees + 120f).toDouble())
            val rad3 = Math.toRadians((rotationDegrees + 240f).toDouble())

            val p1 = Offset(center.x + (outerRadius * cos(rad1)).toFloat(), center.y + (outerRadius * sin(rad1)).toFloat())
            val p2 = Offset(center.x + (outerRadius * cos(rad2)).toFloat(), center.y + (outerRadius * sin(rad2)).toFloat())
            val p3 = Offset(center.x + (outerRadius * cos(rad3)).toFloat(), center.y + (outerRadius * sin(rad3)).toFloat())

            drawCircle(color = LuminousCyan, radius = 5f, center = p1)
            drawCircle(color = MintGreen, radius = 4f, center = p2)
            drawCircle(color = ElectricViolet, radius = 4.5f, center = p3)

            // Dynamic Crosshair Lines
            val crosshairLen = outerRadius * 0.4f
            drawLine(
                color = LuminousCyan.copy(alpha = 0.5f),
                start = Offset(center.x - crosshairLen, center.y),
                end = Offset(center.x + crosshairLen, center.y),
                strokeWidth = 2f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = LuminousCyan.copy(alpha = 0.5f),
                start = Offset(center.x, center.y - crosshairLen),
                end = Offset(center.x, center.y + crosshairLen),
                strokeWidth = 2f,
                cap = StrokeCap.Round
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "No Saved Quotes Yet",
            fontFamily = SpaceGroteskFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Save quotes from the Calculator or Compare tab to track loan variations, pin top choices, and reload terms with a single tap.",
            fontFamily = InterFamily,
            fontSize = 13.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        ShineButton(
            text = "Go to Calculate",
            onClick = onNavigateToCalculate,
            modifier = Modifier.fillMaxWidth(0.7f).height(44.dp)
        )
    }
}
