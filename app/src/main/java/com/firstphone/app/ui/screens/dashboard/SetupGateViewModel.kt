package com.firstphone.app.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firstphone.app.data.repository.SettingsRepository
import com.firstphone.app.ui.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Decides whether to start at the Welcome flow or the Dashboard.
 */
@HiltViewModel
class SetupGateViewModel @Inject constructor(
    private val settings: SettingsRepository
) : ViewModel() {

    private val _startDestination = MutableStateFlow(Routes.WELCOME)
    val startDestination: StateFlow<String> = _startDestination.asStateFlow()

    init {
        viewModelScope.launch {
            val s = settings.get()
            _startDestination.value = if (s.isSetupComplete && s.pinHash != null) Routes.DASHBOARD else Routes.WELCOME
        }
    }
}
