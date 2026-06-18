package com.firstphone.app.ui.screens.parent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firstphone.app.data.repository.SettingsRepository
import com.firstphone.app.util.ScheduleHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class VacationModeUiState(
    val startEpochDay: Long = ScheduleHelper.todayEpochDay(),
    val endEpochDay: Long = ScheduleHelper.todayEpochDay() + 7,
    val limitMin: Int = 60,
    val active: Boolean = false,
    val saved: Boolean = false
)

@HiltViewModel
class VacationModeViewModel @Inject constructor(
    private val settings: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(VacationModeUiState())
    val state: StateFlow<VacationModeUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val s = settings.get()
            val active = s.vacationStartEpochDay > 0L && s.vacationEndEpochDay >= s.vacationStartEpochDay
            _state.update {
                it.copy(
                    startEpochDay = if (active) s.vacationStartEpochDay else ScheduleHelper.todayEpochDay(),
                    endEpochDay = if (active) s.vacationEndEpochDay else ScheduleHelper.todayEpochDay() + 7,
                    limitMin = if (active) s.vacationLimitMin else 60,
                    active = active
                )
            }
        }
    }

    fun setStart(date: LocalDate) = _state.update { it.copy(startEpochDay = date.toEpochDay()) }
    fun setEnd(date: LocalDate) = _state.update { it.copy(endEpochDay = date.toEpochDay()) }
    fun setLimit(min: Int) = _state.update { it.copy(limitMin = min) }

    fun save() {
        viewModelScope.launch {
            val s = _state.value
            settings.setVacation(s.startEpochDay, s.endEpochDay, s.limitMin)
            _state.update { it.copy(saved = true) }
        }
    }

    fun clear() {
        viewModelScope.launch {
            settings.clearVacation()
            _state.update { it.copy(saved = true) }
        }
    }
}
