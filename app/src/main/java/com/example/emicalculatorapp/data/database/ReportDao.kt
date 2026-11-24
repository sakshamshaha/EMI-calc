package com.example.emicalculatorapp.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDao {

    @Query("SELECT * FROM reports ORDER BY createdAt DESC")
    fun getAllReports(): Flow<List<ReportEntity>>

    @Query("SELECT * FROM reports WHERE id = :id")
    suspend fun getReportById(id: Long): ReportEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportEntity): Long

    @Update
    suspend fun updateReport(report: ReportEntity): Int

    @Delete
    suspend fun deleteReport(report: ReportEntity): Int

    @Query("DELETE FROM reports WHERE id = :id")
    suspend fun deleteReportById(id: Long): Int

    @Query("UPDATE reports SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String): Int
}
