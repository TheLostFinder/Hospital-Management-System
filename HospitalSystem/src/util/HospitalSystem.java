package util;

import model.appointments.Appointment;
import model.billing.Bill;
import model.services.MedicalService;
import model.users.AdminStaff;
import model.users.Doctor;
import model.users.Patient;
import model.users.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/*
 * HospitalSystem ek singleton class hai jo poori application ka
 * central data store hai. Tamam patients, doctors, appointments,
 * services aur bills yahan store hote hain. Singleton pattern
 * isliye use kiya gaya hai taake poori app mein sirf ek instance
 * ho aur data consistent rahe.
 */
public class HospitalSystem {

    private static HospitalSystem instance;

    public static HospitalSystem getInstance() {
        if (instance == null) instance = new HospitalSystem();
        return instance;
    }

    private Map<String, Patient>    patients     = new LinkedHashMap<>();
    private Map<String, Doctor>     doctors      = new LinkedHashMap<>();
    private Map<String, AdminStaff> admins       = new LinkedHashMap<>();
    private List<Appointment>       appointments = new ArrayList<>();
    private List<MedicalService>    services     = new ArrayList<>();
    private List<Bill>              bills        = new ArrayList<>();

    /*
     * AtomicInteger thread-safe counter hai jo har entity ke liye
     * unique ID generate karta hai. Har baar increment hota hai.
     */
    private AtomicInteger patientSeq     = new AtomicInteger(1000);
    private AtomicInteger doctorSeq      = new AtomicInteger(2000);
    private AtomicInteger adminSeq       = new AtomicInteger(3000);
    private AtomicInteger appointmentSeq = new AtomicInteger(4000);
    private AtomicInteger serviceSeq     = new AtomicInteger(5000);
    private AtomicInteger billSeq        = new AtomicInteger(6000);

    public String nextPatientId()     { return "P"   + patientSeq.incrementAndGet(); }
    public String nextDoctorId()      { return "D"   + doctorSeq.incrementAndGet(); }
    public String nextAdminId()       { return "A"   + adminSeq.incrementAndGet(); }
    public String nextAppointmentId() { return "APT" + appointmentSeq.incrementAndGet(); }
    public String nextServiceId()     { return "S"   + serviceSeq.incrementAndGet(); }
    public String nextBillId()        { return "B"   + billSeq.incrementAndGet(); }
    /*
     Check karta hai ke same patient/doctor ke liye same time par appointment
     exist karti hai ya nahi. Agar exist karti hai to true return karta hai.
    */
    public boolean hasAppointmentAtTime(String userId, String date, String time, boolean isDoctor) {
        return appointments.stream()
            .anyMatch(a -> {
                String checkId = isDoctor ? a.getDoctor().getUserId() : a.getPatient().getUserId();
                return checkId.equals(userId)
                    && a.getDate().equals(date)
                    && a.getTime().equals(time)
                    && (a.getStatus().equals("SCHEDULED") || a.getStatus().equals("RESCHEDULED"));
            });
    }

    /*
     Higher priority appointment check karta hai. Agar urgent ya emergency appointment
     exist karti hai to lower priority ko reschedule karne ke liye true return karta hai.
    */
    public Appointment getHigherPriorityAppointment(String userId, String date, String time, boolean isDoctor, Appointment.Priority currentPriority) {
        return appointments.stream()
            .filter(a -> {
                String checkId = isDoctor ? a.getDoctor().getUserId() : a.getPatient().getUserId();
                return checkId.equals(userId)
                    && a.getDate().equals(date)
                    && a.getTime().equals(time)
                    && a.getPriority().ordinal() < currentPriority.ordinal();
            })
            .findFirst().orElse(null);
    }

    private HospitalSystem() { seedData(); }

