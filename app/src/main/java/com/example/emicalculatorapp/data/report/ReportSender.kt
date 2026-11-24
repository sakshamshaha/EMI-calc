package com.example.emicalculatorapp.data.report

import android.content.Context
import com.example.emicalculatorapp.data.database.ReportEntity

interface ReportSender {
    suspend fun sendReport(
        context: Context,
        report: ReportEntity,
        onOpenedInEmail: () -> Unit,
        onNoEmailApp: (reportText: String) -> Unit
    )
}
