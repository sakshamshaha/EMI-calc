package com.example.emicalculatorapp.ui.saved

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emicalculatorapp.data.settings.EffectsLevel
import com.example.emicalculatorapp.data.settings.NumberFormatStyle
import com.example.emicalculatorapp.model.LoanCategory
import com.example.emicalculatorapp.model.QuoteCalculations
import com.example.emicalculatorapp.ui.components.ConfettiBurst
import com.example.emicalculatorapp.ui.components.fx.PathCheckmark
import com.example.emicalculatorapp.ui.components.fx.ShineButton
import com.example.emicalculatorapp.ui.theme.CanvasColor
import com.example.emicalculatorapp.ui.theme.ElectricViolet
import com.example.emicalculatorapp.ui.theme.HotPink
import com.example.emicalculatorapp.ui.theme.InterFamily
import com.example.emicalculatorapp.ui.theme.LocalEffectsLevel
import com.example.emicalculatorapp.ui.theme.LocalReduceMotion
import com.example.emicalculatorapp.ui.theme.LuminousCyan
import com.example.emicalculatorapp.ui.theme.MintGreen
import com.example.emicalculatorapp.ui.theme.OutlineVariant
import com.example.emicalculatorapp.ui.theme.SpaceGroteskFamily
import com.example.emicalculatorapp.ui.theme.SurfaceContainerHigh
import com.example.emicalculatorapp.ui.theme.SurfaceContainerLowest
import com.example.emicalculatorapp.ui.theme.TextMuted
import com.example.emicalculatorapp.ui.theme.TextPrimary
import com.example.emicalculatorapp.ui.theme.TextSecondary
import com.example.emicalculatorapp.viewmodel.EMIViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveQuoteBottomSheet(
    category: LoanCategory,
    principal: Double,
    annualRate: Double,
    tenureMonths: Int,
    processingFeePercent: Double = 0.0,
    numberFormat: NumberFormatStyle,
    onSaveQuote: (name: String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    val effectsLevel = LocalEffectsLevel.current
    val reduceMotion = LocalReduceMotion.current

    val defaultGeneratedName = remember(category, principal, annualRate) {
        QuoteCalculations.generateDefaultQuoteName(category, principal, annualRate, numberFormat)
    }

    var quoteName by remember { mutableStateOf(defaultGeneratedName) }
    var isSavingSuccess by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    val isValid = quoteName.trim().isNotEmpty() && quoteName.trim().length <= 40

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CanvasColor,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = null,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 22.dp)
        ) {
            if (isSavingSuccess) {
                // Success Celebratory State
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 36.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    PathCheckmark(
                        size = 64.dp,
                        color = MintGreen,
                        strokeWidth = 5.dp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Quote Saved Successfully!",
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )

                    Text(
                        text = quoteName.trim(),
                        fontFamily = InterFamily,
                        fontSize = 13.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Confetti overlay
                if (!reduceMotion && effectsLevel != EffectsLevel.LITE) {
                    ConfettiBurst(
                        trigger = showConfetti,
                        onComplete = { showConfetti = false },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            } else {
                // Input Form State
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Title Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.BookmarkBorder,
                                contentDescription = null,
                                tint = LuminousCyan,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Save Loan Quote",
                                fontFamily = SpaceGroteskFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = TextPrimary
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(SurfaceContainerHigh)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Summary preview pill
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceContainerHigh.copy(alpha = 0.6f))
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "Amount", fontFamily = InterFamily, fontSize = 11.sp, color = TextMuted)
                                Text(
                                    text = EMIViewModel.formatCurrency(principal, numberFormat),
                                    fontFamily = SpaceGroteskFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = TextPrimary
                                )
                            }
                            Column {
                                Text(text = "Rate / Tenure", fontFamily = InterFamily, fontSize = 11.sp, color = TextMuted)
                                Text(
                                    text = "$annualRate% • ${if (tenureMonths % 12 == 0) "${tenureMonths / 12} Yrs" else "$tenureMonths Mo"}",
                                    fontFamily = SpaceGroteskFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = TextPrimary
                                )
                            }
                            Column {
                                Text(text = "Category", fontFamily = InterFamily, fontSize = 11.sp, color = TextMuted)
                                Text(
                                    text = "${category.title} Loan",
                                    fontFamily = SpaceGroteskFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = LuminousCyan
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Name Label & Counter
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "QUOTE NAME",
                            fontFamily = SpaceGroteskFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 0.8.sp,
                            color = TextSecondary
                        )

                        Text(
                            text = "${quoteName.length}/40",
                            fontFamily = SpaceGroteskFamily,
                            fontSize = 11.sp,
                            color = if (quoteName.length > 40) HotPink else TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Name Input Field
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceContainerLowest)
                            .border(1.dp, if (isValid) OutlineVariant else HotPink, RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        BasicTextField(
                            value = quoteName,
                            onValueChange = { if (it.length <= 40) quoteName = it },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontFamily = SpaceGroteskFamily,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary
                            ),
                            singleLine = true,
                            cursorBrush = SolidColor(LuminousCyan)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Save Button
                    ShineButton(
                        text = "Save Quote",
                        onClick = {
                            if (isValid) {
                                isSavingSuccess = true
                                showConfetti = true
                                onSaveQuote(quoteName.trim())

                                coroutineScope.launch {
                                    delay(900)
                                    onDismiss()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        gradientBrush = Brush.horizontalGradient(listOf(LuminousCyan, ElectricViolet))
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
