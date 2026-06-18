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

data class OverrideUiState(
    val extraToday: Int = 0,
    val done: Boolean = false
)

@HiltViewModel
class OverrideViewModel @Inject constructor(
    private val settings: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OverrideUiState())
    val state: StateFlow<OverrideUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val s = settings.get()
            val today = com.firstphone.app.util.ScheduleHelper.todayEpochDay()
            val v = if (s.overrideEpochDay == today) s.overrideExtraMin else 0
            _state.update { it.copy(extraToday = v) }
        }
    }

    fun add(min: Int) {
        viewModelScope.launch {
            settings.addOverride(min)
            val s = settings.get()
            _state.update { it.copy(extraToday = s.overrideExtraMin, done = true) }
        }
    }
}
