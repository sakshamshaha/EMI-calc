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
data class EMICalculationResult(
    val monthlyEMI: Double,
    val totalPayment: Double,
    val totalInterest: Double,
    val principalSharePercent: Float,
    val interestSharePercent: Float,
    val yearlyVelocity: Double,
    val milestones: List<AmortizationMilestone>
)
