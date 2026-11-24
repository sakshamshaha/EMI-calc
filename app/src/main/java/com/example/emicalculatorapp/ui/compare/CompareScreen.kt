package com.example.emicalculatorapp.ui.compare

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
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.emicalculatorapp.ui.components.GlassCard
import com.example.emicalculatorapp.ui.components.fx.SweepBorderCard
import com.example.emicalculatorapp.ui.theme.CanvasColor
import com.example.emicalculatorapp.ui.theme.ElectricViolet
import com.example.emicalculatorapp.ui.theme.HotPink
import com.example.emicalculatorapp.ui.theme.InterFamily
import com.example.emicalculatorapp.ui.theme.LocalNumberFormat
import com.example.emicalculatorapp.ui.theme.LuminousCyan
import com.example.emicalculatorapp.ui.theme.SpaceGroteskFamily
import com.example.emicalculatorapp.ui.theme.TextPrimary
import com.example.emicalculatorapp.ui.theme.TextSecondary
import com.example.emicalculatorapp.viewmodel.EMIViewModel

/**
 * Loan comparison screen stub.
 * Shares the single EMIViewModel loan state as Baseline Loan A.
 * Full side-by-side comparison, differential graphs, and winner highlight arrive in Phase 4.
 */
@Composable
fun CompareScreen(
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

        Text(
            text = "Compare Loans",
            fontFamily = SpaceGroteskFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = TextPrimary
        )
        Text(
            text = "Evaluate two loan options side-by-side to find the lowest total payout",
            fontFamily = InterFamily,
            fontSize = 13.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
        )

        // Baseline Loan A Card (fed from active calculator state)
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LOAN A (ACTIVE BASELINE)",
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = LuminousCyan
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(LuminousCyan.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Current State",
                            fontFamily = SpaceGroteskFamily,
                            fontSize = 11.sp,
                            color = LuminousCyan
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Amount", fontFamily = InterFamily, fontSize = 11.sp, color = TextSecondary)
                        Text(
                            text = EMIViewModel.formatCurrency(loanAmount, numberFormat),
                            fontFamily = SpaceGroteskFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                    }
                    Column {
                        Text(text = "Rate / Tenure", fontFamily = InterFamily, fontSize = 11.sp, color = TextSecondary)
                        Text(
                            text = "$interestRate% • ${if (isYearMode) "$tenureValue Y" else "$tenureValue M"}",
                            fontFamily = SpaceGroteskFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                    }
                    Column {
                        Text(text = "Monthly EMI", fontFamily = InterFamily, fontSize = 11.sp, color = TextSecondary)
                        Text(
                            text = EMIViewModel.formatCurrency(result.monthlyEMI, numberFormat),
                            fontFamily = SpaceGroteskFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = LuminousCyan
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Phase 4 Comparison Engine Card
        SweepBorderCard(
            modifier = Modifier.fillMaxWidth(),
            gradientColors = listOf(HotPink, ElectricViolet, LuminousCyan, HotPink)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.CompareArrows,
                        contentDescription = null,
                        tint = HotPink,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Side-by-Side Duel Engine",
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextPrimary,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Phase 4 unlocks real-time comparative simulation between Loan A and Loan B, with savings differential bar charts, tenured interest delta, and neon winner badges.",
                    fontFamily = InterFamily,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(HotPink.copy(alpha = 0.15f))
                        .border(1.dp, HotPink.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "⚡ Arriving in Phase 4",
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = HotPink
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

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
