package views.panels;

import models.User;
import views.Main_Frame;
import database.User_Manager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Profile_Panel {
    private JPanel panel;
    private User currentUser;
    private Main_Frame mainFrame;
    private User_Manager userManager;
    
    // UI Components
    private JTextField usernameField;
    private JTextField fullNameField;
    private JTextField emailField;
    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JButton updateProfileButton;
    private JButton changePasswordButton;
    private JLabel statusLabel;
    
    public Profile_Panel(Main_Frame mainFrame) {
        this.mainFrame = mainFrame;
        this.userManager = mainFrame.getUserManager();
        
        initComponents();
    }
    
    private void initComponents() {
        // Create main panel with BorderLayout
        panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Create title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(new Color(240, 240, 240));
        titlePanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("My Profile");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);
        
        // Create content panel with two sections: Profile and Password
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);
        
        // Create profile panel
        JPanel profilePanel = createProfilePanel();
        
        // Create password panel
        JPanel passwordPanel = createPasswordPanel();
        
        // Add panels to content
        contentPanel.add(profilePanel);
        contentPanel.add(passwordPanel);
        
        // Create status panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBorder(new EmptyBorder(0, 20, 20, 20));
        statusPanel.setBackground(Color.WHITE);
        
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        statusPanel.add(statusLabel);
        
        // Add components to main panel
        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        panel.add(statusPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createProfilePanel() {
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.setBorder(BorderFactory.createTitledBorder("Profile Information"));
        profilePanel.setBackground(Color.WHITE);
        
        // Create form elements
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        usernameField = new JTextField(20);
        usernameField.setEditable(false); // Username cannot be changed
        formPanel.add(usernameField, gbc);
        
        // Full Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Full Name:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        fullNameField = new JTextField(20);
        formPanel.add(fullNameField, gbc);
        
        // Email
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);
        
        // Update Button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        updateProfileButton = new JButton("Update Profile");
        updateProfileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateProfile();
            }
        });
        buttonPanel.add(updateProfileButton);
        
        // Add components to profile panel
        profilePanel.add(formPanel);
        profilePanel.add(Box.createVerticalGlue());
        profilePanel.add(buttonPanel);
        
        return profilePanel;
    }
    
    private JPanel createPasswordPanel() {
        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.Y_AXIS));
        passwordPanel.setBorder(BorderFactory.createTitledBorder("Change Password"));
        passwordPanel.setBackground(Color.WHITE);
        
        // Create form elements
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Current Password
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Current Password:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        currentPasswordField = new JPasswordField(20);
        formPanel.add(currentPasswordField, gbc);
        
        // New Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("New Password:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        newPasswordField = new JPasswordField(20);
        formPanel.add(newPasswordField, gbc);
        
        // Confirm Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Confirm Password:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        confirmPasswordField = new JPasswordField(20);
        formPanel.add(confirmPasswordField, gbc);
        
        // Change Password Button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        changePasswordButton = new JButton("Change Password");
        changePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changePassword();
            }
        });
        buttonPanel.add(changePasswordButton);
        
        // Add components to password panel
        passwordPanel.add(formPanel);
        passwordPanel.add(Box.createVerticalGlue());
        passwordPanel.add(buttonPanel);
        
        return passwordPanel;
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        refreshPanel();
    }
    
    public void refreshPanel() {
        if (currentUser != null) {
            usernameField.setText(currentUser.getUsername());
            fullNameField.setText(currentUser.getFullName());
            emailField.setText(currentUser.getEmail());
            
            // Clear password fields
            currentPasswordField.setText("");
            newPasswordField.setText("");
            confirmPasswordField.setText("");
            
            // Clear status
            setStatus(" ", Color.BLACK);
        }
    }
    
    private void updateProfile() {
        if (currentUser == null) {
            setStatus("No user is currently logged in.", Color.RED);
            return;
        }
        
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        
        // Simple validation
        if (fullName.isEmpty() || email.isEmpty()) {
            setStatus("Full name and email cannot be empty.", Color.RED);
            return;
        }
        
        // Update user object
        currentUser.setFullName(fullName);
        currentUser.setEmail(email);
        
        // Save to database
        boolean success = userManager.updateUser(currentUser);
        
        if (success) {
            setStatus("Profile updated successfully!", new Color(0, 128, 0)); // Green
        } else {
            setStatus("Failed to update profile. Please try again.", Color.RED);
        }
    }
    
    private void changePassword() {
        if (currentUser == null) {
            setStatus("No user is currently logged in.", Color.RED);
            return;
        }
        
        String currentPassword = new String(currentPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        // Validate current password
        if (!currentPassword.equals(currentUser.getPassword())) {
            setStatus("Current password is incorrect.", Color.RED);
            return;
        }
        
        // Validate new password
        if (newPassword.isEmpty()) {
            setStatus("New password cannot be empty.", Color.RED);
            return;
        }
        
        // Check if new passwords match
        if (!newPassword.equals(confirmPassword)) {
            setStatus("New passwords do not match.", Color.RED);
            return;
        }
        
        // Update user object
        currentUser.setPassword(newPassword);
        
        // Save to database
        boolean success = userManager.updateUser(currentUser);
        
        if (success) {
            setStatus("Password changed successfully!", new Color(0, 128, 0)); // Green
            currentPasswordField.setText("");
            newPasswordField.setText("");
            confirmPasswordField.setText("");
        } else {
            setStatus("Failed to change password. Please try again.", Color.RED);
        }
    }
    
    private void setStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }
    
    public void clearUserData() {
        currentUser = null;
        usernameField.setText("");
        fullNameField.setText("");
        emailField.setText("");
        currentPasswordField.setText("");
        newPasswordField.setText("");
        confirmPasswordField.setText("");
        setStatus(" ", Color.BLACK);
    }
    
    public JPanel getPanel() {
        return panel;
    }
}