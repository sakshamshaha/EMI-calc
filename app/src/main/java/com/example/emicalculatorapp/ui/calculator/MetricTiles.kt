package com.example.emicalculatorapp.ui.calculator

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emicalculatorapp.data.settings.NumberFormatStyle
import com.example.emicalculatorapp.ui.theme.EMIverseTypography
import com.example.emicalculatorapp.ui.theme.HotPink
import com.example.emicalculatorapp.ui.theme.LuminousCyan
import com.example.emicalculatorapp.ui.theme.PrimaryLight
import com.example.emicalculatorapp.ui.theme.SurfaceContainerLow
import com.example.emicalculatorapp.ui.theme.TextMuted
import com.example.emicalculatorapp.ui.theme.TextPrimary
import com.example.emicalculatorapp.viewmodel.EMIViewModel

@Composable
fun MetricMatrixGrid(
    animatedPrincipal: Float,
    animatedInterest: Float,
    animatedTotal: Float,
    animatedVelocity: Float,
    numberFormat: NumberFormatStyle,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricTile(
                modifier = Modifier.weight(1f),
                title = "PRINCIPAL",
                value = EMIViewModel.formatCurrency(animatedPrincipal.toDouble(), numberFormat),
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
                value = EMIViewModel.formatCurrency(animatedInterest.toDouble(), numberFormat),
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
                value = EMIViewModel.formatCurrency(animatedTotal.toDouble(), numberFormat),
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
                value = EMIViewModel.formatCurrency(animatedVelocity.toDouble(), numberFormat),
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
    }
}

@Composable
fun MetricTile(
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
