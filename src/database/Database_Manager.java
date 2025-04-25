package database;

import models.*;
import java.sql.*;
import java.time.LocalDate;

/**
 * Core Database Manager that initializes the connection and delegates operations
 * to specialized managers for users, accounts, bills, reading history, and chores.
 */
public class Database_Manager {
    private Connection connection;
    private static final String DB_URL = "jdbc:sqlite:data/huts.db";

    // Specialized managers
    private User_Manager userManager;
    private Account_Manager accountManager;
    private Bill_Manager billManager;
    private Reading_History_Manager readingHistoryManager;
    private Chore_Manager choreManager;

    // Singleton pattern
    private static Database_Manager instance;

    public static Database_Manager getInstance() {
        if (instance == null) {
            instance = new Database_Manager();
        }
        return instance;
    }

    private Database_Manager() {
        try {
            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");

            // Create connection
            connection = DriverManager.getConnection(DB_URL);

            // Create tables
            createTables();

            // Initialize managers
            initializeManagers();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void initializeManagers() {
        userManager = new User_Manager(connection);
        accountManager = new Account_Manager(connection);
        billManager = new Bill_Manager(connection);
        readingHistoryManager = new Reading_History_Manager(connection);
        choreManager = new Chore_Manager(connection);
    }

    private void createTables() {
        try (Statement stmt = connection.createStatement()) {
            // Users table - Update to use INTEGER IDs
            stmt.execute("CREATE TABLE IF NOT EXISTS users ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "username TEXT UNIQUE, "
                    + "password TEXT, "
                    + "email TEXT, "
                    + "full_name TEXT)");

            // Accounts table - Update to use INTEGER IDs
            stmt.execute("CREATE TABLE IF NOT EXISTS accounts ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "user_id INTEGER NOT NULL, "
                    + "type TEXT NOT NULL, "    // e.g., 'electricity', 'gas', 'water'
                    + "provider TEXT, "
                    + "account_number TEXT, "
                    + "rate_per_unit REAL, "
                    + "FOREIGN KEY(user_id) REFERENCES users(id))");

            // Bills table - Update to use INTEGER IDs
            stmt.execute("CREATE TABLE IF NOT EXISTS bills ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "account_id INTEGER NOT NULL, "
                    + "start_reading REAL, "
                    + "end_reading REAL, "
                    + "consumption REAL, "
                    + "amount REAL, "
                    + "issue_date TEXT, "
                    + "due_date TEXT, "
                    + "is_paid INTEGER, "
                    + "paid_date TEXT, "
                    + "notes TEXT, "
                    + "FOREIGN KEY(account_id) REFERENCES accounts(id))");

            // Reading history table - Update to use INTEGER IDs
            stmt.execute("CREATE TABLE IF NOT EXISTS reading_history ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "account_id INTEGER NOT NULL, "
                    + "reading_date TEXT, "
                    + "reading_value REAL, "
                    + "FOREIGN KEY(account_id) REFERENCES accounts(id))");

            // Chores table (already using INTEGER IDs, no change needed)
            stmt.execute("CREATE TABLE IF NOT EXISTS chores ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "user_id INTEGER NOT NULL, "
                    + "chore_name TEXT NOT NULL, "
                    + "description TEXT, "
                    + "due_date TEXT, "
                    + "completion_date TEXT, "
                    + "completed INTEGER NOT NULL, "
                    + "frequency TEXT, "
                    + "assigned_to TEXT, "
                    + "priority INTEGER)");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Getters for specialized managers
    public User_Manager getUserManager() {
        return userManager;
    }

    public Account_Manager getAccountManager() {
        return accountManager;
    }

    public Bill_Manager getBillManager() {
        return billManager;
    }

    public Reading_History_Manager getReadingHistoryManager() {
        return readingHistoryManager;
    }
    
    public Chore_Manager getChoreManager() {
        return choreManager;
    }

    public Connection getConnection() {
        return this.connection;
    }

    // Close connection
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}