package com.example.emicalculatorapp.model

import com.example.emicalculatorapp.data.database.QuoteEntity
import com.example.emicalculatorapp.data.settings.NumberFormatStyle
import com.example.emicalculatorapp.util.CurrencyFormatter
import java.util.Locale

data class LoanStateMapping(
    val amount: Double,
    val rate: Double,
    val tenureValue: Int,
    val isYearMode: Boolean,
    val category: LoanCategory,
    val processingFeePercent: Double
)

data class ComputedQuote(
    val entity: QuoteEntity,
    val monthlyEMI: Double,
    val totalInterest: Double,
    val totalPayment: Double,
    val totalCost: Double,
    val formattedTenure: String
)

object QuoteCalculations {

    fun generateDefaultQuoteName(
        category: LoanCategory,
        principal: Double,
        rate: Double,
        numberFormat: NumberFormatStyle = NumberFormatStyle.INDIAN
    ): String {
        val compactAmount = CurrencyFormatter.formatCompactCurrency(principal, numberFormat)
        val rateStr = String.format(Locale.US, "%.2f%%", rate)
        val rawName = "${category.title} loan - $compactAmount at $rateStr"
        return rawName.take(40)
    }

    fun validateQuoteName(input: String): String? {
        val trimmed = input.trim()
        if (trimmed.isEmpty() || trimmed.length > 40) return null
        return trimmed
    }

    fun selectQuoteForComparison(currentSelected: List<Long>, clickedId: Long): List<Long> {
        return if (currentSelected.contains(clickedId)) {
            currentSelected - clickedId
        } else {
            if (currentSelected.size >= 2) {
                // Max 2 allowed: selecting more than 2 is not allowed
                currentSelected
            } else {
                currentSelected + clickedId
            }
        }
    }

    fun sortQuotes(quotes: List<QuoteEntity>): List<QuoteEntity> {
        return quotes.sortedWith(
            compareByDescending<QuoteEntity> { it.isPinned }
                .thenByDescending { it.createdAt }
        )
    }

    fun mapQuoteToLoanState(quote: QuoteEntity): LoanStateMapping {
        val isYearMode = (quote.tenureMonths % 12 == 0) && quote.tenureMonths > 0
        val tenureValue = if (isYearMode) quote.tenureMonths / 12 else quote.tenureMonths
        return LoanStateMapping(
            amount = quote.principal,
            rate = quote.annualRate,
            tenureValue = tenureValue,
            isYearMode = isYearMode,
            category = quote.category,
            processingFeePercent = quote.processingFeePercent
        )
    }

    fun computeQuoteSummary(quote: QuoteEntity): ComputedQuote {
        val emiResult = EMICalculator.calculateEMI(
            principal = quote.principal,
            annualRate = quote.annualRate,
            tenure = quote.tenureMonths,
            isYears = false
        )
        val feeAmount = quote.principal * (quote.processingFeePercent / 100.0)
        val totalCost = emiResult.totalPayment + feeAmount
        val formattedTenure = if (quote.tenureMonths % 12 == 0) {
            "${quote.tenureMonths / 12} Yrs"
        } else {
            "${quote.tenureMonths} Mo"
        }
        return ComputedQuote(
            entity = quote,
            monthlyEMI = emiResult.monthlyEMI,
            totalInterest = emiResult.totalInterest,
            totalPayment = emiResult.totalPayment,
            totalCost = totalCost,
            formattedTenure = formattedTenure
        )
    }
}
