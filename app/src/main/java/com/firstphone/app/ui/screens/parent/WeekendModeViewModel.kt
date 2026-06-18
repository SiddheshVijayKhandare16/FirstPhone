package com.firstphone.app.ui.screens.parent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firstphone.app.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WeekendModeUiState(
    val saturday: Int = 60,
    val sunday: Int = 60,
    val saved: Boolean = false
)

@HiltViewModel
class WeekendModeViewModel @Inject constructor(
    private val settings: SettingsRepository
) : ViewModel() {
    private val _state = MutableStateFlow(WeekendModeUiState())
    val state: StateFlow<WeekendModeUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val s = settings.get()
            _state.update { it.copy(saturday = s.saturdayLimitMin, sunday = s.sundayLimitMin) }
        }
    }

    fun setSaturday(v: Int) = _state.update { it.copy(saturday = v) }
    fun setSunday(v: Int) = _state.update { it.copy(sunday = v) }

    fun save() {
        viewModelScope.launch {
            settings.setWeekendLimits(_state.value.saturday, _state.value.sunday)
            _state.update { it.copy(saved = true) }
        }
    }
}
