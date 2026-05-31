package model.billing;

import model.services.MedicalService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/*
 Bill class patient ke liye generate hone wala invoice represent
 karta hai. Ismein tamam services ki line items, tax aur discount
 ka calculation hota hai. Har bill ka status PENDING se PAID tak
 update ho sakta hai.
 */
public class Bill {

    private String         billId;
    private String         patientId;
    private String         patientName;
    private List<BillItem> items;
    private double         taxRate;
    private double         discountRate;
    private String         status;
    private String         generatedAt;
    private String         paidAt;

    public Bill(String billId, String patientId, String patientName) {
        this.billId       = billId;
        this.patientId    = patientId;
        this.patientName  = patientName;
        this.items        = new ArrayList<>();
        this.taxRate      = 0.05;
        this.discountRate = 0.0;
        this.status       = "PENDING";
        this.generatedAt  = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.paidAt       = "";
    }

    public void addService(MedicalService service) {
        items.add(new BillItem(service.getServiceName(), service.getCategory(), service.getCost()));
    }

    public void addItem(String name, String category, double amount) {
        items.add(new BillItem(name, category, amount));
    }

    public double getSubtotal()       { return items.stream().mapToDouble(BillItem::getAmount).sum(); }
    public double getDiscountAmount() { return getSubtotal() * discountRate; }
    public double getTaxAmount()      { return (getSubtotal() - getDiscountAmount()) * taxRate; }
    public double getTotalAmount()    { return getSubtotal() - getDiscountAmount() + getTaxAmount(); }

    public void markPaid() {
        this.status = "PAID";
        this.paidAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public void markPending()       { this.status = "PENDING"; }
    public void markPartiallyPaid() { this.status = "PARTIALLY_PAID"; }

    /*
 Bill ka formatted receipt text return karta hai jisme
 tamam items, subtotal, tax, discount aur final total
 clearly dikh sakein.
 */
    public String getDetailedReceipt() {
        StringBuilder sb = new StringBuilder();
        sb.append("Bill ID   : ").append(billId).append("\n");
        sb.append("Patient   : ").append(patientName).append(" (").append(patientId).append(")\n");
        sb.append("Generated : ").append(generatedAt).append("\n");
        sb.append("Status    : ").append(status).append("\n");
        sb.append("-------------------------------------\n");
        sb.append(String.format("%-24s %-12s %10s%n", "ITEM", "CATEGORY", "AMOUNT"));
        sb.append("-------------------------------------\n");
        items.forEach(i -> sb.append(String.format("%-24s %-12s %10.2f%n",
                trunc(i.getName(), 23), trunc(i.getCategory(), 11), i.getAmount())));
        sb.append("-------------------------------------\n");
        sb.append(String.format("%-36s %10.2f%n", "Subtotal:", getSubtotal()));
        sb.append(String.format("%-36s %10.2f%n", "Discount (" + ((int)(discountRate*100)) + "%):", getDiscountAmount()));
        sb.append(String.format("%-36s %10.2f%n", "Tax (" + ((int)(taxRate*100)) + "%):", getTaxAmount()));
        sb.append("=====================================\n");
        sb.append(String.format("%-36s %10.2f%n", "TOTAL (Rs):", getTotalAmount()));
        sb.append("=====================================\n");
        if (!paidAt.isEmpty()) sb.append("Paid At   : ").append(paidAt).append("\n");
        return sb.toString();
    }

    private String trunc(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max - 1) + "...";
    }

    public String         getBillId()       { return billId; }
    public String         getPatientId()    { return patientId; }
    public String         getPatientName()  { return patientName; }
    public List<BillItem> getItems()        { return items; }
    public String         getStatus()       { return status; }
    public String         getGeneratedAt()  { return generatedAt; }
    public double         getTaxRate()      { return taxRate; }
    public double         getDiscountRate() { return discountRate; }
    public String         getPaidAt()       { return paidAt; }

    public void setTaxRate(double r)      { this.taxRate      = r; }
    public void setDiscountRate(double r) { this.discountRate = r; }
    public void setStatus(String s)       { this.status       = s; }

    /*
 BillItem bill ke andar ek single line entry hai. Har service
 ya charge alag BillItem ke tor par store hota hai.
 */
    public static class BillItem {
        private String name;
        private String category;
        private double amount;

        public BillItem(String name, String category, double amount) {
            this.name     = name;
            this.category = category;
            this.amount   = amount;
        }

        public String getName()     { return name; }
        public String getCategory() { return category; }
        public double getAmount()   { return amount; }
    }
}