package com.example.emicalculatorapp.ui.components.fx

/**
 * Pure calculation logic for vertical parallax displacement of background cosmic layers.
 */
object ParallaxCalculator {
    const val DEFAULT_MAX_DISPLACEMENT = 120.0f

    /**
     * Calculates the vertical parallax translation offset for a background layer.
     * @param scrollOffset the current vertical scroll position in pixels (>= 0).
     * @param speedFactor multiplier for this layer's speed relative to foreground scroll.
     * @param maxDisplacement maximum allowed offset clamp to prevent runaway displacement.
     * @return offset in pixels to be added to layer center/position.
     */
    fun calculateParallaxOffset(
        scrollOffset: Float,
        speedFactor: Float,
        maxDisplacement: Float = DEFAULT_MAX_DISPLACEMENT
    ): Float {
        if (speedFactor == 0.0f || scrollOffset == 0.0f) return 0.0f
        val rawOffset = -scrollOffset * speedFactor
        return rawOffset.coerceIn(-maxDisplacement, maxDisplacement)
    }
}
