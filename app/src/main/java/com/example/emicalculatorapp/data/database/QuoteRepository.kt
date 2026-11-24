package com.example.emicalculatorapp.data.database

import kotlinx.coroutines.flow.Flow

class QuoteRepository(private val quoteDao: QuoteDao) {

    val allQuotes: Flow<List<QuoteEntity>> = quoteDao.getAllQuotes()

    suspend fun getQuoteById(id: Long): QuoteEntity? = quoteDao.getQuoteById(id)

    suspend fun insert(quote: QuoteEntity): Long = quoteDao.insertQuote(quote)

    suspend fun update(quote: QuoteEntity) = quoteDao.updateQuote(quote)

    suspend fun delete(quote: QuoteEntity) = quoteDao.deleteQuote(quote)

    suspend fun togglePin(quote: QuoteEntity) = quoteDao.updatePinStatus(quote.id, !quote.isPinned)

    suspend fun rename(id: Long, newName: String) = quoteDao.updateName(id, newName)
}
