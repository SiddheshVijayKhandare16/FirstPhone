package com.firstphone.app.ui.screens.pin

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

data class CreatePinUiState(
    val firstPin: String = "",
    val confirmPin: String = "",
    val step: Int = 1, // 1 = enter, 2 = confirm
    val error: String? = null,
    val done: Boolean = false
)

@HiltViewModel
class CreatePinViewModel @Inject constructor(
    private val repo: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CreatePinUiState())
    val state: StateFlow<CreatePinUiState> = _state.asStateFlow()

    fun onFirstPin(p: String) {
        _state.update { it.copy(firstPin = p, error = null) }
        if (p.length == 4) _state.update { it.copy(step = 2) }
    }

    fun onConfirmPin(p: String) {
        _state.update { it.copy(confirmPin = p, error = null) }
        if (p.length == 4) submit()
    }

    private fun submit() {
        val s = _state.value
        if (s.firstPin != s.confirmPin) {
            _state.update { it.copy(error = "pin_mismatch", firstPin = "", confirmPin = "", step = 1) }
            return
        }
        viewModelScope.launch {
            repo.setPin(s.firstPin)
            _state.update { it.copy(done = true) }
        }
    }

    fun reset() {
        _state.value = CreatePinUiState()
    }
}
