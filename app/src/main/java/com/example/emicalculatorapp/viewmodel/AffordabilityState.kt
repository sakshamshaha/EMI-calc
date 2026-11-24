package com.example.emicalculatorapp.viewmodel

import com.example.emicalculatorapp.model.AffordabilityCalculator
import com.example.emicalculatorapp.model.AffordabilityParams
import com.example.emicalculatorapp.model.AffordabilityResult
import com.example.emicalculatorapp.model.EMICalculationResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.round

class AffordabilityState(
    private val scope: CoroutineScope,
    private val liveLoanAmount: StateFlow<Double>,
    private val liveLoanResult: StateFlow<EMICalculationResult>,
    liveRate: StateFlow<Double>,
    liveTenure: StateFlow<Int>
) {
    private val _params = MutableStateFlow(
        AffordabilityParams(
            monthlyIncome = 100000.0,
            existingEmis = 10000.0,
            maxDtiRatioPercent = 40.0,
            interestRate = liveRate.value,
            tenureYears = liveTenure.value
        )
    )
    val params: StateFlow<AffordabilityParams> = _params.asStateFlow()

    private val _result = MutableStateFlow(
        AffordabilityCalculator.calculateAffordability(
            income = 100000.0,
            existingEmis = 10000.0,
            maxRatioPercent = 40.0,
            rate = liveRate.value,
            tenureYears = liveTenure.value,
            liveLoanAmount = liveLoanAmount.value,
            liveLoanEmi = liveLoanResult.value.monthlyEMI
        )
    )
    val result: StateFlow<AffordabilityResult> = _result.asStateFlow()

    init {
        scope.launch {
            combine(
                liveLoanAmount,
                liveLoanResult,
                _params
            ) { loanAmt, loanRes, par ->
                Triple(loanAmt, loanRes.monthlyEMI, par)
            }.distinctUntilChanged().collectLatest { (amt, emi, par) ->
                val calc = withContext(Dispatchers.Default) {
                    AffordabilityCalculator.calculateAffordability(
                        income = par.monthlyIncome,
                        existingEmis = par.existingEmis,
                        maxRatioPercent = par.maxDtiRatioPercent,
                        rate = par.interestRate,
                        tenureYears = par.tenureYears,
                        liveLoanAmount = amt,
                        liveLoanEmi = emi
                    )
                }
                _result.value = calc
            }
        }
    }

    fun setMonthlyIncome(income: Double) {
        val clamped = income.coerceIn(0.0, 10000000.0)
        if (_params.value.monthlyIncome != clamped) {
            _params.value = _params.value.copy(monthlyIncome = clamped)
        }
    }

    fun setExistingEmis(emis: Double) {
        val clamped = emis.coerceIn(0.0, 10000000.0)
        if (_params.value.existingEmis != clamped) {
            _params.value = _params.value.copy(existingEmis = clamped)
        }
    }

    fun setMaxDtiRatio(ratioPercent: Double) {
        val clamped = (round(ratioPercent.coerceIn(20.0, 60.0) * 10.0) / 10.0)
        if (_params.value.maxDtiRatioPercent != clamped) {
            _params.value = _params.value.copy(maxDtiRatioPercent = clamped)
        }
    }

    fun setInterestRate(rate: Double) {
        val clamped = (round(rate.coerceIn(1.0, 30.0) * 100.0) / 100.0)
        if (_params.value.interestRate != clamped) {
            _params.value = _params.value.copy(interestRate = clamped)
        }
    }

    fun setTenureYears(years: Int) {
        val clamped = years.coerceIn(1, 30)
        if (_params.value.tenureYears != clamped) {
            _params.value = _params.value.copy(tenureYears = clamped)
        }
    }

    fun resetToDefaults(defaultRate: Double, defaultTenure: Int) {
        _params.value = AffordabilityParams(
            monthlyIncome = 100000.0,
            existingEmis = 10000.0,
            maxDtiRatioPercent = 40.0,
            interestRate = defaultRate,
            tenureYears = defaultTenure
        )
    }
}
