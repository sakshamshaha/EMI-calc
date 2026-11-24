package com.example.emicalculatorapp.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: String,
    val description: String,
    val diagnosticsText: String,
    val screenshotPath: String? = null,
    val status: String,
    val createdAt: Long = System.currentTimeMillis()
)
