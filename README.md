# TDM Insight: Vancomycin TDM Calculator ⚗️

![Branding](tdm_logo.png)

**TDM Insight** is a professional, native Android clinical tool designed to assist healthcare professionals and students in performing **Vancomycin Therapeutic Drug Monitoring (TDM)**. By leveraging modern pharmacokinetic models, the app ensures therapeutic efficacy while minimizing the risk of nephrotoxicity.

Developed for the **Mobile Application Development (CDE2313)** course at Albukhary International University.

---

## 📽️ Demo & Screenshots

> [!TIP]
> **Video Demonstration**: [View the full App Demo on Google Drive](PASTE_YOUR_DRIVE_LINK_HERE)

| Welcome Screen | Clinical Input | Interactive Graph |
| :---: | :---: | :---: |
| ![Welcome](app/src/main/res/drawable/vancomycin_vial.jpg) | *Professional Form* | *Dynamic Simulation* |

---

## 🚀 Key Clinical Features (Latest Updates)

- **Dynamic Clinical Workflows**: Forms automatically adapt based on your selection (**Pre**, **Post**, or **Pre + Post**). Only required fields are shown to reduce cognitive load.
- **Interactive PK Simulation**: A live Canvas-based graph that redraws in real-time as you adjust Dose, Interval, or Infusion time.
- **Therapeutic Target Windows**: The graph features **Green (Trough)** and **Yellow (Peak)** bands to visually guide the clinician.
- **Professional Math Explanation**: Every result includes a step-by-step mathematical breakdown showing the exact formula, variable substitution, and intermediate values.
- **Smart Validation System**:
    - **Red Asterisk (*)** indicates mandatory medical data.
    - **Clinical Range Guidance**: Placeholders guide users toward realistic values (e.g., Age 18-120).
    - **Math Safety**: Prevents division-by-zero or logarithmic errors in renal function calculations.
- **Clinical Reporting**: Generate professional A4 clinical PDF reports and share them instantly via WhatsApp or Email.
- **CameraX Integration**: Capture and attach fictional lab reports directly to the patient case.

---

## 📂 Project Architecture

The app follows a clean **MVVM (Model-View-ViewModel)** architecture, separating complex clinical math from the user interface.

### **Page Structure (9 Screens)**
1.  **Splash Screen**: Premium 3.5s animated entrance with clinical spinner.
2.  **Welcome Screen**: Branding and high-level clinical overview.
3.  **Workflow Selection**: Choice between Pre, Post, and Pre+Post methods.
4.  **Patient Case Entry**: Collection of Age, Weight, Height, and SCr.
5.  **Camera Preview**: CameraX interface for lab report capture.
6.  **Therapy Input Form**: Dynamic dosing and concentration inputs.
7.  **Review Inputs**: Final summary and validation check.
8.  **Calculation Results**: Intermediate PK values, AUC24, and Interactive Graph.
9.  **Math Explanation**: Step-by-step clinical math breakdown.

### **Folder Hierarchy**
```text
tdm-insight-android/
├── app/
│   └── src/main/java/my/edu/aiu/app/tdm_insight_android/
│       ├── engine/         <-- (Autoritative PK Formulas & Engine)
│       ├── features/       <-- (Graphing, PDF Export, CameraX)
│       ├── model/          <-- (Clinical Data Models)
│       ├── ui/             <-- (Jetpack Compose Screen & Components)
│       └── viewmodel/      <-- (Shared Clinical State Persistence)
├── docs/                   <-- (Clinical Reference Materials)
└── README.md               <-- (This Documentation)
```

---

## 👨‍💻 Team AIU Task Division

Our team divided the work to ensure every clinical and technical requirement was met with high quality.

| Developer | Key Responsibilities & Work Done |
| :--- | :--- |
| **Yalda Ashrafi** | **Project Lead & UI Architect**: Developed the core calculation engine, unified the Pink-to-Aqua UI theme, implemented the validation logic, and built the Splash and Workflow navigation. |
| **Benat Siraj** | **Visualization & Logic**: Built the custom Canvas graph engine, the PDF Export system, CameraX integration, and the Patient Case management workflow. |
| **Mohammad Elyas Yameen** | **Components & UX**: Developed the reusable Form components, the dynamic Simulation logic, the interactive Results interface, and the step-by-step Math Explanation engine. |

---

## 🛠️ Technical Stack
- **Language**: Kotlin
- **UI**: Jetpack Compose (Material 3)
- **APIs**: CameraX, PdfDocument API, Coil 3
- **Tools**: Android Studio, Git/GitHub, Robocopy (for clean deployments)

---

## ⚖️ Clinical Disclaimer
*This is an academic prototype for educational purposes only. It is not intended for real-world clinical decision-making. All demonstration data is fictional.*

**Instructor**: Ts. Mohd Zulkifli Mohd Zaki  
**Albukhary International University (AIU)**
