package com.example.emicalculatorapp.viewmodel

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emicalculatorapp.data.settings.NumberFormatStyle
import com.example.emicalculatorapp.model.AmortizationMilestone
import com.example.emicalculatorapp.model.AmortizationMonth
import com.example.emicalculatorapp.model.AmortizationYear
import com.example.emicalculatorapp.model.EMICalculationResult
import com.example.emicalculatorapp.model.EMICalculator
import com.example.emicalculatorapp.model.FullAmortizationSchedule
import com.example.emicalculatorapp.model.LoanCategory
import com.example.emicalculatorapp.model.LoanComparisonCalculator
import com.example.emicalculatorapp.model.LoanComparisonResult
import com.example.emicalculatorapp.model.LoanInputState
import com.example.emicalculatorapp.util.CurrencyFormatter
import com.example.emicalculatorapp.model.PrepaymentCalculator
import com.example.emicalculatorapp.model.PrepaymentParams
import com.example.emicalculatorapp.model.PrepaymentSimulationResult
import com.example.emicalculatorapp.model.StepUpMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.round

private data class LoanScheduleParams(val principal: Double, val annualRate: Double, val tenure: Int, val isYears: Boolean)
private data class LoanPrepaymentConfig(val principal: Double, val annualRate: Double, val tenure: Int, val isYears: Boolean, val params: PrepaymentParams)

class EMIViewModel : ViewModel() {

    // Input States
    private val _selectedCategory = MutableStateFlow(LoanCategory.HOME)
    val selectedCategory: StateFlow<LoanCategory> = _selectedCategory.asStateFlow()

    private val _loanAmount = MutableStateFlow(4500000.0)
    val loanAmount: StateFlow<Double> = _loanAmount.asStateFlow()

    private val _interestRate = MutableStateFlow(8.55)
    val interestRate: StateFlow<Double> = _interestRate.asStateFlow()

    // Tenure stored in current unit (Years if isYearMode else Months)
    private val _tenureValue = MutableStateFlow(20)
    val tenureValue: StateFlow<Int> = _tenureValue.asStateFlow()

    private val _isYearMode = MutableStateFlow(true)
    val isYearMode: StateFlow<Boolean> = _isYearMode.asStateFlow()

    // Calculation Results
    private val _result = MutableStateFlow(calculateEMI(4500000.0, 8.55, 20, true))
    val result: StateFlow<EMICalculationResult> = _result.asStateFlow()

    // Off-thread Full Amortization Schedule
    private val _fullSchedule = MutableStateFlow(
        calculateFullSchedule(4500000.0, 8.55, 20, true)
    )
    val fullSchedule: StateFlow<FullAmortizationSchedule> = _fullSchedule.asStateFlow()

    // Prepayment Parameters & Accelerated Simulation Result
    private val _prepaymentParams = MutableStateFlow(PrepaymentParams())
    val prepaymentParams: StateFlow<PrepaymentParams> = _prepaymentParams.asStateFlow()

    private val _prepaymentResult = MutableStateFlow(
        PrepaymentCalculator.calculatePrepaymentSimulation(
            principal = 4500000.0,
            annualRate = 8.55,
            tenure = 20,
            isYears = true,
            params = PrepaymentParams()
        )
    )
    val prepaymentResult: StateFlow<PrepaymentSimulationResult> = _prepaymentResult.asStateFlow()

    // Loan Comparison State (Loan B independent state & comparison result)
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
        // 1. Cancellable Full Schedule Calculation
        viewModelScope.launch {
            combine(_loanAmount, _interestRate, _tenureValue, _isYearMode) { p, r, t, ym ->
                LoanScheduleParams(p, r, t, ym)
            }.distinctUntilChanged().collectLatest { cfg ->
                _fullSchedule.value = withContext(Dispatchers.Default) {
                    calculateFullSchedule(cfg.principal, cfg.annualRate, cfg.tenure, cfg.isYears)
                }
            }
        }

        // 2. Cancellable Prepayment Simulation
        viewModelScope.launch {
            combine(_loanAmount, _interestRate, _tenureValue, _isYearMode, _prepaymentParams) { p, r, t, ym, par ->
                LoanPrepaymentConfig(p, r, t, ym, par)
            }.distinctUntilChanged().collectLatest { cfg ->
                _prepaymentResult.value = withContext(Dispatchers.Default) {
                    PrepaymentCalculator.calculatePrepaymentSimulation(
                        cfg.principal, cfg.annualRate, cfg.tenure, cfg.isYears, cfg.params
                    )
                }
            }
        }

