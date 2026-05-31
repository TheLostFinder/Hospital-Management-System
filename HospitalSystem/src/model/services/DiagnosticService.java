package model.services;

/*
DiagnosticService lab tests aur imaging ko represent karta hai
jaise blood test, X-ray, MRI wagera. Har test type ki apni
alag base price hai aur urgent test pe 30 percent extra charge
lagta hai. executeService() mein har test ka simulated result
generate hota hai.
*/
public class DiagnosticService extends MedicalService {

    private String  testType;
    private String  testResult;
    private boolean isUrgent;

    public DiagnosticService(String serviceId, String testType, boolean isUrgent) {
        super(serviceId,
                testType.replace("_", " "),
                "DIAGNOSTICS",
                getBasePrice(testType),
                "Diagnostic test: " + testType.replace("_", " "));
        this.testType   = testType;
        this.testResult = "Pending";
        this.isUrgent   = isUrgent;
    }

    /*
    Har test type ki alag fixed base price hai jo yahan
    set ki gayi hai.
    */
    private static double getBasePrice(String type) {
        switch (type.toUpperCase()) {
            case "BLOOD_TEST": return 800;
            case "XRAY":       return 1200;
            case "MRI":        return 8000;
            case "CT_SCAN":    return 6000;
            case "URINE":      return 500;
            case "ECG":        return 1500;
            default:           return 1000;
        }
    }

    /*
    Test type ke hisaab se simulated result generate karta hai
    aur service ko complete mark karta hai.
    */
    @Override
    public String executeService() {
        switch (testType.toUpperCase()) {
            case "BLOOD_TEST":
                testResult = "CBC: Normal | WBC: 7.2 | RBC: 4.8 | HB: 13.5 g/dL";
                break;
            case "XRAY":
                testResult = "No fractures observed. Lungs clear.";
                break;
            case "MRI":
                testResult = "No abnormal lesions detected. Normal brain structure.";
                break;
            case "CT_SCAN":
                testResult = "No internal bleeding. Organs appear normal.";
                break;
            case "URINE":
                testResult = "Normal - pH 6.5, no protein/glucose detected.";
                break;
            case "ECG":
                testResult = "Normal sinus rhythm. No ST changes.";
                break;
            default:
                testResult = "Test completed - results normal.";
        }
        markCompleted();
        return "Diagnostic '" + getServiceName() + "' completed.\nResult: " + testResult;
    }

    @Override
    protected double calculateFinalCost() {
        double base = getBaseCost();
        return isUrgent ? base * 1.3 : base;
    }

    public String  getTestResult() { return testResult; }
    public String  getTestType()   { return testType; }
    public boolean isUrgent()      { return isUrgent; }

    public void setTestResult(String result) { this.testResult = result; }
}