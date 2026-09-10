package my.edu.aiu.app.tdm_insight_android.validation

import my.edu.aiu.app.tdm_insight_android.model.Field
import my.edu.aiu.app.tdm_insight_android.model.Severity
import my.edu.aiu.app.tdm_insight_android.model.TdmInput
import my.edu.aiu.app.tdm_insight_android.model.ValidationError
import my.edu.aiu.app.tdm_insight_android.model.ValidationOutcome
import my.edu.aiu.app.tdm_insight_android.model.WorkflowType

/**
 * OWNER: Yalda
 *
 * Validation goes far beyond "is this field empty". It covers:
 *   1. required fields
 *   2. numeric and unit ranges
 *   3. cross-field timing logic
 *   4. missing values for the selected workflow
 *   5. protection against division by zero and invalid logarithms
 *   6. messages that separate an error from information to review
 */
object InputValidator {

    fun validate(input: TdmInput): ValidationOutcome {
        val issues = mutableListOf<ValidationError>()
        val p = input.patient

        // ---- 1. Patient fields -------------------------------------
        if (p.caseName.isBlank()) {
            issues += ValidationError(Field.CASE_NAME, "Give the fictional case a name.")
        }
        checkRange(issues, Field.AGE, p.ageYears?.toDouble(), 1.0, 120.0, "Age", "years")
        checkRange(issues, Field.WEIGHT, p.weightKg, 1.0, 300.0, "Weight", "kg")
        checkRange(issues, Field.HEIGHT, p.heightCm, 50.0, 250.0, "Height", "cm")
        checkRange(issues, Field.SCR, p.serumCreatinineMgDl, 0.1, 20.0, "Serum creatinine", "mg/dL")

        // Guard the Cockcroft-Gault division explicitly.
        if (p.serumCreatinineMgDl != null && p.serumCreatinineMgDl <= 0.0) {
            issues += ValidationError(Field.SCR, "Serum creatinine cannot be zero. The calculation would divide by zero.")
        }

        // ---- 2. Dosing fields --------------------------------------
        checkRange(issues, Field.DOSE, input.doseMg, 1.0, 5000.0, "Dose", "mg")
        checkRange(issues, Field.INTERVAL, input.intervalHours, 4.0, 48.0, "Dosing interval", "hours")
        checkRange(issues, Field.INFUSION, input.infusionHours, 0.25, 6.0, "Infusion time", "hours")

        val tau = input.intervalHours
        val tInf = input.infusionHours

        if (tau != null && tInf != null && tInf >= tau) {
            issues += ValidationError(
                Field.INFUSION,
                "The infusion time must be shorter than the dosing interval."
            )
        }

        // ---- 3. Workflow-specific fields ---------------------------
        if (input.workflow.needsPre) {
            checkRange(issues, Field.PRE_CONC, input.preConcentration, 0.1, 100.0, "Pre-dose concentration", "mg/L")
            checkRange(issues, Field.PRE_TIME, input.preSampleHoursBeforeNextDose, 0.0, 12.0, "Pre-dose sampling time", "hours before the next dose")
        }
        if (input.workflow.needsPost) {
            checkRange(issues, Field.POST_CONC, input.postConcentration, 0.1, 200.0, "Post-dose concentration", "mg/L")
            checkRange(issues, Field.POST_TIME, input.postSampleHoursAfterInfusionEnd, 0.0, 12.0, "Post-dose sampling time", "hours after the infusion ends")
        }

        // ---- 4. Cross-field timing logic ---------------------------
        val tPre = input.preSampleHoursBeforeNextDose
        val tPost = input.postSampleHoursAfterInfusionEnd

        if (tau != null && tPre != null && tPre >= tau) {
            issues += ValidationError(Field.PRE_TIME, "The pre-dose sample cannot be taken before the previous dose was given.")
        }

        if (input.workflow == WorkflowType.PRE_POST && tau != null && tInf != null && tPre != null && tPost != null) {
            val postClock = tInf + tPost
            val preClock = tau - tPre
            val gap = preClock - postClock
            if (gap <= 0) {
                issues += ValidationError(
                    Field.POST_TIME,
                    "The post-dose sample must be taken before the pre-dose sample. Check both sampling times."
                )
            } else if (gap < 1.0) {
                issues += ValidationError(
                    Field.POST_TIME,
                    "There is less than one hour between the two samples. Ke will be unreliable. Review this.",
                    Severity.INFO
                )
            }
        }

        // ---- 5. Protect the logarithm ------------------------------
        if (input.workflow == WorkflowType.PRE_POST) {
            val cPre = input.preConcentration
            val cPost = input.postConcentration
            if (cPre != null && cPost != null && cPost <= cPre) {
                issues += ValidationError(
                    Field.POST_CONC,
                    "The post-dose level must be higher than the pre-dose level, otherwise Ke cannot be calculated."
                )
            }
        }

        return ValidationOutcome(issues)
    }

    private fun checkRange(
        issues: MutableList<ValidationError>,
        field: String,
        value: Double?,
        min: Double,
        max: Double,
        label: String,
        unit: String
    ) {
        if (value == null) {
            issues += ValidationError(field, "$label is required.")
            return
        }
        if (value.isNaN() || value.isInfinite()) {
            issues += ValidationError(field, "$label is not a valid number.")
            return
        }
        if (value !in min..max) {
            issues += ValidationError(field, "$label must be between $min and $max $unit.")
        }
    }
}
