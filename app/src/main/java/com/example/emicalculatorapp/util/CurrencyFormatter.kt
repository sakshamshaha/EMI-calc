package com.example.emicalculatorapp.util

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.example.emicalculatorapp.data.settings.NumberFormatStyle
import java.util.Locale
import kotlin.math.abs
import kotlin.math.round

/**
 * Universal Currency & Number Formatting Utility supporting both Indian
 * (Lakhs / Crores) and International (Millions / Billions) grouping,
 * along with stable cursor preservation during real-time user input.
 */
object CurrencyFormatter {

    /**
     * Formats numbers into native Indian grouping style (12,34,567) with ₹ symbol.
     */
    fun formatIndianCurrency(amount: Double, includeDecimals: Boolean = false): String {
        val roundedAmount = round(amount).toLong()
        val isNegative = roundedAmount < 0
        val absVal = abs(roundedAmount)
        val s = absVal.toString()
        val grouped = if (s.length <= 3) {
            s
        } else {
            val lastThree = s.substring(s.length - 3)
            val remaining = s.substring(0, s.length - 3)
            val sb = StringBuilder()
            var count = 0
            for (i in remaining.length - 1 downTo 0) {
                sb.append(remaining[i])
                count++
                if (count == 2 && i != 0) {
                    sb.append(',')
                    count = 0
                }
            }
            sb.reverse().toString() + "," + lastThree
        }

        val prefix = if (isNegative) "-₹ " else "₹ "
        return if (includeDecimals) {
            val cents = abs((amount * 100).toLong() % 100)
            String.format(Locale.US, "%s%s.%02d", prefix, grouped, cents)
        } else {
            "$prefix$grouped"
        }
    }

    /**
     * Formats numbers into International grouping style (1,234,567) with ₹ symbol.
     */
    fun formatInternationalCurrency(amount: Double, includeDecimals: Boolean = false): String {
        val roundedAmount = round(amount).toLong()
        val isNegative = roundedAmount < 0
        val absVal = abs(roundedAmount)
        val s = absVal.toString()
        val grouped = if (s.length <= 3) {
            s
        } else {
            val lastThree = s.substring(s.length - 3)
            val remaining = s.substring(0, s.length - 3)
            val sb = StringBuilder()
            var count = 0
            for (i in remaining.length - 1 downTo 0) {
                sb.append(remaining[i])
                count++
                if (count == 3 && i != 0) {
                    sb.append(',')
                    count = 0
                }
            }
            sb.reverse().toString() + "," + lastThree
        }

        val prefix = if (isNegative) "-₹ " else "₹ "
        return if (includeDecimals) {
            val cents = abs((amount * 100).toLong() % 100)
            String.format(Locale.US, "%s%s.%02d", prefix, grouped, cents)
        } else {
            "$prefix$grouped"
        }
    }

    /**
     * General currency formatter respecting user's selected NumberFormatStyle.
     */
    fun formatCurrency(
        amount: Double,
        formatStyle: NumberFormatStyle = NumberFormatStyle.INDIAN,
        includeDecimals: Boolean = false
    ): String {
        return when (formatStyle) {
            NumberFormatStyle.INDIAN -> formatIndianCurrency(amount, includeDecimals)
            NumberFormatStyle.INTERNATIONAL -> formatInternationalCurrency(amount, includeDecimals)
        }
    }

    /**
     * Compact Indian representation (₹1.5 Cr, ₹45.0 L, ₹50 K).
     */
    fun formatCompactIndianLakh(amount: Double): String {
        val lakhs = amount / 100000.0
        return if (lakhs >= 100.0) {
            String.format(Locale.US, "₹%.1f Cr", lakhs / 100.0)
        } else if (lakhs >= 1.0) {
            String.format(Locale.US, "₹%.1f L", lakhs)
        } else {
            String.format(Locale.US, "₹%.0f K", amount / 1000.0)
        }
    }

    /**
     * Compact International representation (₹1.5 B, ₹4.5 M, ₹50 K).
     */
    fun formatCompactInternational(amount: Double): String {
        val millions = amount / 1000000.0
        return if (millions >= 1000.0) {
            String.format(Locale.US, "₹%.1f B", millions / 1000.0)
        } else if (millions >= 1.0) {
            String.format(Locale.US, "₹%.1f M", millions)
        } else {
            String.format(Locale.US, "₹%.0f K", amount / 1000.0)
        }
    }

    /**
     * Compact currency formatter respecting user's NumberFormatStyle.
     */
    fun formatCompactCurrency(
        amount: Double,
        formatStyle: NumberFormatStyle = NumberFormatStyle.INDIAN
    ): String {
        return when (formatStyle) {
            NumberFormatStyle.INDIAN -> formatCompactIndianLakh(amount)
            NumberFormatStyle.INTERNATIONAL -> formatCompactInternational(amount)
        }
    }

    /**
     * Formats user text field input with stable cursor preservation.
     * Works seamlessly across BOTH Indian and International comma groupings:
     * - Typing at start, middle, or end keeps cursor precisely positioned after newly added digits.
     * - Deleting digits in the middle preserves cursor position without jumping.
     * - Pasting long digit sequences formats and places cursor at the end of the pasted value.
     */
    fun formatAmountWithStableCursor(
        input: TextFieldValue,
        formatStyle: NumberFormatStyle = NumberFormatStyle.INDIAN,
        onAmountParsed: (Double) -> Unit = {}
    ): TextFieldValue {
        val rawText = input.text
        val cursorPos = input.selection.start
        val digitsBeforeCursor = rawText.take(cursorPos).count { it.isDigit() }
        val cleanDigits = rawText.filter { it.isDigit() }.take(9)

        if (cleanDigits.isEmpty()) {
            onAmountParsed(0.0)
            return TextFieldValue("", TextRange(0))
        }

        val parsed = cleanDigits.toDoubleOrNull() ?: 0.0
        onAmountParsed(parsed)

        val formatted = formatCurrency(parsed, formatStyle)
            .replace("₹ ", "")
            .replace("₹", "")
            .trim()

        var targetCursor = 0
        var digitCount = 0
        for (i in formatted.indices) {
            if (digitCount == digitsBeforeCursor) {
                targetCursor = i
                break
            }
            if (formatted[i].isDigit()) {
                digitCount++
            }
            if (digitCount == digitsBeforeCursor) {
                targetCursor = i + 1
                break
            }
        }
        if (digitCount < digitsBeforeCursor) {
            targetCursor = formatted.length
        }

        return TextFieldValue(formatted, TextRange(targetCursor))
    }
}
