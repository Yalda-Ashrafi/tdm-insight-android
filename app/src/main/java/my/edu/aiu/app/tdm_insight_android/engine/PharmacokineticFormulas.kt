package my.edu.aiu.app.tdm_insight_android.engine


import kotlin.math.exp
import kotlin.math.ln

/**
 * OWNER: Yalda
 *
 * Pure pharmacokinetic mathematics. No Android code, no UI code.
 * Because this object is plain Kotlin it can be unit tested directly.
 *
 * EVERY equation here is a published equation, not an invented one.
 * The source of each is written in docs/Formulas.md and must be
 * confirmed with the lecturer before submission.
 */
object PharmacokineticFormulas {

    const val LN2 = 0.6931471805599453

    /** Convert serum creatinine from umol/L to mg/dL (divide by 88.4). */
    fun umolPerLitreToMgPerDl(umolPerL: Double): Double = umolPerL / 88.4

    /**
     * Cockcroft-Gault creatinine clearance, mL/min.
     * Female result is multiplied by 0.85.
     */
    fun cockcroftGaultCrCl(
        ageYears: Int,
        weightKg: Double,
        serumCreatinineMgDl: Double,
        isMale: Boolean
    ): Double {
        require(serumCreatinineMgDl > 0) { "Serum creatinine must be greater than zero." }
        val base = ((140.0 - ageYears) * weightKg) / (72.0 * serumCreatinineMgDl)
        return if (isMale) base else base * 0.85
    }

    /** Devine ideal body weight, kg. Used as a cross-check for dosing weight. */
    fun idealBodyWeight(heightCm: Double, isMale: Boolean): Double {
        val inchesOver5Feet = ((heightCm / 2.54) - 60.0).coerceAtLeast(0.0)
        return if (isMale) 50.0 + 2.3 * inchesOver5Feet else 45.5 + 2.3 * inchesOver5Feet
    }

    /**
     * Matzke population estimate of the vancomycin elimination rate
     * constant from creatinine clearance. Used only when a single
     * concentration is available, so Ke cannot be measured.
     */
    fun populationKeFromCrCl(crClMlMin: Double): Double = 0.00083 * crClMlMin + 0.0044

    /**
     * Measured elimination rate constant from two concentrations.
     * Sawchuk-Zaske method.
     */
    fun keFromTwoLevels(
        higherConcentration: Double,
        lowerConcentration: Double,
        hoursBetweenSamples: Double
    ): Double {
        require(hoursBetweenSamples > 0) { "Time between samples must be greater than zero." }
        require(lowerConcentration > 0) { "Concentrations must be greater than zero." }
        require(higherConcentration > lowerConcentration) {
            "The post-dose level must be higher than the pre-dose level."
        }
        return ln(higherConcentration / lowerConcentration) / hoursBetweenSamples
    }

    /** Elimination half-life in hours. */
    fun halfLife(kePerHour: Double): Double {
        require(kePerHour > 0) { "Ke must be greater than zero." }
        return LN2 / kePerHour
    }

    /** Extrapolate a level backwards in time (towards a higher value). */
    fun extrapolateBack(concentration: Double, kePerHour: Double, hours: Double): Double =
        concentration * exp(kePerHour * hours)

    /** Extrapolate a level forwards in time (towards a lower value). */
    fun extrapolateForward(concentration: Double, kePerHour: Double, hours: Double): Double =
        concentration * exp(-kePerHour * hours)

    /**
     * Volume of distribution from a measured peak and trough,
     * intermittent infusion at steady state (Sawchuk-Zaske).
     */
    fun volumeOfDistributionTwoLevel(
        doseMg: Double,
        infusionHours: Double,
        kePerHour: Double,
        peakAtEndOfInfusion: Double,
        troughBeforeNextDose: Double
    ): Double {
        require(infusionHours > 0) { "Infusion time must be greater than zero." }
        val infusionRate = doseMg / infusionHours
        val numerator = infusionRate * (1 - exp(-kePerHour * infusionHours))
        val denominator = kePerHour * (peakAtEndOfInfusion - troughBeforeNextDose * exp(-kePerHour * infusionHours))
        require(denominator != 0.0) { "Cannot calculate Vd: the denominator is zero." }
        require(denominator > 0) { "The concentrations given are not physically possible for this dose." }
        return numerator / denominator
    }

    /**
     * Volume of distribution when only one level is available and Ke
     * came from the population estimate. Steady-state infusion equation
     * rearranged for Vd.
     */
    fun volumeOfDistributionSingleLevel(
        doseMg: Double,
        infusionHours: Double,
        intervalHours: Double,
        kePerHour: Double,
        peakAtEndOfInfusion: Double
    ): Double {
        require(infusionHours > 0) { "Infusion time must be greater than zero." }
        require(peakAtEndOfInfusion > 0) { "Concentration must be greater than zero." }
        val infusionRate = doseMg / infusionHours
        val numerator = infusionRate * (1 - exp(-kePerHour * infusionHours))
        val denominator = peakAtEndOfInfusion * kePerHour * (1 - exp(-kePerHour * intervalHours))
        require(denominator != 0.0) { "Cannot calculate Vd: the denominator is zero." }
        return numerator / denominator
    }

    /** Clearance in L/h. */
    fun clearance(kePerHour: Double, volumeOfDistributionL: Double): Double =
        kePerHour * volumeOfDistributionL

    /** Total dose given in 24 hours. */
    fun dailyDose(doseMg: Double, intervalHours: Double): Double {
        require(intervalHours > 0) { "Dosing interval must be greater than zero." }
        return doseMg * (24.0 / intervalHours)
    }

    /** AUC over 24 hours in mg.h/L. */
    fun auc24(dailyDoseMg: Double, clearanceLPerHour: Double): Double {
        require(clearanceLPerHour > 0) { "Clearance must be greater than zero." }
        return dailyDoseMg / clearanceLPerHour
    }
}