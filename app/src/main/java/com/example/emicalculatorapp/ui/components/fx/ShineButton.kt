package com.example.emicalculatorapp.ui.components.fx

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
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
import kotlinx.coroutines.delay

/**
 * High-octane CTA button with an angled luminous shine sheen traversing across the surface.
 * Rebuilt natively from Uiverse.io light sheen button patterns.
 * Supports tap feedback, haptics, and auto-tuning according to LocalEffectsLevel.
 */
@Composable
fun ShineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
    gradientBrush: Brush = Brush.horizontalGradient(listOf(ElectricViolet, LuminousCyan))
) {
    val effectsLevel = LocalEffectsLevel.current
    val reduceMotion = LocalReduceMotion.current
    val hapticsEnabled = LocalHapticsEnabled.current
    val view = LocalView.current

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "ShineButtonScale"
    )

    val shineOffset = remember { Animatable(-1f) }
    val enableAutoShine = !reduceMotion && effectsLevel == EffectsLevel.FULL

    LaunchedEffect(enableAutoShine) {
        if (enableAutoShine) {
            while (true) {
                delay(3500)
                shineOffset.snapTo(-1f)
                shineOffset.animateTo(
                    targetValue = 2f,
                    animationSpec = tween(durationMillis = 900, easing = LinearEasing)
                )
            }
        }
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(shape)
            .background(gradientBrush)
            .drawWithCache {
                val width = size.width
                val height = size.height
                val progress = shineOffset.value

                onDrawWithContent {
                    drawContent()
                    if (!reduceMotion && effectsLevel != EffectsLevel.LITE && progress in -0.5f..1.5f) {
                        val currentX = width * progress
                        val shineBrush = Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.35f),
                                Color.Transparent
                            ),
                            start = Offset(currentX - 60f, 0f),
                            end = Offset(currentX + 60f, height)
                        )
                        drawRect(brush = shineBrush)
                    }
                }
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                if (hapticsEnabled) {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                }
                onClick()
            }
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontFamily = SpaceGroteskFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = Color.White
        )
    }
}
