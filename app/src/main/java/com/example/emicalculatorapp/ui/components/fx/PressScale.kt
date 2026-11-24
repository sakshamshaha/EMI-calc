package com.example.emicalculatorapp.ui.components.fx

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import com.example.emicalculatorapp.data.settings.EffectsLevel
import com.example.emicalculatorapp.ui.theme.LocalEffectsLevel
import com.example.emicalculatorapp.ui.theme.LocalHapticsEnabled
import com.example.emicalculatorapp.ui.theme.LocalReduceMotion
import kotlinx.coroutines.launch

/**
 * Modifier providing a spring-loaded tactile press-scale effect.
 * Respects effects level and reduced motion (instant response on Lite).
 * Reads scale inside graphicsLayer to eliminate recompositions.
 */
fun Modifier.pressScale(
    targetScale: Float = 0.96f,
    onClick: (() -> Unit)? = null,
    provideHaptic: Boolean = true
): Modifier = composed {
    val reduceMotion = LocalReduceMotion.current
    val effectsLevel = LocalEffectsLevel.current
    val haptic = LocalHapticFeedback.current
    val hapticsEnabled = LocalHapticsEnabled.current
    val coroutineScope = rememberCoroutineScope()

    if (reduceMotion || effectsLevel == EffectsLevel.LITE) {
        return@composed if (onClick != null) {
            this.pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        if (provideHaptic && hapticsEnabled) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onClick()
                    }
                )
            }
        } else this
    }

    val scale = remember { Animatable(1f) }
    val springSpec = remember {
        spring<Float>(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    }

    this
        .graphicsLayer {
            scaleX = scale.value
            scaleY = scale.value
        }
        .pointerInput(onClick) {
            detectTapGestures(
                onPress = {
                    if (provideHaptic && hapticsEnabled) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    coroutineScope.launch { scale.animateTo(targetScale, springSpec) }
                    tryAwaitRelease()
                    coroutineScope.launch { scale.animateTo(1f, springSpec) }
                },
                onTap = {
                    onClick?.invoke()
                }
            )
        }
}
