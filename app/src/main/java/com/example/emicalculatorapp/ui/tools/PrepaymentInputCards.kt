package com.example.emicalculatorapp.ui.tools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.unit.dp
import com.example.emicalculatorapp.data.settings.NumberFormatStyle
import com.example.emicalculatorapp.model.PrepaymentParams

@Composable
fun PrepaymentInputCards(
    params: PrepaymentParams,
    maxTenureMonths: Int,
    numberFormat: NumberFormatStyle,
    onExtraMonthlyChanged: (Double) -> Unit,
    onLumpSumAmountChanged: (Double) -> Unit,
    onLumpSumMonthChanged: (Int) -> Unit,
    onLumpSumEnabledChanged: (Boolean) -> Unit,
    onStepUpPercentChanged: (Double) -> Unit,
    onStepUpEnabledChanged: (Boolean) -> Unit,
    onDraggingChanged: (Boolean) -> Unit,
    focusManager: FocusManager,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Extra Monthly Contribution Card
        PrepaymentExtraCard(
            extraMonthly = params.extraMonthly,
            numberFormat = numberFormat,
            onExtraMonthlyChanged = onExtraMonthlyChanged,
            onDraggingChanged = onDraggingChanged,
            focusManager = focusManager
        )

        // 2. One-Time Lump Sum Prepayment Card
        PrepaymentLumpSumCard(
            isLumpSumEnabled = params.isLumpSumEnabled,
            lumpSumAmount = params.lumpSumAmount,
            lumpSumMonth = params.lumpSumMonth,
            maxTenureMonths = maxTenureMonths,
            numberFormat = numberFormat,
            onLumpSumEnabledChanged = onLumpSumEnabledChanged,
            onLumpSumAmountChanged = onLumpSumAmountChanged,
            onLumpSumMonthChanged = onLumpSumMonthChanged,
            onDraggingChanged = onDraggingChanged
        )

        // 3. Annual EMI Step-Up % Card
        PrepaymentStepUpCard(
            isStepUpEnabled = params.isStepUpEnabled,
            stepUpPercent = params.stepUpPercent,
            onStepUpEnabledChanged = onStepUpEnabledChanged,
            onStepUpPercentChanged = onStepUpPercentChanged,
            onDraggingChanged = onDraggingChanged
        )
    }
}
