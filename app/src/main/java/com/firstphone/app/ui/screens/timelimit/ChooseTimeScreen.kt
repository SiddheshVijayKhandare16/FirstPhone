package com.firstphone.app.ui.screens.timelimit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import com.firstphone.app.ui.components.PrimaryButton
import com.firstphone.app.util.Constants

@Composable
fun ChooseTimeScreen(
    vm: TimeLimitViewModel = hiltViewModel(),
    onSaved: () -> Unit
) {
    val state by vm.state.collectAsState()
    LaunchedEffect(state.saved) { if (state.saved) onSaved() }

    Scaffold { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .semantics { testTag = "choose-time-screen" }
        ) {
            Text(stringResource(R.string.choose_daily_time), style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            Text(
                "This is the total Vault time per weekday.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(20.dp))

            Constants.DAILY_LIMIT_OPTIONS.forEach { min ->
                val selected = state.selectedMin == min
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .selectable(selected = selected, onClick = { vm.select(min) })
                        .semantics { testTag = "time-option-$min" }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = selected, onClick = { vm.select(min) })
                        Spacer(Modifier.height(0.dp))
                        Column(Modifier.weight(1f).padding(start = 12.dp)) {
                            Text("$min minutes", style = MaterialTheme.typography.titleMedium)
                            if (min == Constants.DEFAULT_DAILY_LIMIT_MIN) {
                                Text(
                                    stringResource(R.string.recommended),
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))
            PrimaryButton(
                text = stringResource(R.string.continue_btn),
                onClick = vm::save,
                testID = "choose-time-save-button"
            )
        }
    }
}
