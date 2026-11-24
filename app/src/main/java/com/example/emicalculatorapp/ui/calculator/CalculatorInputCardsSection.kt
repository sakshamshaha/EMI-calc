package com.example.emicalculatorapp.ui.calculator

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.unit.dp
import com.example.emicalculatorapp.data.settings.NumberFormatStyle
import com.example.emicalculatorapp.viewmodel.EMIViewModel

@Composable
fun CalculatorInputCardsSection(
    loanAmount: Double,
    interestRate: Double,
    tenureValue: Int,
    isYearMode: Boolean,
    numberFormat: NumberFormatStyle,
    viewModel: EMIViewModel,
    focusManager: FocusManager,
    onAmountDraggingChanged: (Boolean) -> Unit,
    onRateDraggingChanged: (Boolean) -> Unit,
    onTenureDraggingChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        LoanAmountCard(
            loanAmount = loanAmount,
            onAmountChanged = { viewModel.setLoanAmount(it) },
            onDraggingChanged = onAmountDraggingChanged,
            numberFormat = numberFormat,
            focusManager = focusManager
        )

        Spacer(modifier = Modifier.height(12.dp))

        InterestRateCard(
            interestRate = interestRate,
            onRateChanged = { viewModel.setInterestRate(it) },
            onDraggingChanged = onRateDraggingChanged,
            focusManager = focusManager
        )

        Spacer(modifier = Modifier.height(12.dp))

        LoanTenureCard(
            tenureValue = tenureValue,
            isYearMode = isYearMode,
            onTenureChanged = { viewModel.setTenureValue(it) },
            onYearModeChanged = { viewModel.setYearMode(it) },
            onDraggingChanged = onTenureDraggingChanged,
            focusManager = focusManager
        )
    }
}
