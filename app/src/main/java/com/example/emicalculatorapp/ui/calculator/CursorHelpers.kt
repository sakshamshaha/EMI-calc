package com.example.emicalculatorapp.ui.calculator

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.example.emicalculatorapp.data.settings.NumberFormatStyle
import com.example.emicalculatorapp.util.CurrencyFormatter

/**
 * Cursor and text formatting helpers for EMI input fields ensuring stable cursor position.
 */
fun formatAmountWithStableCursor(
    input: TextFieldValue,
    numberFormat: NumberFormatStyle = NumberFormatStyle.INDIAN,
    onAmountParsed: (Double) -> Unit
): TextFieldValue {
    return CurrencyFormatter.formatAmountWithStableCursor(
        input = input,
        formatStyle = numberFormat,
        onAmountParsed = onAmountParsed
    )
}

/**
 * Formats interest rate with stable cursor preservation.
 */
fun formatRateWithStableCursor(
    input: TextFieldValue,
    onRateParsed: (Double) -> Unit
): TextFieldValue {
    val rawText = input.text
    val cursorPos = input.selection.start
    var hasDot = false
    val sb = StringBuilder()
    var validCharsBeforeCursor = 0

    for (i in rawText.indices) {
        val c = rawText[i]
        if (c.isDigit()) {
            sb.append(c)
            if (i < cursorPos) validCharsBeforeCursor++
        } else if (c == '.' && !hasDot) {
            sb.append(c)
            hasDot = true
            if (i < cursorPos) validCharsBeforeCursor++
        }
    }
    val cleanStr = sb.toString().take(5)
    val parsed = cleanStr.toDoubleOrNull()
    if (parsed != null) {
        onRateParsed(parsed)
    }
    val newCursor = validCharsBeforeCursor.coerceIn(0, cleanStr.length)
    return TextFieldValue(cleanStr, TextRange(newCursor))
}

/**
 * Formats tenure with stable cursor preservation.
 */
fun formatTenureWithStableCursor(
    input: TextFieldValue,
    maxVal: Int,
    onTenureParsed: (Int) -> Unit
): TextFieldValue {
    val rawText = input.text
    val cursorPos = input.selection.start
    val digitsBeforeCursor = rawText.take(cursorPos).count { it.isDigit() }
    val cleanDigits = rawText.filter { it.isDigit() }.take(3)
    val parsed = cleanDigits.toIntOrNull()
    if (parsed != null) {
        onTenureParsed(parsed.coerceAtMost(maxVal))
    }
    val newCursor = digitsBeforeCursor.coerceIn(0, cleanDigits.length)
    return TextFieldValue(cleanDigits, TextRange(newCursor))
}
