package com.example.emicalculatorapp.model.report

data class DiagnosticInfo(
    val appVersion: String,
    val androidVersion: String,
    val apiLevel: Int,
    val manufacturer: String,
    val model: String,
    val currentScreen: String,
    val effectsLevel: String,
    val numberFormat: String
)

data class LoanInputDiagnostics(
    val loanAmount: Double,
    val interestRate: Double,
    val tenureMonths: Int,
    val loanCategory: String
)

object ReportBodyBuilder {

    fun buildBody(
        category: String,
        description: String,
        diagnosticInfo: DiagnosticInfo? = null,
        loanInputs: LoanInputDiagnostics? = null
    ): String {
        val sb = StringBuilder()
        sb.append("Category: ").append(category.trim()).append("\n\n")
        sb.append("Description:\n")
        sb.append(description.trim())

        if (diagnosticInfo != null) {
            sb.append("\n\n--- Diagnostic Info ---")
            sb.append("\nApp Version: ").append(diagnosticInfo.appVersion)
            sb.append("\nAndroid Version: ").append(diagnosticInfo.androidVersion).append(" (API ").append(diagnosticInfo.apiLevel).append(")")
            sb.append("\nDevice: ").append(diagnosticInfo.manufacturer).append(" ").append(diagnosticInfo.model)
            sb.append("\nScreen: ").append(diagnosticInfo.currentScreen)
            sb.append("\nEffects Level: ").append(diagnosticInfo.effectsLevel)
            sb.append("\nNumber Format: ").append(diagnosticInfo.numberFormat)
        }

        if (loanInputs != null) {
            sb.append("\n\n--- Current Loan Inputs ---")
            sb.append("\nCategory: ").append(loanInputs.loanCategory)
            sb.append("\nAmount: ").append(loanInputs.loanAmount)
            sb.append("\nInterest Rate: ").append(loanInputs.interestRate).append("%")
            sb.append("\nTenure: ").append(loanInputs.tenureMonths).append(" months")
        }

        return sb.toString()
    }
}
