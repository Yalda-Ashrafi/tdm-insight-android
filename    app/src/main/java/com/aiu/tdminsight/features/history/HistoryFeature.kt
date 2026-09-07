package com.aiu.tdminsight.features.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * HistoryFeature.kt
 * Owner: Benat
 *
 * Screen 9 — History (optional).
 *
 * Displays a list of previously saved patient cases so the user can
 * revisit past results without re-entering data. This screen is
 * display/navigation only — it does not persist data itself. Persistence
 * (Room, DataStore, or similar) is expected to be wired in by whoever
 * owns the data layer; this file defines the UI contract it needs.
 *
 * NOTE FOR TEAM: HistoryEntry below is a lightweight summary intended to
 * mirror the relevant fields of Yalda's SavedCase model. Once
 * SavedCase.kt is finalized, this can either be adapted to use it
 * directly, or mapped from it — whichever keeps ownership boundaries
 * cleanest.
 */

/**
 * A summarized entry shown in the history list.
 *
 * @param caseId unique identifier for the saved case
 * @param patientId the patient ID entered on PatientCaseScreen
 * @param patientName optional display name
 * @param dateLabel a human-readable date/time string (already formatted
 *                   by the caller, so this screen has no date-formatting
 *                   dependency of its own)
 * @param resultSummary a short one-line summary of the result
 *                       (e.g. "Trough 14.2 mg/L — within range")
 */
data class HistoryEntry(
    val caseId: String,
    val patientId: String,
    val patientName: String?,
    val dateLabel: String,
    val resultSummary: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    entries: List<HistoryEntry>,
    onEntryClick: (HistoryEntry) -> Unit,
    onDeleteEntry: (HistoryEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Case History") })
        },
        modifier = modifier
    ) { padding ->
        if (entries.isEmpty()) {
            EmptyHistoryState(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(entries, key = { it.caseId }) { entry ->
                    HistoryEntryCard(
                        entry = entry,
                        onClick = { onEntryClick(entry) },
                        onDelete = { onDeleteEntry(entry) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyHistoryState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No saved cases yet",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Cases you complete will appear here so you can review them later.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun HistoryEntryCard(
    entry: HistoryEntry,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.patientName?.takeIf { it.isNotBlank() }
                        ?: entry.patientId,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "ID: ${entry.patientId} • ${entry.dateLabel}",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = entry.resultSummary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            TextButton(onClick = onDelete) {
                Text("Delete")
            }
        }
    }
}