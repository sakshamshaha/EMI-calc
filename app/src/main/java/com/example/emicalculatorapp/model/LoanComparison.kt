package com.example.emicalculatorapp.model

import androidx.compose.runtime.Immutable

enum class ComparisonWinner {
    LOAN_A,
    LOAN_B,
    TIE
}

@Immutable
data class LoanInputState(
    val amount: Double = 4500000.0,
    val rate: Double = 8.05,
    val tenureValue: Int = 20,
    val isYearMode: Boolean = true,
    val processingFeePercent: Double = 0.0
)

@Immutable
data class LoanSummary(
    val amount: Double,
    val rate: Double,
    val tenureValue: Int,
    val isYearMode: Boolean,
    val totalMonths: Int,
    val monthlyEMI: Double,
    val totalInterest: Double,
    val totalPayment: Double,
    val processingFeePercent: Double,
    val processingFeeAmount: Double,
    val totalCost: Double,
    val principalSharePercent: Float,
    val interestSharePercent: Float,
    val feeSharePercent: Float
)

@Immutable
data class LoanComparisonResult(
    val loanA: LoanSummary,
    val loanB: LoanSummary,
    val emiDiff: Double, // loanA.monthlyEMI - loanB.monthlyEMI (>0 means B is cheaper per month)
    val totalCostDiff: Double, // loanA.totalCost - loanB.totalCost (>0 means B saves overall)
    val interestDiff: Double, // loanA.totalInterest - loanB.totalInterest (>0 means B has lower interest)
    val overallWinner: ComparisonWinner, // based on lower totalCost
    val lowerEmiWinner: ComparisonWinner, // based on lower monthlyEMI
    val overallSavings: Double, // abs(totalCostDiff)
    val monthlySavings: Double // abs(emiDiff)
)
