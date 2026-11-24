package com.example.emicalculatorapp.viewmodel

import com.example.emicalculatorapp.data.database.QuoteEntity
import com.example.emicalculatorapp.data.database.QuoteRepository
import com.example.emicalculatorapp.model.LoanCategory
import com.example.emicalculatorapp.model.QuoteCalculations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SavedQuotesState(
    private val scope: CoroutineScope,
    private val repository: QuoteRepository
) {
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _quotes = MutableStateFlow<List<QuoteEntity>>(emptyList())
    val quotes: StateFlow<List<QuoteEntity>> = _quotes.asStateFlow()

    private val _selectedQuoteIds = MutableStateFlow<List<Long>>(emptyList())
    val selectedQuoteIds: StateFlow<List<Long>> = _selectedQuoteIds.asStateFlow()

    private val _lastDeletedQuote = MutableStateFlow<QuoteEntity?>(null)
    val lastDeletedQuote: StateFlow<QuoteEntity?> = _lastDeletedQuote.asStateFlow()

    init {
        scope.launch {
            repository.allQuotes.collect { quoteList ->
                _quotes.value = quoteList
                _isLoading.value = false
            }
        }
    }

    val isSelectionMode: Boolean
        get() = _selectedQuoteIds.value.isNotEmpty()

    fun saveQuote(
        name: String,
        category: LoanCategory,
        principal: Double,
        rate: Double,
        tenureMonths: Int,
        processingFeePercent: Double = 0.0,
        onSaved: (Long) -> Unit = {}
    ) {
        val validName = QuoteCalculations.validateQuoteName(name) ?: return
        scope.launch(Dispatchers.IO) {
            val entity = QuoteEntity(
                name = validName,
                category = category,
                principal = principal,
                annualRate = rate,
                tenureMonths = tenureMonths,
                processingFeePercent = processingFeePercent,
                createdAt = System.currentTimeMillis()
            )
            val newId = repository.insert(entity)
            withContext(Dispatchers.Main) {
                onSaved(newId)
            }
        }
    }

    fun deleteQuote(quote: QuoteEntity) {
        _lastDeletedQuote.value = quote
        scope.launch(Dispatchers.IO) {
            repository.delete(quote)
        }
    }

    fun undoDelete(onRestored: () -> Unit = {}) {
        val quoteToRestore = _lastDeletedQuote.value ?: return
        scope.launch(Dispatchers.IO) {
            repository.insert(quoteToRestore)
            _lastDeletedQuote.value = null
            withContext(Dispatchers.Main) {
                onRestored()
            }
        }
    }

    fun togglePin(quote: QuoteEntity) {
        scope.launch(Dispatchers.IO) {
            repository.togglePin(quote)
        }
    }

    fun renameQuote(id: Long, newName: String) {
        val validName = QuoteCalculations.validateQuoteName(newName) ?: return
        scope.launch(Dispatchers.IO) {
            repository.rename(id, validName)
        }
    }

    fun toggleSelectQuote(id: Long) {
        _selectedQuoteIds.value = QuoteCalculations.selectQuoteForComparison(_selectedQuoteIds.value, id)
    }

    fun clearSelection() {
        _selectedQuoteIds.value = emptyList()
    }
}
