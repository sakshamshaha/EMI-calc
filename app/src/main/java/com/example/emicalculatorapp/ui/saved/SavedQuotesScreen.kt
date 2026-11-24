package com.example.emicalculatorapp.ui.saved

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.emicalculatorapp.data.database.QuoteEntity
import com.example.emicalculatorapp.data.settings.NumberFormatStyle
import com.example.emicalculatorapp.ui.components.AmbientBackground
import com.example.emicalculatorapp.ui.components.GlassCard
import com.example.emicalculatorapp.ui.components.fx.staggeredEntrance
import com.example.emicalculatorapp.ui.theme.CanvasColor
import com.example.emicalculatorapp.ui.theme.ElectricViolet
import com.example.emicalculatorapp.ui.theme.InterFamily
import com.example.emicalculatorapp.ui.theme.LocalNumberFormat
import com.example.emicalculatorapp.ui.theme.LuminousCyan
import com.example.emicalculatorapp.ui.theme.MintGreen
import com.example.emicalculatorapp.ui.theme.SpaceGroteskFamily
import com.example.emicalculatorapp.ui.theme.SurfaceContainerHigh
import com.example.emicalculatorapp.ui.theme.SurfaceContainerLowest
import com.example.emicalculatorapp.ui.theme.TextPrimary
import com.example.emicalculatorapp.ui.theme.TextSecondary
import com.example.emicalculatorapp.viewmodel.SavedQuotesState
import kotlinx.coroutines.launch

/**
 * Saved Quotes screen displaying Room-persisted loan scenarios.
 * Supports swipe-to-delete with undo, tap-to-load into Calculate,
 * inline renaming, pin toggling, and two-quote selection for comparison.
 */
@Composable
fun SavedQuotesScreen(
    savedQuotesState: SavedQuotesState,
    onLoadQuoteIntoCalculate: (QuoteEntity) -> Unit,
    onCompareTwoQuotes: (QuoteEntity, QuoteEntity) -> Unit,
    onNavigateToCalculate: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        if (savedQuotesState.isSelectionMode) {
            savedQuotesState.clearSelection()
        } else {
            onNavigateToCalculate()
        }
    }

    val quotes by savedQuotesState.quotes.collectAsStateWithLifecycle()
    val isLoading by savedQuotesState.isLoading.collectAsStateWithLifecycle()
    val selectedIds by savedQuotesState.selectedQuoteIds.collectAsStateWithLifecycle()
    val numberFormat = LocalNumberFormat.current

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasColor)
    ) {
        AmbientBackground()

        Column(modifier = Modifier.fillMaxSize()) {
            // Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Saved Quotes",
                    fontFamily = SpaceGroteskFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = TextPrimary
                )
                Text(
                    text = "Tap to load into Calculate, long-press to compare 2 quotes",
                    fontFamily = InterFamily,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // Selection Mode Bar
            AnimatedVisibility(
                visible = selectedIds.isNotEmpty(),
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                SelectionActionBar(
                    selectedCount = selectedIds.size,
                    canCompare = selectedIds.size == 2,
                    onCompare = {
                        val first = quotes.find { it.id == selectedIds.getOrNull(0) }
                        val second = quotes.find { it.id == selectedIds.getOrNull(1) }
                        if (first != null && second != null) {
                            onCompareTwoQuotes(first, second)
                            savedQuotesState.clearSelection()
                        }
                    },
                    onCancel = { savedQuotesState.clearSelection() }
                )
            }

            // Main Content: Loading Shimmer, Empty State, or Quote List
            when {
                isLoading -> {
                    LoadingShimmerSkeleton()
                }
                quotes.isEmpty() -> {
                    SavedQuotesEmptyState(
                        onNavigateToCalculate = onNavigateToCalculate,
                        modifier = Modifier.weight(1f)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        itemsIndexed(quotes, key = { _, it -> it.id }) { index, quote ->
                            val isSelected = selectedIds.contains(quote.id)
                            val selectionIndex = when {
                                selectedIds.getOrNull(0) == quote.id -> 1
                                selectedIds.getOrNull(1) == quote.id -> 2
                                else -> 0
                            }

                            SavedQuoteCard(
                                quote = quote,
                                numberFormat = numberFormat,
                                isSelectionMode = selectedIds.isNotEmpty(),
                                isSelected = isSelected,
                                selectionIndex = selectionIndex,
                                modifier = Modifier.staggeredEntrance(index),
                                onQuoteClick = {
                                    if (selectedIds.isNotEmpty()) {
                                        savedQuotesState.toggleSelectQuote(quote.id)
                                    } else {
                                        onLoadQuoteIntoCalculate(quote)
                                        scope.launch {
                                            snackbarHostState.showSnackbar(
                                                message = "Loaded \"${quote.name}\" into Calculate",
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    }
                                },
                                onQuoteLongClick = {
                                    savedQuotesState.toggleSelectQuote(quote.id)
                                },
                                onTogglePin = {
                                    savedQuotesState.togglePin(quote)
                                },
                                onRename = { newName ->
                                    savedQuotesState.renameQuote(quote.id, newName)
                                },
                                onDelete = {
                                    savedQuotesState.deleteQuote(quote)
                                    scope.launch {
                                        val result = snackbarHostState.showSnackbar(
                                            message = "Deleted \"${quote.name}\"",
                                            actionLabel = "Undo",
                                            duration = SnackbarDuration.Short
                                        )
                                        if (result == SnackbarResult.ActionPerformed) {
                                            savedQuotesState.undoDelete()
                                        }
                                    }
                                }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(100.dp))
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 85.dp, start = 16.dp, end = 16.dp)
        )
    }
}

