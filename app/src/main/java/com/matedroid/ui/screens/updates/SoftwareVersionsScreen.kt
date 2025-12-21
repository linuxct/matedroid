package com.matedroid.ui.screens.updates

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.matedroid.ui.theme.CarColorPalette
import com.matedroid.ui.theme.CarColorPalettes
import java.time.format.DateTimeFormatter

enum class UpdatesDateFilter(val label: String, val months: Int?) {
    LAST_6_MONTHS("Last 6 months", 6),
    LAST_YEAR("Last year", 12),
    ALL_TIME("All time", null)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoftwareVersionsScreen(
    carId: Int,
    exteriorColor: String? = null,
    onNavigateBack: () -> Unit,
    viewModel: SoftwareVersionsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedFilter by remember { mutableStateOf(UpdatesDateFilter.ALL_TIME) }
    val isDarkTheme = isSystemInDarkTheme()
    val palette = CarColorPalettes.forExteriorColor(exteriorColor, isDarkTheme)

    LaunchedEffect(carId) {
        viewModel.setCarId(carId)
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    fun applyDateFilter(filter: UpdatesDateFilter) {
        selectedFilter = filter
        viewModel.setFilter(filter.months)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Software Versions") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.isLoading && !uiState.isRefreshing) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                SoftwareVersionsContent(
                    updates = uiState.updates,
                    longestInstalledId = uiState.longestInstalledId,
                    selectedFilter = selectedFilter,
                    palette = palette,
                    onFilterSelected = { applyDateFilter(it) }
                )
            }
        }
    }
}

@Composable
private fun SoftwareVersionsContent(
    updates: List<SoftwareVersionItem>,
    longestInstalledId: Int?,
    selectedFilter: UpdatesDateFilter,
    palette: CarColorPalette,
    onFilterSelected: (UpdatesDateFilter) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            DateFilterChips(
                selectedFilter = selectedFilter,
                palette = palette,
                onFilterSelected = onFilterSelected
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Update History",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (updates.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No software updates found",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(updates, key = { it.id }) { update ->
                SoftwareVersionCard(
                    update = update,
                    isLongestInstalled = update.id == longestInstalledId,
                    palette = palette
                )
            }
        }
    }
}

@Composable
private fun DateFilterChips(
    selectedFilter: UpdatesDateFilter,
    palette: CarColorPalette,
    onFilterSelected: (UpdatesDateFilter) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(UpdatesDateFilter.entries.toList()) { filter ->
            FilterChip(
                selected = filter == selectedFilter,
                onClick = { onFilterSelected(filter) },
                label = { Text(filter.label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = palette.surface,
                    selectedLabelColor = palette.onSurface
                )
            )
        }
    }
}

@Composable
private fun SoftwareVersionCard(
    update: SoftwareVersionItem,
    isLongestInstalled: Boolean,
    palette: CarColorPalette
) {
    val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (update.isCurrent) {
                palette.surface
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.SystemUpdate,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = if (update.isCurrent) palette.accent else MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = update.version,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (update.isCurrent) palette.onSurface else MaterialTheme.colorScheme.onSurface
                        )
                        if (update.isCurrent) {
                            Text(
                                text = "Current version",
                                style = MaterialTheme.typography.labelSmall,
                                color = palette.accent
                            )
                        }
                    }
                }

                if (isLongestInstalled) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Longest installed",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Longest",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Install date
                Column {
                    Text(
                        text = "Installed",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (update.isCurrent) palette.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = update.installDate?.format(dateFormatter) ?: "Unknown",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = if (update.isCurrent) palette.onSurface else MaterialTheme.colorScheme.onSurface
                    )
                }

                // Duration
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = if (update.isCurrent) palette.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Duration",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (update.isCurrent) palette.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = formatDuration(update.durationDays),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = if (update.isCurrent) palette.onSurface else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

private fun formatDuration(days: Long?): String {
    if (days == null) return "Unknown"

    return when {
        days == 0L -> "< 1 day"
        days == 1L -> "1 day"
        days < 30 -> "$days days"
        days < 60 -> {
            val weeks = days / 7
            if (weeks == 1L) "1 week" else "$weeks weeks"
        }
        days < 365 -> {
            val months = days / 30
            if (months == 1L) "~1 month" else "~$months months"
        }
        else -> {
            val years = days / 365
            val remainingMonths = (days % 365) / 30
            if (remainingMonths > 0) {
                if (years == 1L) "~1 year $remainingMonths mo" else "~$years years $remainingMonths mo"
            } else {
                if (years == 1L) "~1 year" else "~$years years"
            }
        }
    }
}
