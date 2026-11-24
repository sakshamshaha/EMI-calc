package com.example.emicalculatorapp.ui.compare

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emicalculatorapp.data.settings.NumberFormatStyle
import com.example.emicalculatorapp.model.LoanInputState
import com.example.emicalculatorapp.model.LoanSummary
import com.example.emicalculatorapp.ui.components.GlassCard
import com.example.emicalculatorapp.ui.components.NeonChip
import com.example.emicalculatorapp.ui.components.QuantumSlider
import com.example.emicalculatorapp.ui.components.fx.GlowPillSwitch
import com.example.emicalculatorapp.ui.components.fx.ShineButton
import com.example.emicalculatorapp.ui.theme.ElectricViolet
import com.example.emicalculatorapp.ui.theme.HotPink
import com.example.emicalculatorapp.ui.theme.InterFamily
import com.example.emicalculatorapp.ui.theme.LuminousCyan
import com.example.emicalculatorapp.ui.theme.MintGreen
import com.example.emicalculatorapp.ui.theme.SpaceGroteskFamily
import com.example.emicalculatorapp.ui.theme.SurfaceBright
import com.example.emicalculatorapp.ui.theme.SurfaceContainerHigh
import com.example.emicalculatorapp.ui.theme.TextMuted
import com.example.emicalculatorapp.ui.theme.TextPrimary
import com.example.emicalculatorapp.ui.theme.TextSecondary
import com.example.emicalculatorapp.viewmodel.EMIViewModel
import java.util.Locale

@Composable
fun CompareLoanACard(
    summaryA: LoanSummary,
    numberFormat: NumberFormatStyle,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LOAN A (ACTIVE BASELINE)",
                    fontFamily = SpaceGroteskFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = LuminousCyan,
                    letterSpacing = 0.8.sp
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(LuminousCyan.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "From Calculator",
                        fontFamily = SpaceGroteskFamily,
                        fontSize = 10.sp,
                        color = LuminousCyan,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Amount", fontFamily = InterFamily, fontSize = 11.sp, color = TextMuted)
                    Text(
                        text = EMIViewModel.formatCurrency(summaryA.amount, numberFormat),
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                }
                Column {
                    Text(text = "Rate / Tenure", fontFamily = InterFamily, fontSize = 11.sp, color = TextMuted)
                    Text(
                        text = "${String.format(Locale.US, "%.2f%%", summaryA.rate)} • ${if (summaryA.isYearMode) "${summaryA.tenureValue}Y" else "${summaryA.tenureValue}M"}",
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                }
                Column {
                    Text(text = "Monthly EMI", fontFamily = InterFamily, fontSize = 11.sp, color = TextMuted)
                    Text(
                        text = EMIViewModel.formatCurrency(summaryA.monthlyEMI, numberFormat),
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = LuminousCyan
                    )
                }
            }
        }
    }
}

