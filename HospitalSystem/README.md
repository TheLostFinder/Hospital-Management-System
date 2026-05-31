# MediCore HMS — Healthcare & Service Management System
## CS212 Object Oriented Programming — PBL Project

---

## Project Structure

```
HospitalSystem/
└── src/
    ├── Main.java                          ← Entry point
    ├── module-info.java
    ├── model/
    │   ├── users/
    │   │   ├── User.java                  ← Abstract base class
    │   │   ├── Patient.java               ← extends User
    │   │   ├── Doctor.java                ← extends User
    │   │   └── AdminStaff.java            ← extends User
    │   ├── services/
    │   │   ├── Serviceable.java           ← Interface
    │   │   ├── MedicalService.java        ← Abstract class implements Serviceable
    │   │   ├── ConsultationService.java   ← Concrete service
    │   │   ├── DiagnosticService.java     ← Concrete service
    │   │   └── TreatmentService.java      ← Concrete service
    │   ├── appointments/
    │   │   └── Appointment.java
    │   └── billing/
    │       └── Bill.java
    ├── controller/
    │   ├── LoginController.java
    │   ├── AdminDashboardController.java
    │   ├── DoctorDashboardController.java
    │   └── PatientDashboardController.java
    ├── util/
    │   └── HospitalSystem.java            ← Singleton data store
    └── view/
        ├── fxml/
        │   ├── Login.fxml
        │   ├── AdminDashboard.fxml
        │   ├── DoctorDashboard.fxml
        │   └── PatientDashboard.fxml
        └── css/
            └── theme.css
```

---

## OOP Concepts Demonstrated

| Concept              | Where Used                                              |
|----------------------|---------------------------------------------------------|
| **Abstraction**      | `User` (abstract), `MedicalService` (abstract)         |
| **Encapsulation**    | All classes — private fields + getters/setters          |
| **Inheritance**      | Patient, Doctor, AdminStaff → extend User               |
|                      | ConsultationService, DiagnosticService → extend MedicalService |
| **Polymorphism**     | `executeService()` — different behavior per service type |
|                      | `getDisplayInfo()` — different per user type            |
| **Interfaces**       | `Serviceable` — implemented by MedicalService           |
| **Access Modifiers** | private fields, protected helpers, public APIs          |
| **Packages**         | model.users, model.services, model.appointments, etc.   |
| **Collections**      | Lists, Maps used throughout HospitalSystem              |
| **Singleton**        | HospitalSystem — one instance across app                |

---

## Demo Credentials

| Role    | Email                    | Password  |
|---------|--------------------------|-----------|
| Admin   | admin@hospital.com       | admin123  |
| Doctor  | ahmed@hospital.com       | pass123   |
| Doctor  | sara@hospital.com        | pass123   |
| Patient | ali@email.com            | pat123    |
| Patient | fatima@email.com         | pat123    |

---

## Setup Instructions

### Requirements
- Java 17 or higher
- JavaFX SDK 17+ (download from https://openjfx.io)
- IntelliJ IDEA (recommended) or Eclipse

### IntelliJ IDEA Setup

1. **Open project** — File → Open → select `HospitalSystem` folder
2. **Add JavaFX library:**
   - File → Project Structure → Libraries → + → Java
   - Navigate to your JavaFX SDK `lib` folder
   - Apply
3. **VM Options** — Run → Edit Configurations → VM Options:
   ```
   --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml
   ```
4. **Set Main class** to `Main`
5. **Run** — Shift+F10

### Command Line (javac + java)

```bash
# Compile (replace /path/to/javafx-sdk with your path)
javac --module-path /path/to/javafx-sdk/lib \
      --add-modules javafx.controls,javafx.fxml \
      -d out \
      $(find src -name "*.java")

# Copy resources
cp -r src/view out/

# Run
java --module-path /path/to/javafx-sdk/lib \
     --add-modules javafx.controls,javafx.fxml \
     -cp out Main
```

---

## Features by Role

### Admin Portal
- View dashboard with live stats (patients, doctors, appointments, revenue)
- Register new patients and doctors via dialog forms
- Schedule and manage appointments
- Generate detailed billing receipts
- View full system reports

### Doctor Portal
- View personal dashboard with assigned patient count
- Browse today's appointment schedule
- Write and save diagnoses (auto-updates patient medical history)
- Toggle availability status
- View completed prescriptions and notes

### Patient Portal
- View personal profile (age, blood group, phone)
- Book appointments with available doctors (auto-creates consultation service)
- View appointment history with status tracking
- View full medical history
- View itemized bills and total charges

---

## Extensibility

To add a new service type:
1. Create a class extending `MedicalService`
2. Implement `executeService()` with custom logic
3. Add cost logic in `calculateFinalCost()`
4. Use it anywhere `MedicalService` is accepted

To add a new user role:
1. Create a class extending `User`
2. Implement `getDisplayInfo()` and `getMenuItems()`
3. Add a new FXML dashboard + Controller
4. Update `LoginController` routing

---

*Military College of Signals — CS212 OOP PBL — BESE-31*
