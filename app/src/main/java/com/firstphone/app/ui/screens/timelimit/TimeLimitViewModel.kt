package com.firstphone.app.ui.screens.timelimit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firstphone.app.data.repository.SettingsRepository
import com.firstphone.app.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TimeLimitUiState(
    val selectedMin: Int = Constants.DEFAULT_DAILY_LIMIT_MIN,
    val saved: Boolean = false
)

@HiltViewModel
class TimeLimitViewModel @Inject constructor(
    private val settings: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TimeLimitUiState())
    val state: StateFlow<TimeLimitUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val s = settings.get()
            _state.update { it.copy(selectedMin = s.weekdayLimitMin) }
        }
    }

    fun select(min: Int) { _state.update { it.copy(selectedMin = min) } }

    fun save() {
        viewModelScope.launch {
            settings.setWeekdayLimit(_state.value.selectedMin)
            settings.markSetupComplete()
            _state.update { it.copy(saved = true) }
        }
    }
}
