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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.round

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
    private var scheduleJob: Job? = null
    private val _fullSchedule = MutableStateFlow(
        calculateFullSchedule(4500000.0, 8.55, 20, true)
    )
    val fullSchedule: StateFlow<FullAmortizationSchedule> = _fullSchedule.asStateFlow()

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

        scheduleJob?.cancel()
        scheduleJob = viewModelScope.launch(Dispatchers.Default) {
            val schedule = calculateFullSchedule(p, r, t, ym)
            _fullSchedule.value = schedule
        }
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
            val milestones = generateAmortizationMilestones(principal, r, emi, n)

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

        private fun generateAmortizationMilestones(
            principal: Double,
            monthlyRate: Double,
            monthlyEMI: Double,
            totalMonths: Int
        ): List<AmortizationMilestone> {
            val totalYears = (totalMonths / 12).coerceAtLeast(1)
            val currentCalendarYear = Calendar.getInstance().get(Calendar.YEAR)

            val milestoneYears = listOf(1, 5, 10, totalYears)
                .filter { it <= totalYears }
                .distinct()
                .sorted()

            return milestoneYears.map { yr ->
                val endMonth = (yr * 12).coerceAtMost(totalMonths)
                val startMonth = ((yr - 1) * 12)

                // Closed-form remaining balance (O(1) calculation)
                val balanceEnd = calculateRemainingBalance(principal, monthlyRate, monthlyEMI, endMonth)
                val balanceStart = calculateRemainingBalance(principal, monthlyRate, monthlyEMI, startMonth)

                val principalPaidInYear = (balanceStart - balanceEnd).coerceAtLeast(0.0)
                val monthsInYear = endMonth - startMonth
                val totalPaidInYear = monthlyEMI * monthsInYear
                val interestPaidInYear = (totalPaidInYear - principalPaidInYear).coerceAtLeast(0.0)

                val totalPrincipalPaidSoFar = (principal - balanceEnd).coerceAtLeast(0.0)
                val totalInterestPaidSoFar = ((monthlyEMI * endMonth) - totalPrincipalPaidSoFar).coerceAtLeast(0.0)

                val yearTotal = principalPaidInYear + interestPaidInYear
                val principalPctInYear = if (yearTotal > 0) (principalPaidInYear / yearTotal).toFloat() * 100f else 50f
                val interestPctInYear = (100f - principalPctInYear).coerceAtLeast(0f)
                val remainingPct = if (principal > 0) ((balanceEnd / principal) * 100.0).toFloat().coerceIn(0f, 100f) else 0f

                val label = when (yr) {
                    1 -> "Year 1 (End of $currentCalendarYear)"
                    5 -> "Year 5 (End of ${currentCalendarYear + 4})"
                    10 -> "Year 10 (Crossover Point)"
                    totalYears -> "Year $totalYears (Payoff Completion)"
                    else -> "Year $yr"
                }

                AmortizationMilestone(
                    year = yr,
                    label = label,
                    remainingBalance = balanceEnd,
                    principalPaidSoFar = totalPrincipalPaidSoFar,
                    interestPaidSoFar = totalInterestPaidSoFar,
                    principalPercentInYear = principalPctInYear,
                    interestPercentInYear = interestPctInYear,
                    remainingPercentOfPrincipal = remainingPct
                )
            }
        }

        private fun calculateRemainingBalance(
            principal: Double,
            r: Double,
            emi: Double,
            months: Int
        ): Double {
            if (months <= 0) return principal
            if (r <= 0.0) {
                return (principal - (emi * months)).coerceAtLeast(0.0)
            }
            val factor = (1.0 + r).pow(months.toDouble())
            val balance = principal * factor - emi * (factor - 1.0) / r
            return balance.coerceAtLeast(0.0)
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
        ): FullAmortizationSchedule {
            val totalMonths = if (isYears) tenure * 12 else tenure
            if (totalMonths <= 0 || principal <= 0.0) {
                return FullAmortizationSchedule(
                    principal = principal,
                    annualRate = annualRate,
                    totalMonths = 0,
                    monthlyEMI = 0.0,
                    totalInterest = 0.0,
                    totalPayment = 0.0,
                    years = emptyList(),
                    allMonths = emptyList()
                )
            }

            val r = (annualRate / 12.0) / 100.0
            val monthlyEMI: Double = if (r > 0.0) {
                val factor = (1.0 + r).pow(totalMonths.toDouble())
                (principal * r * factor) / (factor - 1.0)
            } else {
                principal / totalMonths.toDouble()
            }

            var currentBalance = principal
            val allMonths = ArrayList<AmortizationMonth>(totalMonths)
            val years = ArrayList<AmortizationYear>((totalMonths + 11) / 12)

            var yearOpeningBalance = principal
            var yearPrincipalPaid = 0.0
            var yearInterestPaid = 0.0
            var yearTotalPayment = 0.0
            val currentYearMonths = ArrayList<AmortizationMonth>(12)

            var cumulativePrincipal = 0.0
            var cumulativeInterest = 0.0

            for (m in 1..totalMonths) {
                val interest = if (r > 0.0) currentBalance * r else 0.0
                val isLastMonth = m == totalMonths

                val principalPart: Double
                val actualEMI: Double
                val newBalance: Double

                if (isLastMonth) {
                    principalPart = currentBalance
                    actualEMI = principalPart + interest
                    newBalance = 0.0
                } else {
                    principalPart = (monthlyEMI - interest).coerceIn(0.0, currentBalance)
                    actualEMI = monthlyEMI
                    newBalance = (currentBalance - principalPart).coerceAtLeast(0.0)
                }

                val yearIndex = ((m - 1) / 12) + 1
                val monthData = AmortizationMonth(
                    monthIndex = m,
                    yearIndex = yearIndex,
                    openingBalance = currentBalance,
                    emi = actualEMI,
                    principalPaid = principalPart,
                    interestPaid = interest,
                    closingBalance = newBalance
                )

                allMonths.add(monthData)
                currentYearMonths.add(monthData)

                yearPrincipalPaid += principalPart
                yearInterestPaid += interest
                yearTotalPayment += actualEMI
                cumulativePrincipal += principalPart
                cumulativeInterest += interest

                currentBalance = newBalance

                // Close year bucket
                if (m % 12 == 0 || isLastMonth) {
                    years.add(
                        AmortizationYear(
                            year = yearIndex,
                            openingBalance = yearOpeningBalance,
                            totalPrincipalPaid = yearPrincipalPaid,
                            totalInterestPaid = yearInterestPaid,
                            totalPayment = yearTotalPayment,
                            closingBalance = currentBalance,
                            months = ArrayList(currentYearMonths)
                        )
                    )
                    yearOpeningBalance = currentBalance
                    yearPrincipalPaid = 0.0
                    yearInterestPaid = 0.0
                    yearTotalPayment = 0.0
                    currentYearMonths.clear()
                }
            }

            return FullAmortizationSchedule(
                principal = principal,
                annualRate = annualRate,
                totalMonths = totalMonths,
                monthlyEMI = monthlyEMI,
                totalInterest = cumulativeInterest,
                totalPayment = cumulativePrincipal + cumulativeInterest,
                years = years,
                allMonths = allMonths
            )
        }

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
