package model.appointments;

import model.users.Doctor;
import model.users.Patient;

/*
 Appointment class ek patient aur doctor ke beech scheduled
 meeting ko represent karta hai. Comparable implement kiya gaya
 hai taake appointments priority aur time ke mutabiq sort ho
 sakein. Emergency appointments hamesha upar aate hain.
 */
public class Appointment implements Comparable<Appointment> {

    public enum Priority { NORMAL, URGENT, EMERGENCY }

    private String   appointmentId;
    private Patient  patient;
    private Doctor   doctor;
    private String   date;
    private String   time;
    private String   reason;
    private String   status;
    private Priority priority;
    private String   notes;

    public Appointment(String appointmentId, Patient patient, Doctor doctor,
                       String date, String time, String reason, Priority priority) {
        this.appointmentId = appointmentId;
        this.patient       = patient;
        this.doctor        = doctor;
        this.date          = date;
        this.time          = time;
        this.reason        = reason;
        this.status        = "SCHEDULED";
        this.priority      = priority;
        this.notes         = "";
    }

    /*
 Appointment ko sort karne ke liye pehle priority compare
 hoti hai phir date aur time. Is tarah urgent cases pehle
 aa jaate hain.
 */
    @Override
    public int compareTo(Appointment other) {
        int cmp = other.priority.ordinal() - this.priority.ordinal();
        if (cmp != 0) return cmp;
        cmp = this.date.compareTo(other.date);
        if (cmp != 0) return cmp;
        return this.time.compareTo(other.time);
    }

    /*
 Appointment complete karta hai aur patient ki medical
 history mein automatically ek note add karta hai.
 */
    public void complete() {
        this.status = "COMPLETED";
        patient.addMedicalNote("Appointment on " + date + " with Dr. " + doctor.getName()
                + " - Reason: " + reason);
    }

    public void cancel() {
        this.status = "CANCELLED";
        doctor.removeAppointment(this);
    }

    public void reschedule(String newDate, String newTime) {
        this.date   = newDate;
        this.time   = newTime;
        this.status = "RESCHEDULED";
    }

    /*
 Appointment ko naya time deta hai. 10 minutes baad reschedule hota hai
 conflict resolution ke liye.
    */
    public void autoReschedule10Min() {
        String[] timeParts = this.time.split(":");
        int minutes = Integer.parseInt(timeParts[1]) + 10;
        int hours = Integer.parseInt(timeParts[0]);

        if (minutes >= 60) {
            minutes -= 60;
            hours += 1;
        }

        this.time = String.format("%02d:%02d", hours, minutes);
        this.status = "RESCHEDULED";
    }

    public String getSummary() {
        return "Appt #" + appointmentId
                + " | Patient: " + patient.getName()
                + " | Doctor: Dr. " + doctor.getName()
                + " | " + date + " " + time
                + " | Priority: " + priority
                + " | Status: " + status;
    }

    public String   getAppointmentId() { return appointmentId; }
    public Patient  getPatient()       { return patient; }
    public Doctor   getDoctor()        { return doctor; }
    public String   getDate()          { return date; }
    public String   getTime()          { return time; }
    public String   getReason()        { return reason; }
    public String   getStatus()        { return status; }
    public Priority getPriority()      { return priority; }
    public String   getNotes()         { return notes; }

    public void setStatus(String status) { this.status = status; }
    public void setNotes(String notes)   { this.notes  = notes; }
    public void setDate(String date)     { this.date   = date; }
    public void setTime(String time)     { this.time   = time; }

    @Override
    public String toString() { return getSummary(); }
}