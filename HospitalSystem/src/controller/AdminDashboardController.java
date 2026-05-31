package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.appointments.Appointment;
import model.billing.Bill;
import model.services.*;
import model.users.AdminStaff;
import model.users.Doctor;
import model.users.Patient;
import util.HospitalSystem;

import java.net.URL;
import java.util.ResourceBundle;

/*
 AdminDashboardController admin portal ke tamam tabs ko manage
 karta hai. Patients, doctors, appointments, services aur billing
 sab yahan handle hote hain. ObservableList use ki gayi hai taake
 table mein data automatically update ho jaye.
 */
public class AdminDashboardController implements Initializable {

    @FXML private Label welcomeLabel, statPatients, statDoctors, statAppointments, statRevenue;

    @FXML private TextField patientSearch;
    @FXML private TableView<Patient> patientTable;
    @FXML private TableColumn<Patient,String> ptColId, ptColName, ptColAge, ptColBlood, ptColPhone, ptColEmerg;
    @FXML private TableColumn<Patient,Void>   ptColActions;
    @FXML private TextField     ptName, ptEmail, ptPhone, ptAge, ptAddress;
    @FXML private PasswordField ptPass;
    @FXML private ComboBox<String> ptBlood;
    @FXML private CheckBox ptEmergency;
    @FXML private Label patientFormMsg;

    @FXML private TextField doctorSearch;
    @FXML private TableView<Doctor> doctorTable;
    @FXML private TableColumn<Doctor,String> drColId, drColName, drColSpec, drColQual, drColFee, drColAvail;
    @FXML private TextField     drName, drEmail, drPhone, drQual, drFee;
    @FXML private PasswordField drPass;
    @FXML private ComboBox<String> drSpec;
    @FXML private Label doctorFormMsg;

    @FXML private TableView<Appointment> apptTable;
    @FXML private TableColumn<Appointment,String> apColId, apColPatient, apColDoctor, apColDate, apColTime, apColPriority, apColStatus;
    @FXML private TableColumn<Appointment,Void>   apColAction;
    @FXML private ComboBox<String> apPatient, apDoctor, apPriority;
    @FXML private TextField apDate, apTime, apReason;
    @FXML private Label apptFormMsg;

    @FXML private TableView<MedicalService> serviceTable;
    @FXML private TableColumn<MedicalService,String> svColId, svColPatient, svColName, svColCategory, svColCost, svColStatus;
    @FXML private ComboBox<String> svPatient, svType, svDoctor, svSubType;
    @FXML private CheckBox svUrgent;
    @FXML private Label serviceFormMsg;

    @FXML private TableView<Bill>          billTable;
    @FXML private TableColumn<Bill,String> blColId, blColPatient, blColGenerated, blColSubtotal, blColTax, blColDiscount, blColTotal, blColStatus;
    @FXML private TableColumn<Bill,Void>   blColAction;
    @FXML private TableView<Bill.BillItem>          billItemTable;
    @FXML private TableColumn<Bill.BillItem,String> biColItem, biColCategory, biColAmount;
    @FXML private ComboBox<String> billPatientCombo;
    @FXML private TextField        billDiscount;
    @FXML private Label            billMsg;

    @FXML private Label rptPatients, rptDoctors, rptAppointments, rptBills, rptRevenue;
    @FXML private TableView<Patient> reportTable;
    @FXML private TableColumn<Patient,String> rpColName, rpColAge, rpColBlood, rpColAppts, rpColSvcs, rpColBilled, rpColEmerg;

    private final HospitalSystem hospital = HospitalSystem.getInstance();
    private AdminStaff currentAdmin;

