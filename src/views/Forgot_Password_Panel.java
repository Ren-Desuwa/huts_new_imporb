package views;

import database.Database_Manager;
import database.User_Manager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class Forgot_Password_Panel extends JPanel {
    private final User_Manager userManager;
    private final Main_Frame mainFrame;

    // UI Components
    private JLabel jlbl_ForgotPassword;
    private JTextField jtf_Username;
    private JLabel jlbl_Username;
    private JButton jbtn_ResetPassword;
    private JPasswordField jpf_NewPassword;
    private JPasswordField jpf_ConfirmPassword;
    private JLabel jlbl_NewPassword;
    private JLabel jlbl_ConfirmPassword;
    private JLabel jlbl_BackToLogin;
    private JLabel jlbl_Account_Icon;
    private JPanel contentPanel;

    /**
     * Creates new Forgot Password Panel
     */
    public Forgot_Password_Panel(Main_Frame mainFrame, User_Manager userManager) {
        this.userManager = userManager;
        this.mainFrame = mainFrame;
        initComponents();
    }

    private void initComponents() {
        // Set background and layout
        setBackground(new Color(35, 50, 90));
        setLayout(new BorderLayout());
        
        // Create content panel with BorderLayout
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create form panel with responsive GridBagLayout
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        
        // Initialize components
        jlbl_ForgotPassword = new JLabel("Reset Password");
        jlbl_ForgotPassword.setFont(new Font("Segoe UI", 0, 24));
        jlbl_ForgotPassword.setForeground(new Color(23, 22, 22));
        jlbl_ForgotPassword.setHorizontalAlignment(SwingConstants.CENTER);
        
        jlbl_Account_Icon = new JLabel();
        jlbl_Account_Icon.setIcon(new ImageIcon(getClass().getResource("/assets/icon/AccountBlack.png")));
        
        // Username components
        jtf_Username = new JTextField("Enter Username");
        jtf_Username.setForeground(new Color(23, 22, 22));
        jtf_Username.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                if ("Enter Username".equals(jtf_Username.getText())) {
                    jtf_Username.setText("");
                }
            }
            public void focusLost(FocusEvent evt) {
                if ("".equals(jtf_Username.getText())) {
                    jtf_Username.setText("Enter Username");
                }
            }
        });
        
        jlbl_Username = new JLabel("Username");
        jlbl_Username.setForeground(new Color(23, 22, 22));
        
        // New Password components
        jpf_NewPassword = new JPasswordField("Enter New Password");
        jpf_NewPassword.setForeground(new Color(23, 22, 22));
        jpf_NewPassword.setEchoChar((char) 0);
        jpf_NewPassword.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                if ("Enter New Password".equals(new String(jpf_NewPassword.getPassword()))) {
                    jpf_NewPassword.setEchoChar('\u2022');
                    jpf_NewPassword.setText("");
                }
            }
            public void focusLost(FocusEvent evt) {
                if (new String(jpf_NewPassword.getPassword()).isEmpty()) {
                    jpf_NewPassword.setEchoChar((char) 0);
                    jpf_NewPassword.setText("Enter New Password");
                }
            }
        });
        
        jlbl_NewPassword = new JLabel("New Password");
        jlbl_NewPassword.setForeground(new Color(23, 22, 22));
        
        // Confirm Password components
        jpf_ConfirmPassword = new JPasswordField("Confirm New Password");
        jpf_ConfirmPassword.setForeground(new Color(23, 22, 22));
        jpf_ConfirmPassword.setEchoChar((char) 0);
        jpf_ConfirmPassword.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                if ("Confirm New Password".equals(new String(jpf_ConfirmPassword.getPassword()))) {
                    jpf_ConfirmPassword.setEchoChar('\u2022');
                    jpf_ConfirmPassword.setText("");
                }
            }
            public void focusLost(FocusEvent evt) {
                if (new String(jpf_ConfirmPassword.getPassword()).isEmpty()) {
                    jpf_ConfirmPassword.setEchoChar((char) 0);
                    jpf_ConfirmPassword.setText("Confirm New Password");
                }
            }
        });
        
        jlbl_ConfirmPassword = new JLabel("Confirm Password");
        jlbl_ConfirmPassword.setForeground(new Color(23, 22, 22));
        
        // Reset Password button
        jbtn_ResetPassword = new JButton("Reset Password");
        jbtn_ResetPassword.setBackground(new Color(226, 149, 90));
        jbtn_ResetPassword.setFont(new Font("Segoe UI", 0, 18));
        jbtn_ResetPassword.setForeground(new Color(255, 255, 255));
        jbtn_ResetPassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        jbtn_ResetPassword.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jbtn_ResetPasswordActionPerformed(evt);
            }
        });
        
        // Back to Login link
        jlbl_BackToLogin = new JLabel("Back to Login");
        jlbl_BackToLogin.setForeground(new Color(23, 22, 22));
        jlbl_BackToLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        jlbl_BackToLogin.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                jlbl_BackToLoginMouseClicked(evt);
            }
            public void mouseEntered(MouseEvent evt) {
                jlbl_BackToLogin.setText("<html><u>Back to Login</u></html>");
            }
            public void mouseExited(MouseEvent evt) {
                jlbl_BackToLogin.setText("Back to Login");
            }
        });
        
        // Header panel with icon and title
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setOpaque(false);
        headerPanel.add(jlbl_Account_Icon);
        headerPanel.add(jlbl_ForgotPassword);
        
        // Add components to form panel using GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Username section
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(jlbl_Username, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        formPanel.add(jtf_Username, gbc);
        
        // New Password section
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        formPanel.add(jlbl_NewPassword, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx =.0;
        formPanel.add(jpf_NewPassword, gbc);
        
        // Confirm Password section
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        formPanel.add(jlbl_ConfirmPassword, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        formPanel.add(jpf_ConfirmPassword, gbc);
        
        // Button and back to login link in separate panel for centering
        JPanel actionPanel = new JPanel(new GridBagLayout());
        actionPanel.setOpaque(false);
        
        GridBagConstraints actionConstraints = new GridBagConstraints();
        actionConstraints.gridx = 0;
        actionConstraints.gridy = 0;
        actionConstraints.insets = new Insets(15, 0, 10, 0);
        actionPanel.add(jbtn_ResetPassword, actionConstraints);
        
        actionConstraints.gridy = 1;
        actionConstraints.insets = new Insets(5, 0, 5, 0);
        actionPanel.add(jlbl_BackToLogin, actionConstraints);
        
        // Add panels to content panel
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(actionPanel, BorderLayout.SOUTH);
        
        // Create a wrapper panel to center the form
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(contentPanel);
        
        // Add background
        JLabel jlbl_Background = new JLabel(new ImageIcon(getClass().getResource("/assets/image/background(900x410).png")));
        jlbl_Background.setLayout(new BorderLayout());
        jlbl_Background.add(wrapperPanel, BorderLayout.CENTER);
        
        // Add background to main panel
        add(jlbl_Background, BorderLayout.CENTER);
    }

    private void jbtn_ResetPasswordActionPerformed(ActionEvent evt) {
        String username = jtf_Username.getText().trim();
        String newPassword = new String(jpf_NewPassword.getPassword()).trim();
        String confirmPassword = new String(jpf_ConfirmPassword.getPassword()).trim();
        
        // Validate input
        if (username.equals("Enter Username") || username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your username", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (newPassword.equals("Enter New Password") || newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a new password", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (confirmPassword.equals("Confirm New Password") || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please confirm your new password", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Check if username exists
            if (!userManager.userExists(username)) {
                JOptionPane.showMessageDialog(this, "Username does not exist", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Update password
            boolean success = userManager.updateUserPassword(username, newPassword);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Password reset successful! Please log in with your new password.", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                mainFrame.showLoginPanel();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to reset password", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while resetting password", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void jlbl_BackToLoginMouseClicked(MouseEvent evt) {
        mainFrame.showLoginPanel();
    }
    
    public JPanel getPanel() {
        return this;
    }
    
    public void refreshPanel() {
        // Reset fields
        jtf_Username.setText("Enter Username");
        jpf_NewPassword.setText("Enter New Password");
        jpf_NewPassword.setEchoChar((char) 0);
        jpf_ConfirmPassword.setText("Confirm New Password");
        jpf_ConfirmPassword.setEchoChar((char) 0);
    }
}