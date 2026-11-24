package com.example.emicalculatorapp.ui.affordability

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emicalculatorapp.data.settings.NumberFormatStyle
import com.example.emicalculatorapp.model.AffordabilityParams
import com.example.emicalculatorapp.ui.components.GlassCard
import com.example.emicalculatorapp.ui.components.NeonChip
import com.example.emicalculatorapp.ui.components.QuantumSlider
import com.example.emicalculatorapp.ui.theme.ElectricViolet
import com.example.emicalculatorapp.ui.theme.HotPink
import com.example.emicalculatorapp.ui.theme.InterFamily
import com.example.emicalculatorapp.ui.theme.LuminousCyan
import com.example.emicalculatorapp.ui.theme.MintGreen
import com.example.emicalculatorapp.ui.theme.OutlineVariant
import com.example.emicalculatorapp.ui.theme.PrimaryLight
import com.example.emicalculatorapp.ui.theme.SpaceGroteskFamily
import com.example.emicalculatorapp.ui.theme.SurfaceContainerHigh
import com.example.emicalculatorapp.ui.theme.SurfaceContainerLowest
import com.example.emicalculatorapp.ui.theme.TextMuted
import com.example.emicalculatorapp.ui.theme.TextPrimary
import com.example.emicalculatorapp.ui.theme.TextSecondary
import com.example.emicalculatorapp.viewmodel.EMIViewModel
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AffordabilityInputCards(
    params: AffordabilityParams,
    numberFormat: NumberFormatStyle,
    onIncomeChanged: (Double) -> Unit,
    onExistingEmisChanged: (Double) -> Unit,
    onMaxRatioChanged: (Double) -> Unit,
    onRateChanged: (Double) -> Unit,
    onTenureChanged: (Int) -> Unit,
    onDraggingChanged: (Boolean) -> Unit,
    focusManager: FocusManager,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Monthly Take-Home Income Card
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "MONTHLY TAKE-HOME INCOME", fontFamily = SpaceGroteskFamily, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MintGreen, letterSpacing = 0.8.sp)
                        Text(text = "Net in-hand salary or earnings", fontFamily = InterFamily, fontSize = 11.sp, color = TextMuted)
                    }

                    IncomeInputPill(
                        amount = params.monthlyIncome,
                        onAmountChanged = onIncomeChanged,
                        numberFormat = numberFormat,
                        focusManager = focusManager
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                QuantumSlider(
                    value = (params.monthlyIncome / 1000.0).toFloat().coerceIn(10f, 500f),
                    onValueChange = { onIncomeChanged(it.toDouble() * 1000.0) },
                    valueRange = 10f..500f,
                    steps = 48,
                    ringColor = MintGreen,
                    onDraggingChanged = onDraggingChanged
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(50000.0 to "50K", 100000.0 to "1L", 150000.0 to "1.5L", 250000.0 to "2.5L", 500000.0 to "5L").forEach { (inc, lbl) ->
                        NeonChip(
                            text = lbl,
                            isSelected = kotlin.math.abs(params.monthlyIncome - inc) < 100.0,
                            onClick = { onIncomeChanged(inc) }
                        )
                    }
                }
            }
        }

        // 2. Existing Monthly Commitments Card
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "EXISTING MONTHLY OBLIGATIONS", fontFamily = SpaceGroteskFamily, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = LuminousCyan, letterSpacing = 0.8.sp)
                        Text(text = "Other active EMIs & fixed loans", fontFamily = InterFamily, fontSize = 11.sp, color = TextMuted)
                    }

                    Text(
                        text = EMIViewModel.formatCurrency(params.existingEmis, numberFormat),
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                QuantumSlider(
                    value = (params.existingEmis / 1000.0).toFloat().coerceIn(0f, 200f),
                    onValueChange = { onExistingEmisChanged(it.toDouble() * 1000.0) },
                    valueRange = 0f..200f,
                    steps = 39,
                    ringColor = LuminousCyan,
                    onDraggingChanged = onDraggingChanged
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(0.0 to "None (₹0)", 10000.0 to "10K", 25000.0 to "25K", 50000.0 to "50K", 100000.0 to "1L").forEach { (ex, lbl) ->
                        NeonChip(
                            text = lbl,
                            isSelected = kotlin.math.abs(params.existingEmis - ex) < 100.0,
                            onClick = { onExistingEmisChanged(ex) }
                        )
                    }
                }
            }
        }

        // 3. Max Total EMI-to-Income Budget Ratio Slider Card
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "MAX EMI-TO-INCOME RATIO", fontFamily = SpaceGroteskFamily, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = ElectricViolet, letterSpacing = 0.8.sp)
                        Text(text = "Target budget cap (Default: 40%)", fontFamily = InterFamily, fontSize = 11.sp, color = TextMuted)
                    }

                    Text(
                        text = "${params.maxDtiRatioPercent.toInt()}%",
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = ElectricViolet
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                QuantumSlider(
                    value = params.maxDtiRatioPercent.toFloat(),
                    onValueChange = { onMaxRatioChanged(it.toDouble()) },
                    valueRange = 20f..60f,
                    steps = 39,
                    ringColor = ElectricViolet,
                    onDraggingChanged = onDraggingChanged
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(25.0 to "25% Conservative", 30.0 to "30% Comfortable", 40.0 to "40% Recommended", 50.0 to "50% Aggressive").forEach { (rt, lbl) ->
                        NeonChip(
                            text = lbl,
                            isSelected = kotlin.math.abs(params.maxDtiRatioPercent - rt) < 0.5,
                            onClick = { onMaxRatioChanged(rt) }
                        )
                    }
                }
            }
        }

        // 4. Loan Terms (Rate & Tenure)
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(text = "LOAN TERMS FOR AFFORDABILITY", fontFamily = SpaceGroteskFamily, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextSecondary, letterSpacing = 0.8.sp)

                Spacer(modifier = Modifier.height(12.dp))

                // Rate
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Interest Rate", fontFamily = InterFamily, fontSize = 12.sp, color = TextMuted)
                    Text(text = String.format(Locale.US, "%.2f%%", params.interestRate), fontFamily = SpaceGroteskFamily, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
                }
                QuantumSlider(
                    value = params.interestRate.toFloat(),
                    onValueChange = { onRateChanged(it.toDouble()) },
                    valueRange = 5f..20f,
                    steps = 149,
                    ringColor = PrimaryLight,
                    onDraggingChanged = onDraggingChanged
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Tenure
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Tenure", fontFamily = InterFamily, fontSize = 12.sp, color = TextMuted)
                    Text(text = "${params.tenureYears} Years", fontFamily = SpaceGroteskFamily, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
                }
                QuantumSlider(
                    value = params.tenureYears.toFloat(),
                    onValueChange = { onTenureChanged(it.toInt()) },
                    valueRange = 1f..30f,
                    steps = 28,
                    ringColor = PrimaryLight,
                    onDraggingChanged = onDraggingChanged
                )
            }
        }
    }
}

@Composable
private fun IncomeInputPill(
    amount: Double,
    onAmountChanged: (Double) -> Unit,
    numberFormat: NumberFormatStyle,
    focusManager: FocusManager
) {
    var rawText by remember(amount, numberFormat) {
        val formatted = EMIViewModel.formatCurrency(amount, numberFormat).replace("₹", "").trim()
        mutableStateOf(TextFieldValue(formatted, TextRange(formatted.length)))
    }
    var isFocused by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .width(135.dp)
            .height(38.dp)
            .clip(CircleShape)
            .background(SurfaceContainerLowest)
            .border(1.dp, if (isFocused) MintGreen else OutlineVariant, CircleShape)
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "₹", fontFamily = SpaceGroteskFamily, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MintGreen)
            BasicTextField(
                value = rawText,
                onValueChange = { input ->
                    rawText = EMIViewModel.formatAmountWithStableCursor(
                        input = input,
                        formatStyle = numberFormat,
                        onAmountParsed = { onAmountChanged(it) }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { isFocused = it.isFocused },
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = SpaceGroteskFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.End
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                singleLine = true,
                cursorBrush = SolidColor(MintGreen)
            )
        }
    }
}