        // 3. Cancellable Loan Comparison Engine
        viewModelScope.launch {
            combine(_loanAmount, _interestRate, _tenureValue, _isYearMode, _loanBState) { p, r, t, ym, b ->
                LoanInputState(p, r, t, ym, 0.0) to b
            }.distinctUntilChanged().collectLatest { (a, b) ->
                _comparisonResult.value = withContext(Dispatchers.Default) {
                    LoanComparisonCalculator.compareLoans(a, b)
                }
            }
        }
    }

    fun selectCategory(category: LoanCategory) {
        _selectedCategory.value = category
        _loanAmount.value = category.defaultAmount
        _interestRate.value = category.defaultRate
        _isYearMode.value = true
        _tenureValue.value = category.defaultTenureYears
        clampPrepaymentLumpSumMonth()
        recalculate()
    }

    fun setLoanAmount(amount: Double) {
        val clamped = amount.coerceIn(100000.0, 10000000.0)
        if (_loanAmount.value == clamped) return
        _loanAmount.value = clamped
        recalculate()
    }

    fun setInterestRate(rate: Double) {
        val clamped = rate.coerceIn(1.0, 25.0)
        if (abs(_interestRate.value - clamped) < 0.0001) return
        _interestRate.value = clamped
        recalculate()
    }

    fun setTenureValue(value: Int) {
        val maxVal = if (_isYearMode.value) 30 else 360
        val clamped = value.coerceIn(1, maxVal)
        if (_tenureValue.value == clamped) return
        _tenureValue.value = clamped
        clampPrepaymentLumpSumMonth()
        recalculate()
    }

    fun setYearMode(isYear: Boolean) {
        if (_isYearMode.value == isYear) return
        _isYearMode.value = isYear
        if (isYear) {
            val yr = (_tenureValue.value / 12).coerceIn(1, 30)
            _tenureValue.value = yr
        } else {
            val mo = (_tenureValue.value * 12).coerceIn(1, 360)
            _tenureValue.value = mo
        }
        clampPrepaymentLumpSumMonth()
        recalculate()
    }

    fun resetToHomePreset() {
        selectCategory(LoanCategory.HOME)
    }

    // Prepayment Controls
    fun setExtraMonthly(amount: Double) {
        val clamped = amount.coerceAtLeast(0.0)
        if (_prepaymentParams.value.extraMonthly == clamped) return
        _prepaymentParams.value = _prepaymentParams.value.copy(extraMonthly = clamped)
    }

    fun setLumpSumAmount(amount: Double) {
        val clamped = amount.coerceAtLeast(0.0)
        if (_prepaymentParams.value.lumpSumAmount == clamped) return
        _prepaymentParams.value = _prepaymentParams.value.copy(lumpSumAmount = clamped)
    }

    fun setLumpSumMonth(month: Int) {
        val maxMonth = if (_isYearMode.value) _tenureValue.value * 12 else _tenureValue.value
        val clamped = month.coerceIn(1, maxMonth)
        if (_prepaymentParams.value.lumpSumMonth == clamped) return
        _prepaymentParams.value = _prepaymentParams.value.copy(lumpSumMonth = clamped)
    }

    fun setLumpSumEnabled(enabled: Boolean) {
        if (_prepaymentParams.value.isLumpSumEnabled == enabled) return
        _prepaymentParams.value = _prepaymentParams.value.copy(isLumpSumEnabled = enabled)
    }

    fun setStepUpPercent(percent: Double) {
        val clamped = percent.coerceIn(0.0, 20.0)
        if (_prepaymentParams.value.stepUpPercent == clamped) return
        _prepaymentParams.value = _prepaymentParams.value.copy(stepUpPercent = clamped)
    }

    fun setStepUpEnabled(enabled: Boolean) {
        if (_prepaymentParams.value.isStepUpEnabled == enabled) return
        _prepaymentParams.value = _prepaymentParams.value.copy(isStepUpEnabled = enabled)
    }

    fun setStepUpMode(mode: StepUpMode) {
        if (_prepaymentParams.value.stepUpMode == mode) return
        _prepaymentParams.value = _prepaymentParams.value.copy(stepUpMode = mode)
    }

    private fun clampPrepaymentLumpSumMonth() {
        val clamped = clampLumpSumMonth(
            currentLumpSumMonth = _prepaymentParams.value.lumpSumMonth,
            tenureValue = _tenureValue.value,
            isYearMode = _isYearMode.value
        )
        if (_prepaymentParams.value.lumpSumMonth != clamped) {
            _prepaymentParams.value = _prepaymentParams.value.copy(lumpSumMonth = clamped)
        }
    }

    fun resetPrepayment() {
        _prepaymentParams.value = PrepaymentParams()
    }

    // ================= LOAN B COMPARISON MUTATIONS =================
    fun setLoanBAmount(amount: Double) {
        val clamped = amount.coerceIn(10000.0, 50000000.0)
        if (_loanBState.value.amount == clamped) return
        _loanBState.value = _loanBState.value.copy(amount = clamped)
    }

    fun setLoanBRate(rate: Double) {
        val clamped = (round(rate.coerceIn(1.0, 30.0) * 100.0) / 100.0)
        if (_loanBState.value.rate == clamped) return
        _loanBState.value = _loanBState.value.copy(rate = clamped)
    }

    fun setLoanBTenure(tenure: Int) {
        val maxVal = if (_loanBState.value.isYearMode) 30 else 360
        val clamped = tenure.coerceIn(1, maxVal)
        if (_loanBState.value.tenureValue == clamped) return
        _loanBState.value = _loanBState.value.copy(tenureValue = clamped)
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
        if (_loanBState.value.processingFeePercent == clamped) return
        _loanBState.value = _loanBState.value.copy(processingFeePercent = clamped)
    }

    fun copyAToB() {
        _loanBState.value = LoanInputState(
            amount = _loanAmount.value,
            rate = _interestRate.value,
            tenureValue = _tenureValue.value,
            isYearMode = _isYearMode.value,
            processingFeePercent = _loanBState.value.processingFeePercent
        )
    }

    fun swapAAndB() {
        val prevAAmount = _loanAmount.value
        val prevARate = _interestRate.value
        val prevATenure = _tenureValue.value
        val prevAYearMode = _isYearMode.value

        val b = _loanBState.value

        _loanAmount.value = b.amount
        _interestRate.value = b.rate
        _isYearMode.value = b.isYearMode
        _tenureValue.value = b.tenureValue
        recalculate()
        clampPrepaymentLumpSumMonth()

        _loanBState.value = b.copy(
            amount = prevAAmount,
            rate = prevARate,
            tenureValue = prevATenure,
            isYearMode = prevAYearMode
        )
    }

    private fun recalculate() {
        val p = _loanAmount.value
        val r = _interestRate.value
        val t = _tenureValue.value
        val ym = _isYearMode.value

        _result.value = calculateEMI(
            principal = p,
            annualRate = r,
            tenure = t,
            isYears = ym
        )
    }

    companion object {
        fun clampLumpSumMonth(currentLumpSumMonth: Int, tenureValue: Int, isYearMode: Boolean): Int {
            val maxMonth = if (isYearMode) tenureValue * 12 else tenureValue
            return currentLumpSumMonth.coerceIn(1, maxMonth.coerceAtLeast(1))
        }

        fun calculateEMI(
            principal: Double,
            annualRate: Double,
            tenure: Int,
            isYears: Boolean
        ): EMICalculationResult = EMICalculator.calculateEMI(principal, annualRate, tenure, isYears)

        fun calculateFullSchedule(
            principal: Double,
            annualRate: Double,
            tenure: Int,
            isYears: Boolean
        ): FullAmortizationSchedule = com.example.emicalculatorapp.model.AmortizationScheduleCalculator.calculateFullSchedule(
            principal, annualRate, tenure, isYears
        )

        fun calculatePrepaymentSimulation(
            principal: Double,
            annualRate: Double,
            tenure: Int,
            isYears: Boolean,
            params: PrepaymentParams
        ): PrepaymentSimulationResult = PrepaymentCalculator.calculatePrepaymentSimulation(
            principal, annualRate, tenure, isYears, params
        )

        // Formatting delegations
        fun formatIndianCurrency(amount: Double, includeDecimals: Boolean = false): String =
            CurrencyFormatter.formatIndianCurrency(amount, includeDecimals)

        fun formatInternationalCurrency(amount: Double, includeDecimals: Boolean = false): String =
            CurrencyFormatter.formatInternationalCurrency(amount, includeDecimals)

        fun formatCurrency(amount: Double, formatStyle: NumberFormatStyle = NumberFormatStyle.INDIAN, includeDecimals: Boolean = false): String =
            CurrencyFormatter.formatCurrency(amount, formatStyle, includeDecimals)

        fun formatCompactIndianLakh(amount: Double): String = CurrencyFormatter.formatCompactIndianLakh(amount)

        fun formatCompactInternational(amount: Double): String = CurrencyFormatter.formatCompactInternational(amount)

        fun formatCompactCurrency(amount: Double, formatStyle: NumberFormatStyle = NumberFormatStyle.INDIAN): String =
            CurrencyFormatter.formatCompactCurrency(amount, formatStyle)

        fun formatAmountWithStableCursor(input: TextFieldValue, formatStyle: NumberFormatStyle = NumberFormatStyle.INDIAN, onAmountParsed: (Double) -> Unit = {}): TextFieldValue =
            CurrencyFormatter.formatAmountWithStableCursor(input, formatStyle, onAmountParsed)
    }
}
