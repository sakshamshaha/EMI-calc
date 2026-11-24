package com.example.emicalculatorapp.model

enum class LoanCategory(
    val title: String,
    val defaultAmount: Double,
    val defaultRate: Double,
    val defaultTenureYears: Int
) {
    HOME("Home", 4500000.0, 8.55, 20),
    CAR("Car", 1200000.0, 9.20, 5),
    PERSONAL("Personal", 500000.0, 12.50, 3),
    EDUCATION("Education", 2000000.0, 8.95, 8)
}
