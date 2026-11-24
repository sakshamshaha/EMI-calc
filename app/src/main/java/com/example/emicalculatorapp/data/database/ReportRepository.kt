package com.example.emicalculatorapp.data.database

import kotlinx.coroutines.flow.Flow

class ReportRepository(private val reportDao: ReportDao) {
    fun getAllReports(): Flow<List<ReportEntity>> = reportDao.getAllReports()

    suspend fun getReportById(id: Long): ReportEntity? = reportDao.getReportById(id)

    suspend fun insertReport(report: ReportEntity): Long = reportDao.insertReport(report)

    suspend fun updateReport(report: ReportEntity): Int = reportDao.updateReport(report)

    suspend fun deleteReport(report: ReportEntity): Int = reportDao.deleteReport(report)

    suspend fun deleteReportById(id: Long): Int = reportDao.deleteReportById(id)

    suspend fun updateStatus(id: Long, status: String): Int = reportDao.updateStatus(id, status)
}
