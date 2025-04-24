package database;

import java.sql.*;
import java.util.*;

import models.Account;
import models.Bill;

class Account_Manager {
    private final Connection conn;

    public Account_Manager(Connection conn) {
        this.conn = conn;
    }

    public void createAccount(Account account) throws SQLException {
        String sql = "INSERT INTO accounts(id,user_id,type,provider,account_number,rate_per_unit) VALUES(?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, account.getId() != null ? account.getId() : UUID.randomUUID().toString());
            ps.setString(2, account.getUserId());
            ps.setString(3, account.getType());
            ps.setString(4, account.getProvider());
            ps.setString(5, account.getAccountNumber());
            ps.setDouble(6, account.getRatePerUnit());
            ps.executeUpdate();
        }
    }

    public Account getAccountById(String id) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Account(
                        rs.getString("id"),
                        rs.getString("user_id"),
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

    public List<Account> getAccountsByUserId(String userId) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE user_id = ?";
        List<Account> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Account(
                        rs.getString("id"),
                        rs.getString("user_id"),
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
            ps.setString(4, account.getId());
            ps.executeUpdate();
        }
    }

    public void deleteAccount(String id) throws SQLException {
        String sql = "DELETE FROM accounts WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        }
    }
}