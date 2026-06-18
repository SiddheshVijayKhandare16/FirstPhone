package com.firstphone.app.ui.screens.parent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
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
fun WeekendModeScreen(
    vm: WeekendModeViewModel = hiltViewModel(),
    onDone: () -> Unit
) {
    val state by vm.state.collectAsState()
    LaunchedEffect(state.saved) { if (state.saved) onDone() }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .semantics { testTag = "weekend-mode-screen" }
        ) {
            Text(stringResource(R.string.weekend_mode), style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(16.dp))

            DayPicker("Saturday", state.saturday, vm::setSaturday, "weekend-saturday")
            Spacer(Modifier.height(16.dp))
            DayPicker("Sunday", state.sunday, vm::setSunday, "weekend-sunday")

            Spacer(Modifier.weight(1f))
            PrimaryButton(
                text = "Save",
                onClick = vm::save,
                testID = "weekend-save-button"
            )
        }
    }
}

@Composable
private fun DayPicker(
    title: String,
    selected: Int,
    onSelect: (Int) -> Unit,
    testIDPrefix: String
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth().semantics { testTag = "$testIDPrefix-card" }
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Constants.WEEKEND_LIMIT_OPTIONS.forEach { v ->
                    val label = when (v) {
                        -1 -> "Same as weekday"
                        Int.MAX_VALUE -> "Unlimited"
                        else -> "$v min"
                    }
                    FilterChip(
                        selected = selected == v,
                        onClick = { onSelect(v) },
                        label = { Text(label) },
                        modifier = Modifier.semantics { testTag = "$testIDPrefix-chip-$v" }
                    )
                }
            }
        }
    }
}
