package com.example.emicalculatorapp.ui.calculator

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.emicalculatorapp.data.settings.NumberFormatStyle
import com.example.emicalculatorapp.model.LoanCategory
import com.example.emicalculatorapp.ui.saved.SaveQuoteBottomSheet
import com.example.emicalculatorapp.viewmodel.SavedQuotesState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun BoxScope.CalculatorSaveDialogHost(
    showSaveSheet: Boolean,
    onDismissSheet: () -> Unit,
    selectedCategory: LoanCategory,
    loanAmount: Double,
    interestRate: Double,
    tenureValue: Int,
    isYearMode: Boolean,
    numberFormat: NumberFormatStyle,
    savedQuotesState: SavedQuotesState?,
    onNavigateToSaved: () -> Unit,
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState
) {
    if (showSaveSheet) {
        val tenureMonths = if (isYearMode) tenureValue * 12 else tenureValue
        SaveQuoteBottomSheet(
            category = selectedCategory,
            principal = loanAmount,
            annualRate = interestRate,
            tenureMonths = tenureMonths,
            processingFeePercent = 0.0,
            numberFormat = numberFormat,
            onSaveQuote = { quoteName ->
                savedQuotesState?.saveQuote(
                    name = quoteName,
                    category = selectedCategory,
                    principal = loanAmount,
                    rate = interestRate,
                    tenureMonths = tenureMonths,
                    processingFeePercent = 0.0,
                    onSaved = {
                        coroutineScope.launch {
                            val snackRes = snackbarHostState.showSnackbar(
                                message = "Saved",
                                actionLabel = "View",
                                duration = SnackbarDuration.Short
                            )
                            if (snackRes == SnackbarResult.ActionPerformed) {
                                onNavigateToSaved()
                            }
                        }
                    }
                )
            },
            onDismiss = onDismissSheet
        )
    }

    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 85.dp, start = 16.dp, end = 16.dp)
    )
}