@Composable
fun CompareActionsRow(
    onCopyAToB: () -> Unit,
    onSwap: () -> Unit,
    modifier: Modifier = Modifier
) {
    var swapRotation by remember { mutableStateOf(0f) }
    val animatedRotation by animateFloatAsState(
        targetValue = swapRotation,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "SwapRotation"
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ShineButton(
            text = "Copy A to B",
            onClick = onCopyAToB,
            modifier = Modifier.weight(1f).height(44.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
            gradientBrush = Brush.horizontalGradient(listOf(SurfaceContainerHigh, SurfaceBright))
        )

        ShineButton(
            text = "Swap A ⇄ B",
            onClick = {
                swapRotation += 180f
                onSwap()
            },
            modifier = Modifier.weight(1f).height(44.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
            gradientBrush = Brush.horizontalGradient(listOf(ElectricViolet.copy(alpha = 0.7f), HotPink.copy(alpha = 0.7f)))
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CompareLoanBCard(
    stateB: LoanInputState,
    summaryB: LoanSummary,
    numberFormat: NumberFormatStyle,
    onAmountChanged: (Double) -> Unit,
    onRateChanged: (Double) -> Unit,
    onTenureChanged: (Int) -> Unit,
    onYearModeChanged: (Boolean) -> Unit,
    onProcessingFeeChanged: (Double) -> Unit,
    onDraggingChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LOAN B (COMPACT INPUTS)",
                    fontFamily = SpaceGroteskFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MintGreen,
                    letterSpacing = 0.8.sp
                )

                Text(
                    text = "EMI: ${EMIViewModel.formatCurrency(summaryB.monthlyEMI, numberFormat)}",
                    fontFamily = SpaceGroteskFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MintGreen
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 1. Loan B Amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Loan Amount", fontFamily = InterFamily, fontSize = 12.sp, color = TextSecondary)
                Text(
                    text = EMIViewModel.formatCurrency(stateB.amount, numberFormat),
                    fontFamily = SpaceGroteskFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
            }
            QuantumSlider(
                value = (stateB.amount / 100000.0).toFloat(),
                onValueChange = { onAmountChanged(it.toDouble() * 100000.0) },
                valueRange = 1f..100f,
                steps = 98,
                ringColor = MintGreen,
                onDraggingChanged = onDraggingChanged
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(1000000.0 to "10L", 2500000.0 to "25L", 4500000.0 to "45L", 7500000.0 to "75L").forEach { (amt, lbl) ->
                    NeonChip(
                        text = lbl,
                        isSelected = kotlin.math.abs(stateB.amount - amt) < 1000.0,
                        onClick = { onAmountChanged(amt) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Loan B Interest Rate
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Interest Rate", fontFamily = InterFamily, fontSize = 12.sp, color = TextSecondary)
                Text(
                    text = String.format(Locale.US, "%.2f%%", stateB.rate),
                    fontFamily = SpaceGroteskFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
            }
            QuantumSlider(
                value = stateB.rate.toFloat(),
                onValueChange = { onRateChanged(it.toDouble()) },
                valueRange = 5f..20f,
                steps = 149,
                ringColor = MintGreen,
                onDraggingChanged = onDraggingChanged
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(7.5 to "7.5%", 8.0 to "8.0%", 8.5 to "8.5%", 9.2 to "9.2%", 10.5 to "10.5%").forEach { (rt, lbl) ->
                    NeonChip(
                        text = lbl,
                        isSelected = kotlin.math.abs(stateB.rate - rt) < 0.05,
                        onClick = { onRateChanged(rt) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Loan B Tenure
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Tenure", fontFamily = InterFamily, fontSize = 12.sp, color = TextSecondary)
                Text(
                    text = if (stateB.isYearMode) "${stateB.tenureValue} Years" else "${stateB.tenureValue} Months",
                    fontFamily = SpaceGroteskFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
            }
            QuantumSlider(
                value = stateB.tenureValue.toFloat(),
                onValueChange = { onTenureChanged(it.toInt()) },
                valueRange = if (stateB.isYearMode) 1f..30f else 12f..360f,
                steps = if (stateB.isYearMode) 28 else 347,
                ringColor = MintGreen,
                onDraggingChanged = onDraggingChanged
            )
            GlowPillSwitch(
                options = listOf(true, false),
                selectedOption = stateB.isYearMode,
                onOptionSelected = onYearModeChanged,
                labelProvider = { if (it) "Years" else "Months" },
                modifier = Modifier.fillMaxWidth().height(32.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Processing Fee %
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Processing Fee %", fontFamily = InterFamily, fontSize = 12.sp, color = TextSecondary)
                    Text(
                        text = "Fee: ${EMIViewModel.formatCurrency(summaryB.processingFeeAmount, numberFormat)}",
                        fontFamily = InterFamily,
                        fontSize = 11.sp,
                        color = HotPink
                    )
                }
                Text(
                    text = String.format(Locale.US, "%.2f%%", stateB.processingFeePercent),
                    fontFamily = SpaceGroteskFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
            }
            QuantumSlider(
                value = stateB.processingFeePercent.toFloat(),
                onValueChange = { onProcessingFeeChanged(it.toDouble()) },
                valueRange = 0f..5f,
                steps = 49,
                ringColor = HotPink,
                onDraggingChanged = onDraggingChanged
            )
        }
    }
}
