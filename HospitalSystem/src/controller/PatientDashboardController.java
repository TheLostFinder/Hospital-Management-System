package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.appointments.Appointment;
import model.billing.Bill;
import model.services.ConsultationService;
import model.services.MedicalService;
import model.users.Doctor;
import model.users.Patient;
import util.HospitalSystem;

import java.net.URL;
import java.util.ResourceBundle;

/*
 PatientDashboardController patient portal manage karta hai. Patient
 apne appointments book kar sakta hai, medical history dekh sakta hai,
 services aur bills check kar sakta hai. Har table live update hota
 hai jab koi naya record add hota hai.
*/
public class PatientDashboardController implements Initializable {

    @FXML private Label welcomeLabel, statMyAppts, statMyServices, statTotalBill, emergencyBadge;

    @FXML private ComboBox<String> bookDoctorCombo, bookPriority;
    @FXML private TextField bookDate, bookTime;
    @FXML private TextArea  bookReason;
    @FXML private Label     bookMsg;
    @FXML private TableView<Appointment> myApptTable;
    @FXML private TableColumn<Appointment,String> maColId, maColDoctor, maColSpec, maColDate, maColTime, maColPriority, maColStatus;
    @FXML private TableColumn<Appointment,Void>   maColReschedule;

    @FXML private TableView<Appointment> historyTable;
    @FXML private TableColumn<Appointment,String> hiColDate, hiColDoctor, hiColReason, hiColNotes, hiColStatus;

    @FXML private TableView<MedicalService> myServiceTable;
    @FXML private TableColumn<MedicalService,String> msColId, msColName, msColCategory, msColDoctor, msColCost, msColDate, msColStatus;

    @FXML private TableView<Bill>          myBillTable;
    @FXML private TableColumn<Bill,String> mbColId, mbColGenerated, mbColSubtotal, mbColTax, mbColTotal, mbColStatus;
    @FXML private TableColumn<Bill,Void>   mbColAction;
    @FXML private TableView<Bill.BillItem>  myBillItemTable;
    @FXML private TableColumn<Bill.BillItem,String> biColItem2, biColCategory2, biColAmount2;

    @FXML private Label   profileName, profileInfoLbl, profilePhone, profileEmail, profileAddress, profileId, profileEmergBadge, profileMsg;
    @FXML private TextField updatePhone, updateAddress;

    private Patient currentPatient;
    private final HospitalSystem hospital = HospitalSystem.getInstance();

