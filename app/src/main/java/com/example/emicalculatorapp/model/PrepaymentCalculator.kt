package com.example.emicalculatorapp.model

import kotlin.math.floor
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.round

/**
 * Pure mathematical simulation engine for Prepayment & Debt-Free calculations.
 * Simulates monthly amortization with custom extra monthly EMI, lump-sum prepayments,
 * and annual step-up percentages.
 */
object PrepaymentCalculator {

    fun calculatePrepaymentSimulation(
        principal: Double,
        annualRate: Double,
        tenure: Int,
        isYears: Boolean,
        params: PrepaymentParams
    ): PrepaymentSimulationResult {
        val totalMonths = if (isYears) tenure * 12 else tenure
        val monthlyRate = annualRate / 12.0 / 100.0

        val baseEMI = if (monthlyRate == 0.0) {
            principal / totalMonths
        } else {
            val factor = (1.0 + monthlyRate).pow(totalMonths.toDouble())
            principal * monthlyRate * factor / (factor - 1.0)
        }

        // ================= 1. STANDARD SCHEDULE SIMULATION =================
        var stdBalance = principal
        var stdTotalInterest = 0.0
        var stdTotalPayment = 0.0
        var stdCumulativePrincipal = 0.0
        var stdHalfwayMonth = 0
        var stdCrossoverMonth = 0
        val stdBalances = ArrayList<Double>(totalMonths + 1)
        stdBalances.add(principal)

        for (m in 1..totalMonths) {
            val interestForMonth = stdBalance * monthlyRate
            val principalForMonth = min(stdBalance, baseEMI - interestForMonth)
            val actualPayment = principalForMonth + interestForMonth

            stdBalance -= principalForMonth
            stdTotalInterest += interestForMonth
            stdTotalPayment += actualPayment
            stdCumulativePrincipal += principalForMonth
            stdBalances.add(stdBalance.coerceAtLeast(0.0))

            if (stdHalfwayMonth == 0 && stdCumulativePrincipal >= principal / 2.0) {
                stdHalfwayMonth = m
            }
            if (stdCrossoverMonth == 0 && principalForMonth >= interestForMonth) {
                stdCrossoverMonth = m
            }
        }
        if (stdHalfwayMonth == 0) stdHalfwayMonth = totalMonths
        if (stdCrossoverMonth == 0) stdCrossoverMonth = totalMonths

        // ================= 2. PREPAYMENT ACCELERATED SIMULATION =================
        var curBalance = principal
        var newTotalInterest = 0.0
        var newTotalPayment = 0.0
        var newCumulativePrincipal = 0.0
        var newHalfwayMonth = 0
        var newCrossoverMonth = 0
        var month = 0
        val newBalances = ArrayList<Double>()
        newBalances.add(principal)

        while (curBalance > 0.0001 && month < 1200) {
            month++
            val yearIdx = (month - 1) / 12
            val stepFactor = if (params.isStepUpEnabled && params.stepUpPercent > 0.0) {
                (1.0 + params.stepUpPercent / 100.0).pow(yearIdx.toDouble())
            } else {
                1.0
            }

            val extraMonthly = if (params.extraMonthly > 0.0) params.extraMonthly else 0.0
            // When step-up is enabled, annual increase applies to base EMI (or base EMI + extra)
            val curEmi = (baseEMI * stepFactor) + extraMonthly

            val curLump = if (
                params.isLumpSumEnabled &&
                params.lumpSumAmount > 0.0 &&
                month == params.lumpSumMonth &&
                params.lumpSumMonth in 1..totalMonths
            ) {
                params.lumpSumAmount
            } else {
                0.0
            }

            val plannedPayment = curEmi + curLump
            val interestForMonth = curBalance * monthlyRate

            val plannedPrincipal = plannedPayment - interestForMonth
            val actualPrincipal: Double
            val actualPayment: Double

            if (plannedPrincipal >= curBalance) {
                actualPrincipal = curBalance
                actualPayment = actualPrincipal + interestForMonth
                curBalance = 0.0
            } else {
                actualPrincipal = plannedPrincipal
                actualPayment = plannedPayment
                curBalance -= actualPrincipal
            }

            newTotalInterest += interestForMonth
            newTotalPayment += actualPayment
            newCumulativePrincipal += actualPrincipal
            newBalances.add(curBalance.coerceAtLeast(0.0))

            if (newHalfwayMonth == 0 && newCumulativePrincipal >= principal / 2.0) {
                newHalfwayMonth = month
            }
            if (newCrossoverMonth == 0 && actualPrincipal >= interestForMonth) {
                newCrossoverMonth = month
            }
        }

        val newMonths = month
        if (newHalfwayMonth == 0) newHalfwayMonth = newMonths
        if (newCrossoverMonth == 0) newCrossoverMonth = newMonths

        val monthsSaved = (totalMonths - newMonths).coerceAtLeast(0)
        val interestSaved = (stdTotalInterest - newTotalInterest).coerceAtLeast(0.0)
        val paymentSaved = (stdTotalPayment - newTotalPayment).coerceAtLeast(0.0)

        // ================= 3. DOWNSAMPLED CHART POINTS (<= 120 points) =================
        val maxMonth = maxOf(totalMonths, newMonths)
        val step = if (maxMonth <= 120) 1 else (maxMonth + 119) / 120

        val chartPoints = mutableListOf<PrepaymentPoint>()
        var m = 0
        while (m <= maxMonth) {
            val stdBal = if (m < stdBalances.size) stdBalances[m] else 0.0
            val newBal = if (m < newBalances.size) newBalances[m] else 0.0
            chartPoints.add(
                PrepaymentPoint(
                    month = m,
                    originalBalance = stdBal,
                    newBalance = newBal
                )
            )
            m += step
        }
        // Ensure final payoff month and totalMonths are explicitly included in chart points
        if (chartPoints.none { it.month == newMonths } && newMonths <= maxMonth) {
            chartPoints.add(
                PrepaymentPoint(
                    month = newMonths,
                    originalBalance = if (newMonths < stdBalances.size) stdBalances[newMonths] else 0.0,
                    newBalance = 0.0
                )
            )
        }
        if (chartPoints.none { it.month == totalMonths }) {
            chartPoints.add(
                PrepaymentPoint(
                    month = totalMonths,
                    originalBalance = 0.0,
                    newBalance = 0.0
                )
            )
        }
        chartPoints.sortBy { it.month }

        return PrepaymentSimulationResult(
            originalMonths = totalMonths,
            newMonths = newMonths,
            monthsSaved = monthsSaved,
            yearsSaved = monthsSaved / 12,
            remainingMonthsSaved = monthsSaved % 12,
            newTenureYears = newMonths / 12,
            newTenureMonths = newMonths % 12,
            originalTotalInterest = stdTotalInterest,
            newTotalInterest = newTotalInterest,
            interestSaved = interestSaved,
            originalTotalPayment = stdTotalPayment,
            newTotalPayment = newTotalPayment,
            paymentSaved = paymentSaved,
            chartPoints = chartPoints,
            milestones = PrepaymentMilestones(
                originalHalfwayMonth = stdHalfwayMonth,
                newHalfwayMonth = newHalfwayMonth,
                originalCrossoverMonth = stdCrossoverMonth,
                newCrossoverMonth = newCrossoverMonth
            )
        )
    }
}
