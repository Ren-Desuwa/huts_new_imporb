package views;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.util.*;
import java.text.SimpleDateFormat;

import models.User;
import database.Database_Manager;
import ignoreme.Electricity;
import ignoreme.Electricity_Manager;

public class Electricity_Panel implements Utility_Panel {
    private JPanel electricityPanel;
    private Main_Frame parentFrame;
    private java.util.List<Electricity> electricityAccounts;
    private Map<String, Double> previousElectricityReadings;
    private Electricity_Manager electricityManager;
    private User currentUser;
    
    // UI Components
    private JTextField dateField;
    private JTextField amountField;
    private JTextField kwhField;
    private JTextField totalSpentField;
    private JTextField avgMonthlyField;
    private DefaultTableModel historyTableModel;
    private JTable historyTable;
    
    public Electricity_Panel(Main_Frame parentFrame, Map<String, Double> previousReadings, User currentUser) {
        this.parentFrame = parentFrame;
        this.previousElectricityReadings = previousReadings;
        this.currentUser = currentUser;
        Connection connection = Database_Manager.getInstance().getConnection();
        this.electricityManager = new Electricity_Manager(connection);
        
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
        
        // New Bill Form
        JPanel addBillPanel = new JPanel(new GridBagLayout());
        addBillPanel.setBackground(new Color(255, 235, 180));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Title
        JLabel titleLabel = new JLabel("Add New Electricity Bill");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        addBillPanel.add(titleLabel, gbc);
        
        // Date field
        JLabel dateLabel = new JLabel("Date");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        addBillPanel.add(dateLabel, gbc);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dateField = new JTextField(sdf.format(new Date()), 10);
        gbc.gridx = 1;
        gbc.gridy = 1;
        addBillPanel.add(dateField, gbc);
        
        // Amount field
        JLabel amountLabel = new JLabel("Amount ($)");
        gbc.gridx = 0;
        gbc.gridy = 2;
        addBillPanel.add(amountLabel, gbc);
        
        amountField = new JTextField("1200", 10);
        gbc.gridx = 1;
        gbc.gridy = 2;
        addBillPanel.add(amountField, gbc);
        
        // kWh Used field
        JLabel kwhLabel = new JLabel("kWh Used (optional)");
        gbc.gridx = 0;
        gbc.gridy = 3;
        addBillPanel.add(kwhLabel, gbc);
        
        kwhField = new JTextField(10);
        gbc.gridx = 1;
        gbc.gridy = 3;
        addBillPanel.add(kwhField, gbc);
        
        // Add Bill button
        JButton addButton = new JButton("+ Add Bill");
        addButton.setBackground(new Color(25, 25, 112));
        addButton.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        addButton.addActionListener(e -> addElectricityBill());
        addBillPanel.add(addButton, gbc);
        
        // Electric Bill History
        JPanel historyPanel = new JPanel(new BorderLayout(0, 10));
        historyPanel.setBackground(new Color(255, 235, 180));
        
        JLabel historyLabel = new JLabel("Electric Bill History");
        historyLabel.setFont(new Font("Arial", Font.BOLD, 18));
        historyPanel.add(historyLabel, BorderLayout.NORTH);
        
        // Table for bill history
        String[] columnNames = {"Date", "Amount", "kWh Used", "notes"};
        historyTableModel = new DefaultTableModel(columnNames, 0);
        historyTable = new JTable(historyTableModel);
        JScrollPane scrollPane = new JScrollPane(historyTable);
        historyPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Combine panels on left side
        leftPanel.add(addBillPanel, BorderLayout.NORTH);
        leftPanel.add(historyPanel, BorderLayout.CENTER);
        
        // RIGHT SIDE PANEL
        JPanel rightPanel = new JPanel(new BorderLayout(0, 20));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Histogram Panel
        JPanel histogramPanel = new JPanel(new BorderLayout(0, 10));
        histogramPanel.setBackground(Color.WHITE);
        
        JLabel histogramLabel = new JLabel("Histogram");
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
        
        totalSpentField = new JTextField("php 520");
        totalSpentField.setEditable(false);
        statsGbc.gridx = 0;
        statsGbc.gridy = 1;
        totalsPanel.add(totalSpentField, statsGbc);
        
        // Average Monthly Cost
        JLabel avgLabel = new JLabel("Average Monthly Cost");
        statsGbc.gridx = 0;
        statsGbc.gridy = 2;
        totalsPanel.add(avgLabel, statsGbc);
        
        avgMonthlyField = new JTextField("php 120");
        avgMonthlyField.setEditable(false);
        statsGbc.gridx = 0;
        statsGbc.gridy = 3;
        totalsPanel.add(avgMonthlyField, statsGbc);
        
        // Right stats (usage trend)
        JPanel trendPanel = new JPanel(new GridBagLayout());
        trendPanel.setBackground(new Color(255, 235, 180)); // Same yellow background
        GridBagConstraints trendGbc = new GridBagConstraints();
        trendGbc.fill = GridBagConstraints.HORIZONTAL;
        trendGbc.insets = new Insets(5, 5, 5, 5);
        
        JLabel trendLabel = new JLabel("Usage Trend");
        trendLabel.setFont(new Font("Arial", Font.BOLD, 14));
        trendGbc.gridx = 0;
        trendGbc.gridy = 0;
        trendGbc.gridwidth = 2;
        trendPanel.add(trendLabel, trendGbc);
        
        // January
        JLabel janPercentLabel = new JLabel("2%");
        janPercentLabel.setFont(new Font("Arial", Font.BOLD, 14));
        trendGbc.gridx = 0;
        trendGbc.gridy = 1;
        trendGbc.gridwidth = 1;
        trendPanel.add(janPercentLabel, trendGbc);
        
        JLabel janMonthLabel = new JLabel("Month of January");
        trendGbc.gridx = 1;
        trendGbc.gridy = 1;
        trendPanel.add(janMonthLabel, trendGbc);
        
        // February
        JLabel febPercentLabel = new JLabel("4%");
        febPercentLabel.setFont(new Font("Arial", Font.BOLD, 14));
        trendGbc.gridx = 0;
        trendGbc.gridy = 2;
        trendPanel.add(febPercentLabel, trendGbc);
        
        JLabel febMonthLabel = new JLabel("Month of Febuary");
        trendGbc.gridx = 1;
        trendGbc.gridy = 2;
        trendPanel.add(febMonthLabel, trendGbc);
        
        // March
        JLabel marPercentLabel = new JLabel("8%");
        marPercentLabel.setFont(new Font("Arial", Font.BOLD, 14));
        trendGbc.gridx = 0;
        trendGbc.gridy = 3;
        trendPanel.add(marPercentLabel, trendGbc);
        
        JLabel marMonthLabel = new JLabel("Month of March");
        trendGbc.gridx = 1;
        trendGbc.gridy = 3;
        trendPanel.add(marMonthLabel, trendGbc);
        
        // Add stats panels
        statsPanel.add(totalsPanel);
        statsPanel.add(trendPanel);
        
        // Combine panels on right side
        rightPanel.add(histogramPanel, BorderLayout.NORTH);
        rightPanel.add(statsPanel, BorderLayout.CENTER);
        
        // Add both sides to the content panel
        contentPanel.add(leftPanel);
        contentPanel.add(rightPanel);
        
        // Add content panel to main panel
        electricityPanel.add(contentPanel, BorderLayout.CENTER);
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
            electricityAccounts = electricityManager.getElectricityByUserId(currentUser.getId());
            
            // Populate the table with electricity accounts data
            for (Electricity account : electricityAccounts) {
                Object[] row = {
                    account.getName(),
                    "$" + account.getRatePerKwh() * account.getMeterReading(),
                    account.getMeterReading(),
                    ""
                };
                historyTableModel.addRow(row);
            }
            
            // Calculate and update statistics
            updateStatistics();
        } else {
            electricityAccounts = new ArrayList<>(); // Empty list if no user logged in
        }
        
