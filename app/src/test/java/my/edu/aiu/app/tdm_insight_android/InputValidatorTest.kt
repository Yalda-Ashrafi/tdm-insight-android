package my.edu.aiu.app.tdm_insight_android

import my.edu.aiu.app.tdm_insight_android.model.Field
import my.edu.aiu.app.tdm_insight_android.model.PatientInput
import my.edu.aiu.app.tdm_insight_android.model.TdmInput
import my.edu.aiu.app.tdm_insight_android.model.WorkflowType
import my.edu.aiu.app.tdm_insight_android.validation.InputValidator
import org.junit.Assert.*
import org.junit.Test

/**
 * OWNER: Yalda
 *
 * Tests the InputValidator object to ensure it catches range errors
 * and missing required fields before the engine runs.
 */
class InputValidatorTest {

    @Test
    fun testEmptyCaseNameIsInvalid() {
        val input = TdmInput(
            workflow = WorkflowType.PRE,
            patient = PatientInput(caseName = "   ")
        )
        val outcome = InputValidator.validate(input)
        assertFalse("Case name should not be empty", outcome.isValid)
        assertTrue(outcome.errorMap().containsKey(Field.CASE_NAME))
    }

    @Test
    fun testZeroCreatinineIsInvalid() {
        val input = TdmInput(
            workflow = WorkflowType.PRE,
            patient = PatientInput(
                caseName = "Valid Name",
                serumCreatinineMgDl = 0.0
            )
        )
        val outcome = InputValidator.validate(input)
        assertFalse("Serum creatinine cannot be zero (division by zero guard)", outcome.isValid)
        assertTrue(outcome.errorMap().containsKey(Field.SCR))
    }

    @Test
    fun testValidInputPasses() {
        val input = TdmInput(
            workflow = WorkflowType.PRE,
            patient = PatientInput(
                caseName = "Normal Patient",
                ageYears = 50,
                weightKg = 70.0,
                heightCm = 170.0,
                serumCreatinineMgDl = 1.0
            ),
            doseMg = 1000.0,
            intervalHours = 12.0,
            infusionHours = 1.0,
            preConcentration = 15.0,
            preSampleHoursBeforeNextDose = 0.5
        )
        val outcome = InputValidator.validate(input)
        assertTrue("Valid input should pass validation", outcome.isValid)
    }

    @Test
    fun testImpossibleTimingIsInvalid() {
        val input = TdmInput(
            workflow = WorkflowType.PRE_POST,
            doseMg = 1000.0,
            intervalHours = 8.0,
            infusionHours = 10.0 // Infusion > Interval!
        )
        val outcome = InputValidator.validate(input)
        assertFalse("Infusion time cannot exceed interval", outcome.isValid)
        assertTrue(outcome.errorMap().containsKey(Field.INFUSION))
    }

    // 🔹 New boundary test
    @Test
    fun testNegativeAgeIsInvalid() {
        val input = TdmInput(
            workflow = WorkflowType.PRE,
            patient = PatientInput(ageYears = -5)
        )
        val outcome = InputValidator.validate(input)
        assertFalse("Age cannot be negative", outcome.isValid)
        assertTrue(outcome.errorMap().containsKey(Field.AGE))
    }

    // 🔹 New multi-error test
    @Test
    fun testMultipleInvalidFields() {
        val input = TdmInput(
            workflow = WorkflowType.PRE,
            patient = PatientInput(caseName = "", serumCreatinineMgDl = 0.0)
        )
        val outcome = InputValidator.validate(input)
        assertFalse("Multiple invalid fields should fail", outcome.isValid)
        assertTrue(outcome.errorMap().containsKey(Field.CASE_NAME))
        assertTrue(outcome.errorMap().containsKey(Field.SCR))
    }
}
