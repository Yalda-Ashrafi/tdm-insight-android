# Architecture Diagram

**Owner:** Benat

This diagram shows how TDM Insight's main packages relate to each other:
the UI layer (Compose screens) sends validated patient data to the
engine layer, which returns a result that flows back up to be displayed,
saved, and optionally exported.

```mermaid
graph TD
    subgraph UI["UI Layer (Jetpack Compose)"]
        A[WelcomeScreen] --> B[WorkflowSelectionScreen]
        B --> C[PatientCaseScreen]
        C --> D[InputFormScreen]
        D --> E[ReviewInputsScreen]
        E --> F[ResultsScreen]
        F --> G[ExplanationScreen]
        F --> H[HistoryScreen - optional]
    end

    subgraph Validation["Validation Layer"]
        V1[Form-level validation<br/>in PatientCaseScreen.kt]
        V2[InputValidator.kt<br/>engine-level validation]
    end

    subgraph Engine["Engine Layer"]
        EN1[VancomycinEngine.kt]
        EN2[PharmacokineticFormulas.kt]
        EN3[CalculationExplainer.kt]
    end

    subgraph Model["Model Layer"]
        M1[PatientInput]
        M2[TdmResult]
        M3[SavedCase]
    end

    subgraph Features["Feature Modules (Benat)"]
        FT1[ConcentrationGraph.kt]
        FT2[HistoryFeature.kt]
        FT3[SummaryExporter.kt]
    end

    C --> V1
    V1 --> V2
    V2 --> M1
    M1 --> EN1
    EN1 --> EN2
    EN1 --> EN3
    EN1 --> M2
    M2 --> F
    F --> FT1
    F --> FT3
    M2 --> M3
    M3 --> FT2
    FT2 --> H
```

## Notes

- The **UI Layer** follows the 9-screen flow defined in the project's
  screen plan; each screen is a separate Composable function.
- **Validation** happens twice: a fast, UI-facing pass on the screen
  itself (immediate feedback), and a deeper pass in `InputValidator.kt`
  before data reaches the engine (safety net).
- The **Engine Layer** is intentionally isolated from the UI — it takes
  a `PatientInput` and returns a `TdmResult`, with no knowledge of
  Compose or navigation.
- **Feature modules** (`ConcentrationGraph`, `HistoryFeature`,
  `SummaryExporter`) are display/utility components that consume the
  engine's output but do not perform calculations themselves.