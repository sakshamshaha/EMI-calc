package com.example.emicalculatorapp

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.emicalculatorapp.data.database.AppDatabase
import com.example.emicalculatorapp.data.database.ReportDao
import com.example.emicalculatorapp.data.database.ReportEntity
import com.example.emicalculatorapp.model.report.ReportStatus
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

@RunWith(AndroidJUnit4::class)
class ReportDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var reportDao: ReportDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        reportDao = db.reportDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndGetReport() = runBlocking {
        val report = ReportEntity(
            category = "Bug",
            description = "Slider jumps unexpectedly on drag",
            diagnosticsText = "Sample diagnostics",
            screenshotPath = null,
            status = ReportStatus.DRAFT,
            createdAt = 1000L
        )

        val id = reportDao.insertReport(report)
        assertTrue(id > 0)

        val retrieved = reportDao.getReportById(id)
        assertNotNull(retrieved)
        assertEquals("Bug", retrieved?.category)
        assertEquals("Slider jumps unexpectedly on drag", retrieved?.description)
        assertEquals(ReportStatus.DRAFT, retrieved?.status)
    }

    @Test
    fun updateReportStatus() = runBlocking {
        val report = ReportEntity(
            category = "Suggestion",
            description = "Add dark mode tint options",
            diagnosticsText = "",
            status = ReportStatus.DRAFT,
            createdAt = 2000L
        )

        val id = reportDao.insertReport(report)
        reportDao.updateStatus(id, ReportStatus.OPENED_IN_EMAIL)

        val updated = reportDao.getReportById(id)
        assertEquals(ReportStatus.OPENED_IN_EMAIL, updated?.status)
    }

    @Test
    fun deleteReport() = runBlocking {
        val report = ReportEntity(
            category = "Wrong calculation",
            description = "Comparing loan error",
            diagnosticsText = "",
            status = ReportStatus.DRAFT,
            createdAt = 3000L
        )

        val id = reportDao.insertReport(report)
        assertNotNull(reportDao.getReportById(id))

        reportDao.deleteReportById(id)
        assertNull(reportDao.getReportById(id))
    }

    @Test
    fun getAllReportsSortedNewestFirst() = runBlocking {
        val r1 = ReportEntity(category = "Bug", description = "Report 1", diagnosticsText = "", status = ReportStatus.DRAFT, createdAt = 1000L)
        val r2 = ReportEntity(category = "Suggestion", description = "Report 2", diagnosticsText = "", status = ReportStatus.DRAFT, createdAt = 5000L)
        val r3 = ReportEntity(category = "Something else", description = "Report 3", diagnosticsText = "", status = ReportStatus.DRAFT, createdAt = 3000L)

        reportDao.insertReport(r1)
        reportDao.insertReport(r2)
        reportDao.insertReport(r3)

        val list = reportDao.getAllReports().first()
        assertEquals(3, list.size)
        assertEquals(5000L, list[0].createdAt)
        assertEquals(3000L, list[1].createdAt)
        assertEquals(1000L, list[2].createdAt)
    }
}
