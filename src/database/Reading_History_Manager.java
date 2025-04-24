package database;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

import models.Reading_History;

public class Reading_History_Manager {
    private final Connection conn;

    public Reading_History_Manager(Connection conn) {
        this.conn = conn;
    }

    public void createReading(Reading_History reading) throws SQLException {
        String sql = "INSERT INTO reading_history(id,account_id,reading_date,reading_value) VALUES(?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reading.getId() != null ? reading.getId() : UUID.randomUUID().toString());
            ps.setString(2, reading.getAccountId());
            ps.setString(3, reading.getReadingDate().toString());
            ps.setDouble(4, reading.getReadingValue());
            ps.executeUpdate();
        }
    }

    public Reading_History getReadingById(String id) throws SQLException {
        String sql = "SELECT * FROM reading_history WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Reading_History(
                        rs.getString("id"),
                        rs.getString("account_id"),
                        LocalDate.parse(rs.getString("reading_date")),
                        rs.getDouble("reading_value")
                    );
                }
                return null;
            }
        }
    }

    public List<Reading_History> getReadingsByAccountId(String accountId) throws SQLException {
        String sql = "SELECT * FROM reading_history WHERE account_id = ? ORDER BY reading_date ASC";
        List<Reading_History> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Reading_History(
                        rs.getString("id"),
                        rs.getString("account_id"),
                        LocalDate.parse(rs.getString("reading_date")),
                        rs.getDouble("reading_value")
                    ));
                }
            }
        }
        return list;
    }
    
    public List<Reading_History> getReadingsByAccountIdAndDateRange(String accountId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT * FROM reading_history WHERE account_id = ? AND reading_date >= ? AND reading_date <= ? ORDER BY reading_date ASC";
        List<Reading_History> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountId);
            ps.setString(2, startDate.toString());
            ps.setString(3, endDate.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Reading_History(
                        rs.getString("id"),
                        rs.getString("account_id"),
                        LocalDate.parse(rs.getString("reading_date")),
                        rs.getDouble("reading_value")
                    ));
                }
            }
        }
        return list;
    }

    public Reading_History getLatestReadingByAccountId(String accountId) throws SQLException {
        String sql = "SELECT * FROM reading_history WHERE account_id = ? ORDER BY reading_date DESC LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Reading_History(
                        rs.getString("id"),
                        rs.getString("account_id"),
                        LocalDate.parse(rs.getString("reading_date")),
                        rs.getDouble("reading_value")
                    );
                }
                return null;
            }
        }
    }
    
    public Reading_History getPreviousReading(String accountId, LocalDate beforeDate) throws SQLException {
        String sql = "SELECT * FROM reading_history WHERE account_id = ? AND reading_date < ? ORDER BY reading_date DESC LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountId);
            ps.setString(2, beforeDate.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Reading_History(
                        rs.getString("id"),
                        rs.getString("account_id"),
                        LocalDate.parse(rs.getString("reading_date")),
                        rs.getDouble("reading_value")
                    );
                }
                return null;
            }
        }
    }

    public void updateReading(Reading_History reading) throws SQLException {
        String sql = "UPDATE reading_history SET reading_date=?, reading_value=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reading.getReadingDate().toString());
            ps.setDouble(2, reading.getReadingValue());
            ps.setString(3, reading.getId());
            ps.executeUpdate();
        }
    }

    public void deleteReading(String id) throws SQLException {
        String sql = "DELETE FROM reading_history WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        }
    }
    
    // Generate a bill based on the latest and previous readings
    public models.Bill generateBillFromReadings(String accountId, double ratePerUnit, LocalDate dueDate) throws SQLException {
        Reading_History latestReading = getLatestReadingByAccountId(accountId);
        if (latestReading == null) {
            return null; // No readings available
        }
        
        // Get the previous reading to calculate consumption
        Reading_History previousReading = getPreviousReading(accountId, latestReading.getReadingDate());
        
        double startReading = (previousReading != null) ? previousReading.getReadingValue() : 0;
        double endReading = latestReading.getReadingValue();
        double consumption = endReading - startReading;
        double amount = consumption * ratePerUnit;
        
        // Create a new bill
        return new models.Bill(
            accountId,
            startReading,
            endReading,
            amount,
            LocalDate.now(),
            dueDate
        );
    }
    
    // Get statistics for an account
    public Map<String, Double> getAccountStatistics(String accountId) throws SQLException {
        Map<String, Double> stats = new HashMap<>();
        List<Reading_History> readings = getReadingsByAccountId(accountId);
        
        if (readings.isEmpty()) {
            return stats;
        }
        
        double totalConsumption = 0;
        double maxConsumption = 0;
        double avgDailyConsumption = 0;
        int consumptionPeriods = 0;
        
        Reading_History prev = null;
        for (Reading_History current : readings) {
            if (prev != null) {
                double consumption = current.calculateConsumption(prev);
                long days = current.daysSincePreviousReading(prev);
                
                if (days > 0) {
                    totalConsumption += consumption;
                    maxConsumption = Math.max(maxConsumption, consumption);
                    avgDailyConsumption += consumption / days;
                    consumptionPeriods++;
                }
            }
            prev = current;
        }
        
        stats.put("totalConsumption", totalConsumption);
        stats.put("maxConsumption", maxConsumption);
        stats.put("avgDailyConsumption", consumptionPeriods > 0 ? avgDailyConsumption / consumptionPeriods : 0);
        
        return stats;
    }
}