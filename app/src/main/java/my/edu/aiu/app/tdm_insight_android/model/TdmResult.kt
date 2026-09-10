package my.edu.aiu.app.tdm_insight_android.model

/**
 * OWNER: Yalda
 * One line of the explanation screen.
 */
data class CalculationStep(
    val order: Int,
    val label: String,
    val formula: String,
    val substitution: String,
    val value: String
)

/**
 * OWNER: Yalda
 * Structured output of the calculation engine. The UI only displays this.
 */
data class TdmResult(
    val workflow: WorkflowType,
    val kePerHour: Double,
    val halfLifeHours: Double,
    val volumeOfDistributionL: Double,
    val clearanceLPerHour: Double,
    val peakAtEndOfInfusion: Double,
    val troughBeforeNextDose: Double,
    val auc24: Double,
    val dailyDoseMg: Double,
    val creatinineClearanceMlMin: Double?,
    val keSource: String,
    val steps: List<CalculationStep>,
    val notices: List<ValidationError> = emptyList()
)