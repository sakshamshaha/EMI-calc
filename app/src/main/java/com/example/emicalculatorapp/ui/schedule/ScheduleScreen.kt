package com.example.emicalculatorapp.ui.schedule

import android.content.Intent
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.emicalculatorapp.ui.components.fx.ShimmerBox
import com.example.emicalculatorapp.ui.components.fx.ShineButton
import com.example.emicalculatorapp.ui.components.fx.SweepBorderCard
import com.example.emicalculatorapp.ui.theme.CanvasColor
import com.example.emicalculatorapp.ui.theme.ElectricViolet
import com.example.emicalculatorapp.ui.theme.HotPink
import com.example.emicalculatorapp.ui.theme.InterFamily
import com.example.emicalculatorapp.ui.theme.LocalNumberFormat
import com.example.emicalculatorapp.ui.theme.LuminousCyan
import com.example.emicalculatorapp.ui.theme.MintGreen
import com.example.emicalculatorapp.ui.theme.SpaceGroteskFamily
import com.example.emicalculatorapp.ui.theme.TextPrimary
import com.example.emicalculatorapp.ui.theme.TextSecondary
import com.example.emicalculatorapp.viewmodel.EMIViewModel
import kotlinx.coroutines.launch

/**
 * Phase 2 Amortization Schedule Screen.
 * Features:
 * - Off-thread asynchronous schedule calculation (never blocks slider or frame rate).
 * - LazyColumn with stable keys for expandable year/month rows (no eager composition).
 * - Touch scrub area chart downsampled to <= 120 points for high-performance Canvas drawing.
 * - Exact month reading via scrub PopTooltip.
 * - Multi-page PDF export executed on Dispatchers.IO with ShimmerBox loader and FileProvider sharing.
 */
@Composable
fun ScheduleScreen(
    viewModel: EMIViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val numberFormat = LocalNumberFormat.current

    val schedule by viewModel.fullSchedule.collectAsStateWithLifecycle()
    val isYearMode by viewModel.isYearMode.collectAsStateWithLifecycle()
    val tenureValue by viewModel.tenureValue.collectAsStateWithLifecycle()

    var expandedYears by remember { mutableStateOf(setOf(1)) }
    var isGeneratingPdf by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasColor)
            .padding(horizontal = 20.dp)
    ) {
        // 1. Header with PDF Action Button
        item(key = "schedule_header") {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Amortization Schedule",
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "Yearly & monthly breakdown for your active loan",
                        fontFamily = InterFamily,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                ShineButton(
                    text = "Export PDF",
                    onClick = {
                        if (!isGeneratingPdf) {
                            isGeneratingPdf = true
                            coroutineScope.launch {
                                try {
                                    val uri = AmortizationPdfGenerator.generateAndGetUri(
                                        context = context,
                                        schedule = schedule,
                                        numberFormat = numberFormat
                                    )
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "application/pdf"
                                        putExtra(Intent.EXTRA_STREAM, uri)
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Share Schedule PDF"))
                                } finally {
                                    isGeneratingPdf = false
                                }
                            }
                        }
                    },
                    modifier = Modifier.width(105.dp)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Shimmer loader while generating PDF
        if (isGeneratingPdf) {
            item(key = "pdf_shimmer_box") {
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(bottom = 12.dp)
                )
            }
        }

        // 2. Active Loan Hero Summary Card
        item(key = "schedule_hero_summary") {
            SweepBorderCard(
                modifier = Modifier.fillMaxWidth(),
                gradientColors = listOf(LuminousCyan, ElectricViolet, HotPink, LuminousCyan)
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
                                text = EMIViewModel.formatCurrency(schedule.monthlyEMI, numberFormat),
                                fontFamily = SpaceGroteskFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
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
                                text = EMIViewModel.formatCurrency(schedule.principal, numberFormat),
                                fontFamily = SpaceGroteskFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = TextPrimary
                            )
                        }
                        Column {
                            Text(text = "Total Interest", fontFamily = InterFamily, fontSize = 11.sp, color = TextSecondary)
                            Text(
                                text = EMIViewModel.formatCurrency(schedule.totalInterest, numberFormat),
                                fontFamily = SpaceGroteskFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = ElectricViolet
                            )
                        }
                        Column {
                            Text(text = "Annual Rate", fontFamily = InterFamily, fontSize = 11.sp, color = TextSecondary)
                            Text(
                                text = "${schedule.annualRate}%",
                                fontFamily = SpaceGroteskFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = MintGreen
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 3. Interactive Downsampled Area Chart with Touch Scrub
        item(key = "schedule_area_chart") {
            AmortizationAreaChart(
                schedule = schedule,
                numberFormat = numberFormat
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 4. Section Title: Year by Year breakdown
        item(key = "schedule_table_title") {
            Text(
                text = "Yearly & Monthly Breakdown",
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // 5. Expandable Year & Month Rows (LazyColumn with stable keys)
        items(
            items = schedule.years,
            key = { "year_${it.year}" }
        ) { year ->
            AmortizationYearCard(
                year = year,
                isExpanded = year.year in expandedYears,
                onToggle = {
                    expandedYears = if (year.year in expandedYears) {
                        expandedYears - year.year
                    } else {
                        expandedYears + year.year
                    }
                },
                numberFormat = numberFormat
            )
        }

        // 6. Disclaimer & Bottom Padding
        item(key = "schedule_disclaimer") {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Estimates only, not financial advice.",
                fontFamily = InterFamily,
                fontSize = 11.sp,
                color = TextSecondary.copy(alpha = 0.6f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 90.dp)
            )
        }
    }
}
