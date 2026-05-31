package model.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/*
MedicalService ek abstract class hai jo Serviceable interface ko
implement karta hai. Yeh tamam medical services ka base hai.
ConsultationService, DiagnosticService aur TreatmentService sab
is class ko extend karte hain. executeService() method abstract
hai taake har service apna alag logic likh sake.
*/
public abstract class MedicalService implements Serviceable {

    private String serviceId;
    private String serviceName;
    private String category;
    private double baseCost;
    private String description;
    private String status;
    private String performedAt;
    private String assignedDoctorName;
    private String patientId;

    public MedicalService(String serviceId, String serviceName,
                          String category, double baseCost, String description) {
        this.serviceId          = serviceId;
        this.serviceName        = serviceName;
        this.category           = category;
        this.baseCost           = baseCost;
        this.description        = description;
        this.status             = "PENDING";
        this.performedAt        = "";
        this.assignedDoctorName = "";
        this.patientId          = "";
    }

    @Override public String getServiceName() { return serviceName; }
    @Override public String getCategory()    { return category; }
    @Override public String getDescription() { return description; }

    /*
    getCost() calculateFinalCost() ko call karta hai. Subclasses
    is method ko override karke apni custom pricing laga sakte hain
    jaise emergency surcharge ya follow-up discount.
    */
    @Override
    public double getCost() { return calculateFinalCost(); }

    protected double calculateFinalCost() { return baseCost; }

    @Override
    public abstract String executeService();

    /*
    Service complete hone par status COMPLETED set hoti hai aur
    current time record ho jaata hai.
    */
    public void markCompleted() {
        this.status      = "COMPLETED";
        this.performedAt = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public void markCancelled() { this.status = "CANCELLED"; }

    public String getServiceId()          { return serviceId; }
    public double getBaseCost()           { return baseCost; }
    public String getStatus()             { return status; }
    public String getPerformedAt()        { return performedAt; }
    public String getAssignedDoctorName() { return assignedDoctorName; }
    public String getPatientId()          { return patientId; }

    public void setAssignedDoctorName(String name) { this.assignedDoctorName = name; }
    public void setPatientId(String id)            { this.patientId          = id; }
    public void setBaseCost(double cost)           { this.baseCost           = cost; }
    public void setStatus(String status)           { this.status             = status; }

    @Override
    public String toString() {
        return serviceName + " [" + category + "] - Rs " + getCost() + " | " + status;
    }
}