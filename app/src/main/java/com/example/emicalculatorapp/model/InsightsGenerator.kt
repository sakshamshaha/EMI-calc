package com.example.emicalculatorapp.model

import com.example.emicalculatorapp.data.settings.NumberFormatStyle
import com.example.emicalculatorapp.viewmodel.EMIViewModel
import java.util.Locale
import kotlin.math.round

enum class InsightType {
    INTEREST_SHARE,
    SHORTER_TENURE,
    RATE_HIKE,
    EXTRA_PAYMENT
}

enum class InsightIcon {
    PIE_CHART,
    TRENDING_DOWN,
    PERCENT,
    BOLT
}

data class LoanInsight(
    val id: String,
    val type: InsightType,
    val headlineValue: Double,
    val headlinePrefix: String = "",
    val headlineSuffix: String = "",
    val headlineDisplay: String,
    val description: String,
    val icon: InsightIcon
)

data class RateSensitivityPoint(
    val rateOffset: Double,
    val rate: Double,
    val monthlyEMI: Double,
    val isCurrent: Boolean
)

object InsightsGenerator {

    /**
     * Pure generator computing dynamic insights from the current loan parameters.
     */
    fun generateInsights(
        principal: Double,
        annualRate: Double,
        tenureMonths: Int,
        numberFormat: NumberFormatStyle = NumberFormatStyle.INDIAN
    ): List<LoanInsight> {
        if (principal <= 0.0 || tenureMonths <= 0) return emptyList()

        val currentResult = EMICalculator.calculateEMI(
            principal = principal,
            annualRate = annualRate,
            tenure = tenureMonths,
            isYears = false
        )

        val insights = mutableListOf<LoanInsight>()

        // 1. Interest Share Insight
        if (currentResult.totalPayment > 0.0) {
            val sharePct = (currentResult.totalInterest / currentResult.totalPayment) * 100.0
            val formattedShare = String.format(Locale.US, "%.2f", sharePct)
            insights.add(
                LoanInsight(
                    id = "interest_share",
                    type = InsightType.INTEREST_SHARE,
                    headlineValue = round(sharePct * 100.0) / 100.0,
                    headlinePrefix = "",
                    headlineSuffix = "%",
                    headlineDisplay = "$formattedShare% Interest",
                    description = "Interest represents $formattedShare% of your total ${EMIViewModel.formatCurrency(currentResult.totalPayment, numberFormat)} repayment.",
                    icon = InsightIcon.PIE_CHART
                )
            )
        }

        // 2. Shorter Tenure Insight (reduce by 2y if >=5y, else 1y if >=2y, else skip)
        val reductionMonths = when {
            tenureMonths >= 60 -> 24 // 2 years
            tenureMonths >= 24 -> 12 // 1 year
            else -> 0
        }
        if (reductionMonths > 0) {
            val newTenureMonths = tenureMonths - reductionMonths
            val newResult = EMICalculator.calculateEMI(
                principal = principal,
                annualRate = annualRate,
                tenure = newTenureMonths,
                isYears = false
            )
            val emiIncrease = newResult.monthlyEMI - currentResult.monthlyEMI
            val interestSaved = currentResult.totalInterest - newResult.totalInterest
            val tenureLabel = if (newTenureMonths % 12 == 0) "${newTenureMonths / 12} yrs" else "$newTenureMonths mos"

            insights.add(
                LoanInsight(
                    id = "shorter_tenure",
                    type = InsightType.SHORTER_TENURE,
                    headlineValue = round(emiIncrease * 100.0) / 100.0,
                    headlinePrefix = "+₹",
                    headlineSuffix = "/mo",
                    headlineDisplay = "+${EMIViewModel.formatCurrency(emiIncrease, numberFormat)}/mo",
                    description = "Shortening tenure to $tenureLabel saves ${EMIViewModel.formatCurrency(interestSaved, numberFormat)} in total interest.",
                    icon = InsightIcon.TRENDING_DOWN
                )
            )
        }

        // 3. Rate +0.5% Stress Test Insight
        val higherRate = annualRate + 0.5
        val higherResult = EMICalculator.calculateEMI(
            principal = principal,
            annualRate = higherRate,
            tenure = tenureMonths,
            isYears = false
        )
        val rateEmiIncrease = higherResult.monthlyEMI - currentResult.monthlyEMI
        val totalInterestIncrease = higherResult.totalInterest - currentResult.totalInterest
        insights.add(
            LoanInsight(
                id = "rate_hike",
                type = InsightType.RATE_HIKE,
                headlineValue = round(rateEmiIncrease * 100.0) / 100.0,
                headlinePrefix = "+₹",
                headlineSuffix = "/mo",
                headlineDisplay = "+${EMIViewModel.formatCurrency(rateEmiIncrease, numberFormat)}/mo",
                description = "A +0.5% hike to ${String.format(Locale.US, "%.2f%%", higherRate)} adds ${EMIViewModel.formatCurrency(rateEmiIncrease, numberFormat)}/mo (+${EMIViewModel.formatCurrency(totalInterestIncrease, numberFormat)} total interest).",
                icon = InsightIcon.PERCENT
            )
        )

        // 4. Extra Payment Prepayment Insight (10% of EMI rounded to nearest Rs 500, min Rs 500)
        val rawTenPct = currentResult.monthlyEMI * 0.10
        val extraMonthly = (round(rawTenPct / 500.0) * 500.0).coerceAtLeast(500.0)
        val prepayParams = PrepaymentParams(
            extraMonthly = extraMonthly,
            isLumpSumEnabled = false,
            isStepUpEnabled = false
        )
        val prepayResult = PrepaymentCalculator.calculatePrepaymentSimulation(
            principal = principal,
            annualRate = annualRate,
            tenure = tenureMonths,
            isYears = false,
            params = prepayParams
        )
        val monthsSaved = prepayResult.monthsSaved
        val prepayInterestSaved = prepayResult.interestSaved
        if (monthsSaved > 0) {
            insights.add(
                LoanInsight(
                    id = "extra_payment",
                    type = InsightType.EXTRA_PAYMENT,
                    headlineValue = monthsSaved.toDouble(),
                    headlinePrefix = "Save ",
                    headlineSuffix = " Months",
                    headlineDisplay = "Save $monthsSaved Months",
                    description = "Paying ${EMIViewModel.formatCurrency(extraMonthly, numberFormat)} extra monthly saves ${EMIViewModel.formatCurrency(prepayInterestSaved, numberFormat)} interest and closes $monthsSaved months early.",
                    icon = InsightIcon.BOLT
                )
            )
        }

        return insights
    }

    /**
     * Computes EMI at -1%, current, +1%, and +2% (only rates >= 1.0%).
     */
    fun calculateRateSensitivity(
        principal: Double,
        annualRate: Double,
        tenureMonths: Int
    ): List<RateSensitivityPoint> {
        val offsets = listOf(-1.0, 0.0, 1.0, 2.0)
        return offsets.mapNotNull { offset ->
            val rate = (round((annualRate + offset) * 100.0) / 100.0)
            if (rate >= 1.0) {
                val emiResult = EMICalculator.calculateEMI(principal, rate, tenureMonths, isYears = false)
                RateSensitivityPoint(
                    rateOffset = offset,
                    rate = rate,
                    monthlyEMI = emiResult.monthlyEMI,
                    isCurrent = offset == 0.0
                )
            } else {
                null
            }
        }
    }
}
