package com.example.emicalculatorapp.ui.calculator

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.emicalculatorapp.data.settings.NumberFormatStyle
import com.example.emicalculatorapp.model.EMICalculationResult
import com.example.emicalculatorapp.model.LoanCategory
import com.example.emicalculatorapp.ui.components.ConfettiBurst
import com.example.emicalculatorapp.ui.components.fx.ShineButton
import com.example.emicalculatorapp.ui.theme.ElectricViolet
import com.example.emicalculatorapp.ui.theme.LuminousCyan
import com.example.emicalculatorapp.ui.theme.MintGreen
import com.example.emicalculatorapp.ui.theme.SurfaceBright
import com.example.emicalculatorapp.ui.theme.SurfaceContainerHigh
import com.example.emicalculatorapp.viewmodel.EMIViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun ShareQuoteSection(
    selectedCategory: LoanCategory,
    loanAmount: Double,
    interestRate: Double,
    tenureValue: Int,
    isYearMode: Boolean,
    result: EMICalculationResult,
    numberFormat: NumberFormatStyle,
    onReset: () -> Unit,
    onSaveClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showConfetti by remember { mutableStateOf(false) }
    var isSharing by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Shine Reset Button
        ShineButton(
            text = "Reset",
            onClick = onReset,
            modifier = Modifier.width(76.dp),
            gradientBrush = Brush.horizontalGradient(listOf(SurfaceContainerHigh, SurfaceBright))
        )

        // Save Quote Button
        ShineButton(
            text = "Save",
            onClick = onSaveClick,
            modifier = Modifier.width(82.dp),
            gradientBrush = Brush.horizontalGradient(listOf(MintGreen.copy(alpha = 0.85f), LuminousCyan.copy(alpha = 0.85f)))
        )

        // Share Quote Button
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            ShineButton(
                text = "Share Quote",
                onClick = {
                    if (!isSharing) {
                        isSharing = true
                        showConfetti = true

                        coroutineScope.launch {
                            delay(700)

                            val tenureStr = if (isYearMode) "$tenureValue Years" else "$tenureValue Months"
                            val shareText = """
                                🚀 EMIverse Loan Quote Summary
                                ---------------------------------
                                • Category: ${selectedCategory.title} Loan
                                • Loan Amount: ${EMIViewModel.formatCurrency(loanAmount, numberFormat)}
                                • Interest Rate: ${String.format(Locale.US, "%.2f%%", interestRate)} p.a.
                                • Tenure: $tenureStr
                                
                                💎 Monthly EMI: ${EMIViewModel.formatCurrency(result.monthlyEMI, numberFormat)}
                                📊 Total Interest: ${EMIViewModel.formatCurrency(result.totalInterest, numberFormat)}
                                💰 Total Payable: ${EMIViewModel.formatCurrency(result.totalPayment, numberFormat)}
                                
                                Generated with EMIverse Quantum Engine
                            """.trimIndent()

                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Share EMIverse Quote")
                            context.startActivity(shareIntent)
                            isSharing = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                gradientBrush = Brush.horizontalGradient(listOf(ElectricViolet, LuminousCyan))
            )

            // Confetti Particle Burst Anchor
            ConfettiBurst(
                trigger = showConfetti,
                onComplete = { showConfetti = false }
            )
        }
    }
}
