package database;

import models.Chore;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Chore_Manager {
    private Connection connection;

    public Chore_Manager(Connection connection) {
        this.connection = connection;
    }

    // Create a new chore
    public Chore createChore(Chore chore) throws SQLException {
        String sql = "INSERT INTO chores (user_id, chore_name, description, due_date, completion_date, " +
                     "completed, frequency, assigned_to, priority) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, chore.getUserId());
            pstmt.setString(2, chore.getChoreName());
            pstmt.setString(3, chore.getDescription());
            
            if (chore.getDueDate() != null) {
                pstmt.setDate(4, Date.valueOf(chore.getDueDate()));
            } else {
                pstmt.setNull(4, Types.DATE);
            }
            
            if (chore.getCompletionDate() != null) {
                pstmt.setDate(5, Date.valueOf(chore.getCompletionDate()));
            } else {
                pstmt.setNull(5, Types.DATE);
            }
            
            pstmt.setBoolean(6, chore.isCompleted());
            pstmt.setString(7, chore.getFrequency());
            pstmt.setString(8, chore.getAssignedTo());
            
            if (chore.getPriority() != null) {
                pstmt.setInt(9, chore.getPriority());
            } else {
                pstmt.setNull(9, Types.INTEGER);
            }
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    chore.setId(generatedKeys.getInt(1));
                }
            }
        }
        
        return chore;
    }

    // Get a chore by its ID
    public Chore getChoreById(int id) throws SQLException {
        String sql = "SELECT * FROM chores WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToChore(rs);
                }
            }
        }
        
        return null;
    }
    
    // Get all chores for a specific user
    public List<Chore> getChoresByUserId(Integer userId) throws SQLException {
        List<Chore> chores = new ArrayList<>();
        String sql = "SELECT * FROM chores WHERE user_id = ? ORDER BY due_date ASC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    chores.add(mapResultSetToChore(rs));
                }
            }
        }
        
        return chores;
    }

    // Get all pending chores for a user
    public List<Chore> getPendingChoresByUserId(int userId) throws SQLException {
        List<Chore> chores = new ArrayList<>();
        String sql = "SELECT * FROM chores WHERE user_id = ? AND completed = 0 ORDER BY due_date ASC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    chores.add(mapResultSetToChore(rs));
                }
            }
        }
        
        return chores;
    }

    // Update an existing chore
    public boolean updateChore(Chore chore) throws SQLException {
        String sql = "UPDATE chores SET chore_name = ?, description = ?, due_date = ?, " +
                     "completion_date = ?, completed = ?, frequency = ?, assigned_to = ?, " +
                     "priority = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, chore.getChoreName());
            pstmt.setString(2, chore.getDescription());
            
            if (chore.getDueDate() != null) {
                pstmt.setDate(3, Date.valueOf(chore.getDueDate()));
            } else {
                pstmt.setNull(3, Types.DATE);
            }
            
            if (chore.getCompletionDate() != null) {
                pstmt.setDate(4, Date.valueOf(chore.getCompletionDate()));
            } else {
                pstmt.setNull(4, Types.DATE);
            }
            
            pstmt.setBoolean(5, chore.isCompleted());
            pstmt.setString(6, chore.getFrequency());
            pstmt.setString(7, chore.getAssignedTo());
            
            if (chore.getPriority() != null) {
                pstmt.setInt(8, chore.getPriority());
            } else {
                pstmt.setNull(8, Types.INTEGER);
            }
            
            pstmt.setInt(9, chore.getId());
            
            return pstmt.executeUpdate() > 0;
        }
    }

    // Mark a chore as completed
    public boolean markChoreAsCompleted(int choreId) throws SQLException {
        String sql = "UPDATE chores SET completed = 1, completion_date = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(LocalDate.now()));
            pstmt.setInt(2, choreId);
            
            return pstmt.executeUpdate() > 0;
        }
    }

    // Delete a chore
    public boolean deleteChore(int choreId) throws SQLException {
        String sql = "DELETE FROM chores WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, choreId);
            
            return pstmt.executeUpdate() > 0;
        }
    }

    // Get statistics: count of pending chores
    public int getPendingChoresCount(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM chores WHERE user_id = ? AND completed = 0";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        
        return 0;
    }

    // Get statistics: count of completed chores in the given date range
    public int getCompletedChoresCount(int userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT COUNT(*) FROM chores WHERE user_id = ? AND completed = 1 " +
                     "AND completion_date BETWEEN ? AND ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setDate(2, Date.valueOf(startDate));
            pstmt.setDate(3, Date.valueOf(endDate));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        
        return 0;
    }

    // Helper method to map a ResultSet row to a Chore object
    private Chore mapResultSetToChore(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        Integer userId = rs.getInt("user_id");
        String choreName = rs.getString("chore_name");
        String description = rs.getString("description");
        
        LocalDate dueDate = null;
        if (rs.getDate("due_date") != null) {
            dueDate = rs.getDate("due_date").toLocalDate();
        }
        
        LocalDate completionDate = null;
        if (rs.getDate("completion_date") != null) {
            completionDate = rs.getDate("completion_date").toLocalDate();
        }
        
        boolean completed = rs.getBoolean("completed");
        String frequency = rs.getString("frequency");
        String assignedTo = rs.getString("assigned_to");
        
        Integer priority = rs.getInt("priority");
        if (rs.wasNull()) {
            priority = null;
        }
        
        return new Chore(id, userId, choreName, description, dueDate, completionDate, 
                         completed, frequency, assignedTo, priority);
    }
}