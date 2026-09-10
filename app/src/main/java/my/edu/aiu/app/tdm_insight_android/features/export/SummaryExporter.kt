package my.edu.aiu.app.tdm_insight_android.features.export

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import my.edu.aiu.app.tdm_insight_android.model.SavedCase
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * OWNER: Benat
 *
 * Generates and shares a text summary of a fictional clinical case.
 */
object SummaryExporter {

    fun generateTextSummary(case: SavedCase): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
        val dateString = dateFormat.format(Date(case.savedAtMillis))

        return """
            TDM INSIGHT - CASE SUMMARY
            --------------------------
            Case Name: ${case.caseName}
            Workflow: ${case.workflow.title}
            Date: $dateString
            
            INPUT PARAMETERS:
            - Weight: ${case.weightKg} kg
            - Dose: ${case.doseMg} mg
            - Interval: ${case.intervalHours} h
            
            CALCULATED PARAMETERS:
            - Elimination Rate (Ke): ${f(case.kePerHour, 4)} h⁻¹
            - Half-life: ${f(case.halfLifeHours)} h
            - Volume of Distribution: ${f(case.volumeOfDistributionL)} L
            - Clearance: ${f(case.clearanceLPerHour)} L/h
            
            FINAL RESULT:
            - AUC24: ${f(case.auc24, 1)} mg.h/L
            
            DISCLAIMER: This is a fictional educational tool.
            Never use for real patient care.
        """.trimIndent()
    }

    private fun f(value: Double, decimals: Int = 2): String =
        String.format(Locale.US, "%.${decimals}f", value)

    /**
     * Exports the summary to a temporary file and launches the Android share sheet.
     */
    fun exportAndShare(context: Context, case: SavedCase) {
        val summaryText = generateTextSummary(case)

        // Save summary to a file in the app's external cache to avoid cluttering storage
        val cacheDir = context.externalCacheDir ?: context.cacheDir
        val file = File(cacheDir, "TDM_Case_${case.id}.txt")
        file.writeText(summaryText)

        // Get secure URI via FileProvider
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        // Create share intent
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "TDM Case Summary: ${case.caseName}")
            putExtra(Intent.EXTRA_TEXT, summaryText)
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // Launch chooser. If context is not an Activity, we need to add NEW_TASK flag to the chooser.
        val chooser = Intent.createChooser(shareIntent, "Share Case Summary")
        if (context !is android.app.Activity) {
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }
}
