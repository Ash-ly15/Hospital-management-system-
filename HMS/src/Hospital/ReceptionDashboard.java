package Hospital;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ReceptionDashboard extends JFrame {
    private ConnectionClass connectionClass;

    public ReceptionDashboard() {
        connectionClass = new ConnectionClass(); // Ensure this class establishes a valid database connection

        setTitle("Reception Dashboard");
        setSize(800, 600);
        setLocation(300, 100);
        setLayout(null);

        JButton addPatientButton = new JButton("Add Patient");
        addPatientButton.setBounds(50, 100, 200, 40);
        add(addPatientButton);

        JButton viewPatientsButton = new JButton("View Patients");
        viewPatientsButton.setBounds(50, 160, 200, 40);
        add(viewPatientsButton);

        JButton addAppointmentButton = new JButton("Add Appointment");
        addAppointmentButton.setBounds(50, 220, 200, 40);
        add(addAppointmentButton);

        JButton viewAppointmentsButton = new JButton("View Appointments");
        viewAppointmentsButton.setBounds(50, 280, 200, 40);
        add(viewAppointmentsButton);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBounds(50, 340, 200, 40);
        add(logoutButton);

        addPatientButton.addActionListener(e -> addPatient());
        addAppointmentButton.addActionListener(e -> addAppointment());
        viewPatientsButton.addActionListener(e -> viewPatients());
        viewAppointmentsButton.addActionListener(e -> viewAppointments());
        logoutButton.addActionListener(e -> logout());
        setVisible(true);
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose(); // Close the current window
            new index();// Open the login screen (create this class)
        }
    }

    private void addPatient() {
        JFrame frame = new JFrame("Add Patient");
        frame.setSize(400, 400);
        frame.setLocation(500, 300);
        frame.setLayout(new GridLayout(8, 2));

        JLabel nameLabel = new JLabel("Name:");
        JLabel ageLabel = new JLabel("Age:");
        JLabel genderLabel = new JLabel("Gender:");
        JLabel addressLabel = new JLabel("Address:");
        JLabel phoneLabel = new JLabel("Phone:");
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");

        JTextField nameField = new JTextField();
        JTextField ageField = new JTextField();
        JTextField genderField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        JButton submitButton = new JButton("Submit");
        JButton cancelButton = new JButton("Cancel");

        frame.add(nameLabel);
        frame.add(nameField);
        frame.add(ageLabel);
        frame.add(ageField);
        frame.add(genderLabel);
        frame.add(genderField);
        frame.add(addressLabel);
        frame.add(addressField);
        frame.add(phoneLabel);
        frame.add(phoneField);
        frame.add(usernameLabel);
        frame.add(usernameField);
        frame.add(passwordLabel);
        frame.add(passwordField);
        frame.add(submitButton);
        frame.add(cancelButton);

        submitButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                int age = Integer.parseInt(ageField.getText());
                String gender = genderField.getText();
                String address = addressField.getText();
                String phone = phoneField.getText();
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                addPatientToDatabase(name, age, gender, address, phone, username, password);
                frame.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid Age", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> frame.dispose());

        frame.setVisible(true);
    }

    private void addPatientToDatabase(String name, int age, String gender, String address, String phone, String username, String password) {
    try {
        connectionClass.con.setAutoCommit(false);
        
        // Insert into users table
        String userQuery = "INSERT INTO users (username, password, role, name) VALUES (?, ?, 'patient', ?)";
        PreparedStatement userPst = connectionClass.con.prepareStatement(userQuery, Statement.RETURN_GENERATED_KEYS);
        userPst.setString(1, username);
        userPst.setString(2, password);
        userPst.setString(3, name);
        userPst.executeUpdate();
        
        // Get the generated user_id
        ResultSet rs = userPst.getGeneratedKeys();
        int userId = -1;
        if (rs.next()) {
            userId = rs.getInt(1);
        }
        
        // Insert into patients table with the new user_id
        String patientQuery = "INSERT INTO patients (user_id, name, age, gender, address, phone) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement patientPst = connectionClass.con.prepareStatement(patientQuery);
        patientPst.setInt(1, userId);  // Use the new user_id
        patientPst.setString(2, name);
        patientPst.setInt(3, age);
        patientPst.setString(4, gender);
        patientPst.setString(5, address);
        patientPst.setString(6, phone);
        patientPst.executeUpdate();

        // Commit the transaction
        connectionClass.con.commit();
        JOptionPane.showMessageDialog(null, "Patient and login added successfully!");
    } catch (SQLException e) {
        try {
            connectionClass.con.rollback(); // Rollback if there's an error
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        JOptionPane.showMessageDialog(null, "Error adding patient.", "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    } finally {
        try {
            connectionClass.con.setAutoCommit(true); // Reset auto-commit mode
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}


    private void addAppointment() {
        JFrame frame = new JFrame("Add Appointment");
        frame.setSize(400, 400);
        frame.setLocation(500, 300);
        frame.setLayout(new GridLayout(6, 2));

        JLabel patientIdLabel = new JLabel("Patient ID:");
        JLabel doctorIdLabel = new JLabel("Doctor ID:");
        JLabel appointmentDateLabel = new JLabel("Date (YYYY-MM-DD):");
        JLabel appointmentTimeLabel = new JLabel("Time (HH:MM:SS):");
        JLabel statusLabel = new JLabel("Status:");

        JTextField patientIdField = new JTextField();
        JTextField doctorIdField = new JTextField();
        JTextField appointmentDateField = new JTextField();
        JTextField appointmentTimeField = new JTextField();
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"Scheduled", "Completed"});

        JButton submitButton = new JButton("Submit");
        JButton cancelButton = new JButton("Cancel");

        frame.add(patientIdLabel);
        frame.add(patientIdField);
        frame.add(doctorIdLabel);
        frame.add(doctorIdField);
        frame.add(appointmentDateLabel);
        frame.add(appointmentDateField);
        frame.add(appointmentTimeLabel);
        frame.add(appointmentTimeField);
        frame.add(statusLabel);
        frame.add(statusComboBox);
        frame.add(submitButton);
        frame.add(cancelButton);

        submitButton.addActionListener(e -> {
            try {
                int patientId = Integer.parseInt(patientIdField.getText());
                int doctorId = Integer.parseInt(doctorIdField.getText());
                String appointmentDate = appointmentDateField.getText();
                String appointmentTime = appointmentTimeField.getText();
                String status = (String) statusComboBox.getSelectedItem();

                addAppointmentToDatabase(patientId, doctorId, appointmentDate, appointmentTime, status);
                frame.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid ID or time format.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> frame.dispose());

        frame.setVisible(true);
    }

    private void addAppointmentToDatabase(int patientId, int doctorId, String appointmentDate, String appointmentTime, String status) {
        try {
            String query = "INSERT INTO appointments (patient_id, doctor_id, appointment_date, appointment_time, status) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pst = connectionClass.con.prepareStatement(query);
            pst.setInt(1, patientId);
            pst.setInt(2, doctorId);
            pst.setString(3, appointmentDate);
            pst.setString(4, appointmentTime);
            pst.setString(5, status);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Appointment added successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error adding appointment.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void viewPatients() {
        JFrame frame = new JFrame("View Patients");
        frame.setSize(600, 400);
        frame.setLocation(400, 200);
        frame.setLayout(new BorderLayout());

        String[] columns = {"Patient ID", "Name", "Age", "Gender", "Address", "Phone"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        try {
            String query = "SELECT * FROM patients";
            PreparedStatement pst = connectionClass.con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("patient_id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("gender"),
                        rs.getString("address"),
                        rs.getString("phone")
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error fetching patient data.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private void viewAppointments() {
        JFrame frame = new JFrame("View Appointments");
        frame.setSize(600, 400);
        frame.setLocation(400, 200);
        frame.setLayout(new BorderLayout());

        String[] columns = {"Appointment ID", "Patient ID", "Doctor ID", "Date", "Time", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        try {
            String query = "SELECT * FROM appointments";
            PreparedStatement pst = connectionClass.con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("appointment_id"),
                        rs.getInt("patient_id"),
                        rs.getInt("doctor_id"),
                        rs.getString("appointment_date"),
                        rs.getString("appointment_time"),
                        rs.getString("status")
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error fetching appointment data.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new ReceptionDashboard();
    }
}