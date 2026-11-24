package com.example.emicalculatorapp.viewmodel

import com.example.emicalculatorapp.model.PrepaymentCalculator
import com.example.emicalculatorapp.model.PrepaymentParams
import com.example.emicalculatorapp.model.PrepaymentSimulationResult
import com.example.emicalculatorapp.model.StepUpMode
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

private data class LoanPrepaymentConfig(
    val principal: Double,
    val annualRate: Double,
    val tenure: Int,
    val isYears: Boolean,
    val params: PrepaymentParams
)

class PrepaymentState(
    private val scope: CoroutineScope,
    private val loanAmount: StateFlow<Double>,
    private val interestRate: StateFlow<Double>,
    private val tenureValue: StateFlow<Int>,
    private val isYearMode: StateFlow<Boolean>
) {
    private val _params = MutableStateFlow(PrepaymentParams())
    val params: StateFlow<PrepaymentParams> = _params.asStateFlow()

    private val _result = MutableStateFlow(
        PrepaymentCalculator.calculatePrepaymentSimulation(
            principal = 4500000.0,
            annualRate = 8.55,
            tenure = 20,
            isYears = true,
            params = PrepaymentParams()
        )
    )
    val result: StateFlow<PrepaymentSimulationResult> = _result.asStateFlow()

    init {
        scope.launch {
            combine(loanAmount, interestRate, tenureValue, isYearMode, _params) { p, r, t, ym, par ->
                LoanPrepaymentConfig(p, r, t, ym, par)
            }.distinctUntilChanged().collectLatest { cfg ->
                val simResult = withContext(Dispatchers.Default) {
                    PrepaymentCalculator.calculatePrepaymentSimulation(
                        cfg.principal, cfg.annualRate, cfg.tenure, cfg.isYears, cfg.params
                    )
                }
                _result.value = simResult
            }
        }
    }

    fun setExtraMonthly(amount: Double) {
        val clamped = amount.coerceAtLeast(0.0)
        if (_params.value.extraMonthly != clamped) {
            _params.value = _params.value.copy(extraMonthly = clamped)
        }
    }

    fun setLumpSumAmount(amount: Double) {
        val clamped = amount.coerceAtLeast(0.0)
        if (_params.value.lumpSumAmount != clamped) {
            _params.value = _params.value.copy(lumpSumAmount = clamped)
        }
    }

    fun setLumpSumMonth(month: Int) {
        val maxMonth = if (isYearMode.value) tenureValue.value * 12 else tenureValue.value
        val clamped = month.coerceIn(1, maxMonth.coerceAtLeast(1))
        if (_params.value.lumpSumMonth != clamped) {
            _params.value = _params.value.copy(lumpSumMonth = clamped)
        }
    }

    fun setLumpSumEnabled(enabled: Boolean) {
        if (_params.value.isLumpSumEnabled != enabled) {
            _params.value = _params.value.copy(isLumpSumEnabled = enabled)
        }
    }

    fun setStepUpPercent(percent: Double) {
        val clamped = percent.coerceIn(0.0, 20.0)
        if (_params.value.stepUpPercent != clamped) {
            _params.value = _params.value.copy(stepUpPercent = clamped)
        }
    }

    fun setStepUpEnabled(enabled: Boolean) {
        if (_params.value.isStepUpEnabled != enabled) {
            _params.value = _params.value.copy(isStepUpEnabled = enabled)
        }
    }

    fun setStepUpMode(mode: StepUpMode) {
        if (_params.value.stepUpMode != mode) {
            _params.value = _params.value.copy(stepUpMode = mode)
        }
    }

    fun clampLumpSumMonth() {
        val maxMonth = if (isYearMode.value) tenureValue.value * 12 else tenureValue.value
        val clamped = _params.value.lumpSumMonth.coerceIn(1, maxMonth.coerceAtLeast(1))
        if (_params.value.lumpSumMonth != clamped) {
            _params.value = _params.value.copy(lumpSumMonth = clamped)
        }
    }

    fun reset() {
        _params.value = PrepaymentParams()
    }
}
