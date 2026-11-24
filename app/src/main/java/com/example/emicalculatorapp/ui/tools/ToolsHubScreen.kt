package com.example.emicalculatorapp.ui.tools

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
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emicalculatorapp.ui.components.GlassCard
import com.example.emicalculatorapp.ui.theme.CanvasColor
import com.example.emicalculatorapp.ui.theme.ElectricViolet
import com.example.emicalculatorapp.ui.theme.HotPink
import com.example.emicalculatorapp.ui.theme.InterFamily
import com.example.emicalculatorapp.ui.theme.LuminousCyan
import com.example.emicalculatorapp.ui.theme.MintGreen
import com.example.emicalculatorapp.ui.theme.SpaceGroteskFamily
import com.example.emicalculatorapp.ui.theme.TextPrimary
import com.example.emicalculatorapp.ui.theme.TextSecondary

/**
 * Tools & Financial Insights Hub.
 * Contains modular financial tools with "Coming soon" badges for phases under construction.
 */
@Composable
fun ToolsHubScreen(
    modifier: Modifier = Modifier
) {
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
            text = "Financial Tools Hub",
            fontFamily = SpaceGroteskFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = TextPrimary
        )
        Text(
            text = "Advanced simulation modules, affordability checks & support",
            fontFamily = InterFamily,
            fontSize = 13.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
        )

        // 1. Prepayment Simulator
        ToolHubCard(
            title = "Prepayment & Debt-Free Simulator",
            description = "Simulate extra monthly contributions or lump sums to see how many years and lakhs of interest you cut off.",
            icon = Icons.Default.Speed,
            iconTint = LuminousCyan,
            statusBadge = "Coming in Phase 3"
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Affordability Calculator
        ToolHubCard(
            title = "Borrowing Power & Affordability",
            description = "180° visual gauge evaluating your max comfortable loan based on monthly net income and debt-to-income (DTI) ratio.",
            icon = Icons.Default.AccountBalance,
            iconTint = MintGreen,
            statusBadge = "Coming in Phase 5"
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Smart Rate Sensitivity Strip
        ToolHubCard(
            title = "Rate Sensitivity & 'See the Math'",
            description = "Dynamic ±0.5% rate delta matrices and mathematical breakdown of reducing balance formulas.",
            icon = Icons.Default.AutoAwesome,
            iconTint = ElectricViolet,
            statusBadge = "Coming in Phase 7"
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 4. Help Center & FAQ
        ToolHubCard(
            title = "Help Center & FAQ",
            description = "Searchable loan questions, terminology breakdown, and financial literacy guides.",
            icon = Icons.AutoMirrored.Filled.HelpOutline,
            iconTint = LuminousCyan,
            statusBadge = "Coming in Phase 7B"
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 5. Report a Problem
        ToolHubCard(
            title = "Report a Problem & Diagnostics",
            description = "Attach screenshot diagnostics, preview prefilled feedback emails, and save report history.",
            icon = Icons.Default.ReportProblem,
            iconTint = HotPink,
            statusBadge = "Coming in Phase 7B"
        )

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

@Composable
private fun ToolHubCard(
    title: String,
    description: String,
    icon: ImageVector,
    iconTint: Color,
    statusBadge: String
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(iconTint.copy(alpha = 0.15f))
                            .border(1.dp, iconTint.copy(alpha = 0.35f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = title,
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextPrimary,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = statusBadge,
                        fontFamily = SpaceGroteskFamily,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = description,
                fontFamily = InterFamily,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 17.sp
            )
        }
    }
}
