package com.firstphone.app.ui.screens.appselection

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.firstphone.app.R
import com.firstphone.app.ui.components.PrimaryButton

@Composable
fun AlwaysAllowedScreen(
    vm: AppSelectionViewModel = hiltViewModel(),
    onNext: () -> Unit
) {
    AppPickerScreen(
        title = stringResource(R.string.always_allowed_title),
        subtitle = stringResource(R.string.always_allowed_subtitle),
        vm = vm,
        showAllowed = true,
        onNext = onNext,
        nextTestId = "always-allowed-continue-button",
        screenTestId = "always-allowed-screen"
    )
}

@Composable
fun VaultAppsScreen(
    vm: AppSelectionViewModel = hiltViewModel(),
    onNext: () -> Unit
) {
    AppPickerScreen(
        title = stringResource(R.string.vault_apps_title),
        subtitle = stringResource(R.string.vault_apps_subtitle),
        vm = vm,
        showAllowed = false,
        onNext = onNext,
        nextTestId = "vault-apps-continue-button",
        screenTestId = "vault-apps-screen"
    )
}

@Composable
private fun AppPickerScreen(
    title: String,
    subtitle: String,
    vm: AppSelectionViewModel,
    showAllowed: Boolean,
    onNext: () -> Unit,
    nextTestId: String,
    screenTestId: String
) {
    val state by vm.state.collectAsState()
    val rows = vm.filtered()

    Scaffold(
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .semantics { testTag = screenTestId }
        ) {
            Column(Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                Text(title, style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(6.dp))
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = state.search,
                    onValueChange = vm::setSearch,
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    placeholder = { Text("Search apps") },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { testTag = "app-search-input" }
                )
            }

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(rows, key = { it.packageName }) { row ->
                        AppRowItem(
                            row = row,
                            checked = if (showAllowed) row.isAlwaysAllowed else row.isVault,
                            onToggle = {
                                if (showAllowed) vm.toggleAllowed(row.packageName)
                                else vm.toggleVault(row.packageName)
                            }
                        )
                    }
                }
            }

            Box(Modifier.padding(20.dp)) {
                PrimaryButton(
                    text = stringResource(R.string.continue_btn),
                    onClick = onNext,
                    enabled = !state.isLoading,
                    testID = nextTestId
                )
            }
        }
    }
}

@Composable
private fun AppRowItem(
    row: AppRow,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .semantics { testTag = "app-row-${row.packageName}" }
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    row.label.take(1).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(Modifier.size(14.dp))
            Column(Modifier.weight(1f)) {
                Text(row.label, style = MaterialTheme.typography.titleMedium)
                Text(
                    row.packageName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            Checkbox(
                checked = checked,
                onCheckedChange = { onToggle() },
                modifier = Modifier.semantics { testTag = "app-row-${row.packageName}-checkbox" }
            )
        }
    }
}
