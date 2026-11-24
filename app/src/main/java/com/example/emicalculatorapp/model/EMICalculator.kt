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

    /**
     * Computes the step-by-step mathematical breakdown of the standard amortization formula.
     * When rate is 0, yields the simple variant: EMI = P / n.
     */
    fun calculateMathBreakdown(
        principal: Double,
        annualRate: Double,
        tenureMonths: Int
    ): MathBreakdown {
        val n = tenureMonths.coerceAtLeast(1)
        val isZeroRate = annualRate <= 0.0
        val r = if (isZeroRate) 0.0 else (annualRate / 12.0) / 100.0

        if (isZeroRate) {
            val emi = principal / n.toDouble()
            return MathBreakdown(
                principal = principal,
                annualRate = 0.0,
                tenureMonths = n,
                isZeroRate = true,
                monthlyRate = 0.0,
                growthFactor = 1.0,
                numerator = principal,
                denominator = n.toDouble(),
                emi = emi,
                month1Interest = 0.0,
                month1Principal = emi,
                totalPayment = principal,
                totalInterest = 0.0
            )
        }

        val growthFactor = (1.0 + r).pow(n.toDouble())
        val numerator = principal * r * growthFactor
        val denominator = growthFactor - 1.0
        val emi = numerator / denominator
        val month1Interest = principal * r
        val month1Principal = emi - month1Interest
        val totalPayment = emi * n.toDouble()
        val totalInterest = totalPayment - principal

        return MathBreakdown(
            principal = principal,
            annualRate = annualRate,
            tenureMonths = n,
            isZeroRate = false,
            monthlyRate = r,
            growthFactor = growthFactor,
            numerator = numerator,
            denominator = denominator,
            emi = emi,
            month1Interest = month1Interest,
            month1Principal = month1Principal,
            totalPayment = totalPayment,
            totalInterest = totalInterest
        )
    }
}
