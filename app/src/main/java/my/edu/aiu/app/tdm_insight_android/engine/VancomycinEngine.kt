package my.edu.aiu.app.tdm_insight_android.engine

import my.edu.aiu.app.tdm_insight_android.model.Field
import my.edu.aiu.app.tdm_insight_android.model.Severity
import my.edu.aiu.app.tdm_insight_android.model.TdmInput
import my.edu.aiu.app.tdm_insight_android.model.TdmResult
import my.edu.aiu.app.tdm_insight_android.model.ValidationError
import my.edu.aiu.app.tdm_insight_android.model.WorkflowType
import my.edu.aiu.app.tdm_insight_android.validation.InputValidator
import kotlin.math.exp

/**
 * OWNER: Yalda
 */
sealed interface CalculationOutcome {
    data class Success(val result: TdmResult) : CalculationOutcome
    data class Failure(val issues: List<ValidationError>) : CalculationOutcome
}

object VancomycinEngine {

    private const val AUC_TARGET_LOW = 400.0
    private const val AUC_TARGET_HIGH = 600.0

    fun calculate(input: TdmInput): CalculationOutcome {
        val validation = InputValidator.validate(input)
        if (!validation.isValid) return CalculationOutcome.Failure(validation.errors)

        return try {
            val result = when (input.workflow) {
                WorkflowType.PRE_POST -> calculatePrePost(input)
                WorkflowType.POST -> calculateSingleLevel(input, usePost = true)
                WorkflowType.PRE -> calculateSingleLevel(input, usePost = false)
            }
            CalculationOutcome.Success(result.copy(notices = result.notices + validation.notices + reviewNotices(result)))
        } catch (e: IllegalArgumentException) {
            CalculationOutcome.Failure(
                listOf(ValidationError(Field.GENERAL, e.message ?: "The calculation could not be completed."))
            )
        } catch (e: Exception) {
            CalculationOutcome.Failure(
                listOf(ValidationError(Field.GENERAL, "Unexpected calculation error: ${e.message}"))
            )
        }
    }

    private fun calculatePrePost(input: TdmInput): TdmResult {
        val dose = input.doseMg!!
        val tau = input.intervalHours!!
        val tInf = input.infusionHours!!
        val cPost = input.postConcentration!!
        val cPre = input.preConcentration!!
        val tPost = input.postSampleHoursAfterInfusionEnd!!
        val tPre = input.preSampleHoursBeforeNextDose!!

        val postClock = tInf + tPost
        val preClock = tau - tPre
        val hoursBetween = preClock - postClock

        val ke = PharmacokineticFormulas.keFromTwoLevels(cPost, cPre, hoursBetween)
        val halfLife = PharmacokineticFormulas.halfLife(ke)

        val truePeak = PharmacokineticFormulas.extrapolateBack(cPost, ke, tPost)
        val trueTrough = PharmacokineticFormulas.extrapolateForward(cPre, ke, tPre)

        val vd = PharmacokineticFormulas.volumeOfDistributionTwoLevel(dose, tInf, ke, truePeak, trueTrough)
        val cl = PharmacokineticFormulas.clearance(ke, vd)
        val daily = PharmacokineticFormulas.dailyDose(dose, tau)
        val auc = PharmacokineticFormulas.auc24(daily, cl)
        val crCl = safeCrCl(input)

        val steps = CalculationExplainer.buildPrePostSteps(
            input = input, hoursBetween = hoursBetween, ke = ke, halfLife = halfLife,
            truePeak = truePeak, trueTrough = trueTrough, vd = vd,
            cl = cl, daily = daily, auc = auc
        )

        return TdmResult(
            workflow = WorkflowType.PRE_POST,
            kePerHour = ke,
            halfLifeHours = halfLife,
            volumeOfDistributionL = vd,
            clearanceLPerHour = cl,
            peakAtEndOfInfusion = truePeak,
            troughBeforeNextDose = trueTrough,
            auc24 = auc,
            dailyDoseMg = daily,
            creatinineClearanceMlMin = crCl,
            keSource = "Measured from the two concentrations (Sawchuk-Zaske).",
            steps = steps
        )
    }

