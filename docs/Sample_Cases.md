# Sample Cases

**Owner:** Benat
**Applies to:** Patient Case Entry screen (`PatientCaseScreen.kt`) and downstream vancomycin TDM engine

This document provides worked example patient cases for testing the app.
Each case is designed to satisfy the rules in `Validation_Rules.md` and
exercise a different combination of inputs (e.g. different sexes, level
types, and edge-of-range values). These cases can be typed into the
Patient Case Entry screen manually during testing, and later referenced
when writing automated tests against the engine.

## Case 1 — Standard adult male, trough level

A typical, uncomplicated case with values well within normal ranges.

| Field | Value |
|---|---|
| Patient ID | P-001 |
| Patient Name | Ahmad Faiz |
| Age | 45 |
| Sex | Male |
| Weight | 72 kg |
| Height | 172 cm |
| Serum Creatinine | 0.9 mg/dL |
| Current Dose | 1000 mg |
| Dosing Interval | 12 hours |
| Infusion Duration | 1 hour |
| Level Type | Trough |
| Measured Level | 14.2 mg/L |
| Time Since Last Dose | 11.5 hours |

**Expected behaviour:** All fields pass validation; form proceeds to the
next screen without errors.

## Case 2 — Adult female, peak level

Exercises the "Peak" level type and a different sex selection.

| Field | Value |
|---|---|
| Patient ID | P-002 |
| Patient Name | Nur Aisyah |
| Age | 38 |
| Sex | Female |
| Weight | 58 kg |
| Height | 160 cm |
| Serum Creatinine | 0.7 mg/dL |
| Current Dose | 750 mg |
| Dosing Interval | 8 hours |
| Infusion Duration | 1 hour |
| Level Type | Peak |
| Measured Level | 28.5 mg/L |
| Time Since Last Dose | 1.5 hours |

**Expected behaviour:** All fields pass validation; form proceeds normally.

## Case 3 — Older patient, reduced renal function, random level

A higher serum creatinine simulates reduced renal function, which is
clinically relevant for vancomycin dosing adjustments.

| Field | Value |
|---|---|
| Patient ID | P-003 |
| Patient Name | (left blank — optional field) |
| Age | 78 |
| Sex | Male |
| Weight | 65 kg |
| Height | (left blank — optional field) |
| Serum Creatinine | 2.1 mg/dL |
| Current Dose | 500 mg |
| Dosing Interval | 24 hours |
| Infusion Duration | 1.5 hours |
| Level Type | Random |
| Measured Level | 18.0 mg/L |
| Time Since Last Dose | 6 hours |

**Expected behaviour:** Form accepts the blank optional fields (Patient
Name, Height) and proceeds normally, confirming optional fields do not
block submission.

## Case 4 — Edge-of-range weight (validation boundary test)

Deliberately tests the upper boundary of the weight rule
(greater than 0, up to and including 300 kg).

| Field | Value |
|---|---|
| Patient ID | P-004 |
| Patient Name | Test Case — Max Weight |
| Age | 60 |
| Sex | Male |
| Weight | 300 kg |
| Height | 180 cm |
| Serum Creatinine | 1.0 mg/dL |
| Current Dose | 1250 mg |
| Dosing Interval | 12 hours |
| Infusion Duration | 1 hour |
| Level Type | Trough |
| Measured Level | 15.0 mg/L |
| Time Since Last Dose | 11 hours |

**Expected behaviour:** Passes validation (300 kg is the inclusive upper
bound). Confirm 301 kg correctly fails with "Enter a valid weight in kg".

## Case 5 — Invalid case (for negative testing)

Used to confirm the form correctly blocks submission and shows the
right error messages.

| Field | Value | Expected Error |
|---|---|---|
| Patient ID | (blank) | "Patient ID is required" |
| Age | 150 | "Enter a valid age (1-120)" |
| Sex | (not selected) | "Select sex" |
| Weight | 0 | "Enter a valid weight in kg" |
| Serum Creatinine | -1 | "Enter a valid serum creatinine value" |
| Current Dose | (blank) | "Enter a valid dose in mg" |
| Level Type | (not selected) | "Select level type" |
| Measured Level | 0 | "Enter a valid measured level in mg/L" |

**Expected behaviour:** All listed errors appear simultaneously beneath
their respective fields; the **Continue** button does not proceed to the
next screen.

## Notes for the team

- Cases 1–4 are valid, "happy path" cases intended to reach the engine
  successfully — useful once `VancomycinEngine.kt` is ready, to confirm
  end-to-end results look clinically reasonable.
- Case 5 is a negative test, specific to the form layer, and does not
  need to reach the engine.
- If Yalda's engine expects different units (e.g. µmol/L instead of
  mg/dL for creatinine), these cases should be updated to match — flag
  this early since it also affects `Validation_Rules.md` and
  `PatientCaseScreen.kt`.