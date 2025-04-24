package views.logins;

import database.User_Manager;
import models.User;
import views.Main_Frame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.UUID;

public class Sign_Up_Panel extends JPanel {
    private final User_Manager userManager;
    private final Main_Frame mainFrame;

    // UI Components
    private JLabel jlbl_SignUp;
    private JButton jbtn_SignUp;
    private JTextField jtf_Username;
    private JPasswordField jpf_Password;
    private JPasswordField jpf_ConfirmPassword;
    private JTextField jtf_Email;
    private JTextField jtf_FullName;
    private JLabel jlbl_Password;
    private JLabel jlbl_ConfirmPassword;
    private JLabel jlbl_Username;
    private JLabel jlbl_Email;
    private JLabel jlbl_FullName;
    private JLabel jlbl_Login;
    private JLabel jlbl_Account_Icon;
    private JPanel contentPanel;

    /**
     * Creates new Sign Up Panel
     */
    public Sign_Up_Panel(Main_Frame mainFrame, User_Manager userManager) {
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
        jlbl_SignUp = new JLabel("Sign Up");
        jlbl_SignUp.setFont(new Font("Segoe UI", 0, 24));
        jlbl_SignUp.setForeground(new Color(23, 22, 22));
        jlbl_SignUp.setHorizontalAlignment(SwingConstants.CENTER);
        
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
        
        // Email components
        jtf_Email = new JTextField("Enter Email");
        jtf_Email.setForeground(new Color(23, 22, 22));
        jtf_Email.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                if ("Enter Email".equals(jtf_Email.getText())) {
                    jtf_Email.setText("");
                }
            }
            public void focusLost(FocusEvent evt) {
                if ("".equals(jtf_Email.getText())) {
                    jtf_Email.setText("Enter Email");
                }
            }
        });
        
        jlbl_Email = new JLabel("Email");
        jlbl_Email.setForeground(new Color(23, 22, 22));
        
        // Full Name components
        jtf_FullName = new JTextField("Enter Full Name");
        jtf_FullName.setForeground(new Color(23, 22, 22));
        jtf_FullName.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                if ("Enter Full Name".equals(jtf_FullName.getText())) {
                    jtf_FullName.setText("");
                }
            }
            public void focusLost(FocusEvent evt) {
                if ("".equals(jtf_FullName.getText())) {
                    jtf_FullName.setText("Enter Full Name");
                }
            }
        });
        
        jlbl_FullName = new JLabel("Full Name");
        jlbl_FullName.setForeground(new Color(23, 22, 22));
        
        // Password components
        jpf_Password = new JPasswordField("Enter Password");
        jpf_Password.setForeground(new Color(23, 22, 22));
        jpf_Password.setEchoChar((char) 0);
        jpf_Password.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                if ("Enter Password".equals(new String(jpf_Password.getPassword()))) {
                    jpf_Password.setEchoChar('\u2022');
                    jpf_Password.setText("");
                }
            }
            public void focusLost(FocusEvent evt) {
                if (new String(jpf_Password.getPassword()).isEmpty()) {
                    jpf_Password.setEchoChar((char) 0);
                    jpf_Password.setText("Enter Password");
                }
            }
        });
        
        jlbl_Password = new JLabel("Password");
        jlbl_Password.setForeground(new Color(23, 22, 22));
        
        // Confirm Password components
        jpf_ConfirmPassword = new JPasswordField("Confirm Password");
        jpf_ConfirmPassword.setForeground(new Color(23, 22, 22));
        jpf_ConfirmPassword.setEchoChar((char) 0);
        jpf_ConfirmPassword.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                if ("Confirm Password".equals(new String(jpf_ConfirmPassword.getPassword()))) {
                    jpf_ConfirmPassword.setEchoChar('\u2022');
                    jpf_ConfirmPassword.setText("");
                }
            }
            public void focusLost(FocusEvent evt) {
                if (new String(jpf_ConfirmPassword.getPassword()).isEmpty()) {
                    jpf_ConfirmPassword.setEchoChar((char) 0);
                    jpf_ConfirmPassword.setText("Confirm Password");
                }
            }
        });
        
        jlbl_ConfirmPassword = new JLabel("Confirm Password");
        jlbl_ConfirmPassword.setForeground(new Color(23, 22, 22));
        
        // Sign Up button
        jbtn_SignUp = new JButton("Sign Up");
        jbtn_SignUp.setBackground(new Color(226, 149, 90));
        jbtn_SignUp.setFont(new Font("Segoe UI", 0, 18));
        jbtn_SignUp.setForeground(new Color(255, 255, 255));
        jbtn_SignUp.setCursor(new Cursor(Cursor.HAND_CURSOR));
        jbtn_SignUp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jbtn_SignUpActionPerformed(evt);
            }
        });
        
        // Login link
        jlbl_Login = new JLabel("Already have an account? Login");
        jlbl_Login.setForeground(new Color(23, 22, 22));
        jlbl_Login.setCursor(new Cursor(Cursor.HAND_CURSOR));
        jlbl_Login.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                jlbl_LoginMouseClicked(evt);
            }
            public void mouseEntered(MouseEvent evt) {
                jlbl_Login.setText("<html><u>Already have an account? Login</u></html>");
            }
            public void mouseExited(MouseEvent evt) {
                jlbl_Login.setText("Already have an account? Login");
            }
        });
        
        // Header panel with icon and title
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setOpaque(false);
        headerPanel.add(jlbl_Account_Icon);
        headerPanel.add(jlbl_SignUp);
        
        // Add components to form panel using GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 5, 4, 5);
        
        // Username section
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(jlbl_Username, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        formPanel.add(jtf_Username, gbc);
        
        // Email section
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(jlbl_Email, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        formPanel.add(jtf_Email, gbc);
        
        // Full Name section
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        formPanel.add(jlbl_FullName, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        formPanel.add(jtf_FullName, gbc);
        
        // Password section
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        formPanel.add(jlbl_Password, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        formPanel.add(jpf_Password, gbc);
        
        // Confirm Password section
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        formPanel.add(jlbl_ConfirmPassword, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        formPanel.add(jpf_ConfirmPassword, gbc);
        
        // Button and login link in separate panel for centering
        JPanel actionPanel = new JPanel(new GridBagLayout());
        actionPanel.setOpaque(false);
        
        GridBagConstraints actionConstraints = new GridBagConstraints();
        actionConstraints.gridx = 0;
        actionConstraints.gridy = 0;
        actionConstraints.insets = new Insets(10, 0, 10, 0);
        actionPanel.add(jbtn_SignUp, actionConstraints);
        
        actionConstraints.gridy = 1;
        actionConstraints.insets = new Insets(5, 0, 5, 0);
        actionPanel.add(jlbl_Login, actionConstraints);
        
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

    private void jbtn_SignUpActionPerformed(ActionEvent evt) {
        String username = jtf_Username.getText().trim();
        String email = jtf_Email.getText().trim();
        String fullName = jtf_FullName.getText().trim();
        String password = new String(jpf_Password.getPassword()).trim();
        String confirmPassword = new String(jpf_ConfirmPassword.getPassword()).trim();
        
        // Validate input
        if (username.equals("Enter Username") || username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a username", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (email.equals("Enter Email") || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an email", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (fullName.equals("Enter Full Name") || fullName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your full name", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (password.equals("Enter Password") || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a password", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (confirmPassword.equals("Confirm Password") || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please confirm your password", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Check if username already exists using User_Manager
            if (userManager.userExists(username)) {
                JOptionPane.showMessageDialog(this, "Username already exists", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Generate a UUID for the user
            String userId = userManager.generateNextUserId();
            User newUser = new User(userId, username, password, email, fullName);
            
            // Use User_Manager to add the user
            boolean success = userManager.addUser(newUser);
            
            if (success) {
                // Registration was successful
                JOptionPane.showMessageDialog(this, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Navigate to the login panel
                mainFrame.showLoginPanel();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to register user", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred during registration", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void jlbl_LoginMouseClicked(MouseEvent evt) {
        mainFrame.showLoginPanel();
    }
    
    public JPanel getPanel() {
        return this;
    }
    
    public void refreshPanel() {
        // Reset fields
        jtf_Username.setText("Enter Username");
        jtf_Email.setText("Enter Email");
        jtf_FullName.setText("Enter Full Name");
        jpf_Password.setText("Enter Password");
        jpf_Password.setEchoChar((char) 0);
        jpf_ConfirmPassword.setText("Confirm Password");
        jpf_ConfirmPassword.setEchoChar((char) 0);
    }
}