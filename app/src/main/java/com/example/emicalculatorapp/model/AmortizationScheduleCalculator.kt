package com.example.emicalculatorapp.model

import java.util.Calendar
import kotlin.math.pow

object AmortizationScheduleCalculator {

    /**
     * Calculates the complete month-by-month and year-by-year amortization schedule.
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

    fun generateAmortizationMilestones(
        principal: Double,
        monthlyRate: Double,
        monthlyEMI: Double,
        totalMonths: Int
    ): List<AmortizationMilestone> {
        val totalYears = ((totalMonths + 11) / 12).coerceAtLeast(1)
        val currentCalendarYear = Calendar.getInstance().get(Calendar.YEAR)

        val milestoneYears = listOf(1, 5, 10, totalYears)
            .filter { it <= totalYears }
            .distinct()
            .sorted()

        return milestoneYears.map { yr ->
            val endMonth = (yr * 12).coerceAtMost(totalMonths)
            val startMonth = ((yr - 1) * 12)

            // Closed-form remaining balance (O(1) calculation)
            val balanceEnd = if (endMonth >= totalMonths) 0.0 else calculateRemainingBalance(principal, monthlyRate, monthlyEMI, endMonth)
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

    fun calculateRemainingBalance(
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
}
