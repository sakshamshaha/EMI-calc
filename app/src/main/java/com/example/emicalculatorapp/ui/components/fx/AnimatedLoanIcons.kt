package com.example.emicalculatorapp.ui.components.fx

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.emicalculatorapp.data.settings.EffectsLevel
import com.example.emicalculatorapp.model.LoanCategory
import com.example.emicalculatorapp.ui.theme.LocalEffectsLevel
import com.example.emicalculatorapp.ui.theme.LocalReduceMotion
import kotlin.math.PI
import kotlin.math.sin

/**
 * Native Canvas-rendered loan category icons.
 * Animates once upon selection, then settles into a resting idle state (no infinite loops).
 * Instant on Lite and reduced motion.
 */
@Composable
fun AnimatedLoanCategoryIcon(
    category: LoanCategory,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 20.dp,
    tint: Color = Color.Unspecified
) {
    val effectsLevel = LocalEffectsLevel.current
    val reduceMotion = LocalReduceMotion.current
    val shouldAnimate = isSelected && !reduceMotion && effectsLevel != EffectsLevel.LITE

    val animProgress = remember { Animatable(if (shouldAnimate) 0f else 1f) }

    LaunchedEffect(isSelected) {
        if (shouldAnimate) {
            animProgress.snapTo(0f)
            animProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
            )
        } else {
            animProgress.snapTo(1f)
        }
    }

    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val progress = animProgress.value
        val stroke = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)

        when (category) {
            LoanCategory.HOME -> {
                // Roof
                val roof = Path().apply {
                    moveTo(2.dp.toPx(), h * 0.45f)
                    lineTo(w * 0.5f, 2.dp.toPx())
                    lineTo(w - 2.dp.toPx(), h * 0.45f)
                }
                drawPath(roof, tint, style = stroke)

                // House Walls
                val walls = Path().apply {
                    moveTo(w * 0.15f, h * 0.42f)
                    lineTo(w * 0.15f, h - 2.dp.toPx())
                    lineTo(w * 0.85f, h - 2.dp.toPx())
                    lineTo(w * 0.85f, h * 0.42f)
                }
                drawPath(walls, tint, style = stroke)

                // Animated Door: Opens along left hinge as progress -> 1
                val doorW = w * 0.28f * (1f - progress * 0.55f)
                val doorH = h * 0.38f
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.36f, h - 2.dp.toPx() - doorH),
                    size = Size(doorW, doorH),
                    cornerRadius = CornerRadius(2.dp.toPx()),
                    style = stroke
                )
            }

            LoanCategory.CAR -> {
                // Car Cabin & Body
                val body = Path().apply {
                    moveTo(2.dp.toPx(), h * 0.65f)
                    lineTo(w * 0.22f, h * 0.65f)
                    lineTo(w * 0.35f, h * 0.32f)
                    lineTo(w * 0.68f, h * 0.32f)
                    lineTo(w * 0.82f, h * 0.65f)
                    lineTo(w - 2.dp.toPx(), h * 0.65f)
                    lineTo(w - 2.dp.toPx(), h * 0.78f)
                    lineTo(2.dp.toPx(), h * 0.78f)
                    close()
                }
                drawPath(body, tint, style = stroke)

                // Wheels: Spin angle based on progress (360 deg)
                val wheelRadius = w * 0.12f
                val wheelY = h * 0.78f
                val wheel1Center = Offset(w * 0.28f, wheelY)
                val wheel2Center = Offset(w * 0.72f, wheelY)

                drawCircle(tint, wheelRadius, wheel1Center, style = stroke)
                drawCircle(tint, wheelRadius, wheel2Center, style = stroke)

                // Spinning Wheel Spokes
                val angleRad = (progress * 360f) * (PI / 180f)
                val spokeX = (wheelRadius * 0.8f * sin(angleRad)).toFloat()
                drawLine(
                    color = tint,
                    start = Offset(wheel1Center.x - spokeX, wheel1Center.y),
                    end = Offset(wheel1Center.x + spokeX, wheel1Center.y),
                    strokeWidth = 1.2.dp.toPx()
                )
                drawLine(
                    color = tint,
                    start = Offset(wheel2Center.x - spokeX, wheel2Center.y),
                    end = Offset(wheel2Center.x + spokeX, wheel2Center.y),
                    strokeWidth = 1.2.dp.toPx()
                )
            }

            LoanCategory.PERSONAL -> {
                // Briefcase Body
                val bodyRect = Path().apply {
                    moveTo(2.dp.toPx(), h * 0.35f)
                    lineTo(w - 2.dp.toPx(), h * 0.35f)
                    lineTo(w - 2.dp.toPx(), h - 2.dp.toPx())
                    lineTo(2.dp.toPx(), h - 2.dp.toPx())
                    close()
                }
                drawPath(bodyRect, tint, style = stroke)

                // Handle on Top
                val handle = Path().apply {
                    moveTo(w * 0.35f, h * 0.35f)
                    lineTo(w * 0.35f, h * 0.20f)
                    lineTo(w * 0.65f, h * 0.20f)
                    lineTo(w * 0.65f, h * 0.35f)
                }
                drawPath(handle, tint, style = stroke)

                // Center Latch: pops up and clicks
                val latchY = h * 0.52f - (progress * 2.dp.toPx())
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.42f, latchY),
                    size = Size(w * 0.16f, h * 0.14f),
                    cornerRadius = CornerRadius(1.dp.toPx()),
                    style = stroke
                )
            }

            LoanCategory.EDUCATION -> {
                // Mortarboard: Tilts slightly then levels
                val tiltAngle = (1f - progress) * 12f
                val tiltRad = tiltAngle * (PI / 180f)
                val tiltOffset = (sin(tiltRad) * 4.dp.toPx()).toFloat()

                val cap = Path().apply {
                    moveTo(w * 0.5f, 3.dp.toPx() + tiltOffset)
                    lineTo(w - 2.dp.toPx(), h * 0.32f + tiltOffset)
                    lineTo(w * 0.5f, h * 0.55f + tiltOffset)
                    lineTo(2.dp.toPx(), h * 0.32f + tiltOffset)
                    close()
                }
                drawPath(cap, tint, style = stroke)

                // Skull Cap under
                val skull = Path().apply {
                    moveTo(w * 0.25f, h * 0.44f + tiltOffset)
                    lineTo(w * 0.25f, h * 0.70f)
                    lineTo(w * 0.75f, h * 0.70f)
                    lineTo(w * 0.75f, h * 0.44f + tiltOffset)
                }
                drawPath(skull, tint, style = stroke)

                // Tassel swinging with decaying oscillation
                val swingX = (sin(progress * PI * 4) * (1f - progress) * 6.dp.toPx()).toFloat()
                drawLine(
                    color = tint,
                    start = Offset(w * 0.5f, h * 0.45f + tiltOffset),
                    end = Offset(w * 0.85f + swingX, h * 0.68f),
                    strokeWidth = 1.5.dp.toPx()
                )
            }
        }
    }
}