    private fun calculateSingleLevel(input: TdmInput, usePost: Boolean): TdmResult {
        val dose = input.doseMg!!
        val tau = input.intervalHours!!
        val tInf = input.infusionHours!!

        val crCl = PharmacokineticFormulas.cockcroftGaultCrCl(
            ageYears = input.patient.ageYears!!,
            weightKg = input.patient.weightKg!!,
            serumCreatinineMgDl = input.patient.serumCreatinineMgDl!!,
            isMale = input.patient.isMale
        )
        val ke = PharmacokineticFormulas.populationKeFromCrCl(crCl)
        val halfLife = PharmacokineticFormulas.halfLife(ke)

        val truePeak: Double
        val trueTrough: Double

        if (usePost) {
            val cPost = input.postConcentration!!
            val tPost = input.postSampleHoursAfterInfusionEnd!!
            truePeak = PharmacokineticFormulas.extrapolateBack(cPost, ke, tPost)
            trueTrough = PharmacokineticFormulas.extrapolateForward(truePeak, ke, tau - tInf)
        } else {
            val cPre = input.preConcentration!!
            val tPre = input.preSampleHoursBeforeNextDose!!
            trueTrough = PharmacokineticFormulas.extrapolateForward(cPre, ke, tPre)
            truePeak = PharmacokineticFormulas.extrapolateBack(trueTrough, ke, tau - tInf)
        }

        val vd = PharmacokineticFormulas.volumeOfDistributionSingleLevel(dose, tInf, tau, ke, truePeak)
        val cl = PharmacokineticFormulas.clearance(ke, vd)
        val daily = PharmacokineticFormulas.dailyDose(dose, tau)
        val auc = PharmacokineticFormulas.auc24(daily, cl)

        val steps = CalculationExplainer.buildSingleLevelSteps(
            input = input, usePost = usePost, crCl = crCl, ke = ke, halfLife = halfLife,
            truePeak = truePeak, trueTrough = trueTrough, vd = vd, cl = cl, daily = daily, auc = auc
        )

        return TdmResult(
            workflow = if (usePost) WorkflowType.POST else WorkflowType.PRE,
            kePerHour = ke,
            halfLifeHours = halfLife,
            volumeOfDistributionL = vd,
            clearanceLPerHour = cl,
            peakAtEndOfInfusion = truePeak,
            troughBeforeNextDose = trueTrough,
            auc24 = auc,
            dailyDoseMg = daily,
            creatinineClearanceMlMin = crCl,
            keSource = "Estimated from renal function.",
            steps = steps,
            notices = listOf(
                ValidationError(
                    Field.GENERAL,
                    "Ke was estimated from renal function, not measured. A Pre + Post workflow gives a more accurate result.",
                    Severity.INFO
                )
            )
        )
    }

    private fun safeCrCl(input: TdmInput): Double? {
        val age = input.patient.ageYears ?: return null
        val weight = input.patient.weightKg ?: return null
        val scr = input.patient.serumCreatinineMgDl ?: return null
        if (scr <= 0) return null
        return PharmacokineticFormulas.cockcroftGaultCrCl(age, weight, scr, input.patient.isMale)
    }

    private fun reviewNotices(result: TdmResult): List<ValidationError> {
        val notices = mutableListOf<ValidationError>()
        when (result.workflow) {
            WorkflowType.PRE -> notices += ValidationError(
                Field.GENERAL,
                "Clinical Intent: Verifying efficacy (trough level).",
                Severity.INFO
            )
            WorkflowType.POST -> notices += ValidationError(
                Field.GENERAL,
                "Clinical Intent: Safety check (peak level).",
                Severity.INFO
            )
            else -> {}
        }
        if (result.auc24 < AUC_TARGET_LOW) {
            notices += ValidationError(Field.GENERAL, "AUC24 is below adult target ($AUC_TARGET_LOW).", Severity.INFO)
        } else if (result.auc24 > AUC_TARGET_HIGH) {
            notices += ValidationError(Field.GENERAL, "AUC24 is above adult target ($AUC_TARGET_HIGH).", Severity.INFO)
        }
        return notices
    }

    /** 
     * GENERATES POINTS FOR THE GRAPH
     * Updated to use REAL exponential accumulation and decay.
     */
    fun curvePoints(result: TdmResult, intervalHours: Double, infusionHours: Double, steps: Int = 100): List<Pair<Double, Double>> {
        val points = mutableListOf<Pair<Double, Double>>()
        val ke = result.kePerHour
        
        // 1. Infusion Phase (Rise)
        // Formula: C(t) = (Cmax_ss - Cmin_ss * exp(-k*t)) / (1 - exp(-k*t)) ... simplified for visualization:
        val stepsInf = (steps * (infusionHours / intervalHours)).toInt().coerceAtLeast(10)
        for (i in 0..stepsInf) {
            val t = infusionHours * i / stepsInf
            // Accumulation curve starting from trough
            val c = result.troughBeforeNextDose * exp(-ke * t) + 
                    (result.peakAtEndOfInfusion - result.troughBeforeNextDose * exp(-ke * infusionHours)) * 
                    ((1 - exp(-ke * t)) / (1 - exp(-ke * infusionHours)))
            points += t to c
        }
        
        // 2. Elimination Phase (Decay)
        val stepsElim = steps - stepsInf
        for (i in 0..stepsElim) {
            val tRel = (intervalHours - infusionHours) * i / stepsElim
            val tAbs = infusionHours + tRel
            val c = result.peakAtEndOfInfusion * exp(-ke * tRel)
            points += tAbs to c
        }
        return points
    }
}
