package com.example.emicalculatorapp.data.report

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.emicalculatorapp.config.SupportConfig
import com.example.emicalculatorapp.data.database.ReportEntity
import java.io.File

class EmailReportSender : ReportSender {

    override suspend fun sendReport(
        context: Context,
        report: ReportEntity,
        onOpenedInEmail: () -> Unit,
        onNoEmailApp: (reportText: String) -> Unit
    ) {
        val fullBody = report.diagnosticsText.ifBlank { report.description }

        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(SupportConfig.SUPPORT_EMAIL))
            putExtra(Intent.EXTRA_SUBJECT, "[EMIverse Report] ${report.category}")
            putExtra(Intent.EXTRA_TEXT, fullBody)

            report.screenshotPath?.let { path ->
                val file = File(path)
                if (file.exists()) {
                    val uri: Uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            }
        }

        val chooser = Intent.createChooser(emailIntent, "Send Problem Report via Email").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(chooser)
            onOpenedInEmail()
        } catch (e: ActivityNotFoundException) {
            copyToClipboard(context, fullBody)
            onNoEmailApp(fullBody)
        } catch (e: Exception) {
            copyToClipboard(context, fullBody)
            onNoEmailApp(fullBody)
        }
    }

    private fun copyToClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = ClipData.newPlainText("EMIverse Report", text)
        clipboard?.setPrimaryClip(clip)
    }
}
