package com.example.emicalculatorapp.data.report

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.emicalculatorapp.data.database.AppDatabase
import com.example.emicalculatorapp.model.report.ReportStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChosenReportReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val reportId = intent.getLongExtra(EXTRA_REPORT_ID, -1L)
        if (reportId > 0) {
            val db = AppDatabase.getInstance(context.applicationContext)
            CoroutineScope(Dispatchers.IO).launch {
                db.reportDao().updateStatus(reportId, ReportStatus.OPENED_IN_EMAIL)
            }
        }
    }

    companion object {
        const val EXTRA_REPORT_ID = "com.example.emicalculatorapp.extra.REPORT_ID"
    }
}
