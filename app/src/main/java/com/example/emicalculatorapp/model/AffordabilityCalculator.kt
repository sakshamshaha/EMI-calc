package com.example.emicalculatorapp.model

import kotlin.math.pow
import kotlin.math.round

object AffordabilityCalculator {

    fun calculateAffordability(
        income: Double,
        existingEmis: Double,
        maxRatioPercent: Double,
        rate: Double,
        tenureYears: Int,
        liveLoanAmount: Double,
        liveLoanEmi: Double
    ): AffordabilityResult {
        val isIncomeZero = income <= 0.0
        val n = (tenureYears * 12).coerceAtLeast(1)
        val maxRatio = (maxRatioPercent.coerceIn(20.0, 60.0)) / 100.0

        if (isIncomeZero) {
            return AffordabilityResult(
                monthlyIncome = 0.0,
                existingEmis = existingEmis,
                maxDtiRatioPercent = maxRatioPercent,
                maxAffordableEmi = 0.0,
                maxLoanAmount = 0.0,
                liveLoanAmount = liveLoanAmount,
                liveLoanEmi = liveLoanEmi,
                totalObligationRatio = 0.0,
                incomeNeededForLiveLoan = if (maxRatio > 0) (existingEmis + liveLoanEmi) / maxRatio else 0.0,
                additionalIncomeNeeded = 0.0,
                loanReductionNeeded = liveLoanAmount,
                zone = AffordabilityZone.COMFORTABLE,
                isIncomeZero = true,
                isExistingExceedingCap = false,
                summaryHeadline = "Enter your monthly take-home income",
                summaryDetail = "We'll calculate the maximum loan amount your budget can comfortably service."
            )
        }

        val maxAllowedObligations = income * maxRatio
        val isExistingExceedingCap = existingEmis >= maxAllowedObligations
        val maxAffordableEmi = (maxAllowedObligations - existingEmis).coerceAtLeast(0.0)

        // Loan amount from EMI: P = EMI * (1 - (1+r)^-n) / r
        val maxLoanAmount = if (rate <= 0.0001) {
            maxAffordableEmi * n
        } else {
            val r = (rate / 12.0) / 100.0
            val discountFactor = 1.0 - (1.0 + r).pow(-n.toDouble())
            maxAffordableEmi * discountFactor / r
        }

        // Live loan evaluation
        val totalObligationsWithLive = existingEmis + liveLoanEmi
        val totalObligationRatio = (totalObligationsWithLive / income) * 100.0
        val incomeNeededForLiveLoan = if (maxRatio > 0) totalObligationsWithLive / maxRatio else 0.0
        val additionalIncomeNeeded = (incomeNeededForLiveLoan - income).coerceAtLeast(0.0)
        val loanReductionNeeded = (liveLoanAmount - maxLoanAmount).coerceAtLeast(0.0)

        // Zone thresholds: Comfortable up to 30%, Moderate 30% to 40%, Stretch above 40%
        val zone = when {
            totalObligationRatio <= 30.00001 -> AffordabilityZone.COMFORTABLE
            totalObligationRatio <= 40.00001 -> AffordabilityZone.MODERATE
            else -> AffordabilityZone.STRETCH
        }

        // Plain sentence summaries
        val (headline, detail) = when {
            isExistingExceedingCap -> {
                "Existing obligations exceed your ${maxRatioPercent.toInt()}% budget cap" to
                        "Reduce existing debts or adjust your affordability ratio to unlock borrowing capacity."
            }
            totalObligationRatio > maxRatioPercent -> {
                "You can comfortably borrow up to the recommended cap" to
                        "This loan needs about Rs ${round(additionalIncomeNeeded).toLong()} more monthly income, or a loan about Rs ${round(loanReductionNeeded).toLong()} smaller."
            }
            else -> {
                "You can comfortably borrow within your target budget" to
                        "Your current loan plan fits safely within your ${maxRatioPercent.toInt()}% monthly debt-to-income threshold."
            }
        }

        return AffordabilityResult(
            monthlyIncome = income,
            existingEmis = existingEmis,
            maxDtiRatioPercent = maxRatioPercent,
            maxAffordableEmi = maxAffordableEmi,
            maxLoanAmount = maxLoanAmount,
            liveLoanAmount = liveLoanAmount,
            liveLoanEmi = liveLoanEmi,
            totalObligationRatio = totalObligationRatio,
            incomeNeededForLiveLoan = incomeNeededForLiveLoan,
            additionalIncomeNeeded = additionalIncomeNeeded,
            loanReductionNeeded = loanReductionNeeded,
            zone = zone,
            isIncomeZero = false,
            isExistingExceedingCap = isExistingExceedingCap,
            summaryHeadline = headline,
            summaryDetail = detail
        )
    }
}
