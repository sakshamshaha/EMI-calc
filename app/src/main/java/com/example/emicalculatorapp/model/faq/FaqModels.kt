package com.example.emicalculatorapp.model.faq

data class FaqItem(
    val id: String,
    val category: String,
    val question: String,
    val answer: String,
    val actionTag: String? = null
)

object FaqSearch {
    fun filter(items: List<FaqItem>, query: String): List<FaqItem> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return items
        val terms = trimmed.lowercase().split("\\s+".toRegex()).filter { it.isNotBlank() }
        if (terms.isEmpty()) return items

        return items.filter { item ->
            val q = item.question.lowercase()
            val a = item.answer.lowercase()
            terms.all { term -> q.contains(term) || a.contains(term) }
        }
    }
}
