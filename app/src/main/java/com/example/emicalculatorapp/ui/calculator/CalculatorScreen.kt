package com.example.emicalculatorapp.ui.calculator

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emicalculatorapp.model.LoanCategory
import com.example.emicalculatorapp.ui.components.AmbientBackground
import com.example.emicalculatorapp.ui.components.ConfettiBurst
import com.example.emicalculatorapp.ui.components.GhostPillButton
import com.example.emicalculatorapp.ui.components.GlassCard
import com.example.emicalculatorapp.ui.components.GradientPillButton
import com.example.emicalculatorapp.ui.components.NeonChip
import com.example.emicalculatorapp.ui.components.QuantumSlider
import com.example.emicalculatorapp.ui.theme.DonutInterestGradient
import com.example.emicalculatorapp.ui.theme.DonutPrincipalGradient
import com.example.emicalculatorapp.ui.theme.EMIverseTheme
import com.example.emicalculatorapp.ui.theme.EMIverseTypography
import com.example.emicalculatorapp.ui.theme.ElectricViolet
import com.example.emicalculatorapp.ui.theme.HeroCtaGradient
import com.example.emicalculatorapp.ui.theme.HotPink
import com.example.emicalculatorapp.ui.theme.LocalReduceMotion
import com.example.emicalculatorapp.ui.theme.LuminousCyan
import com.example.emicalculatorapp.ui.theme.OutlineVariant
import com.example.emicalculatorapp.ui.theme.PrimaryContainer
import com.example.emicalculatorapp.ui.theme.PrimaryLight
import com.example.emicalculatorapp.ui.theme.SecondaryContainer
import com.example.emicalculatorapp.ui.theme.SurfaceContainer
import com.example.emicalculatorapp.ui.theme.SurfaceContainerHigh
import com.example.emicalculatorapp.ui.theme.SurfaceContainerHighest
import com.example.emicalculatorapp.ui.theme.SurfaceContainerLow
import com.example.emicalculatorapp.ui.theme.SurfaceContainerLowest
import com.example.emicalculatorapp.ui.theme.TertiaryContainer
import com.example.emicalculatorapp.ui.theme.TextMuted
import com.example.emicalculatorapp.ui.theme.TextPrimary
import com.example.emicalculatorapp.ui.theme.TextSecondary
import com.example.emicalculatorapp.viewmodel.EMIViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun CalculatorScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EMIViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    showTopBar: Boolean = false
) {
    BackHandler { onNavigateBack() }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val reduceMotion = LocalReduceMotion.current

    // Observable ViewModel states collected with lifecycle
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val loanAmount by viewModel.loanAmount.collectAsStateWithLifecycle()
    val interestRate by viewModel.interestRate.collectAsStateWithLifecycle()
    val tenureValue by viewModel.tenureValue.collectAsStateWithLifecycle()
    val isYearMode by viewModel.isYearMode.collectAsStateWithLifecycle()
    val result by viewModel.result.collectAsStateWithLifecycle()

    val haptic = LocalHapticFeedback.current

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

    // Confetti burst trigger & sharing state
    var showConfetti by remember { mutableStateOf(false) }
    var isSharing by remember { mutableStateOf(false) }

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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CategoryPill(
                    title = "Home",
                    icon = Icons.Default.Home,
                    isSelected = selectedCategory == LoanCategory.HOME,
                    onClick = { viewModel.selectCategory(LoanCategory.HOME) }
                )
                CategoryPill(
                    title = "Car",
                    icon = Icons.Default.DirectionsCar,
                    isSelected = selectedCategory == LoanCategory.CAR,
                    onClick = { viewModel.selectCategory(LoanCategory.CAR) }
                )
                CategoryPill(
                    title = "Personal",
                    icon = Icons.Default.Work,
                    isSelected = selectedCategory == LoanCategory.PERSONAL,
                    onClick = { viewModel.selectCategory(LoanCategory.PERSONAL) }
                )
                CategoryPill(
                    title = "Education",
                    icon = Icons.Default.School,
                    isSelected = selectedCategory == LoanCategory.EDUCATION,
                    onClick = { viewModel.selectCategory(LoanCategory.EDUCATION) }
                )
            }

            // ================= 3. HERO DONUT RESULT CARD =================
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = SurfaceContainerLow.copy(alpha = 0.85f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.size(220.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Spacer(
                            modifier = Modifier
                                .fillMaxSize()
                                .drawWithCache {
                                    val strokeW = 14.dp.toPx()
                                    val pad = strokeW / 2f + 4.dp.toPx()
                                    val arcSize = Size(size.width - pad * 2, size.height - pad * 2)
                                    val topLeft = Offset(pad, pad)
                                    val trackStroke = Stroke(width = strokeW)
                                    val arcStroke = Stroke(width = strokeW, cap = StrokeCap.Round)

                                    onDrawBehind {
                                        // Track
                                        drawArc(
                                            color = SurfaceContainer,
                                            startAngle = 0f,
                                            sweepAngle = 360f,
                                            useCenter = false,
                                            topLeft = topLeft,
                                            size = arcSize,
                                            style = trackStroke
                                        )

                                        // Principal Arc (Violet -> Cyan) - read lazily in draw phase
                                        val pFraction = (animatedPrincipalPct / 100f).coerceIn(0.01f, 0.99f)
                                        val pSweep = 360f * pFraction
                                        drawArc(
                                            brush = DonutPrincipalGradient,
                                            startAngle = -90f,
                                            sweepAngle = pSweep,
                                            useCenter = false,
                                            topLeft = topLeft,
                                            size = arcSize,
                                            style = arcStroke
                                        )

                                        // Interest Arc (Hot Pink)
                                        val iSweep = 360f * (1f - pFraction)
                                        drawArc(
                                            brush = DonutInterestGradient,
                                            startAngle = -90f + pSweep + 3f,
                                            sweepAngle = (iSweep - 3f).coerceAtLeast(0f),
                                            useCenter = false,
                                            topLeft = topLeft,
                                            size = arcSize,
                                            style = arcStroke
                                        )
                                    }
                                }
                        )

                        // Center Counter Cluster
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "MONTHLY REPAYMENT",
                                style = EMIverseTypography.labelCaps,
                                color = LuminousCyan,
                                letterSpacing = 0.12.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "₹",
                                    style = EMIverseTypography.headlineSm,
                                    color = PrimaryLight,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 4.dp, end = 2.dp)
                                )
                                Text(
                                    text = EMIViewModel.formatIndianCurrency(animatedEmi.toDouble()).replace("₹ ", "").replace("₹", ""),
                                    style = EMIverseTypography.displayHero,
                                    color = TextPrimary,
                                    fontSize = 32.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(SurfaceContainerHigh.copy(alpha = 0.8f))
                                    .padding(horizontal = 10.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (isYearMode) "$tenureValue Yrs Tenure" else "$tenureValue Months Tenure",
                                    style = EMIverseTypography.labelCaps,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Legend Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(SurfaceContainerHigh.copy(alpha = 0.6f))
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryLight)
                            )
                            Text(
                                text = "Principal: ${String.format(Locale.US, "%.1f%%", animatedPrincipalPct)}",
                                style = EMIverseTypography.labelCaps,
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Row(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(SurfaceContainerHigh.copy(alpha = 0.6f))
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(LuminousCyan)
                            )
                            Text(
                                text = "Interest: ${String.format(Locale.US, "%.1f%%", (100f - animatedPrincipalPct).coerceAtLeast(0f))}",
                                style = EMIverseTypography.labelCaps,
                                color = LuminousCyan
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ================= 4. METRIC MATRIX GRID (2x2) =================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricTile(
                    modifier = Modifier.weight(1f),
                    title = "PRINCIPAL",
                    value = EMIViewModel.formatIndianCurrency(animatedPrincipal.toDouble()),
                    tag = "Direct Capital",
                    tagColor = PrimaryLight,
                    indicator = {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(PrimaryLight)
                        )
                    }
                )

                MetricTile(
                    modifier = Modifier.weight(1f),
                    title = "TOTAL INTEREST",
                    value = EMIViewModel.formatIndianCurrency(animatedInterest.toDouble()),
                    valueColor = LuminousCyan,
                    tag = "Cost of Credit",
                    tagColor = LuminousCyan,
                    indicator = {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(LuminousCyan)
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricTile(
                    modifier = Modifier.weight(1f),
                    title = "TOTAL PAYABLE",
                    value = EMIViewModel.formatIndianCurrency(animatedTotal.toDouble()),
                    valueColor = HotPink,
                    tag = "Principal + Int",
                    tagColor = TextMuted,
                    indicator = {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = HotPink,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                )

                MetricTile(
                    modifier = Modifier.weight(1f),
                    title = "ANNUAL VELOCITY",
                    value = EMIViewModel.formatIndianCurrency(animatedVelocity.toDouble()),
                    tag = "12x Installments",
                    tagColor = LuminousCyan,
                    indicator = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            tint = LuminousCyan,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ================= 5. INTERACTIVE CONTROL CARDS =================

            // Card 1: Loan Amount
            ControlCard(
                icon = Icons.Default.MonetizationOn,
                iconTint = LuminousCyan,
                title = "Loan Amount"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Principal Amount",
                        style = EMIverseTypography.bodySm,
                        color = TextMuted
                    )

                    // Inset Editable Field with Stable Cursor
                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(SurfaceContainerLowest.copy(alpha = 0.85f))
                            .border(1.dp, OutlineVariant.copy(alpha = 0.3f), CircleShape)
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "₹ ",
                            style = EMIverseTypography.headlineSm,
                            color = PrimaryLight,
                            fontWeight = FontWeight.Bold
                        )

                        var amountFieldValue by remember {
                            val initialFormatted = EMIViewModel.formatIndianCurrency(loanAmount)
                                .replace("₹ ", "").replace("₹", "")
                            mutableStateOf(TextFieldValue(initialFormatted, TextRange(initialFormatted.length)))
                        }

                        // Keep field updated when slider or preset changes externally
                        LaunchedEffect(loanAmount) {
                            val currentParsed = amountFieldValue.text.filter { it.isDigit() }.toDoubleOrNull() ?: 0.0
                            if (currentParsed != loanAmount) {
                                val formatted = EMIViewModel.formatIndianCurrency(loanAmount)
                                    .replace("₹ ", "").replace("₹", "")
                                amountFieldValue = TextFieldValue(formatted, TextRange(formatted.length))
                            }
                        }

                        BasicTextField(
                            value = amountFieldValue,
                            onValueChange = { input ->
                                amountFieldValue = formatAmountWithStableCursor(input) { parsed ->
                                    if (parsed in 100000.0..10000000.0) {
                                        viewModel.setLoanAmount(parsed)
                                    }
                                }
                            },
                            textStyle = EMIverseTypography.headlineSm.copy(
                                color = LuminousCyan,
                                textAlign = TextAlign.End,
                                fontWeight = FontWeight.Bold
                            ),
                            cursorBrush = SolidColor(LuminousCyan),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(onDone = {
                                val clean = amountFieldValue.text.filter { it.isDigit() }.toDoubleOrNull() ?: 100000.0
                                viewModel.setLoanAmount(clean)
                                focusManager.clearFocus()
                            }),
                            modifier = Modifier
                                .width(120.dp)
                                .onFocusChanged { focusState ->
                                    if (!focusState.isFocused) {
                                        val clean = amountFieldValue.text.filter { it.isDigit() }.toDoubleOrNull() ?: 100000.0
                                        viewModel.setLoanAmount(clean)
                                        val formatted = EMIViewModel.formatIndianCurrency(loanAmount)
                                            .replace("₹ ", "").replace("₹", "")
                                        amountFieldValue = TextFieldValue(formatted, TextRange(formatted.length))
                                    }
                                }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Chips Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NeonChip(
                        text = "₹10 L",
                        isSelected = loanAmount == 1000000.0,
                        onClick = { viewModel.setLoanAmount(1000000.0) }
                    )
                    NeonChip(
                        text = "₹25 L",
                        isSelected = loanAmount == 2500000.0,
                        onClick = { viewModel.setLoanAmount(2500000.0) }
                    )
                    NeonChip(
                        text = "₹45 L",
                        isSelected = loanAmount == 4500000.0,
                        onClick = { viewModel.setLoanAmount(4500000.0) }
                    )
                    NeonChip(
                        text = "₹75 L",
                        isSelected = loanAmount == 7500000.0,
                        onClick = { viewModel.setLoanAmount(7500000.0) }
                    )
                    NeonChip(
                        text = "₹1 Cr",
                        isSelected = loanAmount == 10000000.0,
                        onClick = { viewModel.setLoanAmount(10000000.0) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Quantum Slider
                QuantumSlider(
                    value = loanAmount.toFloat(),
                    onValueChange = { viewModel.setLoanAmount(it.toDouble()) },
                    valueRange = 100000f..10000000f,
                    stepHapticThreshold = 50000f,
                    bubbleText = EMIViewModel.formatCompactIndianLakh(loanAmount)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "₹ 1 Lakh", style = EMIverseTypography.labelCaps)
                    Text(text = "₹ 50 Lakhs", style = EMIverseTypography.labelCaps)
                    Text(text = "₹ 1 Crore", style = EMIverseTypography.labelCaps)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Card 2: Interest Rate
            ControlCard(
                icon = Icons.Default.Percent,
                iconTint = HotPink,
                title = "Interest Rate (p.a.)"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Annual Percentage",
                        style = EMIverseTypography.bodySm,
                        color = TextMuted
                    )

                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(SurfaceContainerLowest.copy(alpha = 0.85f))
                            .border(1.dp, OutlineVariant.copy(alpha = 0.3f), CircleShape)
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        var rateFieldValue by remember {
                            val initial = String.format(Locale.US, "%.2f", interestRate)
                            mutableStateOf(TextFieldValue(initial, TextRange(initial.length)))
                        }

                        LaunchedEffect(interestRate) {
                            val currentParsed = rateFieldValue.text.toDoubleOrNull() ?: 0.0
                            if (currentParsed != interestRate) {
                                val formatted = String.format(Locale.US, "%.2f", interestRate)
                                rateFieldValue = TextFieldValue(formatted, TextRange(formatted.length))
                            }
                        }

                        BasicTextField(
                            value = rateFieldValue,
                            onValueChange = { input ->
                                rateFieldValue = formatRateWithStableCursor(input) { parsed ->
                                    if (parsed in 1.0..25.0) {
                                        viewModel.setInterestRate(parsed)
                                    }
                                }
                            },
                            textStyle = EMIverseTypography.headlineSm.copy(
                                color = HotPink,
                                textAlign = TextAlign.End,
                                fontWeight = FontWeight.Bold
                            ),
                            cursorBrush = SolidColor(HotPink),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(onDone = {
                                val clean = rateFieldValue.text.toDoubleOrNull() ?: 8.55
                                viewModel.setInterestRate(clean)
                                focusManager.clearFocus()
                            }),
                            modifier = Modifier
                                .width(70.dp)
                                .onFocusChanged { focusState ->
                                    if (!focusState.isFocused) {
                                        val clean = rateFieldValue.text.toDoubleOrNull() ?: 8.55
                                        viewModel.setInterestRate(clean)
                                        val formatted = String.format(Locale.US, "%.2f", interestRate)
                                        rateFieldValue = TextFieldValue(formatted, TextRange(formatted.length))
                                    }
                                }
                        )

                        Text(
                            text = " %",
                            style = EMIverseTypography.headlineSm,
                            color = HotPink,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Rate Quick Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NeonChip(
                        text = "7.50%",
                        isSelected = String.format(Locale.US, "%.2f", interestRate) == "7.50",
                        onClick = { viewModel.setInterestRate(7.50) },
                        selectedGradient = Brush.horizontalGradient(listOf(HotPink, TertiaryContainer))
                    )
                    NeonChip(
                        text = "8.55%",
                        isSelected = String.format(Locale.US, "%.2f", interestRate) == "8.55",
                        onClick = { viewModel.setInterestRate(8.55) },
                        selectedGradient = Brush.horizontalGradient(listOf(HotPink, TertiaryContainer))
                    )
                    NeonChip(
                        text = "9.25%",
                        isSelected = String.format(Locale.US, "%.2f", interestRate) == "9.25",
                        onClick = { viewModel.setInterestRate(9.25) },
                        selectedGradient = Brush.horizontalGradient(listOf(HotPink, TertiaryContainer))
                    )
                    NeonChip(
                        text = "11.50%",
                        isSelected = String.format(Locale.US, "%.2f", interestRate) == "11.50",
                        onClick = { viewModel.setInterestRate(11.50) },
                        selectedGradient = Brush.horizontalGradient(listOf(HotPink, TertiaryContainer))
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                QuantumSlider(
                    value = interestRate.toFloat(),
                    onValueChange = { viewModel.setInterestRate(it.toDouble()) },
                    valueRange = 4.0f..20.0f,
                    stepHapticThreshold = 0.05f,
                    bubbleText = String.format(Locale.US, "%.2f%%", interestRate),
                    activeGradient = Brush.horizontalGradient(listOf(HotPink, TertiaryContainer)),
                    ringColor = HotPink
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "4.0%", style = EMIverseTypography.labelCaps)
                    Text(text = "12.0%", style = EMIverseTypography.labelCaps)
                    Text(text = "20.0%", style = EMIverseTypography.labelCaps)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Card 3: Loan Tenure
            ControlCard(
                icon = Icons.Default.Schedule,
                iconTint = PrimaryLight,
                title = "Loan Tenure"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Segmented Toggle (Yr / Mo)
                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(SurfaceContainerLowest.copy(alpha = 0.85f))
                            .padding(3.dp),
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        SegmentToggleItem(
                            label = "Yr",
                            isActive = isYearMode,
                            onClick = { viewModel.setYearMode(true) }
                        )
                        SegmentToggleItem(
                            label = "Mo",
                            isActive = !isYearMode,
                            onClick = { viewModel.setYearMode(false) }
                        )
                    }

                    // Inset Editable Field with Stable Cursor
                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(SurfaceContainerLowest.copy(alpha = 0.85f))
                            .border(1.dp, OutlineVariant.copy(alpha = 0.3f), CircleShape)
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        var tenureFieldValue by remember {
                            val initial = tenureValue.toString()
                            mutableStateOf(TextFieldValue(initial, TextRange(initial.length)))
                        }

                        LaunchedEffect(tenureValue) {
                            if (tenureFieldValue.text.toIntOrNull() != tenureValue) {
                                val s = tenureValue.toString()
                                tenureFieldValue = TextFieldValue(s, TextRange(s.length))
                            }
                        }

                        val maxTenure = if (isYearMode) 30 else 360

                        BasicTextField(
                            value = tenureFieldValue,
                            onValueChange = { input ->
                                tenureFieldValue = formatTenureWithStableCursor(input, maxTenure) { parsed ->
                                    if (parsed in 1..maxTenure) {
                                        viewModel.setTenureValue(parsed)
                                    }
                                }
                            },
                            textStyle = EMIverseTypography.headlineSm.copy(
                                color = PrimaryLight,
                                textAlign = TextAlign.End,
                                fontWeight = FontWeight.Bold
                            ),
                            cursorBrush = SolidColor(PrimaryLight),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(onDone = {
                                val clean = tenureFieldValue.text.toIntOrNull() ?: if (isYearMode) 20 else 240
                                viewModel.setTenureValue(clean)
                                focusManager.clearFocus()
                            }),
                            modifier = Modifier
                                .width(50.dp)
                                .onFocusChanged { focusState ->
                                    if (!focusState.isFocused) {
                                        val clean = tenureFieldValue.text.toIntOrNull() ?: if (isYearMode) 20 else 240
                                        viewModel.setTenureValue(clean)
                                        val s = tenureValue.toString()
                                        tenureFieldValue = TextFieldValue(s, TextRange(s.length))
                                    }
                                }
                        )

                        Text(
                            text = if (isYearMode) " Yrs" else " Mo",
                            style = EMIverseTypography.headlineSm,
                            color = PrimaryLight,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val quickPresets = if (isYearMode) {
                        listOf(5 to "5 Yrs", 10 to "10 Yrs", 15 to "15 Yrs", 20 to "20 Yrs", 30 to "30 Yrs")
                    } else {
                        listOf(60 to "60 Mo", 120 to "120 Mo", 180 to "180 Mo", 240 to "240 Mo", 360 to "360 Mo")
                    }
                    quickPresets.forEach { (value, label) ->
                        NeonChip(
                            text = label,
                            isSelected = tenureValue == value,
                            onClick = { viewModel.setTenureValue(value) },
                            selectedGradient = Brush.horizontalGradient(listOf(ElectricViolet, PrimaryContainer))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                val maxRange = if (isYearMode) 30f else 360f
                QuantumSlider(
                    value = tenureValue.toFloat(),
                    onValueChange = { viewModel.setTenureValue(it.roundToInt()) },
                    valueRange = 1f..maxRange,
                    stepHapticThreshold = 1f,
                    bubbleText = if (isYearMode) "$tenureValue Yrs" else "$tenureValue Mo",
                    activeGradient = Brush.horizontalGradient(listOf(ElectricViolet, PrimaryContainer)),
                    ringColor = PrimaryLight
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = if (isYearMode) "1 Yr" else "12 Mo", style = EMIverseTypography.labelCaps)
                    Text(text = if (isYearMode) "15 Yrs" else "180 Mo", style = EMIverseTypography.labelCaps)
                    Text(text = if (isYearMode) "30 Yrs" else "360 Mo", style = EMIverseTypography.labelCaps)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ================= 6. AMORTIZATION MILESTONES CARD =================
            var isAmortizationExpanded by rememberSaveable { mutableStateOf(true) }
            val chevronRotation by animateFloatAsState(
                targetValue = if (isAmortizationExpanded) 180f else 0f,
                animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
                label = "ChevronRotation"
            )

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = SurfaceContainerLow.copy(alpha = 0.85f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                isAmortizationExpanded = !isAmortizationExpanded
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Analytics,
                                contentDescription = null,
                                tint = LuminousCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Amortization Milestones",
                                style = EMIverseTypography.headlineSm
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(SecondaryContainer.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "Payoff Horizon",
                                    style = EMIverseTypography.labelCaps,
                                    color = LuminousCyan
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = if (isAmortizationExpanded) "Collapse" else "Expand",
                                tint = TextSecondary,
                                modifier = Modifier
                                    .size(20.dp)
                                    .rotate(chevronRotation)
                            )
                        }
                    }

                    // Dynamic Milestones List (smoothly expands and collapses)
                    AnimatedVisibility(
                        visible = isAmortizationExpanded,
                        enter = expandVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        ) + fadeIn(animationSpec = tween(200)),
                        exit = shrinkVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        ) + fadeOut(animationSpec = tween(200))
                    ) {
                        Column(
                            modifier = Modifier.padding(top = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            result.milestones.forEach { milestone ->
                                MilestoneRow(milestone = milestone)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ================= 7. BOTTOM ACTIONS (RESET & SAVE/SHARE) =================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ghost Reset Button
                GhostPillButton(
                    onClick = { viewModel.resetToHomePreset() },
                    modifier = Modifier.width(100.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = "Reset",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Reset",
                        style = EMIverseTypography.labelMd,
                        color = TextSecondary
                    )
                }

                // Gradient Save & Share Quote Button
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    GradientPillButton(
                        onClick = {
                            if (!isSharing) {
                                isSharing = true
                                showConfetti = true

                                coroutineScope.launch {
                                    // Let the confetti play for 700ms without blocking UI
                                    delay(700)

                                    val tenureStr = if (isYearMode) "$tenureValue Years" else "$tenureValue Months"
                                    val shareText = """
                                        🚀 EMIverse Loan Quote Summary
                                        ---------------------------------
                                        • Category: ${selectedCategory.title} Loan
                                        • Loan Amount: ${EMIViewModel.formatIndianCurrency(loanAmount)}
                                        • Interest Rate: ${String.format(Locale.US, "%.2f%%", interestRate)} p.a.
                                        • Tenure: $tenureStr
                                        
                                        💎 Monthly EMI: ${EMIViewModel.formatIndianCurrency(result.monthlyEMI)}
                                        📊 Total Interest: ${EMIViewModel.formatIndianCurrency(result.totalInterest)}
                                        💰 Total Payable: ${EMIViewModel.formatIndianCurrency(result.totalPayment)}
                                        
                                        Generated with EMIverse Quantum Engine
                                    """.trimIndent()

                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                        type = "text/plain"
                                    }
                                    val shareIntent = Intent.createChooser(sendIntent, "Share EMIverse Quote")
                                    context.startActivity(shareIntent)
                                    isSharing = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        gradient = Brush.horizontalGradient(
                            listOf(TertiaryContainer, PrimaryContainer, SecondaryContainer)
                        ),
                        glowColor = HotPink.copy(alpha = 0.45f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.IosShare,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Save & Share Quote",
                            style = EMIverseTypography.labelLg,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Confetti Particle Burst Anchor
                    ConfettiBurst(
                        trigger = showConfetti,
                        onComplete = { showConfetti = false }
                    )
                }
            }
        }
    }
}

/**
 * Formats amount with stable cursor preservation so cursor never jumps while typing digits.
 */
private fun formatAmountWithStableCursor(
    input: TextFieldValue,
    onAmountParsed: (Double) -> Unit
): TextFieldValue {
    val rawText = input.text
    val cursorPos = input.selection.start
    val digitsBeforeCursor = rawText.take(cursorPos).count { it.isDigit() }
    val cleanDigits = rawText.filter { it.isDigit() }.take(9)

    if (cleanDigits.isEmpty()) {
        onAmountParsed(0.0)
        return TextFieldValue("", TextRange(0))
    }

    val parsed = cleanDigits.toDoubleOrNull() ?: 0.0
    onAmountParsed(parsed)

    val formatted = EMIViewModel.formatIndianCurrency(parsed)
        .replace("₹ ", "").replace("₹", "")

    var targetCursor = 0
    var digitCount = 0
    for (i in formatted.indices) {
        if (digitCount == digitsBeforeCursor) {
            targetCursor = i
            break
        }
        if (formatted[i].isDigit()) {
            digitCount++
        }
        if (digitCount == digitsBeforeCursor) {
            targetCursor = i + 1
            break
        }
    }
    if (digitCount < digitsBeforeCursor) {
        targetCursor = formatted.length
    }

    return TextFieldValue(formatted, TextRange(targetCursor))
}

/**
 * Formats interest rate with stable cursor preservation.
 */
private fun formatRateWithStableCursor(
    input: TextFieldValue,
    onRateParsed: (Double) -> Unit
): TextFieldValue {
    val rawText = input.text
    val cursorPos = input.selection.start
    var hasDot = false
    val sb = StringBuilder()
    var validCharsBeforeCursor = 0

    for (i in rawText.indices) {
        val c = rawText[i]
        if (c.isDigit()) {
            sb.append(c)
            if (i < cursorPos) validCharsBeforeCursor++
        } else if (c == '.' && !hasDot) {
            sb.append(c)
            hasDot = true
            if (i < cursorPos) validCharsBeforeCursor++
        }
    }
    val cleanStr = sb.toString().take(5)
    val parsed = cleanStr.toDoubleOrNull()
    if (parsed != null) {
        onRateParsed(parsed)
    }
    val newCursor = validCharsBeforeCursor.coerceIn(0, cleanStr.length)
    return TextFieldValue(cleanStr, TextRange(newCursor))
}

/**
 * Formats tenure with stable cursor preservation.
 */
private fun formatTenureWithStableCursor(
    input: TextFieldValue,
    maxVal: Int,
    onTenureParsed: (Int) -> Unit
): TextFieldValue {
    val rawText = input.text
    val cursorPos = input.selection.start
    val digitsBeforeCursor = rawText.take(cursorPos).count { it.isDigit() }
    val cleanDigits = rawText.filter { it.isDigit() }.take(3)
    val parsed = cleanDigits.toIntOrNull()
    if (parsed != null) {
        onTenureParsed(parsed.coerceAtMost(maxVal))
    }
    val newCursor = digitsBeforeCursor.coerceIn(0, cleanDigits.length)
    return TextFieldValue(cleanDigits, TextRange(newCursor))
}

@Composable
private fun CategoryPill(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val reduceMotion = LocalReduceMotion.current
    val haptic = LocalHapticFeedback.current

    val scale by animateFloatAsState(
        targetValue = when {
            isPressed && !reduceMotion -> 0.94f
            isSelected && !reduceMotion -> 1.04f
            else -> 1.0f
        },
        animationSpec = if (reduceMotion) tween(0) else spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "PillScale"
    )

    Row(
        modifier = Modifier
            .scale(scale)
            .then(
                if (isSelected) {
                    Modifier.drawBehind {
                        drawCircle(
                            color = HotPink.copy(alpha = 0.4f),
                            radius = size.maxDimension * 0.45f
                        )
                    }
                } else {
                    Modifier
                }
            )
            .clip(CircleShape)
            .then(
                if (isSelected) {
                    Modifier.background(HeroCtaGradient)
                } else {
                    Modifier
                        .background(SurfaceContainerHigh.copy(alpha = 0.7f))
                        .border(1.dp, Color.White.copy(alpha = 0.12f), CircleShape)
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = LuminousCyan),
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onClick()
                }
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isSelected) Color.White else TextSecondary,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = title,
            style = EMIverseTypography.labelMd,
            color = if (isSelected) Color.White else TextSecondary,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun MetricTile(
    title: String,
    value: String,
    tag: String,
    tagColor: Color,
    indicator: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    valueColor: Color = TextPrimary
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceContainerLow.copy(alpha = 0.8f))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = EMIverseTypography.labelCaps,
                    color = TextMuted,
                    fontSize = 9.sp
                )
                indicator()
            }

            Text(
                text = value,
                style = EMIverseTypography.headlineSm,
                color = valueColor,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )

            Text(
                text = tag,
                style = EMIverseTypography.labelCaps,
                color = tagColor,
                fontSize = 9.sp
            )
        }
    }
}

@Composable
private fun ControlCard(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    content: @Composable () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = SurfaceContainer.copy(alpha = 0.75f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = title,
                    style = EMIverseTypography.labelLg,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun SegmentToggleItem(
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .then(
                if (isActive) {
                    Modifier
                        .background(PrimaryLight)
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                } else {
                    Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = PrimaryLight),
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onClick()
                            }
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = EMIverseTypography.labelCaps,
            color = if (isActive) Color(0xFF3C0091) else TextSecondary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun MilestoneRow(milestone: com.example.emicalculatorapp.model.AmortizationMilestone) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceContainerHigh.copy(alpha = 0.4f))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = milestone.label,
                style = EMIverseTypography.bodySm,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = "Balance: ${EMIViewModel.formatCompactIndianLakh(milestone.remainingBalance)}",
                style = EMIverseTypography.bodySm,
                color = LuminousCyan,
                fontWeight = FontWeight.Bold
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
                .background(SurfaceContainerHighest)
        ) {
            val pWeight = (milestone.principalPercentInYear / 100f).coerceIn(0.02f, 0.98f)
            Box(
                modifier = Modifier
                    .weight(pWeight)
                    .fillMaxSize()
                    .background(PrimaryLight)
            )
            Box(
                modifier = Modifier
                    .weight(1f - pWeight)
                    .fillMaxSize()
                    .background(LuminousCyan)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Principal ${EMIViewModel.formatCompactIndianLakh(milestone.principalPaidSoFar)} (${milestone.principalPercentInYear.roundToInt()}%)",
                style = EMIverseTypography.labelCaps,
                color = TextMuted,
                fontSize = 9.sp
            )
            Text(
                text = "Interest ${EMIViewModel.formatCompactIndianLakh(milestone.interestPaidSoFar)} (${milestone.interestPercentInYear.roundToInt()}%)",
                style = EMIverseTypography.labelCaps,
                color = TextMuted,
                fontSize = 9.sp
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF090A15)
@Composable
fun CalculatorScreenPreview() {
    EMIverseTheme {
        CalculatorScreen(onNavigateBack = {})
    }
}
