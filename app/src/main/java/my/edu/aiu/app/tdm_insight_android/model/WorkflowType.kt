package my.edu.aiu.app.tdm_insight_android.model

/**
 * OWNER: Yalda
 * The three Vancomycin TDM workflows required by the case study.
 * The selected workflow decides which input fields are shown and
 * which calculation pathway the engine runs.
 */
enum class WorkflowType(
    val title: String,
    val description: String
) {
    PRE(
        title = "Vancomycin Pre",
        description = "Uses one pre-dose (trough) concentration. Elimination is estimated from renal function."
    ),
    POST(
        title = "Vancomycin Post",
        description = "Uses one post-dose (peak) concentration and its sampling time."
    ),
    PRE_POST(
        title = "Vancomycin Pre + Post",
        description = "Uses both concentrations. The most accurate workflow because Ke is measured, not estimated."
    );

    val needsPre: Boolean get() = this == PRE || this == PRE_POST
    val needsPost: Boolean get() = this == POST || this == PRE_POST
}