package com.example.emicalculatorapp.ui.calculator

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.graphicsLayer
import com.example.emicalculatorapp.ui.components.fx.AnimatedLoanCategoryIcon
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.emicalculatorapp.model.LoanCategory
import com.example.emicalculatorapp.ui.theme.EMIverseTypography
import com.example.emicalculatorapp.ui.theme.HeroCtaGradient
import com.example.emicalculatorapp.ui.theme.HotPink
import com.example.emicalculatorapp.ui.theme.LocalReduceMotion
import com.example.emicalculatorapp.ui.theme.LuminousCyan
import com.example.emicalculatorapp.ui.theme.SurfaceContainerHigh
import com.example.emicalculatorapp.ui.theme.TextSecondary

@Composable
fun LoanCategoryPills(
    selectedCategory: LoanCategory,
    onCategorySelected: (LoanCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(bottom = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CategoryPill(
            title = "Home",
            category = LoanCategory.HOME,
            isSelected = selectedCategory == LoanCategory.HOME,
            onClick = { onCategorySelected(LoanCategory.HOME) }
        )
        CategoryPill(
            title = "Car",
            category = LoanCategory.CAR,
            isSelected = selectedCategory == LoanCategory.CAR,
            onClick = { onCategorySelected(LoanCategory.CAR) }
        )
        CategoryPill(
            title = "Personal",
            category = LoanCategory.PERSONAL,
            isSelected = selectedCategory == LoanCategory.PERSONAL,
            onClick = { onCategorySelected(LoanCategory.PERSONAL) }
        )
        CategoryPill(
            title = "Education",
            category = LoanCategory.EDUCATION,
            isSelected = selectedCategory == LoanCategory.EDUCATION,
            onClick = { onCategorySelected(LoanCategory.EDUCATION) }
        )
    }
}

@Composable
fun CategoryPill(
    title: String,
    category: LoanCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val reduceMotion = LocalReduceMotion.current
    val haptic = LocalHapticFeedback.current

    val scale by animateFloatAsState(
        targetValue = when {
            isPressed && !reduceMotion -> 0.94f
            isSelected && !reduceMotion -> 1.04f
            else -> 1.0f
        },
        animationSpec = if (reduceMotion) tween(0) else spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "PillScale"
    )

    Row(
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .then(
                if (isSelected) {
                    Modifier.drawBehind {
                        drawCircle(
                            color = HotPink.copy(alpha = 0.4f),
                            radius = size.maxDimension * 0.45f
                        )
                    }
                } else {
                    Modifier
                }
            )
            .clip(CircleShape)
            .then(
                if (isSelected) {
                    Modifier.background(HeroCtaGradient)
                } else {
                    Modifier
                        .background(SurfaceContainerHigh.copy(alpha = 0.7f))
                        .border(1.dp, Color.White.copy(alpha = 0.12f), CircleShape)
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = LuminousCyan),
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onClick()
                }
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        AnimatedLoanCategoryIcon(
            category = category,
            isSelected = isSelected,
            size = 18.dp,
            tint = if (isSelected) Color.White else TextSecondary
        )
        Text(
            text = title,
            style = EMIverseTypography.labelMd,
            color = if (isSelected) Color.White else TextSecondary,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
