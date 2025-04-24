package views.panels;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.*;

import models.User;
import views.Main_Frame;
import models.Account;
import models.Bill;
import models.Reading_History;
import database.Database_Manager;

public class Electricity_Panel implements Utility_Panel {
    private JPanel electricityPanel;
    private Main_Frame parentFrame;
    private List<Account> electricityAccounts;
    private List<Bill> electricityBills;
    private User currentUser;
    
    // Database managers
    private Database_Manager dbManager;
    
    // UI Components
    private JTextField dateField;
    private JTextField amountField;
    private JTextField readingField;
    private JTextField totalSpentField;
    private JTextField avgMonthlyField;
    private DefaultTableModel historyTableModel;
    private JTable historyTable;
    
    public Electricity_Panel(Main_Frame parentFrame, User currentUser) {
        this.parentFrame = parentFrame;
        this.currentUser = currentUser;
        this.dbManager = Database_Manager.getInstance();
        
        // Initialize the panel
        electricityPanel = new JPanel(new BorderLayout());
        electricityPanel.setBackground(new Color(240, 240, 240));
        
        createComponents();
        refreshPanel();
    }
    
    private void createComponents() {
        // Main content panel with 2-column layout
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // LEFT SIDE PANEL
        JPanel leftPanel = new JPanel(new BorderLayout(0, 20));
        leftPanel.setBackground(new Color(255, 235, 180)); // Light yellow background
        leftPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // New Reading Form
        JPanel addReadingPanel = new JPanel(new GridBagLayout());
        addReadingPanel.setBackground(new Color(255, 235, 180));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Title
        JLabel titleLabel = new JLabel("Add New Electricity Reading");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        addReadingPanel.add(titleLabel, gbc);
        
        // Date field
        JLabel dateLabel = new JLabel("Date");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        addReadingPanel.add(dateLabel, gbc);
        
        dateField = new JTextField(LocalDate.now().toString(), 10);
        gbc.gridx = 1;
        gbc.gridy = 1;
        addReadingPanel.add(dateField, gbc);
        
        // Account selection
        JLabel accountLabel = new JLabel("Account");
        gbc.gridx = 0;
        gbc.gridy = 2;
        addReadingPanel.add(accountLabel, gbc);
        
        JComboBox<String> accountComboBox = new JComboBox<>();
        gbc.gridx = 1;
        gbc.gridy = 2;
        addReadingPanel.add(accountComboBox, gbc);
        
        // Reading field
        JLabel readingLabel = new JLabel("Meter Reading");
        gbc.gridx = 0;
        gbc.gridy = 3;
        addReadingPanel.add(readingLabel, gbc);
        
        readingField = new JTextField(10);
        gbc.gridx = 1;
        gbc.gridy = 3;
        addReadingPanel.add(readingField, gbc);
        
        // Add Reading button
        JButton addButton = new JButton("+ Add Reading");
        addButton.setBackground(new Color(25, 25, 112));
        addButton.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        addButton.addActionListener(e -> addElectricityReading(accountComboBox.getSelectedIndex()));
        addReadingPanel.add(addButton, gbc);
        
        // Generate Bill button
        JButton generateButton = new JButton("Generate Bill");
        generateButton.setBackground(new Color(0, 100, 0));
        generateButton.setForeground(Color.WHITE);
        gbc.gridx = 1;
        gbc.gridy = 4;
        generateButton.addActionListener(e -> generateElectricityBill(accountComboBox.getSelectedIndex()));
        addReadingPanel.add(generateButton, gbc);
        
        // Add Account button
        JButton addAccountButton = new JButton("+ New Account");
        addAccountButton.setBackground(new Color(70, 130, 180));
        addAccountButton.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        addAccountButton.addActionListener(e -> addElectricityAccount());
        addReadingPanel.add(addAccountButton, gbc);
        
        // Electric Bill History
        JPanel historyPanel = new JPanel(new BorderLayout(0, 10));
        historyPanel.setBackground(new Color(255, 235, 180));
        
        JLabel historyLabel = new JLabel("Electric Bill History");
        historyLabel.setFont(new Font("Arial", Font.BOLD, 18));
        historyPanel.add(historyLabel, BorderLayout.NORTH);
        
        // Table for bill history
        String[] columnNames = {"Date", "Amount", "Start", "End", "Consumption", "Status"};
        historyTableModel = new DefaultTableModel(columnNames, 0);
        historyTable = new JTable(historyTableModel);
        JScrollPane scrollPane = new JScrollPane(historyTable);
        historyPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Combine panels on left side
        leftPanel.add(addReadingPanel, BorderLayout.NORTH);
        leftPanel.add(historyPanel, BorderLayout.CENTER);
        
        // RIGHT SIDE PANEL
        JPanel rightPanel = new JPanel(new BorderLayout(0, 20));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Histogram Panel
        JPanel histogramPanel = new JPanel(new BorderLayout(0, 10));
        histogramPanel.setBackground(Color.WHITE);
        
        JLabel histogramLabel = new JLabel("Consumption Overview");
        histogramLabel.setFont(new Font("Arial", Font.BOLD, 18));
        histogramPanel.add(histogramLabel, BorderLayout.NORTH);
        
        // Placeholder for histogram
        JPanel chartPanel = new JPanel();
        chartPanel.setPreferredSize(new Dimension(0, 250));
        chartPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        histogramPanel.add(chartPanel, BorderLayout.CENTER);
        
        // Stats Panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        statsPanel.setBackground(Color.WHITE);
        
        // Left stats (totals)
        JPanel totalsPanel = new JPanel(new GridBagLayout());
        totalsPanel.setBackground(new Color(255, 235, 180)); // Same yellow background
        GridBagConstraints statsGbc = new GridBagConstraints();
        statsGbc.fill = GridBagConstraints.HORIZONTAL;
        statsGbc.anchor = GridBagConstraints.WEST;
        statsGbc.insets = new Insets(5, 5, 5, 5);
        
        // Total Spent
        JLabel totalLabel = new JLabel("Total Spent (This Year)");
        statsGbc.gridx = 0;
        statsGbc.gridy = 0;
        totalsPanel.add(totalLabel, statsGbc);
        
        totalSpentField = new JTextField("$0.00");
        totalSpentField.setEditable(false);
        statsGbc.gridx = 0;
        statsGbc.gridy = 1;
        totalsPanel.add(totalSpentField, statsGbc);
        
        // Average Monthly Cost
        JLabel avgLabel = new JLabel("Average Monthly Cost");
        statsGbc.gridx = 0;
        statsGbc.gridy = 2;
        totalsPanel.add(avgLabel, statsGbc);
        
        avgMonthlyField = new JTextField("$0.00");
        avgMonthlyField.setEditable(false);
        statsGbc.gridx = 0;
        statsGbc.gridy = 3;
        totalsPanel.add(avgMonthlyField, statsGbc);
        
        // Right stats (usage trend)
        JPanel consumptionPanel = new JPanel(new GridBagLayout());
        consumptionPanel.setBackground(new Color(255, 235, 180)); // Same yellow background
        GridBagConstraints consumptionGbc = new GridBagConstraints();
        consumptionGbc.fill = GridBagConstraints.HORIZONTAL;
        consumptionGbc.insets = new Insets(5, 5, 5, 5);
        
        JLabel consumptionLabel = new JLabel("Consumption Stats");
        consumptionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        consumptionGbc.gridx = 0;
        consumptionGbc.gridy = 0;
        consumptionGbc.gridwidth = 2;
        consumptionPanel.add(consumptionLabel, consumptionGbc);
        
        // Add stats panels
        statsPanel.add(totalsPanel);
        statsPanel.add(consumptionPanel);
        
        // Combine panels on right side
        rightPanel.add(histogramPanel, BorderLayout.NORTH);
        rightPanel.add(statsPanel, BorderLayout.CENTER);
        
        // Add both sides to the content panel
        contentPanel.add(leftPanel);
        contentPanel.add(rightPanel);
        
        // Add content panel to main panel
        electricityPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Update the account combo box when refreshing
        refreshAccountComboBox(accountComboBox);
    }
    
