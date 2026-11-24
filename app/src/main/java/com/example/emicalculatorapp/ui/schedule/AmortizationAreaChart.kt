package com.example.emicalculatorapp.ui.schedule

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emicalculatorapp.data.settings.NumberFormatStyle
import com.example.emicalculatorapp.model.FullAmortizationSchedule
import com.example.emicalculatorapp.ui.components.GlassCard
import com.example.emicalculatorapp.ui.components.fx.PopTooltip
import com.example.emicalculatorapp.ui.theme.ElectricViolet
import com.example.emicalculatorapp.ui.theme.InterFamily
import com.example.emicalculatorapp.ui.theme.LuminousCyan
import com.example.emicalculatorapp.ui.theme.SpaceGroteskFamily
import com.example.emicalculatorapp.ui.theme.TextPrimary
import com.example.emicalculatorapp.ui.theme.TextSecondary
import com.example.emicalculatorapp.viewmodel.EMIViewModel
import kotlin.math.roundToInt

/**
 * Interactive touch-scrub area chart for remaining loan balance.
 * Uses a downsampled path (<= 120 points) for fluid 60/120fps Canvas rendering,
 * while scrub tooltips query exact month values from the full schedule.
 */
@Composable
fun AmortizationAreaChart(
    schedule: FullAmortizationSchedule,
    numberFormat: NumberFormatStyle,
    modifier: Modifier = Modifier
) {
    var scrubbedMonthIndex by remember { mutableStateOf<Int?>(null) }
    val months = schedule.allMonths

    val scrubbedMonth = scrubbedMonthIndex?.let { idx ->
        if (idx in months.indices) months[idx] else null
    }

    // Downsampled path calculation (<= 120 points for smooth performance)
    val step = (months.size / 120).coerceAtLeast(1)
    val chartPoints = remember(months) {
        if (months.isEmpty()) emptyList()
        else months.filterIndexed { index, _ -> index % step == 0 || index == months.size - 1 }
    }

    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Remaining Balance Curve",
                    fontFamily = SpaceGroteskFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )

                Text(
                    text = if (scrubbedMonthIndex != null) "Touch Scrub Active" else "Drag to inspect month",
                    fontFamily = InterFamily,
                    fontSize = 10.sp,
                    color = if (scrubbedMonthIndex != null) LuminousCyan else TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (scrubbedMonth != null) {
                PopTooltip(
                    text = "Month ${scrubbedMonth.monthIndex} (Yr ${scrubbedMonth.yearIndex}): Balance ${EMIViewModel.formatCurrency(scrubbedMonth.closingBalance, numberFormat)}",
                    visible = true,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .pointerInput(months) {
                        if (months.isNotEmpty()) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    val fraction = (offset.x / size.width).coerceIn(0f, 1f)
                                    scrubbedMonthIndex = (fraction * (months.size - 1)).roundToInt()
                                },
                                onDrag = { change, _ ->
                                    val fraction = (change.position.x / size.width).coerceIn(0f, 1f)
                                    scrubbedMonthIndex = (fraction * (months.size - 1)).roundToInt()
                                },
                                onDragEnd = { scrubbedMonthIndex = null },
                                onDragCancel = { scrubbedMonthIndex = null }
                            )
                        }
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    if (chartPoints.size >= 2) {
                        val w = size.width
                        val h = size.height
                        val maxBalance = schedule.principal.coerceAtLeast(1.0)

                        val path = Path()
                        val fillPath = Path()

                        fillPath.moveTo(0f, h)

                        chartPoints.forEachIndexed { i, pt ->
                            val x = (i.toFloat() / (chartPoints.size - 1)) * w
                            val y = (1f - (pt.closingBalance / maxBalance).toFloat()) * (h - 10f) + 5f

                            if (i == 0) {
                                path.moveTo(x, y)
                                fillPath.lineTo(x, y)
                            } else {
                                path.lineTo(x, y)
                                fillPath.lineTo(x, y)
                            }
                        }

                        fillPath.lineTo(w, h)
                        fillPath.close()

                        // Gradient fill
                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    LuminousCyan.copy(alpha = 0.22f),
                                    ElectricViolet.copy(alpha = 0.05f),
                                    Color.Transparent
                                )
                            )
                        )

                        // Path stroke
                        drawPath(
                            path = path,
                            brush = Brush.horizontalGradient(listOf(ElectricViolet, LuminousCyan)),
                            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // Scrub indicator
                        scrubbedMonthIndex?.let { sIdx ->
                            val sFraction = sIdx.toFloat() / (months.size - 1).coerceAtLeast(1)
                            val sX = sFraction * w
                            drawLine(
                                color = LuminousCyan,
                                start = Offset(sX, 0f),
                                end = Offset(sX, h),
                                strokeWidth = 1.5.dp.toPx()
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 4.dp.toPx(),
                                center = Offset(sX, (1f - (months[sIdx].closingBalance / maxBalance).toFloat()) * (h - 10f) + 5f)
                            )
                        }
                    }
                }
            }
        }
    }
}
