package com.example.emicalculatorapp

import com.example.emicalculatorapp.model.EMICalculator
import com.example.emicalculatorapp.model.InsightType
import com.example.emicalculatorapp.model.InsightsGenerator
import com.example.emicalculatorapp.model.PrepaymentCalculator
import com.example.emicalculatorapp.model.PrepaymentParams
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class InsightsTest {

    private val toleranceRupee = 1.0
    private val tolerancePercent = 0.01
    private val toleranceGrowthFactor = 0.00001

    @Test
    fun testHomePresetMathBreakdown() {
        val principal = 4500000.0
        val annualRate = 8.55
        val tenureMonths = 240

        val breakdown = EMICalculator.calculateMathBreakdown(principal, annualRate, tenureMonths)

        assertFalse(breakdown.isZeroRate)
        assertEquals(0.007125, breakdown.monthlyRate, 0.000001)
        assertEquals(5.49554, breakdown.growthFactor, toleranceGrowthFactor)
        assertEquals(176200.76, breakdown.numerator, toleranceRupee)
        assertEquals(4.49554, breakdown.denominator, toleranceGrowthFactor)
        assertEquals(39194.57, breakdown.emi, toleranceRupee)
        assertEquals(32062.50, breakdown.month1Interest, toleranceRupee)
        assertEquals(7132.07, breakdown.month1Principal, toleranceRupee)
        assertEquals(9406696.80, breakdown.totalPayment, toleranceRupee)
        assertEquals(4906696.80, breakdown.totalInterest, toleranceRupee)
    }

    @Test
    fun testHomePresetInsights() {
        val principal = 4500000.0
        val annualRate = 8.55
        val tenureMonths = 240

        val insights = InsightsGenerator.generateInsights(principal, annualRate, tenureMonths)
        assertEquals(4, insights.size)

        // 1. Interest Share 52.16%
        val interestShare = insights.find { it.type == InsightType.INTEREST_SHARE }
        assertNotNull(interestShare)
        assertEquals(52.16, interestShare!!.headlineValue, tolerancePercent)
        assertTrue(interestShare.headlineDisplay.contains("52.16%"))

        // 2. Shorter Tenure (18 years): EMI +1,689.47, interest saved 575,743.51
        val shorterTenure = insights.find { it.type == InsightType.SHORTER_TENURE }
        assertNotNull(shorterTenure)
        assertEquals(1689.47, shorterTenure!!.headlineValue, toleranceRupee)
        assertTrue(shorterTenure.description.contains("18 yrs"))

        val tenure18Result = EMICalculator.calculateEMI(principal, annualRate, 216, isYears = false)
        assertEquals(40884.04, tenure18Result.monthlyEMI, toleranceRupee)
        val currentResult = EMICalculator.calculateEMI(principal, annualRate, 240, isYears = false)
        val interestSaved18 = currentResult.totalInterest - tenure18Result.totalInterest
        assertEquals(575743.51, interestSaved18, toleranceRupee)

        // 3. Rate +0.5% (9.05%): new EMI 40,632.49, EMI +1,437.92, total interest +345,100.45
        val rateHike = insights.find { it.type == InsightType.RATE_HIKE }
        assertNotNull(rateHike)
        assertEquals(1437.92, rateHike!!.headlineValue, toleranceRupee)

        val rate905Result = EMICalculator.calculateEMI(principal, 9.05, 240, isYears = false)
        assertEquals(40632.49, rate905Result.monthlyEMI, toleranceRupee)
        val interestIncrease = rate905Result.totalInterest - currentResult.totalInterest
        assertEquals(345100.45, interestIncrease, toleranceRupee)

        // 4. Extra Rs 4,000 (10% of EMI rounded to Rs 500): 191 months, 49 months saved, interest saved 1,157,534.27
        val extraPayment = insights.find { it.type == InsightType.EXTRA_PAYMENT }
        assertNotNull(extraPayment)
        assertEquals(49.0, extraPayment!!.headlineValue, 0.1)

        val prepayParams = PrepaymentParams(
            extraMonthly = 4000.0,
            isLumpSumEnabled = false,
            isStepUpEnabled = false
        )
        val prepayResult = PrepaymentCalculator.calculatePrepaymentSimulation(
            principal = principal,
            annualRate = annualRate,
            tenure = 240,
            isYears = false,
            params = prepayParams
        )
        assertEquals(191, prepayResult.newMonths)
        assertEquals(49, prepayResult.monthsSaved)
        assertEquals(1157534.27, prepayResult.interestSaved, toleranceRupee)
    }

    @Test
    fun testRateSensitivityStripValues() {
        val principal = 4500000.0
        val annualRate = 8.55
        val tenureMonths = 240

        val sensitivity = InsightsGenerator.calculateRateSensitivity(principal, annualRate, tenureMonths)
        assertEquals(4, sensitivity.size)

        // 7.55% 36,389.40
        assertEquals(7.55, sensitivity[0].rate, 0.001)
        assertEquals(36389.40, sensitivity[0].monthlyEMI, toleranceRupee)
        assertFalse(sensitivity[0].isCurrent)

        // 8.55% 39,194.57 (current)
        assertEquals(8.55, sensitivity[1].rate, 0.001)
        assertEquals(39194.57, sensitivity[1].monthlyEMI, toleranceRupee)
        assertTrue(sensitivity[1].isCurrent)

        // 9.55% 42,092.94
        assertEquals(9.55, sensitivity[2].rate, 0.001)
        assertEquals(42092.94, sensitivity[2].monthlyEMI, toleranceRupee)
        assertFalse(sensitivity[2].isCurrent)

        // 10.55% 45,078.33
        assertEquals(10.55, sensitivity[3].rate, 0.001)
        assertEquals(45078.33, sensitivity[3].monthlyEMI, toleranceRupee)
        assertFalse(sensitivity[3].isCurrent)
    }

    @Test
    fun testHidingRules() {
        // Rule 1: Tenure of 1 year (12 months) hides the shorter-tenure insight
        val oneYearInsights = InsightsGenerator.generateInsights(4500000.0, 8.55, 12)
        val shorterTenure = oneYearInsights.find { it.type == InsightType.SHORTER_TENURE }
        assertNull(shorterTenure)

        // Rule 2: Rate 0 uses the simple math variant: EMI = P / n
        val zeroRateBreakdown = EMICalculator.calculateMathBreakdown(120000.0, 0.0, 12)
        assertTrue(zeroRateBreakdown.isZeroRate)
        assertEquals(0.0, zeroRateBreakdown.monthlyRate, 0.000001)
        assertEquals(10000.0, zeroRateBreakdown.emi, toleranceRupee)
        assertEquals(0.0, zeroRateBreakdown.month1Interest, toleranceRupee)
        assertEquals(10000.0, zeroRateBreakdown.month1Principal, toleranceRupee)
        assertEquals(120000.0, zeroRateBreakdown.totalPayment, toleranceRupee)
        assertEquals(0.0, zeroRateBreakdown.totalInterest, toleranceRupee)

        // Rule 3: Sensitivity skips rates below 1%
        val lowRateSensitivity = InsightsGenerator.calculateRateSensitivity(100000.0, 1.5, 12)
        // Rate is 1.5%: offset -1.0% gives 0.5% which is < 1.0%, so -1% is skipped
        val hasSubOnePercent = lowRateSensitivity.any { it.rate < 1.0 }
        assertFalse(hasSubOnePercent)
        assertEquals(3, lowRateSensitivity.size) // current(1.5), +1(2.5), +2(3.5)
    }
}
