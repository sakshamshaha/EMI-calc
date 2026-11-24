package com.example.emicalculatorapp.ui.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonetizationOn
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
import com.example.emicalculatorapp.data.settings.NumberFormatStyle
import com.example.emicalculatorapp.ui.components.NeonChip
import com.example.emicalculatorapp.ui.components.QuantumSlider
import com.example.emicalculatorapp.ui.theme.EMIverseTypography
import com.example.emicalculatorapp.ui.theme.LuminousCyan
import com.example.emicalculatorapp.ui.theme.OutlineVariant
import com.example.emicalculatorapp.ui.theme.PrimaryLight
import com.example.emicalculatorapp.ui.theme.SurfaceContainerLowest
import com.example.emicalculatorapp.ui.theme.TextMuted
import com.example.emicalculatorapp.viewmodel.EMIViewModel

@Composable
fun LoanAmountCard(
    loanAmount: Double,
    onAmountChanged: (Double) -> Unit,
    onDraggingChanged: (Boolean) -> Unit,
    numberFormat: NumberFormatStyle,
    focusManager: FocusManager,
    modifier: Modifier = Modifier
) {
    ControlCard(
        icon = Icons.Default.MonetizationOn,
        iconTint = LuminousCyan,
        title = "Loan Amount",
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Principal Amount",
                style = EMIverseTypography.bodySm,
                color = TextMuted
            )

            // Inset Editable Field with Stable Cursor
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(SurfaceContainerLowest.copy(alpha = 0.85f))
                    .border(1.dp, OutlineVariant.copy(alpha = 0.3f), CircleShape)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "₹ ",
                    style = EMIverseTypography.headlineSm,
                    color = PrimaryLight,
                    fontWeight = FontWeight.Bold
                )

                var amountFieldValue by remember {
                    val initialFormatted = EMIViewModel.formatCurrency(loanAmount, numberFormat)
                        .replace("₹ ", "").replace("₹", "")
                    mutableStateOf(TextFieldValue(initialFormatted, TextRange(initialFormatted.length)))
                }

                // Keep field updated when slider, preset, or number format changes externally
                LaunchedEffect(loanAmount, numberFormat) {
                    val currentParsed = amountFieldValue.text.filter { it.isDigit() }.toDoubleOrNull() ?: 0.0
                    val formatted = EMIViewModel.formatCurrency(loanAmount, numberFormat)
                        .replace("₹ ", "").replace("₹", "")
                    if (currentParsed != loanAmount || amountFieldValue.text != formatted) {
                        amountFieldValue = TextFieldValue(formatted, TextRange(formatted.length))
                    }
                }

                BasicTextField(
                    value = amountFieldValue,
                    onValueChange = { input ->
                        amountFieldValue = formatAmountWithStableCursor(input, numberFormat) { parsed ->
                            if (parsed in 100000.0..10000000.0) {
                                onAmountChanged(parsed)
                            }
                        }
                    },
                    textStyle = EMIverseTypography.headlineSm.copy(
                        color = LuminousCyan,
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.Bold
                    ),
                    cursorBrush = SolidColor(LuminousCyan),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        val clean = amountFieldValue.text.filter { it.isDigit() }.toDoubleOrNull() ?: 100000.0
                        onAmountChanged(clean)
                        focusManager.clearFocus()
                    }),
                    modifier = Modifier
                        .width(120.dp)
                        .onFocusChanged { focusState ->
                            if (!focusState.isFocused) {
                                val clean = amountFieldValue.text.filter { it.isDigit() }.toDoubleOrNull() ?: 100000.0
                                onAmountChanged(clean)
                                val formatted = EMIViewModel.formatCurrency(loanAmount, numberFormat)
                                    .replace("₹ ", "").replace("₹", "")
                                amountFieldValue = TextFieldValue(formatted, TextRange(formatted.length))
                            }
                        }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NeonChip(
                text = "₹10 L",
                isSelected = loanAmount == 1000000.0,
                onClick = { onAmountChanged(1000000.0) }
            )
            NeonChip(
                text = "₹25 L",
                isSelected = loanAmount == 2500000.0,
                onClick = { onAmountChanged(2500000.0) }
            )
            NeonChip(
                text = "₹45 L",
                isSelected = loanAmount == 4500000.0,
                onClick = { onAmountChanged(4500000.0) }
            )
            NeonChip(
                text = "₹75 L",
                isSelected = loanAmount == 7500000.0,
                onClick = { onAmountChanged(7500000.0) }
            )
            NeonChip(
                text = "₹1 Cr",
                isSelected = loanAmount == 10000000.0,
                onClick = { onAmountChanged(10000000.0) }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        QuantumSlider(
            value = loanAmount.toFloat(),
            onValueChange = { onAmountChanged(it.toDouble()) },
            valueRange = 100000f..10000000f,
            stepHapticThreshold = 50000f,
            bubbleText = EMIViewModel.formatCompactCurrency(loanAmount, numberFormat),
            onDraggingChanged = onDraggingChanged
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "₹ 1 Lakh", style = EMIverseTypography.labelCaps)
            Text(text = "₹ 50 Lakhs", style = EMIverseTypography.labelCaps)
            Text(text = "₹ 1 Crore", style = EMIverseTypography.labelCaps)
        }
    }
}