    private final ObservableList<Patient>       patientList     = FXCollections.observableArrayList();
    private final ObservableList<Doctor>        doctorList      = FXCollections.observableArrayList();
    private final ObservableList<Appointment>   appointmentList = FXCollections.observableArrayList();
    private final ObservableList<MedicalService> serviceList    = FXCollections.observableArrayList();
    private final ObservableList<Bill>          billList        = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupPatientTable();
        setupDoctorTable();
        setupApptTable();
        setupServiceTable();
        setupBillingTable();
        setupReportTable();
        populateCombos();
    }

    public void initData(AdminStaff admin) {
        this.currentAdmin = admin;
        welcomeLabel.setText(admin.getName());
        refreshAllData();
    }

    private void refreshAllData() {
        patientList.setAll(hospital.getAllPatients());
        doctorList.setAll(hospital.getAllDoctors());
        appointmentList.setAll(hospital.getAllAppointments());
        serviceList.setAll(hospital.getAllServices());
        billList.setAll(hospital.getAllBills());
        updateTopStats();
        populateCombos();
    }

    private void updateTopStats() {
        statPatients.setText(String.valueOf(hospital.getTotalPatients()));
        statDoctors.setText(String.valueOf(hospital.getTotalDoctors()));
        statAppointments.setText(String.valueOf(hospital.getTotalAppointments()));
        statRevenue.setText("Rs " + String.format("%.0f", hospital.getTotalRevenue()));
    }

    /*
 Tamam combo boxes ko fresh data se populate karta hai. Yeh
 method har baar call hota hai jab koi naya record add ho.
 */
    private void populateCombos() {
        ObservableList<String> pts = FXCollections.observableArrayList();
        hospital.getAllPatients().forEach(p -> pts.add(p.getUserId() + " - " + p.getName()));
        apPatient.setItems(pts);
        svPatient.setItems(pts);
        billPatientCombo.setItems(pts);

        ObservableList<String> drs = FXCollections.observableArrayList();
        hospital.getAllDoctors().forEach(d -> drs.add(d.getUserId() + " - Dr." + d.getName() + " (" + d.getSpecialization() + ")"));
        apDoctor.setItems(drs);
        svDoctor.setItems(drs);

        apPriority.setItems(FXCollections.observableArrayList("NORMAL", "URGENT", "EMERGENCY"));
        apPriority.setValue("NORMAL");

        ptBlood.setItems(FXCollections.observableArrayList("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"));

        drSpec.setItems(FXCollections.observableArrayList(
                "Cardiology", "Neurology", "Orthopedics", "Pediatrics", "General Medicine",
                "Gynecology", "Dermatology", "ENT", "Ophthalmology", "Psychiatry", "Oncology", "Urology"));

        svType.setItems(FXCollections.observableArrayList("Consultation", "Diagnostic", "Treatment"));
        svType.setOnAction(e -> updateSubType());
    }

    private void updateSubType() {
        String type = svType.getValue();
        if (type == null) return;
        switch (type) {
            case "Consultation":
                svSubType.setItems(FXCollections.observableArrayList("GENERAL", "SPECIALIST", "FOLLOW_UP", "EMERGENCY"));
                break;
            case "Diagnostic":
                svSubType.setItems(FXCollections.observableArrayList("BLOOD_TEST", "XRAY", "MRI", "CT_SCAN", "URINE", "ECG"));
                break;
            case "Treatment":
                svSubType.setItems(FXCollections.observableArrayList("LOW", "MEDIUM", "HIGH", "CRITICAL"));
                break;
        }
    }

    private void setupPatientTable() {
        ptColId.setCellValueFactory(d    -> new SimpleStringProperty(d.getValue().getUserId()));
        ptColName.setCellValueFactory(d  -> new SimpleStringProperty(d.getValue().getName()));
        ptColAge.setCellValueFactory(d   -> new SimpleStringProperty(String.valueOf(d.getValue().getAge())));
        ptColBlood.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getBloodGroup()));
        ptColPhone.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPhone()));
        ptColEmerg.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().isEmergency() ? "YES" : "No"));

        ptColActions.setCellFactory(col -> new TableCell<>() {
            final Button viewBtn  = new Button("View");
            final Button emergBtn = new Button("Toggle Emergency");
            final HBox   box      = new HBox(4, viewBtn, emergBtn);
            {
                viewBtn.getStyleClass().add("btn-table-sm");
                emergBtn.getStyleClass().add("btn-cancel-sm");
                viewBtn.setOnAction(e -> {
                    Patient p = getTableView().getItems().get(getIndex());
                    patientFormMsg.setText(p.getSummary());
                });
                emergBtn.setOnAction(e -> {
                    Patient p = getTableView().getItems().get(getIndex());
                    p.setEmergency(!p.isEmergency());
                    patientTable.refresh();
                    patientFormMsg.setText("Emergency status updated for " + p.getName());
                });
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : box);
            }
        });

        FilteredList<Patient> filtered = new FilteredList<>(patientList, p -> true);
        patientTable.setItems(filtered);
        patientSearch.textProperty().addListener((obs, o, n) ->
                filtered.setPredicate(p -> n == null || n.isBlank()
                        || p.getName().toLowerCase().contains(n.toLowerCase())
                        || p.getUserId().toLowerCase().contains(n.toLowerCase())));
    }

    private void setupDoctorTable() {
        drColId.setCellValueFactory(d    -> new SimpleStringProperty(d.getValue().getUserId()));
        drColName.setCellValueFactory(d  -> new SimpleStringProperty("Dr. " + d.getValue().getName()));
        drColSpec.setCellValueFactory(d  -> new SimpleStringProperty(d.getValue().getSpecialization()));
        drColQual.setCellValueFactory(d  -> new SimpleStringProperty(d.getValue().getQualification()));
        drColFee.setCellValueFactory(d   -> new SimpleStringProperty("Rs " + d.getValue().getConsultationFee()));
        drColAvail.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().isAvailable() ? "Available" : "Not Available"));

        FilteredList<Doctor> filtered = new FilteredList<>(doctorList, d -> true);
        doctorTable.setItems(filtered);
        doctorSearch.textProperty().addListener((obs, o, n) ->
                filtered.setPredicate(d -> n == null || n.isBlank()
                        || d.getName().toLowerCase().contains(n.toLowerCase())
                        || d.getSpecialization().toLowerCase().contains(n.toLowerCase())));
    }

    private void setupApptTable() {
        apColId.setCellValueFactory(d       -> new SimpleStringProperty(d.getValue().getAppointmentId()));
        apColPatient.setCellValueFactory(d  -> new SimpleStringProperty(d.getValue().getPatient().getName()));
        apColDoctor.setCellValueFactory(d   -> new SimpleStringProperty("Dr. " + d.getValue().getDoctor().getName()));
        apColDate.setCellValueFactory(d     -> new SimpleStringProperty(d.getValue().getDate()));
        apColTime.setCellValueFactory(d     -> new SimpleStringProperty(d.getValue().getTime()));
        apColPriority.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPriority().name()));
        apColStatus.setCellValueFactory(d   -> new SimpleStringProperty(d.getValue().getStatus()));

        apColAction.setCellFactory(col -> new TableCell<>() {
            final Button complete = new Button("Complete");
            final Button cancel   = new Button("Cancel");
            final HBox   box      = new HBox(4, complete, cancel);
            {
                complete.getStyleClass().add("btn-table-sm");
                cancel.getStyleClass().add("btn-cancel-sm");
                complete.setOnAction(e -> {
                    Appointment a = getTableView().getItems().get(getIndex());
                    a.complete();
                    apptTable.refresh();
                    updateTopStats();
                    apptFormMsg.setText("Appointment " + a.getAppointmentId() + " marked as complete.");
                });
                cancel.setOnAction(e -> {
                    Appointment a = getTableView().getItems().get(getIndex());
                    a.cancel();
                    apptTable.refresh();
                    apptFormMsg.setText("Appointment " + a.getAppointmentId() + " has been cancelled.");
                });
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : box);
            }
        });

        apptTable.setItems(appointmentList);
    }

    private void setupServiceTable() {
        svColId.setCellValueFactory(d       -> new SimpleStringProperty(d.getValue().getServiceId()));
        svColPatient.setCellValueFactory(d  -> new SimpleStringProperty(d.getValue().getPatientId()));
        svColName.setCellValueFactory(d     -> new SimpleStringProperty(d.getValue().getServiceName()));
        svColCategory.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCategory()));
        svColCost.setCellValueFactory(d     -> new SimpleStringProperty("Rs " + String.format("%.2f", d.getValue().getCost())));
        svColStatus.setCellValueFactory(d   -> new SimpleStringProperty(d.getValue().getStatus()));
        serviceTable.setItems(serviceList);
    }

    private void setupBillingTable() {
        blColId.setCellValueFactory(d        -> new SimpleStringProperty(d.getValue().getBillId()));
        blColPatient.setCellValueFactory(d   -> new SimpleStringProperty(d.getValue().getPatientName()));
        blColGenerated.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getGeneratedAt()));
        blColSubtotal.setCellValueFactory(d  -> new SimpleStringProperty("Rs " + String.format("%.2f", d.getValue().getSubtotal())));
        blColTax.setCellValueFactory(d       -> new SimpleStringProperty("Rs " + String.format("%.2f", d.getValue().getTaxAmount())));
        blColDiscount.setCellValueFactory(d  -> new SimpleStringProperty("Rs " + String.format("%.2f", d.getValue().getDiscountAmount())));
        blColTotal.setCellValueFactory(d     -> new SimpleStringProperty("Rs " + String.format("%.2f", d.getValue().getTotalAmount())));
        blColStatus.setCellValueFactory(d    -> new SimpleStringProperty(d.getValue().getStatus()));

        blColAction.setCellFactory(col -> new TableCell<>() {
            final Button pay = new Button("Mark Paid");
            {
                pay.getStyleClass().add("btn-table-sm");
                pay.setOnAction(e -> {
                    Bill b = getTableView().getItems().get(getIndex());
                    b.markPaid();
                    billTable.refresh();
                    updateTopStats();
                    billMsg.setText("Bill " + b.getBillId() + " has been marked as PAID.");
                });
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : pay);
            }
        });

        billTable.setItems(billList);

        biColItem.setCellValueFactory(d     -> new SimpleStringProperty(d.getValue().getName()));
        biColCategory.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCategory()));
        biColAmount.setCellValueFactory(d   -> new SimpleStringProperty("Rs " + String.format("%.2f", d.getValue().getAmount())));

        billTable.getSelectionModel().selectedItemProperty().addListener((obs, o, selected) -> {
            if (selected != null)
                billItemTable.setItems(FXCollections.observableArrayList(selected.getItems()));
        });
    }

    private void setupReportTable() {
        rpColName.setCellValueFactory(d   -> new SimpleStringProperty(d.getValue().getName()));
        rpColAge.setCellValueFactory(d    -> new SimpleStringProperty(String.valueOf(d.getValue().getAge())));
        rpColBlood.setCellValueFactory(d  -> new SimpleStringProperty(d.getValue().getBloodGroup()));
        rpColAppts.setCellValueFactory(d  -> new SimpleStringProperty(String.valueOf(d.getValue().getAppointments().size())));
        rpColSvcs.setCellValueFactory(d   -> new SimpleStringProperty(String.valueOf(d.getValue().getServicesReceived().size())));
        rpColBilled.setCellValueFactory(d -> new SimpleStringProperty("Rs " + String.format("%.2f", d.getValue().getTotalBillAmount())));
        rpColEmerg.setCellValueFactory(d  -> new SimpleStringProperty(d.getValue().isEmergency() ? "YES" : "No"));
        reportTable.setItems(patientList);
    }

    @FXML private void showRegisterPatient() { ptName.requestFocus(); }

    @FXML private void savePatient() {
        if (ptName.getText().isBlank() || ptEmail.getText().isBlank()
                || ptPass.getText().isBlank() || ptAge.getText().isBlank()) {
            setMsg(patientFormMsg, "Please fill in all required fields.", false);
            return;
        }
        try {
            Patient p = new Patient(hospital.nextPatientId(),
                    ptName.getText().trim(), ptEmail.getText().trim(),
                    ptPass.getText().trim(), ptPhone.getText().trim(),
                    Integer.parseInt(ptAge.getText().trim()),
                    ptBlood.getValue() != null ? ptBlood.getValue() : "O+",
                    ptAddress.getText().trim());
            p.setEmergency(ptEmergency.isSelected());
            hospital.addPatient(p);
            patientList.add(p);
            populateCombos();
            updateTopStats();
            clearPatientForm();
            setMsg(patientFormMsg, "Patient " + p.getName() + " registered. ID: " + p.getUserId(), true);
        } catch (NumberFormatException ex) {
            setMsg(patientFormMsg, "Age must be a valid number.", false);
        }
    }

    @FXML private void clearPatientForm() {
        ptName.clear(); ptEmail.clear(); ptPass.clear();
        ptPhone.clear(); ptAge.clear(); ptAddress.clear();
        ptBlood.setValue(null); ptEmergency.setSelected(false);
        patientFormMsg.setText("");
    }

    @FXML private void searchPatients() {}

    @FXML private void showRegisterDoctor() { drName.requestFocus(); }

    @FXML private void saveDoctor() {
        if (drName.getText().isBlank() || drEmail.getText().isBlank()
                || drPass.getText().isBlank() || drFee.getText().isBlank()) {
            setMsg(doctorFormMsg, "Please fill in all required fields.", false);
            return;
        }
        try {
            Doctor d = new Doctor(hospital.nextDoctorId(),
                    drName.getText().trim(), drEmail.getText().trim(),
                    drPass.getText().trim(), drPhone.getText().trim(),
                    drSpec.getValue() != null ? drSpec.getValue() : "General Medicine",
                    drQual.getText().trim(), Double.parseDouble(drFee.getText().trim()));
            hospital.addDoctor(d);
            doctorList.add(d);
            populateCombos();
            updateTopStats();
            clearDoctorForm();
            setMsg(doctorFormMsg, "Dr. " + d.getName() + " registered. ID: " + d.getUserId(), true);
        } catch (NumberFormatException ex) {
            setMsg(doctorFormMsg, "Fee must be a valid number.", false);
        }
    }

    @FXML private void clearDoctorForm() {
        drName.clear(); drEmail.clear(); drPass.clear();
        drPhone.clear(); drQual.clear(); drFee.clear();
        drSpec.setValue(null); doctorFormMsg.setText("");
    }

    @FXML private void searchDoctors() {}

    @FXML private void showBookAppointment() { apPatient.requestFocus(); }

    @FXML private void saveAppointment() {
        if (apPatient.getValue() == null || apDoctor.getValue() == null
                || apDate.getText().isBlank() || apTime.getText().isBlank()) {
            setMsg(apptFormMsg, "Please fill patient, doctor, date and time.", false);
            return;
        }
        String patientId = apPatient.getValue().split(" - ")[0];
        String doctorId  = apDoctor.getValue().split(" - ")[0];
        Patient p = hospital.getPatient(patientId);
        Doctor  d = hospital.getDoctor(doctorId);
        if (p == null || d == null) { setMsg(apptFormMsg, "Invalid selection.", false); return; }

        Appointment.Priority prio = Appointment.Priority.NORMAL;
        try { prio = Appointment.Priority.valueOf(apPriority.getValue()); } catch (Exception ignored) {}

        Appointment appt = new Appointment(hospital.nextAppointmentId(), p, d,
                apDate.getText().trim(), apTime.getText().trim(), apReason.getText().trim(), prio);
        hospital.addAppointment(appt);
        appointmentList.add(appt);
        d.assignPatient(p);
        updateTopStats();
        clearApptForm();
        setMsg(apptFormMsg, "Appointment " + appt.getAppointmentId() + " scheduled for " + p.getName(), true);
    }

    @FXML private void clearApptForm() {
        apPatient.setValue(null); apDoctor.setValue(null);
        apDate.clear(); apTime.clear(); apReason.clear();
        apPriority.setValue("NORMAL"); apptFormMsg.setText("");
    }

    @FXML private void showAddService() { svPatient.requestFocus(); }

    @FXML private void saveService() {
        if (svPatient.getValue() == null || svType.getValue() == null || svSubType.getValue() == null) {
            setMsg(serviceFormMsg, "Please select patient, type and sub-type.", false);
            return;
        }
        String patientId = svPatient.getValue().split(" - ")[0];
        Patient p = hospital.getPatient(patientId);
        if (p == null) return;

        MedicalService svc = null;
        String id = hospital.nextServiceId();

        switch (svType.getValue()) {
            case "Consultation": {
                String docName = "Unknown";
                if (svDoctor.getValue() != null) {
                    String dId = svDoctor.getValue().split(" - ")[0];
                    Doctor doc = hospital.getDoctor(dId);
                    if (doc != null) docName = doc.getName();
                }
                svc = new ConsultationService(id, docName, svSubType.getValue(), 2000);
                break;
            }
            case "Diagnostic":
                svc = new DiagnosticService(id, svSubType.getValue(), svUrgent.isSelected());
                break;
            case "Treatment":
                TreatmentService.Complexity cx = TreatmentService.Complexity.valueOf(svSubType.getValue());
                svc = new TreatmentService(id, svSubType.getValue() + " Treatment", cx, 1);
                break;
        }

        if (svc != null) {
            svc.setPatientId(p.getUserId());
            svc.executeService();
            hospital.addService(svc);
            serviceList.add(svc);
            p.addService(svc);
            clearServiceForm();
            setMsg(serviceFormMsg, "Service added: " + svc.getServiceName() + " for " + p.getName(), true);
        }
    }

    @FXML private void clearServiceForm() {
        svPatient.setValue(null); svType.setValue(null);
        svDoctor.setValue(null);  svSubType.setValue(null);
        svUrgent.setSelected(false); serviceFormMsg.setText("");
    }

    @FXML private void generateBill() {
        if (billPatientCombo.getValue() == null) {
            setMsg(billMsg, "Select a patient first.", false);
            return;
        }
        String patientId = billPatientCombo.getValue().split(" - ")[0];
        Patient p = hospital.getPatient(patientId);
        if (p == null) return;

        Bill bill = new Bill(hospital.nextBillId(), p.getUserId(), p.getName());
        p.getServicesReceived().forEach(bill::addService);
        if (p.getServicesReceived().isEmpty()) bill.addItem("Registration Fee", "ADMIN", 200.0);

        try {
            String discStr = billDiscount.getText().trim();
            if (!discStr.isEmpty()) {
                double disc = Double.parseDouble(discStr) / 100.0;
                bill.setDiscountRate(Math.min(disc, 1.0));
            }
        } catch (NumberFormatException ignored) {}

        hospital.addBill(bill);
        billList.add(bill);
        p.addBill(bill);
        populateCombos();
        updateTopStats();
        setMsg(billMsg, "Bill " + bill.getBillId() + " generated for " + p.getName()
                + " - Total: Rs " + String.format("%.2f", bill.getTotalAmount()), true);
    }

    @FXML private void markBillPaid() {
        Bill selected = billTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setMsg(billMsg, "Select a bill from the table first.", false);
            return;
        }
        selected.markPaid();
        billTable.refresh();
        updateTopStats();
        setMsg(billMsg, "Bill " + selected.getBillId() + " marked as PAID.", true);
    }

    @FXML private void refreshReports() {
        rptPatients.setText(String.valueOf(hospital.getTotalPatients()));
        rptDoctors.setText(String.valueOf(hospital.getTotalDoctors()));
        rptAppointments.setText(String.valueOf(hospital.getTotalAppointments()));
        rptBills.setText(String.valueOf(hospital.getAllBills().size()));
        rptRevenue.setText("Rs " + String.format("%.0f", hospital.getTotalRevenue()));
        reportTable.setItems(patientList);
    }

    /*
 Message label ko green ya red style ke saath set karta hai.
 success true ho to green, false ho to red dikhta hai.
 */
    private void setMsg(Label lbl, String text, boolean success) {
        lbl.setStyle(success ? "-fx-text-fill: #16A34A;" : "-fx-text-fill: #DC2626;");
        lbl.setText(text);
    }

    @FXML private void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/fxml/Login.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setTitle("Saraiki Medical Complex - Login");
            stage.setScene(new Scene(root, 900, 600));
            stage.centerOnScreen();
        } catch (Exception e) { e.printStackTrace(); }
    }
}