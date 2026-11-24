package com.example.emicalculatorapp.ui.getstarted

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DonutLarge
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emicalculatorapp.ui.components.AmbientBackground
import com.example.emicalculatorapp.ui.components.GradientPillButton
import com.example.emicalculatorapp.ui.theme.CardBorderGradient
import com.example.emicalculatorapp.ui.theme.DonutInterestGradient
import com.example.emicalculatorapp.ui.theme.DonutPrincipalGradient
import com.example.emicalculatorapp.ui.theme.EMIverseTheme
import com.example.emicalculatorapp.ui.theme.EMIverseTypography
import com.example.emicalculatorapp.ui.theme.ElectricViolet
import com.example.emicalculatorapp.ui.theme.GlassDark65
import com.example.emicalculatorapp.ui.theme.HotPink
import com.example.emicalculatorapp.ui.theme.LocalReduceMotion
import com.example.emicalculatorapp.ui.theme.LuminousCyan
import com.example.emicalculatorapp.ui.theme.NeoEmerald
import com.example.emicalculatorapp.ui.theme.OutlineVariant
import com.example.emicalculatorapp.ui.theme.PrimaryContainer
import com.example.emicalculatorapp.ui.theme.PrimaryLight
import com.example.emicalculatorapp.ui.theme.SecondaryContainer
import com.example.emicalculatorapp.ui.theme.SurfaceColor
import com.example.emicalculatorapp.ui.theme.SurfaceContainer
import com.example.emicalculatorapp.ui.theme.SurfaceContainerHigh
import com.example.emicalculatorapp.ui.theme.SurfaceContainerLowest
import com.example.emicalculatorapp.ui.theme.TertiaryContainer
import com.example.emicalculatorapp.ui.theme.TextMuted
import com.example.emicalculatorapp.ui.theme.TextPrimary
import com.example.emicalculatorapp.ui.theme.TextSecondary
import com.example.emicalculatorapp.viewmodel.EMIViewModel
import kotlin.math.cos
import kotlin.math.sin

/**
 * Screen 1: Get Started Screen.
 * Respects LocalReduceMotion for accessibility.
 */