    private void refreshAccountComboBox(JComboBox<String> comboBox) {
        comboBox.removeAllItems();
        
        if (currentUser != null && electricityAccounts != null) {
            for (Account account : electricityAccounts) {
                comboBox.addItem(account.getProvider() + " (" + account.getAccountNumber() + ")");
            }
        }
    }
    
    @Override
    public JPanel getPanel() {
        return electricityPanel;
    }
    
    @Override
    public void refreshPanel() {
        // Clear the table
        historyTableModel.setRowCount(0);
        
        // Fetch only the current user's data
        if (currentUser != null) {
            try {
                // Get all electricity accounts for the current user
                electricityAccounts = getElectricityAccounts();
                
                // Get bills for all electricity accounts
                electricityBills = new ArrayList<>();
                for (Account account : electricityAccounts) {
                    List<Bill> bills = dbManager.getBillManager().getBillsByAccountId(account.getId());
                    electricityBills.addAll(bills);
                }
                
                // Sort bills by issue date (most recent first)
                electricityBills.sort((b1, b2) -> b2.getIssueDate().compareTo(b1.getIssueDate()));
                
                // Populate the table with bill data
                for (Bill bill : electricityBills) {
                    String status = bill.isPaid() ? "Paid" : "Unpaid";
                    Object[] row = {
                        bill.getIssueDate().toString(),
                        String.format("$%.2f", bill.getAmount()),
                        String.format("%.2f", bill.getStartReading()),
                        String.format("%.2f", bill.getEndReading()),
                        String.format("%.2f", bill.getConsumption()),
                        status
                    };
                    historyTableModel.addRow(row);
                }
                
                // Calculate and update statistics
                updateStatistics();
                
                // Make sure to update any combo boxes in the UI
                Component[] components = electricityPanel.getComponents();
                for (Component component : components) {
                    if (component instanceof JPanel) {
                        findAndRefreshComboBox((JPanel) component);
                    }
                }
                
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(parentFrame, 
                    "Error fetching electricity data: " + e.getMessage(), 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } else {
            electricityAccounts = new ArrayList<>(); // Empty list if no user logged in
            electricityBills = new ArrayList<>();
        }
        
        // Refresh UI components
        electricityPanel.revalidate();
        electricityPanel.repaint();
    }
    
    // Helper method to find and refresh combo boxes in nested panels
    private void findAndRefreshComboBox(JPanel panel) {
        Component[] components = panel.getComponents();
        for (Component component : components) {
            if (component instanceof JComboBox) {
                refreshAccountComboBox((JComboBox<String>) component);
            } else if (component instanceof JPanel) {
                findAndRefreshComboBox((JPanel) component);
            }
        }
    }
    
    // Get all electricity accounts for the current user
    private List<Account> getElectricityAccounts() throws SQLException {
        List<Account> allAccounts = dbManager.getAccountManager().getAccountsByUserId(currentUser.getId());
        List<Account> electricityAccounts = new ArrayList<>();
        
        // Filter for electricity accounts only
        for (Account account : allAccounts) {
            if ("electricity".equalsIgnoreCase(account.getType())) {
                electricityAccounts.add(account);
            }
        }
        
        return electricityAccounts;
    }
    
    // Method to add a new electricity reading
    private void addElectricityReading(int selectedAccountIndex) {
        // Check if user is logged in
        if (currentUser == null) {
            JOptionPane.showMessageDialog(parentFrame, 
                "Please log in to add an electricity reading.", 
                "Authentication Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (selectedAccountIndex < 0 || electricityAccounts.size() <= selectedAccountIndex) {
            JOptionPane.showMessageDialog(parentFrame, 
                "Please select a valid account.", 
                "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            String dateStr = dateField.getText();
            String readingStr = readingField.getText();
            
            if (dateStr.isEmpty() || readingStr.isEmpty()) {
                JOptionPane.showMessageDialog(parentFrame, 
                    "Date and Meter Reading are required fields.", 
                    "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            LocalDate readingDate = LocalDate.parse(dateStr);
            double readingValue = Double.parseDouble(readingStr);
            
            // Get the selected account
            Account selectedAccount = electricityAccounts.get(selectedAccountIndex);
            
            // Create and save the new reading
            Reading_History newReading = new Reading_History(
                null, // ID will be generated
                selectedAccount.getId(),
                readingDate,
                readingValue
            );
            
            dbManager.getReadingHistoryManager().createReading(newReading);
            
            // Clear form fields
            dateField.setText(LocalDate.now().toString());
            readingField.setText("");
            
            // Refresh the panel
            refreshPanel();
            
            JOptionPane.showMessageDialog(parentFrame, 
                "Electricity reading added successfully!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(parentFrame, 
                "Please enter a valid number for the meter reading.", 
                "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(parentFrame, 
                "Please enter a valid date in YYYY-MM-DD format.", 
                "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(parentFrame, 
                "Database error: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    // Method to generate a bill from the latest readings
    private void generateElectricityBill(int selectedAccountIndex) {
        // Check if user is logged in
        if (currentUser == null) {
            JOptionPane.showMessageDialog(parentFrame, 
                "Please log in to generate an electricity bill.", 
                "Authentication Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (selectedAccountIndex < 0 || electricityAccounts.size() <= selectedAccountIndex) {
            JOptionPane.showMessageDialog(parentFrame, 
                "Please select a valid account.", 
                "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Get the selected account
            Account selectedAccount = electricityAccounts.get(selectedAccountIndex);
            
            // Get the latest reading
            Reading_History latestReading = dbManager.getReadingHistoryManager().getLatestReadingByAccountId(selectedAccount.getId());
            
            if (latestReading == null) {
                JOptionPane.showMessageDialog(parentFrame, 
                    "No readings available for this account. Please add at least one reading first.", 
                    "No Readings", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Get the previous reading
            Reading_History previousReading = dbManager.getReadingHistoryManager().getPreviousReading(
                selectedAccount.getId(), latestReading.getReadingDate());
            
            double startReading = (previousReading != null) ? previousReading.getReadingValue() : 0;
            double endReading = latestReading.getReadingValue();
            double consumption = endReading - startReading;
            double amount = consumption * selectedAccount.getRatePerUnit();
            
            // Ask user for due date and notes
            JDialog dialog = new JDialog(parentFrame, "Complete Bill Details", true);
            dialog.setSize(400, 300);
            dialog.setLocationRelativeTo(parentFrame);
            dialog.setLayout(new BorderLayout());
            
            JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
            formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            formPanel.add(new JLabel("Issue Date:"));
            JTextField issueDateField = new JTextField(LocalDate.now().toString());
            formPanel.add(issueDateField);
            
            formPanel.add(new JLabel("Due Date:"));
            JTextField dueDateField = new JTextField(LocalDate.now().plusDays(30).toString());
            formPanel.add(dueDateField);
            
            formPanel.add(new JLabel("Start Reading:"));
            JTextField startReadingField = new JTextField(String.valueOf(startReading));
            startReadingField.setEditable(false);
            formPanel.add(startReadingField);
            
            formPanel.add(new JLabel("End Reading:"));
            JTextField endReadingField = new JTextField(String.valueOf(endReading));
            endReadingField.setEditable(false);
            formPanel.add(endReadingField);
            
            formPanel.add(new JLabel("Notes:"));
            JTextField notesField = new JTextField();
            formPanel.add(notesField);
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton cancelButton = new JButton("Cancel");
            JButton generateButton = new JButton("Generate Bill");
            
            cancelButton.addActionListener(e -> dialog.dispose());
            generateButton.addActionListener(e -> {
                try {
                    LocalDate issueDate = LocalDate.parse(issueDateField.getText());
                    LocalDate dueDate = LocalDate.parse(dueDateField.getText());
                    String notes = notesField.getText();
                    
                    // Create a new bill
                    Bill newBill = new Bill(
                        null, // ID will be generated
                        selectedAccount.getId(),
                        startReading,
                        endReading,
                        consumption,
                        amount,
                        issueDate,
                        dueDate,
                        false, // not paid yet
                        null, // no paid date
                        notes
                    );
                    
                    // Save the bill
                    dbManager.getBillManager().createBill(newBill);
                    
                    dialog.dispose();
                    refreshPanel();
                    
                    JOptionPane.showMessageDialog(parentFrame, 
                        "Bill generated successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Please enter valid dates in YYYY-MM-DD format.", 
                        "Input Error", JOptionPane.ERROR_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Database error: " + ex.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            });
            
            buttonPanel.add(cancelButton);
            buttonPanel.add(generateButton);
            
            dialog.add(formPanel, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            
            dialog.setVisible(true);
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(parentFrame, 
                "Database error: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    // Method to update statistics
    private void updateStatistics() {
        double totalSpent = 0.0;
        int billCount = 0;
        
        // Calculate total spent and average bill
        for (Bill bill : electricityBills) {
            totalSpent += bill.getAmount();
            billCount++;
        }
        
        // Update statistics fields
        totalSpentField.setText(String.format("$%.2f", totalSpent));
        avgMonthlyField.setText(String.format("$%.2f", 
            billCount > 0 ? totalSpent / billCount : 0));
    }
    
    // Method to add a new electricity account
    private void addElectricityAccount() {
        // Check if user is logged in
        if (currentUser == null) {
            JOptionPane.showMessageDialog(parentFrame, 
                "Please log in to add an electricity account.", 
                "Authentication Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Create a dialog for adding a new electricity account
        JDialog dialog = new JDialog(parentFrame, "Add Electricity Account", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        formPanel.add(new JLabel("Provider:"));
        JTextField providerField = new JTextField(20);
        formPanel.add(providerField);
        
        formPanel.add(new JLabel("Account Number:"));
        JTextField accountNumberField = new JTextField(20);
        formPanel.add(accountNumberField);
        
        formPanel.add(new JLabel("Rate per kWh ($):"));
        JTextField rateField = new JTextField(20);
        formPanel.add(rateField);
        
        formPanel.add(new JLabel("Initial Reading (kWh):"));
        JTextField initialReadingField = new JTextField(20);
        formPanel.add(initialReadingField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        JButton saveButton = new JButton("Save");
        
        cancelButton.addActionListener(e -> dialog.dispose());
        saveButton.addActionListener(e -> {
            try {
                String provider = providerField.getText();
                String accountNumber = accountNumberField.getText();
                double rate = Double.parseDouble(rateField.getText());
                double initialReading = Double.parseDouble(initialReadingField.getText());
                
                if (provider.isEmpty() || accountNumber.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Provider and Account Number are required.", 
                        "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Create and save the new account
                Account newAccount = new Account(
                    null, // ID will be generated
                    currentUser.getId(),
                    "electricity", // type
                    provider,
                    accountNumber,
                    rate
                );
                
                dbManager.getAccountManager().createAccount(newAccount);
                
                // Get the created account to get its ID
                List<Account> accounts = dbManager.getAccountManager().getAccountsByUserId(currentUser.getId());
                Account createdAccount = null;
                for (Account acc : accounts) {
                    if (acc.getAccountNumber().equals(accountNumber) && acc.getType().equals("electricity")) {
                        createdAccount = acc;
                        break;
                    }
                }
                
                if (createdAccount != null && initialReading > 0) {
                    // Create initial reading
                    Reading_History initialReadingRecord = new Reading_History(
                        null, // ID will be generated
                        createdAccount.getId(),
                        LocalDate.now(),
                        initialReading
                    );
                    dbManager.getReadingHistoryManager().createReading(initialReadingRecord);
                }
                
                dialog.dispose();
                refreshPanel();
                
                JOptionPane.showMessageDialog(parentFrame, 
                    "Electricity account added successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Please enter valid numbers for rate and reading.", 
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Database error: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    // Method to update the current user
    public void setCurrentUser(User user) {
        this.currentUser = user;
        refreshPanel(); // Refresh to show only the current user's data
    }
    
    // Method to clear the panel when user logs out
    public void clearUserData() {
        this.currentUser = null;
        this.electricityAccounts = new ArrayList<>();
        this.electricityBills = new ArrayList<>();
        refreshPanel();
    }
}