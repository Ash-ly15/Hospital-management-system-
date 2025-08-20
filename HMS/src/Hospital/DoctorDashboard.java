package Hospital;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class DoctorDashboard extends JFrame {
    private int doctorId;
    private ConnectionClass connectionClass;

    // Components
    private JButton viewAppointmentsButton;
    private JButton givePrescriptionButton;
    private JButton modifyStatusButton;
    private JButton logoutButton;

    public DoctorDashboard(int doctorId) {
        this.doctorId = doctorId;
        connectionClass = new ConnectionClass(); 

        setTitle("Doctor Dashboard");
        setSize(800, 600);
        setLocation(300, 100);
        setLayout(null);

        JLabel l1 = new JLabel("Doctor Dashboard");
        l1.setFont(new Font("Serif", Font.BOLD, 24));
        l1.setBounds(250, 30, 300, 40);
        add(l1);

        viewAppointmentsButton = new JButton("View Appointments");
        viewAppointmentsButton.setBounds(50, 100, 200, 40);
        add(viewAppointmentsButton);

        givePrescriptionButton = new JButton("Give Prescription");
        givePrescriptionButton.setBounds(50, 160, 200, 40);
        add(givePrescriptionButton);

        modifyStatusButton = new JButton("Modify Appointment Status");
        modifyStatusButton.setBounds(50, 220, 200, 40);
        add(modifyStatusButton);

        logoutButton = new JButton("Logout");
        logoutButton.setBounds(50, 280, 200, 40);
        add(logoutButton);

        viewAppointmentsButton.addActionListener(e -> viewAppointments());
        givePrescriptionButton.addActionListener(e -> givePrescription());
        modifyStatusButton.addActionListener(e -> modifyStatus());
        logoutButton.addActionListener(e -> {
            setVisible(false);
            new index(); // Redirect to Index page
        });

        setVisible(true);
    }

    private void viewAppointments() {
        JFrame frame = new JFrame("View Appointments");
        frame.setSize(600, 400);
        frame.setLocation(400, 200);
        frame.setLayout(new BorderLayout());

        String[] columns = {"Appointment ID", "Patient ID", "Date", "Time", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        try {
            String query = "SELECT * FROM appointments WHERE doctor_id = ?";
            PreparedStatement pst = connectionClass.con.prepareStatement(query);
            pst.setInt(1, doctorId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("appointment_id"),
                    rs.getInt("patient_id"),
                    rs.getDate("appointment_date"),
                    rs.getTime("appointment_time"),
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

    private void givePrescription() {
        JFrame frame = new JFrame("Give Prescription");
        frame.setSize(400, 300);
        frame.setLocation(500, 300);
        frame.setLayout(new GridLayout(3, 2));

        JLabel appointmentIdLabel = new JLabel("Appointment ID:");
        JLabel prescriptionLabel = new JLabel("Prescription:");

        JTextField appointmentIdField = new JTextField();
        JTextArea prescriptionArea = new JTextArea();

        JButton submitButton = new JButton("Submit");
        JButton cancelButton = new JButton("Cancel");

        frame.add(appointmentIdLabel);
        frame.add(appointmentIdField);
        frame.add(prescriptionLabel);
        frame.add(prescriptionArea);
        frame.add(submitButton);
        frame.add(cancelButton);

        submitButton.addActionListener(e -> {
            try {
                int appointmentId = Integer.parseInt(appointmentIdField.getText());
                String prescription = prescriptionArea.getText();
                if (givePrescription(appointmentId, prescription)) {
                    frame.dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid Appointment ID", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> frame.dispose());

        frame.setVisible(true);
    }

    private boolean givePrescription(int appointmentId, String prescription) {
        try {
            // Fetch the patient_id based on the appointment_id
            String fetchPatientQuery = "SELECT patient_id FROM appointments WHERE appointment_id = ?";
            PreparedStatement fetchPatientPst = connectionClass.con.prepareStatement(fetchPatientQuery);
            fetchPatientPst.setInt(1, appointmentId);
            ResultSet rs = fetchPatientPst.executeQuery();

            // Check if the appointment exists and retrieve the patient_id
            if (rs.next()) {
                int patientId = rs.getInt("patient_id");

                // Now insert into the prescriptions table
                String query = "INSERT INTO prescriptions (appointment_id, patient_id, doctor_id, prescription_date, prescription_text) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pst = connectionClass.con.prepareStatement(query);
                pst.setInt(1, appointmentId);
                pst.setInt(2, patientId);  // Insert the retrieved patient_id
                pst.setInt(3, doctorId);   // Use the doctorId
                pst.setDate(4, new java.sql.Date(System.currentTimeMillis())); // Current date
                pst.setString(5, prescription);
                pst.executeUpdate();

                JOptionPane.showMessageDialog(null, "Prescription given successfully!");
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Appointment not found!", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error giving prescription: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    private void modifyStatus() {
        JFrame frame = new JFrame("Modify Appointment Status");
        frame.setSize(400, 300);
        frame.setLocation(500, 300);
        frame.setLayout(new GridLayout(3, 2));

        JLabel appointmentIdLabel = new JLabel("Appointment ID:");
        JLabel statusLabel = new JLabel("Status:");

        JTextField appointmentIdField = new JTextField();
        JTextField statusField = new JTextField();

        JButton submitButton = new JButton("Submit");
        JButton cancelButton = new JButton("Cancel");

        frame.add(appointmentIdLabel);
        frame.add(appointmentIdField);
        frame.add(statusLabel);
        frame.add(statusField);
        frame.add(submitButton);
        frame.add(cancelButton);

        submitButton.addActionListener(e -> {
            try {
                int appointmentId = Integer.parseInt(appointmentIdField.getText());
                String status = statusField.getText();
                modifyStatus(appointmentId, status);
                frame.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid Appointment ID", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> frame.dispose());

        frame.setVisible(true);
    }

    private void modifyStatus(int appointmentId, String status) {
        String query = "UPDATE appointments SET status = ? WHERE appointment_id = ? AND doctor_id = ?";
        try {
            PreparedStatement pst = connectionClass.con.prepareStatement(query);
            pst.setString(1, status);
            pst.setInt(2, appointmentId);
            pst.setInt(3, doctorId);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Appointment status updated successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error updating appointment status.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
