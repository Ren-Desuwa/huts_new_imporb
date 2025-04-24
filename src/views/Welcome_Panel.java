package views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import models.*;
import database.*;
import ignoreme.Electricity;
import ignoreme.Electricity_Manager;
import ignoreme.Gas;
import ignoreme.Gas_Manager;
import ignoreme.Subscription;
import ignoreme.Subscription_Manager;
import ignoreme.Water;
import ignoreme.Water_Manager;

public class Welcome_Panel extends JPanel implements Utility_Panel {
    private Connection connection;
    private static final String DB_URL = "jdbc:sqlite:data/huts.db";
    private Main_Frame parentFrame;
    private Water_Manager waterManager;
    private Gas_Manager gasManager;
    private Electricity_Manager electricityManager;
    private Subscription_Manager subscriptionManager;
    
    // UI Components
    private JLabel welcomeLabel;
    private JPanel statsPanel;
    private JLabel jlbl_BackgroundLeft;
    private JLabel jlbl_BackgroundRight;
    private JPanel contentPanel;
    
    public Welcome_Panel(Main_Frame parentFrame, Database_Manager dbManager) {
        try {
            this.connection = DriverManager.getConnection(DB_URL);
            this.parentFrame = parentFrame;
            
            // Initialize managers
            this.waterManager = new Water_Manager(connection);
            this.gasManager = new Gas_Manager(connection);
            this.electricityManager = new Electricity_Manager(connection);
            this.subscriptionManager = new Subscription_Manager(connection);
            
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
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error connecting to database: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
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
        
        // Welcome label with icon
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        headerPanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel(new ImageIcon(getClass().getResource("/assets/icon/AccountBlack.png")));
        
        welcomeLabel = new JLabel("Welcome to House Utility Management System");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(23, 22, 22));
        
        headerPanel.add(iconLabel);
        headerPanel.add(welcomeLabel);
        headerPanel.setBounds(250, 60, 700, 50);
        
        contentPanel.add(headerPanel);
        
        // Initialize stats panel
        statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.setOpaque(false);
        statsPanel.setBounds(200, 150, 800, 350);
        contentPanel.add(statsPanel);
        
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
        
        // Load data
        resizeComponents();
        refreshPanel();
    }
    
    private void resizeComponents() {
        int width = getWidth();
        int height = getHeight();
        
        // Calculate center positions
        int centerX = width / 2;
        int centerY = height / 2;
        
        // Adjust header position
        int headerWidth = 700;
        int headerHeight = 50;
        contentPanel.getComponent(0).setBounds(centerX - (headerWidth / 2), 60, headerWidth, headerHeight);
        
        // Adjust stats panel position
        int statsPanelWidth = Math.min(800, width - 200);
        int statsPanelHeight = 350;
        statsPanel.setBounds(centerX - (statsPanelWidth / 2), 150, statsPanelWidth, statsPanelHeight);
        
        // Position the SVG background elements in the middle
        // Left SVG - center left
        jlbl_BackgroundLeft.setBounds(centerX - 450, centerY - 102, 450, 205);
        
        // Right SVG - center right
        jlbl_BackgroundRight.setBounds(centerX, centerY - 102, 450, 205);
    }
    
    @Override
    public JPanel getPanel() {
        return this;
    }
    
    @Override
    public void refreshPanel() {
        try {
            // Clear existing stats
            statsPanel.removeAll();
            
            // Get data from database
            List<Electricity> electricityAccounts = electricityManager.getAllElectricity();
            List<Gas> gasAccounts = gasManager.getAllGas();
            List<Water> waterAccounts = waterManager.getAllWater();
            List<Subscription> subscriptions = subscriptionManager.getAllSubscriptions();
            
            // Add stat panels with matching style to Forgot Password panel
            statsPanel.add(createStatPanel("Electricity Accounts", electricityAccounts.size(), new Color(52, 152, 219)));
            statsPanel.add(createStatPanel("Gas Accounts", gasAccounts.size(), new Color(155, 89, 182)));
            statsPanel.add(createStatPanel("Water Accounts", waterAccounts.size(), new Color(46, 204, 113)));
            statsPanel.add(createStatPanel("Active Subscriptions", subscriptions.size(), new Color(226, 149, 90)));
            
            // Resize components to fit current panel size
            resizeComponents();
            
            // Refresh UI
            statsPanel.revalidate();
            statsPanel.repaint();
            
            // Force layout update for the entire panel
            revalidate();
            repaint();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage(),
                    "Data Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JPanel createStatPanel(String title, int count, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(color, 2));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        
        JLabel countLabel = new JLabel(String.valueOf(count));
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        countLabel.setForeground(color);
        countLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // Add a button with matching style to Forgot Password panel
        JButton detailsButton = new JButton("View Details");
        detailsButton.setBackground(color);
        detailsButton.setForeground(Color.WHITE);
        detailsButton.setFont(new Font("Segoe UI", 0, 14));
        detailsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        detailsButton.setFocusPainted(false);
        
        // Button action based on panel type
        detailsButton.addActionListener(e -> {
            switch (title) {
                case "Electricity Accounts":
                    parentFrame.showPanel("electricity");
                    break;
                case "Gas Accounts":
                    parentFrame.showPanel("gas");
                    break;
                case "Water Accounts":
                    parentFrame.showPanel("water");
                    break;
                case "Active Subscriptions":
                    parentFrame.showPanel("subscription");
                    break;
            }
        });
        
        // Create a panel for the button (for better spacing)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(detailsButton);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(countLabel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
}