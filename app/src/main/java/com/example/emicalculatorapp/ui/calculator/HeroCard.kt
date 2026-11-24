package com.example.emicalculatorapp.ui.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emicalculatorapp.data.settings.NumberFormatStyle
import com.example.emicalculatorapp.ui.components.fx.OdometerText
import com.example.emicalculatorapp.ui.components.fx.SweepBorderCard
import com.example.emicalculatorapp.ui.components.fx.TiltCard
import com.example.emicalculatorapp.ui.theme.DonutInterestGradient
import com.example.emicalculatorapp.ui.theme.DonutPrincipalGradient
import com.example.emicalculatorapp.ui.theme.EMIverseTypography
import com.example.emicalculatorapp.ui.theme.ElectricViolet
import com.example.emicalculatorapp.ui.theme.HotPink
import com.example.emicalculatorapp.ui.theme.LuminousCyan
import com.example.emicalculatorapp.ui.theme.PrimaryLight
import com.example.emicalculatorapp.ui.theme.SpaceGroteskFamily
import com.example.emicalculatorapp.ui.theme.SurfaceContainer
import com.example.emicalculatorapp.ui.theme.SurfaceContainerHigh
import com.example.emicalculatorapp.ui.theme.SurfaceContainerLow
import com.example.emicalculatorapp.ui.theme.TextPrimary
import com.example.emicalculatorapp.ui.theme.TextSecondary
import com.example.emicalculatorapp.viewmodel.EMIViewModel
import java.util.Locale

@Composable
fun HeroDonutCard(
    animatedEmi: Float,
    animatedPrincipalPct: Float,
    isYearMode: Boolean,
    tenureValue: Int,
    isAnySliderDragging: Boolean,
    numberFormat: NumberFormatStyle,
    onSeeMathClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    TiltCard(
        enabled = !isAnySliderDragging,
        modifier = modifier.fillMaxWidth()
    ) {
        SweepBorderCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = SurfaceContainerLow.copy(alpha = 0.85f),
            gradientColors = listOf(LuminousCyan, ElectricViolet, HotPink, LuminousCyan)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .semantics {
                        val pPct = animatedPrincipalPct.coerceIn(0f, 100f).toInt()
                        contentDescription = "Loan breakdown donut chart: Principal $pPct percent, Interest ${100 - pPct} percent"
                    },
                contentAlignment = Alignment.Center
            ) {
                Spacer(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawWithCache {
                            val strokeW = 14.dp.toPx()
                            val pad = strokeW / 2f + 4.dp.toPx()
                            val arcSize = Size(size.width - pad * 2, size.height - pad * 2)
                            val topLeft = Offset(pad, pad)
                            val trackStroke = Stroke(width = strokeW)
                            val arcStroke = Stroke(width = strokeW, cap = StrokeCap.Round)

                            onDrawBehind {
                                // Background Track
                                drawArc(
                                    color = SurfaceContainer,
                                    startAngle = 0f,
                                    sweepAngle = 360f,
                                    useCenter = false,
                                    topLeft = topLeft,
                                    size = arcSize,
                                    style = trackStroke
                                )

                                // Principal Arc (Violet -> Cyan) - read lazily in draw phase
                                val pFraction = (animatedPrincipalPct / 100f).coerceIn(0.01f, 0.99f)
                                val pSweep = 360f * pFraction
                                drawArc(
                                    brush = DonutPrincipalGradient,
                                    startAngle = -90f,
                                    sweepAngle = pSweep,
                                    useCenter = false,
                                    topLeft = topLeft,
                                    size = arcSize,
                                    style = arcStroke
                                )

                                // Interest Arc (Hot Pink)
                                val iSweep = 360f * (1f - pFraction)
                                drawArc(
                                    brush = DonutInterestGradient,
                                    startAngle = -90f + pSweep + 3f,
                                    sweepAngle = (iSweep - 3f).coerceAtLeast(0f),
                                    useCenter = false,
                                    topLeft = topLeft,
                                    size = arcSize,
                                    style = arcStroke
                                )
                            }
                        }
                )

                // Center Counter Cluster
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "MONTHLY REPAYMENT",
                        style = EMIverseTypography.labelCaps,
                        color = LuminousCyan,
                        letterSpacing = 0.12.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "₹",
                            style = EMIverseTypography.headlineSm,
                            color = PrimaryLight,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 4.dp, end = 2.dp)
                        )
                        val formattedEmi = EMIViewModel.formatCurrency(animatedEmi.toDouble(), numberFormat)
                            .replace("₹ ", "").replace("₹", "")

                        OdometerText(
                            text = formattedEmi,
                            isContinuous = isAnySliderDragging,
                            textStyle = EMIverseTypography.displayHero.copy(fontSize = 32.sp),
                            color = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(SurfaceContainerHigh.copy(alpha = 0.8f))
                            .padding(horizontal = 10.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isYearMode) "$tenureValue Yrs Tenure" else "$tenureValue Months Tenure",
                            style = EMIverseTypography.labelCaps,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Legend Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(SurfaceContainerHigh.copy(alpha = 0.6f))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(PrimaryLight)
                    )
                    Text(
                        text = "Principal: ${String.format(Locale.US, "%.1f%%", animatedPrincipalPct)}",
                        style = EMIverseTypography.labelCaps,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(SurfaceContainerHigh.copy(alpha = 0.6f))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(LuminousCyan)
                    )
                    Text(
                        text = "Interest: ${String.format(Locale.US, "%.1f%%", (100f - animatedPrincipalPct).coerceAtLeast(0f))}",
                        style = EMIverseTypography.labelCaps,
                        color = LuminousCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // "See the math" educational CTA
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(LuminousCyan.copy(alpha = 0.12f))
                    .border(1.dp, LuminousCyan.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                    .clickable(onClick = onSeeMathClick)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Functions,
                    contentDescription = "See the math",
                    tint = LuminousCyan,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = "See the math",
                    fontFamily = SpaceGroteskFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    color = LuminousCyan
                )
            }
        }
    }
}
}
