package com.firstphone.app.ui.screens.parent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.firstphone.app.R
import com.firstphone.app.ui.components.PinInput
import com.firstphone.app.ui.screens.pin.CreatePinViewModel

@Composable
fun ChangePinScreen(
    vm: CreatePinViewModel = hiltViewModel(),
    onDone: () -> Unit
) {
    val state by vm.state.collectAsState()
    LaunchedEffect(state.done) { if (state.done) onDone() }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .semantics { testTag = "change-pin-screen" },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val title = if (state.step == 1) stringResource(R.string.change_pin) else stringResource(R.string.confirm_parent_pin)
            Text(title, style = MaterialTheme.typography.headlineLarge)
            Spacer(Modifier.height(24.dp))
            if (state.step == 1) {
                PinInput(state.firstPin, vm::onFirstPin, vm::onFirstPin, testID = "change-pin-first-input")
            } else {
                PinInput(state.confirmPin, vm::onConfirmPin, vm::onConfirmPin, testID = "change-pin-confirm-input")
            }
            if (state.error == "pin_mismatch") {
                Spacer(Modifier.height(24.dp))
                Text(stringResource(R.string.pin_mismatch), color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
