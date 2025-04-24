package database;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

import models.Bill;

public class Bill_Manager {
    private final Connection conn;

    public Bill_Manager(Connection conn) {
        this.conn = conn;
    }

    public void createBill(Bill bill) throws SQLException {
        String sql = "INSERT INTO bills(id,account_id,start_reading,end_reading,consumption,amount,issue_date,due_date,is_paid,paid_date,notes) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bill.getId() != null ? bill.getId() : UUID.randomUUID().toString());
            ps.setString(2, bill.getAccountId());
            ps.setDouble(3, bill.getStartReading());
            ps.setDouble(4, bill.getEndReading());
            ps.setDouble(5, bill.getConsumption());
            ps.setDouble(6, bill.getAmount());
            ps.setString(7, bill.getIssueDate().toString());
            ps.setString(8, bill.getDueDate().toString());
            ps.setInt(9, bill.isPaid() ? 1 : 0);
            ps.setString(10, bill.getPaidDate() != null ? bill.getPaidDate().toString() : null);
            ps.setString(11, bill.getNotes());
            ps.executeUpdate();
        }
    }

    public Bill getBillById(String id) throws SQLException {
        String sql = "SELECT * FROM bills WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Bill(
                        rs.getString("id"),
                        rs.getString("account_id"),
                        rs.getDouble("start_reading"),
                        rs.getDouble("end_reading"),
                        rs.getDouble("consumption"),
                        rs.getDouble("amount"),
                        LocalDate.parse(rs.getString("issue_date")),
                        LocalDate.parse(rs.getString("due_date")),
                        rs.getInt("is_paid") == 1,
                        rs.getString("paid_date") != null ? LocalDate.parse(rs.getString("paid_date")) : null,
                        rs.getString("notes")
                    );
                }
                return null;
            }
        }
    }

    public List<Bill> getBillsByAccountId(String accountId) throws SQLException {
        String sql = "SELECT * FROM bills WHERE account_id = ?";
        List<Bill> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Bill(
                        rs.getString("id"),
                        rs.getString("account_id"),
                        rs.getDouble("start_reading"),
                        rs.getDouble("end_reading"),
                        rs.getDouble("consumption"),
                        rs.getDouble("amount"),
                        LocalDate.parse(rs.getString("issue_date")),
                        LocalDate.parse(rs.getString("due_date")),
                        rs.getInt("is_paid") == 1,
                        rs.getString("paid_date") != null ? LocalDate.parse(rs.getString("paid_date")) : null,
                        rs.getString("notes")
                    ));
                }
            }
        }
        return list;
    }

    public void updateBill(Bill bill) throws SQLException {
        String sql = "UPDATE bills SET end_reading=?,consumption=?,amount=?,is_paid=?,paid_date=?,notes=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, bill.getEndReading());
            ps.setDouble(2, bill.getConsumption());
            ps.setDouble(3, bill.getAmount());
            ps.setInt(4, bill.isPaid() ? 1 : 0);
            ps.setString(5, bill.getPaidDate() != null ? bill.getPaidDate().toString() : null);
            ps.setString(6, bill.getNotes());
            ps.setString(7, bill.getId());
            ps.executeUpdate();
        }
    }

    public void deleteBill(String id) throws SQLException {
        String sql = "DELETE FROM bills WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        }
    }
}