package com.example.emicalculatorapp.data.settings

/**
 * Effects levels for performance and battery tuning across devices.
 * - FULL: All dynamic glows, sweep borders, shine traversal, particle effects, and 3D depth.
 * - BALANCED: Standard high-performance 60/120fps animations, optimized sweep shaders.
 * - LITE: Minimalist static borders and simplified transitions for low-RAM/battery saver.
 */
enum class EffectsLevel {
    FULL,
    BALANCED,
    LITE;

    val displayName: String
        get() = when (this) {
            FULL -> "Full (Cyber-Luxe)"
            BALANCED -> "Balanced (Recommended)"
            LITE -> "Lite (Battery Saver)"
        }

    val description: String
        get() = when (this) {
            FULL -> "All real-time glow shaders, sweep beams & micro-interactions"
            BALANCED -> "Silky-smooth 60/120Hz performance with optimized effects"
            LITE -> "Static borders and instant transitions for maximum efficiency"
        }
}
