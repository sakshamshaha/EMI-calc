package com.example.emicalculatorapp

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.emicalculatorapp.data.database.AppDatabase
import com.example.emicalculatorapp.data.database.QuoteDao
import com.example.emicalculatorapp.data.database.QuoteEntity
import com.example.emicalculatorapp.model.LoanCategory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Instrumented Room DAO tests for [QuoteDao].
 * Verifies in-memory database CRUD, undo-reinsert preservation,
 * sort order (pinned first, then newest), and reactive Flow emission.
 *
 * To run from Android Studio:
 * Right click this class -> Run 'QuoteDaoTest' on an emulator or physical device.
 */
@RunWith(AndroidJUnit4::class)
class QuoteDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var quoteDao: QuoteDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        quoteDao = db.quoteDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndGetQuote() = runBlocking {
        val quote = QuoteEntity(
            name = "Home Loan 45L",
            category = LoanCategory.HOME,
            principal = 4500000.0,
            annualRate = 8.55,
            tenureMonths = 240
        )
        val insertedId = quoteDao.insertQuote(quote)
        assertTrue(insertedId > 0)

        val retrieved = quoteDao.getQuoteById(insertedId)
        assertNotNull(retrieved)
        assertEquals("Home Loan 45L", retrieved?.name)
        assertEquals(4500000.0, retrieved?.principal ?: 0.0, 0.01)
    }

    @Test
    fun updateQuoteAndPinStatus() = runBlocking {
        val quote = QuoteEntity(
            name = "Car Loan",
            category = LoanCategory.CAR,
            principal = 1200000.0,
            annualRate = 9.20,
            tenureMonths = 60,
            isPinned = false
        )
        val id = quoteDao.insertQuote(quote)

        // Toggle Pin
        quoteDao.updatePinStatus(id, true)
        var updated = quoteDao.getQuoteById(id)
        assertTrue(updated?.isPinned == true)

        // Rename
        quoteDao.updateName(id, "Dream SUV Loan")
        updated = quoteDao.getQuoteById(id)
        assertEquals("Dream SUV Loan", updated?.name)
    }

    @Test
    fun deleteAndUndoReinsertWithSameId() = runBlocking {
        val quote = QuoteEntity(
            name = "Personal Loan",
            category = LoanCategory.PERSONAL,
            principal = 500000.0,
            annualRate = 12.5,
            tenureMonths = 36
        )
        val id = quoteDao.insertQuote(quote)
        val inserted = quoteDao.getQuoteById(id)!!

        // Delete
        quoteDao.deleteQuote(inserted)
        assertNull(quoteDao.getQuoteById(id))

        // Undo: Reinsert the exact same row retaining id and timestamps
        quoteDao.insertQuote(inserted)
        val restored = quoteDao.getQuoteById(id)
        assertNotNull(restored)
        assertEquals(id, restored?.id)
        assertEquals("Personal Loan", restored?.name)
    }

    @Test
    fun orderingPinnedFirstThenNewest() = runBlocking {
        val q1 = QuoteEntity(name = "Older Normal", category = LoanCategory.PERSONAL, principal = p100k(), annualRate = 12.0, tenureMonths = 12, isPinned = false, createdAt = 1000L)
        val q2 = QuoteEntity(name = "Newer Normal", category = LoanCategory.CAR, principal = p500k(), annualRate = 9.0, tenureMonths = 36, isPinned = false, createdAt = 2000L)
        val q3 = QuoteEntity(name = "Older Pinned", category = LoanCategory.HOME, principal = p4500k(), annualRate = 8.55, tenureMonths = 240, isPinned = true, createdAt = 1500L)
        val q4 = QuoteEntity(name = "Newest Pinned", category = LoanCategory.HOME, principal = p5000k(), annualRate = 8.40, tenureMonths = 240, isPinned = true, createdAt = 3000L)

        val id1 = quoteDao.insertQuote(q1)
        val id2 = quoteDao.insertQuote(q2)
        val id3 = quoteDao.insertQuote(q3)
        val id4 = quoteDao.insertQuote(q4)

        val all = quoteDao.getAllQuotes().first()
        assertEquals(listOf(id4, id3, id2, id1), all.map { it.id })
    }

    private fun p100k() = 100000.0
    private fun p500k() = 500000.0
    private fun p4500k() = 4500000.0
    private fun p5000k() = 5000000.0
}
