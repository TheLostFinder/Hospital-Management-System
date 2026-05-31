package model.users;

import model.appointments.Appointment;

import java.util.ArrayList;
import java.util.List;

/*
 Doctor class User ko extend karta hai. Is mein doctor ki
 specialization, qualification, fee aur availability store hoti
 hai. Doctor ke saath assigned patients aur appointment schedule
 bhi track hota hai. Jab schedule bhar jaata hai to availability
 automatically false ho jaati hai.
*/
public class Doctor extends User {

    private String specialization;
    private String qualification;
    private double consultationFee;
    private boolean isAvailable;
    private List<Patient>     assignedPatients;
    private List<Appointment> schedule;
    private int               maxDailyPatients;

    public Doctor(String userId, String name, String email,
                  String password, String phone,
                  String specialization, String qualification,
                  double consultationFee) {
        super(userId, name, email, password, phone, "DOCTOR");
        this.specialization   = specialization;
        this.qualification    = qualification;
        this.consultationFee  = consultationFee;
        this.isAvailable      = true;
        this.assignedPatients = new ArrayList<>();
        this.schedule         = new ArrayList<>();
        this.maxDailyPatients = 20;
    }

    @Override
    public String getDisplayInfo() {
        return "Dr. " + getName()
             + " | " + specialization
             + " | Fee: Rs " + consultationFee
             + " | Available: " + (isAvailable ? "Yes" : "No");
    }

    @Override
    public String[] getMenuItems() {
        return new String[]{
            "View My Profile",
            "View Assigned Patients",
            "Write Diagnosis",
            "View Schedule",
            "Update Availability",
            "Logout"
        };
    }

    /*
     Patient ko doctor ke saath assign karta hai. Agar patient pehle
     se exist karta ho list mein to dobara add nahi hota.
    */
    public boolean assignPatient(Patient patient) {
        if (!assignedPatients.contains(patient)) {
            assignedPatients.add(patient);
            return true;
        }
        return false;
    }

    public void removePatient(Patient patient) {
        assignedPatients.remove(patient);
    }

    /*
     Appointment schedule mein add karta hai. Agar appointments ki
     taadaad maximum limit tak pahunch jaaye to doctor unavailable
     ho jaata hai.
    */
    public void addAppointment(Appointment appt) {
        schedule.add(appt);
        if (schedule.size() >= maxDailyPatients) {
            isAvailable = false;
        }
    }

    public void removeAppointment(Appointment appt) {
        schedule.remove(appt);
        if (schedule.size() < maxDailyPatients) {
            isAvailable = true;
        }
    }

    /*
     Doctor patient ki medical history mein diagnosis note likhta
     hai. Note mein doctor ka naam aur specialization bhi include
     hota hai taake record clear rahe.
   */
    public void writeDiagnosis(Patient patient, String diagnosis) {
        String note = "[Dr. " + getName() + " | " + specialization + "] " + diagnosis;
        patient.addMedicalNote(note);
    }

    public String  getSpecialization()  { return specialization; }
    public String  getQualification()   { return qualification; }
    public double  getConsultationFee() { return consultationFee; }
    public boolean isAvailable()        { return isAvailable; }

    public List<Patient>     getAssignedPatients() { return assignedPatients; }
    public List<Appointment> getSchedule()         { return schedule; }

    public void setSpecialization(String s)  { this.specialization  = s; }
    public void setConsultationFee(double f) { this.consultationFee = f; }
    public void setAvailable(boolean a)      { this.isAvailable     = a; }
    public void setMaxDailyPatients(int m)   { this.maxDailyPatients = m; }

    public String getScheduleSummary() {
        if (schedule.isEmpty()) return "No appointments scheduled.";
        StringBuilder sb = new StringBuilder("Schedule for Dr. " + getName() + ":\n");
        schedule.forEach(a -> sb.append("  ").append(a.getSummary()).append("\n"));
        return sb.toString();
    }
}