    private final ObservableList<Appointment>    apptList    = FXCollections.observableArrayList();
    private final ObservableList<MedicalService> serviceList = FXCollections.observableArrayList();
    private final ObservableList<Bill>           billList    = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupApptTable();
        setupHistoryTable();
        setupServiceTable();
        setupBillTable();
    }

    public void initData(Patient patient) {
        this.currentPatient = patient;
        welcomeLabel.setText(patient.getName());
        if (patient.isEmergency()) {
            emergencyBadge.setText("EMERGENCY");
        }
        loadDoctorCombo();
        bookPriority.setItems(FXCollections.observableArrayList("NORMAL", "URGENT", "EMERGENCY"));
        bookPriority.setValue("NORMAL");
        refreshAllData();
        loadProfileTab();
    }

    private void loadDoctorCombo() {
        bookDoctorCombo.getItems().clear();
        hospital.getAvailableDoctors().forEach(d ->
            bookDoctorCombo.getItems().add(
                d.getUserId() + " | Dr. " + d.getName()
                + " - " + d.getSpecialization()
                + " (Rs " + d.getConsultationFee() + ")"));
    }

    private void refreshAllData() {
        apptList.setAll(hospital.getAppointmentsForPatient(currentPatient.getUserId()));
        serviceList.setAll(currentPatient.getServicesReceived());
        billList.setAll(currentPatient.getBills());
        updateTopStats();
    }

    private void updateTopStats() {
        statMyAppts.setText(String.valueOf(apptList.size()));
        statMyServices.setText(String.valueOf(serviceList.size()));
        statTotalBill.setText("Rs " + String.format("%.2f", currentPatient.getTotalBillAmount()));
    }

    private void setupApptTable() {
        maColId.setCellValueFactory(d       -> new SimpleStringProperty(d.getValue().getAppointmentId()));
        maColDoctor.setCellValueFactory(d   -> new SimpleStringProperty("Dr. " + d.getValue().getDoctor().getName()));
        maColSpec.setCellValueFactory(d     -> new SimpleStringProperty(d.getValue().getDoctor().getSpecialization()));
        maColDate.setCellValueFactory(d     -> new SimpleStringProperty(d.getValue().getDate()));
        maColTime.setCellValueFactory(d     -> new SimpleStringProperty(d.getValue().getTime()));
        maColPriority.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPriority().name()));
        maColStatus.setCellValueFactory(d   -> new SimpleStringProperty(d.getValue().getStatus()));

        /*
         Reschedule button add karta hai appointment ke liye.
        */
        maColReschedule.setCellFactory(col -> new TableCell<>() {
            final Button rescheduleBtn = new Button("Reschedule");
            {
                rescheduleBtn.getStyleClass().add("btn-table-sm");
                rescheduleBtn.setOnAction(e -> {
                    Appointment appt = getTableView().getItems().get(getIndex());
                    showRescheduleDialog(appt);
                });
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : rescheduleBtn);
            }
        });

        myApptTable.setItems(apptList);
    }

    private void setupHistoryTable() {
        hiColDate.setCellValueFactory(d   -> new SimpleStringProperty(d.getValue().getDate()));
        hiColDoctor.setCellValueFactory(d -> new SimpleStringProperty("Dr. " + d.getValue().getDoctor().getName()));
        hiColReason.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getReason()));
        hiColNotes.setCellValueFactory(d  -> new SimpleStringProperty(
            d.getValue().getNotes().isEmpty() ? "(pending)" : d.getValue().getNotes()));
        hiColStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus()));
        historyTable.setItems(apptList);
    }

    private void setupServiceTable() {
        msColId.setCellValueFactory(d       -> new SimpleStringProperty(d.getValue().getServiceId()));
        msColName.setCellValueFactory(d     -> new SimpleStringProperty(d.getValue().getServiceName()));
        msColCategory.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCategory()));
        msColDoctor.setCellValueFactory(d   -> new SimpleStringProperty(
            d.getValue().getAssignedDoctorName().isEmpty() ? "-" : "Dr. " + d.getValue().getAssignedDoctorName()));
        msColCost.setCellValueFactory(d     -> new SimpleStringProperty("Rs " + String.format("%.2f", d.getValue().getCost())));
        msColDate.setCellValueFactory(d     -> new SimpleStringProperty(
            d.getValue().getPerformedAt().isEmpty() ? "-" : d.getValue().getPerformedAt()));
        msColStatus.setCellValueFactory(d   -> new SimpleStringProperty(d.getValue().getStatus()));
        myServiceTable.setItems(serviceList);
    }

    private void setupBillTable() {
        mbColId.setCellValueFactory(d        -> new SimpleStringProperty(d.getValue().getBillId()));
        mbColGenerated.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getGeneratedAt()));
        mbColSubtotal.setCellValueFactory(d  -> new SimpleStringProperty("Rs " + String.format("%.2f", d.getValue().getSubtotal())));
        mbColTax.setCellValueFactory(d       -> new SimpleStringProperty("Rs " + String.format("%.2f", d.getValue().getTaxAmount())));
        mbColTotal.setCellValueFactory(d     -> new SimpleStringProperty("Rs " + String.format("%.2f", d.getValue().getTotalAmount())));
        mbColStatus.setCellValueFactory(d    -> new SimpleStringProperty(d.getValue().getStatus()));

        mbColAction.setCellFactory(col -> new TableCell<>() {
            final Button payBtn = new Button("Pay Bill");
            {
                payBtn.getStyleClass().add("btn-table-sm");
                payBtn.setOnAction(e -> {
                    Bill b = getTableView().getItems().get(getIndex());
                    if ("PENDING".equals(b.getStatus())) {
                        b.markPaid();
                        myBillTable.refresh();
                        updateTopStats();
                        setMsg(bookMsg, "Bill " + b.getBillId() + " paid successfully.", true);
                    }
                });
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Bill b = getTableView().getItems().get(getIndex());
                    payBtn.setDisable("PAID".equals(b.getStatus()));
                    setGraphic(payBtn);
                }
            }
        });

        myBillTable.setItems(billList);

        biColItem2.setCellValueFactory(d     -> new SimpleStringProperty(d.getValue().getName()));
        biColCategory2.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCategory()));
        biColAmount2.setCellValueFactory(d   -> new SimpleStringProperty("Rs " + String.format("%.2f", d.getValue().getAmount())));

        myBillTable.getSelectionModel().selectedItemProperty().addListener((obs, o, sel) -> {
            if (sel != null)
                myBillItemTable.setItems(FXCollections.observableArrayList(sel.getItems()));
        });
    }

    @FXML private void confirmBooking() {
        if (bookDoctorCombo.getValue() == null
                || bookDate.getText().isBlank()
                || bookTime.getText().isBlank()) {
            setMsg(bookMsg, "Please select a doctor and enter date and time.", false);
            return;
        }
        String doctorId = bookDoctorCombo.getValue().split(" \\| ")[0];
        Doctor doctor = hospital.getDoctor(doctorId);
        if (doctor == null) { setMsg(bookMsg, "Doctor not found.", false); return; }

        Appointment.Priority prio = Appointment.Priority.NORMAL;
        try { prio = Appointment.Priority.valueOf(bookPriority.getValue()); }
        catch (Exception ignored) {}

        String requestedDate = bookDate.getText().trim();
        String requestedTime = bookTime.getText().trim();

        /*
         Appointment ke liye conflict check karta hai. Agar patient ya doctor
         ke paas same time par appointment hai to handle karta hai.
        */
        if (hospital.hasAppointmentAtTime(currentPatient.getUserId(), requestedDate, requestedTime, false)) {
            setMsg(bookMsg, "You already have an appointment at this time.", false);
            return;
        }

        if (hospital.hasAppointmentAtTime(doctorId, requestedDate, requestedTime, true)) {
            /*
             Agar doctor ke paas existing appointment hai to check karta hai
             ki nayi appointment priority higher hai ya nahi.
            */
            Appointment existingAppt = hospital.getHigherPriorityAppointment(
                doctorId, requestedDate, requestedTime, true, prio);
            
            if (existingAppt != null) {
                /*
                 Nayi appointment priority zyada hai to purani ko reschedule karta hai.
                */
                existingAppt.autoReschedule10Min();
                setMsg(bookMsg, "Previous lower priority appointment rescheduled. Your appointment confirmed.", true);
            } else {
                /*
                 Doctor busy hai aur nayi appointment priority nahi hai to reject.
                */
                setMsg(bookMsg, "Doctor is not available at this time. Please choose another time.", false);
                return;
            }
        }

        String reason = bookReason.getText().trim().isEmpty() ? "General consultation" : bookReason.getText().trim();

        Appointment appt = new Appointment(hospital.nextAppointmentId(),
            currentPatient, doctor, requestedDate, requestedTime, reason, prio);
        hospital.addAppointment(appt);
        doctor.assignPatient(currentPatient);

        ConsultationService svc = new ConsultationService(
            hospital.nextServiceId(), doctor.getName(),
            prio == Appointment.Priority.EMERGENCY ? "EMERGENCY" : "GENERAL",
            doctor.getConsultationFee());
        svc.setPatientId(currentPatient.getUserId());
        svc.executeService();
        hospital.addService(svc);
        currentPatient.addService(svc);

        Bill bill = new Bill(hospital.nextBillId(), currentPatient.getUserId(), currentPatient.getName());
        bill.addService(svc);
        hospital.addBill(bill);
        currentPatient.addBill(bill);

        bookDoctorCombo.setValue(null);
        bookDate.clear(); bookTime.clear(); bookReason.clear();
        bookPriority.setValue("NORMAL");

        refreshAllData();
        setMsg(bookMsg, "Appointment " + appt.getAppointmentId() + " booked with Dr. "
            + doctor.getName() + " on " + appt.getDate() + ". Bill created and waiting for payment.", true);
    }

    private void showRescheduleDialog(Appointment appt) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Reschedule Appointment");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField dateField = new TextField(appt.getDate());
        TextField timeField = new TextField(appt.getTime());
        
        grid.add(new Label("New Date (YYYY-MM-DD):"), 0, 0);
        grid.add(dateField, 1, 0);
        grid.add(new Label("New Time (HH:MM):"), 0, 1);
        grid.add(timeField, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        if (dialog.showAndWait().get() == ButtonType.OK) {
            appt.reschedule(dateField.getText().trim(), timeField.getText().trim());
            myApptTable.refresh();
            setMsg(bookMsg, "Appointment rescheduled to " + appt.getDate() + " at " + appt.getTime(), true);
        }
    }

    @FXML private void onBillSelected() {}

    private void loadProfileTab() {
        profileName.setText(currentPatient.getName());
        profileInfoLbl.setText("Age: " + currentPatient.getAge() + "  |  Blood: " + currentPatient.getBloodGroup());
        profilePhone.setText(currentPatient.getPhone());
        profileEmail.setText(currentPatient.getEmail());
        profileAddress.setText(currentPatient.getAddress());
        profileId.setText(currentPatient.getUserId());
        if (currentPatient.isEmergency()) profileEmergBadge.setText("EMERGENCY");
    }

    @FXML private void updateProfile() {
        if (!updatePhone.getText().isBlank())   currentPatient.setPhone(updatePhone.getText().trim());
        if (!updateAddress.getText().isBlank()) currentPatient.setAddress(updateAddress.getText().trim());
        loadProfileTab();
        updatePhone.clear(); updateAddress.clear();
        setMsg(profileMsg, "Profile updated successfully.", true);
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
