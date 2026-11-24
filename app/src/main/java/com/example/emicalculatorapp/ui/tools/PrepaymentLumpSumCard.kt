package com.example.emicalculatorapp.ui.tools

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emicalculatorapp.data.settings.NumberFormatStyle
import com.example.emicalculatorapp.ui.components.QuantumSlider
import com.example.emicalculatorapp.ui.components.fx.GlowPillSwitch
import com.example.emicalculatorapp.ui.theme.ElectricViolet
import com.example.emicalculatorapp.ui.theme.InterFamily
import com.example.emicalculatorapp.ui.theme.LuminousCyan
import com.example.emicalculatorapp.ui.theme.SpaceGroteskFamily
import com.example.emicalculatorapp.ui.theme.TextMuted
import com.example.emicalculatorapp.ui.theme.TextSecondary
import com.example.emicalculatorapp.viewmodel.EMIViewModel
import kotlin.math.roundToInt

@Composable
fun PrepaymentLumpSumCard(
    isLumpSumEnabled: Boolean,
    lumpSumAmount: Double,
    lumpSumMonth: Int,
    maxTenureMonths: Int,
    numberFormat: NumberFormatStyle,
    onLumpSumEnabledChanged: (Boolean) -> Unit,
    onLumpSumAmountChanged: (Double) -> Unit,
    onLumpSumMonthChanged: (Int) -> Unit,
    onDraggingChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    PrepaymentContainerCard(
        icon = Icons.Default.AddCard,
        iconTint = LuminousCyan,
        title = "One-Time Lump Sum Payment",
        modifier = modifier,
        trailingContent = {
            GlowPillSwitch(
                options = listOf(false, true),
                selectedOption = isLumpSumEnabled,
                onOptionSelected = onLumpSumEnabledChanged,
                labelProvider = { if (it) "ON" else "OFF" },
                modifier = Modifier.width(80.dp).height(32.dp)
            )
        }
    ) {
        AnimatedVisibility(
            visible = isLumpSumEnabled,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(modifier = Modifier.padding(top = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Lump Sum Amount",
                        fontFamily = InterFamily,
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                    Text(
                        text = EMIViewModel.formatCurrency(lumpSumAmount, numberFormat),
                        fontFamily = SpaceGroteskFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = LuminousCyan
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                QuantumSlider(
                    value = lumpSumAmount.toFloat(),
                    onValueChange = { onLumpSumAmountChanged(it.toDouble()) },
                    valueRange = 10000f..2000000f,
                    stepHapticThreshold = 25000f,
                    bubbleText = EMIViewModel.formatCompactCurrency(lumpSumAmount, numberFormat),
                    activeGradient = Brush.horizontalGradient(listOf(LuminousCyan, ElectricViolet)),
                    ringColor = LuminousCyan,
                    onDraggingChanged = onDraggingChanged
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Month Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Payment Month: Month $lumpSumMonth (Year ${(lumpSumMonth - 1) / 12 + 1})",
                        fontFamily = InterFamily,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                QuantumSlider(
                    value = lumpSumMonth.toFloat(),
                    onValueChange = { onLumpSumMonthChanged(it.roundToInt()) },
                    valueRange = 1f..maxTenureMonths.toFloat(),
                    stepHapticThreshold = 1f,
                    bubbleText = "Mo $lumpSumMonth",
                    ringColor = LuminousCyan,
                    onDraggingChanged = onDraggingChanged
                )
            }
        }
        if (!isLumpSumEnabled) {
            Text(
                text = "Apply bonuses, tax refunds, or savings to dramatically reduce principal.",
                fontFamily = InterFamily,
                fontSize = 12.sp,
                color = TextMuted
            )
        }
    }
}
