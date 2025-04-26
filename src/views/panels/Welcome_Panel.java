package views.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import models.*;
import views.Main_Frame;
import database.*;

public class Welcome_Panel extends JPanel implements Utility_Panel {
    private Database_Manager dbManager;
    private Main_Frame parentFrame;
    
    // UI Components
    private JLabel welcomeLabel;
    private JPanel statsPanel;
    private JPanel billsPanel;
    private JPanel remindersPanel;
    private JPanel tipPanel;
    private JLabel jlbl_BackgroundLeft;
    private JLabel jlbl_BackgroundRight;
    private JPanel contentPanel;
    
    // Today's date
    private LocalDate today = LocalDate.now();
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
    
    public Welcome_Panel(Main_Frame parentFrame, Database_Manager dbManager) {
        this.parentFrame = parentFrame;
        this.dbManager = dbManager;
        
        // Initialize the panel with white background
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());
        
        initComponents();
        
        // Add component listener for responsiveness
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeComponents();
            }
        });
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        // Call resizeComponents once the panel is added to the container hierarchy
        SwingUtilities.invokeLater(this::resizeComponents);
    }
    
    private void initComponents() {
        // Content panel to hold all components
        contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(null);
        
        // Welcome header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        // Welcome message with user name and date
        String userName = (parentFrame.getCurrentUser() != null) ? 
                          parentFrame.getCurrentUser().getFullName() : "User";
        
        JPanel welcomeTextPanel = new JPanel(new GridLayout(2, 1));
        welcomeTextPanel.setOpaque(false);
        
        welcomeLabel = new JLabel("Welcome Back!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(new Color(23, 22, 22));
        
        JLabel userDateLabel = new JLabel("Hello, " + userName + "! Today is " + 
                                         today.format(dateFormatter) + 
                                         " Hope you're having a great day keeping the household running smoothly!");
        userDateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        welcomeTextPanel.add(welcomeLabel);
        welcomeTextPanel.add(userDateLabel);
        
        // Add icon to header
        JLabel iconLabel = new JLabel(new ImageIcon(getClass().getResource("/assets/icon/AccountBlack.png")));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
        
        headerPanel.add(iconLabel, BorderLayout.WEST);
        headerPanel.add(welcomeTextPanel, BorderLayout.CENTER);
        headerPanel.setBounds(200, 30, 800, 80);
        
        contentPanel.add(headerPanel);
        
        // Main content layout - 2x2 grid for different panels
        JPanel mainContentPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        mainContentPanel.setOpaque(false);
        mainContentPanel.setBounds(200, 130, 800, 400);
        
        // Initialize panels
        statsPanel = createOverviewPanel();
        billsPanel = createBillsPanel();
        remindersPanel = createRemindersPanel();
        tipPanel = createTipPanel();
        
        mainContentPanel.add(statsPanel);
        mainContentPanel.add(billsPanel);
        mainContentPanel.add(remindersPanel);
        mainContentPanel.add(tipPanel);
        
        contentPanel.add(mainContentPanel);
        
        // Background SVG elements - Left
        jlbl_BackgroundLeft = new JLabel();
        jlbl_BackgroundLeft.setIcon(new ImageIcon(getClass().getResource("/assets/image/background_LEFT(12000x700).png")));
        jlbl_BackgroundLeft.setBounds(0, 0, 450, 205);
        contentPanel.add(jlbl_BackgroundLeft);
        
        // Background SVG elements - Right
        jlbl_BackgroundRight = new JLabel();
        jlbl_BackgroundRight.setIcon(new ImageIcon(getClass().getResource("/assets/image/background_RIGHT(12000x700).png")));
        jlbl_BackgroundRight.setBounds(750, 495, 450, 205);
        contentPanel.add(jlbl_BackgroundRight);
        
        // Add content panel to main panel
        add(contentPanel, BorderLayout.CENTER);
        
        // Initialize with current data
        resizeComponents();
    }
    
    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(52, 152, 219), 2));
        
        // Panel title
        JLabel titleLabel = new JLabel("Quick Overview");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 10));
        
        // Panel content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 10));
        
        // Add bill items with bullet points
        addBulletPoint(contentPanel, "Electricity (April): ", "$72.50", new Color(52, 152, 219));
        addBulletPoint(contentPanel, "Water (April): ", "$30.25", new Color(46, 204, 113));
        addBulletPoint(contentPanel, "Chores Completed: ", "7 of 10", new Color(226, 149, 90));
        
        // Button to view all accounts
        JButton viewDetailsButton = new JButton("View All Accounts");
        viewDetailsButton.setBackground(new Color(52, 152, 219));
        viewDetailsButton.setForeground(Color.WHITE);
        viewDetailsButton.setFont(new Font("Segoe UI", 0, 14));
        viewDetailsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewDetailsButton.setFocusPainted(false);
        
        viewDetailsButton.addActionListener(e -> {
            // Show accounts panel
            parentFrame.showPanel("accounts");
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(viewDetailsButton);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createBillsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(155, 89, 182), 2));
        
        // Panel title
        JLabel titleLabel = new JLabel("Recent Bills");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 10));
        
        // Panel content - bills table
        String[] columnNames = {"Utility", "Amount", "Due Date", "Status"};
        Object[][] data = {
            {"Electricity", "$72.50", "May 10", "Unpaid"},
            {"Water", "$30.25", "April 30", "Unpaid"},
            {"Gas", "$45.80", "April 20", "Paid"}
        };
        
        JTable billsTable = new JTable(data, columnNames);
        billsTable.setRowHeight(25);
        billsTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        billsTable.setShowGrid(false);
        billsTable.setIntercellSpacing(new Dimension(0, 0));
        billsTable.setFocusable(false);
        billsTable.setRowSelectionAllowed(false);
        
        // Set custom renderer for status column
        billsTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                         boolean isSelected, boolean hasFocus,
                                                         int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if ("Paid".equals(value)) {
                    c.setForeground(new Color(46, 204, 113));
                } else {
                    c.setForeground(new Color(231, 76, 60));
                }
                
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(billsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        // Pay bills button
        JButton payBillsButton = new JButton("Pay Bills");
        payBillsButton.setBackground(new Color(155, 89, 182));
        payBillsButton.setForeground(Color.WHITE);
        payBillsButton.setFont(new Font("Segoe UI", 0, 14));
        payBillsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        payBillsButton.setFocusPainted(false);
        
        payBillsButton.addActionListener(e -> {
            // Show payment panel
            parentFrame.showPanel("payments");
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(payBillsButton);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createRemindersPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(231, 76, 60), 2));
        
        // Panel title
        JLabel titleLabel = new JLabel("Reminders");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 10));
        
        // Panel content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 10));
        
        // Add reminder items
        addReminderItem(contentPanel, "Electricity meter reading due in 3 days");
        addReminderItem(contentPanel, "Water bill unpaid ($30.25)");
        addReminderItem(contentPanel, "Chore pending: \"Clean kitchen\"");
        
        // Add reminder button
        JButton addReminderButton = new JButton("Add Reminder");
        addReminderButton.setBackground(new Color(231, 76, 60));
        addReminderButton.setForeground(Color.WHITE);
        addReminderButton.setFont(new Font("Segoe UI", 0, 14));
        addReminderButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addReminderButton.setFocusPainted(false);
        
        addReminderButton.addActionListener(e -> {
            // Show a dialog to add a new reminder
            String newReminder = JOptionPane.showInputDialog(parentFrame, 
                                                         "Enter a new reminder:",
                                                         "Add Reminder",
                                                         JOptionPane.PLAIN_MESSAGE);
            
            if (newReminder != null && !newReminder.trim().isEmpty()) {
                // Add the new reminder to the panel
                addReminderItem(contentPanel, newReminder);
                contentPanel.revalidate();
                contentPanel.repaint();
            }
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(addReminderButton);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createTipPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(46, 204, 113), 2));
        
        // Panel title
        JLabel titleLabel = new JLabel("Tip of the Day");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 10));
        
        // Panel content - Tip of the day
        JLabel tipLabel = new JLabel("<html><i>\"Unplug devices when not in use to reduce standby power consumption and save on your electricity bill!\"</i></html>");
        tipLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tipLabel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        
        // Refresh tip button
        JButton refreshTipButton = new JButton("New Tip");
        refreshTipButton.setBackground(new Color(46, 204, 113));
        refreshTipButton.setForeground(Color.WHITE);
        refreshTipButton.setFont(new Font("Segoe UI", 0, 14));
        refreshTipButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshTipButton.setFocusPainted(false);
        
        // Sample tips
        String[] tips = {
            "Unplug devices when not in use to reduce standby power consumption and save on your electricity bill!",
            "Fix leaky faucets promptly - a single dripping tap can waste up to 3,000 gallons of water per year.",
            "Use LED bulbs instead of incandescent ones to save up to 80% on lighting energy costs.",
            "Run your dishwasher and washing machine only when full to maximize water and energy efficiency.",
            "Program your thermostat to reduce heating or cooling when you're away from home."
        };
        
        final int[] currentTipIndex = {0};
        
        refreshTipButton.addActionListener(e -> {
            // Show next tip
            currentTipIndex[0] = (currentTipIndex[0] + 1) % tips.length;
            tipLabel.setText("<html><i>\"" + tips[currentTipIndex[0]] + "\"</i></html>");
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(refreshTipButton);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(tipLabel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void addBulletPoint(JPanel panel, String label, String value, Color valueColor) {
        JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 3));
        itemPanel.setBackground(Color.WHITE);
        
        // Bullet point
        JLabel bulletLabel = new JLabel("•");
        bulletLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        // Label
        JLabel textLabel = new JLabel(label);
        textLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // Value
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        valueLabel.setForeground(valueColor);
        
        itemPanel.add(bulletLabel);
        itemPanel.add(textLabel);
        itemPanel.add(valueLabel);
        
        panel.add(itemPanel);
    }
    
    private void addReminderItem(JPanel panel, String reminderText) {
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setBackground(Color.WHITE);
        itemPanel.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
        
        // Checkbox
        JCheckBox checkbox = new JCheckBox();
        checkbox.setBackground(Color.WHITE);
        
        // Reminder text
        JLabel textLabel = new JLabel(reminderText);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Line through text when checked
        checkbox.addActionListener(e -> {
            if (checkbox.isSelected()) {
                textLabel.setText("<html><strike>" + reminderText + "</strike></html>");
            } else {
                textLabel.setText(reminderText);
            }
        });
        
        itemPanel.add(checkbox, BorderLayout.WEST);
        itemPanel.add(textLabel, BorderLayout.CENTER);
        
        panel.add(itemPanel);
    }
    
    private void resizeComponents() {
        int width = getWidth();
        int height = getHeight();
        
        // Calculate center positions
        int centerX = width / 2;
        
        // Adjust header position
        int headerWidth = Math.min(800, width - 100);
        contentPanel.getComponent(0).setBounds(centerX - (headerWidth / 2), 30, headerWidth, 80);
        
        // Adjust main content panel position
        int contentWidth = Math.min(800, width - 100);
        int contentHeight = 400;
        contentPanel.getComponent(1).setBounds(centerX - (contentWidth / 2), 130, contentWidth, contentHeight);
        
        // Position the SVG background elements
        // Left SVG - lower left
        jlbl_BackgroundLeft.setBounds(0, height - 205, 450, 205);
        
        // Right SVG - upper right
        jlbl_BackgroundRight.setBounds(width - 450, 0, 450, 205);
    }
    
    @Override
    public JPanel getPanel() {
        return this;
    }
    
    @Override
    public void refreshPanel() {
        try {
            // Update user name in welcome message
            User currentUser = parentFrame.getCurrentUser();
            String userName = (currentUser != null) ? currentUser.getFullName() : "User";
            
            JPanel welcomeTextPanel = (JPanel) ((JPanel) contentPanel.getComponent(0)).getComponent(1);
            JLabel userDateLabel = (JLabel) welcomeTextPanel.getComponent(1);
            userDateLabel.setText("Hello, " + userName + "! Today is " + 
                                 today.format(dateFormatter) + 
                                 " Hope you're having a great day keeping the household running smoothly!");
            
            // Get account data and update panels
            // This would typically involve database queries to get real-time data
            
            // Resize components to fit current panel size
            resizeComponents();
            
            // Refresh UI
            revalidate();
            repaint();
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage(),
                    "Data Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // This method retained from original implementation for database functionality
    private int getAccountCountByType(String accountType) {
        try {
            // Check if current user exists
            User currentUser = parentFrame.getCurrentUser();
            if (currentUser == null) {
                return 0;
            }
            
            // Get the current user ID
            int userId = currentUser.getId();
            
            // Get accounts for the current user
            List<Account> accounts = dbManager.getAccountManager().getAccountsByUserId(userId);
            
            // Count accounts of the specified type
            return (int) accounts.stream()
                .filter(account -> account.getType().equalsIgnoreCase(accountType))
                .count();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
}