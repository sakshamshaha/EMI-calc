package com.example.emicalculatorapp.ui.affordability

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emicalculatorapp.data.settings.NumberFormatStyle
import com.example.emicalculatorapp.model.AffordabilityResult
import com.example.emicalculatorapp.model.AffordabilityZone
import com.example.emicalculatorapp.ui.components.fx.OdometerText
import com.example.emicalculatorapp.ui.components.fx.SweepBorderCard
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
import com.example.emicalculatorapp.viewmodel.EMIViewModel

@Composable
fun AffordabilityHeroCard(
    result: AffordabilityResult,
    numberFormat: NumberFormatStyle,
    isContinuousDrag: Boolean = false,
    modifier: Modifier = Modifier
) {
    val zoneColor = when (result.zone) {
        AffordabilityZone.COMFORTABLE -> MintGreen
        AffordabilityZone.MODERATE -> LuminousCyan
        AffordabilityZone.STRETCH -> HotPink
    }

    val gradientColors = when (result.zone) {
        AffordabilityZone.COMFORTABLE -> listOf(MintGreen, LuminousCyan, ElectricViolet, MintGreen)
        AffordabilityZone.MODERATE -> listOf(LuminousCyan, ElectricViolet, HotPink, LuminousCyan)
        AffordabilityZone.STRETCH -> listOf(HotPink, ElectricViolet, LuminousCyan, HotPink)
    }

    SweepBorderCard(
        modifier = modifier.fillMaxWidth(),
        gradientColors = gradientColors
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header: Status tag & DTI Ratio
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(zoneColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (result.isIncomeZero) "BUDGET ESTIMATE" else "MAX BORROWING CAPACITY",
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp,
                        color = zoneColor
                    )
                }

                if (!result.isIncomeZero) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(zoneColor.copy(alpha = 0.15f))
                            .border(1.dp, zoneColor.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 9.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = when (result.zone) {
                                AffordabilityZone.COMFORTABLE -> "Comfortable"
                                AffordabilityZone.MODERATE -> "Moderate"
                                AffordabilityZone.STRETCH -> "Stretch"
                            },
                            fontFamily = SpaceGroteskFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = zoneColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (result.isIncomeZero) {
                Text(
                    text = "Enter your monthly take-home income below to calculate your personalized loan capacity.",
                    fontFamily = SpaceGroteskFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    lineHeight = 22.sp
                )
            } else if (result.isExistingExceedingCap) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = HotPink,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Existing EMIs exceed ${result.maxDtiRatioPercent.toInt()}% cap",
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = HotPink
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "You currently have ₹${EMIViewModel.formatCurrency(result.existingEmis, numberFormat)} in monthly commitments. No new borrowing capacity is available under this budget cap.",
                    fontFamily = InterFamily,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            } else {
                Text(
                    text = "You can comfortably borrow up to",
                    fontFamily = InterFamily,
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Hero Max Loan Amount with OdometerText
                OdometerText(
                    text = EMIViewModel.formatCurrency(result.maxLoanAmount, numberFormat),
                    isContinuous = isContinuousDrag,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontFamily = SpaceGroteskFamily,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Sub Metrics: Max Affordable EMI
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Max Affordable EMI", fontFamily = InterFamily, fontSize = 11.sp, color = TextMuted)
                        Text(
                            text = EMIViewModel.formatCurrency(result.maxAffordableEmi, numberFormat) + "/mo",
                            fontFamily = SpaceGroteskFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = LuminousCyan
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Target Budget Cap", fontFamily = InterFamily, fontSize = 11.sp, color = TextMuted)
                        Text(
                            text = "${result.maxDtiRatioPercent.toInt()}% of Income",
                            fontFamily = SpaceGroteskFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Guidance & Detail Sentence
            if (!result.isIncomeZero) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceContainerHigh.copy(alpha = 0.7f))
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = if (result.additionalIncomeNeeded > 1.0) {
                            "This loan needs about ${EMIViewModel.formatCurrency(result.additionalIncomeNeeded, numberFormat)} more monthly income, or a loan about ${EMIViewModel.formatCurrency(result.loanReductionNeeded, numberFormat)} smaller."
                        } else {
                            result.summaryDetail
                        },
                        fontFamily = InterFamily,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}
