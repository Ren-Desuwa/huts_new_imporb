package database;

import java.sql.*;
import java.util.*;

import models.Account;

public class Account_Manager {
    private final Connection conn;

    public Account_Manager(Connection conn) {
        this.conn = conn;
    }

    public void createAccount(Account account) throws SQLException {
        String sql = "INSERT INTO accounts(user_id,type,provider,account_number,rate_per_unit) VALUES(?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, account.getUserId());
            ps.setString(2, account.getType());
            ps.setString(3, account.getProvider());
            ps.setString(4, account.getAccountNumber());
            ps.setDouble(5, account.getRatePerUnit());
            ps.executeUpdate();
            
            // Get the generated ID
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    account.setId(rs.getInt(1));
                }
            }
        }
    }

    public Account getAccountById(int id) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Account(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("type"),
                        rs.getString("provider"),
                        rs.getString("account_number"),
                        rs.getDouble("rate_per_unit")
                    );
                }
                return null;
            }
        }
    }

    public List<Account> getAccountsByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE user_id = ?";
        List<Account> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Account(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("type"),
                        rs.getString("provider"),
                        rs.getString("account_number"),
                        rs.getDouble("rate_per_unit")
                    ));
                }
            }
        }
        return list;
    }

    public void updateAccount(Account account) throws SQLException {
        String sql = "UPDATE accounts SET provider=?,account_number=?,rate_per_unit=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, account.getProvider());
            ps.setString(2, account.getAccountNumber());
            ps.setDouble(3, account.getRatePerUnit());
            ps.setInt(4, account.getId());
            ps.executeUpdate();
        }
    }

    public void deleteAccount(int id) throws SQLException {
        String sql = "DELETE FROM accounts WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}