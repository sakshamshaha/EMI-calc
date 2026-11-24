package com.example.emicalculatorapp.ui.tools

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
import androidx.compose.material.icons.filled.Payments
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
import androidx.compose.ui.graphics.Brush
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
import com.example.emicalculatorapp.ui.calculator.formatAmountWithStableCursor
import com.example.emicalculatorapp.ui.components.NeonChip
import com.example.emicalculatorapp.ui.components.QuantumSlider
import com.example.emicalculatorapp.ui.theme.InterFamily
import com.example.emicalculatorapp.ui.theme.LuminousCyan
import com.example.emicalculatorapp.ui.theme.MintGreen
import com.example.emicalculatorapp.ui.theme.OutlineVariant
import com.example.emicalculatorapp.ui.theme.SpaceGroteskFamily
import com.example.emicalculatorapp.ui.theme.SurfaceContainerLowest
import com.example.emicalculatorapp.ui.theme.TextMuted
import com.example.emicalculatorapp.viewmodel.EMIViewModel

@Composable
fun PrepaymentExtraCard(
    extraMonthly: Double,
    numberFormat: NumberFormatStyle,
    onExtraMonthlyChanged: (Double) -> Unit,
    onDraggingChanged: (Boolean) -> Unit,
    focusManager: FocusManager,
    modifier: Modifier = Modifier
) {
    PrepaymentContainerCard(
        icon = Icons.Default.Payments,
        iconTint = MintGreen,
        title = "Extra Monthly Contribution",
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Added to regular EMI each month",
                fontFamily = InterFamily,
                fontSize = 12.sp,
                color = TextMuted
            )

            // Editable Field
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
                    fontFamily = SpaceGroteskFamily,
                    fontSize = 16.sp,
                    color = MintGreen,
                    fontWeight = FontWeight.Bold
                )

                var extraFieldValue by remember {
                    val initial = EMIViewModel.formatCurrency(extraMonthly, numberFormat)
                        .replace("₹ ", "").replace("₹", "")
                    mutableStateOf(TextFieldValue(initial, TextRange(initial.length)))
                }

                LaunchedEffect(extraMonthly, numberFormat) {
                    val currentParsed = extraFieldValue.text.filter { it.isDigit() }.toDoubleOrNull() ?: 0.0
                    val formatted = EMIViewModel.formatCurrency(extraMonthly, numberFormat)
                        .replace("₹ ", "").replace("₹", "")
                    if (currentParsed != extraMonthly || extraFieldValue.text != formatted) {
                        extraFieldValue = TextFieldValue(formatted, TextRange(formatted.length))
                    }
                }

                BasicTextField(
                    value = extraFieldValue,
                    onValueChange = { input ->
                        extraFieldValue = formatAmountWithStableCursor(input, numberFormat) { parsed ->
                            onExtraMonthlyChanged(parsed.coerceIn(0.0, 500000.0))
                        }
                    },
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontFamily = SpaceGroteskFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MintGreen,
                        textAlign = TextAlign.End
                    ),
                    cursorBrush = SolidColor(MintGreen),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        val clean = extraFieldValue.text.filter { it.isDigit() }.toDoubleOrNull() ?: 0.0
                        onExtraMonthlyChanged(clean)
                        focusManager.clearFocus()
                    }),
                    modifier = Modifier
                        .width(90.dp)
                        .onFocusChanged { focusState ->
                            if (!focusState.isFocused) {
                                val clean = extraFieldValue.text.filter { it.isDigit() }.toDoubleOrNull() ?: 0.0
                                onExtraMonthlyChanged(clean)
                                val formatted = EMIViewModel.formatCurrency(extraMonthly, numberFormat)
                                    .replace("₹ ", "").replace("₹", "")
                                extraFieldValue = TextFieldValue(formatted, TextRange(formatted.length))
                            }
                        }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(0.0 to "None", 2000.0 to "₹2,000", 5000.0 to "₹5,000", 10000.0 to "₹10,000", 25000.0 to "₹25,000").forEach { (value, label) ->
                NeonChip(
                    text = label,
                    isSelected = extraMonthly == value,
                    onClick = { onExtraMonthlyChanged(value) },
                    selectedGradient = Brush.horizontalGradient(listOf(MintGreen, LuminousCyan))
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        QuantumSlider(
            value = extraMonthly.toFloat(),
            onValueChange = { onExtraMonthlyChanged(it.toDouble()) },
            valueRange = 0f..100000f,
            stepHapticThreshold = 1000f,
            bubbleText = EMIViewModel.formatCompactCurrency(extraMonthly, numberFormat),
            activeGradient = Brush.horizontalGradient(listOf(MintGreen, LuminousCyan)),
            ringColor = MintGreen,
            onDraggingChanged = onDraggingChanged
        )
    }
}
