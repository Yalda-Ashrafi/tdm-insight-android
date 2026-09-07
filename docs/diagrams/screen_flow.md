# Screen Flow Diagram

**Owner:** Benat

This diagram shows the navigation flow between all 9 screens of TDM
Insight, including the optional History screen and the branch back to
patient entry for a new case.

```mermaid
flowchart TD
    Start([App Launch]) --> Welcome[1. Welcome / Start]
    Welcome --> PatientCase[2. Patient Case Entry]
    Welcome --> History[9. History - optional]
    PatientCase --> Workflow[3. Workflow Selection]
    Workflow --> InputForm[4. Dynamic Input Form]
    InputForm --> Review[5. Review Inputs]
    Review -->|Edit| InputForm
    Review -->|Confirm| Results[6. Results]
    Results --> Explanation[7. Explanation]
    Results --> Export{{Export / Share Summary}}
    Explanation --> About[8. About / Disclaimer]
    Results -->|Save case| History
    History -->|Select past case| Results
    Welcome --> About

    style Start fill:#4CAF50,color:#fff
    style Results fill:#2962FF,color:#fff
    style History fill:#FFA726,color:#fff
```

## Notes

- Screens 1–8 are mandatory per the project's screen plan; Screen 9
  (History) is optional.
- The **Review Inputs** screen allows looping back to the **Dynamic
  Input Form** if the user needs to correct something before
  calculation.
- From **Results**, the user can either view the **Explanation**
  (reasoning/formulas behind the recommendation), **export/share** the
  summary, or **save the case** to History for later review.
- **About / Disclaimer** is reachable from the Welcome screen at any
  time, independent of an active case.