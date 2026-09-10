package my.edu.aiu.app.tdm_insight_android.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import my.edu.aiu.app.tdm_insight_android.engine.CalculationOutcome
import my.edu.aiu.app.tdm_insight_android.engine.VancomycinEngine
import my.edu.aiu.app.tdm_insight_android.model.*
import my.edu.aiu.app.tdm_insight_android.validation.InputValidator

class PatientViewModel : ViewModel() {
    // 1. App State
    var selectedWorkflow by mutableStateOf(WorkflowType.PRE_POST)
    var calculationResult by mutableStateOf<CalculationOutcome?>(null)
    var validationOutcome by mutableStateOf<ValidationOutcome?>(null)

    // 2. Patient Details
    var caseName by mutableStateOf("")
    var age by mutableStateOf("")
    var weight by mutableStateOf("")
    var height by mutableStateOf("")
    var scr by mutableStateOf("")
    var isMale by mutableStateOf(true)
    var photoUri by mutableStateOf<Uri?>(null)
    
    // 3. Therapy Details
    var dose by mutableStateOf("")
    var interval by mutableStateOf("")
    var infusion by mutableStateOf("")
    var preConc by mutableStateOf("")
    var preTime by mutableStateOf("")
    var postConc by mutableStateOf("")
    var postTime by mutableStateOf("")
    
    // 4. Results Simulation (for the bottom of results screen)
    var plannedDose by mutableStateOf("")
    var plannedInterval by mutableStateOf("")
    var plannedInfusion by mutableStateOf("")
    var expectedCmax by mutableStateOf(0.0)
    var expectedCmin by mutableStateOf(0.0)

    /**
     * Requirement 7: Validate information before calculation.
     */
    fun performValidation(): Boolean {
        val input = buildInput()
        val outcome = InputValidator.validate(input)
        validationOutcome = outcome
        return outcome.isValid
    }

    /**
     * Requirement 9: Run the TDM calculation.
     */
    fun performCalculation() {
        val input = buildInput()
        calculationResult = VancomycinEngine.calculate(input)
    }

    private fun buildInput(): TdmInput {
        return TdmInput(
            workflow = selectedWorkflow,
            patient = PatientInput(
                caseName = caseName,
                ageYears = age.toIntOrNull(),
                weightKg = weight.toDoubleOrNull(),
                heightCm = height.toDoubleOrNull(),
                isMale = isMale,
                serumCreatinineMgDl = scr.toDoubleOrNull()
            ),
            doseMg = dose.toDoubleOrNull(),
            intervalHours = interval.toDoubleOrNull(),
            infusionHours = infusion.toDoubleOrNull(),
            preConcentration = preConc.toDoubleOrNull(),
            preSampleHoursBeforeNextDose = preTime.toDoubleOrNull(),
            postConcentration = postConc.toDoubleOrNull(),
            postSampleHoursAfterInfusionEnd = postTime.toDoubleOrNull()
        )
    }
}
