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

data class ParentGateUiState(
    val pin: String = "",
    val error: String? = null,
    val lockedUntilMs: Long = 0L,
    val success: Boolean = false
)

@HiltViewModel
class ParentGateViewModel @Inject constructor(
    private val repo: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ParentGateUiState())
    val state: StateFlow<ParentGateUiState> = _state.asStateFlow()

    fun onPin(p: String) {
        _state.update { it.copy(pin = p, error = null) }
        if (p.length == 4) verify(p)
    }

    private fun verify(p: String) {
        viewModelScope.launch {
            val (ok, lock) = repo.verifyPin(p)
            if (ok) {
                _state.update { it.copy(success = true, error = null, lockedUntilMs = 0L) }
            } else {
                _state.update {
                    it.copy(
                        pin = "",
                        error = if (lock > 0L) "locked" else "incorrect",
                        lockedUntilMs = lock
                    )
                }
            }
        }
    }
}
