package com.example.emicalculatorapp.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Core Cosmic Palette from DESIGN.md
val CanvasColor = Color(0xFF090A15)
val SurfaceColor = Color(0xFF12121E)
val SurfaceBright = Color(0xFF383845)
val SurfaceDim = Color(0xFF12121E)

// Container Tiers
val SurfaceContainerLowest = Color(0xFF0C0D18)
val SurfaceContainerLow = Color(0xFF1A1B26)
val SurfaceContainer = Color(0xFF1E1F2B)
val SurfaceContainerHigh = Color(0xFF282935)
val SurfaceContainerHighest = Color(0xFF333440)

// Luminous Light Sources
val ElectricViolet = Color(0xFF8B5CF6)
val PrimaryLight = Color(0xFFD0BCFF)
val PrimaryContainer = Color(0xFFA078FF)
val OnPrimary = Color(0xFF3C0091)

val LuminousCyan = Color(0xFF06B6D4)
val SecondaryLight = Color(0xFF4CD7F6)
val SecondaryContainer = Color(0xFF03B5D3)
val OnSecondary = Color(0xFF003640)

val HotPink = Color(0xFFEC4899)
val TertiaryLight = Color(0xFFFFB0CD)
val TertiaryContainer = Color(0xFFF751A1)
val OnTertiary = Color(0xFF640039)

val NeoEmerald = Color(0xFF10B981)
val MintGreen = NeoEmerald

// Text & Outlines
val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFFE2E1F1)
val TextMuted = Color(0xFF94A3B8)
val OutlineColor = Color(0xFF958EA0)
val OutlineVariant = Color(0xFF494454)

// Glassmorphism Variable Alphas
val GlassDark65 = Color(0x0D, 0x0F, 0x24, (255 * 0.65).toInt())
val GlassDark80 = Color(0x14, 0x18, 0x36, (255 * 0.80).toInt())
val GlassContainerLow85 = Color(0x1A, 0x1B, 0x26, (255 * 0.85).toInt())
val GlassWhite05 = Color(0xFF, 0xFF, 0xFF, (255 * 0.05).toInt())
val GlassWhite10 = Color(0xFF, 0xFF, 0xFF, (255 * 0.10).toInt())

// Gradients
val PrimaryButtonGradient = Brush.linearGradient(
    colors = listOf(TertiaryContainer, PrimaryContainer, SecondaryContainer)
)

val HeroCtaGradient = Brush.horizontalGradient(
    colors = listOf(HotPink, ElectricViolet, LuminousCyan)
)

val CardBorderGradient = Brush.verticalGradient(
    colors = listOf(
        ElectricViolet.copy(alpha = 0.35f),
        LuminousCyan.copy(alpha = 0.08f)
    )
)

val DonutPrincipalGradient = Brush.linearGradient(
    colors = listOf(ElectricViolet, LuminousCyan)
)

val DonutInterestGradient = Brush.linearGradient(
    colors = listOf(HotPink, Color(0xFFF43F5E))
)
