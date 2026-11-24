package com.example.emicalculatorapp.viewmodel

import com.example.emicalculatorapp.data.settings.NumberFormatStyle
import com.example.emicalculatorapp.model.InsightsGenerator
import com.example.emicalculatorapp.model.LoanInsight
import com.example.emicalculatorapp.model.RateSensitivityPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class InsightsInputParams(
    val loanAmount: Double,
    val interestRate: Double,
    val tenureValue: Int,
    val isYearMode: Boolean,
    val numberFormat: NumberFormatStyle
)

@OptIn(FlowPreview::class)
class InsightsState(
    private val scope: CoroutineScope,
    private val loanAmount: StateFlow<Double>,
    private val interestRate: StateFlow<Double>,
    private val tenureValue: StateFlow<Int>,
    private val isYearMode: StateFlow<Boolean>,
    private val numberFormat: StateFlow<NumberFormatStyle>
) {
    private val _insights = MutableStateFlow<List<LoanInsight>>(emptyList())
    val insights: StateFlow<List<LoanInsight>> = _insights.asStateFlow()

    private val _sensitivityPoints = MutableStateFlow<List<RateSensitivityPoint>>(emptyList())
    val sensitivityPoints: StateFlow<List<RateSensitivityPoint>> = _sensitivityPoints.asStateFlow()

    init {
        scope.launch {
            combine(loanAmount, interestRate, tenureValue, isYearMode, numberFormat) { p, r, t, ym, nf ->
                InsightsInputParams(p, r, t, ym, nf)
            }
                .distinctUntilChanged()
                .debounce(150L)
                .collectLatest { params ->
                    val (ins, sens) = withContext(Dispatchers.Default) {
                        val tenureMonths = if (params.isYearMode) params.tenureValue * 12 else params.tenureValue
                        val insList = InsightsGenerator.generateInsights(
                            principal = params.loanAmount,
                            annualRate = params.interestRate,
                            tenureMonths = tenureMonths,
                            numberFormat = params.numberFormat
                        )
                        val sensList = InsightsGenerator.calculateRateSensitivity(
                            principal = params.loanAmount,
                            annualRate = params.interestRate,
                            tenureMonths = tenureMonths
                        )
                        insList to sensList
                    }
                    _insights.value = ins
                    _sensitivityPoints.value = sens
                }
        }
    }
}
