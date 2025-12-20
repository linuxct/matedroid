package com.matedroid.ui.screens.mileage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matedroid.data.api.models.DriveData
import com.matedroid.data.repository.ApiResult
import com.matedroid.data.repository.TeslamateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import javax.inject.Inject

data class MonthlyMileage(
    val yearMonth: YearMonth,
    val totalDistance: Double,
    val driveCount: Int,
    val totalEnergy: Double,
    val avgBatteryUsage: Double,
    val drives: List<DriveData>
)

data class DailyMileage(
    val date: LocalDate,
    val totalDistance: Double,
    val driveCount: Int,
    val totalEnergy: Double,
    val avgBatteryUsage: Double,
    val drives: List<DriveData>
)

data class MileageUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val allDrives: List<DriveData> = emptyList(),
    val selectedYear: Int = LocalDate.now().year,
    val availableYears: List<Int> = emptyList(),
    val monthlyData: List<MonthlyMileage> = emptyList(),
    val totalDistance: Double = 0.0,
    val avgMonthlyDistance: Double = 0.0,
    val totalDriveCount: Int = 0,
    // Detail view state
    val selectedMonth: YearMonth? = null,
    val dailyData: List<DailyMileage> = emptyList()
)

@HiltViewModel
class MileageViewModel @Inject constructor(
    private val repository: TeslamateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MileageUiState())
    val uiState: StateFlow<MileageUiState> = _uiState.asStateFlow()

    private var carId: Int? = null

    private val dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME

    fun setCarId(id: Int) {
        if (carId != id) {
            carId = id
            loadAllDrives()
        }
    }

    fun refresh() {
        carId?.let {
            _uiState.update { it.copy(isRefreshing = true) }
            loadAllDrives()
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun selectYear(year: Int) {
        _uiState.update { it.copy(selectedYear = year) }
        aggregateByMonth()
    }

    fun selectMonth(yearMonth: YearMonth) {
        _uiState.update { it.copy(selectedMonth = yearMonth) }
        aggregateByDay(yearMonth)
    }

    fun clearSelectedMonth() {
        _uiState.update { it.copy(selectedMonth = null, dailyData = emptyList()) }
    }

    private fun loadAllDrives() {
        val id = carId ?: return

        viewModelScope.launch {
            val state = _uiState.value
            if (!state.isRefreshing) {
                _uiState.update { it.copy(isLoading = true) }
            }

            when (val result = repository.getDrives(id)) {
                is ApiResult.Success -> {
                    val drives = result.data
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            allDrives = drives,
                            error = null
                        )
                    }
                    extractAvailableYears(drives)
                    aggregateByMonth()
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

    private fun extractAvailableYears(drives: List<DriveData>) {
        val years = drives.mapNotNull { drive ->
            parseDateTime(drive.startDate)?.year
        }.distinct().sortedDescending()

        val currentYear = _uiState.value.selectedYear
        val selectedYear = if (years.contains(currentYear)) currentYear else years.firstOrNull() ?: LocalDate.now().year

        _uiState.update {
            it.copy(
                availableYears = years,
                selectedYear = selectedYear
            )
        }
    }

    private fun aggregateByMonth() {
        val state = _uiState.value
        val drives = state.allDrives
        val year = state.selectedYear

        // Filter drives for selected year
        val yearDrives = drives.filter { drive ->
            parseDateTime(drive.startDate)?.year == year
        }

        // Group by month
        val grouped = yearDrives.groupBy { drive ->
            val dateTime = parseDateTime(drive.startDate)
            if (dateTime != null) {
                YearMonth.of(dateTime.year, dateTime.month)
            } else {
                null
            }
        }.filterKeys { it != null }

        // Create monthly aggregates
        val monthlyData = grouped.map { (yearMonth, monthDrives) ->
            val totalDistance = monthDrives.sumOf { it.distance ?: 0.0 }
            val totalEnergy = monthDrives.sumOf { it.energyConsumedNet ?: 0.0 }
            val batteryUsages = monthDrives.mapNotNull { drive ->
                val start = drive.startBatteryLevel
                val end = drive.endBatteryLevel
                if (start != null && end != null) (start - end).toDouble() else null
            }
            val avgBatteryUsage = if (batteryUsages.isNotEmpty()) batteryUsages.average() else 0.0

            MonthlyMileage(
                yearMonth = yearMonth!!,
                totalDistance = totalDistance,
                driveCount = monthDrives.size,
                totalEnergy = totalEnergy,
                avgBatteryUsage = avgBatteryUsage,
                drives = monthDrives
            )
        }.sortedByDescending { it.yearMonth }

        // Calculate totals for selected year
        val totalDistance = monthlyData.sumOf { it.totalDistance }
        val totalDriveCount = monthlyData.sumOf { it.driveCount }
        val avgMonthlyDistance = if (monthlyData.isNotEmpty()) totalDistance / monthlyData.size else 0.0

        _uiState.update {
            it.copy(
                monthlyData = monthlyData,
                totalDistance = totalDistance,
                avgMonthlyDistance = avgMonthlyDistance,
                totalDriveCount = totalDriveCount
            )
        }
    }

    private fun aggregateByDay(yearMonth: YearMonth) {
        val state = _uiState.value
        val drives = state.allDrives

        // Filter drives for selected month
        val monthDrives = drives.filter { drive ->
            val dateTime = parseDateTime(drive.startDate)
            dateTime != null && YearMonth.of(dateTime.year, dateTime.month) == yearMonth
        }

        // Group by day
        val grouped = monthDrives.groupBy { drive ->
            parseDateTime(drive.startDate)?.toLocalDate()
        }.filterKeys { it != null }

        // Create daily aggregates
        val dailyData = grouped.map { (date, dayDrives) ->
            val totalDistance = dayDrives.sumOf { it.distance ?: 0.0 }
            val totalEnergy = dayDrives.sumOf { it.energyConsumedNet ?: 0.0 }
            val batteryUsages = dayDrives.mapNotNull { drive ->
                val start = drive.startBatteryLevel
                val end = drive.endBatteryLevel
                if (start != null && end != null) (start - end).toDouble() else null
            }
            val avgBatteryUsage = if (batteryUsages.isNotEmpty()) batteryUsages.average() else 0.0

            DailyMileage(
                date = date!!,
                totalDistance = totalDistance,
                driveCount = dayDrives.size,
                totalEnergy = totalEnergy,
                avgBatteryUsage = avgBatteryUsage,
                drives = dayDrives.sortedByDescending { it.startDate }
            )
        }.sortedByDescending { it.date }

        _uiState.update {
            it.copy(dailyData = dailyData)
        }
    }

    private fun parseDateTime(dateStr: String?): LocalDateTime? {
        if (dateStr == null) return null
        return try {
            LocalDateTime.parse(dateStr, dateTimeFormatter)
        } catch (e: DateTimeParseException) {
            try {
                // Try alternative formats
                LocalDateTime.parse(dateStr.replace("Z", ""))
            } catch (e2: Exception) {
                null
            }
        }
    }

    // Get monthly data for chart (all 12 months, with 0 for missing months)
    fun getChartData(): List<Pair<Int, Double>> {
        val state = _uiState.value
        val year = state.selectedYear
        val monthlyMap = state.monthlyData.associate { it.yearMonth.monthValue to it.totalDistance }

        return (1..12).map { month ->
            month to (monthlyMap[month] ?: 0.0)
        }
    }

    // Get daily data for chart within selected month
    fun getDailyChartData(): List<Pair<Int, Double>> {
        val state = _uiState.value
        val selectedMonth = state.selectedMonth ?: return emptyList()
        val dailyMap = state.dailyData.associate { it.date.dayOfMonth to it.totalDistance }

        val daysInMonth = selectedMonth.lengthOfMonth()
        return (1..daysInMonth).map { day ->
            day to (dailyMap[day] ?: 0.0)
        }.filter { it.second > 0 } // Only return days with data for cleaner chart
    }
}
