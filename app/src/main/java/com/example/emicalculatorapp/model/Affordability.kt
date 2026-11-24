package com.example.emicalculatorapp.model

import androidx.compose.runtime.Immutable

enum class AffordabilityZone {
    COMFORTABLE, // <= 30%
    MODERATE,    // 30% to 40%
    STRETCH      // > 40%
}

@Immutable
data class AffordabilityParams(
    val monthlyIncome: Double = 100000.0,
    val existingEmis: Double = 10000.0,
    val maxDtiRatioPercent: Double = 40.0, // 20% to 60%
    val interestRate: Double = 8.55,
    val tenureYears: Int = 20
)

@Immutable
data class AffordabilityResult(
    val monthlyIncome: Double,
    val existingEmis: Double,
    val maxDtiRatioPercent: Double,
    val maxAffordableEmi: Double,
    val maxLoanAmount: Double,
    val liveLoanAmount: Double,
    val liveLoanEmi: Double,
    val totalObligationRatio: Double, // in percent e.g. 49.19%
    val incomeNeededForLiveLoan: Double,
    val additionalIncomeNeeded: Double,
    val loanReductionNeeded: Double,
    val zone: AffordabilityZone,
    val isIncomeZero: Boolean,
    val isExistingExceedingCap: Boolean,
    val summaryHeadline: String,
    val summaryDetail: String
)
