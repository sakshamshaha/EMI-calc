package com.example.emicalculatorapp.model

import kotlin.math.pow

object EMICalculator {

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

        val milestones = AmortizationScheduleCalculator.generateAmortizationMilestones(principal, r, emi, n)

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
}
