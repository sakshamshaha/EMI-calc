package com.example.emicalculatorapp.ui.navigation

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emicalculatorapp.data.settings.EffectsLevel
import com.example.emicalculatorapp.ui.theme.ElectricViolet
import com.example.emicalculatorapp.ui.theme.LocalEffectsLevel
import com.example.emicalculatorapp.ui.theme.LocalHapticsEnabled
import com.example.emicalculatorapp.ui.theme.LocalReduceMotion
import com.example.emicalculatorapp.ui.theme.LuminousCyan
import com.example.emicalculatorapp.ui.theme.SpaceGroteskFamily
import com.example.emicalculatorapp.ui.theme.SurfaceColor
import com.example.emicalculatorapp.ui.theme.TextPrimary
import com.example.emicalculatorapp.ui.theme.TextSecondary

/**
 * Floating cyber-luxe glass navigation bar with a sliding neon indicator.
 * Positioned cleanly above the system gesture bar with navigationBarsPadding.
 */
@Composable
fun GlassBottomBar(
    currentRoute: String,
    onNavigate: (NavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = NavDestination.items
    val effectiveRoute = if (currentRoute == "prepayment_simulator") NavDestination.Tools.route else currentRoute
    val selectedIndex = items.indexOfFirst { it.route == effectiveRoute }.coerceAtLeast(0)
    val effectsLevel = LocalEffectsLevel.current
    val reduceMotion = LocalReduceMotion.current
    val hapticsEnabled = LocalHapticsEnabled.current
    val view = LocalView.current

    val animatedFraction by animateFloatAsState(
        targetValue = selectedIndex.toFloat() / items.size.toFloat(),
        animationSpec = if (reduceMotion) {
            spring(stiffness = Spring.StiffnessHigh)
        } else {
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow)
        },
        label = "BottomNavFraction"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(68.dp)
            .clip(RoundedCornerShape(34.dp))
            .background(SurfaceColor.copy(alpha = 0.90f))
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    listOf(
                        LuminousCyan.copy(alpha = 0.35f),
                        ElectricViolet.copy(alpha = 0.25f),
                        LuminousCyan.copy(alpha = 0.35f)
                    )
                ),
                shape = RoundedCornerShape(34.dp)
            )
            .drawBehind {
                val itemCount = items.size
                val itemWidth = size.width / itemCount
                val pillWidth = itemWidth - 12.dp.toPx()
                val pillHeight = size.height - 14.dp.toPx()
                val pillX = animatedFraction * size.width + 6.dp.toPx()
                val pillY = 7.dp.toPx()

                // Neon glow underlay
                if (effectsLevel == EffectsLevel.FULL && !reduceMotion) {
                    drawRoundRect(
                        brush = Brush.radialGradient(
                            colors = listOf(LuminousCyan.copy(alpha = 0.35f), Color.Transparent),
                            center = Offset(pillX + pillWidth / 2, pillY + pillHeight / 2),
                            radius = pillWidth * 0.8f
                        ),
                        topLeft = Offset(pillX - 6.dp.toPx(), pillY - 4.dp.toPx()),
                        size = Size(pillWidth + 12.dp.toPx(), pillHeight + 8.dp.toPx()),
                        cornerRadius = CornerRadius(26.dp.toPx(), 26.dp.toPx())
                    )
                }

                // Sliding neon pill
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        listOf(
                            ElectricViolet.copy(alpha = 0.45f),
                            LuminousCyan.copy(alpha = 0.35f)
                        )
                    ),
                    topLeft = Offset(pillX, pillY),
                    size = Size(pillWidth, pillHeight),
                    cornerRadius = CornerRadius(24.dp.toPx(), 24.dp.toPx())
                )
            }
            .padding(horizontal = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, destination ->
                val isSelected = destination.route == effectiveRoute
                val interactionSource = remember { MutableInteractionSource() }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            if (!isSelected) {
                                if (hapticsEnabled) {
                                    view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                                }
                                onNavigate(destination)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                            contentDescription = destination.title,
                            tint = if (isSelected) LuminousCyan else TextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = destination.title,
                            fontFamily = SpaceGroteskFamily,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 10.sp,
                            color = if (isSelected) TextPrimary else TextSecondary,
                            maxLines = 1,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
