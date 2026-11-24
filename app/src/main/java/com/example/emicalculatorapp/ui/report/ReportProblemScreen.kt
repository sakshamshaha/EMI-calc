package com.example.emicalculatorapp.ui.report

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.emicalculatorapp.model.report.DiagnosticInfo
import com.example.emicalculatorapp.model.report.LoanInputDiagnostics
import com.example.emicalculatorapp.model.report.ReportBodyBuilder
import com.example.emicalculatorapp.ui.components.AmbientBackground
import com.example.emicalculatorapp.ui.components.fx.ShineButton
import com.example.emicalculatorapp.ui.theme.CanvasColor
import com.example.emicalculatorapp.ui.theme.InterFamily
import com.example.emicalculatorapp.ui.theme.LocalEffectsLevel
import com.example.emicalculatorapp.ui.theme.LocalNumberFormat
import com.example.emicalculatorapp.ui.theme.SpaceGroteskFamily
import com.example.emicalculatorapp.ui.theme.SurfaceContainerHigh
import com.example.emicalculatorapp.ui.theme.TextPrimary
import com.example.emicalculatorapp.ui.theme.TextSecondary
import com.example.emicalculatorapp.viewmodel.EMIViewModel
import com.example.emicalculatorapp.viewmodel.ReportsState
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun ReportProblemScreen(
    onNavigateBack: () -> Unit,
    reportsState: ReportsState,
    viewModel: EMIViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    val category by reportsState.category.collectAsStateWithLifecycle()
    val description by reportsState.description.collectAsStateWithLifecycle()
    val screenshotPath by reportsState.screenshotPath.collectAsStateWithLifecycle()
    val includeDiagnostics by reportsState.includeDiagnostics.collectAsStateWithLifecycle()
    val includeLoanInputs by reportsState.includeLoanInputs.collectAsStateWithLifecycle()
    val validationError by reportsState.validationError.collectAsStateWithLifecycle()
    val showSuccessBanner by reportsState.showSuccessBanner.collectAsStateWithLifecycle()
    val clipboardNotice by reportsState.clipboardNotice.collectAsStateWithLifecycle()
    val reports by reportsState.reports.collectAsStateWithLifecycle()

    val effectsLevel = LocalEffectsLevel.current
    val numberFormat = LocalNumberFormat.current

    val loanAmount by viewModel.loanAmount.collectAsStateWithLifecycle()
    val interestRate by viewModel.interestRate.collectAsStateWithLifecycle()
    val tenureValue by viewModel.tenureValue.collectAsStateWithLifecycle()
    val isYearMode by viewModel.isYearMode.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()

    val diagnosticInfo = remember(effectsLevel, numberFormat) {
        DiagnosticInfo(
            appVersion = "1.0",
            androidVersion = Build.VERSION.RELEASE ?: "Unknown",
            apiLevel = Build.VERSION.SDK_INT,
            manufacturer = Build.MANUFACTURER ?: "Generic",
            model = Build.MODEL ?: "Android Device",
            currentScreen = "Report a Problem",
            effectsLevel = effectsLevel.name,
            numberFormat = numberFormat.name
        )
    }

    val loanInputs = remember(loanAmount, interestRate, tenureValue, isYearMode, selectedCategory) {
        LoanInputDiagnostics(
            loanAmount = loanAmount,
            interestRate = interestRate,
            tenureMonths = if (isYearMode) tenureValue * 12 else tenureValue,
            loanCategory = selectedCategory.title
        )
    }

    val previewText = remember(category, description, includeDiagnostics, includeLoanInputs, diagnosticInfo, loanInputs) {
        ReportBodyBuilder.buildBody(
            category = category.displayName,
            description = description.ifBlank { "(your description here)" },
            diagnosticInfo = if (includeDiagnostics) diagnosticInfo else null,
            loanInputs = if (includeDiagnostics && includeLoanInputs) loanInputs else null
        )
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            try {
                val dir = File(context.filesDir, "reports").apply { mkdirs() }
                val file = File(dir, "report_${System.currentTimeMillis()}.jpg")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    file.outputStream().use { output -> input.copyTo(output) }
                }
                reportsState.setScreenshot(file.absolutePath)
            } catch (_: Exception) {}
        }
    }

    LaunchedEffect(clipboardNotice) {
        clipboardNotice?.let { notice ->
            snackbarHostState.showSnackbar(notice, duration = SnackbarDuration.Long)
            reportsState.clearClipboardNotice()
        }
    }

    Scaffold(
        containerColor = CanvasColor,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            AmbientBackground(scrollOffsetProvider = { scrollState.value.toFloat() })

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(SurfaceContainerHigh.copy(alpha = 0.8f))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(modifier = Modifier.padding(start = 14.dp)) {
                        Text(
                            text = "Report a Problem",
                            fontFamily = SpaceGroteskFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "Submit feedback, bugs, or math issues",
                            fontFamily = InterFamily,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Category Chips
                ReportCategoryChips(
                    selectedCategory = category,
                    onCategorySelected = { reportsState.setCategory(it) }
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Description Field
                ReportDescriptionField(
                    description = description,
                    validationError = validationError,
                    onDescriptionChange = { reportsState.setDescription(it) }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Screenshot Section
                ReportScreenshotSection(
                    screenshotPath = screenshotPath,
                    onPickScreenshot = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    onRemoveScreenshot = { reportsState.setScreenshot(null) }
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Diagnostics Preview Card
                ReportDiagnosticsPreview(
                    includeDiagnostics = includeDiagnostics,
                    onToggleDiagnostics = { reportsState.toggleDiagnostics(it) },
                    includeLoanInputs = includeLoanInputs,
                    onToggleLoanInputs = { reportsState.toggleLoanInputs(it) },
                    previewText = previewText
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Send Report Button
                ShineButton(
                    text = "Send Report",
                    onClick = {
                        reportsState.sendReport(
                            context = context,
                            diagnosticInfo = diagnosticInfo,
                            loanInputs = loanInputs
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(26.dp))

                // Report History List
                ReportHistoryList(
                    reports = reports,
                    onResumeDraft = { reportsState.resumeDraft(it) },
                    onDeleteReport = { report ->
                        reportsState.deleteReport(report) {
                            coroutineScope.launch {
                                val result = snackbarHostState.showSnackbar(
                                    message = "Report deleted",
                                    actionLabel = "Undo",
                                    duration = SnackbarDuration.Short
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    reportsState.restoreReport(report)
                                }
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(30.dp))
            }

            if (showSuccessBanner) {
                ReportSuccessDialog(
                    onDismiss = { reportsState.dismissSuccessBanner() }
                )
            }
        }
    }
}
