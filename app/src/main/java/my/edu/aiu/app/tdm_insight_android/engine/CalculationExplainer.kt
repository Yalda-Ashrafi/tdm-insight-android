package my.edu.aiu.app.tdm_insight_android.engine

import my.edu.aiu.app.tdm_insight_android.model.CalculationStep
import my.edu.aiu.app.tdm_insight_android.model.TdmInput
import java.util.Locale

/**
 * OWNER: Yalda
 *
 * Turns the calculation into a readable sequence for the Explanation screen:
 *   Input Values -> Intermediate Values -> Pharmacokinetic Parameters -> Final Result
 * The result screen must never show only a final number.
 */
object CalculationExplainer {

    private fun f(value: Double, decimals: Int = 2): String =
        String.format(Locale.US, "%.${decimals}f", value)

    fun buildPrePostSteps(
        input: TdmInput,
        hoursBetween: Double,
        ke: Double,
        halfLife: Double,
        truePeak: Double,
        trueTrough: Double,
        vd: Double,
        cl: Double,
        daily: Double,
        auc: Double
    ): List<CalculationStep> {
        val tInf = input.infusionHours!!
        val tau = input.intervalHours!!
        val tPost = input.postSampleHoursAfterInfusionEnd!!
        val tPre = input.preSampleHoursBeforeNextDose!!
        val cPost = input.postConcentration!!
        val cPre = input.preConcentration!!
        val dose = input.doseMg!!

        return listOf(
            CalculationStep(
                1, "Time between the two samples",
                "t = (tau - pre sample time) - (infusion time + post sample time)",
                "(${f(tau)} - ${f(tPre)}) - (${f(tInf)} + ${f(tPost)})",
                "${f(hoursBetween)} h"
            ),
            CalculationStep(
                2, "Elimination rate constant (Ke)",
                "Ke = ln(C post / C pre) / t",
                "ln(${f(cPost)} / ${f(cPre)}) / ${f(hoursBetween)}",
                "${f(ke, 4)} per hour"
            ),
            CalculationStep(
                3, "Elimination half-life",
                "t half = 0.693 / Ke",
                "0.693 / ${f(ke, 4)}",
                "${f(halfLife)} h"
            ),
            CalculationStep(
                4, "Peak extrapolated to the end of the infusion",
                "C max = C post x e^(Ke x post sample time)",
                "${f(cPost)} x e^(${f(ke, 4)} x ${f(tPost)})",
                "${f(truePeak)} mg/L"
            ),
            CalculationStep(
                5, "Trough extrapolated to just before the next dose",
                "C min = C pre x e^(-Ke x pre sample time)",
                "${f(cPre)} x e^(-${f(ke, 4)} x ${f(tPre)})",
                "${f(trueTrough)} mg/L"
            ),
            CalculationStep(
                6, "Volume of distribution (Vd)",
                "Vd = (Dose / t inf)(1 - e^(-Ke x t inf)) / (Ke x (C max - C min x e^(-Ke x t inf)))",
                "dose ${f(dose)} mg, infusion ${f(tInf)} h",
                "${f(vd)} L"
            ),
            CalculationStep(
                7, "Clearance",
                "CL = Ke x Vd",
                "${f(ke, 4)} x ${f(vd)}",
                "${f(cl)} L/h"
            ),
            CalculationStep(
                8, "Total dose in 24 hours",
                "Daily dose = Dose x (24 / tau)",
                "${f(dose)} x (24 / ${f(tau)})",
                "${f(daily, 0)} mg"
            ),
            CalculationStep(
                9, "AUC over 24 hours (final result)",
                "AUC24 = Daily dose / CL",
                "${f(daily, 0)} / ${f(cl)}",
                "${f(auc, 1)} mg.h/L"
            )
        )
    }

    fun buildSingleLevelSteps(
        input: TdmInput,
        usePost: Boolean,
        crCl: Double,
        ke: Double,
        halfLife: Double,
        truePeak: Double,
        trueTrough: Double,
        vd: Double,
        cl: Double,
        daily: Double,
        auc: Double
    ): List<CalculationStep> {
        val p = input.patient
        val tInf = input.infusionHours!!
        val tau = input.intervalHours!!
        val dose = input.doseMg!!
        val sexNote = if (p.isMale) "male, no correction" else "female, multiplied by 0.85"

        val measuredLabel = if (usePost) "post-dose level" else "pre-dose level"
        val measuredValue = if (usePost) input.postConcentration!! else input.preConcentration!!

        return listOf(
            CalculationStep(
                1, "Creatinine clearance (Cockcroft-Gault)",
                "CrCl = ((140 - age) x weight) / (72 x SCr), $sexNote",
                "((140 - ${p.ageYears}) x ${f(p.weightKg!!)}) / (72 x ${f(p.serumCreatinineMgDl!!)})",
                "${f(crCl, 1)} mL/min"
            ),
            CalculationStep(
                2, "Estimated elimination rate constant (Ke)",
                "Ke = 0.00083 x CrCl + 0.0044",
                "0.00083 x ${f(crCl, 1)} + 0.0044",
                "${f(ke, 4)} per hour"
            ),
            CalculationStep(
                3, "Elimination half-life",
                "t half = 0.693 / Ke",
                "0.693 / ${f(ke, 4)}",
                "${f(halfLife)} h"
            ),
            CalculationStep(
                4, "Measured $measuredLabel",
                "value entered by the user",
                "${f(measuredValue)} mg/L",
                "${f(measuredValue)} mg/L"
            ),
            CalculationStep(
                5, "Peak at the end of the infusion",
                "extrapolated using Ke",
                "using Ke = ${f(ke, 4)}",
                "${f(truePeak)} mg/L"
            ),
            CalculationStep(
                6, "Trough just before the next dose",
                "extrapolated using Ke",
                "using Ke = ${f(ke, 4)}",
                "${f(trueTrough)} mg/L"
            ),
            CalculationStep(
                7, "Volume of distribution (Vd)",
                "Vd = (Dose / t inf)(1 - e^(-Ke x t inf)) / (C max x Ke x (1 - e^(-Ke x tau)))",
                "dose ${f(dose)} mg, infusion ${f(tInf)} h, interval ${f(tau)} h",
                "${f(vd)} L"
            ),
            CalculationStep(
                8, "Clearance",
                "CL = Ke x Vd",
                "${f(ke, 4)} x ${f(vd)}",
                "${f(cl)} L/h"
            ),
            CalculationStep(
                9, "Total dose in 24 hours",
                "Daily dose = Dose x (24 / tau)",
                "${f(dose)} x (24 / ${f(tau)})",
                "${f(daily, 0)} mg"
            ),
            CalculationStep(
                10, "AUC over 24 hours (final result)",
                "AUC24 = Daily dose / CL",
                "${f(daily, 0)} / ${f(cl)}",
                "${f(auc, 1)} mg.h/L"
            )
        )
    }
}
