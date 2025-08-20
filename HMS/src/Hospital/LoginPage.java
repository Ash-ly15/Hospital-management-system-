package Hospital;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class LoginPage extends JFrame implements ActionListener {

    JTextField usernameField;
    JPasswordField passwordField;
    JButton loginButton, cancelButton;
    String role;  
    
    public LoginPage(String role) {
        this.role = role;

        setTitle(role + " Login");
        setSize(400, 300);
        setLocation(500, 300);
        setLayout(null);

        JLabel l1 = new JLabel("Username:");
        l1.setBounds(50, 70, 100, 30);
        add(l1);

        JLabel l2 = new JLabel("Password:");
        l2.setBounds(50, 120, 100, 30);
        add(l2);

        usernameField = new JTextField();
        usernameField.setBounds(150, 70, 150, 30);
        add(usernameField);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 120, 150, 30);
        add(passwordField);

        loginButton = new JButton("Login");
        loginButton.setBounds(70, 180, 100, 30);
        loginButton.addActionListener(this);
        add(loginButton);

        cancelButton = new JButton("Cancel");
        cancelButton.setBounds(200, 180, 100, 30);
        cancelButton.addActionListener(this);
        add(cancelButton);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == loginButton) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            try {
                ConnectionClass conClass = new ConnectionClass();
                String query = "SELECT * FROM users WHERE username = ? AND password = ? AND role = ?";
                PreparedStatement pst = conClass.con.prepareStatement(query);
                pst.setString(1, username);
                pst.setString(2, password);
                pst.setString(3, role.toLowerCase()); 
                
                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    JOptionPane.showMessageDialog(null, "Login successful!");
                    int userId = rs.getInt("id"); 
                    
                    // Redirect based on role
                    switch (role.toLowerCase()) {
                        case "doctor":
                            new DoctorDashboard(userId);  // Pass Doctor ID to dashboard
                            break;
                        case "patient":
                            new PatientDashboard(userId);  // Pass Patient ID to dashboard
                            break;
                        case "receptionist":
                            new ReceptionDashboard();  // Receptionist Dashboard
                            break;
                        case "admin":
                            new AdminDashboard();  // Admin Dashboard
                            break;
                    }
                    this.setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid username or password.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Database connection error.");
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "An unexpected error occurred.");
            }
        } else if (ae.getSource() == cancelButton) {
            this.setVisible(false);
            new index();  // Redirect to index page
        }
    }
}
