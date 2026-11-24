package com.example.emicalculatorapp.ui.compare

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emicalculatorapp.data.settings.NumberFormatStyle
import com.example.emicalculatorapp.model.LoanSummary
import com.example.emicalculatorapp.ui.components.GlassCard
import com.example.emicalculatorapp.ui.components.fx.ShineButton
import com.example.emicalculatorapp.ui.theme.ElectricViolet
import com.example.emicalculatorapp.ui.theme.InterFamily
import com.example.emicalculatorapp.ui.theme.LuminousCyan
import com.example.emicalculatorapp.ui.theme.SpaceGroteskFamily
import com.example.emicalculatorapp.ui.theme.SurfaceBright
import com.example.emicalculatorapp.ui.theme.SurfaceContainerHigh
import com.example.emicalculatorapp.ui.theme.TextMuted
import com.example.emicalculatorapp.ui.theme.TextPrimary
import com.example.emicalculatorapp.ui.theme.TextSecondary
import com.example.emicalculatorapp.viewmodel.EMIViewModel
import java.util.Locale

@Composable
fun CompareLoanACard(
    summaryA: LoanSummary,
    numberFormat: NumberFormatStyle,
    onSaveLoanA: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LOAN A (ACTIVE BASELINE)",
                    fontFamily = SpaceGroteskFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = LuminousCyan,
                    letterSpacing = 0.8.sp
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(LuminousCyan.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "From Calculator",
                        fontFamily = SpaceGroteskFamily,
                        fontSize = 10.sp,
                        color = LuminousCyan,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Amount", fontFamily = InterFamily, fontSize = 11.sp, color = TextMuted)
                    Text(
                        text = EMIViewModel.formatCurrency(summaryA.amount, numberFormat),
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                }
                Column {
                    Text(text = "Rate / Tenure", fontFamily = InterFamily, fontSize = 11.sp, color = TextMuted)
                    Text(
                        text = "${String.format(Locale.US, "%.2f%%", summaryA.rate)} • ${if (summaryA.isYearMode) "${summaryA.tenureValue}Y" else "${summaryA.tenureValue}M"}",
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                }
                Column {
                    Text(text = "Monthly EMI", fontFamily = InterFamily, fontSize = 11.sp, color = TextMuted)
                    Text(
                        text = EMIViewModel.formatCurrency(summaryA.monthlyEMI, numberFormat),
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = LuminousCyan
                    )
                }
            }

            // Save Loan A Action
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onSaveLoanA,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = null,
                        tint = LuminousCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Save this loan",
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = LuminousCyan
                    )
                }
            }
        }
    }
}

@Composable
fun CompareActionsRow(
    onCopyAToB: () -> Unit,
    onSwap: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ShineButton(
                text = "Copy A to B",
                onClick = onCopyAToB,
                modifier = Modifier.weight(1f),
                gradientBrush = Brush.horizontalGradient(listOf(SurfaceContainerHigh, SurfaceBright))
            )

            ShineButton(
                text = "Swap A ⇄ B",
                onClick = onSwap,
                modifier = Modifier.weight(1f),
                gradientBrush = Brush.horizontalGradient(listOf(ElectricViolet, LuminousCyan))
            )
        }

        Text(
            text = "Swap also updates your Calculate tab.",
            fontFamily = InterFamily,
            fontSize = 11.sp,
            color = TextSecondary.copy(alpha = 0.8f),
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}
