package com.example.emicalculatorapp.ui.compare

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
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
import com.example.emicalculatorapp.model.ComparisonWinner
import com.example.emicalculatorapp.model.LoanComparisonResult
import com.example.emicalculatorapp.ui.components.GlassCard
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CompareHeroCard(
    result: LoanComparisonResult,
    numberFormat: NumberFormatStyle,
    isContinuousDrag: Boolean = false,
    modifier: Modifier = Modifier
) {
    val winnerName = when (result.overallWinner) {
        ComparisonWinner.LOAN_A -> "Loan A"
        ComparisonWinner.LOAN_B -> "Loan B"
        ComparisonWinner.TIE -> "Tie"
    }

    val winnerColor = when (result.overallWinner) {
        ComparisonWinner.LOAN_A -> LuminousCyan
        ComparisonWinner.LOAN_B -> MintGreen
        ComparisonWinner.TIE -> TextSecondary
    }

    val hasWinner = result.overallWinner != ComparisonWinner.TIE

    val content: @Composable () -> Unit = {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header Row: Status & Badges
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
                            .background(winnerColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (hasWinner) "$winnerName WINS OVERALL" else "BALANCED COMPARISON",
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp,
                        color = winnerColor
                    )
                }

                if (hasWinner) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(winnerColor.copy(alpha = 0.15f))
                            .border(1.dp, winnerColor.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = winnerColor,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Best Value",
                                fontFamily = SpaceGroteskFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = winnerColor
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Hero Savings Line
            if (hasWinner) {
                Text(
                    text = "$winnerName saves you overall",
                    fontFamily = InterFamily,
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    OdometerText(
                        text = EMIViewModel.formatCurrency(result.overallSavings, numberFormat),
                        isContinuous = isContinuousDrag,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontFamily = SpaceGroteskFamily,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = TextPrimary
                    )
                }
            } else {
                Text(
                    text = "Both loan options cost the exact same",
                    fontFamily = SpaceGroteskFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Total payout including processing fees is identical.",
                    fontFamily = InterFamily,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sub-metrics badges row
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Monthly EMI Winner Badge
                when (result.lowerEmiWinner) {
                    ComparisonWinner.LOAN_A -> {
                        EmiWinnerPill(
                            loanName = "Loan A",
                            savings = result.monthlySavings,
                            numberFormat = numberFormat,
                            color = LuminousCyan
                        )
                    }
                    ComparisonWinner.LOAN_B -> {
                        EmiWinnerPill(
                            loanName = "Loan B",
                            savings = result.monthlySavings,
                            numberFormat = numberFormat,
                            color = MintGreen
                        )
                    }
                    ComparisonWinner.TIE -> {
                        // Equal EMI
                    }
                }

                // Interest Delta Badge
                if (kotlin.math.abs(result.interestDiff) > 1.0) {
                    val interestWinnerName = if (result.interestDiff > 0) "Loan B" else "Loan A"
                    val interestSavings = kotlin.math.abs(result.interestDiff)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(ElectricViolet.copy(alpha = 0.12f))
                            .border(1.dp, ElectricViolet.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "$interestWinnerName saves ${EMIViewModel.formatCurrency(interestSavings, numberFormat)} interest",
                            fontFamily = SpaceGroteskFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp,
                            color = ElectricViolet
                        )
                    }
                }
            }
        }
    }

    if (hasWinner) {
        SweepBorderCard(
            modifier = modifier.fillMaxWidth(),
            gradientColors = listOf(winnerColor, ElectricViolet, HotPink, winnerColor)
        ) {
            content()
        }
    } else {
        GlassCard(modifier = modifier.fillMaxWidth()) {
            content()
        }
    }
}

@Composable
private fun EmiWinnerPill(
    loanName: String,
    savings: Double,
    numberFormat: NumberFormatStyle,
    color: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "$loanName: Lower EMI (-${EMIViewModel.formatCurrency(savings, numberFormat)}/mo)",
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                color = color
            )
        }
    }
}
