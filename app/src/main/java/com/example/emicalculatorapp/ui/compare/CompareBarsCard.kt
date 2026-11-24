package com.example.emicalculatorapp.ui.compare

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emicalculatorapp.data.settings.NumberFormatStyle
import com.example.emicalculatorapp.model.LoanComparisonResult
import com.example.emicalculatorapp.model.LoanSummary
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
import com.example.emicalculatorapp.viewmodel.EMIViewModel

@Composable
fun CompareBarsCard(
    result: LoanComparisonResult,
    numberFormat: NumberFormatStyle,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Text(
                text = "METRIC COMPARISONS",
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 1.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 1. Monthly EMI Comparison
            MetricComparisonRow(
                title = "Monthly EMI",
                valA = result.loanA.monthlyEMI,
                valB = result.loanB.monthlyEMI,
                numberFormat = numberFormat
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Total Interest Comparison
            MetricComparisonRow(
                title = "Total Interest",
                valA = result.loanA.totalInterest,
                valB = result.loanB.totalInterest,
                numberFormat = numberFormat
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Total Cost (Payment + Processing Fee)
            MetricComparisonRow(
                title = "Total Cost (With Fees)",
                valA = result.loanA.totalCost,
                valB = result.loanB.totalCost,
                numberFormat = numberFormat
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Stacked Composition Bars for Both Loans
            Text(
                text = "COST COMPOSITION BREAKDOWN",
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 0.8.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(10.dp))

            StackedLoanCostBar(title = "Loan A", summary = result.loanA)
            Spacer(modifier = Modifier.height(10.dp))
            StackedLoanCostBar(title = "Loan B", summary = result.loanB)

            Spacer(modifier = Modifier.height(12.dp))

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LegendDot(color = LuminousCyan, label = "Principal")
                LegendDot(color = ElectricViolet, label = "Interest")
                LegendDot(color = HotPink, label = "Proc. Fee")
            }
        }
    }
}

@Composable
private fun MetricComparisonRow(
    title: String,
    valA: Double,
    valB: Double,
    numberFormat: NumberFormatStyle
) {
    val maxVal = maxOf(valA, valB).coerceAtLeast(1.0)
    val ratioA = ((valA / maxVal).toFloat()).coerceIn(0.04f, 1f)
    val ratioB = ((valB / maxVal).toFloat()).coerceIn(0.04f, 1f)

    val animatedRatioA by animateFloatAsState(
        targetValue = ratioA,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "RatioA_$title"
    )
    val animatedRatioB by animateFloatAsState(
        targetValue = ratioB,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "RatioB_$title"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontFamily = SpaceGroteskFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Loan A Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "A",
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = LuminousCyan,
                modifier = Modifier.width(18.dp)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(SurfaceContainerHigh)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedRatioA)
                        .clip(RoundedCornerShape(4.dp))
                        .background(LuminousCyan)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = EMIViewModel.formatCurrency(valA, numberFormat),
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = TextPrimary,
                modifier = Modifier.width(88.dp)
            )
        }

        Spacer(modifier = Modifier.height(5.dp))

        // Loan B Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "B",
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = MintGreen,
                modifier = Modifier.width(18.dp)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(SurfaceContainerHigh)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedRatioB)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MintGreen)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = EMIViewModel.formatCurrency(valB, numberFormat),
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = TextPrimary,
                modifier = Modifier.width(88.dp)
            )
        }
    }
}

@Composable
private fun StackedLoanCostBar(
    title: String,
    summary: LoanSummary
) {
    val principalShare = summary.principalSharePercent / 100f
    val interestShare = summary.interestSharePercent / 100f
    val feeShare = summary.feeSharePercent / 100f

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontFamily = SpaceGroteskFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp,
            color = TextSecondary,
            modifier = Modifier.width(50.dp)
        )

        Row(
            modifier = Modifier
                .weight(1f)
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(SurfaceContainerHigh)
        ) {
            if (principalShare > 0.001f) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(principalShare)
                        .background(LuminousCyan)
                )
            }
            if (interestShare > 0.001f) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(interestShare)
                        .background(ElectricViolet)
                )
            }
            if (feeShare > 0.001f) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(feeShare)
                        .background(HotPink)
                )
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = label,
            fontFamily = InterFamily,
            fontSize = 11.sp,
            color = TextSecondary
        )
    }
}