@Composable
fun GetStartedScreen(
    onNavigateToCalculator: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val reduceMotion = LocalReduceMotion.current
    val haptic = LocalHapticFeedback.current

    // Counter increment animation: counts up from 0 to 42,850 on mount, or shows immediately if reduceMotion
    var countTarget by remember { mutableFloatStateOf(if (reduceMotion) 42850f else 0f) }
    LaunchedEffect(reduceMotion) {
        countTarget = 42850f
    }
    val animatedCount by animateIntAsState(
        targetValue = countTarget.toInt(),
        animationSpec = if (reduceMotion) tween(durationMillis = 0) else tween(durationMillis = 1400, easing = FastOutSlowInEasing),
        label = "EmiCounterAnimation"
    )

    // Sweep arc animation on mount (0 to 1 over 1.2s, read lazily inside draw scope)
    val sweepAnimatable = remember { Animatable(if (reduceMotion) 1f else 0f) }
    LaunchedEffect(reduceMotion) {
        if (!reduceMotion) {
            sweepAnimatable.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing)
            )
        }
    }

    // Infinite transitions for ambient elements
    val infiniteTransition = rememberInfiniteTransition(label = "HeroInfiniteTransition")

    // Slow 55s rotation for dashed orbital track (read lazily in graphicsLayer)
    val orbitRotationState = if (reduceMotion) {
        remember { mutableFloatStateOf(0f) }
    } else {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 55000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "OrbitRotation"
        )
    }

    // 12s rotation for photon satellite dot (read lazily inside Canvas DrawScope)
    val satelliteAngleState = if (reduceMotion) {
        remember { mutableFloatStateOf(45f) }
    } else {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 12000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "SatelliteAngle"
        )
    }

    // 2.4s pulse for live repo dot & button glow (read lazily in drawBehind/glowAlphaProvider)
    val pulseAlphaState = if (reduceMotion) {
        remember { mutableFloatStateOf(1f) }
    } else {
        infiniteTransition.animateFloat(
            initialValue = 0.5f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1200, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "PulseAlpha"
        )
    }

    // Gentle float bobbing for chips (read lazily in graphicsLayer)
    val chipBobYState = if (reduceMotion) {
        remember { mutableFloatStateOf(0f) }
    } else {
        infiniteTransition.animateFloat(
            initialValue = -3f,
            targetValue = 3f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2600, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "ChipBobY"
        )
    }

    // Cached dashed stroke effect to avoid allocation inside draw scope
    val dashedPathEffect = remember { PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f) }

    Box(modifier = modifier.fillMaxSize()) {
        // Dynamic Ambient Photon Field & Grid
        AmbientBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ================= 1. TOP BRANDING BAR =================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo + Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .drawBehind {
                                drawCircle(
                                    color = LuminousCyan.copy(alpha = 0.25f),
                                    radius = size.maxDimension * 0.55f
                                )
                            }
                            .clip(CircleShape)
                            .background(SurfaceContainerHigh)
                            .border(1.dp, LuminousCyan.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AllInclusive,
                            contentDescription = "Logo",
                            tint = LuminousCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    SpanStyle(
                                        brush = Brush.horizontalGradient(
                                            listOf(ElectricViolet, LuminousCyan, HotPink)
                                        ),
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append("EMIverse")
                                }
                            },
                            style = EMIverseTypography.headlineSm
                        )
                        Text(
                            text = "V3.4 QUANTUM",
                            style = EMIverseTypography.labelCaps,
                            color = TextMuted
                        )
                    }
                }

                // Live Repo Status Pill
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(SurfaceContainerHigh.copy(alpha = 0.8f))
                        .border(1.dp, OutlineVariant.copy(alpha = 0.35f), CircleShape)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .drawBehind {
                                drawCircle(
                                    color = NeoEmerald.copy(alpha = pulseAlphaState.value),
                                    radius = size.maxDimension * 0.7f
                                )
                            }
                            .clip(CircleShape)
                            .background(NeoEmerald)
                    )
                    Text(
                        text = "LIVE REPO 6.5%",
                        style = EMIverseTypography.labelCaps,
                        color = TextSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ================= 2. HERO TYPOGRAPHY =================
            // Badge Pill
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(PrimaryLight.copy(alpha = 0.10f))
                    .border(1.dp, PrimaryLight.copy(alpha = 0.25f), CircleShape)
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = PrimaryLight,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "NEXT-GEN FINTECH MODELING",
                    style = EMIverseTypography.labelCaps,
                    color = PrimaryLight,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Headlines
            Text(
                text = "Know Your EMI.",
                style = EMIverseTypography.displayHero,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            brush = Brush.horizontalGradient(
                                listOf(PrimaryLight, LuminousCyan, HotPink)
                            ),
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("Own Your Future.")
                    }
                },
                style = EMIverseTypography.displayHero,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Plan loans smarter with instantaneous quantum amortization and tactile visuals.",
                style = EMIverseTypography.bodyMd,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ================= 3. CENTRAL GLOWING DONUT HERO =================
            val density = LocalDensity.current
            val orbitStroke = remember(density) {
                with(density) {
                    Stroke(width = 1.dp.toPx(), pathEffect = dashedPathEffect)
                }
            }
            val strokeWPx = with(density) { 14.dp.toPx() }
            val paddingPx = with(density) { strokeWPx / 2f + 4.dp.toPx() }
            val trackStroke = remember(strokeWPx) { Stroke(width = strokeWPx) }
            val arcStroke = remember(strokeWPx) { Stroke(width = strokeWPx, cap = StrokeCap.Round) }

            Box(
                modifier = Modifier.size(280.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer Rotating Dashed Orbital Track (55s, read lazily via graphicsLayer)
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { rotationZ = orbitRotationState.value }
                ) {
                    val r = (size.minDimension / 2f) - 6.dp.toPx()
                    drawCircle(
                        color = OutlineVariant.copy(alpha = 0.4f),
                        radius = r,
                        style = orbitStroke
                    )
                }

                // Orbiting Photon Satellite Dot (12s, read lazily in Canvas draw)
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val centerOffset = Offset(size.width / 2f, size.height / 2f)
                    val r = (size.minDimension / 2f) - 6.dp.toPx()
                    val rad = Math.toRadians(satelliteAngleState.value.toDouble())
                    val x = centerOffset.x + (r * cos(rad)).toFloat()
                    val y = centerOffset.y + (r * sin(rad)).toFloat()

                    drawCircle(
                        color = LuminousCyan.copy(alpha = 0.45f),
                        radius = 10.dp.toPx(),
                        center = Offset(x, y)
                    )
                    drawCircle(
                        color = LuminousCyan,
                        radius = 6.dp.toPx(),
                        center = Offset(x, y)
                    )
                    drawCircle(
                        color = SurfaceColor,
                        radius = 2.5.dp.toPx(),
                        center = Offset(x, y)
                    )
                }

                // Multi-Stop Glowing Donut Canvas (read sweep progress lazily)
                Canvas(modifier = Modifier.size(220.dp)) {
                    val arcSize = Size(size.width - paddingPx * 2, size.height - paddingPx * 2)
                    val topLeft = Offset(paddingPx, paddingPx)

                    drawArc(
                        color = SurfaceContainer,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = trackStroke
                    )

                    val sweep = sweepAnimatable.value
                    val principalSweep = 259.2f * sweep
                    drawArc(
                        brush = DonutPrincipalGradient,
                        startAngle = -90f,
                        sweepAngle = principalSweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = arcStroke
                    )

                    val interestSweep = 100.8f * sweep
                    drawArc(
                        brush = DonutInterestGradient,
                        startAngle = -90f + principalSweep + 4f,
                        sweepAngle = (interestSweep - 4f).coerceAtLeast(0f),
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = arcStroke
                    )
                }

                // Center Dynamic Metric Glass Disc
                Box(
                    modifier = Modifier
                        .size(156.dp)
                        .drawBehind {
                            drawCircle(
                                color = ElectricViolet.copy(alpha = 0.15f),
                                radius = size.maxDimension * 0.5f
                            )
                        }
                        .clip(CircleShape)
                        .background(GlassDark65)
                        .border(1.dp, CardBorderGradient, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "ESTIMATED EMI",
                            style = EMIverseTypography.labelCaps,
                            color = TextMuted,
                            letterSpacing = 0.14.sp
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            HeroCounterText(count = animatedCount)
                            Text(
                                text = " /mo",
                                style = EMIverseTypography.labelMd,
                                color = TextMuted,
                                modifier = Modifier.padding(bottom = 3.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Legend Pill
                        Row(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(SurfaceContainerLowest.copy(alpha = 0.85f))
                                .border(1.dp, OutlineVariant.copy(alpha = 0.25f), CircleShape)
                                .padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .clip(CircleShape)
                                        .background(LuminousCyan)
                                )
                                Text(
                                    text = "72% P",
                                    style = EMIverseTypography.labelCaps,
                                    color = TextSecondary,
                                    fontSize = 9.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(10.dp)
                                    .background(OutlineVariant.copy(alpha = 0.4f))
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .clip(CircleShape)
                                        .background(HotPink)
                                )
                                Text(
                                    text = "28% I",
                                    style = EMIverseTypography.labelCaps,
                                    color = TextSecondary,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ================= 4. FLOATING FEATURE CHIPS =================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { translationY = chipBobYState.value.dp.toPx() },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FeatureChip(
                    icon = Icons.Default.Bolt,
                    label = "Instant Results",
                    tag = "0.1s",
                    tint = LuminousCyan
                )

                Spacer(modifier = Modifier.width(8.dp))

                FeatureChip(
                    icon = Icons.Default.DonutLarge,
                    label = "Visual Split",
                    tag = "HD 3D",
                    tint = HotPink
                )

                Spacer(modifier = Modifier.width(8.dp))

                FeatureChip(
                    icon = Icons.Default.Psychology,
                    label = "AI Optimizer",
                    tag = "Smart",
                    tint = ElectricViolet
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ================= 5. CTA BUTTON & STEPPER =================
            GradientPillButton(
                onClick = onNavigateToCalculator,
                modifier = Modifier.fillMaxWidth(),
                gradient = Brush.horizontalGradient(
                    listOf(TertiaryContainer, PrimaryContainer, SecondaryContainer)
                ),
                glowColor = HotPink,
                glowAlphaProvider = { 0.40f * pulseAlphaState.value }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Launch EMIverse",
                        style = EMIverseTypography.headlineSm,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Stepper indicator dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(22.dp)
                        .height(5.dp)
                        .clip(CircleShape)
                        .background(LuminousCyan)
                )
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(OutlineVariant.copy(alpha = 0.6f))
                )
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(OutlineVariant.copy(alpha = 0.6f))
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom Navigation Links
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = LuminousCyan),
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onNavigateToCalculator()
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Custom Baseline",
                        style = EMIverseTypography.labelMd,
                        color = TextSecondary
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = LuminousCyan),
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onNavigateToCalculator()
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Quick Skip",
                        style = EMIverseTypography.labelMd,
                        color = TextMuted
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun HeroCounterText(count: Int) {
    Text(
        text = "₹${EMIViewModel.formatIndianCurrency(count.toDouble()).replace("₹ ", "").replace("₹", "")}",
        style = EMIverseTypography.headlineLg,
        color = TextPrimary
    )
}

@Composable
private fun FeatureChip(
    icon: ImageVector,
    label: String,
    tag: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(SurfaceContainerHigh.copy(alpha = 0.65f))
            .border(1.dp, tint.copy(alpha = 0.3f), CircleShape)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = label,
            style = EMIverseTypography.labelCaps,
            color = TextPrimary,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
        )
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.18f))
                .padding(horizontal = 5.dp, vertical = 1.dp)
        ) {
            Text(
                text = tag,
                style = EMIverseTypography.labelCaps,
                color = tint,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF090A15)
@Composable
fun GetStartedScreenPreview() {
    EMIverseTheme {
        GetStartedScreen(onNavigateToCalculator = {})
    }
}
