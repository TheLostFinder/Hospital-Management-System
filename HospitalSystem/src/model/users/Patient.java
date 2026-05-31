package model.users;

import model.appointments.Appointment;
import model.billing.Bill;
import model.services.MedicalService;

import java.util.ArrayList;
import java.util.List;

/*
 Patient class User ko extend karta hai. Yeh class ek patient ki
 poori profile manage karta hai jisme uski medical history,
 appointments, li gayi services aur bills sab included hain.
 Har naya record add hota rehta hai taake history preserve ho.
   */
public class Patient extends User {

    private int age;
    private String bloodGroup;
    private String address;
    private String medicalHistory;
    private List<Appointment>    appointments;
    private List<MedicalService> servicesReceived;
    private List<Bill>           bills;
    private boolean isEmergency;

    public Patient(String userId, String name, String email,
                   String password, String phone,
                   int age, String bloodGroup, String address) {
        super(userId, name, email, password, phone, "PATIENT");
        this.age              = age;
        this.bloodGroup       = bloodGroup;
        this.address          = address;
        this.medicalHistory   = "";
        this.appointments     = new ArrayList<>();
        this.servicesReceived = new ArrayList<>();
        this.bills            = new ArrayList<>();
        this.isEmergency      = false;
    }

    @Override
    public String getDisplayInfo() {
        return "Patient: " + getName()
             + " | Age: " + age
             + " | Blood: " + bloodGroup
             + " | Emergency: " + (isEmergency ? "YES" : "No");
    }

    @Override
    public String[] getMenuItems() {
        return new String[]{
            "View My Profile",
            "Book Appointment",
            "View Appointments",
            "View Medical History",
            "View Bills",
            "Logout"
        };
    }

    /*
     Naya medical note patient ki history mein add karta hai.
     Agar history pehle se khaali hai to seedha set ho jaata hai,
     warna nayi line pe append hota hai.
    */
    public void addMedicalNote(String note) {
        if (medicalHistory.isEmpty()) {
            medicalHistory = note;
        } else {
            medicalHistory += "\n" + note;
        }
    }

    public void addService(MedicalService service) {
        servicesReceived.add(service);
    }

    public void addAppointment(Appointment appt) {
        appointments.add(appt);
    }

    public void addBill(Bill bill) {
        bills.add(bill);
    }

    /*
     Patient ke tamam bills ka total amount calculate karke return
     karta hai streams use karke.
    */
    public double getTotalBillAmount() {
        return bills.stream().mapToDouble(Bill::getTotalAmount).sum();
    }

    public int     getAge()            { return age; }
    public String  getBloodGroup()     { return bloodGroup; }
    public String  getAddress()        { return address; }
    public String  getMedicalHistory() { return medicalHistory; }
    public boolean isEmergency()       { return isEmergency; }

    public List<Appointment>    getAppointments()     { return appointments; }
    public List<MedicalService> getServicesReceived() { return servicesReceived; }
    public List<Bill>           getBills()            { return bills; }

    public void setAge(int age)                 { this.age        = age; }
    public void setBloodGroup(String bg)        { this.bloodGroup = bg; }
    public void setAddress(String address)      { this.address    = address; }
    public void setEmergency(boolean emergency) { this.isEmergency = emergency; }

    /*
     Patient ka mukammal summary text return karta hai jo report
     ya detail view mein display hota hai.
    */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("PATIENT SUMMARY\n");
        sb.append("Name      : ").append(getName()).append("\n");
        sb.append("ID        : ").append(getUserId()).append("\n");
        sb.append("Age       : ").append(age).append("\n");
        sb.append("Blood     : ").append(bloodGroup).append("\n");
        sb.append("Phone     : ").append(getPhone()).append("\n");
        sb.append("Address   : ").append(address).append("\n");
        sb.append("Emergency : ").append(isEmergency ? "YES" : "No").append("\n\n");
        sb.append("Medical History\n");
        sb.append(medicalHistory.isEmpty() ? "No records yet." : medicalHistory).append("\n\n");
        sb.append("Services Received\n");
        if (servicesReceived.isEmpty()) {
            sb.append("None.\n");
        } else {
            servicesReceived.forEach(s -> sb.append("  ")
                .append(s.getServiceName())
                .append(" - Rs ").append(s.getCost()).append("\n"));
        }
        sb.append("\nTotal Bill : Rs ").append(getTotalBillAmount()).append("\n");
        return sb.toString();
    }
}
