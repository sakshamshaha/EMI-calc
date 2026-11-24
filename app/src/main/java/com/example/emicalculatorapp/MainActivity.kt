package com.example.emicalculatorapp

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.TransformOrigin
import com.example.emicalculatorapp.ui.calculator.CalculatorScreen
import com.example.emicalculatorapp.ui.getstarted.GetStartedScreen
import com.example.emicalculatorapp.ui.theme.EMIverseTheme
import com.example.emicalculatorapp.ui.theme.LocalReduceMotion

enum class ScreenState {
    GET_STARTED,
    CALCULATOR
}

class MainActivity : ComponentActivity() {

    private val PREFS_NAME = "emiverse_prefs"
    private val KEY_FIRST_LAUNCH = "is_first_launch"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        configureHighRefreshRate()

        val sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isFirstLaunch = sharedPrefs.getBoolean(KEY_FIRST_LAUNCH, true)

        setContent {
            EMIverseTheme {
                var currentScreen by remember {
                    mutableStateOf(
                        if (isFirstLaunch) ScreenState.GET_STARTED else ScreenState.CALCULATOR
                    )
                }

                val reduceMotion = LocalReduceMotion.current
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = {
                        if (reduceMotion) {
                            fadeIn(animationSpec = tween(0)) togetherWith fadeOut(animationSpec = tween(0))
                        } else if (targetState == ScreenState.CALCULATOR) {
                            // Expanding reveal transition: scales up gracefully from the launch button position
                            (scaleIn(
                                initialScale = 0.92f,
                                transformOrigin = TransformOrigin(0.5f, 0.85f),
                                animationSpec = tween(380, easing = FastOutSlowInEasing)
                            ) + fadeIn(animationSpec = tween(350, easing = FastOutSlowInEasing)))
                                .togetherWith(
                                    scaleOut(
                                        targetScale = 0.95f,
                                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                                    ) + fadeOut(animationSpec = tween(260, easing = FastOutSlowInEasing))
                                )
                        } else {
                            // Collapsing back transition: scales down and tucks back toward the button
                            (scaleIn(
                                initialScale = 0.95f,
                                animationSpec = tween(350, easing = FastOutSlowInEasing)
                            ) + fadeIn(animationSpec = tween(350, easing = FastOutSlowInEasing)))
                                .togetherWith(
                                    scaleOut(
                                        targetScale = 0.90f,
                                        transformOrigin = TransformOrigin(0.5f, 0.85f),
                                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                                    ) + fadeOut(animationSpec = tween(240, easing = FastOutSlowInEasing))
                                )
                        }
                    },
                    label = "ScreenTransition"
                ) { screen ->
                    when (screen) {
                        ScreenState.GET_STARTED -> {
                            GetStartedScreen(
                                onNavigateToCalculator = {
                                    // Mark first launch complete so returning users go directly to Calculator
                                    sharedPrefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
                                    currentScreen = ScreenState.CALCULATOR
                                }
                            )
                        }
                        ScreenState.CALCULATOR -> {
                            CalculatorScreen(
                                onNavigateBack = {
                                    currentScreen = ScreenState.GET_STARTED
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Configures the window to request the device's highest supported display refresh rate
     * (90Hz / 120Hz / 144Hz) on Android 6.0+ so the app is never locked to 60Hz.
     */
    private fun configureHighRefreshRate() {
        try {
            val modes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                display?.supportedModes
            } else {
                @Suppress("DEPRECATION")
                windowManager.defaultDisplay?.supportedModes
            }
            val highestMode = modes?.maxByOrNull { it.refreshRate }
            if (highestMode != null && highestMode.refreshRate > 60f) {
                val layoutParams = window.attributes
                layoutParams.preferredDisplayModeId = highestMode.modeId
                window.attributes = layoutParams
            }
        } catch (_: Exception) {
            // Ignore if display mode switching is restricted by device policy
        }
    }
}
