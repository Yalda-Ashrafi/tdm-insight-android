package my.edu.aiu.app.tdm_insight_android.features.history

import androidx.compose.runtime.mutableStateListOf
import my.edu.aiu.app.tdm_insight_android.model.SavedCase
import my.edu.aiu.app.tdm_insight_android.model.TdmResult
import my.edu.aiu.app.tdm_insight_android.model.TdmInput

/**
 * OWNER: Benat
 *
 * Manages the fictional cases saved by the user. 
 * Using mutableStateListOf allows Compose to automatically recompose 
 * screens when the history changes (e.g., allowing for updates after adding or deleting a case).
 */
object HistoryFeature {

    private val _history = mutableStateListOf<SavedCase>()
    val history: List<SavedCase> get() = _history

    /**
     * Converts an active calculation result into a saved entry.
     * New entries are added to the top of the list.
     */
    fun saveCalculation(input: TdmInput, result: TdmResult) {
        val newEntry = SavedCase(
            id = System.currentTimeMillis(),
            caseName = input.patient.caseName.ifBlank { "Untitled Case" },
            workflow = input.workflow,
            savedAtMillis = System.currentTimeMillis(),
            weightKg = input.patient.weightKg ?: 0.0,
            doseMg = input.doseMg ?: 0.0,
            intervalHours = input.intervalHours ?: 0.0,
            kePerHour = result.kePerHour,
            halfLifeHours = result.halfLifeHours,
            volumeOfDistributionL = result.volumeOfDistributionL,
            clearanceLPerHour = result.clearanceLPerHour,
            auc24 = result.auc24,
        )
        _history.add(0, newEntry)
    }

    fun clearHistory() {
        _history.clear()
    }

    fun deleteEntry(id: Long) {
        _history.removeAll { it.id == id }
    }
}
