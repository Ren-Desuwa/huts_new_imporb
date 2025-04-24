package views;

import java.awt.*;
import java.sql.Connection;
import java.util.List;
import javax.swing.*;
import java.util.*;

import database.Database_Manager;
import ignoreme.Electricity_Manager;
import ignoreme.Water;
import ignoreme.Water_Manager;

public class Water_Panel implements Utility_Panel {
    private JPanel waterPanel;
    private Main_Frame parentFrame;
    private java.util.List<Water> waterAccounts;
    private Map<String, Double> previousWaterReadings;
    private Water_Manager waterManager;
    
    public Water_Panel(Main_Frame parentFrame, Map<String, Double> previousReadings) {
        this.parentFrame = parentFrame;
        this.previousWaterReadings = previousReadings;
        Connection connection = Database_Manager.getInstance().getConnection();
        this.waterManager = new Water_Manager(connection);
        
        // Initialize the panel
        waterPanel = new JPanel(new BorderLayout());
        waterPanel.setBackground(new Color(240, 240, 240));
        
        refreshPanel();
    }
    
    @Override
    public JPanel getPanel() {
        return waterPanel;
    }
    
    @Override
    public void refreshPanel() {
        // Clear the panel
        waterPanel.removeAll();
        
        // Fetch current data
        waterAccounts = waterManager.getAllWater();
        
        // Add title
        JLabel titleLabel = new JLabel("Water Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        
        // Create buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonsPanel.setBackground(new Color(240, 240, 240));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        
        JButton addButton = new JButton("Add Account");
        JButton updateButton = new JButton("Update Reading");
        JButton calculateButton = new JButton("Calculate Bill");
        JButton removeButton = new JButton("Remove Account");
        
        addButton.addActionListener(e -> addWaterAccount());
        updateButton.addActionListener(e -> updateWaterReading());
        calculateButton.addActionListener(e -> calculateWaterBill());
        removeButton.addActionListener(e -> removeWaterAccount());
        
        buttonsPanel.add(addButton);
        buttonsPanel.add(updateButton);
        buttonsPanel.add(calculateButton);
        buttonsPanel.add(removeButton);
        
        // Create table for data
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        String[] columnNames = {"Name", "Provider", "Account Number", "Current Reading", "Rate ($/kL)"};
        Object[][] data = new Object[waterAccounts.size()][5];
        
        for (int i = 0; i < waterAccounts.size(); i++) {
            Water account = waterAccounts.get(i);
            data[i][0] = account.getName();
            data[i][1] = account.getProvider();
            data[i][2] = account.getAccountNumber();
            data[i][3] = account.getMeterReading();
            data[i][4] = account.getRatePerCubicMeter();
        }
        
        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add components to water panel
        waterPanel.add(titleLabel, BorderLayout.NORTH);
        waterPanel.add(buttonsPanel, BorderLayout.SOUTH);
        waterPanel.add(tablePanel, BorderLayout.CENTER);
        
        waterPanel.revalidate();
        waterPanel.repaint();
    }

    private void removeWaterAccount() {
        if (waterAccounts.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, 
                "No water accounts found.", 
                "No Accounts", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Create a dialog for selecting an account to remove
        JDialog dialog = new JDialog(parentFrame, "Remove Water Account", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel accountLabel = new JLabel("Select Account:");
        JComboBox<String> accountCombo = new JComboBox<>();
        
        for (Water account : waterAccounts) {
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
            Water selected = waterAccounts.get(index);
            
            // Confirm deletion
            int confirm = JOptionPane.showConfirmDialog(dialog,
                "Are you sure you want to remove the account: " + selected.getName() + "?",
                "Confirm Removal", JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                // Remove from database
                waterManager.deleteWater(selected.getAccountNumber());
                
                // Remove from previous readings
                previousWaterReadings.remove(selected.getAccountNumber());
                
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
    
    private void addWaterAccount() {
        // Create a dialog for adding a new water account
        JDialog dialog = new JDialog(parentFrame, "Add Water Account", true);
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
        
        JLabel rateLabel = new JLabel("Rate per kL ($):");
        JTextField rateField = new JTextField(20);
        
        JLabel readingLabel = new JLabel("Initial Reading (kL):");
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
                
                Water water = new Water(name, provider, accountNumber, rate);
                water.setMeterReading(reading);
                
                // Save to database
                waterManager.saveWater(water);
                previousWaterReadings.put(accountNumber, reading);
                
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
    
    private void updateWaterReading() {
        if (waterAccounts.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, 
                "No water accounts found.", 
                "No Accounts", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Create a dialog for selecting an account and updating its reading
        JDialog dialog = new JDialog(parentFrame, "Update Water Reading", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel accountLabel = new JLabel("Select Account:");
        JComboBox<String> accountCombo = new JComboBox<>();
        
        for (Water account : waterAccounts) {
            accountCombo.addItem(account.getName() + " (" + account.getAccountNumber() + ")");
        }
        
        JLabel readingLabel = new JLabel("New Reading (kL):");
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
                
                Water selected = waterAccounts.get(index);
                previousWaterReadings.put(selected.getAccountNumber(), selected.getMeterReading());
                selected.setMeterReading(newReading);
                
                // Update in database
                waterManager.updateWater(selected);
                
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
    
    private void calculateWaterBill() {
        if (waterAccounts.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, 
                "No water accounts found.", 
                "No Accounts", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Create a dialog for selecting an account and showing the bill
        JDialog dialog = new JDialog(parentFrame, "Water Bill Calculation", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setLayout(new BorderLayout());
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel accountLabel = new JLabel("Select Account:");
        JComboBox<String> accountCombo = new JComboBox<>();
        
        for (Water account : waterAccounts) {
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
            Water selected = waterAccounts.get(index);
            double previousReading = previousWaterReadings.getOrDefault(selected.getAccountNumber(), 0.0);
            double bill = selected.calculateBill(previousReading);
            double consumption = selected.getMeterReading() - previousReading;
            
            StringBuilder sb = new StringBuilder();
            sb.append("Bill Calculation:\n\n");
            sb.append("Account: ").append(selected.getName()).append("\n");
            sb.append("Provider: ").append(selected.getProvider()).append("\n");
            sb.append("Account Number: ").append(selected.getAccountNumber()).append("\n\n");
            sb.append("Previous Reading: ").append(previousReading).append(" kL\n");
            sb.append("Current Reading: ").append(selected.getMeterReading()).append(" kL\n");
            sb.append("Consumption: ").append(consumption).append(" kL\n\n");
            sb.append("Rate: $").append(selected.getRatePerCubicMeter()).append(" per kL\n");
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
    
    public List<Water> getWaterAccounts() {
        return waterAccounts;
    }
    
    public Map<String, Double> getPreviousReadings() {
        return previousWaterReadings;
    }
}