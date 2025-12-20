package com.matedroid.ui.screens.charges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matedroid.data.api.models.ChargeDetail
import com.matedroid.data.api.models.Units
import com.matedroid.data.repository.ApiResult
import com.matedroid.data.repository.TeslamateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChargeDetailUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val chargeDetail: ChargeDetail? = null,
    val units: Units? = null,
    val stats: ChargeDetailStats? = null
)

data class ChargeDetailStats(
    val powerMax: Int,
    val powerMin: Int,
    val powerAvg: Double,
    val voltageMax: Int,
    val voltageMin: Int,
    val voltageAvg: Double,
    val currentMax: Int,
    val currentMin: Int,
    val currentAvg: Double,
    val tempMax: Double,
    val tempMin: Double,
    val tempAvg: Double,
    val batteryStart: Int,
    val batteryEnd: Int,
    val batteryAdded: Int,
    val energyAdded: Double,
    val energyUsed: Double,
    val efficiency: Double,
    val durationMin: Int,
    val cost: Double?
)

@HiltViewModel
class ChargeDetailViewModel @Inject constructor(
    private val repository: TeslamateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChargeDetailUiState())
    val uiState: StateFlow<ChargeDetailUiState> = _uiState.asStateFlow()

    private var carId: Int? = null
    private var chargeId: Int? = null

    fun loadChargeDetail(carId: Int, chargeId: Int) {
        if (this.carId == carId && this.chargeId == chargeId && _uiState.value.chargeDetail != null) {
            return // Already loaded
        }

        this.carId = carId
        this.chargeId = chargeId

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Fetch charge detail and units in parallel
            val detailResult = repository.getChargeDetail(carId, chargeId)
            val statusResult = repository.getCarStatus(carId)

            val units = when (statusResult) {
                is ApiResult.Success -> statusResult.data.units
                is ApiResult.Error -> null
            }

            when (detailResult) {
                is ApiResult.Success -> {
                    val detail = detailResult.data
                    val stats = calculateStats(detail)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            chargeDetail = detail,
                            units = units,
                            stats = stats,
                            error = null
                        )
                    }
                }
                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = detailResult.message
                        )
                    }
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun calculateStats(detail: ChargeDetail): ChargeDetailStats {
        val points = detail.chargePoints ?: emptyList()

        // Power stats
        val powers = points.mapNotNull { it.chargerPower }
        val powerMax = powers.maxOrNull() ?: 0
        val powerMin = powers.filter { it > 0 }.minOrNull() ?: 0
        val powerAvg = if (powers.isNotEmpty()) powers.average() else 0.0

        // Voltage stats
        val voltages = points.mapNotNull { it.chargerVoltage }
        val voltageMax = voltages.maxOrNull() ?: 0
        val voltageMin = voltages.filter { it > 0 }.minOrNull() ?: 0
        val voltageAvg = if (voltages.isNotEmpty()) voltages.average() else 0.0

        // Current stats
        val currents = points.mapNotNull { it.chargerCurrent }
        val currentMax = currents.maxOrNull() ?: 0
        val currentMin = currents.filter { it > 0 }.minOrNull() ?: 0
        val currentAvg = if (currents.isNotEmpty()) currents.average() else 0.0

        // Temperature stats
        val temps = points.mapNotNull { it.outsideTemp }
        val tempMax = temps.maxOrNull() ?: detail.outsideTempAvg ?: 0.0
        val tempMin = temps.minOrNull() ?: detail.outsideTempAvg ?: 0.0
        val tempAvg = if (temps.isNotEmpty()) temps.average() else detail.outsideTempAvg ?: 0.0

        // Battery stats
        val batteryLevels = points.mapNotNull { it.batteryLevel }
        val batteryStart = batteryLevels.firstOrNull() ?: detail.startBatteryLevel ?: 0
        val batteryEnd = batteryLevels.lastOrNull() ?: detail.endBatteryLevel ?: 0
        val batteryAdded = batteryEnd - batteryStart

        // Energy stats
        val energyAdded = detail.chargeEnergyAdded ?: 0.0
        val energyUsed = detail.chargeEnergyUsed ?: energyAdded
        val efficiency = if (energyUsed > 0) (energyAdded / energyUsed) * 100 else 100.0

        return ChargeDetailStats(
            powerMax = powerMax,
            powerMin = powerMin,
            powerAvg = powerAvg,
            voltageMax = voltageMax,
            voltageMin = voltageMin,
            voltageAvg = voltageAvg,
            currentMax = currentMax,
            currentMin = currentMin,
            currentAvg = currentAvg,
            tempMax = tempMax,
            tempMin = tempMin,
            tempAvg = tempAvg,
            batteryStart = batteryStart,
            batteryEnd = batteryEnd,
            batteryAdded = batteryAdded,
            energyAdded = energyAdded,
            energyUsed = energyUsed,
            efficiency = efficiency,
            durationMin = detail.durationMin ?: 0,
            cost = detail.cost
        )
    }
}
