package com.example.emicalculatorapp.ui.schedule

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emicalculatorapp.data.settings.NumberFormatStyle
import com.example.emicalculatorapp.model.AmortizationYear
import com.example.emicalculatorapp.ui.theme.ElectricViolet
import com.example.emicalculatorapp.ui.theme.InterFamily
import com.example.emicalculatorapp.ui.theme.LuminousCyan
import com.example.emicalculatorapp.ui.theme.SpaceGroteskFamily
import com.example.emicalculatorapp.ui.theme.SurfaceColor
import com.example.emicalculatorapp.ui.theme.SurfaceContainerLowest
import com.example.emicalculatorapp.ui.theme.TextMuted
import com.example.emicalculatorapp.ui.theme.TextPrimary
import com.example.emicalculatorapp.ui.theme.TextSecondary
import com.example.emicalculatorapp.viewmodel.EMIViewModel

/**
 * Expandable Year Card row with 12-month drill-down table.
 */
@Composable
fun AmortizationYearCard(
    year: AmortizationYear,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    numberFormat: NumberFormatStyle,
    modifier: Modifier = Modifier
) {
    val chevronRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
        label = "YearChevron_${year.year}"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceColor.copy(alpha = 0.85f))
            .border(
                1.dp,
                if (isExpanded) LuminousCyan.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.06f),
                RoundedCornerShape(14.dp)
            )
            .padding(14.dp)
    ) {
        // Year Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onToggle() },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(ElectricViolet.copy(alpha = 0.25f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Year ${year.year}",
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = LuminousCyan
                    )
                }

                Column(modifier = Modifier.padding(start = 10.dp)) {
                    Text(
                        text = "Principal: ${EMIViewModel.formatCurrency(year.totalPrincipalPaid, numberFormat)}",
                        fontFamily = SpaceGroteskFamily,
                        fontSize = 12.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "Interest: ${EMIViewModel.formatCurrency(year.totalInterestPaid, numberFormat)}",
                        fontFamily = InterFamily,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Balance",
                        fontFamily = InterFamily,
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                    Text(
                        text = EMIViewModel.formatCurrency(year.closingBalance, numberFormat),
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = TextPrimary
                    )
                }

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(20.dp)
                        .rotate(chevronRotation)
                )
            }
        }

        // 12-Month Drill-Down Rows
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(animationSpec = tween(220)) + fadeIn(),
            exit = shrinkVertically(animationSpec = tween(180)) + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(SurfaceContainerLowest)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Month", fontFamily = InterFamily, fontSize = 10.sp, color = TextMuted)
                    Text(text = "Principal", fontFamily = InterFamily, fontSize = 10.sp, color = TextMuted)
                    Text(text = "Interest", fontFamily = InterFamily, fontSize = 10.sp, color = TextMuted)
                    Text(text = "Balance", fontFamily = InterFamily, fontSize = 10.sp, color = TextMuted)
                }

                year.months.forEach { month ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "M${month.monthIndex}",
                            fontFamily = SpaceGroteskFamily,
                            fontSize = 11.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = EMIViewModel.formatCurrency(month.principalPaid, numberFormat),
                            fontFamily = SpaceGroteskFamily,
                            fontSize = 11.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = EMIViewModel.formatCurrency(month.interestPaid, numberFormat),
                            fontFamily = SpaceGroteskFamily,
                            fontSize = 11.sp,
                            color = ElectricViolet
                        )
                        Text(
                            text = EMIViewModel.formatCurrency(month.closingBalance, numberFormat),
                            fontFamily = SpaceGroteskFamily,
                            fontSize = 11.sp,
                            color = LuminousCyan,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
