# TDM Insight Android

An Android native mobile application that reimplements the core workflow of [myTDM Calculator](https://www.mytdmcalculator.com/) — a clinical therapeutic drug monitoring (TDM) tool for vancomycin dosing — as a modern, mobile-first app for pharmacists and clinicians.

## Course Information

| | |
|---|---|
| **Course Code** | CDE2313 |
| **Course Title** | Mobile Application Development |
| **Programme** | Bachelor in Computer Science |
| **Academic Session** | 2025/2026, Semester 3 |
| **Lecturer** | Ts Mohd Zulkifli Mohd Zaki |
| **Assessment** | Group Project (Assessment 2, 25%) |

## Group Members

| Name | Student ID | Role |
|---|---|---|
| Yalda Ashrafi | _TBD_ | Team Leader — Core engine, navigation, models, theme |
| Benat (Siraj) | _TBD_ | Enhancements — Patient case entry, history, graph, export |
| Mohammad Elyas Yameen | _TBD_ | UI/UX — Input form, results, explanation screens |

## Case Study — Problem Overview

Vancomycin is a narrow-therapeutic-index antibiotic: too low a dose risks treatment failure, too high risks toxicity (particularly to the kidneys). Clinicians rely on Therapeutic Drug Monitoring (TDM) — measuring drug levels in a patient's blood — to individualize dosing. Existing tools for this (such as the web-based myTDM Calculator) are desktop/browser-oriented and not well suited to bedside or mobile clinical use.

**TDM Insight** addresses this by bringing the same clinical workflow — patient data entry, dose/level interpretation, and dosing recommendations — into a native Android app, with a modern interface, saved case history, and shareable result summaries.

### Summary of Implemented Solution
- Guided, step-by-step workflow: patient details → workflow selection → dynamic input form → review → results → explanation
- Local input validation to catch incomplete or clinically implausible data before calculation
- Visual concentration-vs-time graph with a shaded therapeutic range band
- Case history so previous patients/results can be revisited
- One-tap export/share of a result summary as a text file

## Key Implemented Features

- **Patient Case Entry** — structured form capturing demographics, renal function, current dosing regimen, and measured drug level, with inline validation feedback
- **Workflow Selection & Dynamic Input Form** — adapts the input flow to the clinical scenario
- **Review & Results screens** — surfaces the calculated recommendation clearly
- **Explanation screen** — shows the reasoning/formulas behind the result
- **Concentration Graph** — a native-Compose-Canvas chart plotting the concentration-time curve with a therapeutic range band
- **Case History** *(optional feature)* — a saved list of past cases for later review
- **Summary Export** — generates a plain-text case summary and opens the Android share sheet

## Technology Stack & Application Architecture

- **Language:** Kotlin
- **UI Toolkit:** Jetpack Compose with Material 3
- **Architecture:** Feature-organized package structure (`ui/screens`, `features/*`, `engine`, `model`, `navigation`, `validation`)
- **Charting:** Native Compose `Canvas` (no external charting dependency)
- **File sharing:** `FileProvider` + Android share sheet
- **Testing:** JUnit-based unit tests for engine and validation logic

## Installation Guide

1. Clone the repository:
   ```
   git clone https://github.com/Yalda-Ashrafi/tdm-insight-android.git
   ```
2. Open the project in Android Studio (recommended: latest stable release)
3. Let Gradle sync complete
4. Connect a device or start an emulator (API level per `build.gradle.kts`)
5. Click **Run** ▶

## How to Build the Project

```
./gradlew assembleDebug
```

The debug APK will be generated under `app/build/outputs/apk/debug/`. A release APK is also provided directly in this repository at `apk/app-release.apk` for convenience.

## APK Download

See [`apk/app-release.apk`](./apk/app-release.apk) in this repository.

## Screenshots

_Screenshots to be added as each screen is finalized — see `screenshots/` folder._

## GitHub Repository Structure

```
tdm-insight-android/
├── README.md
├── CHANGELOG.md
├── .gitignore
├── app/                     Android application source
├── docs/
│   ├── Case_Study_Analysis.md
│   ├── Formulas.md
│   ├── Validation_Rules.md
│   ├── Sample_Cases.md
│   ├── Testing.md
│   ├── References.md
│   ├── Team_Contribution.md
│   ├── wireframe/
│   └── diagrams/
├── screenshots/
├── apk/
├── presentation/
└── ai/                      AI usage log
```

## Acknowledgements

- Course guidance: Ts Mohd Zulkifli Mohd Zaki, School of Computing & Informatics, Albukhary International University
- Clinical workflow reference: [myTDM Calculator](https://www.mytdmcalculator.com/)

## References

_References to be added per APA 7th edition as clinical formulas and sources are finalized in `docs/Formulas.md` and `docs/References.md`._