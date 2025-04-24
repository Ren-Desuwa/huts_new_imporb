package views;

import models.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.text.DecimalFormat;

import database.*;
import ignoreme.Electricity;
import ignoreme.Electricity_Manager;
import ignoreme.Gas;
import ignoreme.Gas_Manager;
import ignoreme.Gas_Panel;
import ignoreme.Subscription;
import ignoreme.Subscription_Manager;
import ignoreme.Water;
import ignoreme.Water_Manager;


public class Summary_Panel {
    private JPanel panel;
    private Main_Frame mainFrame;
    private Database_Manager dbManager;
    private Electricity_Manager electricityManager;
    private Gas_Manager gasManager;
    private Water_Manager waterManager;
    private Subscription_Manager subscriptionManager;
    
    // Reference to other panels
    private Electricity_Panel electricityPanel;
    private Gas_Panel gasPanel;
    private Water_Panel waterPanel;
    private Subscription_Panel subscriptionPanel;
    
    // UI Components
    private JLabel totalCostLabel;
    private JPanel chartPanel;
    private JTable billsTable;
    private DefaultTableModel tableModel;
    
    public Summary_Panel(Main_Frame mainFrame, Electricity_Panel electricityPanel, 
            Gas_Panel gasPanel, Water_Panel waterPanel, Subscription_Panel subscriptionPanel) {
        this.mainFrame = mainFrame;
        this.dbManager = mainFrame.getDbManager();
        this.electricityPanel = electricityPanel;
        this.gasPanel = gasPanel;
        this.waterPanel = waterPanel;
        this.subscriptionPanel = subscriptionPanel;
        
        // Initialize managers
        this.electricityManager = new Electricity_Manager(dbManager.getConnection());
        this.gasManager = new Gas_Manager(dbManager.getConnection());
        this.waterManager = new Water_Manager(dbManager.getConnection());
        this.subscriptionManager = new Subscription_Manager(dbManager.getConnection());
        
        initializePanel();
    }
    
