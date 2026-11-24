package com.example.emicalculatorapp.ui.calculator

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.emicalculatorapp.ui.components.AmbientBackground
import com.example.emicalculatorapp.ui.saved.SaveQuoteBottomSheet
import com.example.emicalculatorapp.ui.theme.EMIverseTheme
import com.example.emicalculatorapp.ui.theme.EMIverseTypography
import com.example.emicalculatorapp.ui.theme.LocalNumberFormat
import com.example.emicalculatorapp.ui.theme.LocalReduceMotion
import com.example.emicalculatorapp.ui.theme.LuminousCyan
import com.example.emicalculatorapp.ui.theme.SurfaceContainerHigh
import com.example.emicalculatorapp.ui.theme.TextPrimary
import com.example.emicalculatorapp.ui.theme.TextSecondary
import com.example.emicalculatorapp.viewmodel.EMIViewModel
import com.example.emicalculatorapp.viewmodel.SavedQuotesState
import kotlinx.coroutines.launch

@Composable
fun CalculatorScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EMIViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    savedQuotesState: SavedQuotesState? = null,
    onNavigateToSaved: () -> Unit = {},
    showTopBar: Boolean = false
) {
    BackHandler { onNavigateBack() }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val reduceMotion = LocalReduceMotion.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var showSaveSheet by remember { mutableStateOf(false) }

    // Observable ViewModel states collected with lifecycle
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val loanAmount by viewModel.loanAmount.collectAsStateWithLifecycle()
    val interestRate by viewModel.interestRate.collectAsStateWithLifecycle()
    val tenureValue by viewModel.tenureValue.collectAsStateWithLifecycle()
    val isYearMode by viewModel.isYearMode.collectAsStateWithLifecycle()
    val result by viewModel.result.collectAsStateWithLifecycle()
    val numberFormat = LocalNumberFormat.current

    // Dragging tracking for OdometerText continuous vs discrete updates
    var isAmountDragging by remember { mutableStateOf(false) }
    var isRateDragging by remember { mutableStateOf(false) }
    var isTenureDragging by remember { mutableStateOf(false) }
    val isAnySliderDragging = isAmountDragging || isRateDragging || isTenureDragging

    // Entrance animation: runs once smoothly on first appearance without replaying
    var hasEntered by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        hasEntered = true
    }
    val contentAlpha by animateFloatAsState(
        targetValue = if (hasEntered || reduceMotion) 1f else 0f,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "ContentAlpha"
    )
    val contentOffsetY by animateFloatAsState(
        targetValue = if (hasEntered || reduceMotion) 0f else 24f,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "ContentOffsetY"
    )

    // Fluid critically damped spring physics for numerical rolling counters & donut arc sweeps
    val numberAnimSpec = if (reduceMotion) {
        tween<Float>(durationMillis = 0)
    } else {
        spring<Float>(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
    }

    val animatedEmi by animateFloatAsState(
        targetValue = result.monthlyEMI.toFloat(),
        animationSpec = numberAnimSpec,
        label = "AnimatedEMI"
    )
    val animatedPrincipal by animateFloatAsState(
        targetValue = loanAmount.toFloat(),
        animationSpec = numberAnimSpec,
        label = "AnimatedPrincipal"
    )
    val animatedInterest by animateFloatAsState(
        targetValue = result.totalInterest.toFloat(),
        animationSpec = numberAnimSpec,
        label = "AnimatedInterest"
    )
    val animatedTotal by animateFloatAsState(
        targetValue = result.totalPayment.toFloat(),
        animationSpec = numberAnimSpec,
        label = "AnimatedTotal"
    )
    val animatedVelocity by animateFloatAsState(
        targetValue = result.yearlyVelocity.toFloat(),
        animationSpec = numberAnimSpec,
        label = "AnimatedVelocity"
    )
    val animatedPrincipalPct by animateFloatAsState(
        targetValue = result.principalSharePercent,
        animationSpec = numberAnimSpec,
        label = "AnimatedPrincipalPct"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
    ) {
        AmbientBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = contentAlpha
                    translationY = contentOffsetY.dp.toPx()
                }
                .verticalScroll(scrollState)
                .padding(horizontal = 18.dp, vertical = 12.dp)
        ) {
            // ================= 1. TOP BAR =================
            if (showTopBar) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
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

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .drawBehind {
                                    drawCircle(
                                        color = LuminousCyan.copy(alpha = 0.5f),
                                        radius = size.maxDimension * 0.8f
                                    )
                                }
                                .clip(CircleShape)
                                .background(LuminousCyan)
                        )
                        Text(
                            text = "EMI Calculator",
                            style = EMIverseTypography.headlineSm,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = { /* Presets / Settings */ },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(SurfaceContainerHigh.copy(alpha = 0.8f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Presets",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // ================= 2. LOAN CATEGORY FILTER PILLS =================
            LoanCategoryPills(
                selectedCategory = selectedCategory,
                onCategorySelected = { viewModel.selectCategory(it) }
            )

            // ================= 3. HERO DONUT RESULT CARD =================
            HeroDonutCard(
                animatedEmi = animatedEmi,
                animatedPrincipalPct = animatedPrincipalPct,
                isYearMode = isYearMode,
                tenureValue = tenureValue,
                isAnySliderDragging = isAnySliderDragging,
                numberFormat = numberFormat
            )

            Spacer(modifier = Modifier.height(14.dp))

            // ================= 4. METRIC MATRIX GRID (2x2) =================
            MetricMatrixGrid(
                animatedPrincipal = animatedPrincipal,
                animatedInterest = animatedInterest,
                animatedTotal = animatedTotal,
                animatedVelocity = animatedVelocity,
                numberFormat = numberFormat
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ================= 5. INTERACTIVE CONTROL CARDS =================
            LoanAmountCard(
                loanAmount = loanAmount,
                onAmountChanged = { viewModel.setLoanAmount(it) },
                onDraggingChanged = { isAmountDragging = it },
                numberFormat = numberFormat,
                focusManager = focusManager
            )

            Spacer(modifier = Modifier.height(12.dp))

            InterestRateCard(
                interestRate = interestRate,
                onRateChanged = { viewModel.setInterestRate(it) },
                onDraggingChanged = { isRateDragging = it },
                focusManager = focusManager
            )

            Spacer(modifier = Modifier.height(12.dp))

            LoanTenureCard(
                tenureValue = tenureValue,
                isYearMode = isYearMode,
                onTenureChanged = { viewModel.setTenureValue(it) },
                onYearModeChanged = { viewModel.setYearMode(it) },
                onDraggingChanged = { isTenureDragging = it },
                focusManager = focusManager
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ================= 6. AMORTIZATION MILESTONES CARD =================
            AmortizationMilestonesCard(
                milestones = result.milestones
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ================= 7. BOTTOM ACTIONS (RESET & SAVE/SHARE) =================
            ShareQuoteSection(
                selectedCategory = selectedCategory,
                loanAmount = loanAmount,
                interestRate = interestRate,
                tenureValue = tenureValue,
                isYearMode = isYearMode,
                result = result,
                numberFormat = numberFormat,
                onReset = { viewModel.resetToHomePreset() },
                onSaveClick = { showSaveSheet = true }
            )
        }

        if (showSaveSheet) {
            SaveQuoteBottomSheet(
                category = selectedCategory,
                principal = loanAmount,
                annualRate = interestRate,
                tenureMonths = if (isYearMode) tenureValue * 12 else tenureValue,
                processingFeePercent = 0.0,
                numberFormat = numberFormat,
                onSaveQuote = { quoteName ->
                    savedQuotesState?.saveQuote(
                        name = quoteName,
                        category = selectedCategory,
                        principal = loanAmount,
                        rate = interestRate,
                        tenureMonths = if (isYearMode) tenureValue * 12 else tenureValue,
                        processingFeePercent = 0.0,
                        onSaved = {
                            coroutineScope.launch {
                                val snackRes = snackbarHostState.showSnackbar(
                                    message = "Quote saved to Vault",
                                    actionLabel = "View",
                                    duration = SnackbarDuration.Short
                                )
                                if (snackRes == SnackbarResult.ActionPerformed) {
                                    onNavigateToSaved()
                                }
                            }
                        }
                    )
                },
                onDismiss = { showSaveSheet = false }
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 85.dp, start = 16.dp, end = 16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF090A15)
@Composable
fun CalculatorScreenPreview() {
    EMIverseTheme {
        CalculatorScreen(onNavigateBack = {})
    }
}
