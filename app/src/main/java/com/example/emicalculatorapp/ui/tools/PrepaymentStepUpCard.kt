package com.example.emicalculatorapp.ui.tools

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emicalculatorapp.model.StepUpMode
import com.example.emicalculatorapp.ui.components.NeonChip
import com.example.emicalculatorapp.ui.components.QuantumSlider
import com.example.emicalculatorapp.ui.components.fx.GlowPillSwitch
import com.example.emicalculatorapp.ui.theme.ElectricViolet
import com.example.emicalculatorapp.ui.theme.InterFamily
import com.example.emicalculatorapp.ui.theme.MintGreen
import com.example.emicalculatorapp.ui.theme.SpaceGroteskFamily
import com.example.emicalculatorapp.ui.theme.TextMuted
import com.example.emicalculatorapp.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun PrepaymentStepUpCard(
    isStepUpEnabled: Boolean,
    stepUpPercent: Double,
    stepUpMode: StepUpMode,
    onStepUpEnabledChanged: (Boolean) -> Unit,
    onStepUpPercentChanged: (Double) -> Unit,
    onStepUpModeChanged: (StepUpMode) -> Unit,
    onDraggingChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    PrepaymentContainerCard(
        icon = Icons.AutoMirrored.Filled.TrendingUp,
        iconTint = ElectricViolet,
        title = "Annual EMI Step-Up %",
        modifier = modifier,
        trailingContent = {
            GlowPillSwitch(
                options = listOf(false, true),
                selectedOption = isStepUpEnabled,
                onOptionSelected = onStepUpEnabledChanged,
                labelProvider = { if (it) "ON" else "OFF" },
                modifier = Modifier.width(80.dp).height(32.dp)
            )
        }
    ) {
        AnimatedVisibility(
            visible = isStepUpEnabled,
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
                        text = "Increase repayment each year by",
                        fontFamily = InterFamily,
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                    Text(
                        text = String.format(Locale.US, "%.1f%% p.a.", stepUpPercent),
                        fontFamily = SpaceGroteskFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricViolet
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(5.0, 10.0, 15.0, 20.0).forEach { pct ->
                        NeonChip(
                            text = "${pct.toInt()}%",
                            isSelected = stepUpPercent == pct,
                            onClick = { onStepUpPercentChanged(pct) },
                            selectedGradient = Brush.horizontalGradient(listOf(ElectricViolet, MintGreen))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                QuantumSlider(
                    value = stepUpPercent.toFloat(),
                    onValueChange = { onStepUpPercentChanged(it.toDouble()) },
                    valueRange = 0f..20f,
                    stepHapticThreshold = 0.5f,
                    bubbleText = String.format(Locale.US, "%.1f%%", stepUpPercent),
                    activeGradient = Brush.horizontalGradient(listOf(ElectricViolet, MintGreen)),
                    ringColor = ElectricViolet,
                    onDraggingChanged = onDraggingChanged
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Step-up applies to:",
                    fontFamily = SpaceGroteskFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(6.dp))

                GlowPillSwitch(
                    options = listOf(StepUpMode.EXTRA_PAYMENT, StepUpMode.WHOLE_EMI),
                    selectedOption = stepUpMode,
                    onOptionSelected = onStepUpModeChanged,
                    labelProvider = { if (it == StepUpMode.EXTRA_PAYMENT) "Extra payment" else "Whole EMI" },
                    modifier = Modifier.fillMaxWidth().height(36.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (stepUpMode == StepUpMode.EXTRA_PAYMENT) {
                        "Extra payment mode: month payment = EMI + extra × (1 + step%)^(year-1)."
                    } else {
                        "Whole EMI mode: month payment = (EMI + extra) × (1 + step%)^(year-1)."
                    },
                    fontFamily = InterFamily,
                    fontSize = 11.sp,
                    color = TextMuted,
                    lineHeight = 15.sp
                )
            }
        }
        if (!isStepUpEnabled) {
            Text(
                text = "As your salary grows each year, stepping up EMI accelerates debt clearance exponentially.",
                fontFamily = InterFamily,
                fontSize = 12.sp,
                color = TextMuted
            )
        }
    }
}
