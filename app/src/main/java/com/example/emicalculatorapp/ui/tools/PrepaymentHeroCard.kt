package com.example.emicalculatorapp.ui.tools

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emicalculatorapp.data.settings.NumberFormatStyle
import com.example.emicalculatorapp.model.PrepaymentSimulationResult
import com.example.emicalculatorapp.ui.components.fx.OdometerText
import com.example.emicalculatorapp.ui.components.fx.SweepBorderCard
import com.example.emicalculatorapp.ui.theme.ElectricViolet
import com.example.emicalculatorapp.ui.theme.HotPink
import com.example.emicalculatorapp.ui.theme.InterFamily
import com.example.emicalculatorapp.ui.theme.LuminousCyan
import com.example.emicalculatorapp.ui.theme.MintGreen
import com.example.emicalculatorapp.ui.theme.SpaceGroteskFamily
import com.example.emicalculatorapp.ui.theme.SurfaceContainerLow
import com.example.emicalculatorapp.ui.theme.TextMuted
import com.example.emicalculatorapp.ui.theme.TextPrimary
import com.example.emicalculatorapp.ui.theme.TextSecondary
import com.example.emicalculatorapp.viewmodel.EMIViewModel

@Composable
fun PrepaymentHeroCard(
    result: PrepaymentSimulationResult,
    numberFormat: NumberFormatStyle,
    isContinuousDrag: Boolean,
    modifier: Modifier = Modifier
) {
    SweepBorderCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = SurfaceContainerLow.copy(alpha = 0.85f),
        gradientColors = listOf(MintGreen, LuminousCyan, ElectricViolet, MintGreen)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header tag
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MintGreen.copy(alpha = 0.15f))
                    .border(1.dp, MintGreen.copy(alpha = 0.35f), CircleShape)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MintGreen,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "DEBT-FREE ACCELERATOR",
                    fontFamily = SpaceGroteskFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MintGreen,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Debt-Free Timeline Announcement
            Text(
                text = if (result.monthsSaved > 0) {
                    "Debt-free in ${result.newTenureYears} yrs ${result.newTenureMonths} mos"
                } else {
                    "Standard Payoff Timeline"
                },
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Months Saved Highlight Counter
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                OdometerText(
                    text = result.monthsSaved.toString(),
                    isContinuous = isContinuousDrag,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontFamily = SpaceGroteskFamily,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MintGreen
                )
                Text(
                    text = " months earlier",
                    fontFamily = SpaceGroteskFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = LuminousCyan,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }

            if (result.monthsSaved > 0) {
                Text(
                    text = "(${result.yearsSaved} Years ${result.remainingMonthsSaved} Months shaved off)",
                    fontFamily = InterFamily,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Glowing Green Interest Saved Badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MintGreen.copy(alpha = 0.12f))
                    .border(1.dp, MintGreen.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MintGreen.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Savings,
                                contentDescription = null,
                                tint = MintGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "TOTAL INTEREST SAVED",
                                fontFamily = SpaceGroteskFamily,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MintGreen
                            )
                            Text(
                                text = "Direct interest expense eliminated",
                                fontFamily = InterFamily,
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }

                    Text(
                        text = EMIViewModel.formatCurrency(result.interestSaved, numberFormat),
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MintGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Payment Comparison Row: Original vs New Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.04f))
                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "ORIGINAL TOTAL",
                            fontFamily = SpaceGroteskFamily,
                            fontSize = 9.sp,
                            color = TextMuted,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = EMIViewModel.formatCompactCurrency(result.originalTotalPayment, numberFormat),
                            fontFamily = SpaceGroteskFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = HotPink
                        )
                        Text(
                            text = "${result.originalMonths} months",
                            fontFamily = InterFamily,
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.04f))
                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "ACCELERATED TOTAL",
                            fontFamily = SpaceGroteskFamily,
                            fontSize = 9.sp,
                            color = TextMuted,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = EMIViewModel.formatCompactCurrency(result.newTotalPayment, numberFormat),
                            fontFamily = SpaceGroteskFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = LuminousCyan
                        )
                        Text(
                            text = "${result.newMonths} months",
                            fontFamily = InterFamily,
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}
