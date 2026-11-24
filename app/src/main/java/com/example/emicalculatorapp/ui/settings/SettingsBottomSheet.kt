package com.example.emicalculatorapp.ui.settings

import android.os.Build
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emicalculatorapp.data.settings.EffectsLevel
import com.example.emicalculatorapp.data.settings.NumberFormatStyle
import com.example.emicalculatorapp.data.settings.SettingsRepository
import com.example.emicalculatorapp.ui.components.GlassCard
import com.example.emicalculatorapp.ui.components.fx.GlowPillSwitch
import com.example.emicalculatorapp.ui.theme.ElectricViolet
import com.example.emicalculatorapp.ui.theme.InterFamily
import com.example.emicalculatorapp.ui.theme.LocalEffectsLevel
import com.example.emicalculatorapp.ui.theme.LocalHapticsEnabled
import com.example.emicalculatorapp.ui.theme.LocalNumberFormat
import com.example.emicalculatorapp.ui.theme.LuminousCyan
import com.example.emicalculatorapp.ui.theme.SpaceGroteskFamily
import com.example.emicalculatorapp.ui.theme.SurfaceColor
import com.example.emicalculatorapp.ui.theme.SurfaceContainerHigh
import com.example.emicalculatorapp.ui.theme.TextPrimary
import com.example.emicalculatorapp.ui.theme.TextSecondary
import kotlinx.coroutines.launch

/**
 * Cyber-luxe Settings bottom sheet.
 * Controls Effects Level, Haptic feedback, and Number Format.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBottomSheet(
    settingsRepository: SettingsRepository,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    val currentEffects = LocalEffectsLevel.current
    val currentNumberFormat = LocalNumberFormat.current
    val hapticsEnabled = LocalHapticsEnabled.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceColor.copy(alpha = 0.96f),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .size(width = 44.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(TextSecondary.copy(alpha = 0.35f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .padding(bottom = 36.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Preferences",
                    fontFamily = SpaceGroteskFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = TextPrimary
                )

                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            sheetState.hide()
                            onDismiss()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 1. Effects Level Control
            Text(
                text = "VISUAL EFFECTS LEVEL",
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                color = LuminousCyan
            )
            Text(
                text = currentEffects.description,
                fontFamily = InterFamily,
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
            )

            GlowPillSwitch(
                options = listOf(EffectsLevel.FULL, EffectsLevel.BALANCED, EffectsLevel.LITE),
                selectedOption = currentEffects,
                onOptionSelected = { level ->
                    coroutineScope.launch { settingsRepository.setEffectsLevel(level) }
                },
                labelProvider = { it.name.lowercase().replaceFirstChar { c -> c.uppercase() } }
            )

            Spacer(modifier = Modifier.height(22.dp))

            // 2. Number Formatting Control
            Text(
                text = "NUMBER & CURRENCY FORMAT",
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                color = LuminousCyan
            )
            Text(
                text = "Example: ${currentNumberFormat.example}",
                fontFamily = InterFamily,
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
            )

            GlowPillSwitch(
                options = listOf(NumberFormatStyle.INDIAN, NumberFormatStyle.INTERNATIONAL),
                selectedOption = currentNumberFormat,
                onOptionSelected = { format ->
                    coroutineScope.launch { settingsRepository.setNumberFormat(format) }
                },
                labelProvider = { if (it == NumberFormatStyle.INDIAN) "Indian (L/Cr)" else "International (M/B)" }
            )

            Spacer(modifier = Modifier.height(22.dp))

            // 3. Haptics Toggle
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Vibration,
                            contentDescription = null,
                            tint = LuminousCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Column(modifier = Modifier.padding(start = 12.dp)) {
                            Text(
                                text = "Haptic Feedback",
                                fontFamily = SpaceGroteskFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "Tactile ticks on slider step transitions",
                                fontFamily = InterFamily,
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Switch(
                        checked = hapticsEnabled,
                        onCheckedChange = { checked ->
                            coroutineScope.launch { settingsRepository.setHapticsEnabled(checked) }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = ElectricViolet,
                            uncheckedTrackColor = SurfaceContainerHigh
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 4. Hardware Awareness Diagnostics
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.04f))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Memory,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (settingsRepository.isLowRamDevice) "Low-RAM Device (Lite Default)" else "Standard RAM (Balanced Default)",
                            fontFamily = SpaceGroteskFamily,
                            fontSize = 11.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    Text(
                        text = "Android ${Build.VERSION.RELEASE}",
                        fontFamily = SpaceGroteskFamily,
                        fontSize = 11.sp,
                        color = TextSecondary.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
