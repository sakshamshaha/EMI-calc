package com.example.emicalculatorapp

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.TransformOrigin
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.emicalculatorapp.data.settings.NumberFormatStyle
import com.example.emicalculatorapp.data.settings.SettingsRepository
import com.example.emicalculatorapp.ui.getstarted.GetStartedScreen
import com.example.emicalculatorapp.ui.navigation.MainAppScaffold
import com.example.emicalculatorapp.ui.theme.EMIverseTheme
import com.example.emicalculatorapp.ui.theme.LocalReduceMotion
import com.example.emicalculatorapp.viewmodel.EMIViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

enum class ScreenState {
    GET_STARTED,
    MAIN_APP
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        configureHighRefreshRate()

        val settingsRepository = SettingsRepository(applicationContext)

        // Keep splash visible until DataStore has resolved first-launch preference
        var isDataStoreReady by mutableStateOf(false)
        splashScreen.setKeepOnScreenCondition { !isDataStoreReady }

        splashScreen.setOnExitAnimationListener { splashScreenView ->
            // Short 200ms scale/fade exit animation
            val fadeOut = ObjectAnimator.ofFloat(splashScreenView.view, View.ALPHA, 1f, 0f).apply {
                duration = 200L
                interpolator = FastOutSlowInInterpolator()
                doOnEnd { splashScreenView.remove() }
            }
            fadeOut.start()
        }

        setContent {
            val coroutineScope = rememberCoroutineScope()
            val sharedViewModel: EMIViewModel = viewModel()

            val firstLaunchFlow = remember(settingsRepository) {
                settingsRepository.isFirstLaunchFlow.map { it as Boolean? }
            }
            val isFirstLaunchNullable by firstLaunchFlow.collectAsStateWithLifecycle(initialValue = null)

            LaunchedEffect(isFirstLaunchNullable) {
                if (isFirstLaunchNullable != null) {
                    isDataStoreReady = true
                }
            }

            val effectsLevel by settingsRepository.effectsLevelFlow.collectAsStateWithLifecycle(initialValue = settingsRepository.defaultEffectsLevel)
            val numberFormat by settingsRepository.numberFormatFlow.collectAsStateWithLifecycle(initialValue = NumberFormatStyle.INDIAN)
            val hapticsEnabled by settingsRepository.hapticsEnabledFlow.collectAsStateWithLifecycle(initialValue = true)

            EMIverseTheme(
                effectsLevel = effectsLevel,
                numberFormat = numberFormat,
                hapticsEnabled = hapticsEnabled
            ) {
                var currentScreen by remember(isFirstLaunchNullable) {
                    mutableStateOf(
                        if (isFirstLaunchNullable == true) ScreenState.GET_STARTED else ScreenState.MAIN_APP
                    )
                }

                val reduceMotion = LocalReduceMotion.current
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = {
                        if (reduceMotion) {
                            fadeIn(animationSpec = tween(0)) togetherWith fadeOut(animationSpec = tween(0))
                        } else if (targetState == ScreenState.MAIN_APP) {
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
                                    coroutineScope.launch {
                                        settingsRepository.setFirstLaunchComplete()
                                    }
                                    currentScreen = ScreenState.MAIN_APP
                                }
                            )
                        }
                        ScreenState.MAIN_APP -> {
                            MainAppScaffold(
                                sharedViewModel = sharedViewModel,
                                settingsRepository = settingsRepository,
                                onNavigateToGetStarted = {
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
