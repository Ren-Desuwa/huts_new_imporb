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
import models.Chore;
import views.Main_Frame;
import database.Database_Manager;

public class Chore_Panel implements Utility_Panel {
    private JPanel chorePanel;
    private Main_Frame parentFrame;
    private List<Chore> chores;
    private User currentUser;
    
    // Database manager
    private Database_Manager dbManager;
    
    // UI Components
    private JTextField choreNameField;
    private JTextArea descriptionField;
    private JTextField dueDateField;
    private JComboBox<String> frequencyComboBox;
    private JTextField assignedToField;
    private JComboBox<Integer> priorityComboBox;
    private JTextField pendingChoresField;
    private JTextField completedThisWeekField;
    private DefaultTableModel choreTableModel;
    private JTable choreTable;
    
    public Chore_Panel(Main_Frame parentFrame, User currentUser) {
        this.parentFrame = parentFrame;
        this.currentUser = currentUser;
        this.dbManager = Database_Manager.getInstance();
        
        // Initialize the panel
        chorePanel = new JPanel(new BorderLayout());
        chorePanel.setBackground(new Color(240, 240, 240));
        
        createComponents();
        refreshPanel();
    }
    
    private void createComponents() {
        // Main content panel with 2-column layout
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // LEFT SIDE PANEL
        JPanel leftPanel = new JPanel(new BorderLayout(0, 20));
        leftPanel.setBackground(new Color(230, 240, 250)); // Light blue background
        leftPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // New Chore Form
        JPanel addChorePanel = new JPanel(new GridBagLayout());
        addChorePanel.setBackground(new Color(230, 240, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Title
        JLabel titleLabel = new JLabel("Add New Chore");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        addChorePanel.add(titleLabel, gbc);
        
        // Chore Name field
        JLabel nameLabel = new JLabel("Chore Name");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        addChorePanel.add(nameLabel, gbc);
        
        choreNameField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        addChorePanel.add(choreNameField, gbc);
        
        // Description field
        JLabel descLabel = new JLabel("Description");
        gbc.gridx = 0;
        gbc.gridy = 2;
        addChorePanel.add(descLabel, gbc);
        
        descriptionField = new JTextArea(3, 15);
        descriptionField.setLineWrap(true);
        JScrollPane descScrollPane = new JScrollPane(descriptionField);
        gbc.gridx = 1;
        gbc.gridy = 2;
        addChorePanel.add(descScrollPane, gbc);
        
        // Due Date field
        JLabel dueDateLabel = new JLabel("Due Date");
        gbc.gridx = 0;
        gbc.gridy = 3;
        addChorePanel.add(dueDateLabel, gbc);
        
        dueDateField = new JTextField(LocalDate.now().toString(), 10);
        gbc.gridx = 1;
        gbc.gridy = 3;
        addChorePanel.add(dueDateField, gbc);
        
        // Frequency selection
        JLabel frequencyLabel = new JLabel("Frequency");
        gbc.gridx = 0;
        gbc.gridy = 4;
        addChorePanel.add(frequencyLabel, gbc);
        
        String[] frequencies = {"One-time", "Daily", "Weekly", "Monthly"};
        frequencyComboBox = new JComboBox<>(frequencies);
        gbc.gridx = 1;
        gbc.gridy = 4;
        addChorePanel.add(frequencyComboBox, gbc);
        
        // Assigned To field
        JLabel assignedToLabel = new JLabel("Assigned To");
        gbc.gridx = 0;
        gbc.gridy = 5;
        addChorePanel.add(assignedToLabel, gbc);
        
        assignedToField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 5;
        addChorePanel.add(assignedToField, gbc);
        
        // Priority selection
        JLabel priorityLabel = new JLabel("Priority (1-5)");
        gbc.gridx = 0;
        gbc.gridy = 6;
        addChorePanel.add(priorityLabel, gbc);
        
        Integer[] priorities = {1, 2, 3, 4, 5};
        priorityComboBox = new JComboBox<>(priorities);
        priorityComboBox.setSelectedIndex(2); // Default to medium priority (3)
        gbc.gridx = 1;
        gbc.gridy = 6;
        addChorePanel.add(priorityComboBox, gbc);
        
        // Add Chore button
        JButton addButton = new JButton("+ Add Chore");
        addButton.setBackground(new Color(25, 25, 112));
        addButton.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        addButton.addActionListener(e -> addChore());
        addChorePanel.add(addButton, gbc);
        
        // Chore List
        JPanel choreListPanel = new JPanel(new BorderLayout(0, 10));
        choreListPanel.setBackground(new Color(230, 240, 250));
        
        JLabel listLabel = new JLabel("My Chores");
        listLabel.setFont(new Font("Arial", Font.BOLD, 18));
        choreListPanel.add(listLabel, BorderLayout.NORTH);
        
        // Table for chores
        String[] columnNames = {"Name", "Due Date", "Assigned To", "Priority", "Status", "Actions"};
        choreTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only Actions column is editable
            }
        };
        
        choreTable = new JTable(choreTableModel);
        JScrollPane scrollPane = new JScrollPane(choreTable);
        choreListPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add button renderer for Actions column
        choreTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        choreTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox()));
        
        // Combine panels on left side
        leftPanel.add(addChorePanel, BorderLayout.NORTH);
        leftPanel.add(choreListPanel, BorderLayout.CENTER);
        
        // RIGHT SIDE PANEL
        JPanel rightPanel = new JPanel(new BorderLayout(0, 20));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Statistics Panel
        JPanel statsPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        statsPanel.setBackground(new Color(230, 240, 250));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel statsTitle = new JLabel("Chore Statistics");
        statsTitle.setFont(new Font("Arial", Font.BOLD, 18));
        
        // Pending chores
        JLabel pendingLabel = new JLabel("Pending Chores:");
        pendingChoresField = new JTextField("0");
        pendingChoresField.setEditable(false);
        
        // Completed this week
        JLabel completedWeekLabel = new JLabel("Completed This Week:");
        completedThisWeekField = new JTextField("0");
        completedThisWeekField.setEditable(false);
        
        // Add components to stats panel
        statsPanel.add(statsTitle);
        statsPanel.add(new JLabel()); // Empty cell
        statsPanel.add(pendingLabel);
        statsPanel.add(pendingChoresField);
        statsPanel.add(completedWeekLabel);
        statsPanel.add(completedThisWeekField);
        
        // Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        
        JLabel filterLabel = new JLabel("Filter by status:");
        String[] filterOptions = {"All", "Pending", "Completed"};
        JComboBox<String> filterComboBox = new JComboBox<>(filterOptions);
        filterComboBox.addActionListener(e -> {
            String selection = (String) filterComboBox.getSelectedItem();
            filterChoresByStatus(selection);
        });
        
        filterPanel.add(filterLabel);
        filterPanel.add(filterComboBox);
        
        // Calendar/Planner Panel (Placeholder)
        JPanel plannerPanel = new JPanel(new BorderLayout());
        plannerPanel.setBackground(Color.WHITE);
        plannerPanel.setBorder(BorderFactory.createTitledBorder("Upcoming Chores"));
        
        // Add a simple list of upcoming chores here (placeholder)
        JList<String> upcomingList = new JList<>();
        plannerPanel.add(new JScrollPane(upcomingList), BorderLayout.CENTER);
        
        // Combine panels on right side
        JPanel rightTopPanel = new JPanel(new BorderLayout());
        rightTopPanel.add(statsPanel, BorderLayout.NORTH);
        rightTopPanel.add(filterPanel, BorderLayout.SOUTH);
        
        rightPanel.add(rightTopPanel, BorderLayout.NORTH);
        rightPanel.add(plannerPanel, BorderLayout.CENTER);
        
        // Add both sides to the content panel
        contentPanel.add(leftPanel);
        contentPanel.add(rightPanel);
        
        // Add content panel to main panel
        chorePanel.add(contentPanel, BorderLayout.CENTER);
    }
    
    @Override
    public JPanel getPanel() {
        return chorePanel;
    }
    
    @Override
    public void refreshPanel() {
        // Clear the table
        choreTableModel.setRowCount(0);
        
        // Fetch only the current user's data
        if (currentUser != null) {
            try {
                // Get all chores for the current user
                chores = dbManager.getChoreManager().getChoresByUserId(currentUser.getId());
                
                // Populate the table with chore data
                for (Chore chore : chores) {
                    String status = chore.isCompleted() ? "Completed" : "Pending";
                    String priority = priorityToString(chore.getPriority());
                    
                    Object[] row = {
                        chore.getChoreName(),
                        chore.getDueDate() != null ? chore.getDueDate().toString() : "",
                        chore.getAssignedTo(),
                        priority,
                        status,
                        "Actions"  // This will be rendered as a button
                    };
                    choreTableModel.addRow(row);
                }
                
                // Update statistics
                updateStatistics();
                
                // Update the upcoming chores list
                updateUpcomingChores();
                
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(parentFrame, 
                    "Error fetching chore data: " + e.getMessage(), 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } else {
            chores = new ArrayList<>(); // Empty list if no user logged in
        }
        
        // Refresh UI components
        chorePanel.revalidate();
        chorePanel.repaint();
    }
    
    // Method to add a new chore
    private void addChore() {
        // Check if user is logged in
        if (currentUser == null) {
            JOptionPane.showMessageDialog(parentFrame, 
                "Please log in to add a chore.", 
                "Authentication Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            String choreName = choreNameField.getText();
            String description = descriptionField.getText();
            String dueDateStr = dueDateField.getText();
            String frequency = (String) frequencyComboBox.getSelectedItem();
            String assignedTo = assignedToField.getText();
            Integer priority = (Integer) priorityComboBox.getSelectedItem();
            
            if (choreName.isEmpty()) {
                JOptionPane.showMessageDialog(parentFrame, 
                    "Chore Name is required.", 
                    "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            LocalDate dueDate = null;
            if (!dueDateStr.isEmpty()) {
                dueDate = LocalDate.parse(dueDateStr);
            }
            
            // Create and save the new chore
            Chore newChore = new Chore(
                currentUser.getId(),
                choreName,
                description,
                dueDate,
                false, // not completed yet
                frequency,
                assignedTo,
                priority
            );
            
            dbManager.getChoreManager().createChore(newChore);
            
            // Clear form fields
            choreNameField.setText("");
            descriptionField.setText("");
            dueDateField.setText(LocalDate.now().toString());
            frequencyComboBox.setSelectedIndex(0);
            assignedToField.setText("");
            priorityComboBox.setSelectedIndex(2);
            
            // Refresh the panel
            refreshPanel();
            
            JOptionPane.showMessageDialog(parentFrame, 
                "Chore added successfully!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
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
    
    // Method to update chore statistics
    private void updateStatistics() {
        try {
            // Calculate pending chores
            int pendingCount = dbManager.getChoreManager().getPendingChoresCount(currentUser.getId());
            pendingChoresField.setText(String.valueOf(pendingCount));
            
            // Calculate completed chores for this week
            LocalDate today = LocalDate.now();
            LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
            int completedThisWeek = dbManager.getChoreManager().getCompletedChoresCount(
                currentUser.getId(), startOfWeek, today);
            completedThisWeekField.setText(String.valueOf(completedThisWeek));
        } catch (SQLException ex) {
            System.err.println("Error updating statistics: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    // Method to update the upcoming chores list
    private void updateUpcomingChores() {
        // This is a placeholder method - in a real implementation, 
        // you might display upcoming chores in a calendar view or list
    }
    
    // Filter chores by status (All, Pending, Completed)
    private void filterChoresByStatus(String status) {
        choreTableModel.setRowCount(0);
        
        if (chores != null) {
            for (Chore chore : chores) {
                boolean shouldInclude = true;
                
                if (status.equals("Pending") && chore.