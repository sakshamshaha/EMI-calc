package com.example.emicalculatorapp.data.settings

/**
 * Number formatting style for currency presentation.
 * - INDIAN: Traditional Lakh/Crore grouping (e.g. ₹ 45,00,000)
 * - INTERNATIONAL: Standard Million/Billion grouping (e.g. ₹ 4,500,000)
 */
enum class NumberFormatStyle {
    INDIAN,
    INTERNATIONAL;

    val displayName: String
        get() = when (this) {
            INDIAN -> "Indian (Lakhs / Crores)"
            INTERNATIONAL -> "International (Millions / Billions)"
        }

    val example: String
        get() = when (this) {
            INDIAN -> "₹ 45,00,000"
            INTERNATIONAL -> "₹ 4,500,000"
        }
}
