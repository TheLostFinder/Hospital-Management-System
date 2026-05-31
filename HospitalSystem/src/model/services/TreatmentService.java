package model.services;

/*
 TreatmentService kisi bhi qisam ke treatment ko represent karta
 hai jaise surgery, physiotherapy ya medication course. Cost
 complexity level aur duration dono ke basis par calculate hoti
 hai. Jitne zyada din treatment chalegi utni zyada cost hogi.
 */
public class TreatmentService extends MedicalService {

    public enum Complexity { LOW, MEDIUM, HIGH, CRITICAL }

    private String     treatmentType;
    private Complexity complexity;
    private int        durationDays;
    private String     medicationPrescribed;

    public TreatmentService(String serviceId, String treatmentType,
                            Complexity complexity, int durationDays) {
        super(serviceId,
                treatmentType,
                "TREATMENT",
                getBasePrice(complexity),
                "Treatment: " + treatmentType + " | Duration: " + durationDays + " day(s)");
        this.treatmentType        = treatmentType;
        this.complexity           = complexity;
        this.durationDays         = durationDays;
        this.medicationPrescribed = "";
    }

    private static double getBasePrice(Complexity c) {
        switch (c) {
            case LOW:      return 2000;
            case MEDIUM:   return 8000;
            case HIGH:     return 25000;
            case CRITICAL: return 75000;
            default:       return 5000;
        }
    }

    @Override
    public String executeService() {
        markCompleted();
        return "Treatment '" + treatmentType + "' administered.\n"
                + "Complexity: " + complexity + " | Duration: " + durationDays + " day(s)\n"
                + "Medication: " + (medicationPrescribed.isEmpty() ? "None prescribed." : medicationPrescribed);
    }

    /*
 Final cost base price aur daily rate ke combination se banti
 hai. Har din ke liye 500 Rs extra add hote hain.
 */
    @Override
    protected double calculateFinalCost() {
        return getBasePrice(complexity) + (durationDays * 500);
    }

    public String     getTreatmentType()        { return treatmentType; }
    public Complexity getComplexity()           { return complexity; }
    public int        getDurationDays()         { return durationDays; }
    public String     getMedicationPrescribed() { return medicationPrescribed; }

    public void setMedicationPrescribed(String med) { this.medicationPrescribed = med; }
}