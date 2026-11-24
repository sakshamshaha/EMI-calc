package com.example.emicalculatorapp.ui.components.fx

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import com.example.emicalculatorapp.data.settings.EffectsLevel
import com.example.emicalculatorapp.ui.theme.LocalEffectsLevel
import com.example.emicalculatorapp.ui.theme.LocalReduceMotion
import kotlinx.coroutines.delay

/**
 * Modifier providing a staggered slide-up and fade entrance for list items.
 * Triggers only on first appearance (never on re-scroll).
 * Instant on Lite and reduced motion.
 */
fun Modifier.staggeredEntrance(
    index: Int,
    baseDelayMillis: Int = 45
): Modifier = composed {
    val reduceMotion = LocalReduceMotion.current
    val effectsLevel = LocalEffectsLevel.current

    if (reduceMotion || effectsLevel == EffectsLevel.LITE) {
        return@composed this
    }

    var hasAppeared by remember { mutableStateOf(false) }
    val alpha = remember { Animatable(if (hasAppeared) 1f else 0f) }
    val translationY = remember { Animatable(if (hasAppeared) 0f else 28f) }

    LaunchedEffect(Unit) {
        if (!hasAppeared) {
            delay((index.coerceIn(0, 10) * baseDelayMillis).toLong())
            hasAppeared = true
            alpha.animateTo(1f, tween(durationMillis = 280, easing = FastOutSlowInEasing))
            translationY.animateTo(0f, tween(durationMillis = 280, easing = FastOutSlowInEasing))
        }
    }

    this.graphicsLayer {
        this.alpha = alpha.value
        this.translationY = translationY.value
    }
}
