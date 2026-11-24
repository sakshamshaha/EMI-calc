package com.example.emicalculatorapp

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.emicalculatorapp.data.database.AppDatabase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Migration test validating Schema 1 -> Schema 2 transition.
 * Verifies that the new `reports` table is created while all existing
 * saved quotes in `quotes` table remain intact.
 */
@RunWith(AndroidJUnit4::class)
class AppDatabaseMigrationTest {

    private val TEST_DB = "migration-test-db"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrate1To2_preservesSavedQuotes() {
        // Create DB in version 1 and insert a quote
        var db = helper.createDatabase(TEST_DB, 1).apply {
            execSQL(
                """
                INSERT INTO quotes (id, name, category, principal, annualRate, tenureMonths, processingFeePercent, isPinned, createdAt)
                VALUES (1, 'Dream Home', 'HOME', 4500000.0, 8.55, 240, 0.5, 1, 123456789)
                """.trimIndent()
            )
            close()
        }

        // Migrate to version 2 using MIGRATION_1_2
        db = helper.runMigrationsAndValidate(TEST_DB, 2, true, AppDatabase.MIGRATION_1_2)

        // Verify quote survived
        val cursor = db.query("SELECT * FROM quotes WHERE id = 1")
        assertTrue(cursor.moveToFirst())
        assertEquals("Dream Home", cursor.getString(cursor.getColumnIndexOrThrow("name")))
        assertEquals(4500000.0, cursor.getDouble(cursor.getColumnIndexOrThrow("principal")), 0.01)
        assertEquals(8.55, cursor.getDouble(cursor.getColumnIndexOrThrow("annualRate")), 0.01)
        assertEquals(240, cursor.getInt(cursor.getColumnIndexOrThrow("tenureMonths")))
        assertEquals(1, cursor.getInt(cursor.getColumnIndexOrThrow("isPinned")))
        cursor.close()

        // Verify reports table exists and accepts insertion
        db.execSQL(
            """
            INSERT INTO reports (id, category, description, diagnosticsText, screenshotPath, status, createdAt)
            VALUES (1, 'Bug', 'Test report post migration', 'Diag info', NULL, 'DRAFT', 987654321)
            """.trimIndent()
        )
        val reportCursor = db.query("SELECT * FROM reports WHERE id = 1")
        assertTrue(reportCursor.moveToFirst())
        assertEquals("Bug", reportCursor.getString(reportCursor.getColumnIndexOrThrow("category")))
        assertEquals("Test report post migration", reportCursor.getString(reportCursor.getColumnIndexOrThrow("description")))
        reportCursor.close()
    }
}
