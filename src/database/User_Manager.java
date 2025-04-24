package database;

import models.User;
import java.sql.*;
import java.util.UUID;

public class User_Manager {
    private Connection connection;
    
    public User_Manager(Connection connection) {
        this.connection = connection;
    }
    
    public boolean saveUser(User user) {
        String id = UUID.randomUUID().toString();
        String sql = "INSERT INTO users (id, username, password, email, full_name) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, user.getPassword()); // Note: In a real app, passwords should be hashed
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getFullName());
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                user.setId(id); // Set the generated ID back to the user object
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Add method for Sign_Up_Panel to add a new user
    public boolean addUser(User user) {
        // This is essentially the same as saveUser but with a different name
        // as referenced in Sign_Up_Panel
        return saveUser(user);
    }
    
    // Add method for Sign_Up_Panel and Login_Panel to check if a user exists
    public boolean userExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Add method for Login_Panel to get a user by username
    public User getUser(String username) {
        return getUserByUsername(username);
    }
    
    // Add method for Forgot_Password_Panel to update a user's password
    public boolean updateUserPassword(String username, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE username = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newPassword); // Note: In a real app, passwords should be hashed
            pstmt.setString(2, username);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean authenticateUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password); // Note: In a real app, passwords should be hashed
            
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // Return true if a matching user is found
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Add a method to get a user by username and password
    public User getUserByCredentials(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password); // Note: In a real app, passwords should be hashed
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String id = rs.getString("id");
                String email = rs.getString("email");
                String fullName = rs.getString("full_name");
                
                User user = new User(id, username, password, email, fullName);
                return user;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public User getUserById(String id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                String email = rs.getString("email");
                String fullName = rs.getString("full_name");
                
                return new User(id, username, password, email, fullName);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String id = rs.getString("id");
                String password = rs.getString("password");
                String email = rs.getString("email");
                String fullName = rs.getString("full_name");
                
                return new User(id, username, password, email, fullName);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET username = ?, password = ?, email = ?, full_name = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getFullName());
            pstmt.setString(5, user.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteUser(String id) {
        String sql = "DELETE FROM users WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
 // Get the highest user ID currently in the database
    public String getHighestUserId() {
        String sql = "SELECT id FROM users ORDER BY id DESC LIMIT 1";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getString("id");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // If no users exist yet, return a starting ID
        return "USER-0000";
    }

    // Generate a new sequential ID based on the highest existing ID
    public String generateNextUserId() {
        String highestId = getHighestUserId();
        
        if (highestId.startsWith("USER-")) {
            try {
                // Extract the numeric part
                int numericPart = Integer.parseInt(highestId.substring(5));
                // Increment by 1
                numericPart++;
                // Format the new ID with leading zeros
                return String.format("USER-%04d", numericPart);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        
        // If there was an issue with the format, fall back to "USER-0001"
        return "USER-0001";
    }
}