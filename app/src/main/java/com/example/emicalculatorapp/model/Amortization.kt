package com.example.emicalculatorapp.model

import androidx.compose.runtime.Immutable

@Immutable
data class AmortizationMilestone(
    val year: Int,
    val label: String,
    val remainingBalance: Double,
    val principalPaidSoFar: Double,
    val interestPaidSoFar: Double,
    val principalPercentInYear: Float,
    val interestPercentInYear: Float,
    val remainingPercentOfPrincipal: Float
)

@Immutable
data class AmortizationMonth(
    val monthIndex: Int,
    val yearIndex: Int,
    val openingBalance: Double,
    val emi: Double,
    val principalPaid: Double,
    val interestPaid: Double,
    val closingBalance: Double
)

@Immutable
data class AmortizationYear(
    val year: Int,
    val openingBalance: Double,
    val totalPrincipalPaid: Double,
    val totalInterestPaid: Double,
    val totalPayment: Double,
    val closingBalance: Double,
    val months: List<AmortizationMonth>
)

@Immutable
data class FullAmortizationSchedule(
    val principal: Double,
    val annualRate: Double,
    val totalMonths: Int,
    val monthlyEMI: Double,
    val totalInterest: Double,
    val totalPayment: Double,
    val years: List<AmortizationYear>,
    val allMonths: List<AmortizationMonth>
)

@Immutable
data class EMICalculationResult(
    val monthlyEMI: Double,
    val totalPayment: Double,
    val totalInterest: Double,
    val principalSharePercent: Float,
    val interestSharePercent: Float,
    val yearlyVelocity: Double,
    val milestones: List<AmortizationMilestone>
)
