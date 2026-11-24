package com.example.emicalculatorapp.ui.tools

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emicalculatorapp.data.settings.NumberFormatStyle
import com.example.emicalculatorapp.model.PrepaymentPoint
import com.example.emicalculatorapp.ui.components.GlassCard
import com.example.emicalculatorapp.ui.components.fx.PopTooltip
import com.example.emicalculatorapp.ui.theme.ElectricViolet
import com.example.emicalculatorapp.ui.theme.InterFamily
import com.example.emicalculatorapp.ui.theme.MintGreen
import com.example.emicalculatorapp.ui.theme.SpaceGroteskFamily
import com.example.emicalculatorapp.ui.theme.SurfaceContainerLow
import com.example.emicalculatorapp.ui.theme.TextMuted
import com.example.emicalculatorapp.ui.theme.TextPrimary
import com.example.emicalculatorapp.ui.theme.TextSecondary
import com.example.emicalculatorapp.viewmodel.EMIViewModel

@Composable
fun PrepaymentComparisonChart(
    points: List<PrepaymentPoint>,
    numberFormat: NumberFormatStyle,
    modifier: Modifier = Modifier
) {
    if (points.isEmpty()) return

    var scrubPoint by remember { mutableStateOf<PrepaymentPoint?>(null) }
    var scrubX by remember { mutableStateOf(0f) }

    val maxPrincipal = remember(points) {
        points.maxOfOrNull { maxOf(it.originalBalance, it.newBalance) }?.coerceAtLeast(1.0) ?: 1.0
    }
    val maxMonth = remember(points) {
        points.maxOfOrNull { it.month }?.coerceAtLeast(1) ?: 1
    }

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = SurfaceContainerLow.copy(alpha = 0.85f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Balance Payoff Trajectory",
                    fontFamily = SpaceGroteskFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextPrimary
                )
                Text(
                    text = "Touch to scrub",
                    fontFamily = InterFamily,
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Chart Canvas Box with Scrub Tooltip
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(points) {
                            detectTapGestures(
                                onPress = { offset ->
                                    scrubX = offset.x
                                    val progress = (offset.x / size.width).coerceIn(0f, 1f)
                                    val targetMonth = (progress * maxMonth).toInt()
                                    scrubPoint = points.minByOrNull { kotlin.math.abs(it.month - targetMonth) }
                                    tryAwaitRelease()
                                    scrubPoint = null
                                }
                            )
                        }
                        .pointerInput(points) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    scrubX = offset.x
                                    val progress = (offset.x / size.width).coerceIn(0f, 1f)
                                    val targetMonth = (progress * maxMonth).toInt()
                                    scrubPoint = points.minByOrNull { kotlin.math.abs(it.month - targetMonth) }
                                },
                                onDragEnd = { scrubPoint = null },
                                onDragCancel = { scrubPoint = null },
                                onDrag = { change, _ ->
                                    scrubX = change.position.x
                                    val progress = (change.position.x / size.width).coerceIn(0f, 1f)
                                    val targetMonth = (progress * maxMonth).toInt()
                                    scrubPoint = points.minByOrNull { kotlin.math.abs(it.month - targetMonth) }
                                }
                            )
                        }
                ) {
                    val w = size.width
                    val h = size.height
                    val bottomPadding = 10f

                    // Grid lines (3 horizontal guides)
                    val gridColor = Color.White.copy(alpha = 0.06f)
                    drawLine(gridColor, Offset(0f, 0f), Offset(w, 0f), strokeWidth = 1f)
                    drawLine(gridColor, Offset(0f, h * 0.5f), Offset(w, h * 0.5f), strokeWidth = 1f)
                    drawLine(gridColor, Offset(0f, h - bottomPadding), Offset(w, h - bottomPadding), strokeWidth = 1f)

                    val originalPath = Path()
                    val acceleratedPath = Path()

                    points.forEachIndexed { index, pt ->
                        val x = (pt.month.toFloat() / maxMonth) * w
                        val origY = (1f - (pt.originalBalance.toFloat() / maxPrincipal.toFloat())) * (h - bottomPadding)
                        val newY = (1f - (pt.newBalance.toFloat() / maxPrincipal.toFloat())) * (h - bottomPadding)

                        if (index == 0) {
                            originalPath.moveTo(x, origY)
                            acceleratedPath.moveTo(x, newY)
                        } else {
                            originalPath.lineTo(x, origY)
                            acceleratedPath.lineTo(x, newY)
                        }
                    }

                    // Draw Original Curve (Violet)
                    drawPath(
                        path = originalPath,
                        color = ElectricViolet.copy(alpha = 0.7f),
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Draw Accelerated Curve (Mint Green)
                    drawPath(
                        path = acceleratedPath,
                        color = MintGreen,
                        style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Draw Scrub Line & Indicator
                    scrubPoint?.let { pt ->
                        val sx = (pt.month.toFloat() / maxMonth) * w
                        drawLine(
                            color = Color.White.copy(alpha = 0.5f),
                            start = Offset(sx, 0f),
                            end = Offset(sx, h - bottomPadding),
                            strokeWidth = 1.5.dp.toPx()
                        )

                        val origY = (1f - (pt.originalBalance.toFloat() / maxPrincipal.toFloat())) * (h - bottomPadding)
                        val newY = (1f - (pt.newBalance.toFloat() / maxPrincipal.toFloat())) * (h - bottomPadding)

                        drawCircle(ElectricViolet, radius = 5.dp.toPx(), center = Offset(sx, origY))
                        drawCircle(MintGreen, radius = 6.dp.toPx(), center = Offset(sx, newY))
                    }
                }

                // PopTooltip anchored on scrub point
                scrubPoint?.let { pt ->
                    PopTooltip(
                        text = "Mo ${pt.month} • Prepay: ${EMIViewModel.formatCompactCurrency(pt.newBalance, numberFormat)} (Std: ${EMIViewModel.formatCompactCurrency(pt.originalBalance, numberFormat)})",
                        visible = true,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Legend Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(ElectricViolet)
                    )
                    Text(
                        text = "Original Trajectory",
                        fontFamily = InterFamily,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.size(20.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(MintGreen)
                    )
                    Text(
                        text = "With Prepayment",
                        fontFamily = InterFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MintGreen
                    )
                }
            }
        }
    }
}
