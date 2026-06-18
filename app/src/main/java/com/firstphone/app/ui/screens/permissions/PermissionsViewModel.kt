package com.firstphone.app.ui.screens.permissions

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firstphone.app.service.FirstPhoneDeviceAdminReceiver
import com.firstphone.app.util.PermissionChecker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PermissionsUiState(
    val usage: Boolean = false,
    val accessibility: Boolean = false,
    val deviceAdmin: Boolean = false
) {
    val allGranted: Boolean get() = usage && accessibility && deviceAdmin
}

@HiltViewModel
class PermissionsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val checker: PermissionChecker
) : ViewModel() {

    private val _state = MutableStateFlow(PermissionsUiState())
    val state: StateFlow<PermissionsUiState> = _state.asStateFlow()

    init { refresh() }

    fun refresh() {
        _state.update {
            it.copy(
                usage = checker.hasUsageAccess(),
                accessibility = checker.hasAccessibilityService(),
                deviceAdmin = checker.hasDeviceAdmin()
            )
        }
    }

    fun startPolling() {
        viewModelScope.launch {
            while (!_state.value.allGranted) {
                refresh()
                delay(800L)
            }
        }
    }

    fun openUsageAccessSettings() {
        val i = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(i)
    }

    fun openAccessibilitySettings() {
        val i = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(i)
    }

    fun requestDeviceAdmin() {
        val component = ComponentName(context, FirstPhoneDeviceAdminReceiver::class.java)
        val i = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
            .putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, component)
            .putExtra(
                DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "First Phone uses Device Admin so children cannot uninstall the app without the Parent PIN."
            )
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(i)
    }
}
