package com.example.emicalculatorapp

import com.example.emicalculatorapp.model.PrepaymentCalculator
import com.example.emicalculatorapp.model.PrepaymentParams
import com.example.emicalculatorapp.model.StepUpMode
import com.example.emicalculatorapp.viewmodel.EMIViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PrepaymentSimulatorTest {

    @Test
    fun testExtraMonthlyOnHomeLoan() {
        // Home loan: 45,00,000 @ 8.55% for 20 years with extra ₹5,000/mo
        val result = PrepaymentCalculator.calculatePrepaymentSimulation(
            principal = 4500000.0,
            annualRate = 8.55,
            tenure = 20,
            isYears = true,
            params = PrepaymentParams(extraMonthly = 5000.0)
        )

        assertEquals(240, result.originalMonths)
        assertEquals(183, result.newMonths)
        assertEquals(57, result.monthsSaved)
        assertEquals(4, result.yearsSaved)
        assertEquals(9, result.remainingMonthsSaved)

        // Exact math checks within ±1 rupee tolerance
        assertEquals(3547142.99, result.newTotalInterest, 1.0)
        assertEquals(1359553.38, result.interestSaved, 1.0)
    }

    @Test
    fun testLumpSumAtMonth12OnHomeLoan() {
        // Home loan: 45,00,000 @ 8.55% for 20 years with lump sum ₹5,00,000 at month 12
        val result = PrepaymentCalculator.calculatePrepaymentSimulation(
            principal = 4500000.0,
            annualRate = 8.55,
            tenure = 20,
            isYears = true,
            params = PrepaymentParams(
                lumpSumAmount = 500000.0,
                lumpSumMonth = 12,
                isLumpSumEnabled = true
            )
        )

        assertEquals(240, result.originalMonths)
        assertEquals(187, result.newMonths)
        assertEquals(53, result.monthsSaved)
        assertEquals(4, result.yearsSaved)
        assertEquals(5, result.remainingMonthsSaved)

        // Exact math checks within ±1 rupee tolerance
        assertEquals(3322410.67, result.newTotalInterest, 1.0)
        assertEquals(1584285.70, result.interestSaved, 1.0)
    }

    @Test
    fun testAnnualStepUpOnCarLoan() {
        // Car loan: 12,00,000 @ 9.20% for 5 years with 10% annual step-up
        val result = PrepaymentCalculator.calculatePrepaymentSimulation(
            principal = 1200000.0,
            annualRate = 9.20,
            tenure = 5,
            isYears = true,
            params = PrepaymentParams(
                stepUpPercent = 10.0,
                isStepUpEnabled = true,
                stepUpMode = StepUpMode.WHOLE_EMI
            )
        )

        assertEquals(60, result.originalMonths)
        assertEquals(50, result.newMonths)
        assertEquals(10, result.monthsSaved)

        // Exact math checks within ±1 rupee tolerance
        assertEquals(267041.89, result.newTotalInterest, 1.0)
        assertEquals(34558.14, result.interestSaved, 1.0)
    }

    @Test
    fun testEdgeCaseExtraLargerThanRemainingBalance() {
        // Extra payment ₹50,00,000 on a ₹10,00,000 loan
        val result = PrepaymentCalculator.calculatePrepaymentSimulation(
            principal = 1000000.0,
            annualRate = 10.0,
            tenure = 5,
            isYears = true,
            params = PrepaymentParams(extraMonthly = 5000000.0)
        )

        // Must pay off in month 1 and balance must not go negative
        assertEquals(1, result.newMonths)
        assertEquals(59, result.monthsSaved)
        assertTrue(result.newTotalPayment >= 1000000.0)
        assertTrue(result.chartPoints.last().newBalance == 0.0)
    }

    @Test
    fun testEdgeCaseLumpSumInMonth1() {
        val result = PrepaymentCalculator.calculatePrepaymentSimulation(
            principal = 1000000.0,
            annualRate = 10.0,
            tenure = 5,
            isYears = true,
            params = PrepaymentParams(
                lumpSumAmount = 200000.0,
                lumpSumMonth = 1,
                isLumpSumEnabled = true
            )
        )

        assertTrue(result.newMonths < 60)
        assertTrue(result.interestSaved > 0.0)
    }

    @Test
    fun testEdgeCaseLumpSumBeyondTenureIgnored() {
        // Lump sum at month 300 on a 240-month loan must be ignored
        val result = PrepaymentCalculator.calculatePrepaymentSimulation(
            principal = 4500000.0,
            annualRate = 8.55,
            tenure = 20,
            isYears = true,
            params = PrepaymentParams(
                lumpSumAmount = 500000.0,
                lumpSumMonth = 300,
                isLumpSumEnabled = true
            )
        )

        assertEquals(240, result.newMonths)
        assertEquals(0, result.monthsSaved)
        assertEquals(0.0, result.interestSaved, 0.01)
    }

    @Test
    fun testEdgeCaseStepUpAtZeroPercent() {
        val result = PrepaymentCalculator.calculatePrepaymentSimulation(
            principal = 4500000.0,
            annualRate = 8.55,
            tenure = 20,
            isYears = true,
            params = PrepaymentParams(
                stepUpPercent = 0.0,
                isStepUpEnabled = true
            )
        )

        assertEquals(240, result.newMonths)
        assertEquals(0, result.monthsSaved)
        assertEquals(0.0, result.interestSaved, 0.01)
    }

    @Test
    fun testZeroPrepaymentMatchesStandardScheduleExactly() {
        val result = PrepaymentCalculator.calculatePrepaymentSimulation(
            principal = 4500000.0,
            annualRate = 8.55,
            tenure = 20,
            isYears = true,
            params = PrepaymentParams()
        )

        assertEquals(240, result.newMonths)
        assertEquals(0, result.monthsSaved)
        assertEquals(0.0, result.interestSaved, 0.001)
        assertEquals(result.originalTotalInterest, result.newTotalInterest, 0.001)
        assertEquals(result.originalTotalPayment, result.newTotalPayment, 0.001)
    }

    @Test
    fun testHomeLoanExtra2000With10PercentStepUpOnExtra() {
        // Home Rs 45,00,000 at 8.55% for 20 years, extra Rs 2,000/month with 10% step-up on the extra:
        // 193 months, total interest 3,928,819.13, interest saved 977,877.24.
        val result = PrepaymentCalculator.calculatePrepaymentSimulation(
            principal = 4500000.0,
            annualRate = 8.55,
            tenure = 20,
            isYears = true,
            params = PrepaymentParams(
                extraMonthly = 2000.0,
                stepUpPercent = 10.0,
                isStepUpEnabled = true,
                stepUpMode = StepUpMode.EXTRA_PAYMENT
            )
        )

        assertEquals(193, result.newMonths)
        assertEquals(3928819.13, result.newTotalInterest, 1.0)
        assertEquals(977877.24, result.interestSaved, 1.0)
    }

    @Test
    fun testCarLoanExtra2000Flat() {
        // Car Rs 12,00,000 at 9.20% for 5 years, extra Rs 2,000/month flat:
        // 55 months, total interest 272,585.57, interest saved 29,014.47.
        val result = PrepaymentCalculator.calculatePrepaymentSimulation(
            principal = 1200000.0,
            annualRate = 9.20,
            tenure = 5,
            isYears = true,
            params = PrepaymentParams(
                extraMonthly = 2000.0
            )
        )

        assertEquals(55, result.newMonths)
        assertEquals(272585.57, result.newTotalInterest, 1.0)
        assertEquals(29014.47, result.interestSaved, 1.0)
    }

    @Test
    fun testCarLoanExtra2000With10PercentStepUpOnExtra() {
        // Car, extra Rs 2,000 with 10% step-up on the extra:
        // 54 months, total interest 269,903.92.
        val result = PrepaymentCalculator.calculatePrepaymentSimulation(
            principal = 1200000.0,
            annualRate = 9.20,
            tenure = 5,
            isYears = true,
            params = PrepaymentParams(
                extraMonthly = 2000.0,
                stepUpPercent = 10.0,
                isStepUpEnabled = true,
                stepUpMode = StepUpMode.EXTRA_PAYMENT
            )
        )

        assertEquals(54, result.newMonths)
        assertEquals(269903.92, result.newTotalInterest, 1.0)
    }

    @Test
    fun testCarLoanWholeEmiStepUp10Percent() {
        // Car, no extra, whole-EMI step-up of 10%:
        // 50 months, total interest 267,041.89.
        val result = PrepaymentCalculator.calculatePrepaymentSimulation(
            principal = 1200000.0,
            annualRate = 9.20,
            tenure = 5,
            isYears = true,
            params = PrepaymentParams(
                extraMonthly = 0.0,
                stepUpPercent = 10.0,
                isStepUpEnabled = true,
                stepUpMode = StepUpMode.WHOLE_EMI
            )
        )

        assertEquals(50, result.newMonths)
        assertEquals(267041.89, result.newTotalInterest, 1.0)
    }

    @Test
    fun testTenureChangeClampsLumpSumMonth() {
        val clamped1 = EMIViewModel.clampLumpSumMonth(currentLumpSumMonth = 150, tenureValue = 5, isYearMode = true)
        assertEquals(60, clamped1)

        val clamped2 = EMIViewModel.clampLumpSumMonth(currentLumpSumMonth = 45, tenureValue = 5, isYearMode = true)
        assertEquals(45, clamped2)

        val clampedMonthMode = EMIViewModel.clampLumpSumMonth(currentLumpSumMonth = 45, tenureValue = 36, isYearMode = false)
        assertEquals(36, clampedMonthMode)
    }
}
