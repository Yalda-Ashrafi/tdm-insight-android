package com.aiu.tdminsight.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/**
 * PatientCaseScreen.kt
 * Owner: Benat
 *
 * Screen 2 — Patient case entry (mandatory).
 *
 * Collects the raw clinical inputs needed for the vancomycin TDM engine
 * (Yalda's VancomycinEngine / PharmacokineticFormulas). This screen does NOT
 * calculate anything itself — it only captures and locally validates input,
 * then hands a PatientCaseFormState off to the navigation layer.
 *
 * NOTE FOR TEAM: field names below are a draft based on standard vancomycin
 * PK inputs (needed to compute creatinine clearance + dosing). Please
 * reconcile field names/types against model.PatientInput before merging —
 * rename via find-and-replace once confirmed with Yalda.
 */

enum class Sex { MALE, FEMALE }

enum class LevelType { PEAK, TROUGH, RANDOM }

data class PatientCaseFormState(
    val patientId: String = "",
    val patientName: String = "",
    val age: String = "",
    val sex: Sex? = null,
    val weightKg: String = "",
    val heightCm: String = "",
    val serumCreatinine: String = "", // mg/dL
    val currentDoseMg: String = "",
    val dosingIntervalHours: String = "",
    val infusionDurationHours: String = "",
    val levelType: LevelType? = null,
    val measuredLevel: String = "", // mg/L
    val timeSinceLastDoseHours: String = ""
)

/**
 * Simple field-level validation. Kept here (UI-facing, quick feedback) and is
 * separate from the deeper InputValidator.kt (Yalda) that will re-validate
 * before the engine runs — this is just to stop obviously bad input early
 * and give the user immediate feedback.
 */
