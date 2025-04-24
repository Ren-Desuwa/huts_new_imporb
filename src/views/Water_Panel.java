package views;

import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import javax.swing.*;

import database.Database_Manager;
import models.Account;
import models.Bill;
import models.Reading_History;

public class Water_Panel implements Utility_Panel {
    private JPanel waterPanel;
    private Main_Frame parentFrame;
    private List<Account> waterAccounts;
    private Database_Manager dbManager;
    
    public Water_Panel(Main_Frame parentFrame, Database_Manager dbManager) {
        this.parentFrame = parentFrame;
        this.dbManager = dbManager;
        
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
        
        try {
            // Fetch current data - get water accounts for current user
            String userId = parentFrame.getCurrentUser().getId();
            List<Account> allAccounts = dbManager.getAccountManager().getAccountsByUserId(userId);
            
            // Filter out only water accounts
            waterAccounts = allAccounts.stream()
                .filter(account -> account.getType().equalsIgnoreCase("water"))
                .toList();
            
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
            
            String[] columnNames = {"Provider", "Account Number", "Current Reading", "Rate ($/kL)"};
            Object[][] data = new Object[waterAccounts.size()][4];
            
            for (int i = 0; i < waterAccounts.size(); i++) {
                Account account = waterAccounts.get(i);
                data[i][0] = account.getProvider();
                data[i][1] = account.getAccountNumber();
                
                // Get the latest reading for this account
                Reading_History latestReading = dbManager.getReadingHistoryManager().getLatestReadingByAccountId(account.getId());
                data[i][2] = latestReading != null ? latestReading.getReadingValue() : "No readings";
                data[i][3] = account.getRatePerUnit();
            }
            
            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            tablePanel.add(scrollPane, BorderLayout.CENTER);
            
            // Add components to water panel
            waterPanel.add(titleLabel, BorderLayout.NORTH);
            waterPanel.add(buttonsPanel, BorderLayout.SOUTH);
            waterPanel.add(tablePanel, BorderLayout.CENTER);
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parentFrame, 
                "Error loading water accounts: " + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        
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
        
        for (Account account : waterAccounts) {
            accountCombo.addItem(account.getProvider() + " (" + account.getAccountNumber() + ")");
        }
        
        formPanel.add(accountLabel);
        formPanel.add(accountCombo);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        JButton removeButton = new JButton("Remove");
        
        cancelButton.addActionListener(e -> dialog.dispose());
        removeButton.addActionListener(e -> {
            int index = accountCombo.getSelectedIndex();
            Account selected = waterAccounts.get(index);
            
            // Confirm deletion
            int confirm = JOptionPane.showConfirmDialog(dialog,
                "Are you sure you want to remove the account: " + selected.getProvider() + " " + selected.getAccountNumber() + "?",
                "Confirm Removal", JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    // Remove from database
                    dbManager.getAccountManager().deleteAccount(selected.getId());
                    
                    dialog.dispose();
                    refreshPanel(); // Refresh the panel
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(dialog, 
                        "Error deleting account: " + ex.getMessage(), 
                        "Database Error", JOptionPane.ERROR_MESSAGE);
                }
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
        
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel providerLabel = new JLabel("Provider:");
        JTextField providerField = new JTextField(20);
        
        JLabel accountLabel = new JLabel("Account Number:");
        JTextField accountField = new JTextField(20);
        
        JLabel rateLabel = new JLabel("Rate per kL ($):");
        JTextField rateField = new JTextField(20);
        
        JLabel readingLabel = new JLabel("Initial Reading (kL):");
        JTextField readingField = new JTextField(20);
        
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
                String provider = providerField.getText();
                String accountNumber = accountField.getText();
                double rate = Double.parseDouble(rateField.getText());
                double reading = Double.parseDouble(readingField.getText());
                
                if (provider.isEmpty() || accountNumber.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Please fill in all fields", 
                        "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Create new account
                Account waterAccount = new Account(
                    UUID.randomUUID().toString(),
                    parentFrame.getCurrentUser().getId(),
                    "water",
                    provider,
                    accountNumber,
                    rate
                );
                
                // Save account to database
                dbManager.getAccountManager().createAccount(waterAccount);
                
                // Create initial reading record
                Reading_History initialReading = new Reading_History(
                    UUID.randomUUID().toString(),
                    waterAccount.getId(),
                    LocalDate.now(),
                    reading
                );
                
                // Save reading to database
                dbManager.getReadingHistoryManager().createReading(initialReading);
                
                dialog.dispose();
                refreshPanel(); // Refresh the panel
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Please enter valid numbers for rate and reading.", 
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, 
                    "Error saving account: " + ex.getMessage(), 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
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
        
        for (Account account : waterAccounts) {
            accountCombo.addItem(account.getProvider() + " (" + account.getAccountNumber() + ")");
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
                
                Account selected = waterAccounts.get(index);
                
                // Create new reading record
                Reading_History reading = new Reading_History(
                    UUID.randomUUID().toString(),
                    selected.getId(),
                    LocalDate.now(),
                    newReading
                );
                
                // Save reading to database
                dbManager.getReadingHistoryManager().createReading(reading);
                
                dialog.dispose();
                refreshPanel(); // Refresh the panel
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Please enter a valid number for the reading.", 
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, 
                    "Error saving reading: " + ex.getMessage(), 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
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
        
        for (Account account : waterAccounts) {
            accountCombo.addItem(account.getProvider() + " (" + account.getAccountNumber() + ")");
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
            try {
                int index = accountCombo.getSelectedIndex();
                Account selected = waterAccounts.get(index);
                
                // Generate the bill using the reading history manager
                LocalDate dueDate = LocalDate.now().plusDays(30); // Due in 30 days
                Bill bill = dbManager.getReadingHistoryManager().generateBillFromReadings(
                    selected.getId(), 
                    selected.getRatePerUnit(), 
                    dueDate
                );
                
                if (bill == null) {
                    resultArea.setText("Not enough readings to generate a bill. Please add at least two readings.");
                    return;
                }
                
                StringBuilder sb = new StringBuilder();
                sb.append("Bill Calculation:\n\n");
                sb.append("Provider: ").append(selected.getProvider()).append("\n");
                sb.append("Account Number: ").append(selected.getAccountNumber()).append("\n\n");
                sb.append("Previous Reading: ").append(bill.getStartReading()).append(" kL\n");
                sb.append("Current Reading: ").append(bill.getEndReading()).append(" kL\n");
                sb.append("Consumption: ").append(bill.getConsumption()).append(" kL\n\n");
                sb.append("Rate: $").append(selected.getRatePerUnit()).append(" per kL\n");
                sb.append("Total Bill: $").append(String.format("%.2f", bill.getAmount())).append("\n");
                sb.append("Issue Date: ").append(bill.getIssueDate()).append("\n");
                sb.append("Due Date: ").append(bill.getDueDate()).append("\n");
                
                resultArea.setText(sb.toString());
                
                // Option to save this bill
                int saveOption = JOptionPane.showConfirmDialog(dialog,
                    "Would you like to save this bill?",
                    "Save Bill", JOptionPane.YES_NO_OPTION);
                    
                if (saveOption == JOptionPane.YES_OPTION) {
                    dbManager.getBillManager().createBill(bill);
                    JOptionPane.showMessageDialog(dialog, 
                        "Bill saved successfully.", 
                        "Bill Saved", JOptionPane.INFORMATION_MESSAGE);
                }
                
            } catch (SQLException ex) {
                ex.printStackTrace();
                resultArea.setText("Error calculating bill: " + ex.getMessage());
            }
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
}