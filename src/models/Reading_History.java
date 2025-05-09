package models;

import java.time.LocalDate;

public class Reading_History {
    private int id;
    private int accountId;
    private LocalDate readingDate;
    private double readingValue;
    
    // Constructor with all fields
    public Reading_History(int id, int accountId, LocalDate readingDate, double readingValue) {
        this.id = id;
        this.accountId = accountId;
        this.readingDate = readingDate;
        this.readingValue = readingValue;
    }
    
    // Constructor without ID (for new readings)
    public Reading_History(int accountId, LocalDate readingDate, double readingValue) {
        this.id = -1; // Will be set by database
        this.accountId = accountId;
        this.readingDate = readingDate;
        this.readingValue = readingValue;
    }
    
    // Constructor with current date
    public Reading_History(int accountId, double readingValue) {
        this.id = -1; // Will be set by database
        this.accountId = accountId;
        this.readingDate = LocalDate.now();
        this.readingValue = readingValue;
    }
    
    // Getters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getAccountId() { return accountId; }
    
    public LocalDate getReadingDate() { return readingDate; }
    public void setReadingDate(LocalDate readingDate) { this.readingDate = readingDate; }
    
    public double getReadingValue() { return readingValue; }
    public void setReadingValue(double readingValue) { this.readingValue = readingValue; }
    
    // Helper method to calculate consumption between this reading and a previous one
    public double calculateConsumption(Reading_History previousReading) {
        if (previousReading == null) {
            return 0.0;
        }
        return this.readingValue - previousReading.getReadingValue();
    }
    
    // Helper method to get days between readings
    public long daysSincePreviousReading(Reading_History previousReading) {
        if (previousReading == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(previousReading.getReadingDate(), this.readingDate);
    }
    
    // Calculate average daily consumption since previous reading
    public double calculateDailyAverage(Reading_History previousReading) {
        long days = daysSincePreviousReading(previousReading);
        if (days <= 0) {
            return 0.0;
        }
        double consumption = calculateConsumption(previousReading);
        return consumption / days;
    }
    
    @Override
    public String toString() {
        return "Reading on " + readingDate + ": " + readingValue;
    }
}