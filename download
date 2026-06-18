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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.firstphone.app.ui.components.PrimaryButton
import com.firstphone.app.util.Constants
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VacationModeScreen(
    vm: VacationModeViewModel = hiltViewModel(),
    onDone: () -> Unit
) {
    val state by vm.state.collectAsState()
    LaunchedEffect(state.saved) { if (state.saved) onDone() }

    var showStart by remember { mutableStateOf(false) }
    var showEnd by remember { mutableStateOf(false) }

    val fmt = DateTimeFormatter.ofPattern("MMM d, yyyy")
    val startDate = LocalDate.ofEpochDay(state.startEpochDay)
    val endDate = LocalDate.ofEpochDay(state.endEpochDay)

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .semantics { testTag = "vacation-mode-screen" }
        ) {
            Text("Vacation Mode", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DateButton(label = "Start", text = startDate.format(fmt), onClick = { showStart = true }, testID = "vacation-start-button")
                DateButton(label = "End", text = endDate.format(fmt), onClick = { showEnd = true }, testID = "vacation-end-button")
            }

            Spacer(Modifier.height(20.dp))

            Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surface) {
                Column(Modifier.padding(16.dp).fillMaxWidth()) {
                    Text("Daily Vault limit during vacation", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Constants.VACATION_LIMIT_OPTIONS.forEach { v ->
                            val label = if (v == Int.MAX_VALUE) "Unlimited" else "$v min"
                            FilterChip(
                                selected = state.limitMin == v,
                                onClick = { vm.setLimit(v) },
                                label = { Text(label) },
                                modifier = Modifier.semantics { testTag = "vacation-limit-chip-$v" }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            PrimaryButton(text = "Save Vacation", onClick = vm::save, testID = "vacation-save-button")
            Spacer(Modifier.height(12.dp))
            if (state.active) {
                OutlinedButton(
                    onClick = vm::clear,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .semantics { testTag = "vacation-clear-button" },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Cancel Vacation") }
            }
        }
    }

    if (showStart) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showStart = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = datePickerState.selectedDateMillis
                    if (millis != null) {
                        vm.setStart(java.time.Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate())
                    }
                    showStart = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showStart = false }) { Text("Cancel") } }
        ) { DatePicker(state = datePickerState) }
    }
    if (showEnd) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = endDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showEnd = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = datePickerState.selectedDateMillis
                    if (millis != null) {
                        vm.setEnd(java.time.Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate())
                    }
                    showEnd = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showEnd = false }) { Text("Cancel") } }
        ) { DatePicker(state = datePickerState) }
    }
}

@Composable
private fun DateButton(label: String, text: String, onClick: () -> Unit, testID: String) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier.semantics { testTag = testID }
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text, style = MaterialTheme.typography.titleMedium)
        }
    }
}
