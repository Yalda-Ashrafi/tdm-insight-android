package my.edu.aiu.app.tdm_insight_android.model



/**
 * OWNER: Yalda (shape) — used by Benat's history feature.
 * A fictional case stored on the device.
 */
data class SavedCase(
    val id: Long,
    val caseName: String,
    val workflow: WorkflowType,
    val savedAtMillis: Long,
    val weightKg: Double,
    val doseMg: Double,
    val intervalHours: Double,
    val kePerHour: Double,
    val halfLifeHours: Double,
    val volumeOfDistributionL: Double,
    val clearanceLPerHour: Double,
    val auc24: Double,
)