package com.firstphone.app.ui.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.firstphone.app.R
import com.firstphone.app.ui.components.KeyValueRow
import com.firstphone.app.ui.components.SectionCard
import com.firstphone.app.util.ScheduleHelper

@OptIn(ExperimentalComposeUiApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    vm: DashboardViewModel = hiltViewModel(),
    onOpenParentSettings: () -> Unit
) {
    val state by vm.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                actions = {
                    IconButton(
                        onClick = onOpenParentSettings,
                        modifier = Modifier.semantics { testTag = "dashboard-parent-settings-button" }
                    ) {
                        Icon(Icons.Filled.Settings, contentDescription = "Parent settings")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .semantics { testTag = "dashboard-screen" },
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            VaultHeroCard(state = state)
            UsageCard(state)
            ProtectedCard(state)
            ScheduleCard(state)
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun VaultHeroCard(state: DashboardUiState) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(R.string.vault_status),
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(Modifier.height(4.dp))
            Text(
                if (state.isActive) stringResource(R.string.status_active) else stringResource(R.string.status_paused),
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.semantics { testTag = "dashboard-vault-status" }
            )
            Spacer(Modifier.height(24.dp))
            val remaining = if (state.limitMin == Int.MAX_VALUE) "Unlimited"
                else ScheduleHelper.formatTime(state.remainingMs)
            Text(
                text = remaining,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.displayLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.semantics { testTag = "dashboard-remaining-time" }
            )
            Text(
                stringResource(R.string.remaining_today),
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun UsageCard(state: DashboardUiState) {
    SectionCard {
        Column {
            Text("Daily Usage", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text(
                stringResource(R.string.used_today, ScheduleHelper.formatTime(state.usedMs)),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.semantics { testTag = "dashboard-used-today" }
            )
        }
    }
}

@Composable
private fun ProtectedCard(state: DashboardUiState) {
    SectionCard {
        Column {
            Text("Protected Apps", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text(
                stringResource(R.string.apps_protected, state.protectedAppCount),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.semantics { testTag = "dashboard-protected-count" }
            )
        }
    }
}

@Composable
private fun ScheduleCard(state: DashboardUiState) {
    SectionCard {
        Column {
            Text(stringResource(R.string.current_schedule), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            KeyValueRow("Weekdays", ScheduleHelper.formatMinutes(state.weekdayLimit))
            KeyValueRow("Saturday", ScheduleHelper.formatMinutes(if (state.saturdayLimit == -1) state.weekdayLimit else state.saturdayLimit))
            KeyValueRow("Sunday", ScheduleHelper.formatMinutes(if (state.sundayLimit == -1) state.weekdayLimit else state.sundayLimit))
            if (state.overrideExtraMin > 0) {
                KeyValueRow(
                    "Today's bonus",
                    if (state.overrideExtraMin == Int.MAX_VALUE) "Unlimited" else "+${state.overrideExtraMin} min"
                )
            }
        }
    }
}
