package com.example.emicalculatorapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.emicalculatorapp.ui.theme.CardBorderGradient
import com.example.emicalculatorapp.ui.theme.ElectricViolet
import com.example.emicalculatorapp.ui.theme.GlassDark65
import com.example.emicalculatorapp.ui.theme.MajorCardShape

/**
 * High-performance Glassmorphic Card.
 * Uses translucent gradient background + multi-stop neon border stroke + soft ambient glow.
 * Eliminates expensive RenderEffect GPU blur passes on scrolling/animating surfaces for 60/120fps fluid scrolling.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = MajorCardShape,
    backgroundColor: Color = GlassDark65,
    borderGradient: Brush = CardBorderGradient,
    borderWidth: Dp = 1.dp,
    glowColor: Color = ElectricViolet.copy(alpha = 0.12f),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .drawBehind {
                // Soft ambient neon glow shadow behind card
                drawCircle(
                    color = glowColor,
                    radius = size.maxDimension * 0.5f,
                    center = center
                )
            }
            .clip(shape)
            .background(backgroundColor)
            .border(
                width = borderWidth,
                brush = borderGradient,
                shape = shape
            ),
        content = content
    )
}
