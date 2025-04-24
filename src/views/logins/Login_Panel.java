package views.logins;

import models.*;
import views.Main_Frame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import database.User_Manager;

import java.awt.*;
import java.awt.event.*;

public class Login_Panel extends JPanel {
    private final User_Manager userManager;
    private final Main_Frame mainFrame;

    // UI Components
    private JLabel jlbl_Login;
    private JButton jbtn_Login;
    private JTextField jtf_Username;
    private JPasswordField jpf_Password;
    private JLabel jlbl_Password;
    private JLabel jlbl_Username;
    private JLabel jlbl_Forgot_Password;
    private JLabel jlbl_Sign_Up;
    private JLabel jlbl_Account_Icon;
    private JPanel contentPanel;

    /**
     * Creates new Login Panel
     */
    public Login_Panel(Main_Frame mainFrame, User_Manager userManager) {
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
        jlbl_Login = new JLabel("Log In");
        jlbl_Login.setFont(new Font("Segoe UI", 0, 24));
        jlbl_Login.setForeground(new Color(23, 22, 22));
        jlbl_Login.setHorizontalAlignment(SwingConstants.CENTER);
        
        jlbl_Account_Icon = new JLabel();
        jlbl_Account_Icon.setIcon(new ImageIcon(getClass().getResource("/assets/icon/AccountBlack.png")));
        
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
        
        jbtn_Login = new JButton("Log In");
        jbtn_Login.setBackground(new Color(226, 149, 90));
        jbtn_Login.setFont(new Font("Segoe UI", 0, 18));
        jbtn_Login.setForeground(new Color(255, 255, 255));
        jbtn_Login.setCursor(new Cursor(Cursor.HAND_CURSOR));
        jbtn_Login.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jbtn_LoginActionPerformed(evt);
            }
        });
        
        jlbl_Forgot_Password = new JLabel("Forgot Password?");
        jlbl_Forgot_Password.setForeground(new Color(23, 22, 22));
        jlbl_Forgot_Password.setCursor(new Cursor(Cursor.HAND_CURSOR));
        jlbl_Forgot_Password.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                jlbl_Forgot_PasswordMouseClicked(evt);
            }
            public void mouseEntered(MouseEvent evt) {
                jlbl_Forgot_Password.setText("<html><u>Forgot Password?</u></html>");
            }
            public void mouseExited(MouseEvent evt) {
                jlbl_Forgot_Password.setText("Forgot Password?");
            }
        });
        
        jlbl_Sign_Up = new JLabel("Sign Up");
        jlbl_Sign_Up.setForeground(new Color(23, 22, 22));
        jlbl_Sign_Up.setCursor(new Cursor(Cursor.HAND_CURSOR));
        jlbl_Sign_Up.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                jlbl_Sign_UpMouseClicked(evt);
            }
            public void mouseEntered(MouseEvent evt) {
                jlbl_Sign_Up.setText("<html><u>Sign Up</u></html>");
            }
            public void mouseExited(MouseEvent evt) {
                jlbl_Sign_Up.setText("Sign Up");
            }
        });
        
        // Header panel with icon and title
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setOpaque(false);
        headerPanel.add(jlbl_Account_Icon);
        headerPanel.add(jlbl_Login);
        
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
        
        // Password section
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        formPanel.add(jlbl_Password, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        formPanel.add(jpf_Password, gbc);
        
        // Forgot password link
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(jlbl_Forgot_Password, gbc);
        
        // Login button
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(15, 5, 15, 5);
        formPanel.add(jbtn_Login, gbc);
        
        // Sign up link
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.insets = new Insets(5, 5, 5, 5);
        formPanel.add(jlbl_Sign_Up, gbc);
        
        // Add panels to content panel
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(formPanel, BorderLayout.CENTER);
        
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

    private void jbtn_LoginActionPerformed(ActionEvent evt) {
        String username = jtf_Username.getText().trim();
        String password = new String(jpf_Password.getPassword()).trim();

        try {
            // Debug print to see what values are being used
            System.out.println("Attempting login with: " + username + " / " + password);
            
            // Check if user exists first
            if (!userManager.userExists(username)) {
                JOptionPane.showMessageDialog(this, "Username does not exist.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Get the user object to check password
            models.User user = userManager.getUser(username);
            System.out.println("User found: " + user);
            System.out.println("Stored password: " + user.getPassword());
            
            boolean isAuthenticated = userManager.authenticateUser(username, password);
            System.out.println("Authentication result: " + isAuthenticated);

            if (isAuthenticated) {
                JOptionPane.showMessageDialog(this, "Login successful!");
                
                // Set the current user in Main_Frame
                mainFrame.setCurrentUser(user);
                
                // Show the main application window
                mainFrame.showMainContent();
            } else {
                // Invalid credentials
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while trying to log in.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void jlbl_Sign_UpMouseClicked(MouseEvent evt) {
        // Show sign up panel
        mainFrame.showSignUpPanel();
    }

    private void jlbl_Forgot_PasswordMouseClicked(MouseEvent evt) {
        // Show forgot password panel
        mainFrame.showForgotPasswordPanel();
    }
    
    public JPanel getPanel() {
        return this;
    }
    
    public void refreshPanel() {
        // Reset fields if needed
        jtf_Username.setText("Enter Username");
        jpf_Password.setText("Enter Password");
        jpf_Password.setEchoChar((char) 0);
    }
}