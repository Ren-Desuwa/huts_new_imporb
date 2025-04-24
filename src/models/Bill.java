package models;

import java.time.LocalDate;
import java.util.UUID;

public class Bill {
    private String id; // Changed from UUID to String
    private String utilityId; // Changed from UUID to String
    private double amount;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private boolean isPaid;
    private LocalDate paidDate;
    private double consumption; // usage for the billing period
    
    // Constructor with String IDs
    public Bill(String id, String utilityId, double amount, double consumption, 
                LocalDate issueDate, LocalDate dueDate) {
        this.id = id;
        this.utilityId = utilityId;
        this.amount = amount;
        this.consumption = consumption;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.isPaid = false;
        this.paidDate = null;
    }
    
    // Constructor without ID (for new bills)
    public Bill(String utilityId, double amount, double consumption, 
                LocalDate issueDate, LocalDate dueDate) {
        this.id = UUID.randomUUID().toString(); // Generate a new ID string
        this.utilityId = utilityId;
        this.amount = amount;
        this.consumption = consumption;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.isPaid = false;
        this.paidDate = null;
    }
    
    // Getters and setters
    public String getId() { return id; }
    
    public String getUtilityId() { return utilityId; }
    
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    
    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
    
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    
    public boolean isPaid() { return isPaid; }
    public void setPaid(boolean paid) { 
        isPaid = paid;
        if (paid && paidDate == null) {
            paidDate = LocalDate.now();
        } else if (!paid) {
            paidDate = null;
        }
    }
    
    public LocalDate getPaidDate() { return paidDate; }
    public void setPaidDate(LocalDate paidDate) { this.paidDate = paidDate; }
    
    public double getConsumption() { return consumption; }
    public void setConsumption(double consumption) { this.consumption = consumption; }
    
    // Mark the bill as paid with the current date
    public void markAsPaid() {
        this.isPaid = true;
        this.paidDate = LocalDate.now();
    }
    
    // Calculate days until due
    public long daysUntilDue() {
        if (isPaid) return 0;
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
    }
    
    // Check if the bill is overdue
    public boolean isOverdue() {
        return !isPaid && LocalDate.now().isAfter(dueDate);
    }
}