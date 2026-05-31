package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.appointments.Appointment;
import model.billing.Bill;
import model.users.Doctor;
import model.users.Patient;
import util.HospitalSystem;

import java.net.URL;
import java.util.ResourceBundle;

/*
 DoctorDashboardController doctor portal ke tamam features handle
 karta hai. Doctor apna schedule dekh sakta hai, patients ki
 history check kar sakta hai, diagnosis likh sakta hai aur apni
 availability toggle kar sakta hai.
 */
public class DoctorDashboardController implements Initializable {

    @FXML private Label  welcomeLabel, statMyPatients, statMyAppts, statAvailBadge;
    @FXML private Button toggleAvailBtn;

    @FXML private ComboBox<String>       scheduleFilter, completeApptCombo;
    @FXML private TableView<Appointment> scheduleTable;
    @FXML private TableColumn<Appointment,String> scColId, scColPatient, scColDate, scColTime, scColReason, scColPriority, scColStatus;
    @FXML private TableColumn<Appointment,Void>   scColComplete;
    @FXML private TextArea  diagnosisArea;
    @FXML private TextField prescriptionField;
    @FXML private Label     scheduleMsg;

    @FXML private TableView<Patient> myPatientTable;
    @FXML private TableColumn<Patient,String> mpColId, mpColName, mpColAge, mpColBlood, mpColAppts, mpColEmerg;
    @FXML private Label    detailName, detailInfo, detailPhone, patientDetailMsg;
    @FXML private TextArea historyArea;
    @FXML private TextField quickNote;

    @FXML private TableView<Appointment> diagnosisTable;
    @FXML private TableColumn<Appointment,String> dgColDate, dgColPatient, dgColAppt, dgColNotes, dgColStatus;

    @FXML private Label     profileName, profileSpec, profileQual, profileEmail, profilePhone, profileFee, profileMsg;
    @FXML private TextField newFeeField;

    private Doctor currentDoctor;
    private final HospitalSystem hospital = HospitalSystem.getInstance();

