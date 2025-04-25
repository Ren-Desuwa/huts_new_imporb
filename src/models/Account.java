package models;

public class Account {
    private int id;
    private int userId;
    private String type; // e.g., "electricity", "water"
    private String provider;
    private String accountNumber;
    private double ratePerUnit;

    public Account(int id, int userId, String type, String provider, String accountNumber, double ratePerUnit) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.provider = provider;
        this.accountNumber = accountNumber;
        this.ratePerUnit = ratePerUnit;
    }

    // Constructor for new accounts (without ID)
    public Account(int userId, String type, String provider, String accountNumber, double ratePerUnit) {
        this.id = -1; // Will be set by database
        this.userId = userId;
        this.type = type;
        this.provider = provider;
        this.accountNumber = accountNumber;
        this.ratePerUnit = ratePerUnit;
    }

    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getType() { return type; }
    public String getProvider() { return provider; }
    public String getAccountNumber() { return accountNumber; }
    public double getRatePerUnit() { return ratePerUnit; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setProvider(String provider) { this.provider = provider; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public void setRatePerUnit(double ratePerUnit) { this.ratePerUnit = ratePerUnit; }
}