fun validatePatientCaseForm(state: PatientCaseFormState): Map<String, String> {
    val errors = mutableMapOf<String, String>()

    if (state.patientId.isBlank()) errors["patientId"] = "Patient ID is required"

    val age = state.age.toIntOrNull()
    if (age == null || age <= 0 || age > 120) errors["age"] = "Enter a valid age (1-120)"

    if (state.sex == null) errors["sex"] = "Select sex"

    val weight = state.weightKg.toDoubleOrNull()
    if (weight == null || weight <= 0 || weight > 300) errors["weightKg"] = "Enter a valid weight in kg"

    val scr = state.serumCreatinine.toDoubleOrNull()
    if (scr == null || scr <= 0) errors["serumCreatinine"] = "Enter a valid serum creatinine value"

    val dose = state.currentDoseMg.toDoubleOrNull()
    if (dose == null || dose <= 0) errors["currentDoseMg"] = "Enter a valid dose in mg"

    val interval = state.dosingIntervalHours.toDoubleOrNull()
    if (interval == null || interval <= 0) errors["dosingIntervalHours"] = "Enter a valid dosing interval"

    val infusion = state.infusionDurationHours.toDoubleOrNull()
    if (infusion == null || infusion <= 0) errors["infusionDurationHours"] = "Enter a valid infusion duration"

    if (state.levelType == null) errors["levelType"] = "Select level type"

    val level = state.measuredLevel.toDoubleOrNull()
    if (level == null || level <= 0) errors["measuredLevel"] = "Enter a valid measured level in mg/L"

    val timeSince = state.timeSinceLastDoseHours.toDoubleOrNull()
    if (timeSince == null || timeSince < 0) errors["timeSinceLastDoseHours"] = "Enter a valid time since last dose"

    return errors
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientCaseScreen(
    onSubmit: (PatientCaseFormState) -> Unit,
    onCancel: () -> Unit
) {
    var formState by remember { mutableStateOf(PatientCaseFormState()) }
    var errors by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var sexExpanded by remember { mutableStateOf(false) }
    var levelExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Patient Case Entry") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "Enter the patient's clinical details below. All fields are required unless marked optional.",
                style = MaterialTheme.typography.bodyMedium
            )

            OutlinedTextField(
                value = formState.patientId,
                onValueChange = { formState = formState.copy(patientId = it) },
                label = { Text("Patient ID") },
                isError = errors.containsKey("patientId"),
                supportingText = { errors["patientId"]?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = formState.patientName,
                onValueChange = { formState = formState.copy(patientName = it) },
                label = { Text("Patient Name (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = formState.age,
                onValueChange = { formState = formState.copy(age = it) },
                label = { Text("Age (years)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = errors.containsKey("age"),
                supportingText = { errors["age"]?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = sexExpanded,
                onExpandedChange = { sexExpanded = it }
            ) {
                OutlinedTextField(
                    value = formState.sex?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Sex") },
                    isError = errors.containsKey("sex"),
                    supportingText = { errors["sex"]?.let { Text(it) } },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = sexExpanded, onDismissRequest = { sexExpanded = false }) {
                    Sex.values().forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.name) },
                            onClick = {
                                formState = formState.copy(sex = option)
                                sexExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = formState.weightKg,
                onValueChange = { formState = formState.copy(weightKg = it) },
                label = { Text("Weight (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = errors.containsKey("weightKg"),
                supportingText = { errors["weightKg"]?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = formState.heightCm,
                onValueChange = { formState = formState.copy(heightCm = it) },
                label = { Text("Height (cm, optional)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = formState.serumCreatinine,
                onValueChange = { formState = formState.copy(serumCreatinine = it) },
                label = { Text("Serum Creatinine (mg/dL)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = errors.containsKey("serumCreatinine"),
                supportingText = { errors["serumCreatinine"]?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            Divider()
            Text("Current Dosing Regimen", style = MaterialTheme.typography.titleSmall)

            OutlinedTextField(
                value = formState.currentDoseMg,
                onValueChange = { formState = formState.copy(currentDoseMg = it) },
                label = { Text("Current Dose (mg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = errors.containsKey("currentDoseMg"),
                supportingText = { errors["currentDoseMg"]?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = formState.dosingIntervalHours,
                onValueChange = { formState = formState.copy(dosingIntervalHours = it) },
                label = { Text("Dosing Interval (hours)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = errors.containsKey("dosingIntervalHours"),
                supportingText = { errors["dosingIntervalHours"]?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = formState.infusionDurationHours,
                onValueChange = { formState = formState.copy(infusionDurationHours = it) },
                label = { Text("Infusion Duration (hours)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = errors.containsKey("infusionDurationHours"),
                supportingText = { errors["infusionDurationHours"]?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            Divider()
            Text("Measured Level", style = MaterialTheme.typography.titleSmall)

            ExposedDropdownMenuBox(
                expanded = levelExpanded,
                onExpandedChange = { levelExpanded = it }
            ) {
                OutlinedTextField(
                    value = formState.levelType?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Level Type") },
                    isError = errors.containsKey("levelType"),
                    supportingText = { errors["levelType"]?.let { Text(it) } },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = levelExpanded, onDismissRequest = { levelExpanded = false }) {
                    LevelType.values().forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.name) },
                            onClick = {
                                formState = formState.copy(levelType = option)
                                levelExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = formState.measuredLevel,
                onValueChange = { formState = formState.copy(measuredLevel = it) },
                label = { Text("Measured Level (mg/L)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = errors.containsKey("measuredLevel"),
                supportingText = { errors["measuredLevel"]?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = formState.timeSinceLastDoseHours,
                onValueChange = { formState = formState.copy(timeSinceLastDoseHours = it) },
                label = { Text("Time Since Last Dose (hours)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = errors.containsKey("timeSinceLastDoseHours"),
                supportingText = { errors["timeSinceLastDoseHours"]?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) {
                    Text("Cancel")
                }
                Button(
                    onClick = {
                        val validationErrors = validatePatientCaseForm(formState)
                        errors = validationErrors
                        if (validationErrors.isEmpty()) {
                            onSubmit(formState)
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Continue")
                }
            }
        }
    }
}
