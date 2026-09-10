# TDM Insight: Vancomycin TDM Calculator ⚗️

**TDM Insight** is a professional, native Android application designed to assist clinicians and medical students in performing **Vancomycin Therapeutic Drug Monitoring (TDM)**. It leverages established pharmacokinetic models to calculate critical parameters, helping ensure therapeutic efficacy while minimizing toxicity risk.

Developed as part of the **Mobile Application Development course (CDE2313)** at Albukhary International University (AIU).

---

## 🚀 Key Features

- **Clinical Workflows**: Supports multiple TDM pathways:
  - **Pre-Dose (Trough-only)**: Elimination estimated from renal function.
  - **Pre + Post (Peak & Trough)**: Measured Ke using the Sawchuk-Zaske method for maximum precision.
- **Precision PK Engine**: Automated calculation of Elimination Rate ($Ke$), Half-life ($t_{1/2}$), Volume of Distribution ($V_d$), Clearance ($CL$), and 24-hour AUC.
- **Interactive Dose Simulation**: Live "what-if" simulator on the results screen to predict Peak and Trough levels for new dosing regimens.
- **Visual Analytics**: Real-time concentration-time curve plotting using a custom Canvas-based graphing system.
- **Clinical Reporting**: Generate professional A4 clinical PDF reports and share them instantly via WhatsApp, Email, or Google Drive.
- **Camera Integration**: Built-in lab report capture using **CameraX** to attach clinical evidence to cases.
- **Persistence**: Full data persistence across the session using a shared **ViewModel** architecture.

---

## 🎨 Professional UI/UX

- **Hospital-Ready Design**: A high-contrast, clinical color palette using **Deep Teal** and **Emerald Green**.
- **Modern Material 3**: Built entirely with **Jetpack Compose**, featuring smooth animations, rounded corners, and a responsive layout.
- **Animated Splash Screen**: A premium 3.5-second animated entrance with a clinical spinner loader.
- **Unified Aesthetic**: Consistent **Pastel Pink to Aqua** gradient background across all screens.

---

## 🛠️ Technical Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Navigation**: Compose Navigation with Modal Navigation Drawer
- **Architecture**: MVVM (Model-View-ViewModel)
- **APIs Used**:
  - **CameraX**: For clinical document capture.
  - **PdfDocument API**: For native clinical report generation.
  - **Coil**: For efficient image loading and processing.
  - **Coroutines**: For smooth asynchronous background tasks.

---

## 👨‍💻 Team AIU (The Developers)

| Name | Role & Contributions |
| :--- | :--- |
| **Yalda Ashrafi** | Project Lead, Calculation Engine, UI Architecture, Validation Logic |
| **Benat Siraj** | Graph Visualization, PDF Export Engine, Camera Integration, History Feature |
| **Mohammad Elyas Yameen** | UI Components, Simulation Logic, Results & Explanation Workflows |

**Instructor**: Ts. Mohd Zulkifli Mohd Zaki (Lead Instructor)  
**Course**: Mobile Application Development (CDE2313)

---

## ⚠️ Disclaimer

*This application is an academic prototype developed for educational purposes only. It is not intended for real patient care decisions. Always consult official clinical guidelines and local hospital protocols.*

---

## 📂 Repository Structure

- `/app`: Full Android source code.
- `/docs`: Project documentation including formulas and team contributions.
- `tdm_logo.png`: Official application branding asset.
