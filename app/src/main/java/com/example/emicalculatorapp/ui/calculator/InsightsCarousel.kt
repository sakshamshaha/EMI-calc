package com.example.emicalculatorapp.ui.calculator

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.emicalculatorapp.data.settings.EffectsLevel
import com.example.emicalculatorapp.model.InsightIcon
import com.example.emicalculatorapp.model.LoanInsight
import com.example.emicalculatorapp.ui.components.GlassCard
import com.example.emicalculatorapp.ui.components.fx.OdometerText
import com.example.emicalculatorapp.ui.theme.ElectricViolet
import com.example.emicalculatorapp.ui.theme.InterFamily
import com.example.emicalculatorapp.ui.theme.LocalEffectsLevel
import com.example.emicalculatorapp.ui.theme.LocalReduceMotion
import com.example.emicalculatorapp.ui.theme.LuminousCyan
import com.example.emicalculatorapp.ui.theme.MintGreen
import com.example.emicalculatorapp.ui.theme.SpaceGroteskFamily
import com.example.emicalculatorapp.ui.theme.SurfaceContainerHigh
import com.example.emicalculatorapp.ui.theme.TextPrimary
import com.example.emicalculatorapp.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun InsightsCarousel(
    insights: List<LoanInsight>,
    modifier: Modifier = Modifier
) {
    if (insights.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { insights.size })
    val effectsLevel = LocalEffectsLevel.current
    val reduceMotion = LocalReduceMotion.current

    var isScreenResumed by remember { mutableStateOf(true) }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isScreenResumed = true
            } else if (event == Lifecycle.Event.ON_PAUSE) {
                isScreenResumed = false
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val canAutoAdvance = !reduceMotion && effectsLevel != EffectsLevel.LITE && isScreenResumed && !pagerState.isScrollInProgress

    LaunchedEffect(canAutoAdvance, pagerState.currentPage, insights.size) {
        if (canAutoAdvance && insights.size > 1) {
            delay(5000L)
            val nextPage = (pagerState.currentPage + 1) % insights.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SMART INSIGHTS",
                    fontFamily = SpaceGroteskFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = LuminousCyan,
                    letterSpacing = 0.8.sp
                )

                // Animated Page Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(insights.size) { index ->
                        val isSelected = pagerState.currentPage == index
                        val dotWidth by animateDpAsState(
                            targetValue = if (isSelected) 16.dp else 6.dp,
                            animationSpec = tween(durationMillis = 200),
                            label = "DotWidth"
                        )
                        Box(
                            modifier = Modifier
                                .height(6.dp)
                                .width(dotWidth)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) LuminousCyan else SurfaceContainerHigh
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth()
            ) { page ->
                val insight = insights[page]
                InsightCardContent(insight = insight)
            }
        }
    }
}

@Composable
private fun InsightCardContent(insight: LoanInsight) {
    val (iconTint, iconBg) = when (insight.icon) {
        InsightIcon.PIE_CHART -> ElectricViolet to ElectricViolet.copy(alpha = 0.15f)
        InsightIcon.TRENDING_DOWN -> MintGreen to MintGreen.copy(alpha = 0.15f)
        InsightIcon.PERCENT -> LuminousCyan to LuminousCyan.copy(alpha = 0.15f)
        InsightIcon.BOLT -> MintGreen to MintGreen.copy(alpha = 0.15f)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = getInsightVector(insight.icon),
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            OdometerText(
                text = insight.headlineDisplay,
                isContinuous = false,
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = SpaceGroteskFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = TextPrimary
                ),
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = insight.description,
                fontFamily = InterFamily,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )
        }
    }
}

private fun getInsightVector(icon: InsightIcon): ImageVector = when (icon) {
    InsightIcon.PIE_CHART -> Icons.Default.PieChart
    InsightIcon.TRENDING_DOWN -> Icons.AutoMirrored.Filled.TrendingDown
    InsightIcon.PERCENT -> Icons.Default.Percent
    InsightIcon.BOLT -> Icons.Default.Bolt
}
