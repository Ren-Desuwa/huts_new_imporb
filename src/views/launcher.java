package views;

import java.awt.EventQueue;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class launcher {
	public static void main(String[] args) {
        // Set Nimbus look and feel if available
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        
        // Create and display the application window
        java.awt.EventQueue.invokeLater(() -> {
            Main_Frame mainFrame = new Main_Frame();
            mainFrame.setVisible(true);
            // Start with login panel
            mainFrame.showLoginPanel();
        });
    }
}