    private void initializePanel() {
        panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Monthly Summary");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Create Overview Panel (Top section)
        JPanel overviewPanel = new JPanel(new BorderLayout());
        overviewPanel.setBorder(BorderFactory.createTitledBorder("Monthly Overview"));
        
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        
        // Month panel
        JPanel monthPanel = new JPanel(new BorderLayout());
        JLabel monthLabel = new JLabel("Current Month:");
        JLabel monthValueLabel = new JLabel(LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        monthValueLabel.setFont(new Font("Arial", Font.BOLD, 14));
        monthPanel.add(monthLabel, BorderLayout.NORTH);
        monthPanel.add(monthValueLabel, BorderLayout.CENTER);
        
        // Total Cost Panel
        JPanel totalPanel = new JPanel(new BorderLayout());
        JLabel totalLabel = new JLabel("Estimated Total Cost:");
        totalCostLabel = new JLabel("$0.00");
        totalCostLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalPanel.add(totalLabel, BorderLayout.NORTH);
        totalPanel.add(totalCostLabel, BorderLayout.CENTER);
        
        // Previous Month Panel
        JPanel prevMonthPanel = new JPanel(new BorderLayout());
        JLabel prevMonthLabel = new JLabel("Compared to Last Month:");
        JLabel prevMonthValueLabel = new JLabel("N/A");
        prevMonthValueLabel.setFont(new Font("Arial", Font.BOLD, 14));
        prevMonthPanel.add(prevMonthLabel, BorderLayout.NORTH);
        prevMonthPanel.add(prevMonthValueLabel, BorderLayout.CENTER);
        
        // Export Panel
        JPanel exportPanel = new JPanel(new BorderLayout());
        JButton exportButton = new JButton("Export Summary");
        exportButton.addActionListener(e -> exportSummary());
        exportPanel.add(new JLabel(" "), BorderLayout.NORTH);
        exportPanel.add(exportButton, BorderLayout.CENTER);
        
        statsPanel.add(monthPanel);
        statsPanel.add(totalPanel);
        statsPanel.add(prevMonthPanel);
        statsPanel.add(exportPanel);
        
        overviewPanel.add(statsPanel, BorderLayout.CENTER);
        
        // Create chart panel (visualization)
        chartPanel = new JPanel();
        chartPanel.setBorder(BorderFactory.createTitledBorder("Cost Breakdown"));
        chartPanel.setPreferredSize(new Dimension(panel.getWidth(), 200));
        
        // Create expense table
        String[] columnNames = {"Utility Type", "Account", "Provider", "Usage", "Unit", "Cost ($)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        billsTable = new JTable(tableModel);
        billsTable.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(billsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Current Bills"));
        
        // Create content panel to hold chart and table
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(chartPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add components to main panel
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(overviewPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        // Initial load
        loadSummaryData();
    }
    
    public JPanel getPanel() {
        return panel;
    }
    
    public void refreshPanel() {
        loadSummaryData();
        updateChartPanel();
    }
    
    private void loadSummaryData() {
        tableModel.setRowCount(0);
        double totalCost = 0.0;
        
        // Get all utility accounts
        List<Electricity> electricityAccounts = electricityManager.getAllElectricity();
        List<Gas> gasAccounts = gasManager.getAllGas();
        List<Water> waterAccounts = waterManager.getAllWater();
        List<Subscription> subscriptions = subscriptionManager.getAllSubscriptions();
        
        DecimalFormat df = new DecimalFormat("#.##");
        
        // Add electricity accounts
        for (Electricity account : electricityAccounts) {
            // Get previous reading from electricityPanel
            Map<String, Double> previousElectricityReadings = electricityPanel.getPreviousReadings();
            Double previousReading = previousElectricityReadings.get(account.getAccountNumber());
            double usage = 0.0;
            if (previousReading != null) {
                usage = account.getMeterReading() - previousReading;
            }
            
            double cost = usage * account.getRatePerKwh();
            totalCost += cost;
            
            Object[] rowData = {
                "Electricity",
                account.getName(),
                account.getProvider(),
                df.format(usage),
                "kWh",
                String.format("%.2f", cost)
            };
            tableModel.addRow(rowData);
        }
        
        // Add gas accounts
        for (Gas account : gasAccounts) {
            // Get previous reading from gasPanel
            Map<String, Double> previousGasReadings = gasPanel.getPreviousReadings();
            Double previousReading = previousGasReadings.get(account.getAccountNumber());
            double usage = 0.0;
            if (previousReading != null) {
                usage = account.getMeterReading() - previousReading;
            }
            
            double cost = usage * account.getRatePerUnit();
            totalCost += cost;
            
            Object[] rowData = {
                "Gas",
                account.getName(),
                account.getProvider(),
                df.format(usage),
                "m³",
                String.format("%.2f", cost)
            };
            tableModel.addRow(rowData);
        }
        
        // Add water accounts
        for (Water account : waterAccounts) {
            // Get previous reading from waterPanel
            Map<String, Double> previousWaterReadings = waterPanel.getPreviousReadings();
            Double previousReading = previousWaterReadings.get(account.getAccountNumber());
            double usage = 0.0;
            if (previousReading != null) {
                usage = account.getMeterReading() - previousReading;
            }
            
            double cost = usage * account.getRatePerCubicMeter();
            totalCost += cost;
            
            Object[] rowData = {
                "Water",
                account.getName(),
                account.getProvider(),
                df.format(usage),
                "m³",
                String.format("%.2f", cost)
            };
            tableModel.addRow(rowData);
        }
        
        // Add subscriptions
        for (Subscription subscription : subscriptions) {
            totalCost += subscription.getMonthlyCost();
            
            Object[] rowData = {
                subscription.getType().toString(),
                subscription.getName(),
                subscription.getProvider(),
                "1",
                "month",
                String.format("%.2f", subscription.getMonthlyCost())
            };
            tableModel.addRow(rowData);
        }
        
        // Update total cost label
        totalCostLabel.setText(String.format("php%.2f", totalCost));
        
        // Update chart
        updateChartPanel();
    }
    
    private void updateChartPanel() {
        chartPanel.removeAll();
        
        // Get counts and calculate percentages for pie chart
        Map<String, Double> costByCategory = new HashMap<>();
        
        // Calculate total cost by category
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String category = (String) tableModel.getValueAt(i, 0);
            String costStr = (String) tableModel.getValueAt(i, 5);
            
            // Remove the $ sign and parse the cost
            double cost = Double.parseDouble(costStr.replace("php", "").trim());
            
            costByCategory.put(category, costByCategory.getOrDefault(category, 0.0) + cost);
        }
        
        // Create a simple bar chart representation
        JPanel barChartPanel = new JPanel(new BorderLayout());
        JPanel barsPanel = new JPanel(new GridLayout(1, costByCategory.size(), 10, 0));
        
        // Colors for different categories
        Map<String, Color> categoryColors = new HashMap<>();
        categoryColors.put("Electricity", new Color(255, 165, 0)); // Orange
        categoryColors.put("Gas", new Color(65, 105, 225));        // Royal Blue
        categoryColors.put("Water", new Color(30, 144, 255));      // Dodger Blue
        categoryColors.put("INTERNET", new Color(50, 205, 50));    // Lime Green
        categoryColors.put("STREAMING", new Color(220, 20, 60));   // Crimson
        categoryColors.put("PHONE", new Color(148, 0, 211));       // Dark Violet
        categoryColors.put("OTHER", new Color(169, 169, 169));     // Dark Gray
        
        double totalCost = costByCategory.values().stream().mapToDouble(Double::doubleValue).sum();
        
        // Create bars for each category
        for (Map.Entry<String, Double> entry : costByCategory.entrySet()) {
            String category = entry.getKey();
            double cost = entry.getValue();
            double percentage = (cost / totalCost) * 100;
            
            // Create bar panel
            JPanel categoryPanel = new JPanel(new BorderLayout());
            categoryPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            
            // Create bar
            JPanel barContainer = new JPanel(new BorderLayout());
            barContainer.setPreferredSize(new Dimension(50, 150));
            
            JPanel bar = new JPanel();
            bar.setBackground(categoryColors.getOrDefault(category, Color.GRAY));
            bar.setPreferredSize(new Dimension(40, (int)(percentage * 1.5)));
            
            JPanel emptySpace = new JPanel();
            emptySpace.setOpaque(false);
            
            barContainer.add(emptySpace, BorderLayout.CENTER);
            barContainer.add(bar, BorderLayout.SOUTH);
            
            // Create labels
            JLabel categoryLabel = new JLabel(category, SwingConstants.CENTER);
            JLabel costLabel = new JLabel(String.format("php%.2f", cost), SwingConstants.CENTER);
            JLabel percentLabel = new JLabel(String.format("%.1f%%", percentage), SwingConstants.CENTER);
            
            // Add components to panel
            categoryPanel.add(categoryLabel, BorderLayout.NORTH);
            categoryPanel.add(barContainer, BorderLayout.CENTER);
            JPanel bottomPanel = new JPanel(new GridLayout(2, 1));
            bottomPanel.add(costLabel);
            bottomPanel.add(percentLabel);
            categoryPanel.add(bottomPanel, BorderLayout.SOUTH);
            
            barsPanel.add(categoryPanel);
        }
        
        barChartPanel.add(barsPanel, BorderLayout.CENTER);
        
        // Add chart to panel
        chartPanel.setLayout(new BorderLayout());
        chartPanel.add(barChartPanel, BorderLayout.CENTER);
        
        chartPanel.revalidate();
        chartPanel.repaint();
    }
    
    private void exportSummary() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Summary Report");
        
        int userSelection = fileChooser.showSaveDialog(mainFrame);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            try {
                StringBuilder reportBuilder = new StringBuilder();
                reportBuilder.append("House Utility Management System - Monthly Summary\n");
                reportBuilder.append("================================================================\n");
                reportBuilder.append("Date: ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))).append("\n\n");
                
                reportBuilder.append("MONTHLY TOTAL COST: ").append(totalCostLabel.getText()).append("\n\n");
                
                reportBuilder.append("BREAKDOWN BY UTILITY TYPE:\n");
                reportBuilder.append("----------------------------------------------------------------\n");
                
                // Group by utility type
                Map<String, Double> costByType = new HashMap<>();
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    String type = (String) tableModel.getValueAt(i, 0);
                    String costStr = (String) tableModel.getValueAt(i, 5);
                    double cost = Double.parseDouble(costStr);
                    
                    costByType.put(type, costByType.getOrDefault(type, 0.0) + cost);
                }
                
                // Add type summaries
                for (Map.Entry<String, Double> entry : costByType.entrySet()) {
                    reportBuilder.append(String.format("%-15s php%.2f\n", entry.getKey() + ":", entry.getValue()));
                }
                
                reportBuilder.append("\nDETAILED BILL BREAKDOWN:\n");
                reportBuilder.append("----------------------------------------------------------------\n");
                reportBuilder.append(String.format("%-15s %-20s %-20s %-10s %-5s %-10s\n", 
                    "Type", "Account", "Provider", "Usage", "Unit", "Cost"));
                reportBuilder.append("----------------------------------------------------------------\n");
                
                // Add detailed rows
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    String type = (String) tableModel.getValueAt(i, 0);
                    String account = (String) tableModel.getValueAt(i, 1);
                    String provider = (String) tableModel.getValueAt(i, 2);
                    String usage = (String) tableModel.getValueAt(i, 3);
                    String unit = (String) tableModel.getValueAt(i, 4);
                    String cost = (String) tableModel.getValueAt(i, 5);
                    
                    reportBuilder.append(String.format("%-15s %-20s %-20s %-10s %-5s php%-10s\n", 
                        type, account, provider, usage, unit, cost));
                }
                
                reportBuilder.append("\n\nThis report was generated automatically by the House Utility Management System.");
                
                // Write to file
                java.nio.file.Files.writeString(fileChooser.getSelectedFile().toPath(), reportBuilder.toString());
                
                JOptionPane.showMessageDialog(mainFrame, 
                    "Summary report exported successfully!", 
                    "Export Complete", 
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainFrame, 
                    "Error exporting summary: " + ex.getMessage(), 
                    "Export Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}