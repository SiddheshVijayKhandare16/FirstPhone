package com.firstphone.app.ui.screens.dashboard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firstphone.app.data.local.entities.SettingsEntity
import com.firstphone.app.data.repository.AppRepository
import com.firstphone.app.data.repository.SettingsRepository
import com.firstphone.app.service.UsageMonitorService
import com.firstphone.app.util.ScheduleHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val isActive: Boolean = true,
    val limitMin: Int = 18,
    val usedMs: Long = 0L,
    val remainingMs: Long = 0L,
    val protectedAppCount: Int = 0,
    val weekdayLimit: Int = 18,
    val saturdayLimit: Int = 60,
    val sundayLimit: Int = 60,
    val overrideExtraMin: Int = 0
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settings: SettingsRepository,
    private val apps: AppRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardUiState())
    val state: StateFlow<DashboardUiState> = _state.asStateFlow()

    init {
        combine(settings.observe(), apps.observeVault()) { s, vault ->
            s to vault
        }.onEach { (s, vault) ->
            val settings = s ?: return@onEach
            recompute(settings, vault.size)
        }.launchIn(viewModelScope)

        // Ensure the foreground monitor is running
        viewModelScope.launch { UsageMonitorService.startIfNeeded(context) }
    }

    private fun recompute(s: SettingsEntity, vaultCount: Int) {
        val baseLimit = ScheduleHelper.limitToday(
            s.weekdayLimitMin, s.saturdayLimitMin, s.sundayLimitMin,
            s.vacationStartEpochDay, s.vacationEndEpochDay, s.vacationLimitMin
        )
        val today = ScheduleHelper.todayEpochDay()
        val override = if (s.overrideEpochDay == today) s.overrideExtraMin else 0
        val effectiveLimitMin = when {
            baseLimit == Int.MAX_VALUE || override == Int.MAX_VALUE -> Int.MAX_VALUE
            else -> baseLimit + override
        }
        val limitMs = if (effectiveLimitMin == Int.MAX_VALUE) Long.MAX_VALUE
            else effectiveLimitMin * 60_000L
        val remaining = (limitMs - s.usedTodayMs).coerceAtLeast(0L)

        _state.value = DashboardUiState(
            isActive = remaining > 0L && vaultCount > 0,
            limitMin = effectiveLimitMin,
            usedMs = s.usedTodayMs,
            remainingMs = remaining,
            protectedAppCount = vaultCount,
            weekdayLimit = s.weekdayLimitMin,
            saturdayLimit = s.saturdayLimitMin,
            sundayLimit = s.sundayLimitMin,
            overrideExtraMin = override
        )
    }
}
