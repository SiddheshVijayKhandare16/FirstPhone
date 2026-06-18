package com.firstphone.app.ui.screens.pin

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.firstphone.app.R
import com.firstphone.app.ui.components.PinInput

@Composable
fun CreatePinScreen(
    vm: CreatePinViewModel = hiltViewModel(),
    onPinCreated: () -> Unit
) {
    val state by vm.state.collectAsState()

    LaunchedEffect(state.done) {
        if (state.done) onPinCreated()
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .semantics { testTag = "create-pin-screen" },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val title = if (state.step == 1) stringResource(R.string.create_parent_pin) else stringResource(R.string.confirm_parent_pin)
            Text(title, style = MaterialTheme.typography.headlineLarge, textAlign = TextAlign.Center)
            Spacer(Modifier.height(12.dp))
            Text(
                "Use a 4-digit PIN only you know.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(40.dp))

            if (state.step == 1) {
                PinInput(
                    pin = state.firstPin,
                    onPinChange = vm::onFirstPin,
                    onComplete = vm::onFirstPin,
                    testID = "create-pin-first-input"
                )
            } else {
                PinInput(
                    pin = state.confirmPin,
                    onPinChange = vm::onConfirmPin,
                    onComplete = vm::onConfirmPin,
                    testID = "create-pin-confirm-input"
                )
            }

            if (state.error == "pin_mismatch") {
                Spacer(Modifier.height(24.dp))
                Text(
                    stringResource(R.string.pin_mismatch),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
