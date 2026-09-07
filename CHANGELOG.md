# Changelog

All notable changes to this project are documented here. Dates reflect when work was completed, not merge dates.

## [Unreleased]

### Added — Benat (features/enhancements)
- `PatientCaseScreen.kt` — patient case entry form (Screen 2) with field-level input validation
- `docs/Validation_Rules.md` — documentation of form validation rules
- `docs/Sample_Cases.md` — worked example patient cases for manual and future automated testing
- `ConcentrationGraph.kt` — native Compose Canvas chart of concentration vs. time, with therapeutic range shading
- `HistoryFeature.kt` — optional case history screen (Screen 9) with empty-state handling
- `SummaryExporter.kt` — plain-text case summary export via Android share sheet
- `res/xml/file_paths.xml` — FileProvider path configuration supporting the export feature

### Pending
- FileProvider `<provider>` entry in `AndroidManifest.xml` (owned by Yalda) — required for `SummaryExporter` to function at runtime
- Field name reconciliation between `PatientCaseScreen.kt` and `model/PatientInput.kt`
- Confirmation of serum creatinine units (mg/dL assumed) against the engine's expected input
- Mapping between `HistoryFeature.kt`'s `HistoryEntry` and `model/SavedCase.kt` once finalized
- `README.md` sections pending team input: Student IDs, screenshots, references
- `docs/diagrams/` architecture diagrams
- Release APK build
- `ai/AI_Usage_Log.pdf`

---

_This changelog will be updated as further features are added and branches are merged into `main`._