    private final ObservableList<Appointment> scheduleList  = FXCollections.observableArrayList();
    private final ObservableList<Patient>     patientList   = FXCollections.observableArrayList();
    private final ObservableList<Appointment> diagnosisList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupScheduleTable();
        setupPatientTable();
        setupDiagnosisTable();
    }

    public void initData(Doctor doctor) {
        this.currentDoctor = doctor;
        welcomeLabel.setText("Dr. " + doctor.getName());
        loadProfileTab();
        refreshAllData();
    }

    private void refreshAllData() {
        scheduleList.setAll(hospital.getAppointmentsForDoctor(currentDoctor.getUserId()));

        java.util.Set<String> seen = new java.util.HashSet<>();
        java.util.List<Patient> unique = new java.util.ArrayList<>();
        hospital.getAppointmentsForDoctor(currentDoctor.getUserId()).forEach(a -> {
            if (seen.add(a.getPatient().getUserId())) unique.add(a.getPatient());
        });
        patientList.setAll(unique);

        diagnosisList.setAll(
                hospital.getAppointmentsForDoctor(currentDoctor.getUserId()).stream()
                        .filter(a -> a.getStatus().equals("COMPLETED") || !a.getNotes().isEmpty())
                        .collect(java.util.stream.Collectors.toList()));

        updateTopStats();
        loadApptCombo();
    }

    private void updateTopStats() {
        statMyPatients.setText(String.valueOf(patientList.size()));
        statMyAppts.setText(String.valueOf(scheduleList.size()));
        if (currentDoctor.isAvailable()) {
            statAvailBadge.setText("Available");
            statAvailBadge.setStyle("-fx-font-size:15px;-fx-font-weight:bold;-fx-text-fill:#4ADE80;");
            toggleAvailBtn.setText("Available");
            toggleAvailBtn.getStyleClass().setAll("btn-avail-on");
        } else {
            statAvailBadge.setText("Unavailable");
            statAvailBadge.setStyle("-fx-font-size:15px;-fx-font-weight:bold;-fx-text-fill:#F87171;");
            toggleAvailBtn.setText("Unavailable");
            toggleAvailBtn.getStyleClass().setAll("btn-avail-off");
        }
    }

    private void loadApptCombo() {
        completeApptCombo.getItems().clear();
        hospital.getAppointmentsForDoctor(currentDoctor.getUserId()).stream()
                .filter(a -> a.getStatus().equals("SCHEDULED") || a.getStatus().equals("RESCHEDULED"))
                .forEach(a -> completeApptCombo.getItems().add(
                        a.getAppointmentId() + " - " + a.getPatient().getName() + " (" + a.getDate() + ")"));
    }

    private void setupScheduleTable() {
        scColId.setCellValueFactory(d       -> new SimpleStringProperty(d.getValue().getAppointmentId()));
        scColPatient.setCellValueFactory(d  -> new SimpleStringProperty(d.getValue().getPatient().getName()));
        scColDate.setCellValueFactory(d     -> new SimpleStringProperty(d.getValue().getDate()));
        scColTime.setCellValueFactory(d     -> new SimpleStringProperty(d.getValue().getTime()));
        scColReason.setCellValueFactory(d   -> new SimpleStringProperty(d.getValue().getReason()));
        scColPriority.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPriority().name()));
        scColStatus.setCellValueFactory(d   -> new SimpleStringProperty(d.getValue().getStatus()));

        scColComplete.setCellFactory(col -> new TableCell<>() {
            final Button btn = new Button("Complete");
            {
                btn.getStyleClass().add("btn-table-sm");
                btn.setOnAction(e -> {
                    Appointment a = getTableView().getItems().get(getIndex());
                    a.complete();
                    scheduleTable.refresh();
                    diagnosisList.setAll(
                            hospital.getAppointmentsForDoctor(currentDoctor.getUserId()).stream()
                                    .filter(ap -> ap.getStatus().equals("COMPLETED") || !ap.getNotes().isEmpty())
                                    .collect(java.util.stream.Collectors.toList()));
                    loadApptCombo();
                    updateTopStats();
                    setMsg(scheduleMsg, "Appointment completed for " + a.getPatient().getName(), true);
                });
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : btn);
            }
        });

        scheduleTable.setItems(scheduleList);

        scheduleFilter.setItems(FXCollections.observableArrayList("ALL", "SCHEDULED", "COMPLETED", "CANCELLED"));
        scheduleFilter.setValue("ALL");
        scheduleFilter.setOnAction(e -> {
            String f = scheduleFilter.getValue();
            if ("ALL".equals(f)) {
                scheduleTable.setItems(scheduleList);
            } else {
                ObservableList<Appointment> filtered = FXCollections.observableArrayList();
                scheduleList.stream().filter(a -> a.getStatus().equals(f)).forEach(filtered::add);
                scheduleTable.setItems(filtered);
            }
        });
    }

    private void setupPatientTable() {
        mpColId.setCellValueFactory(d    -> new SimpleStringProperty(d.getValue().getUserId()));
        mpColName.setCellValueFactory(d  -> new SimpleStringProperty(d.getValue().getName()));
        mpColAge.setCellValueFactory(d   -> new SimpleStringProperty(String.valueOf(d.getValue().getAge())));
        mpColBlood.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getBloodGroup()));
        mpColAppts.setCellValueFactory(d -> new SimpleStringProperty(
                String.valueOf(hospital.getAppointmentsForPatient(d.getValue().getUserId()).size())));
        mpColEmerg.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().isEmergency() ? "YES" : "No"));
        myPatientTable.setItems(patientList);
    }

    private void setupDiagnosisTable() {
        dgColDate.setCellValueFactory(d    -> new SimpleStringProperty(d.getValue().getDate()));
        dgColPatient.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPatient().getName()));
        dgColAppt.setCellValueFactory(d    -> new SimpleStringProperty(d.getValue().getAppointmentId()));
        dgColNotes.setCellValueFactory(d   -> new SimpleStringProperty(
                d.getValue().getNotes().isEmpty() ? d.getValue().getReason() : d.getValue().getNotes()));
        dgColStatus.setCellValueFactory(d  -> new SimpleStringProperty(d.getValue().getStatus()));
        diagnosisTable.setItems(diagnosisList);
    }


    @FXML private void completeAppointment() {
        String selected = completeApptCombo.getValue();
        if (selected == null || diagnosisArea.getText().isBlank()) {
            setMsg(scheduleMsg, "Select an appointment and enter diagnosis.", false);
            return;
        }
        String apptId = selected.split(" - ")[0];
        hospital.getAllAppointments().stream()
                .filter(a -> a.getAppointmentId().equals(apptId))
                .findFirst().ifPresent(a -> {
                /*
                 Bill check karta hai. Agar patient ka bill PAID nahi hai
                 to appointment complete nahi kar sakte.
                 */
                    Bill patientBill = hospital.getAllBills().stream()
                            .filter(b -> b.getPatientId().equals(a.getPatient().getUserId())
                                    && b.getStatus().equals("PENDING"))
                            .findFirst().orElse(null);

                    if (patientBill != null) {
                        setMsg(scheduleMsg, "Patient bill is still pending. Ask patient to pay first.", false);
                        return;
                    }

                    a.complete();
                    a.setNotes(diagnosisArea.getText().trim());
                    currentDoctor.writeDiagnosis(a.getPatient(), diagnosisArea.getText().trim());
                    diagnosisArea.clear();
                    completeApptCombo.setValue(null);
                    refreshAllData();
                    setMsg(scheduleMsg, "Appointment completed. Diagnosis saved for " + a.getPatient().getName(), true);
                });
    }

    @FXML private void onPatientSelected() {
        Patient p = myPatientTable.getSelectionModel().getSelectedItem();
        if (p == null) return;
        detailName.setText(p.getName());
        detailInfo.setText("Age: " + p.getAge() + "  |  Blood: " + p.getBloodGroup());
        detailPhone.setText(p.getPhone() + "  |  " + p.getAddress());
        historyArea.setText(p.getMedicalHistory().isEmpty()
                ? "No medical history recorded yet." : p.getMedicalHistory());
    }

    @FXML private void addQuickNote() {
        Patient p = myPatientTable.getSelectionModel().getSelectedItem();
        if (p == null) { setMsg(patientDetailMsg, "Select a patient first.", false); return; }
        if (quickNote.getText().isBlank()) { setMsg(patientDetailMsg, "Enter a note first.", false); return; }
        currentDoctor.writeDiagnosis(p, quickNote.getText().trim());
        historyArea.setText(p.getMedicalHistory());
        quickNote.clear();
        setMsg(patientDetailMsg, "Note added to " + p.getName() + " history.", true);
    }

    @FXML private void toggleAvailability() {
        currentDoctor.setAvailable(!currentDoctor.isAvailable());
        updateTopStats();
    }

    private void loadProfileTab() {
        profileName.setText("Dr. " + currentDoctor.getName());
        profileSpec.setText(currentDoctor.getSpecialization());
        profileQual.setText(currentDoctor.getQualification());
        profileEmail.setText(currentDoctor.getEmail());
        profilePhone.setText(currentDoctor.getPhone());
        profileFee.setText("Rs " + currentDoctor.getConsultationFee());
    }

    @FXML private void updateFee() {
        try {
            double fee = Double.parseDouble(newFeeField.getText().trim());
            currentDoctor.setConsultationFee(fee);
            profileFee.setText("Rs " + fee);
            newFeeField.clear();
            setMsg(profileMsg, "Consultation fee updated to Rs " + fee, true);
        } catch (NumberFormatException e) {
            setMsg(profileMsg, "Enter a valid number.", false);
        }
    }

    private void setMsg(Label lbl, String text, boolean success) {
        lbl.setStyle(success ? "-fx-text-fill: #16A34A;" : "-fx-text-fill: #DC2626;");
        lbl.setText(text);
    }

    @FXML private void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/fxml/Login.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.centerOnScreen();
        } catch (Exception e) { e.printStackTrace(); }
    }
}