package com.example.emicalculatorapp.viewmodel

import com.example.emicalculatorapp.model.LoanComparisonCalculator
import com.example.emicalculatorapp.model.LoanComparisonResult
import com.example.emicalculatorapp.model.LoanInputState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.round

class CompareState(
    private val scope: CoroutineScope,
    private val loanAmount: MutableStateFlow<Double>,
    private val interestRate: MutableStateFlow<Double>,
    private val tenureValue: MutableStateFlow<Int>,
    private val isYearMode: MutableStateFlow<Boolean>,
    private val onLoanAChanged: () -> Unit
) {
    private val _loanBState = MutableStateFlow(
        LoanInputState(
            amount = 4500000.0,
            rate = 8.05,
            tenureValue = 20,
            isYearMode = true,
            processingFeePercent = 0.0
        )
    )
    val loanBState: StateFlow<LoanInputState> = _loanBState.asStateFlow()

    private val _comparisonResult = MutableStateFlow(
        LoanComparisonCalculator.compareLoans(
            LoanInputState(4500000.0, 8.55, 20, true, 0.0),
            LoanInputState(4500000.0, 8.05, 20, true, 0.0)
        )
    )
    val comparisonResult: StateFlow<LoanComparisonResult> = _comparisonResult.asStateFlow()

    init {
        scope.launch {
            combine(loanAmount, interestRate, tenureValue, isYearMode, _loanBState) { p, r, t, ym, b ->
                LoanInputState(p, r, t, ym, 0.0) to b
            }.distinctUntilChanged().collectLatest { (a, b) ->
                val comp = withContext(Dispatchers.Default) {
                    LoanComparisonCalculator.compareLoans(a, b)
                }
                _comparisonResult.value = comp
            }
        }
    }

    fun setLoanBAmount(amount: Double) {
        val clamped = amount.coerceIn(10000.0, 50000000.0)
        if (_loanBState.value.amount != clamped) {
            _loanBState.value = _loanBState.value.copy(amount = clamped)
        }
    }

    fun setLoanBRate(rate: Double) {
        val clamped = (round(rate.coerceIn(1.0, 30.0) * 100.0) / 100.0)
        if (_loanBState.value.rate != clamped) {
            _loanBState.value = _loanBState.value.copy(rate = clamped)
        }
    }

    fun setLoanBTenure(tenure: Int) {
        val maxVal = if (_loanBState.value.isYearMode) 30 else 360
        val clamped = tenure.coerceIn(1, maxVal)
        if (_loanBState.value.tenureValue != clamped) {
            _loanBState.value = _loanBState.value.copy(tenureValue = clamped)
        }
    }

    fun setLoanBYearMode(isYear: Boolean) {
        if (_loanBState.value.isYearMode == isYear) return
        val currentTenure = _loanBState.value.tenureValue
        val convertedTenure = if (isYear) {
            (currentTenure / 12).coerceIn(1, 30)
        } else {
            (currentTenure * 12).coerceIn(1, 360)
        }
        _loanBState.value = _loanBState.value.copy(
            isYearMode = isYear,
            tenureValue = convertedTenure
        )
    }

    fun setLoanBProcessingFee(feePercent: Double) {
        val clamped = (round(feePercent.coerceIn(0.0, 5.0) * 100.0) / 100.0)
        if (_loanBState.value.processingFeePercent != clamped) {
            _loanBState.value = _loanBState.value.copy(processingFeePercent = clamped)
        }
    }

    fun setLoanB(
        amount: Double,
        rate: Double,
        tenureValue: Int,
        isYearMode: Boolean,
        processingFeePercent: Double = 0.0
    ) {
        _loanBState.value = LoanInputState(
            amount = amount.coerceIn(10000.0, 50000000.0),
            rate = (round(rate.coerceIn(1.0, 30.0) * 100.0) / 100.0),
            tenureValue = tenureValue.coerceIn(1, if (isYearMode) 30 else 360),
            isYearMode = isYearMode,
            processingFeePercent = (round(processingFeePercent.coerceIn(0.0, 5.0) * 100.0) / 100.0)
        )
    }

    fun copyAToB() {
        _loanBState.value = LoanInputState(
            amount = loanAmount.value,
            rate = interestRate.value,
            tenureValue = tenureValue.value,
            isYearMode = isYearMode.value,
            processingFeePercent = _loanBState.value.processingFeePercent
        )
    }

    fun swapAAndB() {
        val prevAAmount = loanAmount.value
        val prevARate = interestRate.value
        val prevATenure = tenureValue.value
        val prevAYearMode = isYearMode.value

        val b = _loanBState.value

        loanAmount.value = b.amount
        interestRate.value = b.rate
        isYearMode.value = b.isYearMode
        tenureValue.value = b.tenureValue
        onLoanAChanged()

        _loanBState.value = b.copy(
            amount = prevAAmount,
            rate = prevARate,
            tenureValue = prevATenure,
            isYearMode = prevAYearMode
        )
    }
}
