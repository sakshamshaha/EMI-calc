package com.example.emicalculatorapp.ui.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.emicalculatorapp.ui.components.GlassCard
import com.example.emicalculatorapp.ui.components.fx.SweepBorderCard
import com.example.emicalculatorapp.ui.theme.CanvasColor
import com.example.emicalculatorapp.ui.theme.ElectricViolet
import com.example.emicalculatorapp.ui.theme.InterFamily
import com.example.emicalculatorapp.ui.theme.LocalNumberFormat
import com.example.emicalculatorapp.ui.theme.LuminousCyan
import com.example.emicalculatorapp.ui.theme.MintGreen
import com.example.emicalculatorapp.ui.theme.SpaceGroteskFamily
import com.example.emicalculatorapp.ui.theme.SurfaceColor
import com.example.emicalculatorapp.ui.theme.TextPrimary
import com.example.emicalculatorapp.ui.theme.TextSecondary
import com.example.emicalculatorapp.viewmodel.EMIViewModel

/**
 * Schedule screen displaying amortization breakdown.
 * Wired directly to the shared EMIViewModel single source of truth.
 * Full interactive timeline, month expanders, and PDF export arrive in Phase 2.
 */
@Composable
fun ScheduleScreen(
    viewModel: EMIViewModel,
    modifier: Modifier = Modifier
) {
    val result by viewModel.result.collectAsStateWithLifecycle()
    val loanAmount by viewModel.loanAmount.collectAsStateWithLifecycle()
    val interestRate by viewModel.interestRate.collectAsStateWithLifecycle()
    val tenureValue by viewModel.tenureValue.collectAsStateWithLifecycle()
    val isYearMode by viewModel.isYearMode.collectAsStateWithLifecycle()
    val numberFormat = LocalNumberFormat.current

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasColor)
            .padding(horizontal = 20.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Screen Header
        Text(
            text = "Amortization Schedule",
            fontFamily = SpaceGroteskFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = TextPrimary
        )
        Text(
            text = "Yearly & monthly payoff distribution for your active loan",
            fontFamily = InterFamily,
            fontSize = 13.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
        )

        // Active Loan Summary Card
        SweepBorderCard(
            modifier = Modifier.fillMaxWidth(),
            gradientColors = listOf(LuminousCyan, ElectricViolet, LuminousCyan)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "MONTHLY INSTALLMENT",
                            fontFamily = SpaceGroteskFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            color = LuminousCyan
                        )
                        Text(
                            text = EMIViewModel.formatCurrency(result.monthlyEMI, numberFormat),
                            fontFamily = SpaceGroteskFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 26.sp,
                            color = TextPrimary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(ElectricViolet.copy(alpha = 0.2f))
                            .border(1.dp, ElectricViolet.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (isYearMode) "$tenureValue Years" else "$tenureValue Months",
                            fontFamily = SpaceGroteskFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Principal", fontFamily = InterFamily, fontSize = 11.sp, color = TextSecondary)
                        Text(
                            text = EMIViewModel.formatCurrency(loanAmount, numberFormat),
                            fontFamily = SpaceGroteskFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                    }
                    Column {
                        Text(text = "Total Interest", fontFamily = InterFamily, fontSize = 11.sp, color = TextSecondary)
                        Text(
                            text = EMIViewModel.formatCurrency(result.totalInterest, numberFormat),
                            fontFamily = SpaceGroteskFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = ElectricViolet
                        )
                    }
                    Column {
                        Text(text = "Annual Rate", fontFamily = InterFamily, fontSize = 11.sp, color = TextSecondary)
                        Text(
                            text = "$interestRate%",
                            fontFamily = SpaceGroteskFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = MintGreen
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Phase 2 Preview Feature Card
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = LuminousCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Interactive Amortization Engine",
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextPrimary,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Phase 2 will unlock full yearly breakdown tables, touch-scrub area charts, month-by-month principal/interest expansion rows, and one-tap paginated PDF report generation.",
                    fontFamily = InterFamily,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(LuminousCyan.copy(alpha = 0.12f))
                        .border(1.dp, LuminousCyan.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "🚀 Arriving in Phase 2",
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = LuminousCyan
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Estimates disclaimer
        Text(
            text = "Estimates only, not financial advice.",
            fontFamily = InterFamily,
            fontSize = 11.sp,
            color = TextSecondary.copy(alpha = 0.6f),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(100.dp))
    }
}
