package com.example.emicalculatorapp.ui.affordability

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emicalculatorapp.data.settings.NumberFormatStyle
import com.example.emicalculatorapp.ui.theme.MintGreen
import com.example.emicalculatorapp.ui.theme.OutlineVariant
import com.example.emicalculatorapp.ui.theme.SpaceGroteskFamily
import com.example.emicalculatorapp.ui.theme.SurfaceContainerLowest
import com.example.emicalculatorapp.ui.theme.TextPrimary
import com.example.emicalculatorapp.viewmodel.EMIViewModel

@Composable
fun IncomeInputPill(
    amount: Double,
    onAmountChanged: (Double) -> Unit,
    numberFormat: NumberFormatStyle,
    focusManager: FocusManager
) {
    var rawText by remember(amount, numberFormat) {
        val formatted = EMIViewModel.formatCurrency(amount, numberFormat).replace("₹", "").trim()
        mutableStateOf(TextFieldValue(formatted, TextRange(formatted.length)))
    }
    var isFocused by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .width(135.dp)
            .height(38.dp)
            .clip(CircleShape)
            .background(SurfaceContainerLowest)
            .border(1.dp, if (isFocused) MintGreen else OutlineVariant, CircleShape)
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "₹", fontFamily = SpaceGroteskFamily, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MintGreen)
            BasicTextField(
                value = rawText,
                onValueChange = { input ->
                    rawText = EMIViewModel.formatAmountWithStableCursor(
                        input = input,
                        formatStyle = numberFormat,
                        onAmountParsed = { onAmountChanged(it) }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { isFocused = it.isFocused },
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = SpaceGroteskFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.End
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                singleLine = true,
                cursorBrush = SolidColor(MintGreen)
            )
        }
    }
}