        // Refresh UI components
        electricityPanel.revalidate();
        electricityPanel.repaint();
    }
    
    // Method to add a new electricity bill
    private void addElectricityBill() {
        // Check if user is logged in
        if (currentUser == null) {
            JOptionPane.showMessageDialog(parentFrame, 
                "Please log in to add an electricity bill.", 
                "Authentication Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            String date = dateField.getText();
            String amountStr = amountField.getText();
            String kwhStr = kwhField.getText();
            
            if (date.isEmpty() || amountStr.isEmpty()) {
                JOptionPane.showMessageDialog(parentFrame, 
                    "Date and Amount are required fields.", 
                    "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            double amount = Double.parseDouble(amountStr);
            double kwh = kwhStr.isEmpty() ? 0.0 : Double.parseDouble(kwhStr);
            
            // Create a new electricity record with basic details
            // In a real app, you would need to create a Bill entity separate from Account
            Electricity electricity = new Electricity("Bill " + date, "Provider", "ACC" + System.currentTimeMillis(), amount / (kwh > 0 ? kwh : 100));
            electricity.setMeterReading(kwh);
            
            // Save to database with current user's ID
            electricityManager.addElectricity(electricity, currentUser.getId());
            
            // Store the initial reading as the previous reading
            previousElectricityReadings.put(electricity.getAccountNumber(), kwh);
            
            // Clear form fields
            dateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            amountField.setText("");
            kwhField.setText("");
            
            // Refresh the panel
            refreshPanel();
            
            JOptionPane.showMessageDialog(parentFrame, 
                "Electricity bill added successfully!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(parentFrame, 
                "Please enter valid numbers for Amount and kWh.", 
                "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Method to update statistics
    private void updateStatistics() {
        double totalSpent = 0.0;
        
        for (Electricity account : electricityAccounts) {
            double previousReading = previousElectricityReadings.getOrDefault(account.getAccountNumber(), 0.0);
            double bill = account.calculateBill(previousReading);
            totalSpent += bill;
        }
        
        // Update statistics fields
        totalSpentField.setText("php " + (int)totalSpent);
        avgMonthlyField.setText("php " + (electricityAccounts.isEmpty() ? 0 : (int)(totalSpent / 4))); // Simple average
    }
    
    // Original methods kept for compatibility
    private void addElectricityAccount() {
        // Implementation as in original code
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
        
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel nameLabel = new JLabel("Account Name:");
        JTextField nameField = new JTextField(20);
        
        JLabel providerLabel = new JLabel("Provider:");
        JTextField providerField = new JTextField(20);
        
        JLabel accountLabel = new JLabel("Account Number:");
        JTextField accountField = new JTextField(20);
        
        JLabel rateLabel = new JLabel("Rate per kWh ($):");
        JTextField rateField = new JTextField(20);
        
        JLabel readingLabel = new JLabel("Initial Reading (kWh):");
        JTextField readingField = new JTextField(20);
        
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(providerLabel);
        formPanel.add(providerField);
        formPanel.add(accountLabel);
        formPanel.add(accountField);
        formPanel.add(rateLabel);
        formPanel.add(rateField);
        formPanel.add(readingLabel);
        formPanel.add(readingField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        JButton saveButton = new JButton("Save");
        
        cancelButton.addActionListener(e -> dialog.dispose());
        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                String provider = providerField.getText();
                String accountNumber = accountField.getText();
                double rate = Double.parseDouble(rateField.getText());
                double reading = Double.parseDouble(readingField.getText());
                
                Electricity electricity = new Electricity(name, provider, accountNumber, rate);
                electricity.setMeterReading(reading);
                
                // Save to database with current user's ID
                electricityManager.addElectricity(electricity, currentUser.getId());
                
                // Store the initial reading as the previous reading
                previousElectricityReadings.put(accountNumber, reading);
                
                // Refresh the data from the database
                electricityAccounts = electricityManager.getElectricityByUserId(currentUser.getId());
                
                dialog.dispose();
                refreshPanel(); // Refresh the panel
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Please enter valid numbers for rate and reading.", 
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private void updateElectricityReading() {
        // Implementation as in original code
        if (currentUser == null) {
            JOptionPane.showMessageDialog(parentFrame, 
                "Please log in to update electricity readings.", 
                "Authentication Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (electricityAccounts.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, 
                "No electricity accounts found for your user.", 
                "No Accounts", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Rest of the method remains unchanged
        JDialog dialog = new JDialog(parentFrame, "Update Electricity Reading", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel accountLabel = new JLabel("Select Account:");
        JComboBox<String> accountCombo = new JComboBox<>();
        
        for (Electricity account : electricityAccounts) {
            accountCombo.addItem(account.getName() + " (" + account.getAccountNumber() + ")");
        }
        
        JLabel readingLabel = new JLabel("New Reading (kWh):");
        JTextField readingField = new JTextField(20);
        
        formPanel.add(accountLabel);
        formPanel.add(accountCombo);
        formPanel.add(readingLabel);
        formPanel.add(readingField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        JButton saveButton = new JButton("Save");
        
        cancelButton.addActionListener(e -> dialog.dispose());
        saveButton.addActionListener(e -> {
            try {
                int index = accountCombo.getSelectedIndex();
                double newReading = Double.parseDouble(readingField.getText());
                
                Electricity selected = electricityAccounts.get(index);
                // Store current reading as previous reading
                previousElectricityReadings.put(selected.getAccountNumber(), selected.getMeterReading());
                selected.setMeterReading(newReading);
                
                // Update in database
                electricityManager.updateElectricity(selected);
                
                dialog.dispose();
                refreshPanel(); // Refresh the panel
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Please enter a valid number for the reading.", 
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private void calculateElectricityBill() {
        // Implementation as in original code
        if (currentUser == null) {
            JOptionPane.showMessageDialog(parentFrame, 
                "Please log in to calculate electricity bills.", 
                "Authentication Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (electricityAccounts.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, 
                "No electricity accounts found for your user.", 
                "No Accounts", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Rest of the method remains unchanged
        JDialog dialog = new JDialog(parentFrame, "Electricity Bill Calculation", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setLayout(new BorderLayout());
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel accountLabel = new JLabel("Select Account:");
        JComboBox<String> accountCombo = new JComboBox<>();
        
        for (Electricity account : electricityAccounts) {
            accountCombo.addItem(account.getName() + " (" + account.getAccountNumber() + ")");
        }
        
        JButton calculateButton = new JButton("Calculate");
        
        topPanel.add(accountLabel);
        topPanel.add(accountCombo);
        topPanel.add(calculateButton);
        
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        
        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        
        resultPanel.add(scrollPane, BorderLayout.CENTER);
        
        calculateButton.addActionListener(e -> {
            int index = accountCombo.getSelectedIndex();
            Electricity selected = electricityAccounts.get(index);
            double previousReading = previousElectricityReadings.getOrDefault(selected.getAccountNumber(), 0.0);
            double bill = selected.calculateBill(previousReading);
            double consumption = selected.getMeterReading() - previousReading;
            
            StringBuilder sb = new StringBuilder();
            sb.append("Bill Calculation:\n\n");
            sb.append("Account: ").append(selected.getName()).append("\n");
            sb.append("Provider: ").append(selected.getProvider()).append("\n");
            sb.append("Account Number: ").append(selected.getAccountNumber()).append("\n\n");
            sb.append("Previous Reading: ").append(previousReading).append(" kWh\n");
            sb.append("Current Reading: ").append(selected.getMeterReading()).append(" kWh\n");
            sb.append("Consumption: ").append(consumption).append(" kWh\n\n");
            sb.append("Rate: $").append(selected.getRatePerKwh()).append(" per kWh\n");
            sb.append("Total Bill: $").append(String.format("%.2f", bill)).append("\n");
            
            resultArea.setText(sb.toString());
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);
        
        dialog.add(topPanel, BorderLayout.NORTH);
        dialog.add(resultPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private void removeElectricityAccount() {
        // Implementation as in original code
        if (currentUser == null) {
            JOptionPane.showMessageDialog(parentFrame, 
                "Please log in to remove electricity accounts.", 
                "Authentication Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (electricityAccounts.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, 
                "No electricity accounts found for your user.", 
                "No Accounts", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Rest of the method remains unchanged
        JDialog dialog = new JDialog(parentFrame, "Remove Electricity Account", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel accountLabel = new JLabel("Select Account:");
        JComboBox<String> accountCombo = new JComboBox<>();
        
        for (Electricity account : electricityAccounts) {
            accountCombo.addItem(account.getName() + " (" + account.getAccountNumber() + ")");
        }
        
        formPanel.add(accountLabel);
        formPanel.add(accountCombo);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        JButton removeButton = new JButton("Remove");
        
        cancelButton.addActionListener(e -> dialog.dispose());
        removeButton.addActionListener(e -> {
            int index = accountCombo.getSelectedIndex();
            Electricity selected = electricityAccounts.get(index);
            
            // Confirm deletion
            int confirm = JOptionPane.showConfirmDialog(dialog,
                "Are you sure you want to remove the account: " + selected.getName() + "?",
                "Confirm Removal", JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                // Remove from database
                electricityManager.deleteElectricity(selected.getAccountNumber());
                
                // Remove from previous readings
                previousElectricityReadings.remove(selected.getAccountNumber());
                
                dialog.dispose();
                refreshPanel(); // Refresh the panel
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(removeButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    public java.util.List<Electricity> getElectricityAccounts() {
        return electricityAccounts;
    }
    
    public Map<String, Double> getPreviousReadings() {
        return previousElectricityReadings;
    }
    
    // Method to update the current user
    public void setCurrentUser(User user) {
        this.currentUser = user;
        refreshPanel(); // Refresh to show only the current user's data
    }
    
    // Method to clear the panel when user logs out
    public void clearUserData() {
        this.currentUser = null;
        this.electricityAccounts.clear();
        refreshPanel();
    }
    
}