package com.example.emicalculatorapp.model

/**
 * Breakdown of the EMI formula steps for educational presentation in "See the math".
 */
data class MathBreakdown(
    val principal: Double,
    val annualRate: Double,
    val tenureMonths: Int,
    val isZeroRate: Boolean,
    val monthlyRate: Double,
    val growthFactor: Double,
    val numerator: Double,
    val denominator: Double,
    val emi: Double,
    val month1Interest: Double,
    val month1Principal: Double,
    val totalPayment: Double,
    val totalInterest: Double
)
