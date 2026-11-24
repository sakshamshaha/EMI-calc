package com.example.emicalculatorapp.ui.compare

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.emicalculatorapp.model.LoanCategory
import com.example.emicalculatorapp.ui.components.AmbientBackground
import com.example.emicalculatorapp.ui.saved.SaveQuoteBottomSheet
import com.example.emicalculatorapp.ui.theme.InterFamily
import com.example.emicalculatorapp.ui.theme.LocalNumberFormat
import com.example.emicalculatorapp.ui.theme.SpaceGroteskFamily
import com.example.emicalculatorapp.ui.theme.TextPrimary
import com.example.emicalculatorapp.ui.theme.TextSecondary
import com.example.emicalculatorapp.viewmodel.EMIViewModel
import com.example.emicalculatorapp.viewmodel.SavedQuotesState
import kotlinx.coroutines.launch

@Composable
fun CompareScreen(
    viewModel: EMIViewModel,
    savedQuotesState: SavedQuotesState? = null,
    onNavigateToSaved: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val compResult by viewModel.comparisonResult.collectAsStateWithLifecycle()
    val stateB by viewModel.loanBState.collectAsStateWithLifecycle()
    val numberFormat = LocalNumberFormat.current

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var isDraggingSlider by remember { mutableStateOf(false) }
    var showSaveSheetLoanA by remember { mutableStateOf(false) }
    var showSaveSheetLoanB by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
    ) {
        AmbientBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Title Header
            Column {
                Text(
                    text = "Compare Loans",
                    fontFamily = SpaceGroteskFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = TextPrimary
                )
                Text(
                    text = "Evaluate two loan options side-by-side to find the lowest total payout",
                    fontFamily = InterFamily,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // 1. Dynamic Hero Winner Card
            CompareHeroCard(
                result = compResult,
                numberFormat = numberFormat,
                isContinuousDrag = isDraggingSlider
            )

            // 2. Animated Delta & Stacked Breakdown Bars
            CompareBarsCard(
                result = compResult,
                numberFormat = numberFormat
            )

            // 3. Loan A Active Baseline Card
            CompareLoanACard(
                summaryA = compResult.loanA,
                numberFormat = numberFormat,
                onSaveLoanA = { showSaveSheetLoanA = true }
            )

            // 4. Quick Actions: Copy A to B and Swap
            CompareActionsRow(
                onCopyAToB = { viewModel.copyAToB() },
                onSwap = { viewModel.swapAAndB() }
            )

            // 5. Loan B Independent Inputs Card
            CompareLoanBCard(
                stateB = stateB,
                summaryB = compResult.loanB,
                numberFormat = numberFormat,
                onAmountChanged = { viewModel.setLoanBAmount(it) },
                onRateChanged = { viewModel.setLoanBRate(it) },
                onTenureChanged = { viewModel.setLoanBTenure(it) },
                onYearModeChanged = { viewModel.setLoanBYearMode(it) },
                onProcessingFeeChanged = { viewModel.setLoanBProcessingFee(it) },
                onDraggingChanged = { isDraggingSlider = it },
                onSaveLoanB = { showSaveSheetLoanB = true }
            )

            // Legal guidance footnote
            Text(
                text = "Estimates only. Processing fees modeled as percentage of principal.",
                fontFamily = InterFamily,
                fontSize = 11.sp,
                color = TextSecondary.copy(alpha = 0.6f),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 4.dp)
            )

            // Bottom bar clearance padding
            Spacer(modifier = Modifier.height(100.dp))
        }

        // Save Quote Modal Sheets
        if (showSaveSheetLoanA) {
            val summaryA = compResult.loanA
            val tenureMonths = if (summaryA.isYearMode) summaryA.tenureValue * 12 else summaryA.tenureValue
            SaveQuoteBottomSheet(
                category = LoanCategory.HOME,
                principal = summaryA.amount,
                annualRate = summaryA.rate,
                tenureMonths = tenureMonths,
                processingFeePercent = summaryA.processingFeePercent,
                numberFormat = numberFormat,
                onSaveQuote = { name ->
                    savedQuotesState?.saveQuote(
                        name = name,
                        category = LoanCategory.HOME,
                        principal = summaryA.amount,
                        rate = summaryA.rate,
                        tenureMonths = tenureMonths,
                        processingFeePercent = summaryA.processingFeePercent,
                        onSaved = {
                            coroutineScope.launch {
                                val res = snackbarHostState.showSnackbar(
                                    message = "Saved",
                                    actionLabel = "View",
                                    duration = SnackbarDuration.Short
                                )
                                if (res == SnackbarResult.ActionPerformed) {
                                    onNavigateToSaved()
                                }
                            }
                        }
                    )
                },
                onDismiss = { showSaveSheetLoanA = false }
            )
        }

        if (showSaveSheetLoanB) {
            val tenureMonths = if (stateB.isYearMode) stateB.tenureValue * 12 else stateB.tenureValue
            SaveQuoteBottomSheet(
                category = LoanCategory.PERSONAL,
                principal = stateB.amount,
                annualRate = stateB.rate,
                tenureMonths = tenureMonths,
                processingFeePercent = stateB.processingFeePercent,
                numberFormat = numberFormat,
                onSaveQuote = { name ->
                    savedQuotesState?.saveQuote(
                        name = name,
                        category = LoanCategory.PERSONAL,
                        principal = stateB.amount,
                        rate = stateB.rate,
                        tenureMonths = tenureMonths,
                        processingFeePercent = stateB.processingFeePercent,
                        onSaved = {
                            coroutineScope.launch {
                                val res = snackbarHostState.showSnackbar(
                                    message = "Saved",
                                    actionLabel = "View",
                                    duration = SnackbarDuration.Short
                                )
                                if (res == SnackbarResult.ActionPerformed) {
                                    onNavigateToSaved()
                                }
                            }
                        }
                    )
                },
                onDismiss = { showSaveSheetLoanB = false }
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 85.dp, start = 16.dp, end = 16.dp)
        )
    }
}
