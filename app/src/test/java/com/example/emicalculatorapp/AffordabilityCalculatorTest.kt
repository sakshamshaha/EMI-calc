package com.example.emicalculatorapp

import com.example.emicalculatorapp.model.AffordabilityCalculator
import com.example.emicalculatorapp.model.AffordabilityZone
import com.example.emicalculatorapp.model.EMICalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AffordabilityCalculatorTest {

    @Test
    fun testReferenceCase1_Income100k_Existing10k_Ratio40() {
        // Income 1,00,000; existing EMIs 10,00, ratio 40%; rate 8.55%; 20 years
        // -> max EMI 30,000; max loan 3,444,354.82.
        // Same case, live loan Rs 45,00,000 (EMI 39,194.57):
        // total obligation ratio 49.19%, zone Stretch; income needed at 40% = 122,986.42.
        val result = AffordabilityCalculator.calculateAffordability(
            income = 100000.0,
            existingEmis = 10000.0,
            maxRatioPercent = 40.0,
            rate = 8.55,
            tenureYears = 20,
            liveLoanAmount = 4500000.0,
            liveLoanEmi = 39194.57
        )

        assertEquals(30000.0, result.maxAffordableEmi, 0.01)
        assertEquals(3444354.82, result.maxLoanAmount, 1.0)
        assertEquals(49.19, result.totalObligationRatio, 0.05)
        assertEquals(AffordabilityZone.STRETCH, result.zone)
        assertEquals(122986.42, result.incomeNeededForLiveLoan, 1.0)
    }

    @Test
    fun testReferenceCase2_Income200k_Existing0_Ratio40() {
        // Income 2,00,000; existing 0; ratio 40%; 8.55%; 20 years
        // -> max EMI 80,000; max loan 9,184,946.19; live loan ratio 19.60%, zone Comfortable.
        val result = AffordabilityCalculator.calculateAffordability(
            income = 200000.0,
            existingEmis = 0.0,
            maxRatioPercent = 40.0,
            rate = 8.55,
            tenureYears = 20,
            liveLoanAmount = 4500000.0,
            liveLoanEmi = 39194.57
        )

        assertEquals(80000.0, result.maxAffordableEmi, 0.01)
        assertEquals(9184946.19, result.maxLoanAmount, 1.0)
        assertEquals(19.60, result.totalObligationRatio, 0.05)
        assertEquals(AffordabilityZone.COMFORTABLE, result.zone)
    }

    @Test
    fun testReferenceCase3_Income150k_Existing20k_Ratio35() {
        // Income 1,50,000; existing 20,000; ratio 35%
        // -> max EMI 32,500; max loan 3,731,384.39; ratio with the live loan 39.46%, zone Moderate.
        val result = AffordabilityCalculator.calculateAffordability(
            income = 150000.0,
            existingEmis = 20000.0,
            maxRatioPercent = 35.0,
            rate = 8.55,
            tenureYears = 20,
            liveLoanAmount = 4500000.0,
            liveLoanEmi = 39194.57
        )

        assertEquals(32500.0, result.maxAffordableEmi, 0.01)
        assertEquals(3731384.39, result.maxLoanAmount, 1.0)
        assertEquals(39.46, result.totalObligationRatio, 0.05)
        assertEquals(AffordabilityZone.MODERATE, result.zone)
    }

    @Test
    fun testReferenceCase4_ZeroInterestRate() {
        // Rate 0%, max EMI 25,000 over 10 years -> max loan 3,000,000.
        val result = AffordabilityCalculator.calculateAffordability(
            income = 100000.0,
            existingEmis = 0.0,
            maxRatioPercent = 25.0,
            rate = 0.0,
            tenureYears = 10,
            liveLoanAmount = 1000000.0,
            liveLoanEmi = 10000.0
        )

        assertEquals(25000.0, result.maxAffordableEmi, 0.01)
        assertEquals(3000000.0, result.maxLoanAmount, 1.0)
    }

    @Test
    fun testRoundTrip_MaxLoanYieldsMaxEmi() {
        // Round trip: the EMI computed from the max loan equals the max EMI (within 1 rupee).
        val result = AffordabilityCalculator.calculateAffordability(
            income = 100000.0,
            existingEmis = 10000.0,
            maxRatioPercent = 40.0,
            rate = 8.55,
            tenureYears = 20,
            liveLoanAmount = 4500000.0,
            liveLoanEmi = 39194.57
        )

        val emiCalc = EMICalculator.calculateEMI(
            principal = result.maxLoanAmount,
            annualRate = 8.55,
            tenure = 20,
            isYears = true
        )

        assertEquals(result.maxAffordableEmi, emiCalc.monthlyEMI, 1.0)
    }

    @Test
    fun testEdgeCases_IncomeZero_ExistingExceedingCap_ZoneBoundaries() {
        // 1. Income = 0
        val zeroIncome = AffordabilityCalculator.calculateAffordability(
            income = 0.0,
            existingEmis = 5000.0,
            maxRatioPercent = 40.0,
            rate = 8.55,
            tenureYears = 20,
            liveLoanAmount = 1000000.0,
            liveLoanEmi = 10000.0
        )
        assertTrue(zeroIncome.isIncomeZero)
        assertEquals(0.0, zeroIncome.maxAffordableEmi, 0.01)
        assertEquals(0.0, zeroIncome.maxLoanAmount, 0.01)

        // 2. Existing EMIs >= cap
        val existingExceeds = AffordabilityCalculator.calculateAffordability(
            income = 100000.0,
            existingEmis = 45000.0,
            maxRatioPercent = 40.0, // Cap = 40,000
            rate = 8.55,
            tenureYears = 20,
            liveLoanAmount = 1000000.0,
            liveLoanEmi = 10000.0
        )
        assertTrue(existingExceeds.isExistingExceedingCap)
        assertEquals(0.0, existingExceeds.maxAffordableEmi, 0.01)
        assertEquals(0.0, existingExceeds.maxLoanAmount, 0.01)

        // 3. Zone boundaries: exactly 30% and exactly 40%
        // Obligation = 30k on 100k income -> exactly 30%
        val exactly30 = AffordabilityCalculator.calculateAffordability(
            income = 100000.0,
            existingEmis = 0.0,
            maxRatioPercent = 50.0,
            rate = 8.55,
            tenureYears = 20,
            liveLoanAmount = 1000000.0,
            liveLoanEmi = 30000.0
        )
        assertEquals(30.0, exactly30.totalObligationRatio, 0.01)
        assertEquals(AffordabilityZone.COMFORTABLE, exactly30.zone)

        // Obligation = 40k on 100k income -> exactly 40%
        val exactly40 = AffordabilityCalculator.calculateAffordability(
            income = 100000.0,
            existingEmis = 0.0,
            maxRatioPercent = 50.0,
            rate = 8.55,
            tenureYears = 20,
            liveLoanAmount = 1000000.0,
            liveLoanEmi = 40000.0
        )
        assertEquals(40.0, exactly40.totalObligationRatio, 0.01)
        assertEquals(AffordabilityZone.MODERATE, exactly40.zone)

        // Obligation = 40.01k on 100k income -> Stretch
        val justAbove40 = AffordabilityCalculator.calculateAffordability(
            income = 100000.0,
            existingEmis = 0.0,
            maxRatioPercent = 50.0,
            rate = 8.55,
            tenureYears = 20,
            liveLoanAmount = 1000000.0,
            liveLoanEmi = 40010.0
        )
        assertEquals(AffordabilityZone.STRETCH, justAbove40.zone)
    }
}
