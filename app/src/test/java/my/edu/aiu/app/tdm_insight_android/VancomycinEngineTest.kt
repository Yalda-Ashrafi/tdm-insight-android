package my.edu.aiu.app.tdm_insight_android

import my.edu.aiu.app.tdm_insight_android.engine.CalculationOutcome
import my.edu.aiu.app.tdm_insight_android.engine.PharmacokineticFormulas
import my.edu.aiu.app.tdm_insight_android.engine.VancomycinEngine
import my.edu.aiu.app.tdm_insight_android.model.PatientInput
import my.edu.aiu.app.tdm_insight_android.model.TdmInput
import my.edu.aiu.app.tdm_insight_android.model.WorkflowType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * OWNER: Yalda
 *
 * Unit tests for the PK math and the calculation engine.
 * These tests ensure that clinical logic remains correct even if
 * the UI changes.
 */
class VancomycinEngineTest {

    @Test
    fun testHalfLifeCalculation() {
        // Half-life = ln(2) / Ke
        // 0.693 / 0.1 = 6.93
        val result = PharmacokineticFormulas.halfLife(0.1)
        assertEquals(6.931, result, 0.001)
    }

    @Test
    fun testCrClCalculation() {
        // Example adult male: 40y, 70kg, SCr 1.0 mg/dL
        // CrCl = ((140 - 40) * 70) / (72 * 1.0) = 7000 / 72 = 97.22
        val crCl = PharmacokineticFormulas.cockcroftGaultCrCl(
            ageYears = 40,
            weightKg = 70.0,
            serumCreatinineMgDl = 1.0,
            isMale = true
        )
        assertEquals(97.22, crCl, 0.01)
    }

    @Test
    fun testEngineValidationFailure() {
        // Missing required fields should result in Failure
        val emptyInput = TdmInput(
            workflow = WorkflowType.PRE_POST,
            patient = PatientInput(caseName = "")
        )
        val outcome = VancomycinEngine.calculate(emptyInput)
        assertTrue(outcome is CalculationOutcome.Failure)
    }

    @Test
    fun testEnginePrePostSuccess() {
        // Fictional successful case (Measured Ke)
        val input = TdmInput(
            workflow = WorkflowType.PRE_POST,
            patient = PatientInput(
                caseName = "Test Case",
                ageYears = 65,
                weightKg = 80.0,
                heightCm = 175.0,
                serumCreatinineMgDl = 1.2
            ),
            doseMg = 1000.0,
            intervalHours = 12.0,
            infusionHours = 1.0,
            postConcentration = 30.0,
            postSampleHoursAfterInfusionEnd = 1.0,
            preConcentration = 15.0,
            preSampleHoursBeforeNextDose = 1.0
        )

        val outcome = VancomycinEngine.calculate(input)
        assertTrue("Calculation should succeed", outcome is CalculationOutcome.Success)
        
        if (outcome is CalculationOutcome.Success) {
            val result = outcome.result
            // Check that Ke was actually calculated from the levels
            assertTrue(result.kePerHour > 0)
            assertEquals("Measured from the two concentrations (Sawchuk-Zaske).", result.keSource)
        }
    }
}
