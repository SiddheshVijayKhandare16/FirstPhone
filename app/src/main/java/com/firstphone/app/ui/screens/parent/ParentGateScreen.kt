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
import androidx.compose.material3.TextButton
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
fun ParentGateScreen(
    vm: ParentGateViewModel = hiltViewModel(),
    onSuccess: () -> Unit,
    onCancel: () -> Unit
) {
    val state by vm.state.collectAsState()
    LaunchedEffect(state.success) { if (state.success) onSuccess() }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .semantics { testTag = "parent-gate-screen" },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(stringResource(R.string.enter_parent_pin), style = MaterialTheme.typography.headlineLarge)
            Spacer(Modifier.height(24.dp))
            PinInput(
                pin = state.pin,
                onPinChange = vm::onPin,
                onComplete = vm::onPin,
                testID = "parent-gate-pin-input"
            )
            Spacer(Modifier.height(24.dp))
            when (state.error) {
                "incorrect" -> Text(
                    stringResource(R.string.pin_incorrect),
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
                "locked" -> {
                    val mins = ((state.lockedUntilMs - System.currentTimeMillis()).coerceAtLeast(0L) / 60_000L) + 1
                    Text(
                        "Too many attempts. Try again in $mins minute(s).",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            TextButton(
                onClick = onCancel,
                modifier = Modifier.semantics { testTag = "parent-gate-cancel-button" }
            ) { Text("Cancel") }
        }
    }
}
