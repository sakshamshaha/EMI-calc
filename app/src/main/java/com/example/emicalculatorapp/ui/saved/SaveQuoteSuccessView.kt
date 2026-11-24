package com.example.emicalculatorapp.ui.saved

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emicalculatorapp.data.settings.EffectsLevel
import com.example.emicalculatorapp.ui.components.ConfettiBurst
import com.example.emicalculatorapp.ui.components.fx.PathCheckmark
import com.example.emicalculatorapp.ui.theme.InterFamily
import com.example.emicalculatorapp.ui.theme.MintGreen
import com.example.emicalculatorapp.ui.theme.SpaceGroteskFamily
import com.example.emicalculatorapp.ui.theme.TextPrimary
import com.example.emicalculatorapp.ui.theme.TextSecondary

@Composable
fun BoxScope.SaveQuoteSuccessView(
    quoteName: String,
    showConfetti: Boolean,
    onConfettiComplete: () -> Unit,
    effectsLevel: EffectsLevel,
    reduceMotion: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        PathCheckmark(
            size = 64.dp,
            color = MintGreen,
            strokeWidth = 5.dp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Quote Saved Successfully!",
            fontFamily = SpaceGroteskFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = TextPrimary
        )

        Text(
            text = quoteName.trim(),
            fontFamily = InterFamily,
            fontSize = 13.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 4.dp)
        )
    }

    if (!reduceMotion && effectsLevel != EffectsLevel.LITE) {
        ConfettiBurst(
            trigger = showConfetti,
            onComplete = onConfettiComplete,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
