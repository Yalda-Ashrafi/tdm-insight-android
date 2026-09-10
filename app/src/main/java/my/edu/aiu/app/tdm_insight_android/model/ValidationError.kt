package my.edu.aiu.app.tdm_insight_android.model


/**
 * OWNER: Yalda
 * The case study asks for messages that separate a real ERROR from
 * information that simply needs review, so severity is part of the model.
 */
enum class Severity { ERROR, INFO }

data class ValidationError(
    val field: String,
    val message: String,
    val severity: Severity = Severity.ERROR
)

data class ValidationOutcome(
    val issues: List<ValidationError> = emptyList()
) {
    val errors: List<ValidationError> get() = issues.filter { it.severity == Severity.ERROR }
    val notices: List<ValidationError> get() = issues.filter { it.severity == Severity.INFO }
    val isValid: Boolean get() = errors.isEmpty()

    /** Map of field key to error message, for the UI. */
    fun errorMap(): Map<String, String> = errors.associate { it.field to it.message }
}