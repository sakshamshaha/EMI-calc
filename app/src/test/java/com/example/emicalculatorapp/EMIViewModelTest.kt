package com.example.emicalculatorapp

import com.example.emicalculatorapp.viewmodel.EMIViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EMIViewModelTest {

    @Test
    fun testHomeLoanPresetCalculation() {
        // Preset: 45,00,000 / 8.55% / 20 Yrs (240 months)
        val result = EMIViewModel.calculateEMI(
            principal = 4500000.0,
            annualRate = 8.55,
            tenure = 20,
            isYears = true
        )

        // Expected EMI: 39,194.57 (tolerance ±0.05)
        assertEquals(39194.57, result.monthlyEMI, 0.05)
        // Total Payment: 9,406,696.37 (tolerance ±1.0)
        assertEquals(9406696.37, result.totalPayment, 1.0)
        // Total Interest: 4,906,696.37 (tolerance ±1.0)
        assertEquals(4906696.37, result.totalInterest, 1.0)
        assertTrue(result.principalSharePercent > 47f && result.principalSharePercent < 49f)
        assertTrue(result.interestSharePercent > 51f && result.interestSharePercent < 53f)
        assertEquals(4, result.milestones.size) // Years 1, 5, 10, 20
    }

    @Test
    fun testCarLoanPresetCalculation() {
        // Preset: 12,00,000 / 9.20% / 5 Yrs (60 months)
        val result = EMIViewModel.calculateEMI(
            principal = 1200000.0,
            annualRate = 9.20,
            tenure = 5,
            isYears = true
        )

        // Expected EMI: 25,026.67 (tolerance ±0.05)
        assertEquals(25026.67, result.monthlyEMI, 0.05)
        // Total Payment: 1,501,600.04 (tolerance ±1.0)
        assertEquals(1501600.04, result.totalPayment, 1.0)
        // Total Interest: 301,600.04 (tolerance ±1.0)
        assertEquals(301600.04, result.totalInterest, 1.0)
        assertEquals(2, result.milestones.size) // Years 1 and 5 (Year 10 omitted since tenure is 5 yrs)
    }

    @Test
    fun testPersonalLoanPresetCalculation() {
        // Preset: 5,00,000 / 12.50% / 3 Yrs (36 months)
        val result = EMIViewModel.calculateEMI(
            principal = 500000.0,
            annualRate = 12.50,
            tenure = 3,
            isYears = true
        )

        // Expected EMI: 16,726.81 (tolerance ±0.05)
        assertEquals(16726.81, result.monthlyEMI, 0.05)
        // Total Payment: 602,165.26 (tolerance ±1.0)
        assertEquals(602165.26, result.totalPayment, 1.0)
        // Total Interest: 102,165.26 (tolerance ±1.0)
        assertEquals(102165.26, result.totalInterest, 1.0)
        assertEquals(2, result.milestones.size) // Years 1 and 3 (5 & 10 omitted)
    }

    @Test
    fun testEducationLoanPresetCalculation() {
        // Preset: 20,00,000 / 8.95% / 8 Yrs (96 months)
        val result = EMIViewModel.calculateEMI(
            principal = 2000000.0,
            annualRate = 8.95,
            tenure = 8,
            isYears = true
        )

        // Expected EMI: 29,248.56 (tolerance ±0.05)
        assertEquals(29248.56, result.monthlyEMI, 0.05)
        // Total Payment: 2,807,861.40 (tolerance ±1.0)
        assertEquals(2807861.40, result.totalPayment, 1.0)
        // Total Interest: 807,861.40 (tolerance ±1.0)
        assertEquals(807861.40, result.totalInterest, 1.0)
        assertEquals(3, result.milestones.size) // Years 1, 5, 8 (10 omitted)
    }

    @Test
    fun testZeroInterestCalculation() {
        // 1,20,000 / 0% / 1 Yr (12 months)
        val result = EMIViewModel.calculateEMI(
            principal = 120000.0,
            annualRate = 0.0,
            tenure = 1,
            isYears = true
        )

        assertEquals(10000.0, result.monthlyEMI, 0.001)
        assertEquals(120000.0, result.totalPayment, 0.001)
        assertEquals(0.0, result.totalInterest, 0.001)
    }

    @Test
    fun testCurrencyFormatting() {
        val formatted = EMIViewModel.formatIndianCurrency(4500000.0)
        assertEquals("₹ 45,00,000", formatted)

        val formattedEmi = EMIViewModel.formatIndianCurrency(39215.0)
        assertEquals("₹ 39,215", formattedEmi)

        val compact = EMIViewModel.formatCompactIndianLakh(4380000.0)
        assertEquals("₹43.8 L", compact)
    }
}
