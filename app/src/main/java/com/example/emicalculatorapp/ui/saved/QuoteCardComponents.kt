package com.example.emicalculatorapp.ui.saved

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emicalculatorapp.data.database.QuoteEntity
import com.example.emicalculatorapp.data.settings.NumberFormatStyle
import com.example.emicalculatorapp.model.ComputedQuote
import com.example.emicalculatorapp.ui.theme.HotPink
import com.example.emicalculatorapp.ui.theme.InterFamily
import com.example.emicalculatorapp.ui.theme.LuminousCyan
import com.example.emicalculatorapp.ui.theme.SpaceGroteskFamily
import com.example.emicalculatorapp.ui.theme.TextMuted
import com.example.emicalculatorapp.ui.theme.TextPrimary
import com.example.emicalculatorapp.viewmodel.EMIViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuoteCardDismissBackground(
    dismissDirection: SwipeToDismissBoxValue,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(HotPink.copy(alpha = 0.85f))
            .padding(horizontal = 24.dp),
        contentAlignment = if (dismissDirection == SwipeToDismissBoxValue.StartToEnd) {
            Alignment.CenterStart
        } else {
            Alignment.CenterEnd
        }
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete",
            tint = TextPrimary,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun QuoteMetricsGrid(
    quote: QuoteEntity,
    computed: ComputedQuote,
    numberFormat: NumberFormatStyle,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = "Amount", fontFamily = InterFamily, fontSize = 11.sp, color = TextMuted)
            Text(
                text = EMIViewModel.formatCurrency(quote.principal, numberFormat),
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = TextPrimary
            )
        }
        Column {
            Text(text = "Rate / Tenure", fontFamily = InterFamily, fontSize = 11.sp, color = TextMuted)
            Text(
                text = "${String.format(Locale.US, "%.2f%%", quote.annualRate)} • ${computed.formattedTenure}",
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = TextPrimary
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(text = "Monthly EMI", fontFamily = InterFamily, fontSize = 11.sp, color = TextMuted)
            Text(
                text = EMIViewModel.formatCurrency(computed.monthlyEMI, numberFormat),
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = LuminousCyan
            )
        }
    }
}
