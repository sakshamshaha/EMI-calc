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
import androidx.compose.material.icons.filled.Percent
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
import com.example.emicalculatorapp.ui.components.NeonChip
import com.example.emicalculatorapp.ui.components.QuantumSlider
import com.example.emicalculatorapp.ui.theme.EMIverseTypography
import com.example.emicalculatorapp.ui.theme.HotPink
import com.example.emicalculatorapp.ui.theme.OutlineVariant
import com.example.emicalculatorapp.ui.theme.SurfaceContainerLowest
import com.example.emicalculatorapp.ui.theme.TertiaryContainer
import com.example.emicalculatorapp.ui.theme.TextMuted
import java.util.Locale

@Composable
fun InterestRateCard(
    interestRate: Double,
    onRateChanged: (Double) -> Unit,
    onDraggingChanged: (Boolean) -> Unit,
    focusManager: FocusManager,
    modifier: Modifier = Modifier
) {
    ControlCard(
        icon = Icons.Default.Percent,
        iconTint = HotPink,
        title = "Interest Rate (p.a.)",
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Annual Percentage",
                style = EMIverseTypography.bodySm,
                color = TextMuted
            )

            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(SurfaceContainerLowest.copy(alpha = 0.85f))
                    .border(1.dp, OutlineVariant.copy(alpha = 0.3f), CircleShape)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                var rateFieldValue by remember {
                    val initial = String.format(Locale.US, "%.2f", interestRate)
                    mutableStateOf(TextFieldValue(initial, TextRange(initial.length)))
                }

                LaunchedEffect(interestRate) {
                    val currentParsed = rateFieldValue.text.toDoubleOrNull() ?: 0.0
                    if (currentParsed != interestRate) {
                        val formatted = String.format(Locale.US, "%.2f", interestRate)
                        rateFieldValue = TextFieldValue(formatted, TextRange(formatted.length))
                    }
                }

                BasicTextField(
                    value = rateFieldValue,
                    onValueChange = { input ->
                        rateFieldValue = formatRateWithStableCursor(input) { parsed ->
                            if (parsed in 1.0..25.0) {
                                onRateChanged(parsed)
                            }
                        }
                    },
                    textStyle = EMIverseTypography.headlineSm.copy(
                        color = HotPink,
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.Bold
                    ),
                    cursorBrush = SolidColor(HotPink),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        val clean = rateFieldValue.text.toDoubleOrNull() ?: 8.55
                        onRateChanged(clean)
                        focusManager.clearFocus()
                    }),
                    modifier = Modifier
                        .width(70.dp)
                        .onFocusChanged { focusState ->
                            if (!focusState.isFocused) {
                                val clean = rateFieldValue.text.toDoubleOrNull() ?: 8.55
                                onRateChanged(clean)
                                val formatted = String.format(Locale.US, "%.2f", interestRate)
                                rateFieldValue = TextFieldValue(formatted, TextRange(formatted.length))
                            }
                        }
                )

                Text(
                    text = " %",
                    style = EMIverseTypography.headlineSm,
                    color = HotPink,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Rate Quick Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NeonChip(
                text = "7.50%",
                isSelected = String.format(Locale.US, "%.2f", interestRate) == "7.50",
                onClick = { onRateChanged(7.50) },
                selectedGradient = Brush.horizontalGradient(listOf(HotPink, TertiaryContainer))
            )
            NeonChip(
                text = "8.55%",
                isSelected = String.format(Locale.US, "%.2f", interestRate) == "8.55",
                onClick = { onRateChanged(8.55) },
                selectedGradient = Brush.horizontalGradient(listOf(HotPink, TertiaryContainer))
            )
            NeonChip(
                text = "9.25%",
                isSelected = String.format(Locale.US, "%.2f", interestRate) == "9.25",
                onClick = { onRateChanged(9.25) },
                selectedGradient = Brush.horizontalGradient(listOf(HotPink, TertiaryContainer))
            )
            NeonChip(
                text = "11.50%",
                isSelected = String.format(Locale.US, "%.2f", interestRate) == "11.50",
                onClick = { onRateChanged(11.50) },
                selectedGradient = Brush.horizontalGradient(listOf(HotPink, TertiaryContainer))
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        QuantumSlider(
            value = interestRate.toFloat(),
            onValueChange = { onRateChanged(it.toDouble()) },
            valueRange = 4.0f..20.0f,
            stepHapticThreshold = 0.05f,
            bubbleText = String.format(Locale.US, "%.2f%%", interestRate),
            activeGradient = Brush.horizontalGradient(listOf(HotPink, TertiaryContainer)),
            ringColor = HotPink,
            onDraggingChanged = onDraggingChanged
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "4.0%", style = EMIverseTypography.labelCaps)
            Text(text = "12.0%", style = EMIverseTypography.labelCaps)
            Text(text = "20.0%", style = EMIverseTypography.labelCaps)
        }
    }
}
