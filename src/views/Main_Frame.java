package views;

import models.*;
import views.logins.Forgot_Password_Panel;
import views.logins.Login_Panel;
import views.logins.Sign_Up_Panel;
import views.panels.Electricity_Panel;
import views.panels.Profile_Panel;
import views.panels.Water_Panel;
import views.panels.Welcome_Panel;

import javax.swing.*;
import database.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Main_Frame extends JFrame {
    // Maps to store previous readings for bill calculations
    private Map<String, Double> previousElectricityReadings = new HashMap<>();
    private Map<String, Double> previousGasReadings = new HashMap<>();
    private Map<String, Double> previousWaterReadings = new HashMap<>();
    
    // Current logged-in user
    private User currentUser = null;
    
    // UI components
    private JPanel mainPanel;
    private JPanel menuPanel;
    private JPanel contentPanel;
    private final CardLayout cardLayout;
    
    // Panels
    private Login_Panel loginPanel;
    private Forgot_Password_Panel forgotPasswordPanel;
    private Sign_Up_Panel signUpPanel;
    private Welcome_Panel welcomePanel;
    private Electricity_Panel electricityPanel;
    private Water_Panel waterPanel;
    private Profile_Panel profilePanel;
    
    // Constants for panel names
    private static final String MAIN_CONTENT_PANEL = "MAIN_CONTENT_PANEL";
    private static final String LOGIN_PANEL = "login";
    private static final String FORGOT_PASSWORD_PANEL = "forgot_password";
    private static final String SIGNUP_PANEL = "signup";
    private static final String WELCOME_PANEL = "welcome";
    private static final String ELECTRICITY_PANEL = "electricity";
    private static final String WATER_PANEL = "water";
    private static final String PROFILE_PANEL = "profile";
    
    // Size constants
    private static final Dimension AUTH_PANEL_SIZE = new Dimension(600, 400);
    private static final Dimension MAIN_PANEL_SIZE = new Dimension(1200, 700);
    
    // Database manager instance
    private Database_Manager dbManager;
    
    public Main_Frame() {
        // Initialize database manager
        dbManager = Database_Manager.getInstance();
        
        // Set up window properties
        setTitle("Home Utility Tracking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        
        // Create card layout and content panel
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        
        // Initialize panels
        initPanels();
        
        // Add panels to content pane
        getContentPane().add(contentPanel);
        
        // Set initial size for login panel
        setSize(AUTH_PANEL_SIZE);
        
        // Center on screen
        setLocationRelativeTo(null);
    }
    
    private void initPanels() {
        // Create authentication panels
        loginPanel = new Login_Panel(this, dbManager.getUserManager());
        signUpPanel = new Sign_Up_Panel(this, dbManager.getUserManager());
        forgotPasswordPanel = new Forgot_Password_Panel(this, dbManager.getUserManager());
        
        // Initialize main content panel components
        initMainContentPanel();
        
        // Add panels to content panel with unique identifiers
        contentPanel.add(loginPanel.getPanel(), LOGIN_PANEL);
        contentPanel.add(signUpPanel.getPanel(), SIGNUP_PANEL);
        contentPanel.add(forgotPasswordPanel.getPanel(), FORGOT_PASSWORD_PANEL);
        contentPanel.add(mainPanel, MAIN_CONTENT_PANEL);
    }

    private void initMainContentPanel() {
        // Create main panel with border layout
        mainPanel = new JPanel(new BorderLayout());
        
        // Create menu panel
        menuPanel = new JPanel();
        menuPanel.setPreferredSize(new Dimension(200, 0));
        menuPanel.setBackground(new Color(50, 57, 64));
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        
        // Add menu buttons
        addMenuButtons();
        
        // Create content panel
        JPanel contentArea = new JPanel(new CardLayout());
        contentArea.setBackground(Color.WHITE);
        
        // Initialize content panels
        welcomePanel = new Welcome_Panel(this, dbManager);
        electricityPanel = new Electricity_Panel(this, currentUser);
        waterPanel = new Water_Panel(this, currentUser);
        profilePanel = new Profile_Panel(this); // Initialize the profile panel
        
        // Add panels to content area
        contentArea.add(welcomePanel.getPanel(), "WELCOME");
        contentArea.add(electricityPanel.getPanel(), "ELECTRICITY");
        contentArea.add(waterPanel.getPanel(), "WATER");
        contentArea.add(profilePanel.getPanel(), "PROFILE"); // Add profile panel to content area
        
        // Add components to main panel
        mainPanel.add(menuPanel, BorderLayout.WEST);
        mainPanel.add(contentArea, BorderLayout.CENTER);
    }
    
    private void addMenuButtons() {
        JButton dashboardBtn = createMenuButton("Dashboard");
        JButton electricityBtn = createMenuButton("Electricity");
        JButton waterBtn = createMenuButton("Water");
        JButton profileBtn = createMenuButton("Profile");  // New Profile button
        JButton logoutBtn = createMenuButton("Logout");
        
        // Add action listeners
        dashboardBtn.addActionListener(e -> showWelcomePanel());
        electricityBtn.addActionListener(e -> showElectricityPanel());
        waterBtn.addActionListener(e -> showWaterPanel());
        profileBtn.addActionListener(e -> showProfilePanel());  // Add listener for profile
        logoutBtn.addActionListener(e -> logout());
        
        // Add buttons to menu panel
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        menuPanel.add(createTitleLabel("HUTS"));
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        menuPanel.add(dashboardBtn);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        menuPanel.add(electricityBtn);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        menuPanel.add(waterBtn);
        menuPanel.add(Box.createVerticalGlue());
        menuPanel.add(profileBtn);  // Add Profile button above Logout
        menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));  // Add spacing
        menuPanel.add(logoutBtn);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));
    }
    
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 40));
        button.setFocusPainted(false);
        button.setBackground(new Color(80, 87, 94));
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(100, 107, 114));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(80, 87, 94));
            }
        });
        
        return button;
    }
    
    private JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        return label;
    }
    
    // Method to set the current user after successful login
    public void setCurrentUser(User user) {
        this.currentUser = user;
        
        // Update the user in all panels that need it
        if (profilePanel != null) profilePanel.setCurrentUser(user);
        if (electricityPanel != null) electricityPanel.setCurrentUser(user);
        //if (waterPanel != null) waterPanel.setCurrentUser(user);
        
        // Update other panels similarly if they need user information
    }
    
    // Panel display methods
    private void showWelcomePanel() {
        CardLayout cardLayout = (CardLayout) ((JPanel) mainPanel.getComponent(1)).getLayout();
        welcomePanel.refreshPanel();
        cardLayout.show((JPanel) mainPanel.getComponent(1), "WELCOME");
    }
    
    private void showElectricityPanel() {
        CardLayout cardLayout = (CardLayout) ((JPanel) mainPanel.getComponent(1)).getLayout();
        electricityPanel.refreshPanel();
        cardLayout.show((JPanel) mainPanel.getComponent(1), "ELECTRICITY");
    }

    private void showWaterPanel() {
        CardLayout cardLayout = (CardLayout) ((JPanel) mainPanel.getComponent(1)).getLayout();
        waterPanel.refreshPanel();
        cardLayout.show((JPanel) mainPanel.getComponent(1), "WATER");
    }
    
    private void showProfilePanel() {
        CardLayout cardLayout = (CardLayout) ((JPanel) mainPanel.getComponent(1)).getLayout();
        profilePanel.refreshPanel();
        cardLayout.show((JPanel) mainPanel.getComponent(1), "PROFILE");
    }
    
    // Accessor methods that might be needed by panels
    public Database_Manager getDbManager() {
        return dbManager;
    }
    
    public User_Manager getUserManager() {
        return dbManager.getUserManager();
    }
    
    public Account_Manager getAccountManager() {
        return dbManager.getAccountManager();
    }
    
    public Bill_Manager getBillManager() {
        return dbManager.getBillManager();
    }
    
    public Reading_History_Manager getReadingHistoryManager() {
        return dbManager.getReadingHistoryManager();
    }
    
    public void showLoginPanel() {
        // Resize the frame for the login panel
        resizeFrame(AUTH_PANEL_SIZE);
        loginPanel.refreshPanel();
        cardLayout.show(contentPanel, LOGIN_PANEL);
    }
    
    /**
     * Shows the sign up panel
     */
    public void showSignUpPanel() {
        // Resize the frame for the sign up panel
        resizeFrame(AUTH_PANEL_SIZE);
        signUpPanel.refreshPanel();
        cardLayout.show(contentPanel, SIGNUP_PANEL);
    }
    
    /**
     * Shows the forgot password panel
     */
    public void showForgotPasswordPanel() {
        // Resize the frame for the forgot password panel
        resizeFrame(AUTH_PANEL_SIZE);
        forgotPasswordPanel.refreshPanel();
        cardLayout.show(contentPanel, FORGOT_PASSWORD_PANEL);
    }
    
    /**
     * Shows the main content panel after successful login
     */
    public void showMainContent() {
        // Resize the frame for the main panel
        resizeFrame(MAIN_PANEL_SIZE);
        
        // Show main content panel
        cardLayout.show(contentPanel, MAIN_CONTENT_PANEL);
        
        // Default to welcome panel
        showWelcomePanel();
        
        // Force revalidation to ensure proper sizing
        revalidateAllPanels();
        validate();
        repaint();
    }
    
    /**
     * Resizes the frame to fit the specified dimension
     * @param dimension the target dimension
     */
    private void resizeFrame(Dimension dimension) {
        setSize(dimension);
        setLocationRelativeTo(null); // Re-center the frame
    }
    
    /**
     * Logs out the user and returns to login panel
     */
    public void logout() {
        // Clear user data in panels
        if (profilePanel != null) {
            profilePanel.clearUserData();
        }
        if (electricityPanel != null) {
            electricityPanel.clearUserData();
        }
        
        // Clear current user reference
        this.currentUser = null;
        
        // Return to login screen
        showLoginPanel();
    }
    
    public void showPanel(String panelName) {
        // Refresh the panel before showing it
        refreshPanel(panelName);
        
        // Resize frame based on panel type
        if (panelName.equals(LOGIN_PANEL) || panelName.equals(FORGOT_PASSWORD_PANEL) || panelName.equals(SIGNUP_PANEL)) {
            resizeFrame(AUTH_PANEL_SIZE);
        } else {
            resizeFrame(MAIN_PANEL_SIZE);
        }
        
        // Get the content area panel which is the center component of mainPanel
        if (panelName.equals(LOGIN_PANEL) || panelName.equals(FORGOT_PASSWORD_PANEL) || panelName.equals(SIGNUP_PANEL)) {
            // These panels are in the outer contentPanel, not in contentArea
            this.cardLayout.show(contentPanel, panelName);
        } else {
            JPanel contentArea = (JPanel) mainPanel.getComponent(1);
            CardLayout cardLayout = (CardLayout) contentArea.getLayout();
            
            // Show the panel using the correct panel mappings
            switch (panelName.toLowerCase()) {
                case WELCOME_PANEL:
                    cardLayout.show(contentArea, "WELCOME");
                    break;
                case ELECTRICITY_PANEL:
                    cardLayout.show(contentArea, "ELECTRICITY");
                    break;
                case WATER_PANEL:
                    cardLayout.show(contentArea, "WATER");
                    break;
                case PROFILE_PANEL:
                    cardLayout.show(contentArea, "PROFILE");
                    break;
                default:
                    System.out.println("Invalid panel name: " + panelName);
                    break;
            }
        }
    }

    // Update the refreshPanel method to include profilePanel
    private void refreshPanel(String panelName) {
        switch (panelName.toLowerCase()) {
            case LOGIN_PANEL:
                loginPanel.refreshPanel();
                break;
            case FORGOT_PASSWORD_PANEL:
                forgotPasswordPanel.refreshPanel();
                break;
            case SIGNUP_PANEL:
                signUpPanel.refreshPanel();
                break;
            case WELCOME_PANEL:
                welcomePanel.refreshPanel();
                break;
            case ELECTRICITY_PANEL:
                electricityPanel.refreshPanel();
                break;
            case WATER_PANEL:
                waterPanel.refreshPanel();
                break;
            case PROFILE_PANEL:
                profilePanel.refreshPanel();
                break;
        }
    }
    
    private void revalidateAllPanels() {
        loginPanel.refreshPanel();
        signUpPanel.refreshPanel();
        forgotPasswordPanel.refreshPanel();
        
        // Call refresh on other panels if they have it
        if (welcomePanel != null) welcomePanel.refreshPanel();
        if (electricityPanel != null) electricityPanel.refreshPanel();
        if (profilePanel != null) profilePanel.refreshPanel();
    }
    
    // Getter for current user
    public User getCurrentUser() {    
        return currentUser;
    }
}