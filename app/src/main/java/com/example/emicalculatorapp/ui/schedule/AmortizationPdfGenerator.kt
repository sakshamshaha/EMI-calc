package com.example.emicalculatorapp.ui.schedule

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.emicalculatorapp.data.settings.NumberFormatStyle
import com.example.emicalculatorapp.model.FullAmortizationSchedule
import com.example.emicalculatorapp.viewmodel.EMIViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * High-performance off-thread PDF Document Generator for EMIverse Amortization Schedules.
 * Produces clean, paginated A4 documents compatible with all standard PDF viewers.
 */
object AmortizationPdfGenerator {

    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN = 36f

    suspend fun generateAndGetUri(
        context: Context,
        schedule: FullAmortizationSchedule,
        numberFormat: NumberFormatStyle = NumberFormatStyle.INDIAN
    ): Uri = withContext(Dispatchers.IO) {
        val document = PdfDocument()

        val titlePaint = Paint().apply {
            color = Color.rgb(18, 18, 30)
            textSize = 18f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val headerPaint = Paint().apply {
            color = Color.rgb(90, 95, 120)
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }

        val sectionPaint = Paint().apply {
            color = Color.rgb(139, 92, 246) // Electric Violet
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val textPaint = Paint().apply {
            color = Color.rgb(30, 35, 45)
            textSize = 9f
            isAntiAlias = true
        }

        val boldTextPaint = Paint().apply {
            color = Color.rgb(20, 25, 35)
            textSize = 9f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val tableHeaderPaint = Paint().apply {
            color = Color.rgb(240, 242, 248)
            style = Paint.Style.FILL
        }

        val tableRowAltPaint = Paint().apply {
            color = Color.rgb(248, 249, 252)
            style = Paint.Style.FILL
        }

        val linePaint = Paint().apply {
            color = Color.rgb(220, 224, 235)
            strokeWidth = 0.75f
        }

        val footerPaint = Paint().apply {
            color = Color.rgb(130, 135, 150)
            textSize = 8f
            isAntiAlias = true
        }

        val years = schedule.years
        val rowsPerPage = 28
        // Page 1 has header and summary, so it holds ~16 table rows. Subsequent pages hold ~28 rows.
        val page1Rows = 16
        val remainingYears = if (years.size > page1Rows) years.subList(page1Rows, years.size) else emptyList()
        val totalPages = 1 + if (remainingYears.isNotEmpty()) {
            (remainingYears.size + rowsPerPage - 1) / rowsPerPage
        } else 0

        var currentPageNumber = 1

        // ================= PAGE 1 =================
        var pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, currentPageNumber).create()
        var page = document.startPage(pageInfo)
        var canvas = page.canvas

        var y = MARGIN + 20f

        // Document Branding
        canvas.drawText("EMIverse Loan Amortization Schedule", MARGIN, y, titlePaint)
        y += 14f

        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US)
        canvas.drawText("Generated on ${dateFormat.format(Date())} • Exact Math Verification", MARGIN, y, headerPaint)
        y += 20f

        // Summary Card Box
        val summaryBoxTop = y
        val summaryBoxHeight = 85f
        val summaryPaint = Paint().apply {
            color = Color.rgb(244, 245, 252)
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(MARGIN, summaryBoxTop, PAGE_WIDTH - MARGIN, summaryBoxTop + summaryBoxHeight, 8f, 8f, summaryPaint)
        canvas.drawRoundRect(MARGIN, summaryBoxTop, PAGE_WIDTH - MARGIN, summaryBoxTop + summaryBoxHeight, 8f, 8f, linePaint)

        // Summary Content Row 1
        val col1 = MARGIN + 16f
        val col2 = MARGIN + 180f
        val col3 = MARGIN + 350f

        var sy = summaryBoxTop + 24f
        canvas.drawText("LOAN AMOUNT", col1, sy, headerPaint)
        canvas.drawText("ANNUAL RATE", col2, sy, headerPaint)
        canvas.drawText("TENURE", col3, sy, headerPaint)

        sy += 14f
        canvas.drawText(EMIViewModel.formatCurrency(schedule.principal, numberFormat), col1, sy, boldTextPaint)
        canvas.drawText("${schedule.annualRate}% p.a.", col2, sy, boldTextPaint)
        canvas.drawText("${schedule.totalMonths / 12} Years (${schedule.totalMonths} Months)", col3, sy, boldTextPaint)

        // Summary Content Row 2
        sy += 20f
        canvas.drawText("MONTHLY EMI", col1, sy, headerPaint)
        canvas.drawText("TOTAL INTEREST", col2, sy, headerPaint)
        canvas.drawText("TOTAL PAYMENT", col3, sy, headerPaint)

        sy += 14f
        canvas.drawText(EMIViewModel.formatCurrency(schedule.monthlyEMI, numberFormat), col1, sy, boldTextPaint)
        canvas.drawText(EMIViewModel.formatCurrency(schedule.totalInterest, numberFormat), col2, sy, boldTextPaint)
        canvas.drawText(EMIViewModel.formatCurrency(schedule.totalPayment, numberFormat), col3, sy, boldTextPaint)

        y = summaryBoxTop + summaryBoxHeight + 25f

        // Section Title
        canvas.drawText("ANNUAL AMORTIZATION SCHEDULE", MARGIN, y, sectionPaint)
        y += 12f

        // Table Header
        val colW = (PAGE_WIDTH - (MARGIN * 2)) / 5
        val tableTop = y
        canvas.drawRect(MARGIN, tableTop, PAGE_WIDTH - MARGIN, tableTop + 18f, tableHeaderPaint)
        canvas.drawRect(MARGIN, tableTop, PAGE_WIDTH - MARGIN, tableTop + 18f, linePaint)

        val headerY = tableTop + 12f
        canvas.drawText("Year", MARGIN + 6f, headerY, boldTextPaint)
        canvas.drawText("Opening Balance", MARGIN + colW + 6f, headerY, boldTextPaint)
        canvas.drawText("Principal Paid", MARGIN + colW * 2 + 6f, headerY, boldTextPaint)
        canvas.drawText("Interest Paid", MARGIN + colW * 3 + 6f, headerY, boldTextPaint)
        canvas.drawText("Closing Balance", MARGIN + colW * 4 + 6f, headerY, boldTextPaint)

        y = tableTop + 18f

        // Render Page 1 Years
        val page1Years = years.take(page1Rows)
        for (i in page1Years.indices) {
            val yr = page1Years[i]
            if (i % 2 == 1) {
                canvas.drawRect(MARGIN, y, PAGE_WIDTH - MARGIN, y + 18f, tableRowAltPaint)
            }
            canvas.drawLine(MARGIN, y + 18f, PAGE_WIDTH - MARGIN, y + 18f, linePaint)

            val rowY = y + 12f
            canvas.drawText("Year ${yr.year}", MARGIN + 6f, rowY, textPaint)
            canvas.drawText(EMIViewModel.formatCurrency(yr.openingBalance, numberFormat), MARGIN + colW + 6f, rowY, textPaint)
            canvas.drawText(EMIViewModel.formatCurrency(yr.totalPrincipalPaid, numberFormat), MARGIN + colW * 2 + 6f, rowY, textPaint)
            canvas.drawText(EMIViewModel.formatCurrency(yr.totalInterestPaid, numberFormat), MARGIN + colW * 3 + 6f, rowY, textPaint)
            canvas.drawText(EMIViewModel.formatCurrency(yr.closingBalance, numberFormat), MARGIN + colW * 4 + 6f, rowY, textPaint)

            y += 18f
        }

        // Page 1 Footer
        val footerY = PAGE_HEIGHT - MARGIN + 10f
        canvas.drawLine(MARGIN, footerY - 14f, PAGE_WIDTH - MARGIN, footerY - 14f, linePaint)
        canvas.drawText("Estimates only, not financial advice.", MARGIN, footerY, footerPaint)
        canvas.drawText("Page 1 of $totalPages", PAGE_WIDTH - MARGIN - 48f, footerY, footerPaint)

        document.finishPage(page)

        // ================= SUBSEQUENT PAGES (IF YEARS > 16) =================
        var remainingIndex = page1Rows
        while (remainingIndex < years.size) {
            currentPageNumber++
            val chunk = years.subList(remainingIndex, (remainingIndex + rowsPerPage).coerceAtMost(years.size))
            remainingIndex += rowsPerPage

            pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, currentPageNumber).create()
            page = document.startPage(pageInfo)
            canvas = page.canvas

            y = MARGIN + 20f
            canvas.drawText("ANNUAL AMORTIZATION SCHEDULE (CONTINUED)", MARGIN, y, sectionPaint)
            y += 14f

            // Table Header
            canvas.drawRect(MARGIN, y, PAGE_WIDTH - MARGIN, y + 18f, tableHeaderPaint)
            canvas.drawRect(MARGIN, y, PAGE_WIDTH - MARGIN, y + 18f, linePaint)

            val nextHeaderY = y + 12f
            canvas.drawText("Year", MARGIN + 6f, nextHeaderY, boldTextPaint)
            canvas.drawText("Opening Balance", MARGIN + colW + 6f, nextHeaderY, boldTextPaint)
            canvas.drawText("Principal Paid", MARGIN + colW * 2 + 6f, nextHeaderY, boldTextPaint)
            canvas.drawText("Interest Paid", MARGIN + colW * 3 + 6f, nextHeaderY, boldTextPaint)
            canvas.drawText("Closing Balance", MARGIN + colW * 4 + 6f, nextHeaderY, boldTextPaint)

            y += 18f

            for (i in chunk.indices) {
                val yr = chunk[i]
                if (i % 2 == 1) {
                    canvas.drawRect(MARGIN, y, PAGE_WIDTH - MARGIN, y + 18f, tableRowAltPaint)
                }
                canvas.drawLine(MARGIN, y + 18f, PAGE_WIDTH - MARGIN, y + 18f, linePaint)

                val rowY = y + 12f
                canvas.drawText("Year ${yr.year}", MARGIN + 6f, rowY, textPaint)
                canvas.drawText(EMIViewModel.formatCurrency(yr.openingBalance, numberFormat), MARGIN + colW + 6f, rowY, textPaint)
                canvas.drawText(EMIViewModel.formatCurrency(yr.totalPrincipalPaid, numberFormat), MARGIN + colW * 2 + 6f, rowY, textPaint)
                canvas.drawText(EMIViewModel.formatCurrency(yr.totalInterestPaid, numberFormat), MARGIN + colW * 3 + 6f, rowY, textPaint)
                canvas.drawText(EMIViewModel.formatCurrency(yr.closingBalance, numberFormat), MARGIN + colW * 4 + 6f, rowY, textPaint)

                y += 18f
            }

            // Footer
            canvas.drawLine(MARGIN, footerY - 14f, PAGE_WIDTH - MARGIN, footerY - 14f, linePaint)
            canvas.drawText("Estimates only, not financial advice.", MARGIN, footerY, footerPaint)
            canvas.drawText("Page $currentPageNumber of $totalPages", PAGE_WIDTH - MARGIN - 48f, footerY, footerPaint)

            document.finishPage(page)
        }

        // Save PDF to cache
        val pdfFile = File(context.cacheDir, "EMIverse_Schedule_${System.currentTimeMillis()}.pdf")
        FileOutputStream(pdfFile).use { out ->
            document.writeTo(out)
        }
        document.close()

        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", pdfFile)
    }
}
