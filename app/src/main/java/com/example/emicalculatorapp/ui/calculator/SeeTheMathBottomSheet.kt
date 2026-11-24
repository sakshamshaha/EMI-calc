package com.example.emicalculatorapp.ui.calculator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emicalculatorapp.data.settings.EffectsLevel
import com.example.emicalculatorapp.data.settings.NumberFormatStyle
import com.example.emicalculatorapp.model.MathBreakdown
import com.example.emicalculatorapp.ui.components.fx.OdometerText
import com.example.emicalculatorapp.ui.components.fx.ShineButton
import com.example.emicalculatorapp.ui.theme.CanvasColor
import com.example.emicalculatorapp.ui.theme.ElectricViolet
import com.example.emicalculatorapp.ui.theme.InterFamily
import com.example.emicalculatorapp.ui.theme.LocalEffectsLevel
import com.example.emicalculatorapp.ui.theme.LocalReduceMotion
import com.example.emicalculatorapp.ui.theme.LuminousCyan
import com.example.emicalculatorapp.ui.theme.MintGreen
import com.example.emicalculatorapp.ui.theme.SpaceGroteskFamily
import com.example.emicalculatorapp.ui.theme.SurfaceContainerHigh
import com.example.emicalculatorapp.ui.theme.SurfaceContainerLowest
import com.example.emicalculatorapp.ui.theme.TextMuted
import com.example.emicalculatorapp.ui.theme.TextPrimary
import com.example.emicalculatorapp.ui.theme.TextSecondary
import com.example.emicalculatorapp.viewmodel.EMIViewModel
import kotlinx.coroutines.delay
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeeTheMathBottomSheet(
    breakdown: MathBreakdown,
    numberFormat: NumberFormatStyle,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val effectsLevel = LocalEffectsLevel.current
    val reduceMotion = LocalReduceMotion.current
    val isInstant = reduceMotion || effectsLevel == EffectsLevel.LITE

    var replayKey by remember { mutableIntStateOf(0) }
    var visibleStepCount by remember { mutableIntStateOf(if (isInstant) 7 else 0) }

    LaunchedEffect(replayKey) {
        if (isInstant) {
            visibleStepCount = 7
        } else {
            visibleStepCount = 0
            for (step in 1..7) {
                delay(250L)
                visibleStepCount = step
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CanvasColor,
        dragHandle = null,
        modifier = modifier
    ) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp)
                .verticalScroll(scrollState)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "See the Math",
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "Step-by-step mathematical amortization proof",
                        fontFamily = InterFamily,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { replayKey++ }, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.Replay,
                            contentDescription = "Replay Animation",
                            tint = LuminousCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Formula Card with Superscript
            FormulaHeaderCard(isZeroRate = breakdown.isZeroRate)

            Spacer(modifier = Modifier.height(16.dp))

            // Step 1: Inputs
            StepRow(
                stepNumber = 1,
                title = "Loan Parameters",
                visible = visibleStepCount >= 1,
                content = "P = ${EMIViewModel.formatCurrency(breakdown.principal, numberFormat)}, Rate = ${String.format(Locale.US, "%.2f%%", breakdown.annualRate)} p.a., n = ${breakdown.tenureMonths} months"
            )

            // Step 2: Monthly Rate
            StepRow(
                stepNumber = 2,
                title = "Monthly Interest Rate (r)",
                visible = visibleStepCount >= 2,
                content = if (breakdown.isZeroRate) "r = 0.0 (Interest Free)" else "r = ${breakdown.annualRate} / 12 / 100 = ${String.format(Locale.US, "%.6f", breakdown.monthlyRate)}"
            )

            if (!breakdown.isZeroRate) {
                // Step 3: Growth Factor (1 + r)^n
                StepRow(
                    stepNumber = 3,
                    title = "Growth Compounding Factor (1 + r)ⁿ",
                    visible = visibleStepCount >= 3,
                    content = "(1 + ${String.format(Locale.US, "%.6f", breakdown.monthlyRate)})^${breakdown.tenureMonths} = ${String.format(Locale.US, "%.5f", breakdown.growthFactor)}"
                )

                // Step 4: Numerator & Denominator
                StepRow(
                    stepNumber = 4,
                    title = "Numerator & Denominator",
                    visible = visibleStepCount >= 4,
                    content = "Numerator: ${String.format(Locale.US, "%,.2f", breakdown.numerator)}\nDenominator: ${String.format(Locale.US, "%.5f", breakdown.denominator)}"
                )
            }

            // Step 5: Monthly EMI
            AnimatedVisibility(
                visible = visibleStepCount >= 5,
                enter = fadeIn(tween(250)) + slideInVertically(tween(250)) { it / 2 }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceContainerLowest)
                        .border(1.dp, LuminousCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            StepBadge(stepNumber = 5)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Monthly Installment (EMI)",
                                fontFamily = SpaceGroteskFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = LuminousCyan
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (breakdown.isZeroRate) "EMI = P / n = ${EMIViewModel.formatCurrency(breakdown.emi, numberFormat)}" else "EMI = Numerator / Denominator",
                            fontFamily = InterFamily,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OdometerText(
                            text = EMIViewModel.formatCurrency(breakdown.emi, numberFormat),
                            isContinuous = false,
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontFamily = SpaceGroteskFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = TextPrimary
                            ),
                            color = TextPrimary
                        )
                    }
                }
            }

            // Step 6: Month-1 Split
            StepRow(
                stepNumber = 6,
                title = "First Month Allocation",
                visible = visibleStepCount >= 6,
                content = "Interest (P × r): ${EMIViewModel.formatCurrency(breakdown.month1Interest, numberFormat)}\nPrincipal (EMI - Interest): ${EMIViewModel.formatCurrency(breakdown.month1Principal, numberFormat)}"
            )

            // Step 7: Total Outlay
            StepRow(
                stepNumber = 7,
                title = "Total Lifecycle Cost",
                visible = visibleStepCount >= 7,
                content = "Total Payment (EMI × n): ${EMIViewModel.formatCurrency(breakdown.totalPayment, numberFormat)}\nTotal Interest: ${EMIViewModel.formatCurrency(breakdown.totalInterest, numberFormat)}"
            )

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun FormulaHeaderCard(isZeroRate: Boolean) {
    val formulaAnnotated = remember(isZeroRate) {
        if (isZeroRate) {
            buildAnnotatedString {
                append("EMI = P / n")
            }
        } else {
            buildAnnotatedString {
                append("EMI = P × r × (1 + r)")
                pushStyle(SpanStyle(baselineShift = BaselineShift.Superscript, fontSize = 10.sp))
                append("n")
                pop()
                append(" / [ (1 + r)")
                pushStyle(SpanStyle(baselineShift = BaselineShift.Superscript, fontSize = 10.sp))
                append("n")
                pop()
                append(" - 1 ]")
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceContainerLowest.copy(alpha = 0.8f))
            .border(1.dp, ElectricViolet.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Column {
            Text(
                text = if (isZeroRate) "ZERO-INTEREST FORMULA" else "STANDARD REDUCING FORMULA",
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = ElectricViolet,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = formulaAnnotated,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun StepRow(
    stepNumber: Int,
    title: String,
    visible: Boolean,
    content: String
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(250)) + slideInVertically(tween(250)) { it / 2 }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(SurfaceContainerLowest.copy(alpha = 0.5f))
                .border(0.5.dp, SurfaceContainerHigh, RoundedCornerShape(10.dp))
                .padding(12.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StepBadge(stepNumber = stepNumber)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = content,
                    fontFamily = InterFamily,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun StepBadge(stepNumber: Int) {
    Box(
        modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(LuminousCyan.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$stepNumber",
            fontFamily = SpaceGroteskFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = LuminousCyan
        )
    }
}
