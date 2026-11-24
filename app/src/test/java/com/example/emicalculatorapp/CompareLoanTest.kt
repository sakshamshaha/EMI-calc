package com.example.emicalculatorapp

import com.example.emicalculatorapp.model.ComparisonWinner
import com.example.emicalculatorapp.model.LoanComparisonCalculator
import com.example.emicalculatorapp.model.LoanInputState
import org.junit.Assert.assertEquals
import org.junit.Test

class CompareLoanTest {

    @Test
    fun testLoanA_vs_LoanB_LowerInterestRate() {
        // A (45L @ 8.55% 20y) vs B (45L @ 8.00% 20y):
        // EMI diff: 1,554.77, interest diff: 373,143.62, B wins
        val loanA = LoanInputState(amount = 4500000.0, rate = 8.55, tenureValue = 20, isYearMode = true)
        val loanB = LoanInputState(amount = 4500000.0, rate = 8.00, tenureValue = 20, isYearMode = true)

        val result = LoanComparisonCalculator.compareLoans(loanA, loanB)

        assertEquals(39194.57, result.loanA.monthlyEMI, 0.05)
        assertEquals(37639.80, result.loanB.monthlyEMI, 0.05)
        assertEquals(1554.77, result.emiDiff, 0.05)
        assertEquals(373143.62, result.interestDiff, 1.0)
        assertEquals(373143.62, result.totalCostDiff, 1.0)
        assertEquals(ComparisonWinner.LOAN_B, result.overallWinner)
        assertEquals(ComparisonWinner.LOAN_B, result.lowerEmiWinner)
    }

    @Test
    fun testLoanA_vs_LoanC_ShorterTenure() {
        // A (45L @ 8.55% 20y) vs C (45L @ 8.55% 15y):
        // C EMI higher by Rs 5,250.70, but total cost/interest lower by Rs 1,406,548.16.
        // C wins total cost, A wins lower EMI.
        val loanA = LoanInputState(amount = 4500000.0, rate = 8.55, tenureValue = 20, isYearMode = true)
        val loanC = LoanInputState(amount = 4500000.0, rate = 8.55, tenureValue = 15, isYearMode = true)

        val result = LoanComparisonCalculator.compareLoans(loanA, loanC)

        // C has higher EMI -> emiDiff = A.emi - C.emi = -5,250.70
        assertEquals(-5250.70, result.emiDiff, 0.05)
        assertEquals(5250.70, result.monthlySavings, 0.05)
        assertEquals(ComparisonWinner.LOAN_A, result.lowerEmiWinner)

        // C has much lower total cost and interest
        assertEquals(1406548.16, result.totalCostDiff, 1.0)
        assertEquals(1406548.16, result.interestDiff, 1.0)
        assertEquals(ComparisonWinner.LOAN_B, result.overallWinner) // B is Loan C
    }

    @Test
    fun testLoanA_vs_LoanD_ProcessingFeeModeling() {
        // A (45L @ 8.55% 20y) vs D (45L @ 8.30% 20y + 1% fee):
        // Total cost: 9,281,232.10; saves Rs 125,464.27 overall vs A.
        val loanA = LoanInputState(amount = 4500000.0, rate = 8.55, tenureValue = 20, isYearMode = true, processingFeePercent = 0.0)
        val loanD = LoanInputState(amount = 4500000.0, rate = 8.30, tenureValue = 20, isYearMode = true, processingFeePercent = 1.0)

        val result = LoanComparisonCalculator.compareLoans(loanA, loanD)

        assertEquals(45000.0, result.loanB.processingFeeAmount, 0.01)
        assertEquals(9281232.10, result.loanB.totalCost, 1.0)
        assertEquals(125464.27, result.totalCostDiff, 1.0)
        assertEquals(125464.27, result.overallSavings, 1.0)
        assertEquals(ComparisonWinner.LOAN_B, result.overallWinner)
    }

    @Test
    fun testTieZeroDifferenceProducesNoWinner() {
        val loan1 = LoanInputState(amount = 3000000.0, rate = 9.0, tenureValue = 10, isYearMode = true, processingFeePercent = 0.5)
        val loan2 = LoanInputState(amount = 3000000.0, rate = 9.0, tenureValue = 10, isYearMode = true, processingFeePercent = 0.5)

        val result = LoanComparisonCalculator.compareLoans(loan1, loan2)

        assertEquals(0.0, result.emiDiff, 0.001)
        assertEquals(0.0, result.totalCostDiff, 0.001)
        assertEquals(ComparisonWinner.TIE, result.overallWinner)
        assertEquals(ComparisonWinner.TIE, result.lowerEmiWinner)
    }
}
