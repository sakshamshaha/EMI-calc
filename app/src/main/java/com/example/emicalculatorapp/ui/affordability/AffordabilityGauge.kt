package com.example.emicalculatorapp.ui.affordability

import android.graphics.Paint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emicalculatorapp.model.AffordabilityZone
import com.example.emicalculatorapp.ui.components.GlassCard
import com.example.emicalculatorapp.ui.theme.ElectricViolet
import com.example.emicalculatorapp.ui.theme.HotPink
import com.example.emicalculatorapp.ui.theme.InterFamily
import com.example.emicalculatorapp.ui.theme.LuminousCyan
import com.example.emicalculatorapp.ui.theme.MintGreen
import com.example.emicalculatorapp.ui.theme.SpaceGroteskFamily
import com.example.emicalculatorapp.ui.theme.SurfaceContainerHigh
import com.example.emicalculatorapp.ui.theme.TextMuted
import com.example.emicalculatorapp.ui.theme.TextPrimary
import com.example.emicalculatorapp.ui.theme.TextSecondary
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

/**
 * 180-degree semi-circle speedometer gauge for Debt-to-Income (DTI) obligation ratio.
 * 
 * CRITICAL RULE:
 * Needle animation is driven by a spring, and the animated value is read ONLY
 * inside the Canvas draw scope lambda via [fractionProvider] to avoid recomposition churn.
 */
@Composable
fun AffordabilityGauge(
    ratioPercent: Double,
    zone: AffordabilityZone,
    modifier: Modifier = Modifier
) {
    // Target ratio mapped to 0..1 fraction (where 0% = 0f, 60% = 1f)
    val targetFraction = (ratioPercent.coerceIn(0.0, 60.0) / 60.0).toFloat()

    val animatableFraction = remember { Animatable(0f) }

    LaunchedEffect(targetFraction) {
        animatableFraction.animateTo(
            targetValue = targetFraction,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )
    }

    val zoneColor = when (zone) {
        AffordabilityZone.COMFORTABLE -> MintGreen
        AffordabilityZone.MODERATE -> LuminousCyan
        AffordabilityZone.STRETCH -> HotPink
    }

    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "TOTAL OBLIGATION RATIO",
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 1.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Canvas reads animatableFraction inside draw scope ONLY
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(145.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(145.dp)
                        .semantics {
                            contentDescription = "Total obligation ratio gauge: ${ratioPercent.toInt()} percent, Zone ${zone.name}"
                        }
                ) {
                    val width = size.width
                    val height = size.height
                    val radius = (minOf(width, height * 2f) / 2.2f).coerceAtLeast(60f)
                    val centerX = width / 2f
                    val centerY = height - 12f
                    val arcStrokeWidth = 14f

                    val arcSize = Size(radius * 2f, radius * 2f)
                    val topLeft = Offset(centerX - radius, centerY - radius)

                    // 1. Background Track
                    drawArc(
                        color = SurfaceContainerHigh,
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = arcStrokeWidth, cap = StrokeCap.Round)
                    )

                    // 2. Zone 1: Comfortable (0% to 30% -> 90 degrees)
                    drawArc(
                        color = MintGreen.copy(alpha = 0.85f),
                        startAngle = 180f,
                        sweepAngle = 90f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = arcStrokeWidth, cap = StrokeCap.Butt)
                    )

                    // 3. Zone 2: Moderate (30% to 40% -> 30 degrees)
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(LuminousCyan, ElectricViolet),
                            center = Offset(centerX, centerY)
                        ),
                        startAngle = 270f,
                        sweepAngle = 30f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = arcStrokeWidth, cap = StrokeCap.Butt)
                    )

                    // 4. Zone 3: Stretch (40% to 60% -> 60 degrees)
                    drawArc(
                        color = HotPink.copy(alpha = 0.85f),
                        startAngle = 300f,
                        sweepAngle = 60f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = arcStrokeWidth, cap = StrokeCap.Round)
                    )

                    // 5. Read animated needle fraction exclusively in draw scope
                    val currentFraction = animatableFraction.value
                    val needleAngleDeg = 180f + (currentFraction * 180f)
                    val needleAngleRad = Math.toRadians(needleAngleDeg.toDouble())

                    val needleLength = radius * 0.72f
                    val tipX = centerX + (needleLength * cos(needleAngleRad)).toFloat()
                    val tipY = centerY + (needleLength * sin(needleAngleRad)).toFloat()

                    // Needle glow shadow
                    drawLine(
                        color = zoneColor.copy(alpha = 0.35f),
                        start = Offset(centerX, centerY),
                        end = Offset(tipX, tipY),
                        strokeWidth = 10f,
                        cap = StrokeCap.Round
                    )

                    // Needle primary line
                    drawLine(
                        color = TextPrimary,
                        start = Offset(centerX, centerY),
                        end = Offset(tipX, tipY),
                        strokeWidth = 3.5f,
                        cap = StrokeCap.Round
                    )

                    // Needle pivot center circle
                    drawCircle(
                        color = zoneColor,
                        radius = 8f,
                        center = Offset(centerX, centerY)
                    )
                    drawCircle(
                        color = TextPrimary,
                        radius = 4f,
                        center = Offset(centerX, centerY)
                    )
                }

                // Center readout
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = String.format(Locale.US, "%.1f%%", ratioPercent),
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = zoneColor
                    )
                    Text(
                        text = when (zone) {
                            AffordabilityZone.COMFORTABLE -> "Comfortable (≤30%)"
                            AffordabilityZone.MODERATE -> "Moderate (30-40%)"
                            AffordabilityZone.STRETCH -> "Stretch (>40%)"
                        },
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        color = zoneColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Guidance disclaimer
            Text(
                text = "Rule-of-thumb guidance, not financial advice.",
                fontFamily = InterFamily,
                fontSize = 11.sp,
                color = TextMuted
            )
        }
    }
}
