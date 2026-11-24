package com.example.emicalculatorapp.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emicalculatorapp.ui.theme.EMIverseTypography
import com.example.emicalculatorapp.ui.theme.ElectricViolet
import com.example.emicalculatorapp.ui.theme.LocalHapticsEnabled
import com.example.emicalculatorapp.ui.theme.LocalReduceMotion
import com.example.emicalculatorapp.ui.theme.LuminousCyan
import com.example.emicalculatorapp.ui.theme.SurfaceContainerLowest
import com.example.emicalculatorapp.ui.theme.TextPrimary
import kotlin.math.abs

/**
 * Custom Quantum Slider featuring:
 * - 6dp rounded gradient filled track (Electric Violet -> Luminous Cyan)
 * - 28dp white orb thumb with 2dp electric cyan ring
 * - Dynamic scale (1.15x) and ambient glow flare while dragging
 * - Real-time floating value bubble above the thumb with end-clamp protection
 * - Light haptic ticks on discrete value step updates
 * - Respects LocalReduceMotion
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuantumSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier,
    steps: Int = 0,
    bubbleText: String = "",
    activeGradient: Brush = Brush.horizontalGradient(listOf(ElectricViolet, LuminousCyan)),
    ringColor: Color = LuminousCyan,
    stepHapticThreshold: Float = 0f,
    onDraggingChanged: ((Boolean) -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isDragged by interactionSource.collectIsDraggedAsState()
    val isActive = isPressed || isDragged
    val reduceMotion = LocalReduceMotion.current

    LaunchedEffect(isActive) {
        onDraggingChanged?.invoke(isActive)
    }

    val haptic = LocalHapticFeedback.current
    val hapticsEnabled = LocalHapticsEnabled.current
    var lastHapticValue by remember { mutableFloatStateOf(value) }

    LaunchedEffect(value) {
        if (stepHapticThreshold > 0f && isActive && hapticsEnabled) {
            if (abs(value - lastHapticValue) >= stepHapticThreshold) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                lastHapticValue = value
            }
        }
    }

    val thumbScale by animateFloatAsState(
        targetValue = if (isActive && !reduceMotion) 1.15f else 1.0f,
        animationSpec = if (reduceMotion) tween(0) else spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "ThumbScale"
    )

    // Fraction from 0..1 to adjust bubble offset at track edges
    val fraction = ((value - valueRange.start) / (valueRange.endInclusive - valueRange.start)).coerceIn(0f, 1f)
    // Shift bubble horizontally away from screen edges if near extremes (guarantees no clipping on 360dp or 1.3x font scale)
    val bubbleXOffset = when {
        fraction < 0.25f -> ((0.25f - fraction) * 100f).dp
        fraction > 0.75f -> -((fraction - 0.75f) * 100f).dp
        else -> 0.dp
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp)
    ) {
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            interactionSource = interactionSource,
            modifier = Modifier.fillMaxWidth(),
            track = { sliderState ->
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                ) {
                    val trackWidth = size.width
                    val strokeW = 6.dp.toPx()
                    val y = size.height / 2f

                    // Inactive track (dark glass)
                    drawLine(
                        color = Color.White.copy(alpha = 0.08f),
                        start = Offset(0f, y),
                        end = Offset(trackWidth, y),
                        strokeWidth = strokeW,
                        cap = StrokeCap.Round
                    )

                    // Active filled gradient track
                    val f = ((sliderState.value - sliderState.valueRange.start) /
                            (sliderState.valueRange.endInclusive - sliderState.valueRange.start))
                        .coerceIn(0f, 1f)
                    val activeEnd = trackWidth * f

                    if (activeEnd > 0f) {
                        drawLine(
                            brush = activeGradient,
                            start = Offset(0f, y),
                            end = Offset(activeEnd, y),
                            strokeWidth = strokeW,
                            cap = StrokeCap.Round
                        )
                    }
                }
            },
            thumb = {
                Box(
                    modifier = Modifier.wrapContentSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Floating Value Bubble above thumb when active
                    if (isActive && bubbleText.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .offset(x = bubbleXOffset, y = (-38).dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceContainerLowest.copy(alpha = 0.95f))
                                .border(1.dp, ringColor.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = bubbleText,
                                style = EMIverseTypography.labelCaps,
                                color = TextPrimary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Thumb: 28dp white orb with cyan ring & glow
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .scale(thumbScale)
                            .drawBehind {
                                if (isActive && !reduceMotion) {
                                    drawCircle(
                                        color = ringColor.copy(alpha = 0.45f),
                                        radius = size.maxDimension * 0.75f
                                    )
                                }
                            }
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(2.dp, ringColor, CircleShape)
                    )
                }
            },
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.Transparent,
                inactiveTrackColor = Color.Transparent
            )
        )
    }
}
