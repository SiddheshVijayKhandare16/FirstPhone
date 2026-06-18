package com.firstphone.app.ui.screens.permissions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.firstphone.app.R
import com.firstphone.app.ui.components.PrimaryButton
import com.firstphone.app.ui.components.SectionCard

@Composable
fun PermissionsScreen(
    vm: PermissionsViewModel = hiltViewModel(),
    onAllGranted: () -> Unit
) {
    val state by vm.state.collectAsState()

    LaunchedEffect(Unit) { vm.startPolling() }
    LifecycleResumeEffect(Unit) {
        vm.refresh()
        onPauseOrDispose { }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .verticalScroll(rememberScrollState())
                .semantics { testTag = "permissions-screen" },
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(stringResource(R.string.permissions_title), style = MaterialTheme.typography.headlineLarge)
            Text(
                stringResource(R.string.permissions_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            PermissionRow(
                title = stringResource(R.string.perm_usage_title),
                desc = stringResource(R.string.perm_usage_desc),
                granted = state.usage,
                onEnable = vm::openUsageAccessSettings,
                testID = "perm-usage"
            )
            PermissionRow(
                title = stringResource(R.string.perm_accessibility_title),
                desc = stringResource(R.string.perm_accessibility_desc),
                granted = state.accessibility,
                onEnable = vm::openAccessibilitySettings,
                testID = "perm-accessibility"
            )
            PermissionRow(
                title = stringResource(R.string.perm_device_admin_title),
                desc = stringResource(R.string.perm_device_admin_desc),
                granted = state.deviceAdmin,
                onEnable = vm::requestDeviceAdmin,
                testID = "perm-device-admin"
            )

            Spacer(Modifier.height(8.dp))

            PrimaryButton(
                text = stringResource(R.string.continue_btn),
                onClick = onAllGranted,
                enabled = state.allGranted,
                testID = "permissions-continue-button"
            )
        }
    }
}

@Composable
private fun PermissionRow(
    title: String,
    desc: String,
    granted: Boolean,
    onEnable: () -> Unit,
    testID: String
) {
    SectionCard(modifier = Modifier.semantics { testTag = "$testID-card" }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = CircleShape,
                color = if (granted) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(36.dp)
            ) {
                if (granted) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }
            Spacer(Modifier.size(16.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(
                    desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (granted) {
                Text(stringResource(R.string.enabled), color = MaterialTheme.colorScheme.primary)
            } else {
                TextButton(
                    onClick = onEnable,
                    modifier = Modifier.semantics { testTag = "$testID-enable-button" }
                ) { Text(stringResource(R.string.enable)) }
            }
        }
    }
}
