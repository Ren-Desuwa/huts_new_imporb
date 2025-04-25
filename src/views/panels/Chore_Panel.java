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
        JLabel priorityLabel = new JLabel("Priority (1 is the Highest)");
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
                // Convert String user ID to Integer if needed
                Integer userId = currentUser.getId();
                chores = dbManager.getChoreManager().getChoresByUserId(userId);
                
                // Rest of the method remains the same...
                
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
            
            Chore newChore = new Chore(
            	    currentUser.getId(),  // Convert to Integer
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
                
                if (status.equals("Pending") && chore.isCompleted()) {
                    shouldInclude = false;
                } else if (status.equals("Completed") && !chore.isCompleted()) {
                    shouldInclude = false;
                }
                
                if (shouldInclude) {
                    String choreStatus = chore.isCompleted() ? "Completed" : "Pending";
                    String priority = priorityToString(chore.getPriority());
                    
                    Object[] row = {
                        chore.getChoreName(),
                        chore.getDueDate() != null ? chore.getDueDate().toString() : "",
                        chore.getAssignedTo(),
                        priority,
                        choreStatus,
                        "Actions"  // This will be rendered as a button
                    };
                    choreTableModel.addRow(row);
                }
            }
        }
    }
    
    // Convert priority number to string description
    private String priorityToString(Integer priority) {
        if (priority == null) return "Medium";
        
        switch (priority) {
            case 1: return "Highest";
            case 2: return "High";
            case 3: return "Medium";
            case 4: return "Low";
            case 5: return "Lowest";
            default: return "Medium";
        }
    }
    
    // Button renderer for the Actions column
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(UIManager.getColor("Button.background"));
            }
            setText("Actions ▼");
            return this;
        }
    }
    
    // Button editor for the Actions column
    private class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private int row;
        
        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            if (isSelected) {
                button.setForeground(table.getSelectionForeground());
                button.setBackground(table.getSelectionBackground());
            } else {
                button.setForeground(table.getForeground());
                button.setBackground(table.getBackground());
            }
            label = "Actions ▼";
            button.setText(label);
            this.row = row;
            isPushed = true;
            return button;
        }
        
        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // Show popup menu with actions
                showActionsPopupMenu(row);
            }
            isPushed = false;
            return label;
        }
        
        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
    
    // Show actions popup menu for a chore
    private void showActionsPopupMenu(int row) {
        if (row < 0 || row >= chores.size()) return;
        
        Chore selectedChore = chores.get(row);
        JPopupMenu popupMenu = new JPopupMenu();
        
        // Complete/Reopen option
        JMenuItem completeItem;
        if (selectedChore.isCompleted()) {
            completeItem = new JMenuItem("Mark as Pending");
            completeItem.addActionListener(e -> reopenChore(selectedChore));
        } else {
            completeItem = new JMenuItem("Mark as Completed");
            completeItem.addActionListener(e -> completeChore(selectedChore));
        }
        
        // Edit option
        JMenuItem editItem = new JMenuItem("Edit");
        editItem.addActionListener(e -> editChore(selectedChore));
        
        // Delete option
        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.addActionListener(e -> deleteChore(selectedChore));
        
        // Add items to popup menu
        popupMenu.add(completeItem);
        popupMenu.add(editItem);
        popupMenu.add(deleteItem);
        
        // Show the popup
        Point p = choreTable.getLocationOnScreen();
        int rowHeight = choreTable.getRowHeight(row);
        popupMenu.show(null, p.x + choreTable.getWidth() - 100, 
            p.y + choreTable.getCellRect(row, 0, true).y + rowHeight);
    }
    
    // Mark a chore as completed
    private void completeChore(Chore chore) {
        try {
            dbManager.getChoreManager().markChoreAsCompleted(chore.getId());
            refreshPanel();
            JOptionPane.showMessageDialog(parentFrame, 
                "Chore marked as completed!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(parentFrame, 
                "Database error: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    // Reopen a completed chore
    private void reopenChore(Chore chore) {
        try {
            chore.setCompleted(false);
            chore.setCompletionDate(null);
            dbManager.getChoreManager().updateChore(chore);
            refreshPanel();
            JOptionPane.showMessageDialog(parentFrame, 
                "Chore reopened!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(parentFrame, 
                "Database error: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    // Delete a chore
    private void deleteChore(Chore chore) {
        int confirm = JOptionPane.showConfirmDialog(parentFrame, 
            "Are you sure you want to delete this chore?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                dbManager.getChoreManager().deleteChore(chore.getId());
                refreshPanel();
                JOptionPane.showMessageDialog(parentFrame, 
                    "Chore deleted successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(parentFrame, 
                    "Database error: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
    // Edit a chore
    private void editChore(Chore chore) {
        // Create a dialog for editing the chore
        JDialog editDialog = new JDialog(parentFrame, "Edit Chore", true);
        editDialog.setLayout(new BorderLayout());
        editDialog.setSize(400, 450);
        editDialog.setLocationRelativeTo(parentFrame);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Chore Name field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Chore Name:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField nameField = new JTextField(chore.getChoreName(), 20);
        formPanel.add(nameField, gbc);
        
        // Description field
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Description:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        JTextArea descArea = new JTextArea(chore.getDescription(), 3, 20);
        descArea.setLineWrap(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        formPanel.add(descScroll, gbc);
        
        // Due Date field
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Due Date:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        JTextField dueDateField = new JTextField(
            chore.getDueDate() != null ? chore.getDueDate().toString() : "", 20);
        formPanel.add(dueDateField, gbc);
        
        // Frequency field
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Frequency:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        String[] frequencies = {"One-time", "Daily", "Weekly", "Monthly"};
        JComboBox<String> freqCombo = new JComboBox<>(frequencies);
        if (chore.getFrequency() != null) {
            for (int i = 0; i < frequencies.length; i++) {
                if (frequencies[i].equalsIgnoreCase(chore.getFrequency())) {
                    freqCombo.setSelectedIndex(i);
                    break;
                }
            }
        }
        formPanel.add(freqCombo, gbc);
        
        // Assigned To field
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Assigned To:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 4;
        JTextField assignedField = new JTextField(chore.getAssignedTo(), 20);
        formPanel.add(assignedField, gbc);
        
        // Priority field
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Priority:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 5;
        Integer[] priorities = {1, 2, 3, 4, 5};
        JComboBox<Integer> priorityCombo = new JComboBox<>(priorities);
        if (chore.getPriority() != null) {
            priorityCombo.setSelectedItem(chore.getPriority());
        } else {
            priorityCombo.setSelectedIndex(2); // Default to medium (3)
        }
        formPanel.add(priorityCombo, gbc);
        
        // Completed status
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(new JLabel("Status:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 6;
        JCheckBox completedCheck = new JCheckBox("Completed", chore.isCompleted());
        formPanel.add(completedCheck, gbc);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> {
            try {
                // Update chore with new values
                chore.setChoreName(nameField.getText());
                chore.setDescription(descArea.getText());
                
                String dueDateStr = dueDateField.getText();
                if (!dueDateStr.isEmpty()) {
                    chore.setDueDate(LocalDate.parse(dueDateStr));
                } else {
                    chore.setDueDate(null);
                }
                
                chore.setFrequency((String) freqCombo.getSelectedItem());
                chore.setAssignedTo(assignedField.getText());
                chore.setPriority((Integer) priorityCombo.getSelectedItem());
                
                boolean wasCompleted = chore.isCompleted();
                boolean isNowCompleted = completedCheck.isSelected();
                
                chore.setCompleted(isNowCompleted);
                
                // If status changed from pending to completed, set completion date
                if (!wasCompleted && isNowCompleted) {
                    chore.setCompletionDate(LocalDate.now());
                } else if (wasCompleted && !isNowCompleted) {
                    chore.setCompletionDate(null);
                }
                
                // Save changes to database
                dbManager.getChoreManager().updateChore(chore);
                
                // Close dialog and refresh panel
                editDialog.dispose();
                refreshPanel();
                
                JOptionPane.showMessageDialog(parentFrame, 
                    "Chore updated successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(editDialog, 
                    "Please enter a valid date in YYYY-MM-DD format.", 
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(editDialog, 
                    "Database error: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        
        cancelButton.addActionListener(e -> editDialog.dispose());
        
        buttonsPanel.add(saveButton);
        buttonsPanel.add(cancelButton);
        
        editDialog.add(formPanel, BorderLayout.CENTER);
        editDialog.add(buttonsPanel, BorderLayout.SOUTH);
        editDialog.setVisible(true);
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        refreshPanel(); // Refresh to show only the current user's data
    }
}