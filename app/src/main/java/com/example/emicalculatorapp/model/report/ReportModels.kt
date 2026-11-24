package com.example.emicalculatorapp.model.report

enum class ReportCategory(val displayName: String) {
    BUG("Bug"),
    WRONG_CALCULATION("Wrong calculation"),
    SUGGESTION("Suggestion"),
    SOMETHING_ELSE("Something else")
}

object ReportStatus {
    const val DRAFT = "DRAFT"
    const val OPENED_IN_EMAIL = "OPENED_IN_EMAIL"
}

object ReportValidation {
    const val MIN_LENGTH = 10
    const val MAX_LENGTH = 1000

    fun isValid(description: String): Boolean {
        val trimmed = description.trim()
        return trimmed.length in MIN_LENGTH..MAX_LENGTH
    }

    fun getValidationMessage(description: String): String? {
        val trimmed = description.trim()
        return when {
            trimmed.isEmpty() -> "Description is required."
            trimmed.length < MIN_LENGTH -> "Description must be at least $MIN_LENGTH characters (currently ${trimmed.length})."
            trimmed.length > MAX_LENGTH -> "Description cannot exceed $MAX_LENGTH characters (currently ${trimmed.length})."
            else -> null
        }
    }
}
