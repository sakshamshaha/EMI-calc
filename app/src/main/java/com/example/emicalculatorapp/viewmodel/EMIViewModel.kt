package com.example.emicalculatorapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.emicalculatorapp.model.AmortizationMilestone
import com.example.emicalculatorapp.model.EMICalculationResult
import com.example.emicalculatorapp.model.LoanCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
        _result.value = calculateEMI(
            principal = _loanAmount.value,
            annualRate = _interestRate.value,
            tenure = _tenureValue.value,
            isYears = _isYearMode.value
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
         * Formats numbers into native Indian grouping style (12,34,567) with ₹ symbol.
         */
        fun formatIndianCurrency(amount: Double, includeDecimals: Boolean = false): String {
            val roundedAmount = round(amount).toLong()
            val isNegative = roundedAmount < 0
            val absVal = abs(roundedAmount)
            val s = absVal.toString()
            val grouped = if (s.length <= 3) {
                s
            } else {
                val lastThree = s.substring(s.length - 3)
                val remaining = s.substring(0, s.length - 3)
                val sb = StringBuilder()
                var count = 0
                for (i in remaining.length - 1 downTo 0) {
                    sb.append(remaining[i])
                    count++
                    if (count == 2 && i != 0) {
                        sb.append(',')
                        count = 0
                    }
                }
                sb.reverse().toString() + "," + lastThree
            }

            val prefix = if (isNegative) "-₹ " else "₹ "
            return if (includeDecimals) {
                val cents = abs((amount * 100).toLong() % 100)
                String.format(Locale.US, "%s%s.%02d", prefix, grouped, cents)
            } else {
                "$prefix$grouped"
            }
        }

        fun formatCompactIndianLakh(amount: Double): String {
            val lakhs = amount / 100000.0
            return if (lakhs >= 100.0) {
                String.format(Locale.US, "₹%.1f Cr", lakhs / 100.0)
            } else if (lakhs >= 1.0) {
                String.format(Locale.US, "₹%.1f L", lakhs)
            } else {
                String.format(Locale.US, "₹%.0f K", amount / 1000.0)
            }
        }
    }
}
