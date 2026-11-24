package com.example.emicalculatorapp.ui.theme

import android.app.Activity
import android.provider.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val LocalReduceMotion = compositionLocalOf { false }

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    secondary = SecondaryLight,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    tertiary = TertiaryLight,
    onTertiary = OnTertiary,
    tertiaryContainer = TertiaryContainer,
    background = SurfaceColor,
    surface = SurfaceColor,
    surfaceVariant = SurfaceContainerHigh,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = OutlineColor,
    outlineVariant = OutlineVariant
)

/**
 * Checks system animation scale (Settings.Global.ANIMATOR_DURATION_SCALE).
 * When scale is 0, user has enabled "Reduce motion" or turned off animations.
 */
@Composable
fun isSystemReduceMotionEnabled(): Boolean {
    val context = LocalContext.current
    return remember(context) {
        try {
            val scale = Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1.0f
            )
            scale == 0f
        } catch (e: Exception) {
            false
        }
    }
}

/**
 * EMIverse Cyber-Luxe Theme for next-generation FinTech screens.
 * Sets up edge-to-edge system bars with light icons over dark background.
 * Provides LocalReduceMotion to disable motion when accessibility setting is active.
 */
@Composable
fun EMIverseTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    val reduceMotion = isSystemReduceMotionEnabled()

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                // Transparent status and navigation bars for seamless edge-to-edge
                window.statusBarColor = CanvasColor.toArgb()
                window.navigationBarColor = CanvasColor.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                // False means light icons on dark status/nav bars
                insetsController.isAppearanceLightStatusBars = false
                insetsController.isAppearanceLightNavigationBars = false
            }
        }
    }

    CompositionLocalProvider(LocalReduceMotion provides reduceMotion) {
        MaterialTheme(
            colorScheme = colorScheme,
            shapes = EMIverseShapes,
            content = content
        )
    }
}
