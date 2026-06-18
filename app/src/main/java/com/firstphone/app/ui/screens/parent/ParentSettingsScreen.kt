package com.firstphone.app.ui.screens.parent

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import com.firstphone.app.R
import com.firstphone.app.ui.components.SectionCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentSettingsScreen(
    onChangePin: () -> Unit,
    onEditAllowed: () -> Unit,
    onEditVault: () -> Unit,
    onChangeLimits: () -> Unit,
    onWeekendMode: () -> Unit,
    onVacationMode: () -> Unit,
    onOverride: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.parent_settings)) },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.semantics { testTag = "parent-settings-back-button" }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .semantics { testTag = "parent-settings-screen" },
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            SettingsRow(
                title = stringResource(R.string.change_pin),
                subtitle = "Update the parent PIN.",
                onClick = onChangePin,
                testID = "ps-change-pin"
            )
            SettingsRow(
                title = stringResource(R.string.edit_allowed_apps),
                subtitle = "Apps usable without limits.",
                onClick = onEditAllowed,
                testID = "ps-edit-allowed"
            )
            SettingsRow(
                title = stringResource(R.string.edit_vault_apps),
                subtitle = "Apps that share daily time.",
                onClick = onEditVault,
                testID = "ps-edit-vault"
            )
            SettingsRow(
                title = stringResource(R.string.change_daily_limits),
                subtitle = "Set weekday Vault minutes.",
                onClick = onChangeLimits,
                testID = "ps-change-limits"
            )
            SettingsRow(
                title = stringResource(R.string.weekend_mode),
                subtitle = "Saturday & Sunday rules.",
                onClick = onWeekendMode,
                testID = "ps-weekend-mode"
            )
            SettingsRow(
                title = stringResource(R.string.vacation_mode),
                subtitle = "Temporary limit for trips.",
                onClick = onVacationMode,
                testID = "ps-vacation-mode"
            )
            SettingsRow(
                title = stringResource(R.string.quick_override),
                subtitle = "Add extra time just for today.",
                onClick = onOverride,
                testID = "ps-override"
            )
        }
    }
}

@Composable
private fun SettingsRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testID: String
) {
    SectionCard(modifier = Modifier
        .clickable { onClick() }
        .semantics { testTag = testID }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
