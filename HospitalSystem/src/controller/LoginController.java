package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.users.AdminStaff;
import model.users.Doctor;
import model.users.Patient;
import model.users.User;
import util.HospitalSystem;

/*
 LoginController login screen ko handle karta hai. User ka email
 aur password verify karke us ke role ke mutabiq sahi dashboard
 par redirect karta hai. Galat credentials par error message
 dikhata hai.
 */
public class LoginController {

    @FXML private TextField     emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label         errorLabel;

    private final HospitalSystem hospital = HospitalSystem.getInstance();

    @FXML
    private void handleLogin() {
        String email    = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Please enter both email and password.");
            return;
        }

        User user = hospital.authenticate(email, password);

        if (user == null) {
            showError("Invalid credentials. Please try again.");
            return;
        }

        try {
            String fxmlPath;
            if      (user instanceof AdminStaff) fxmlPath = "/view/fxml/AdminDashboard.fxml";
            else if (user instanceof Doctor)     fxmlPath = "/view/fxml/DoctorDashboard.fxml";
            else                                 fxmlPath = "/view/fxml/PatientDashboard.fxml";

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Object controller = loader.getController();
            if      (controller instanceof AdminDashboardController)
                ((AdminDashboardController)   controller).initData((AdminStaff) user);
            else if (controller instanceof DoctorDashboardController)
                ((DoctorDashboardController)  controller).initData((Doctor)     user);
            else if (controller instanceof PatientDashboardController)
                ((PatientDashboardController) controller).initData((Patient)    user);

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setTitle("Saraiki Medical Complex - " + user.getRole() + " Dashboard");
            stage.setScene(new Scene(root));
            stage.centerOnScreen();

        } catch (Exception e) {
            showError("Error loading dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
    }
}