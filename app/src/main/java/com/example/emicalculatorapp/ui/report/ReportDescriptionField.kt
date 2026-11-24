package com.example.emicalculatorapp.ui.report

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emicalculatorapp.model.report.ReportValidation
import com.example.emicalculatorapp.ui.theme.HotPink
import com.example.emicalculatorapp.ui.theme.InterFamily
import com.example.emicalculatorapp.ui.theme.LuminousCyan
import com.example.emicalculatorapp.ui.theme.SpaceGroteskFamily
import com.example.emicalculatorapp.ui.theme.SurfaceContainerHigh
import com.example.emicalculatorapp.ui.theme.SurfaceContainerLowest
import com.example.emicalculatorapp.ui.theme.TextMuted
import com.example.emicalculatorapp.ui.theme.TextPrimary
import com.example.emicalculatorapp.ui.theme.TextSecondary

@Composable
fun ReportDescriptionField(
    description: String,
    validationError: String?,
    onDescriptionChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "DESCRIPTION (10 - 1000 CHARACTERS)",
            fontFamily = SpaceGroteskFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = TextSecondary,
            letterSpacing = 0.9.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            modifier = Modifier.fillMaxWidth().height(130.dp),
            placeholder = {
                Text(
                    text = "Describe what happened, steps to reproduce, or formula discrepancy...",
                    fontFamily = InterFamily,
                    fontSize = 13.sp,
                    color = TextMuted
                )
            },
            textStyle = TextStyle(
                fontFamily = InterFamily,
                fontSize = 14.sp,
                color = TextPrimary
            ),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SurfaceContainerLowest,
                unfocusedContainerColor = SurfaceContainerLowest,
                focusedBorderColor = if (validationError != null) HotPink else LuminousCyan,
                unfocusedBorderColor = if (validationError != null) HotPink.copy(alpha = 0.6f) else SurfaceContainerHigh
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = validationError ?: "",
                fontFamily = InterFamily,
                fontSize = 11.sp,
                color = HotPink
            )
            Text(
                text = "${description.length} / ${ReportValidation.MAX_LENGTH}",
                fontFamily = SpaceGroteskFamily,
                fontSize = 11.sp,
                color = if (description.length > ReportValidation.MAX_LENGTH) HotPink else TextSecondary
            )
        }
    }
}
