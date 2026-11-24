package com.example.emicalculatorapp.ui.calculator

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emicalculatorapp.data.settings.NumberFormatStyle
import com.example.emicalculatorapp.model.RateSensitivityPoint
import com.example.emicalculatorapp.ui.components.GlassCard
import com.example.emicalculatorapp.ui.theme.ElectricViolet
import com.example.emicalculatorapp.ui.theme.InterFamily
import com.example.emicalculatorapp.ui.theme.LocalReduceMotion
import com.example.emicalculatorapp.ui.theme.LuminousCyan
import com.example.emicalculatorapp.ui.theme.SpaceGroteskFamily
import com.example.emicalculatorapp.ui.theme.SurfaceContainerHigh
import com.example.emicalculatorapp.ui.theme.TextMuted
import com.example.emicalculatorapp.ui.theme.TextPrimary
import com.example.emicalculatorapp.ui.theme.TextSecondary
import com.example.emicalculatorapp.viewmodel.EMIViewModel
import java.util.Locale

@Composable
fun RateSensitivityStrip(
    points: List<RateSensitivityPoint>,
    numberFormat: NumberFormatStyle,
    modifier: Modifier = Modifier
) {
    if (points.isEmpty()) return

    val maxEmi = points.maxOfOrNull { it.monthlyEMI } ?: 1.0
    val minEmi = points.minOfOrNull { it.monthlyEMI } ?: 1.0
    val reduceMotion = LocalReduceMotion.current

    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RATE SENSITIVITY",
                    fontFamily = SpaceGroteskFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = LuminousCyan,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = "EMI impact across ±1-2% interest",
                    fontFamily = InterFamily,
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                points.forEach { pt ->
                    val ratio = if (maxEmi > 0) (pt.monthlyEMI / maxEmi).toFloat().coerceIn(0.2f, 1f) else 0.5f
                    val animatedHeightFraction by animateFloatAsState(
                        targetValue = if (reduceMotion) ratio else ratio,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "BarHeight_${pt.rate}"
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        // EMI Label
                        Text(
                            text = EMIViewModel.formatCurrency(pt.monthlyEMI, numberFormat),
                            fontFamily = SpaceGroteskFamily,
                            fontWeight = if (pt.isCurrent) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 10.sp,
                            color = if (pt.isCurrent) LuminousCyan else TextSecondary,
                            maxLines = 1
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Animated Bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.55f)
                                .height((60.dp * animatedHeightFraction).coerceAtLeast(12.dp))
                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                .background(
                                    if (pt.isCurrent) {
                                        Brush.verticalGradient(listOf(LuminousCyan, ElectricViolet))
                                    } else {
                                        Brush.verticalGradient(
                                            listOf(
                                                SurfaceContainerHigh.copy(alpha = 0.9f),
                                                SurfaceContainerHigh.copy(alpha = 0.4f)
                                            )
                                        )
                                    }
                                )
                                .then(
                                    if (pt.isCurrent) {
                                        Modifier.border(
                                            1.dp,
                                            LuminousCyan.copy(alpha = 0.7f),
                                            RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                                        )
                                    } else Modifier
                                )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Rate Label
                        Text(
                            text = String.format(Locale.US, "%.2f%%", pt.rate),
                            fontFamily = SpaceGroteskFamily,
                            fontWeight = if (pt.isCurrent) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp,
                            color = if (pt.isCurrent) TextPrimary else TextSecondary
                        )

                        if (pt.isCurrent) {
                            Text(
                                text = "Current",
                                fontFamily = InterFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                color = LuminousCyan
                            )
                        } else {
                            Text(
                                text = if (pt.rateOffset > 0) "+${pt.rateOffset.toInt()}%" else "${pt.rateOffset.toInt()}%",
                                fontFamily = InterFamily,
                                fontSize = 9.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}
