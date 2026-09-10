package my.edu.aiu.app.tdm_insight_android.model



/**
 * OWNER: Yalda
 * Fictional patient details. Never real patient data.
 * Serum creatinine is entered in mg/dL.
 */
data class PatientInput(
    val caseName: String = "",
    val ageYears: Int? = null,
    val weightKg: Double? = null,
    val heightCm: Double? = null,
    val isMale: Boolean = true,
    val serumCreatinineMgDl: Double? = null
)

/**
 * OWNER: Yalda
 * Everything the calculation engine needs. Fields not used by the
 * selected workflow stay null.
 *
 * Time convention (important, explain this to the lecturer):
 *   postSampleHoursAfterInfusionEnd = hours AFTER the infusion finished
 *   preSampleHoursBeforeNextDose    = hours BEFORE the next dose starts
 */
data class TdmInput(
    val workflow: WorkflowType = WorkflowType.PRE_POST,
    val patient: PatientInput = PatientInput(),
    val doseMg: Double? = null,
    val intervalHours: Double? = null,
    val infusionHours: Double? = null,
    val preConcentration: Double? = null,
    val preSampleHoursBeforeNextDose: Double? = null,
    val postConcentration: Double? = null,
    val postSampleHoursAfterInfusionEnd: Double? = null
)

/** Field keys. Elyas uses these to place an error under the right text field. */
object Field {
    const val CASE_NAME = "caseName"
    const val AGE = "ageYears"
    const val WEIGHT = "weightKg"
    const val HEIGHT = "heightCm"
    const val SCR = "serumCreatinineMgDl"
    const val DOSE = "doseMg"
    const val INTERVAL = "intervalHours"
    const val INFUSION = "infusionHours"
    const val PRE_CONC = "preConcentration"
    const val PRE_TIME = "preSampleHoursBeforeNextDose"
    const val POST_CONC = "postConcentration"
    const val POST_TIME = "postSampleHoursAfterInfusionEnd"
    const val GENERAL = "general"
}