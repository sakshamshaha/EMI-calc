package com.example.emicalculatorapp.ui.saved

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emicalculatorapp.data.database.QuoteEntity
import com.example.emicalculatorapp.data.settings.NumberFormatStyle
import com.example.emicalculatorapp.model.LoanCategory
import com.example.emicalculatorapp.model.QuoteCalculations
import com.example.emicalculatorapp.ui.components.GlassCard
import com.example.emicalculatorapp.ui.theme.ElectricViolet
import com.example.emicalculatorapp.ui.theme.HotPink
import com.example.emicalculatorapp.ui.theme.InterFamily
import com.example.emicalculatorapp.ui.theme.LuminousCyan
import com.example.emicalculatorapp.ui.theme.MintGreen
import com.example.emicalculatorapp.ui.theme.SpaceGroteskFamily
import com.example.emicalculatorapp.ui.theme.SurfaceContainerHigh
import com.example.emicalculatorapp.ui.theme.TextMuted
import com.example.emicalculatorapp.ui.theme.TextPrimary
import com.example.emicalculatorapp.ui.theme.TextSecondary
import com.example.emicalculatorapp.viewmodel.EMIViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SavedQuoteCard(
    quote: QuoteEntity,
    numberFormat: NumberFormatStyle,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    selectionIndex: Int, // 1 for A, 2 for B, 0 for unselected
    onQuoteClick: () -> Unit,
    onQuoteLongClick: () -> Unit,
    onTogglePin: () -> Unit,
    onRename: (newName: String) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val computed = remember(quote) {
        QuoteCalculations.computeQuoteSummary(quote)
    }

    var showMenu by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var renameInput by remember { mutableStateOf(quote.name) }

    val formattedDate = remember(quote.createdAt) {
        SimpleDateFormat("dd MMM yyyy", Locale.US).format(Date(quote.createdAt))
    }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart || value == SwipeToDismissBoxValue.StartToEnd) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            QuoteCardDismissBackground(dismissDirection = dismissState.dismissDirection)
        },
        enableDismissFromStartToEnd = !isSelectionMode,
        enableDismissFromEndToStart = !isSelectionMode,
        modifier = modifier.fillMaxWidth()
    ) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(
                    width = if (isSelected) 1.5.dp else if (quote.isPinned) 1.dp else 0.5.dp,
                    color = if (isSelected) LuminousCyan else if (quote.isPinned) MintGreen.copy(alpha = 0.5f) else Color.Transparent,
                    shape = RoundedCornerShape(20.dp)
                )
                .combinedClickable(
                    onClick = onQuoteClick,
                    onLongClick = onQuoteLongClick
                )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Top Row: Category icon, Name, Pin, Menu
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Selection indicator
                    if (isSelectionMode) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) LuminousCyan else SurfaceContainerHigh),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Text(
                                    text = if (selectionIndex == 1) "A" else "B",
                                    fontFamily = SpaceGroteskFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.Black
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Outlined.Circle,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                    }

                    // Category Pill
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(SurfaceContainerHigh),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getCategoryIcon(quote.category),
                            contentDescription = null,
                            tint = LuminousCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Title & Date
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = quote.name,
                            fontFamily = SpaceGroteskFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimary,
                            maxLines = 1
                        )
                        Text(
                            text = "${quote.category.title} Loan • $formattedDate",
                            fontFamily = InterFamily,
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    // Pin Icon Button
                    IconButton(
                        onClick = onTogglePin,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (quote.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                            contentDescription = if (quote.isPinned) "Unpin" else "Pin",
                            tint = if (quote.isPinned) MintGreen else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Card Menu (Rename / Delete)
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menu",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Rename", fontFamily = InterFamily) },
                                onClick = {
                                    showMenu = false
                                    renameInput = quote.name
                                    showRenameDialog = true
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(if (quote.isPinned) "Unpin" else "Pin", fontFamily = InterFamily) },
                                onClick = {
                                    showMenu = false
                                    onTogglePin()
                                },
                                leadingIcon = {
                                    Icon(Icons.Filled.PushPin, contentDescription = null, modifier = Modifier.size(18.dp))
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete", fontFamily = InterFamily, color = HotPink) },
                                onClick = {
                                    showMenu = false
                                    onDelete()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = HotPink, modifier = Modifier.size(18.dp))
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Metric Grid: Amount, Rate, Tenure, Computed EMI
                QuoteMetricsGrid(
                    quote = quote,
                    computed = computed,
                    numberFormat = numberFormat
                )
            }
        }
    }

    if (showRenameDialog) {
        QuoteRenameDialog(
            initialName = quote.name,
            onConfirm = { newName ->
                onRename(newName)
                showRenameDialog = false
            },
            onDismiss = { showRenameDialog = false }
        )
    }
}
