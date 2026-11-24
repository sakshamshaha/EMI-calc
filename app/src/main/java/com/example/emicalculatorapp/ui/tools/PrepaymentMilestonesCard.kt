package com.example.emicalculatorapp.ui.tools

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
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emicalculatorapp.model.PrepaymentMilestones
import com.example.emicalculatorapp.ui.components.GlassCard
import com.example.emicalculatorapp.ui.theme.ElectricViolet
import com.example.emicalculatorapp.ui.theme.HotPink
import com.example.emicalculatorapp.ui.theme.InterFamily
import com.example.emicalculatorapp.ui.theme.LuminousCyan
import com.example.emicalculatorapp.ui.theme.MintGreen
import com.example.emicalculatorapp.ui.theme.SpaceGroteskFamily
import com.example.emicalculatorapp.ui.theme.SurfaceContainerLow
import com.example.emicalculatorapp.ui.theme.TextMuted
import com.example.emicalculatorapp.ui.theme.TextPrimary
import com.example.emicalculatorapp.ui.theme.TextSecondary

@Composable
fun PrepaymentMilestonesCard(
    milestones: PrepaymentMilestones,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = SurfaceContainerLow.copy(alpha = 0.85f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Payoff Acceleration Milestones",
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextPrimary
            )

            // Milestone 1: 50% Principal Halfway Mark
            val halfwayDiff = (milestones.originalHalfwayMonth - milestones.newHalfwayMonth).coerceAtLeast(0)
            MilestoneComparisonRow(
                icon = Icons.Default.Flag,
                iconTint = LuminousCyan,
                title = "50% Principal Halfway Mark",
                subtitle = "Point where half of the borrowed loan amount is completely repaid",
                originalText = "Month ${milestones.originalHalfwayMonth} (${milestones.originalHalfwayMonth / 12}y ${milestones.originalHalfwayMonth % 12}m)",
                newText = "Month ${milestones.newHalfwayMonth} (${milestones.newHalfwayMonth / 12}y ${milestones.newHalfwayMonth % 12}m)",
                benefitTag = if (halfwayDiff > 0) "$halfwayDiff months faster" else "Unchanged"
            )

            // Milestone 2: Principal = Interest Crossover
            val crossoverDiff = (milestones.originalCrossoverMonth - milestones.newCrossoverMonth).coerceAtLeast(0)
            MilestoneComparisonRow(
                icon = Icons.Default.Timeline,
                iconTint = ElectricViolet,
                title = "Principal = Interest Crossover",
                subtitle = "Month when your monthly payment starts paying more principal than interest",
                originalText = "Month ${milestones.originalCrossoverMonth} (${milestones.originalCrossoverMonth / 12}y ${milestones.originalCrossoverMonth % 12}m)",
                newText = "Month ${milestones.newCrossoverMonth} (${milestones.newCrossoverMonth / 12}y ${milestones.newCrossoverMonth % 12}m)",
                benefitTag = if (crossoverDiff > 0) "$crossoverDiff months earlier" else "Unchanged"
            )
        }
    }
}

@Composable
private fun MilestoneComparisonRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    originalText: String,
    newText: String,
    benefitTag: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.04f))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = title,
                    fontFamily = SpaceGroteskFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = TextPrimary
                )
            }

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MintGreen.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = benefitTag,
                    fontFamily = SpaceGroteskFamily,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MintGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = subtitle,
            fontFamily = InterFamily,
            fontSize = 10.sp,
            color = TextMuted,
            lineHeight = 14.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Original",
                    fontFamily = InterFamily,
                    fontSize = 10.sp,
                    color = TextMuted
                )
                Text(
                    text = originalText,
                    fontFamily = SpaceGroteskFamily,
                    fontSize = 12.sp,
                    color = HotPink,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "With Prepayment",
                    fontFamily = InterFamily,
                    fontSize = 10.sp,
                    color = TextMuted
                )
                Text(
                    text = newText,
                    fontFamily = SpaceGroteskFamily,
                    fontSize = 12.sp,
                    color = MintGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
