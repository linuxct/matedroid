package com.matedroid.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matedroid.data.local.SettingsDataStore
import com.matedroid.data.repository.ApiResult
import com.matedroid.data.repository.TeslamateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val serverUrl: String = "",
    val apiToken: String = "",
    val acceptInvalidCerts: Boolean = false,
    val isLoading: Boolean = true,
    val isTesting: Boolean = false,
    val isSaving: Boolean = false,
    val testResult: TestResult? = null,
    val error: String? = null
)

sealed class TestResult {
    data object Success : TestResult()
    data class Failure(val message: String) : TestResult()
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore,
    private val repository: TeslamateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            val settings = settingsDataStore.settings.first()
            _uiState.value = _uiState.value.copy(
                serverUrl = settings.serverUrl,
                apiToken = settings.apiToken,
                acceptInvalidCerts = settings.acceptInvalidCerts,
                isLoading = false
            )
        }
    }

    fun updateServerUrl(url: String) {
        _uiState.value = _uiState.value.copy(
            serverUrl = url,
            testResult = null,
            error = null
        )
    }

    fun updateApiToken(token: String) {
        _uiState.value = _uiState.value.copy(
            apiToken = token,
            testResult = null,
            error = null
        )
    }

    fun updateAcceptInvalidCerts(accept: Boolean) {
        _uiState.value = _uiState.value.copy(
            acceptInvalidCerts = accept,
            testResult = null,
            error = null
        )
    }

    fun testConnection() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isTesting = true, testResult = null, error = null)

            val url = _uiState.value.serverUrl.trimEnd('/')
            if (url.isBlank()) {
                _uiState.value = _uiState.value.copy(
                    isTesting = false,
                    testResult = TestResult.Failure("Server URL is required")
                )
                return@launch
            }

            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                _uiState.value = _uiState.value.copy(
                    isTesting = false,
                    testResult = TestResult.Failure("URL must start with http:// or https://")
                )
                return@launch
            }

            when (val result = repository.testConnection(url, _uiState.value.acceptInvalidCerts)) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isTesting = false,
                        testResult = TestResult.Success
                    )
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isTesting = false,
                        testResult = TestResult.Failure(result.message)
                    )
                }
            }
        }
    }

    fun saveSettings(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)

            try {
                val url = _uiState.value.serverUrl.trimEnd('/')
                if (url.isBlank()) {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        error = "Server URL is required"
                    )
                    return@launch
                }

                settingsDataStore.saveSettings(
                    serverUrl = url,
                    apiToken = _uiState.value.apiToken,
                    acceptInvalidCerts = _uiState.value.acceptInvalidCerts
                )

                _uiState.value = _uiState.value.copy(isSaving = false)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = e.message ?: "Failed to save settings"
                )
            }
        }
    }

    fun clearTestResult() {
        _uiState.value = _uiState.value.copy(testResult = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
