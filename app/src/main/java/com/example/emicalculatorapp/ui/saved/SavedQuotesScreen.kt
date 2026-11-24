package com.example.emicalculatorapp.ui.saved

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emicalculatorapp.ui.components.GlassCard
import com.example.emicalculatorapp.ui.components.fx.SweepBorderCard
import com.example.emicalculatorapp.ui.theme.CanvasColor
import com.example.emicalculatorapp.ui.theme.ElectricViolet
import com.example.emicalculatorapp.ui.theme.InterFamily
import com.example.emicalculatorapp.ui.theme.LuminousCyan
import com.example.emicalculatorapp.ui.theme.SpaceGroteskFamily
import com.example.emicalculatorapp.ui.theme.TextPrimary
import com.example.emicalculatorapp.ui.theme.TextSecondary

/**
 * Saved Quotes screen stub.
 * Full local Room database storage, swipe-to-delete with undo, and quote loading arrive in Phase 6.
 */
@Composable
fun SavedQuotesScreen(
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasColor)
            .padding(horizontal = 20.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Saved Quotes",
            fontFamily = SpaceGroteskFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = TextPrimary
        )
        Text(
            text = "Locally preserved loan calculations, comparisons & scenarios",
            fontFamily = InterFamily,
            fontSize = 13.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
        )

        SweepBorderCard(
            modifier = Modifier.fillMaxWidth(),
            gradientColors = listOf(ElectricViolet, LuminousCyan, ElectricViolet)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = null,
                        tint = LuminousCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Saved Loan Quotes",
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextPrimary,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Your saved quotes will appear here. Phase 6 integrates full Room persistence for saving custom quotes, animated vector checkmarks, celebratory feedback, and instant reload into the calculator.",
                    fontFamily = InterFamily,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(ElectricViolet.copy(alpha = 0.2f))
                        .border(1.dp, ElectricViolet.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "💾 Arriving in Phase 6",
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = LuminousCyan
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Estimates only, not financial advice.",
            fontFamily = InterFamily,
            fontSize = 11.sp,
            color = TextSecondary.copy(alpha = 0.6f),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(100.dp))
    }
}
