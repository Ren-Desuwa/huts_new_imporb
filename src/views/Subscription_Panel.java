package views;

import models.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

import database.Database_Manager;
import ignoreme.Subscription;
import ignoreme.SubscriptionType;
import ignoreme.Subscription_Manager;

public class Subscription_Panel {
    private JPanel panel;
    private JTable subscriptionTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    
    private Main_Frame mainFrame;
    private Subscription_Manager subscriptionManager;
    
    public Subscription_Panel(Main_Frame mainFrame) {
        this.mainFrame = mainFrame;
        this.subscriptionManager = mainFrame.getDbManager().getSubscriptionManager();
        
        initializePanel();
    }
    
    private void initializePanel() {
        panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Subscription Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Create table
        String[] columnNames = {"Name", "Provider", "Account Number", "Type", "Monthly Cost ($)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        subscriptionTable = new JTable(tableModel);
        subscriptionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        subscriptionTable.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(subscriptionTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        // Create buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        addButton = new JButton("Add Subscription");
        editButton = new JButton("Edit Subscription");
        deleteButton = new JButton("Delete Subscription");
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        
        // Add action listeners
        addButton.addActionListener(e -> showAddSubscriptionDialog());
        editButton.addActionListener(e -> showEditSubscriptionDialog());
        deleteButton.addActionListener(e -> deleteSelectedSubscription());
        
        // Add components to main panel
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Load data
        loadSubscriptionData();
    }
    
    public JPanel getPanel() {
        return panel;
    }
    
    public void refreshPanel() {
        loadSubscriptionData();
    }
    
    private void loadSubscriptionData() {
        tableModel.setRowCount(0);
        List<Subscription> subscriptions = subscriptionManager.getAllSubscriptions();
        
        for (Subscription subscription : subscriptions) {
            Object[] rowData = {
                subscription.getName(),
                subscription.getProvider(),
                subscription.getAccountNumber(),
                subscription.getType(),
                String.format("%.2f", subscription.getMonthlyCost())
            };
            tableModel.addRow(rowData);
        }
    }
    
    private void showAddSubscriptionDialog() {
        JDialog dialog = new JDialog(mainFrame, "Add Subscription", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(mainFrame);
        
        JPanel dialogPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField nameField = new JTextField();
        JTextField providerField = new JTextField();
        JTextField accountField = new JTextField();
        JComboBox<SubscriptionType> typeComboBox = new JComboBox<>(SubscriptionType.values());
        JTextField costField = new JTextField();
        
        dialogPanel.add(new JLabel("Name:"));
        dialogPanel.add(nameField);
        dialogPanel.add(new JLabel("Provider:"));
        dialogPanel.add(providerField);
        dialogPanel.add(new JLabel("Account Number:"));
        dialogPanel.add(accountField);
        dialogPanel.add(new JLabel("Type:"));
        dialogPanel.add(typeComboBox);
        dialogPanel.add(new JLabel("Monthly Cost ($):"));
        dialogPanel.add(costField);
        
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String provider = providerField.getText().trim();
                String accountNumber = accountField.getText().trim();
                SubscriptionType type = (SubscriptionType) typeComboBox.getSelectedItem();
                double monthlyCost = Double.parseDouble(costField.getText().trim());
                
                if (name.isEmpty() || provider.isEmpty() || accountNumber.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill out all fields", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Subscription subscription = new Subscription(name, provider, accountNumber, type, monthlyCost);
                subscriptionManager.saveSubscription(subscription);
                
                dialog.dispose();
                loadSubscriptionData();
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid number for monthly cost", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        dialog.setLayout(new BorderLayout());
        dialog.add(dialogPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void showEditSubscriptionDialog() {
        int selectedRow = subscriptionTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(mainFrame, "Please select a subscription to edit", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String accountNumber = (String) tableModel.getValueAt(selectedRow, 2);
        Subscription subscription = subscriptionManager.getSubscriptionByAccountNumber(accountNumber);
        
        if (subscription == null) {
            JOptionPane.showMessageDialog(mainFrame, "Could not find subscription details", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog(mainFrame, "Edit Subscription", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(mainFrame);
        
        JPanel dialogPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField nameField = new JTextField(subscription.getName());
        JTextField providerField = new JTextField(subscription.getProvider());
        JTextField accountField = new JTextField(subscription.getAccountNumber());
        accountField.setEditable(false);
        JComboBox<SubscriptionType> typeComboBox = new JComboBox<>(SubscriptionType.values());
        typeComboBox.setSelectedItem(subscription.getType());
        JTextField costField = new JTextField(String.valueOf(subscription.getMonthlyCost()));
        
        dialogPanel.add(new JLabel("Name:"));
        dialogPanel.add(nameField);
        dialogPanel.add(new JLabel("Provider:"));
        dialogPanel.add(providerField);
        dialogPanel.add(new JLabel("Account Number:"));
        dialogPanel.add(accountField);
        dialogPanel.add(new JLabel("Type:"));
        dialogPanel.add(typeComboBox);
        dialogPanel.add(new JLabel("Monthly Cost ($):"));
        dialogPanel.add(costField);
        
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String provider = providerField.getText().trim();
                SubscriptionType type = (SubscriptionType) typeComboBox.getSelectedItem();
                double monthlyCost = Double.parseDouble(costField.getText().trim());
                
                if (name.isEmpty() || provider.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill out all fields", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                subscription.setName(name);
                subscription.setProvider(provider);
                subscription.setType(type);
                subscription.setMonthlyCost(monthlyCost);
                
                subscriptionManager.updateSubscription(subscription);
                
                dialog.dispose();
                loadSubscriptionData();
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid number for monthly cost", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        dialog.setLayout(new BorderLayout());
        dialog.add(dialogPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void deleteSelectedSubscription() {
        int selectedRow = subscriptionTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(mainFrame, "Please select a subscription to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String accountNumber = (String) tableModel.getValueAt(selectedRow, 2);
        
        int confirm = JOptionPane.showConfirmDialog(
            mainFrame,
            "Are you sure you want to delete this subscription?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = subscriptionManager.deleteSubscription(accountNumber);
            if (success) {
                loadSubscriptionData();
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Error deleting subscription", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
