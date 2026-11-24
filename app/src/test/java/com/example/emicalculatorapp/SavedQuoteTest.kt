package com.example.emicalculatorapp

import com.example.emicalculatorapp.data.database.QuoteEntity
import com.example.emicalculatorapp.data.settings.NumberFormatStyle
import com.example.emicalculatorapp.model.LoanCategory
import com.example.emicalculatorapp.model.QuoteCalculations
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SavedQuoteTest {

    @Test
    fun testDefaultNameGenerator() {
        val name = QuoteCalculations.generateDefaultQuoteName(
            category = LoanCategory.HOME,
            principal = 4500000.0,
            rate = 8.55,
            numberFormat = NumberFormatStyle.INDIAN
        )
        // Default format: "Home loan - ₹45L at 8.55%" (or formatted currency)
        assertTrue(name.startsWith("Home loan - "))
        assertTrue(name.contains("8.55%"))
        assertTrue(name.length <= 40)
    }

    @Test
    fun testNameValidation() {
        // 1. Valid names
        assertEquals("Home Loan", QuoteCalculations.validateQuoteName("Home Loan"))
        assertEquals("Car Loan 2026", QuoteCalculations.validateQuoteName("  Car Loan 2026  "))

        // 2. Blank or whitespace-only names are invalid
        assertNull(QuoteCalculations.validateQuoteName(""))
        assertNull(QuoteCalculations.validateQuoteName("   "))

        // 3. Names longer than 40 characters are invalid
        val longName = "A".repeat(41)
        assertNull(QuoteCalculations.validateQuoteName(longName))

        val exactly40Chars = "A".repeat(40)
        assertEquals(exactly40Chars, QuoteCalculations.validateQuoteName(exactly40Chars))
    }

    @Test
    fun testSelectExactlyTwoQuotesAndAssignment() {
        var selected = emptyList<Long>()

        // 1. Select first quote (ID: 101)
        selected = QuoteCalculations.selectQuoteForComparison(selected, 101L)
        assertEquals(listOf(101L), selected)

        // 2. Select second quote (ID: 202) -> Both selected
        selected = QuoteCalculations.selectQuoteForComparison(selected, 202L)
        assertEquals(listOf(101L, 202L), selected)

        // First selected is Loan A, second selected is Loan B
        val loanAId = selected[0]
        val loanBId = selected[1]
        assertEquals(101L, loanAId)
        assertEquals(202L, loanBId)

        // 3. Attempting to select a 3rd quote (ID: 303) is disallowed
        val attemptedThird = QuoteCalculations.selectQuoteForComparison(selected, 303L)
        assertEquals(2, attemptedThird.size)
        assertEquals(listOf(101L, 202L), attemptedThird)

        // 4. Deselecting an item works cleanly
        val afterDeselect = QuoteCalculations.selectQuoteForComparison(selected, 101L)
        assertEquals(listOf(202L), afterDeselect)
    }

    @Test
    fun testSortOrderPinnedFirstThenNewest() {
        val q1 = QuoteEntity(id = 1, name = "Older Normal", category = LoanCategory.PERSONAL, principal = 100000.0, annualRate = 12.0, tenureMonths = 12, isPinned = false, createdAt = 1000L)
        val q2 = QuoteEntity(id = 2, name = "Newer Normal", category = LoanCategory.CAR, principal = 500000.0, annualRate = 9.0, tenureMonths = 36, isPinned = false, createdAt = 2000L)
        val q3 = QuoteEntity(id = 3, name = "Older Pinned", category = LoanCategory.HOME, principal = 4500000.0, annualRate = 8.55, tenureMonths = 240, isPinned = true, createdAt = 1500L)
        val q4 = QuoteEntity(id = 4, name = "Newest Pinned", category = LoanCategory.HOME, principal = 5000000.0, annualRate = 8.40, tenureMonths = 240, isPinned = true, createdAt = 3000L)

        val unsorted = listOf(q1, q2, q3, q4)
        val sorted = QuoteCalculations.sortQuotes(unsorted)

        // Expected order:
        // 1. Newest Pinned (q4: isPinned=true, createdAt=3000)
        // 2. Older Pinned (q3: isPinned=true, createdAt=1500)
        // 3. Newer Normal (q2: isPinned=false, createdAt=2000)
        // 4. Older Normal (q1: isPinned=false, createdAt=1000)
        assertEquals(listOf(4L, 3L, 2L, 1L), sorted.map { it.id })
    }

    @Test
    fun testMappingFromEntityToLoanAndRoundTrip() {
        // Entity stored with tenure in months = 240 (20 years)
        val entity20Years = QuoteEntity(
            id = 42,
            name = "Dream Home Loan",
            category = LoanCategory.HOME,
            principal = 4500000.0,
            annualRate = 8.55,
            tenureMonths = 240,
            processingFeePercent = 1.0,
            isPinned = true,
            createdAt = 1700000000L
        )

        val mapping = QuoteCalculations.mapQuoteToLoanState(entity20Years)
        assertEquals(4500000.0, mapping.amount, 0.01)
        assertEquals(8.55, mapping.rate, 0.001)
        assertEquals(20, mapping.tenureValue)
        assertTrue(mapping.isYearMode)
        assertEquals(LoanCategory.HOME, mapping.category)
        assertEquals(1.0, mapping.processingFeePercent, 0.01)

        // Non-annual months (e.g. 18 months)
        val entity18Months = QuoteEntity(
            id = 43,
            name = "Short Bridge Loan",
            category = LoanCategory.PERSONAL,
            principal = 200000.0,
            annualRate = 11.5,
            tenureMonths = 18,
            processingFeePercent = 0.5
        )

        val mapping18 = QuoteCalculations.mapQuoteToLoanState(entity18Months)
        assertEquals(18, mapping18.tenureValue)
        assertFalse(mapping18.isYearMode)

        // Computed display calculation consistency
        val summary = QuoteCalculations.computeQuoteSummary(entity20Years)
        assertEquals(39194.57, summary.monthlyEMI, 0.05)
        assertEquals(45000.0, summary.totalCost - summary.totalPayment, 0.01) // 1% fee on 45L = 45k
        assertEquals("20 Yrs", summary.formattedTenure)
    }
}
