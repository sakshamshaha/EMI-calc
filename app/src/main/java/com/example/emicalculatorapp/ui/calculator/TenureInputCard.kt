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
import androidx.compose.material.icons.filled.Schedule
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
import com.example.emicalculatorapp.ui.components.fx.GlowPillSwitch
import com.example.emicalculatorapp.ui.theme.EMIverseTypography
import com.example.emicalculatorapp.ui.theme.ElectricViolet
import com.example.emicalculatorapp.ui.theme.OutlineVariant
import com.example.emicalculatorapp.ui.theme.PrimaryContainer
import com.example.emicalculatorapp.ui.theme.PrimaryLight
import com.example.emicalculatorapp.ui.theme.SurfaceContainerLowest
import kotlin.math.roundToInt

@Composable
fun LoanTenureCard(
    tenureValue: Int,
    isYearMode: Boolean,
    onTenureChanged: (Int) -> Unit,
    onYearModeChanged: (Boolean) -> Unit,
    onDraggingChanged: (Boolean) -> Unit,
    focusManager: FocusManager,
    modifier: Modifier = Modifier
) {
    ControlCard(
        icon = Icons.Default.Schedule,
        iconTint = PrimaryLight,
        title = "Loan Tenure",
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Segmented Toggle (Yr / Mo) using GlowPillSwitch
            GlowPillSwitch(
                options = listOf(true, false),
                selectedOption = isYearMode,
                onOptionSelected = onYearModeChanged,
                labelProvider = { if (it) "Yr" else "Mo" },
                modifier = Modifier.width(96.dp).height(36.dp)
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
                var tenureFieldValue by remember {
                    val initial = tenureValue.toString()
                    mutableStateOf(TextFieldValue(initial, TextRange(initial.length)))
                }

                LaunchedEffect(tenureValue) {
                    if (tenureFieldValue.text.toIntOrNull() != tenureValue) {
                        val s = tenureValue.toString()
                        tenureFieldValue = TextFieldValue(s, TextRange(s.length))
                    }
                }

                val maxTenure = if (isYearMode) 30 else 360

                BasicTextField(
                    value = tenureFieldValue,
                    onValueChange = { input ->
                        tenureFieldValue = formatTenureWithStableCursor(input, maxTenure) { parsed ->
                            if (parsed in 1..maxTenure) {
                                onTenureChanged(parsed)
                            }
                        }
                    },
                    textStyle = EMIverseTypography.headlineSm.copy(
                        color = PrimaryLight,
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.Bold
                    ),
                    cursorBrush = SolidColor(PrimaryLight),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        val clean = tenureFieldValue.text.toIntOrNull() ?: if (isYearMode) 20 else 240
                        onTenureChanged(clean)
                        focusManager.clearFocus()
                    }),
                    modifier = Modifier
                        .width(50.dp)
                        .onFocusChanged { focusState ->
                            if (!focusState.isFocused) {
                                val clean = tenureFieldValue.text.toIntOrNull() ?: if (isYearMode) 20 else 240
                                onTenureChanged(clean)
                                val s = tenureValue.toString()
                                tenureFieldValue = TextFieldValue(s, TextRange(s.length))
                            }
                        }
                )

                Text(
                    text = if (isYearMode) " Yrs" else " Mo",
                    style = EMIverseTypography.headlineSm,
                    color = PrimaryLight,
                    fontWeight = FontWeight.Bold
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
            val quickPresets = if (isYearMode) {
                listOf(5 to "5 Yrs", 10 to "10 Yrs", 15 to "15 Yrs", 20 to "20 Yrs", 30 to "30 Yrs")
            } else {
                listOf(60 to "60 Mo", 120 to "120 Mo", 180 to "180 Mo", 240 to "240 Mo", 360 to "360 Mo")
            }
            quickPresets.forEach { (value, label) ->
                NeonChip(
                    text = label,
                    isSelected = tenureValue == value,
                    onClick = { onTenureChanged(value) },
                    selectedGradient = Brush.horizontalGradient(listOf(ElectricViolet, PrimaryContainer))
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        val maxRange = if (isYearMode) 30f else 360f
        QuantumSlider(
            value = tenureValue.toFloat(),
            onValueChange = { onTenureChanged(it.roundToInt()) },
            valueRange = 1f..maxRange,
            stepHapticThreshold = 1f,
            bubbleText = if (isYearMode) "$tenureValue Yrs" else "$tenureValue Mo",
            activeGradient = Brush.horizontalGradient(listOf(ElectricViolet, PrimaryContainer)),
            ringColor = PrimaryLight,
            onDraggingChanged = onDraggingChanged
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = if (isYearMode) "1 Yr" else "12 Mo", style = EMIverseTypography.labelCaps)
            Text(text = if (isYearMode) "15 Yrs" else "180 Mo", style = EMIverseTypography.labelCaps)
            Text(text = if (isYearMode) "30 Yrs" else "360 Mo", style = EMIverseTypography.labelCaps)
        }
    }
}