    /*
     * Application start hone par kuch default doctors, admin aur
     * patients add kiye jaate hain taake demo immediately kaam kare.
     */
    private void seedData() {
        Doctor d1 = new Doctor(nextDoctorId(), "Umair Naveed", "nav@mail",
                "123", "0300-1234567", "Cardiology", "MBBS, FCPS", 20000);
        Doctor d2 = new Doctor(nextDoctorId(), "Rayan Ahmed", "ray@mail",
                "123", "0301-2345678", "Neurology", "MBBS, MRCP", 2500);
        Doctor d3 = new Doctor(nextDoctorId(), "Talha", "tal@mail",
                "123", "0302-3456789", "Orthopedics", "MBBS, MS Ortho", 18000);
        doctors.put(d1.getUserId(), d1);
        doctors.put(d2.getUserId(), d2);
        doctors.put(d3.getUserId(), d3);
        AdminStaff a1 = new AdminStaff(nextAdminId(), "Rayan", "adm@mail",
                "123", "0303-4567890", "Reception", "Senior Admin Officer");
        admins.put(a1.getUserId(), a1);

        Patient p1 = new Patient(nextPatientId(), "Zohaib", "zoh@mail",
                "123", "0304-5678901", 34, "B+", "House 5, Islamabad");
        Patient p2 = new Patient(nextPatientId(), "Muhammad Irtaza", "irtu@mail",
                "123", "0305-6789012", 28, "O-", "Street 12, Rawalpindi");
        patients.put(p1.getUserId(), p1);
        patients.put(p2.getUserId(), p2);
    }
    /*
     Email aur password se user ko authenticate karta hai. Pehle
     patients, phir doctors, phir admins check karta hai. Agar
     koi match na mile to null return karta hai.
     */
    public User authenticate(String email, String password) {
        for (Patient p : patients.values())
            if (p.getEmail().equals(email) && p.checkPassword(password)) return p;
        for (Doctor d : doctors.values())
            if (d.getEmail().equals(email) && d.checkPassword(password)) return d;
        for (AdminStaff a : admins.values())
            if (a.getEmail().equals(email) && a.checkPassword(password)) return a;
        return null;
    }

    public void             addPatient(Patient p)  { patients.put(p.getUserId(), p); }
    public Patient          getPatient(String id)  { return patients.get(id); }
    public Collection<Patient> getAllPatients()     { return patients.values(); }

    public Patient findPatientByName(String name) {
        return patients.values().stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    public void            addDoctor(Doctor d)   { doctors.put(d.getUserId(), d); }
    public Doctor          getDoctor(String id)  { return doctors.get(id); }
    public Collection<Doctor> getAllDoctors()     { return doctors.values(); }

    public List<Doctor> getDoctorsBySpecialization(String spec) {
        return doctors.values().stream()
                .filter(d -> d.getSpecialization().equalsIgnoreCase(spec))
                .collect(Collectors.toList());
    }

    /*
     Sirf available doctors return karta hai taake appointment
     booking mein sirf woh doctors dikhein jo patients le sakte
     hain.
    */
    public List<Doctor> getAvailableDoctors() {
        return doctors.values().stream()
                .filter(Doctor::isAvailable)
                .collect(Collectors.toList());
    }

    public void addAdmin(AdminStaff a)           { admins.put(a.getUserId(), a); }
    public Collection<AdminStaff> getAllAdmins() { return admins.values(); }

    /*
     Appointment add karte waqt woh patient aur doctor dono ke
     records mein bhi add ho jaati hai taake sab jagah synced
     rahe.
    */
    public void addAppointment(Appointment appt) {
        appointments.add(appt);
        appt.getPatient().addAppointment(appt);
        appt.getDoctor().addAppointment(appt);
    }

    public List<Appointment> getAllAppointments() { return appointments; }

    public List<Appointment> getAppointmentsForPatient(String patientId) {
        return appointments.stream()
                .filter(a -> a.getPatient().getUserId().equals(patientId))
                .sorted()
                .collect(Collectors.toList());
    }

    public List<Appointment> getAppointmentsForDoctor(String doctorId) {
        return appointments.stream()
                .filter(a -> a.getDoctor().getUserId().equals(doctorId))
                .sorted()
                .collect(Collectors.toList());
    }

    public void addService(MedicalService s)    { services.add(s); }
    public List<MedicalService> getAllServices() { return services; }

    public void addBill(Bill b)     { bills.add(b); }
    public List<Bill> getAllBills() { return bills; }

    public List<Bill> getBillsForPatient(String patientId) {
        return bills.stream()
                .filter(b -> b.getPatientId().equals(patientId))
                .collect(Collectors.toList());
    }

    public int    getTotalPatients()     { return patients.size(); }
    public int    getTotalDoctors()      { return doctors.size(); }
    public int    getTotalAppointments() { return appointments.size(); }

    /*
     Sirf PAID bills ka total revenue calculate karke return
     karta hai.
    */
    public double getTotalRevenue() {
        return bills.stream()
                .filter(b -> b.getStatus().equals("PAID"))
                .mapToDouble(Bill::getTotalAmount).sum();
    }
}
