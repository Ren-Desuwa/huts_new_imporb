package models;

public class Account {
    private String id;
    private String userId;
    private String type; // e.g., "electricity", "water"
    private String provider;
    private String accountNumber;
    private double ratePerUnit;

    public Account(String id, String userId, String type, String provider, String accountNumber, double ratePerUnit) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.provider = provider;
        this.accountNumber = accountNumber;
        this.ratePerUnit = ratePerUnit;
    }

    // Getters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getType() { return type; }
    public String getProvider() { return provider; }
    public String getAccountNumber() { return accountNumber; }
    public double getRatePerUnit() { return ratePerUnit; }

    // Setters
    public void setProvider(String provider) { this.provider = provider; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public void setRatePerUnit(double ratePerUnit) { this.ratePerUnit = ratePerUnit; }
}
