package com.example.emicalculatorapp.ui.components.fx

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.example.emicalculatorapp.data.settings.EffectsLevel
import com.example.emicalculatorapp.ui.theme.LocalEffectsLevel
import com.example.emicalculatorapp.ui.theme.LocalReduceMotion
import com.example.emicalculatorapp.ui.theme.TextPrimary

/**
 * Mechanical digital odometer text component.
 * Rebuilt natively from Uiverse.io digit roller patterns.
 * 
 * CRITICAL RULE:
 * When [isContinuous] is true (e.g. while dragging a slider), it skips per-digit rolling
 * and snaps instantly with a high-speed micro-fade to avoid recomposition jitter.
 * When [isContinuous] is false (discrete changes like presets, chips, typed values),
 * each changing digit rolls vertically with an authentic mechanical roll effect.
 */
@Composable
fun OdometerText(
    text: String,
    modifier: Modifier = Modifier,
    isContinuous: Boolean = false,
    textStyle: TextStyle,
    color: Color = TextPrimary
) {
    val effectsLevel = LocalEffectsLevel.current
    val reduceMotion = LocalReduceMotion.current

    // Skip digit roll if continuous drag, reduced motion, or LITE effects
    val skipRoll = isContinuous || reduceMotion || effectsLevel == EffectsLevel.LITE

    if (skipRoll) {
        Text(
            text = text,
            style = textStyle,
            color = color,
            modifier = modifier
        )
    } else {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            text.forEachIndexed { index, char ->
                AnimatedContent(
                    targetState = char,
                    transitionSpec = {
                        val isIncreasing = targetState > initialState
                        val slideIn = slideInVertically(
                            animationSpec = tween(durationMillis = 280),
                            initialOffsetY = { if (isIncreasing) it else -it }
                        ) + fadeIn(animationSpec = tween(durationMillis = 200))

                        val slideOut = slideOutVertically(
                            animationSpec = tween(durationMillis = 280),
                            targetOffsetY = { if (isIncreasing) -it else it }
                        ) + fadeOut(animationSpec = tween(durationMillis = 180))

                        slideIn togetherWith slideOut
                    },
                    label = "OdometerChar_$index"
                ) { targetChar ->
                    Text(
                        text = targetChar.toString(),
                        style = textStyle,
                        color = color
                    )
                }
            }
        }
    }
}
