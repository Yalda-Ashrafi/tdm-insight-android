# Validation Rules

**Owner:** Benat
**Applies to:** Patient Case Entry screen (`PatientCaseScreen.kt`)

This document describes the input validation rules enforced when a user enters
a patient case into TDM Insight. These rules exist to catch incomplete or
clinically implausible data before it reaches the vancomycin dosing engine,
since bad inputs at this stage would produce meaningless or dangerous-looking
results downstream.

Validation happens at two levels in this app:

1. **Form-level validation** (this document, implemented in
   `validatePatientCaseForm()` inside `PatientCaseScreen.kt`) — quick,
   field-by-field checks that give the user immediate feedback as they fill
   in the form.
2. **Engine-level validation** (`InputValidator.kt`, owned by Yalda) — a
   deeper pass that re-checks the same data before it is handed to the
   pharmacokinetic engine, acting as a safety net in case the UI layer is
   bypassed.

## Fields and rules

| Field | Rule | Reasoning |
|---|---|---|
| Patient ID | Required, cannot be blank | Every case must be traceable to a patient record. |
| Patient Name | Optional | Not clinically required for the calculation; kept for readability of saved cases. |
| Age | Required. Must be a whole number between 1 and 120 | Rules out empty input, non-numeric input, and biologically implausible ages. |
| Sex | Required, must select Male or Female | Needed for creatinine clearance calculation (Cockcroft-Gault formula differs by sex). |
| Weight | Required. Must be a number greater than 0 and no more than 300 kg | Directly used in dosing and clearance calculations; upper bound catches obvious data-entry errors. |
| Height | Optional | Not required for the current calculation but may support future enhancements (e.g. BMI-based dosing adjustments). |
| Serum Creatinine | Required. Must be a number greater than 0 | Core input to estimate renal function, which drives vancomycin clearance. |
| Current Dose | Required. Must be a number greater than 0 | The dose the patient is currently receiving; needed to interpret the measured level. |
| Dosing Interval | Required. Must be a number greater than 0 | Time between doses (hours); needed for pharmacokinetic modelling. |
| Infusion Duration | Required. Must be a number greater than 0 | Length of the infusion (hours); affects peak concentration timing. |
| Level Type | Required, must select Peak, Trough, or Random | Determines how the measured level should be interpreted relative to the dosing schedule. |
| Measured Level | Required. Must be a number greater than 0 | The lab result (mg/L) that the engine uses to back-calculate patient-specific clearance. |
| Time Since Last Dose | Required. Must be a number, 0 or greater | Needed to know where in the dosing interval the blood sample was drawn. |

## Behaviour when validation fails

- Each field with an error shows an inline message directly beneath it,
  naming the problem (e.g. "Enter a valid age (1-120)").
- The **Continue** button re-runs all checks on tap. If any field fails,
  the form does not proceed to the next screen, and all relevant error
  messages are shown at once (not one at a time), so the user can fix
  everything in a single pass.
- No partial or unvalidated data is ever passed to the engine.

## Notes for the team

- These rules are a first pass based on standard vancomycin TDM inputs.
  If Yalda's `InputValidator.kt` enforces different bounds (e.g. a
  different maximum weight, or additional cross-field checks), this
  document and `PatientCaseScreen.kt` should be updated to match, so the
  two validation layers stay consistent.
- Cross-field checks (e.g. "time since last dose cannot exceed the dosing
  interval") are intentionally left to the engine-level validation layer,
  since they depend on relationships the form-level pass does not
  evaluate.