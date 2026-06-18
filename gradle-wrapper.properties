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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.firstphone.app.R
import com.firstphone.app.ui.components.PrimaryButton
import com.firstphone.app.ui.components.SecondaryButton
import com.firstphone.app.util.Constants

@Composable
fun OverrideScreen(
    vm: OverrideViewModel = hiltViewModel(),
    onDone: () -> Unit
) {
    val state by vm.state.collectAsState()
    LaunchedEffect(state.done) { if (state.done) onDone() }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .semantics { testTag = "override-screen" },
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(stringResource(R.string.quick_override), style = MaterialTheme.typography.headlineMedium)
            Text(
                "Adds extra Vault time only for today. Resets at midnight.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
            if (state.extraToday > 0) {
                Text(
                    if (state.extraToday == Int.MAX_VALUE) "Unlimited today" else "+${state.extraToday} minutes added today",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.semantics { testTag = "override-current-extra" }
                )
            }
            Spacer(Modifier.height(8.dp))

            Constants.OVERRIDE_OPTIONS.forEach { min ->
                val label = if (min == Int.MAX_VALUE) "Unlimited today" else "Add $min minutes today"
                if (min == Int.MAX_VALUE) {
                    SecondaryButton(text = label, onClick = { vm.add(min) }, testID = "override-add-$min")
                } else {
                    PrimaryButton(text = label, onClick = { vm.add(min) }, testID = "override-add-$min")
                }
            }
        }
    }
}
