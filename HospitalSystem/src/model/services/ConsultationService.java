package model.services;

/*
ConsultationService doctor aur patient ke darmiyan consultation
represent karta hai. Consultation type ke hisaab se cost alag
hoti hai. Emergency consultation 50 percent mehngi hoti hai
jabke follow-up mein 20 percent discount milti hai.
*/
public class ConsultationService extends MedicalService {

    private String consultationType;
    private String doctorNotes;

    public ConsultationService(String serviceId, String doctorName,
                               String consultationType, double fee) {
        super(serviceId,
                "Consultation (" + consultationType + ")",
                "CONSULTATION",
                fee,
                "Medical consultation with Dr. " + doctorName);
        this.consultationType = consultationType;
        this.doctorNotes      = "";
        setAssignedDoctorName(doctorName);
    }

    @Override
    public String executeService() {
        markCompleted();
        return "Consultation completed with Dr. " + getAssignedDoctorName()
                + " | Type: " + consultationType
                + " | Notes: " + (doctorNotes.isEmpty() ? "Pending." : doctorNotes);
    }

    /*
    Consultation ki final cost type ke basis par calculate hoti
    hai. EMERGENCY pe 1.5x aur FOLLOW_UP pe 0.8x multiplier
    lagta hai.
    */
    @Override
    protected double calculateFinalCost() {
        double base = getBaseCost();
        if ("EMERGENCY".equalsIgnoreCase(consultationType)) return base * 1.5;
        if ("FOLLOW_UP".equalsIgnoreCase(consultationType))  return base * 0.8;
        return base;
    }

    public void setDoctorNotes(String notes) { this.doctorNotes      = notes; }
    public String getDoctorNotes()           { return doctorNotes; }
    public String getConsultationType()      { return consultationType; }
}