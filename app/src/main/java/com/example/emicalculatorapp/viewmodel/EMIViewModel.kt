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

    // Dedicated Feature State Holders
    val prepayment = PrepaymentState(viewModelScope, _loanAmount, _interestRate, _tenureValue, _isYearMode)
    val compare = CompareState(viewModelScope, _loanAmount, _interestRate, _tenureValue, _isYearMode) {
        recalculate()
        clampPrepaymentLumpSumMonth()
    }
    val affordability = AffordabilityState(
        scope = viewModelScope,
        liveLoanAmount = _loanAmount,
        liveLoanResult = _result,
        liveRate = _interestRate,
        liveTenure = _tenureValue
    )
    private val _numberFormat = MutableStateFlow(NumberFormatStyle.INDIAN)
    val numberFormat: StateFlow<NumberFormatStyle> = _numberFormat.asStateFlow()
    fun setNumberFormat(format: NumberFormatStyle) {
        _numberFormat.value = format
    }

    val insightsState = InsightsState(
        scope = viewModelScope,
        loanAmount = _loanAmount,
        interestRate = _interestRate,
        tenureValue = _tenureValue,
        isYearMode = _isYearMode,
        numberFormat = _numberFormat
    )
    val insights: StateFlow<List<com.example.emicalculatorapp.model.LoanInsight>> = insightsState.insights
    val rateSensitivity: StateFlow<List<com.example.emicalculatorapp.model.RateSensitivityPoint>> = insightsState.sensitivityPoints

    fun getMathBreakdown(): com.example.emicalculatorapp.model.MathBreakdown {
        val n = if (_isYearMode.value) _tenureValue.value * 12 else _tenureValue.value
        return EMICalculator.calculateMathBreakdown(_loanAmount.value, _interestRate.value, n)
    }

    // Direct Forwarders for Prepayment State
    val prepaymentParams: StateFlow<PrepaymentParams> = prepayment.params
    val prepaymentResult: StateFlow<PrepaymentSimulationResult> = prepayment.result
    fun setExtraMonthly(amount: Double) = prepayment.setExtraMonthly(amount)
    fun setLumpSumAmount(amount: Double) = prepayment.setLumpSumAmount(amount)
    fun setLumpSumMonth(month: Int) = prepayment.setLumpSumMonth(month)
    fun setLumpSumEnabled(enabled: Boolean) = prepayment.setLumpSumEnabled(enabled)
    fun setStepUpPercent(percent: Double) = prepayment.setStepUpPercent(percent)
    fun setStepUpEnabled(enabled: Boolean) = prepayment.setStepUpEnabled(enabled)
    fun setStepUpMode(mode: StepUpMode) = prepayment.setStepUpMode(mode)
    fun resetPrepayment() = prepayment.reset()

    // Direct Forwarders for Loan Comparison State
    val loanBState: StateFlow<LoanInputState> = compare.loanBState
    val comparisonResult: StateFlow<LoanComparisonResult> = compare.comparisonResult
    fun setLoanBAmount(amount: Double) = compare.setLoanBAmount(amount)
    fun setLoanBRate(rate: Double) = compare.setLoanBRate(rate)
    fun setLoanBTenure(tenure: Int) = compare.setLoanBTenure(tenure)
    fun setLoanBYearMode(isYear: Boolean) = compare.setLoanBYearMode(isYear)
    fun setLoanBProcessingFee(feePercent: Double) = compare.setLoanBProcessingFee(feePercent)
    fun copyAToB() = compare.copyAToB()
    fun swapAAndB() = compare.swapAAndB()

    init {
        // Cancellable Full Schedule Calculation
        viewModelScope.launch {
            combine(_loanAmount, _interestRate, _tenureValue, _isYearMode) { p, r, t, ym ->
                LoanScheduleParams(p, r, t, ym)
            }.distinctUntilChanged().collectLatest { cfg ->
                _fullSchedule.value = withContext(Dispatchers.Default) {
                    calculateFullSchedule(cfg.principal, cfg.annualRate, cfg.tenure, cfg.isYears)
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

    fun loadQuote(
        category: LoanCategory,
        amount: Double,
        rate: Double,
        tenureValue: Int,
        isYearMode: Boolean
    ) {
        _selectedCategory.value = category
        _loanAmount.value = amount
        _interestRate.value = rate
        _isYearMode.value = isYearMode
        _tenureValue.value = tenureValue
        clampPrepaymentLumpSumMonth()
        recalculate()
    }

    fun setLoanB(
        amount: Double,
        rate: Double,
        tenureValue: Int,
        isYearMode: Boolean,
        processingFeePercent: Double = 0.0
    ) = compare.setLoanB(amount, rate, tenureValue, isYearMode, processingFeePercent)

    private fun clampPrepaymentLumpSumMonth() = prepayment.clampLumpSumMonth()

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
