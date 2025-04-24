package models;

import java.time.LocalDate;
import java.util.UUID;

public class Bill {
    private String id;
    private String accountId;
    private double startReading;
    private double endReading;
    private double consumption;
    private double amount;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private boolean isPaid;
    private LocalDate paidDate;
    private String notes;
    
    // Constructor with all fields
    public Bill(String id, String accountId, double startReading, double endReading, 
                double consumption, double amount, LocalDate issueDate, 
                LocalDate dueDate, boolean isPaid, LocalDate paidDate, String notes) {
        this.id = id;
        this.accountId = accountId;
        this.startReading = startReading;
        this.endReading = endReading;
        this.consumption = consumption;
        this.amount = amount;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.isPaid = isPaid;
        this.paidDate = paidDate;
        this.notes = notes;
    }
    
    // Constructor without ID (for new bills)
    public Bill(String accountId, double startReading, double endReading, 
                double amount, LocalDate issueDate, LocalDate dueDate) {
        this.id = UUID.randomUUID().toString();
        this.accountId = accountId;
        this.startReading = startReading;
        this.endReading = endReading;
        this.consumption = endReading - startReading;
        this.amount = amount;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.isPaid = false;
        this.paidDate = null;
        this.notes = "";
    }
    
    // Getters and setters
    public String getId() { return id; }
    
    public String getAccountId() { return accountId; }
    
    public double getStartReading() { return startReading; }
    public void setStartReading(double startReading) { 
        this.startReading = startReading; 
        this.consumption = this.endReading - startReading;
    }
    
    public double getEndReading() { return endReading; }
    public void setEndReading(double endReading) { 
        this.endReading = endReading; 
        this.consumption = endReading - this.startReading;
    }
    
    public double getConsumption() { return consumption; }
    public void setConsumption(double consumption) { this.consumption = consumption; }
    
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
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
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