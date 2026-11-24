package com.example.emicalculatorapp.viewmodel

import android.content.Context
import com.example.emicalculatorapp.data.database.ReportEntity
import com.example.emicalculatorapp.data.database.ReportRepository
import com.example.emicalculatorapp.data.report.ReportSender
import com.example.emicalculatorapp.model.report.DiagnosticInfo
import com.example.emicalculatorapp.model.report.LoanInputDiagnostics
import com.example.emicalculatorapp.model.report.ReportBodyBuilder
import com.example.emicalculatorapp.model.report.ReportCategory
import com.example.emicalculatorapp.model.report.ReportStatus
import com.example.emicalculatorapp.model.report.ReportValidation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReportsState(
    private val scope: CoroutineScope,
    private val repository: ReportRepository,
    private val reportSender: ReportSender
) {

    val reports: StateFlow<List<ReportEntity>> = repository.getAllReports()
        .stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _category = MutableStateFlow(ReportCategory.BUG)
    val category: StateFlow<ReportCategory> = _category.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _screenshotPath = MutableStateFlow<String?>(null)
    val screenshotPath: StateFlow<String?> = _screenshotPath.asStateFlow()

    private val _includeDiagnostics = MutableStateFlow(true)
    val includeDiagnostics: StateFlow<Boolean> = _includeDiagnostics.asStateFlow()

    private val _includeLoanInputs = MutableStateFlow(true)
    val includeLoanInputs: StateFlow<Boolean> = _includeLoanInputs.asStateFlow()

    private val _validationError = MutableStateFlow<String?>(null)
    val validationError: StateFlow<String?> = _validationError.asStateFlow()

    private val _showSuccessBanner = MutableStateFlow(false)
    val showSuccessBanner: StateFlow<Boolean> = _showSuccessBanner.asStateFlow()

    private val _clipboardNotice = MutableStateFlow<String?>(null)
    val clipboardNotice: StateFlow<String?> = _clipboardNotice.asStateFlow()

    private var editingDraftId: Long? = null

    fun setCategory(newCategory: ReportCategory) {
        _category.value = newCategory
    }

    fun setDescription(text: String) {
        _description.value = text
        if (_validationError.value != null) {
            _validationError.value = ReportValidation.getValidationMessage(text)
        }
    }

    fun setScreenshot(path: String?) {
        _screenshotPath.value = path
    }

    fun toggleDiagnostics(enabled: Boolean) {
        _includeDiagnostics.value = enabled
    }

    fun toggleLoanInputs(enabled: Boolean) {
        _includeLoanInputs.value = enabled
    }

    fun dismissSuccessBanner() {
        _showSuccessBanner.value = false
    }

    fun clearClipboardNotice() {
        _clipboardNotice.value = null
    }

    fun resumeDraft(report: ReportEntity) {
        editingDraftId = report.id
        val matchedCategory = ReportCategory.entries.find { it.displayName == report.category } ?: ReportCategory.BUG
        _category.value = matchedCategory
        _description.value = report.description
        _screenshotPath.value = report.screenshotPath
        _validationError.value = null
    }

    fun deleteReport(report: ReportEntity, onDeleted: () -> Unit = {}) {
        scope.launch {
            repository.deleteReport(report)
            onDeleted()
        }
    }

    fun restoreReport(report: ReportEntity) {
        scope.launch {
            repository.insertReport(report)
        }
    }

    fun sendReport(
        context: Context,
        diagnosticInfo: DiagnosticInfo,
        loanInputs: LoanInputDiagnostics
    ) {
        val currentDesc = _description.value
        val errorMsg = ReportValidation.getValidationMessage(currentDesc)
        if (errorMsg != null) {
            _validationError.value = errorMsg
            return
        }
        _validationError.value = null

        val currentCat = _category.value.displayName
        val fullBody = ReportBodyBuilder.buildBody(
            category = currentCat,
            description = currentDesc,
            diagnosticInfo = if (_includeDiagnostics.value) diagnosticInfo else null,
            loanInputs = if (_includeDiagnostics.value && _includeLoanInputs.value) loanInputs else null
        )

        scope.launch {
            val draftId = editingDraftId
            val entity = ReportEntity(
                id = draftId ?: 0L,
                category = currentCat,
                description = currentDesc.trim(),
                diagnosticsText = fullBody,
                screenshotPath = _screenshotPath.value,
                status = ReportStatus.DRAFT,
                createdAt = System.currentTimeMillis()
            )

            val savedId = repository.insertReport(entity)
            val activeReport = entity.copy(id = if (draftId != null && draftId > 0) draftId else savedId)

            reportSender.sendReport(
                context = context,
                report = activeReport,
                onOpenedInEmail = {
                    scope.launch {
                        repository.updateStatus(activeReport.id, ReportStatus.OPENED_IN_EMAIL)
                        _showSuccessBanner.value = true
                        resetForm()
                    }
                },
                onNoEmailApp = {
                    scope.launch {
                        _clipboardNotice.value = "No email app found. Report copied to clipboard! You can paste and email to support@emiverse.app"
                        resetForm()
                    }
                }
            )
        }
    }

    private fun resetForm() {
        editingDraftId = null
        _description.value = ""
        _screenshotPath.value = null
        _validationError.value = null
    }
}
