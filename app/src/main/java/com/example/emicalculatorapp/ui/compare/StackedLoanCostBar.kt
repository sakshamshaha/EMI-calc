package com.example.emicalculatorapp.ui.compare

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emicalculatorapp.model.LoanSummary
import com.example.emicalculatorapp.ui.theme.ElectricViolet
import com.example.emicalculatorapp.ui.theme.HotPink
import com.example.emicalculatorapp.ui.theme.InterFamily
import com.example.emicalculatorapp.ui.theme.LuminousCyan
import com.example.emicalculatorapp.ui.theme.SpaceGroteskFamily
import com.example.emicalculatorapp.ui.theme.SurfaceContainerHigh
import com.example.emicalculatorapp.ui.theme.TextSecondary

@Composable
fun StackedLoanCostBar(
    title: String,
    summary: LoanSummary
) {
    val principalShare = summary.principalSharePercent / 100f
    val interestShare = summary.interestSharePercent / 100f
    val feeShare = summary.feeSharePercent / 100f

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontFamily = SpaceGroteskFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp,
            color = TextSecondary,
            modifier = Modifier.width(50.dp)
        )

        Row(
            modifier = Modifier
                .weight(1f)
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(SurfaceContainerHigh)
        ) {
            if (principalShare > 0.001f) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(principalShare)
                        .background(LuminousCyan)
                )
            }
            if (interestShare > 0.001f) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(interestShare)
                        .background(ElectricViolet)
                )
            }
            if (feeShare > 0.001f) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(feeShare)
                        .background(HotPink)
                )
            }
        }
    }
}

@Composable
fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = label,
            fontFamily = InterFamily,
            fontSize = 11.sp,
            color = TextSecondary
        )
    }
}
