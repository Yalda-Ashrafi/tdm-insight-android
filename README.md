
[README.md](https://github.com/user-attachments/files/32057972/README.md)
# TDM Insight

Native Android Therapeutic Drug Monitoring (Vancomycin) calculator, built for CDE2313 Mobile Application Development.

## Course information

| | |
|---|---|
| Course | CDE2313 – Mobile Application Development |
| Programme | Bachelor in Data Science |
| Semester | 3, 2025/2026 |
| Lecturer | Ts Mohd Zulkifli Mohd Zaki |
| Assessment | Assessment 2 – Group Project (25%) |

## Group members

| Name | Student ID | Role |
|---|---|---|
| Mohammad Elyas Yameen | AIU24102041 | UI/UX Designer & Demo Lead |
| [Teammate name] | [Student ID] | [Role] |
| [Teammate name] | [Student ID] | [Role] |

## Case study

**Problem overview:** Hospital pharmacy staff manually perform Vancomycin Therapeutic Drug Monitoring (TDM) calculations, which need patient dosing history, drug concentration samples, and timing data pulled together correctly before a pharmacokinetic result can be trusted.

**Summary of implemented solution:** TDM Insight guides the user through three Vancomycin workflows (Pre-dose, Post-dose, Pre + Post), shows only the input fields relevant to the selected workflow, validates entries (including cross-field timing checks), runs the calculation through a dedicated engine separate from the UI, and shows a step-by-step explanation of how each result was derived.

## Key implemented features

- Workflow selector (Pre / Post / Pre + Post) with dynamic input forms
- Field-level and cross-field validation (required fields, numeric ranges, timing logic, divide-by-zero/log-domain guards)
- Standalone TDM calculation engine (UI → input state → validation → engine → result model → results UI)
- Explainable results screen with an input → intermediate → parameter → final-result breakdown
- [Add any optional enhancement your team implemented, e.g. calculation history, concentration-time graph]

## Technology stack and architecture

- **Language:** Kotlin
- **UI:** Jetpack Compose / XML, Material 3
- **Architecture:** [e.g. MVVM — describe your actual layers: UI, ViewModel, calculation engine, result model]
- **Build tool:** Gradle

## Installation guide

1. Clone this repository: `git clone [repo URL]`
2. Open the project in Android Studio ([version]).
3. Let Gradle sync finish.
4. Run on an emulator or device (minSdk [X]).

## How to build the project

```
./gradlew assembleRelease
```

The signed/release APK will be output to `app/build/outputs/apk/release/`.

## APK download

See [`/apk/app-release.apk`](./apk/app-release.apk).

## Screenshots

| Workflow selector | Input form (validation) | Explainable result |
|---|---|---|
| ![selector](./screenshots/selector.png) | ![form](./screenshots/form_validation.png) | ![result](./screenshots/result_explanation.png) |

*(Replace with your actual captured screenshots — aim for one per workflow, one showing a validation error, and one showing the expanded explanation.)*

## GitHub repository structure

```
MobileAppProject/
├── README.md
├── LICENCE
├── .gitignore
├── app/
├── gradle/
├── screenshots/
├── docs/
│   ├── Case_Study_Analysis.md
│   ├── wireframe/
│   └── diagrams/
├── apk/
│   └── app-release.apk
├── presentation/
│   ├── Presentation.pptx
│   └── Presentation.pdf
├── ai/
│   └── AI_Usage_Log.pdf
└── assets/
```

## Acknowledgements

- Case study and assessment instructions: Ts Mohd Zulkifli Mohd Zaki, Madam Siti Shafrah Shahawai, Albukhary International University.
- Reference calculators consulted for domain understanding: myTDM Calculator, Malaysian Pharmacy Information System (PhIS) TDM Calculator documentation.

## References

- [Add APA 7th edition references for any clinical/pharmacokinetic sources used for the Vancomycin equations, per your lecturer-approved specification.]

## Important disclaimer

TDM Insight is an academic software prototype for educational and software development purposes only. It is not a clinically validated prescribing, diagnostic, or treatment-decision system. All demonstration cases are fictional.
