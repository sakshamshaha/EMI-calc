package com.example.emicalculatorapp

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.example.emicalculatorapp.data.settings.NumberFormatStyle
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

        val international = EMIViewModel.formatInternationalCurrency(4500000.0)
        assertEquals("₹ 4,500,000", international)

        val formatIndian = EMIViewModel.formatCurrency(4500000.0, com.example.emicalculatorapp.data.settings.NumberFormatStyle.INDIAN)
        assertEquals("₹ 45,00,000", formatIndian)

        val formatIntl = EMIViewModel.formatCurrency(4500000.0, com.example.emicalculatorapp.data.settings.NumberFormatStyle.INTERNATIONAL)
        assertEquals("₹ 4,500,000", formatIntl)
    }

    @Test
    fun testStableCursorTypingBothFormats() {
        // Typing into Indian format
        val typeIndian1 = EMIViewModel.formatAmountWithStableCursor(
            input = TextFieldValue("4", TextRange(1)),
            formatStyle = NumberFormatStyle.INDIAN
        )
        assertEquals("4", typeIndian1.text)
        assertEquals(1, typeIndian1.selection.start)

        val typeIndian2 = EMIViewModel.formatAmountWithStableCursor(
            input = TextFieldValue("45000", TextRange(5)),
            formatStyle = NumberFormatStyle.INDIAN
        )
        assertEquals("45,000", typeIndian2.text)
        assertEquals(6, typeIndian2.selection.start)

        // Typing into International format
        val typeIntl = EMIViewModel.formatAmountWithStableCursor(
            input = TextFieldValue("1234567", TextRange(7)),
            formatStyle = NumberFormatStyle.INTERNATIONAL
        )
        assertEquals("1,234,567", typeIntl.text)
        assertEquals(9, typeIntl.selection.start)
    }

    @Test
    fun testStableCursorMiddleDeleteBothFormats() {
        // Indian: "4,95,00,000" with cursor after '9' (index 3). Backspace deletes '9'.
        // Raw text: "4,5,00,000" with cursor at index 2 (digits before cursor = 1 ('4'))
        val deleteIndian = EMIViewModel.formatAmountWithStableCursor(
            input = TextFieldValue("4,5,00,000", TextRange(2)),
            formatStyle = NumberFormatStyle.INDIAN
        )
        assertEquals("45,00,000", deleteIndian.text)
        // Cursor remains right after '4' (index 1)
        assertEquals(1, deleteIndian.selection.start)

        // International: "45,900,000" with cursor after '9' (index 4). Backspace deletes '9'.
        // Raw text: "45,00,000" with cursor at index 3 (digits before cursor = 2 ('4', '5'))
        val deleteIntl = EMIViewModel.formatAmountWithStableCursor(
            input = TextFieldValue("45,00,000", TextRange(3)),
            formatStyle = NumberFormatStyle.INTERNATIONAL
        )
        assertEquals("4,500,000", deleteIntl.text)
        // In "4,500,000", after 2 digits ('4' and '5') is index 3
        assertEquals(3, deleteIntl.selection.start)
    }

    @Test
    fun testStableCursorPasteBothFormats() {
        // Pasting "4500000" into Indian format
        val pasteIndian = EMIViewModel.formatAmountWithStableCursor(
            input = TextFieldValue("4500000", TextRange(7)),
            formatStyle = NumberFormatStyle.INDIAN
        )
        assertEquals("45,00,000", pasteIndian.text)
        assertEquals(9, pasteIndian.selection.start)

        // Pasting "4500000" into International format
        val pasteIntl = EMIViewModel.formatAmountWithStableCursor(
            input = TextFieldValue("4500000", TextRange(7)),
            formatStyle = NumberFormatStyle.INTERNATIONAL
        )
        assertEquals("4,500,000", pasteIntl.text)
        assertEquals(9, pasteIntl.selection.start)
    }

    @Test
    fun testFullAmortizationScheduleProperties() {
        // Home preset: 45,00,000 @ 8.55% for 20 years (240 months)
        val schedule = EMIViewModel.calculateFullSchedule(
            principal = 4500000.0,
            annualRate = 8.55,
            tenure = 20,
            isYears = true
        )

        assertEquals(240, schedule.totalMonths)
        assertEquals(240, schedule.allMonths.size)
        assertEquals(20, schedule.years.size)

        // 1. Final month closing balance is ~0.0
        val finalMonth = schedule.allMonths.last()
        assertEquals(0.0, finalMonth.closingBalance, 0.01)

        // 2. Final year closing balance is ~0.0
        val finalYear = schedule.years.last()
        assertEquals(0.0, finalYear.closingBalance, 0.01)

        // 3. Sum of principal payments equals original loan amount (45,00,000)
        val sumPrincipal = schedule.allMonths.sumOf { it.principalPaid }
        assertEquals(4500000.0, sumPrincipal, 0.01)

        // 4. Sum of interest payments equals total interest
        val sumInterest = schedule.allMonths.sumOf { it.interestPaid }
        assertEquals(schedule.totalInterest, sumInterest, 0.05)

        // 5. Total payment equals sum of monthly actual EMIs
        val sumPayment = schedule.allMonths.sumOf { it.emi }
        assertEquals(schedule.totalPayment, sumPayment, 0.05)

        // 6. Year 1 matches independent manual calculation:
        // Month 1 opening = 4500000, r = 8.55/12/100 = 0.007125
        // Month 1 interest = 32062.50, EMI = 39194.57 -> principal = 7132.07
        // Sum of 12 months geometric principal growth = P_1 * ((1+r)^12 - 1) / r = 89,019.62
        val year1 = schedule.years[0]
        assertEquals(12, year1.months.size)
        assertEquals(4500000.0, year1.openingBalance, 0.01)
        assertEquals(89019.62, year1.totalPrincipalPaid, 1.0)
        assertEquals(381315.22, year1.totalInterestPaid, 1.0)
        assertEquals(4500000.0 - year1.totalPrincipalPaid, year1.closingBalance, 0.01)
    }
}
