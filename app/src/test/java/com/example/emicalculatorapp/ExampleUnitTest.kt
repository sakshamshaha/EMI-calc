package com.example.emicalculatorapp

import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.pow

class ExampleUnitTest {

    private fun calculateMonthlyEMI(principal: Double, annualRate: Double, tenureYears: Int): Double {
        val totalMonths = tenureYears * 12
        return if (annualRate == 0.0) {
            principal / totalMonths
        } else {
            val monthlyRate = annualRate / 12.0 / 100.0
            val rateFactor = (1.0 + monthlyRate).pow(totalMonths.toDouble())
            (principal * monthlyRate * rateFactor) / (rateFactor - 1.0)
        }
    }

    private fun formatCurrency(amount: Double): String {
        val locale = Locale.forLanguageTag("en-IN")
        val formatter = NumberFormat.getCurrencyInstance(locale)
        formatter.maximumFractionDigits = 2
        formatter.minimumFractionDigits = 2
        val formatted = formatter.format(amount)
        return formatted.replace("₹", "₹ ").replace("₹  ", "₹ ")
    }

    @Test
    fun testStandardEMICalculation() {
        // Principal: 1,00,000, Annual Rate: 10%, Tenure: 1 year (12 months)
        val emi = calculateMonthlyEMI(100000.0, 10.0, 1)
        assertEquals(8791.59, emi, 0.01)

        val totalPayment = emi * 12
        assertEquals(105499.06, totalPayment, 0.02)

        val totalInterest = totalPayment - 100000.0
        assertEquals(5499.06, totalInterest, 0.02)
    }

    @Test
    fun testZeroInterestEMICalculation() {
        // Principal: 1,20,000, Annual Rate: 0%, Tenure: 1 year (12 months)
        val emi = calculateMonthlyEMI(120000.0, 0.0, 1)
        assertEquals(10000.0, emi, 0.001)

        val totalPayment = emi * 12
        assertEquals(120000.0, totalPayment, 0.001)

        val totalInterest = totalPayment - 120000.0
        assertEquals(0.0, totalInterest, 0.001)
    }

    @Test
    fun testFiveYearTenureEMICalculation() {
        // Principal: 5,00,000, Annual Rate: 8.5%, Tenure: 5 years (60 months)
        val emi = calculateMonthlyEMI(500000.0, 8.5, 5)
        assertEquals(10258.27, emi, 0.02)
    }

    @Test
    fun testCurrencyFormatting() {
        val formatted = formatCurrency(123456.78)
        println("FORMATTED OUTPUT IS: $formatted")
        // Check that it contains the decimal amount
        org.junit.Assert.assertTrue(formatted.contains("456.78"))
    }
}
