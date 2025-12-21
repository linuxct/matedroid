package com.matedroid.ui.screens.updates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matedroid.data.api.models.UpdateData
import com.matedroid.data.repository.ApiResult
import com.matedroid.data.repository.TeslamateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class SoftwareVersionsUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val updates: List<SoftwareVersionItem> = emptyList(),
    val longestInstalledId: Int? = null,
    val error: String? = null,
    val filterMonths: Int? = null  // null = All time, 6 = Last 6 months, 12 = Last year
)

data class SoftwareVersionItem(
    val id: Int,
    val version: String,
    val installDate: LocalDateTime?,
    val endDate: LocalDateTime?,
    val durationDays: Long?,
    val isCurrent: Boolean
)

@HiltViewModel
class SoftwareVersionsViewModel @Inject constructor(
    private val repository: TeslamateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SoftwareVersionsUiState())
    val uiState: StateFlow<SoftwareVersionsUiState> = _uiState.asStateFlow()

    private var carId: Int? = null
    private var allUpdates: List<UpdateData> = emptyList()

    fun setCarId(id: Int) {
        if (carId != id) {
            carId = id
            loadUpdates()
        }
    }

    fun setFilter(months: Int?) {
        _uiState.update { it.copy(filterMonths = months) }
        applyFilter(months)
    }

    fun refresh() {
        carId?.let {
            _uiState.update { it.copy(isRefreshing = true) }
            loadUpdates()
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun loadUpdates() {
        val id = carId ?: return

        viewModelScope.launch {
            val state = _uiState.value
            if (!state.isRefreshing) {
                _uiState.update { it.copy(isLoading = true) }
            }

            when (val result = repository.getUpdates(id)) {
                is ApiResult.Success -> {
                    allUpdates = result.data
                    applyFilter(_uiState.value.filterMonths)
                }
                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    private fun applyFilter(months: Int?) {
        val cutoffDate = months?.let {
            LocalDate.now().minusMonths(it.toLong())
        }

        val filteredUpdates = if (cutoffDate != null) {
            allUpdates.filter { update ->
                val startDate = parseDate(update.startDate)
                startDate == null || startDate.toLocalDate() >= cutoffDate
            }
        } else {
            allUpdates
        }

        val items = filteredUpdates.mapIndexed { index, update ->
            val startDate = parseDate(update.startDate)
            val endDate = parseDate(update.endDate)
            val isCurrent = index == 0 && endDate == null

            val durationDays = when {
                startDate == null -> null
                endDate != null -> Duration.between(startDate, endDate).toDays()
                isCurrent -> Duration.between(startDate, LocalDateTime.now()).toDays()
                else -> null
            }

            SoftwareVersionItem(
                id = update.id ?: 0,
                version = update.version ?: "Unknown",
                installDate = startDate,
                endDate = endDate,
                durationDays = durationDays,
                isCurrent = isCurrent
            )
        }

        // Find the version that stayed installed the longest (among displayed ones)
        val longestId = items
            .filter { it.durationDays != null }
            .maxByOrNull { it.durationDays!! }
            ?.id

        _uiState.update {
            it.copy(
                isLoading = false,
                isRefreshing = false,
                updates = items,
                longestInstalledId = longestId,
                error = null
            )
        }
    }

    private fun parseDate(dateStr: String?): LocalDateTime? {
        if (dateStr == null) return null
        return try {
            LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME)
        } catch (e: Exception) {
            try {
                LocalDateTime.parse(dateStr.replace(" ", "T"), DateTimeFormatter.ISO_DATE_TIME)
            } catch (e2: Exception) {
                null
            }
        }
    }
}
