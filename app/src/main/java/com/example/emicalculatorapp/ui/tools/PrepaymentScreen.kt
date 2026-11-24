package com.example.emicalculatorapp.ui.tools

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.emicalculatorapp.data.settings.EffectsLevel
import com.example.emicalculatorapp.ui.components.AmbientBackground
import com.example.emicalculatorapp.ui.components.ConfettiBurst
import com.example.emicalculatorapp.ui.components.fx.CelebrationThresholdTracker
import com.example.emicalculatorapp.ui.components.fx.ShineButton
import com.example.emicalculatorapp.ui.theme.LocalEffectsLevel
import com.example.emicalculatorapp.ui.theme.LocalNumberFormat
import com.example.emicalculatorapp.ui.theme.LocalReduceMotion
import com.example.emicalculatorapp.ui.theme.MintGreen
import com.example.emicalculatorapp.ui.theme.SpaceGroteskFamily
import com.example.emicalculatorapp.ui.theme.SurfaceBright
import com.example.emicalculatorapp.ui.theme.SurfaceContainerHigh
import com.example.emicalculatorapp.ui.theme.TextPrimary
import com.example.emicalculatorapp.ui.theme.TextSecondary
import com.example.emicalculatorapp.viewmodel.EMIViewModel

@Composable
fun PrepaymentScreen(
    onNavigateBack: () -> Unit,
    viewModel: EMIViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    val loanAmount by viewModel.loanAmount.collectAsStateWithLifecycle()
    val interestRate by viewModel.interestRate.collectAsStateWithLifecycle()
    val tenureValue by viewModel.tenureValue.collectAsStateWithLifecycle()
    val isYearMode by viewModel.isYearMode.collectAsStateWithLifecycle()
    val params by viewModel.prepaymentParams.collectAsStateWithLifecycle()
    val result by viewModel.prepaymentResult.collectAsStateWithLifecycle()
    val numberFormat = LocalNumberFormat.current

    var isDraggingSlider by remember { mutableStateOf(false) }

    val totalMonths = if (isYearMode) tenureValue * 12 else tenureValue

    val celebrationTracker = remember { CelebrationThresholdTracker(threshold = 60) }
    var showConfetti by remember { mutableStateOf(false) }
    val reduceMotion = LocalReduceMotion.current
    val effectsLevel = LocalEffectsLevel.current

    LaunchedEffect(result.monthsSaved) {
        if (!reduceMotion && effectsLevel != EffectsLevel.LITE && celebrationTracker.update(result.monthsSaved)) {
            showConfetti = true
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
    ) {
        AmbientBackground(scrollOffset = scrollState.value.toFloat())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ================= TOP BAR =================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SurfaceContainerHigh.copy(alpha = 0.8f))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(modifier = Modifier.padding(start = 14.dp)) {
                    Text(
                        text = "Prepayment Simulator",
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "Base: ${EMIViewModel.formatCompactCurrency(loanAmount, numberFormat)} @ ${String.format(java.util.Locale.US, "%.2f%%", interestRate)} for ${if (isYearMode) "$tenureValue Yrs" else "$tenureValue Mo"}",
                        fontFamily = SpaceGroteskFamily,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            // ================= 1. HERO ACCELERATOR CARD =================
            PrepaymentHeroCard(
                result = result,
                numberFormat = numberFormat,
                isContinuousDrag = isDraggingSlider
            )

            // ================= 2. DUAL-LINE TRAJECTORY CHART =================
            PrepaymentComparisonChart(
                points = result.chartPoints,
                numberFormat = numberFormat
            )

            // ================= 3. ACCELERATION MILESTONES =================
            PrepaymentMilestonesCard(
                milestones = result.milestones
            )

            // ================= 4. INTERACTIVE CONTROLS =================
            PrepaymentInputCards(
                params = params,
                maxTenureMonths = totalMonths,
                numberFormat = numberFormat,
                onExtraMonthlyChanged = { viewModel.setExtraMonthly(it) },
                onLumpSumAmountChanged = { viewModel.setLumpSumAmount(it) },
                onLumpSumMonthChanged = { viewModel.setLumpSumMonth(it) },
                onLumpSumEnabledChanged = { viewModel.setLumpSumEnabled(it) },
                onStepUpPercentChanged = { viewModel.setStepUpPercent(it) },
                onStepUpEnabledChanged = { viewModel.setStepUpEnabled(it) },
                onStepUpModeChanged = { viewModel.setStepUpMode(it) },
                onDraggingChanged = { isDraggingSlider = it },
                focusManager = focusManager
            )

            // ================= 5. RESET ACTIONS =================
            ShineButton(
                text = "Reset All Prepayments",
                onClick = { viewModel.resetPrepayment() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 100.dp),
                gradientBrush = Brush.horizontalGradient(listOf(SurfaceContainerHigh, SurfaceBright))
            )
        }

        if (showConfetti) {
            ConfettiBurst(
                trigger = true,
                onComplete = { showConfetti = false },
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
