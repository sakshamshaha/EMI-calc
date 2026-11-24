package com.example.emicalculatorapp.ui.components.fx

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import com.example.emicalculatorapp.data.settings.EffectsLevel
import com.example.emicalculatorapp.ui.theme.LocalEffectsLevel
import com.example.emicalculatorapp.ui.theme.LocalReduceMotion
import kotlinx.coroutines.launch

/**
 * 3D touch-driven interactive tilt card.
 * Full effects level only; static on Balanced, Lite, or when input is active.
 * Reads rotation angles lazily inside graphicsLayer to avoid recomposition.
 */
@Composable
fun TiltCard(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(20.dp),
    content: @Composable BoxScope.() -> Unit
) {
    val effectsLevel = LocalEffectsLevel.current
    val reduceMotion = LocalReduceMotion.current
    val isTiltAllowed = enabled && !reduceMotion && effectsLevel == EffectsLevel.FULL

    if (!isTiltAllowed) {
        Box(modifier = modifier, content = content)
        return
    }

    val coroutineScope = rememberCoroutineScope()
    var cardSize by remember { mutableStateOf(Size.Zero) }

    val rotX = remember { Animatable(0f) }
    val rotY = remember { Animatable(0f) }

    val springSpec = remember {
        spring<Float>(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    }

    Box(
        modifier = modifier
            .onSizeChanged { cardSize = Size(it.width.toFloat(), it.height.toFloat()) }
            .pointerInput(cardSize, enabled) {
                detectTapGestures(
                    onPress = { offset ->
                        val touchX = offset.x - cardSize.width / 2f
                        val touchY = offset.y - cardSize.height / 2f
                        val (targetX, targetY) = TiltCalculator.calculateTiltAngles(
                            touchX = touchX,
                            touchY = touchY,
                            width = cardSize.width,
                            height = cardSize.height
                        )
                        coroutineScope.launch {
                            rotX.animateTo(targetX, springSpec)
                        }
                        coroutineScope.launch {
                            rotY.animateTo(targetY, springSpec)
                        }
                        tryAwaitRelease()
                        coroutineScope.launch { rotX.animateTo(0f, springSpec) }
                        coroutineScope.launch { rotY.animateTo(0f, springSpec) }
                    }
                )
            }
            .pointerInput(cardSize, enabled) {
                detectDragGestures(
                    onDrag = { change, _ ->
                        change.consume()
                        val touchX = change.position.x - cardSize.width / 2f
                        val touchY = change.position.y - cardSize.height / 2f
                        val (targetX, targetY) = TiltCalculator.calculateTiltAngles(
                            touchX = touchX,
                            touchY = touchY,
                            width = cardSize.width,
                            height = cardSize.height
                        )
                        coroutineScope.launch { rotX.snapTo(targetX) }
                        coroutineScope.launch { rotY.snapTo(targetY) }
                    },
                    onDragEnd = {
                        coroutineScope.launch { rotX.animateTo(0f, springSpec) }
                        coroutineScope.launch { rotY.animateTo(0f, springSpec) }
                    },
                    onDragCancel = {
                        coroutineScope.launch { rotX.animateTo(0f, springSpec) }
                        coroutineScope.launch { rotY.animateTo(0f, springSpec) }
                    }
                )
            }
            .graphicsLayer {
                this.cameraDistance = 16f * density
                this.rotationX = rotX.value
                this.rotationY = rotY.value
                this.shape = shape
                this.clip = true
            },
        content = content
    )
}
