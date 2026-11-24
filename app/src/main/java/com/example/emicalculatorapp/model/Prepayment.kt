package com.example.emicalculatorapp.model

import androidx.compose.runtime.Immutable

@Immutable
data class PrepaymentParams(
    val extraMonthly: Double = 0.0,
    val lumpSumAmount: Double = 0.0,
    val lumpSumMonth: Int = 12,
    val isLumpSumEnabled: Boolean = false,
    val stepUpPercent: Double = 0.0,
    val isStepUpEnabled: Boolean = false
)

@Immutable
data class PrepaymentPoint(
    val month: Int,
    val originalBalance: Double,
    val newBalance: Double
)

@Immutable
data class PrepaymentMilestones(
    val originalHalfwayMonth: Int,
    val newHalfwayMonth: Int,
    val originalCrossoverMonth: Int,
    val newCrossoverMonth: Int
)

@Immutable
data class PrepaymentSimulationResult(
    val originalMonths: Int,
    val newMonths: Int,
    val monthsSaved: Int,
    val yearsSaved: Int,
    val remainingMonthsSaved: Int,
    val newTenureYears: Int,
    val newTenureMonths: Int,
    val originalTotalInterest: Double,
    val newTotalInterest: Double,
    val interestSaved: Double,
    val originalTotalPayment: Double,
    val newTotalPayment: Double,
    val paymentSaved: Double,
    val chartPoints: List<PrepaymentPoint>,
    val milestones: PrepaymentMilestones
)
