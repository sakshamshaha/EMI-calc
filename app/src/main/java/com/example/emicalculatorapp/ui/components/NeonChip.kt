package com.example.emicalculatorapp.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.example.emicalculatorapp.ui.theme.EMIverseTypography
import com.example.emicalculatorapp.ui.theme.ElectricViolet
import com.example.emicalculatorapp.ui.theme.HeroCtaGradient
import com.example.emicalculatorapp.ui.theme.LocalReduceMotion
import com.example.emicalculatorapp.ui.theme.LuminousCyan
import com.example.emicalculatorapp.ui.theme.SurfaceContainerHigh
import com.example.emicalculatorapp.ui.theme.TextMuted
import com.example.emicalculatorapp.ui.theme.TextPrimary

/**
 * Neon Chip with tactile spring scale bounce, glowing selection state, and haptic feedback.
 */
@Composable
fun NeonChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedGradient: Brush = HeroCtaGradient,
    glowColor: Color = ElectricViolet.copy(alpha = 0.4f)
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val reduceMotion = LocalReduceMotion.current
    val haptic = LocalHapticFeedback.current

    val scale by animateFloatAsState(
        targetValue = when {
            isPressed && !reduceMotion -> 0.97f
            isSelected && !reduceMotion -> 1.05f
            else -> 1.0f
        },
        animationSpec = if (reduceMotion) tween(0) else spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "ChipScale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .then(
                if (isSelected) {
                    Modifier.drawBehind {
                        drawCircle(
                            color = glowColor,
                            radius = size.maxDimension * 0.45f,
                            center = center
                        )
                    }
                } else {
                    Modifier
                }
            )
            .clip(CircleShape)
            .then(
                if (isSelected) {
                    Modifier.background(selectedGradient)
                } else {
                    Modifier
                        .background(SurfaceContainerHigh.copy(alpha = 0.8f))
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.10f),
                            shape = CircleShape
                        )
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = LuminousCyan),
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onClick()
                }
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = EMIverseTypography.labelCaps,
            color = if (isSelected) TextPrimary else TextMuted
        )
    }
}
