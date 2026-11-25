package com.example.emicalculatorapp

import com.example.emicalculatorapp.model.AmortizationScheduleCalculator
import com.example.emicalculatorapp.model.ComparisonWinner
import com.example.emicalculatorapp.model.LoanComparisonCalculator
import com.example.emicalculatorapp.model.LoanInputState
import com.example.emicalculatorapp.model.PrepaymentCalculator
import com.example.emicalculatorapp.model.PrepaymentParams
import com.example.emicalculatorapp.ui.navigation.NavDestination
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FinancialEdgeCaseRegressionTest {

    @Test
    fun testNavDestinationItemsAreNotNullAndInitialized() {
        val items = NavDestination.items
        assertEquals(5, items.size)
        items.forEach { dest ->
            assertNotNull("NavDestination item should never be null", dest)
            assertNotNull("NavDestination route should never be null", dest.route)
            assertTrue("NavDestination route should not be empty", dest.route.isNotEmpty())
        }
    }

    @Test
    fun testLoanComparisonZeroInterestRateCalculatesValidEMI() {
        // Loan with 0% interest rate: EMI should equal Principal / Total Months
        val loanA = LoanInputState(amount = 120000.0, rate = 0.0, tenureValue = 12, isYearMode = false)
        val summaryA = LoanComparisonCalculator.computeLoanSummary(loanA)

        assertEquals(10000.0, summaryA.monthlyEMI, 0.01)
        assertEquals(0.0, summaryA.totalInterest, 0.01)
        assertEquals(120000.0, summaryA.totalPayment, 0.01)
        assertEquals(120000.0, summaryA.totalCost, 0.01)
    }

    @Test
    fun testLoanComparisonZeroInterestWithProcessingFee() {
        val loanA = LoanInputState(amount = 100000.0, rate = 0.0, tenureValue = 10, isYearMode = false, processingFeePercent = 1.0)
        val loanB = LoanInputState(amount = 100000.0, rate = 0.0, tenureValue = 10, isYearMode = false, processingFeePercent = 2.0)

        val result = LoanComparisonCalculator.compareLoans(loanA, loanB)

        assertEquals(10000.0, result.loanA.monthlyEMI, 0.01)
        assertEquals(10000.0, result.loanB.monthlyEMI, 0.01)
        assertEquals(1000.0, result.loanA.processingFeeAmount, 0.01)
        assertEquals(2000.0, result.loanB.processingFeeAmount, 0.01)
        assertEquals(101000.0, result.loanA.totalCost, 0.01)
        assertEquals(102000.0, result.loanB.totalCost, 0.01)
        assertEquals(ComparisonWinner.LOAN_A, result.overallWinner)
        assertEquals(ComparisonWinner.TIE, result.lowerEmiWinner)
    }

    @Test
    fun testLoanComparisonInvalidInputsGuarded() {
        val invalidLoan = LoanInputState(amount = -50000.0, rate = -5.0, tenureValue = -2, isYearMode = true)
        val summary = LoanComparisonCalculator.computeLoanSummary(invalidLoan)

        assertEquals(0.0, summary.monthlyEMI, 0.001)
        assertEquals(0.0, summary.totalCost, 0.001)
    }

    @Test
    fun testAmortizationMilestonesIncludeFinalPartialYear15Months() {
        // 15 months loan: should have 2 milestone entries (Year 1 and Year 2 payoff completion)
        val principal = 150000.0
        val monthlyRate = (12.0 / 12.0) / 100.0 // 1% per month
        val factor = Math.pow(1.0 + monthlyRate, 15.0)
        val emi = (principal * monthlyRate * factor) / (factor - 1.0)

        val milestones = AmortizationScheduleCalculator.generateAmortizationMilestones(
            principal = principal,
            monthlyRate = monthlyRate,
            monthlyEMI = emi,
            totalMonths = 15
        )

        assertEquals(2, milestones.size)
        val year1 = milestones[0]
        val year2 = milestones[1]

        assertEquals(1, year1.year)
        assertEquals(2, year2.year)
        assertTrue(year2.label.contains("Payoff Completion"))
        assertEquals(0.0, year2.remainingBalance, 0.001)
        assertEquals(0f, year2.remainingPercentOfPrincipal, 0.001f)
    }

    @Test
    fun testAmortizationScheduleFullPayoffAtEnd() {
        val schedule = AmortizationScheduleCalculator.calculateFullSchedule(
            principal = 300000.0,
            annualRate = 9.0,
            tenure = 15,
            isYears = false
        )

        assertEquals(15, schedule.totalMonths)
        assertEquals(2, schedule.years.size)
        assertEquals(0.0, schedule.allMonths.last().closingBalance, 0.001)
        assertEquals(0.0, schedule.years.last().closingBalance, 0.001)
        assertEquals(300000.0, schedule.totalPayment - schedule.totalInterest, 0.01)
    }

    @Test
    fun testPrepaymentSimulationInvalidInputsGuarded() {
        val result = PrepaymentCalculator.calculatePrepaymentSimulation(
            principal = -10000.0,
            annualRate = -5.0,
            tenure = 0,
            isYears = true,
            params = PrepaymentParams()
        )

        assertEquals(0.0, result.interestSaved, 0.001)
        assertEquals(0, result.monthsSaved)
    }
}
