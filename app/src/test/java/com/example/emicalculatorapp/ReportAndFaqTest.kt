package com.example.emicalculatorapp

import com.example.emicalculatorapp.data.database.ReportEntity
import com.example.emicalculatorapp.model.faq.FaqItem
import com.example.emicalculatorapp.model.faq.FaqRepository
import com.example.emicalculatorapp.model.faq.FaqSearch
import com.example.emicalculatorapp.model.report.DiagnosticInfo
import com.example.emicalculatorapp.model.report.LoanInputDiagnostics
import com.example.emicalculatorapp.model.report.ReportBodyBuilder
import com.example.emicalculatorapp.model.report.ReportCategory
import com.example.emicalculatorapp.model.report.ReportStatus
import com.example.emicalculatorapp.model.report.ReportValidation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ReportAndFaqTest {

    private val sampleDiagnostics = DiagnosticInfo(
        appVersion = "1.0",
        androidVersion = "14",
        apiLevel = 34,
        manufacturer = "Google",
        model = "Pixel 8",
        currentScreen = "Calculate",
        effectsLevel = "BALANCED",
        numberFormat = "INDIAN"
    )

    private val sampleLoanInputs = LoanInputDiagnostics(
        loanAmount = 4500000.0,
        interestRate = 8.55,
        tenureMonths = 240,
        loanCategory = "Home"
    )

    @Test
    fun testReportBodyBuilderWithAllDiagnostics() {
        val body = ReportBodyBuilder.buildBody(
            category = ReportCategory.BUG.displayName,
            description = "The EMI calculation formula does not update when dragging.",
            diagnosticInfo = sampleDiagnostics,
            loanInputs = sampleLoanInputs
        )

        assertTrue(body.startsWith("Category: Bug"))
        assertTrue(body.contains("Description:\nThe EMI calculation formula does not update when dragging."))
        assertTrue(body.contains("--- Diagnostic Info ---"))
        assertTrue(body.contains("App Version: 1.0"))
        assertTrue(body.contains("Android Version: 14 (API 34)"))
        assertTrue(body.contains("Device: Google Pixel 8"))
        assertTrue(body.contains("Screen: Calculate"))
        assertTrue(body.contains("Effects Level: BALANCED"))
        assertTrue(body.contains("Number Format: INDIAN"))
        assertTrue(body.contains("--- Current Loan Inputs ---"))
        assertTrue(body.contains("Category: Home"))
        assertTrue(body.contains("Amount: 4500000.0"))
        assertTrue(body.contains("Interest Rate: 8.55%"))
        assertTrue(body.contains("Tenure: 240 months"))
    }

    @Test
    fun testReportBodyBuilderWithoutDiagnostics() {
        val body = ReportBodyBuilder.buildBody(
            category = ReportCategory.SUGGESTION.displayName,
            description = "Please add export to CSV in a future update.",
            diagnosticInfo = null,
            loanInputs = null
        )

        assertTrue(body.startsWith("Category: Suggestion"))
        assertTrue(body.contains("Description:\nPlease add export to CSV in a future update."))
        assertFalse(body.contains("--- Diagnostic Info ---"))
        assertFalse(body.contains("--- Current Loan Inputs ---"))
    }

    @Test
    fun testReportBodyBuilderWithoutLoanInputs() {
        val body = ReportBodyBuilder.buildBody(
            category = ReportCategory.WRONG_CALCULATION.displayName,
            description = "Discrepancy in monthly schedule amortization.",
            diagnosticInfo = sampleDiagnostics,
            loanInputs = null
        )

        assertTrue(body.contains("--- Diagnostic Info ---"))
        assertFalse(body.contains("--- Current Loan Inputs ---"))
    }

    @Test
    fun testReportBodyBuilderTrimming() {
        val rawDesc = "   \n\n  Found an issue with padding.  \n\n  "
        val body = ReportBodyBuilder.buildBody(
            category = "  Bug  ",
            description = rawDesc
        )

        assertTrue(body.contains("Category: Bug"))
        assertTrue(body.contains("Description:\nFound an issue with padding."))
    }

    @Test
    fun testDescriptionValidationBoundaries() {
        // 9 characters: Invalid
        val nineChars = "123456789"
        assertEquals(9, nineChars.length)
        assertFalse(ReportValidation.isValid(nineChars))
        assertNotNull(ReportValidation.getValidationMessage(nineChars))

        // 10 characters: Valid
        val tenChars = "1234567890"
        assertEquals(10, tenChars.length)
        assertTrue(ReportValidation.isValid(tenChars))
        assertNull(ReportValidation.getValidationMessage(tenChars))

        // 1000 characters: Valid
        val thousandChars = "a".repeat(1000)
        assertEquals(1000, thousandChars.length)
        assertTrue(ReportValidation.isValid(thousandChars))
        assertNull(ReportValidation.getValidationMessage(thousandChars))

        // 1001 characters: Invalid
        val thousandOneChars = "a".repeat(1001)
        assertEquals(1001, thousandOneChars.length)
        assertFalse(ReportValidation.isValid(thousandOneChars))
        assertNotNull(ReportValidation.getValidationMessage(thousandOneChars))
    }

    @Test
    fun testFaqSearchEmptyReturnsAll() {
        val all = FaqRepository.faqItems
        assertEquals(all.size, FaqSearch.filter(all, "").size)
        assertEquals(all.size, FaqSearch.filter(all, "   ").size)
    }

    @Test
    fun testFaqSearchMatchesQuestionAndCaseInsensitive() {
        val all = FaqRepository.faqItems

        val resultsLower = FaqSearch.filter(all, "calculated")
        val resultsUpper = FaqSearch.filter(all, "CALCULATED")
        val resultsMixed = FaqSearch.filter(all, "cALCuLaTed")

        assertTrue(resultsLower.isNotEmpty())
        assertEquals(resultsLower.size, resultsUpper.size)
        assertEquals(resultsLower.size, resultsMixed.size)
        assertTrue(resultsLower.any { it.id == "faq_emi_calc" })
    }

    @Test
    fun testFaqSearchMatchesAnswerContent() {
        val all = FaqRepository.faqItems
        val results = FaqSearch.filter(all, "reducing balance")

        assertTrue(results.isNotEmpty())
        assertTrue(results.any { it.id == "faq_emi_calc" })
    }

    @Test
    fun testFaqSearchMultiWordQuery() {
        val all = FaqRepository.faqItems
        val results = FaqSearch.filter(all, "winner badges")

        assertEquals(2, results.size)
        assertTrue(results.any { it.id == "faq_compare_loans" })
        assertTrue(results.any { it.id == "faq_tabs_overview" })

        val specificResults = FaqSearch.filter(all, "rule-of-thumb zones")
        assertEquals(1, specificResults.size)
        assertEquals("faq_affordability", specificResults.first().id)
    }

    @Test
    fun testFaqSearchNoMatch() {
        val all = FaqRepository.faqItems
        val results = FaqSearch.filter(all, "xyznonexistentquery123")
        assertTrue(results.isEmpty())
    }

    @Test
    fun testReportStatusTransitions() {
        var report = ReportEntity(
            id = 1,
            category = "Bug",
            description = "Test description with more than 10 chars",
            diagnosticsText = "Diagnostics info",
            screenshotPath = null,
            status = ReportStatus.DRAFT,
            createdAt = 1000L
        )

        assertEquals(ReportStatus.DRAFT, report.status)

        // Status transition upon opening in email app
        report = report.copy(status = ReportStatus.OPENED_IN_EMAIL)
        assertEquals(ReportStatus.OPENED_IN_EMAIL, report.status)
    }

    @Test
    fun testReportSortOrderNewestFirst() {
        val reportOld = ReportEntity(
            id = 1,
            category = "Bug",
            description = "Old bug",
            diagnosticsText = "",
            status = ReportStatus.OPENED_IN_EMAIL,
            createdAt = 1000L
        )

        val reportNew = ReportEntity(
            id = 2,
            category = "Suggestion",
            description = "New suggestion",
            diagnosticsText = "",
            status = ReportStatus.DRAFT,
            createdAt = 5000L
        )

        val reportMid = ReportEntity(
            id = 3,
            category = "Wrong calculation",
            description = "Mid report",
            diagnosticsText = "",
            status = ReportStatus.DRAFT,
            createdAt = 3000L
        )

        val unsorted = listOf(reportOld, reportNew, reportMid)
        val sorted = unsorted.sortedByDescending { it.createdAt }

        assertEquals(reportNew.id, sorted[0].id)
        assertEquals(reportMid.id, sorted[1].id)
        assertEquals(reportOld.id, sorted[2].id)
    }
}
