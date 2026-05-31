package model.users;

import model.appointments.Appointment;

import java.util.ArrayList;
import java.util.List;

/*
 AdminStaff class User ko extend karta hai. Yeh hospital ka
 administrative user hota hai jo appointments manage karta hai,
 records maintain karta hai aur system ka operational flow
 ensure karta hai. Har action activity log mein record hoti hai.
*/
public class AdminStaff extends User {

    private String department;
    private String designation;
    private List<String> activityLog;

    public AdminStaff(String userId, String name, String email,
                      String password, String phone,
                      String department, String designation) {
        super(userId, name, email, password, phone, "ADMIN");
        this.department  = department;
        this.designation = designation;
        this.activityLog = new ArrayList<>();
    }

    @Override
    public String getDisplayInfo() {
        return "Admin: " + getName()
             + " | " + designation
             + " | Dept: " + department;
    }

    @Override
    public String[] getMenuItems() {
        return new String[]{
            "Register New Patient",
            "Register New Doctor",
            "Manage Appointments",
            "Generate Patient Report",
            "Generate Billing Report",
            "View All Users",
            "Logout"
        };
    }

    /*
     Admin jo bhi action kare uska timestamp ke saath record
     activity log mein save hota hai. Yeh audit trail maintain
     karne ke liye use hota hai.
    */
    public void logActivity(String action) {
        activityLog.add("[" + java.time.LocalDateTime.now() + "] " + getName() + ": " + action);
    }

    public List<String> getActivityLog() {
        return activityLog;
    }

    /*
     Naya appointment system ki list mein add karta hai aur
     activity log mein bhi record karta hai.
    */
    public boolean scheduleAppointment(Appointment appt, List<Appointment> systemAppointments) {
        systemAppointments.add(appt);
        logActivity("Scheduled appointment: " + appt.getSummary());
        return true;
    }

    /*
     Appointment ko cancel karta hai aur list se hataata hai.
     Agar appointment exist nahi karti to false return karta hai.
    */
    public boolean cancelAppointment(Appointment appt, List<Appointment> systemAppointments) {
        if (systemAppointments.remove(appt)) {
            appt.setStatus("CANCELLED");
            logActivity("Cancelled appointment: " + appt.getSummary());
            return true;
        }
        return false;
    }

    public String getDepartment()  { return department; }
    public String getDesignation() { return designation; }

    public void setDepartment(String d)  { this.department  = d; }
    public void setDesignation(String d) { this.designation = d; }
}
