package com.example.emicalculatorapp.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.emicalculatorapp.model.LoanCategory

@Entity(tableName = "quotes")
data class QuoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: LoanCategory,
    val principal: Double,
    val annualRate: Double,
    val tenureMonths: Int,
    val processingFeePercent: Double = 0.0,
    val isPinned: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
