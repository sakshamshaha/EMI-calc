package com.example.emicalculatorapp.data.database

import androidx.room.TypeConverter
import com.example.emicalculatorapp.model.LoanCategory

class Converters {
    @TypeConverter
    fun fromCategory(category: LoanCategory): String = category.name

    @TypeConverter
    fun toCategory(value: String): LoanCategory = try {
        LoanCategory.valueOf(value)
    } catch (e: Exception) {
        LoanCategory.HOME
    }
}
