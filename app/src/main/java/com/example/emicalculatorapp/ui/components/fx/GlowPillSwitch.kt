package com.example.emicalculatorapp.ui.components.fx

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
 * Morphing glowing pill segmented switch for multi-option selection.
 * Rebuilt natively from Uiverse.io segmented control patterns.
 */
@Composable
fun <T> GlowPillSwitch(
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    labelProvider: (T) -> String,
    modifier: Modifier = Modifier
) {
    val effectsLevel = LocalEffectsLevel.current
    val reduceMotion = LocalReduceMotion.current
    val hapticsEnabled = LocalHapticsEnabled.current
    val view = LocalView.current

    val selectedIndex = options.indexOf(selectedOption).coerceAtLeast(0)
    val optionCount = options.size.coerceAtLeast(1)

    val targetFraction = selectedIndex.toFloat() / optionCount.toFloat()
    val animatedFraction by animateFloatAsState(
        targetValue = targetFraction,
        animationSpec = if (reduceMotion) {
            spring(stiffness = Spring.StiffnessHigh)
        } else {
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow)
        },
        label = "PillSwitchFraction"
    )

    Box(
        modifier = modifier
            .height(44.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(SurfaceColor.copy(alpha = 0.7f))
            .drawBehind {
                val pillWidth = size.width / optionCount
                val pillHeight = size.height - 8.dp.toPx()
                val pillX = animatedFraction * size.width + 4.dp.toPx()
                val pillY = 4.dp.toPx()

                // Draw glowing indicator pill
                if (effectsLevel == EffectsLevel.FULL && !reduceMotion) {
                    drawRoundRect(
                        brush = Brush.radialGradient(
                            colors = listOf(LuminousCyan.copy(alpha = 0.45f), Color.Transparent),
                            center = Offset(pillX + pillWidth / 2, pillY + pillHeight / 2),
                            radius = pillWidth * 0.7f
                        ),
                        topLeft = Offset(pillX - 6.dp.toPx(), pillY - 2.dp.toPx()),
                        size = Size(pillWidth + 4.dp.toPx(), pillHeight + 4.dp.toPx()),
                        cornerRadius = CornerRadius(18.dp.toPx(), 18.dp.toPx())
                    )
                }

                drawRoundRect(
                    brush = Brush.horizontalGradient(listOf(ElectricViolet, LuminousCyan)),
                    topLeft = Offset(pillX, pillY),
                    size = Size(pillWidth - 8.dp.toPx(), pillHeight),
                    cornerRadius = CornerRadius(18.dp.toPx(), 18.dp.toPx())
                )
            }
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            options.forEachIndexed { index, option ->
                val isSelected = index == selectedIndex
                val interactionSource = remember { MutableInteractionSource() }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .semantics {
                            this.selected = isSelected
                            this.role = Role.RadioButton
                        }
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            if (index != selectedIndex) {
                                if (hapticsEnabled) {
                                    view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                                }
                                onOptionSelected(option)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = labelProvider(option),
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.sp,
                        color = if (isSelected) Color.White else TextSecondary,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
