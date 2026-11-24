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
import com.example.emicalculatorapp.model.FullAmortizationSchedule
import com.example.emicalculatorapp.model.LoanCategory
import com.example.emicalculatorapp.util.CurrencyFormatter
import com.example.emicalculatorapp.model.PrepaymentCalculator
import com.example.emicalculatorapp.model.PrepaymentParams
import com.example.emicalculatorapp.model.PrepaymentSimulationResult
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

private data class LoanScheduleParams(
    val principal: Double,
    val annualRate: Double,
    val tenure: Int,
    val isYears: Boolean
)

private data class LoanPrepaymentConfig(
    val principal: Double,
    val annualRate: Double,
    val tenure: Int,
    val isYears: Boolean,
    val params: PrepaymentParams
)

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

    init {
        // Cancellable Full Schedule Calculation:
        // Automatically cancels stale computations when slider is rapidly dragged.
        // distinctUntilChanged ensures redundant inputs are skipped.
        viewModelScope.launch {
            combine(
                _loanAmount,
                _interestRate,
                _tenureValue,
                _isYearMode
            ) { p, r, t, ym ->
                LoanScheduleParams(p, r, t, ym)
            }
                .distinctUntilChanged()
                .collectLatest { config ->
                    val schedule = withContext(Dispatchers.Default) {
                        calculateFullSchedule(config.principal, config.annualRate, config.tenure, config.isYears)
                    }
                    _fullSchedule.value = schedule
                }
        }

        // Cancellable Prepayment Simulation:
        // Automatically cancels and recomputes off-thread on Dispatchers.Default.
        viewModelScope.launch {
            combine(
                _loanAmount,
                _interestRate,
                _tenureValue,
                _isYearMode,
                _prepaymentParams
            ) { p, r, t, ym, params ->
                LoanPrepaymentConfig(p, r, t, ym, params)
            }
                .distinctUntilChanged()
                .collectLatest { config ->
                    val simResult = withContext(Dispatchers.Default) {
                        PrepaymentCalculator.calculatePrepaymentSimulation(
                            principal = config.principal,
                            annualRate = config.annualRate,
                            tenure = config.tenure,
                            isYears = config.isYears,
                            params = config.params
                        )
                    }
                    _prepaymentResult.value = simResult
                }
        }
    }

    fun selectCategory(category: LoanCategory) {
        _selectedCategory.value = category
        _loanAmount.value = category.defaultAmount
        _interestRate.value = category.defaultRate
        _isYearMode.value = true
        _tenureValue.value = category.defaultTenureYears
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

    fun resetPrepayment() {
        _prepaymentParams.value = PrepaymentParams()
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
        /**
         * Pure mathematical calculation function using Double precision.
         * Formula:
         * r = annualRate / 12 / 100
         * n = totalMonths
         * EMI = P * r * (1 + r)^n / ((1 + r)^n - 1)
         * If r == 0: EMI = P / n
         */
        fun calculateEMI(
            principal: Double,
            annualRate: Double,
            tenure: Int,
            isYears: Boolean
        ): EMICalculationResult {
            val n = if (isYears) tenure * 12 else tenure
            if (n <= 0 || principal <= 0.0) {
                return EMICalculationResult(0.0, 0.0, 0.0, 0f, 0f, 0.0, emptyList())
            }

            val r = (annualRate / 12.0) / 100.0
            val emi: Double = if (r > 0.0) {
                val factor = (1.0 + r).pow(n.toDouble())
                (principal * r * factor) / (factor - 1.0)
            } else {
                principal / n.toDouble()
            }

            val totalPayment = emi * n
            val totalInterest = (totalPayment - principal).coerceAtLeast(0.0)
            val principalShare = if (totalPayment > 0) (principal / totalPayment).toFloat() else 0.5f
            val interestShare = if (totalPayment > 0) (totalInterest / totalPayment).toFloat() else 0.5f
            val yearlyVelocity = emi * 12.0

            // Generate Amortization Milestones dynamically
            val milestones = com.example.emicalculatorapp.model.AmortizationScheduleCalculator.generateAmortizationMilestones(principal, r, emi, n)

            return EMICalculationResult(
                monthlyEMI = emi,
                totalPayment = totalPayment,
                totalInterest = totalInterest,
                principalSharePercent = principalShare * 100f,
                interestSharePercent = interestShare * 100f,
                yearlyVelocity = yearlyVelocity,
                milestones = milestones
            )
        }

        /**
         * Calculates full month-by-month and year-by-year amortization schedule.
         * Satisfies exact balance clearing (final balance is 0.0) and closed conservation of capital.
         */
        fun calculateFullSchedule(
            principal: Double,
            annualRate: Double,
            tenure: Int,
            isYears: Boolean
        ): FullAmortizationSchedule = com.example.emicalculatorapp.model.AmortizationScheduleCalculator.calculateFullSchedule(
            principal, annualRate, tenure, isYears
        )

        /**
         * Simulates prepayment scenarios (extra monthly EMI, lump-sum, annual step-up).
         */
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

        fun formatCurrency(
            amount: Double,
            formatStyle: NumberFormatStyle = NumberFormatStyle.INDIAN,
            includeDecimals: Boolean = false
        ): String = CurrencyFormatter.formatCurrency(amount, formatStyle, includeDecimals)

        fun formatCompactIndianLakh(amount: Double): String =
            CurrencyFormatter.formatCompactIndianLakh(amount)

        fun formatCompactInternational(amount: Double): String =
            CurrencyFormatter.formatCompactInternational(amount)

        fun formatCompactCurrency(
            amount: Double,
            formatStyle: NumberFormatStyle = NumberFormatStyle.INDIAN
        ): String = CurrencyFormatter.formatCompactCurrency(amount, formatStyle)

        fun formatAmountWithStableCursor(
            input: TextFieldValue,
            formatStyle: NumberFormatStyle = NumberFormatStyle.INDIAN,
            onAmountParsed: (Double) -> Unit = {}
        ): TextFieldValue = CurrencyFormatter.formatAmountWithStableCursor(input, formatStyle, onAmountParsed)
    }
}
