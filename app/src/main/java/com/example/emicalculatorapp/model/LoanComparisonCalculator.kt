package com.example.emicalculatorapp.model

import kotlin.math.abs
import kotlin.math.pow

object LoanComparisonCalculator {

    fun computeLoanSummary(input: LoanInputState): LoanSummary {
        val totalMonths = if (input.isYearMode) input.tenureValue * 12 else input.tenureValue
        if (input.amount <= 0.0 || input.rate <= 0.0 || totalMonths <= 0) {
            return LoanSummary(
                amount = input.amount,
                rate = input.rate,
                tenureValue = input.tenureValue,
                isYearMode = input.isYearMode,
                totalMonths = totalMonths.coerceAtLeast(1),
                monthlyEMI = 0.0,
                totalInterest = 0.0,
                totalPayment = 0.0,
                processingFeePercent = input.processingFeePercent,
                processingFeeAmount = 0.0,
                totalCost = 0.0,
                principalSharePercent = 100f,
                interestSharePercent = 0f,
                feeSharePercent = 0f
            )
        }

        val monthlyRate = (input.rate / 12.0) / 100.0
        val factor = (1.0 + monthlyRate).pow(totalMonths.toDouble())
        val monthlyEMI = input.amount * monthlyRate * factor / (factor - 1.0)
        val totalPayment = monthlyEMI * totalMonths
        val totalInterest = totalPayment - input.amount
        val processingFeeAmount = input.amount * (input.processingFeePercent / 100.0)
        val totalCost = totalPayment + processingFeeAmount

        val grandTotal = totalCost.coerceAtLeast(1.0)
        val principalShare = ((input.amount / grandTotal) * 100f).toFloat().coerceIn(0f, 100f)
        val interestShare = ((totalInterest / grandTotal) * 100f).toFloat().coerceIn(0f, 100f)
        val feeShare = ((processingFeeAmount / grandTotal) * 100f).toFloat().coerceIn(0f, 100f)

        return LoanSummary(
            amount = input.amount,
            rate = input.rate,
            tenureValue = input.tenureValue,
            isYearMode = input.isYearMode,
            totalMonths = totalMonths,
            monthlyEMI = monthlyEMI,
            totalInterest = totalInterest,
            totalPayment = totalPayment,
            processingFeePercent = input.processingFeePercent,
            processingFeeAmount = processingFeeAmount,
            totalCost = totalCost,
            principalSharePercent = principalShare,
            interestSharePercent = interestShare,
            feeSharePercent = feeShare
        )
    }

    fun compareLoans(loanAInput: LoanInputState, loanBInput: LoanInputState): LoanComparisonResult {
        val summaryA = computeLoanSummary(loanAInput)
        val summaryB = computeLoanSummary(loanBInput)

        val emiDiff = summaryA.monthlyEMI - summaryB.monthlyEMI
        val totalCostDiff = summaryA.totalCost - summaryB.totalCost
        val interestDiff = summaryA.totalInterest - summaryB.totalInterest

        // Tolerance of 0.01 for floating-point tie check
        val overallWinner = when {
            abs(totalCostDiff) < 0.01 -> ComparisonWinner.TIE
            totalCostDiff > 0 -> ComparisonWinner.LOAN_B
            else -> ComparisonWinner.LOAN_A
        }

        val lowerEmiWinner = when {
            abs(emiDiff) < 0.01 -> ComparisonWinner.TIE
            emiDiff > 0 -> ComparisonWinner.LOAN_B
            else -> ComparisonWinner.LOAN_A
        }

        return LoanComparisonResult(
            loanA = summaryA,
            loanB = summaryB,
            emiDiff = emiDiff,
            totalCostDiff = totalCostDiff,
            interestDiff = interestDiff,
            overallWinner = overallWinner,
            lowerEmiWinner = lowerEmiWinner,
            overallSavings = abs(totalCostDiff),
            monthlySavings = abs(emiDiff)
        )
    }
}
