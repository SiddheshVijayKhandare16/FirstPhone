package com.firstphone.app.ui.screens.appselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firstphone.app.data.local.entities.ManagedAppEntity
import com.firstphone.app.data.repository.AppRepository
import com.firstphone.app.util.InstalledAppsProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class AppRow(
    val packageName: String,
    val label: String,
    val isAlwaysAllowed: Boolean,
    val isVault: Boolean
)

data class AppSelectionUiState(
    val isLoading: Boolean = true,
    val rows: List<AppRow> = emptyList(),
    val search: String = ""
)

@HiltViewModel
class AppSelectionViewModel @Inject constructor(
    private val repo: AppRepository,
    private val installed: InstalledAppsProvider
) : ViewModel() {

    private val _state = MutableStateFlow(AppSelectionUiState())
    val state: StateFlow<AppSelectionUiState> = _state.asStateFlow()

    init { loadApps() }

    private fun loadApps() {
        viewModelScope.launch {
            val launchable = withContext(Dispatchers.IO) { installed.launchableApps() }
            val existing = withContext(Dispatchers.IO) { repo.getVault() } // any selected previously
            val existingByPkg = existing.associateBy { it.packageName }
            val defaults = installed.defaultAlwaysAllowed
            val merged = launchable.map { info ->
                val prev = existingByPkg[info.packageName]
                ManagedAppEntity(
                    packageName = info.packageName,
                    label = info.label,
                    isAlwaysAllowed = prev?.isAlwaysAllowed ?: (info.packageName in defaults),
                    isVault = prev?.isVault ?: false
                )
            }
            // Persist freshly discovered apps so user choices survive
            withContext(Dispatchers.IO) { repo.saveDiscoveredApps(merged) }

            _state.update {
                it.copy(
                    isLoading = false,
                    rows = merged.map { e ->
                        AppRow(e.packageName, e.label, e.isAlwaysAllowed, e.isVault)
                    }
                )
            }
        }
    }

    fun setSearch(q: String) { _state.update { it.copy(search = q) } }

    fun toggleAllowed(pkg: String) {
        val rows = _state.value.rows.toMutableList()
        val i = rows.indexOfFirst { it.packageName == pkg }
        if (i < 0) return
        val row = rows[i]
        val newAllowed = !row.isAlwaysAllowed
        // Allowed and Vault are mutually exclusive
        rows[i] = row.copy(isAlwaysAllowed = newAllowed, isVault = if (newAllowed) false else row.isVault)
        _state.update { it.copy(rows = rows) }
        viewModelScope.launch {
            repo.setAllowed(pkg, newAllowed)
            if (newAllowed) repo.setVault(pkg, false)
        }
    }

    fun toggleVault(pkg: String) {
        val rows = _state.value.rows.toMutableList()
        val i = rows.indexOfFirst { it.packageName == pkg }
        if (i < 0) return
        val row = rows[i]
        val newVault = !row.isVault
        rows[i] = row.copy(isVault = newVault, isAlwaysAllowed = if (newVault) false else row.isAlwaysAllowed)
        _state.update { it.copy(rows = rows) }
        viewModelScope.launch {
            repo.setVault(pkg, newVault)
            if (newVault) repo.setAllowed(pkg, false)
        }
    }

    fun filtered(): List<AppRow> {
        val q = _state.value.search.trim().lowercase()
        return if (q.isEmpty()) _state.value.rows else _state.value.rows.filter {
            it.label.lowercase().contains(q) || it.packageName.lowercase().contains(q)
        }
    }
}
