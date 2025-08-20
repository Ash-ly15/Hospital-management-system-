package Hospital;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class PatientDashboard extends JFrame {
    private int patientId;
    private int userId;  // Assuming you get this from the login
    private ConnectionClass connectionClass;

    // Components
    private JButton viewAppointmentsButton;
    private JButton viewPrescriptionsButton;
    private JButton logoutButton;

    public PatientDashboard(int userId) {
        this.userId = userId;
        connectionClass = new ConnectionClass();  // Ensure valid DB connection

        // Fetch patientId from userId
        fetchPatientId();  // Get the correct patient_id

        setTitle("Patient Dashboard");
        setSize(800, 600);
        setLocation(300, 100);
        setLayout(null);

        JLabel l1 = new JLabel("Patient Dashboard");
        l1.setFont(new Font("Serif", Font.BOLD, 24));
        l1.setBounds(250, 30, 300, 40);
        add(l1);

        viewAppointmentsButton = new JButton("View Appointments");
        viewAppointmentsButton.setBounds(50, 100, 200, 40);
        add(viewAppointmentsButton);

        viewPrescriptionsButton = new JButton("View Prescriptions");
        viewPrescriptionsButton.setBounds(50, 160, 200, 40);
        add(viewPrescriptionsButton);

        logoutButton = new JButton("Logout");
        logoutButton.setBounds(50, 220, 200, 40);
        add(logoutButton);

        // Action Listeners
        viewAppointmentsButton.addActionListener(e -> viewAppointments());
        viewPrescriptionsButton.addActionListener(e -> viewPrescriptions());
        logoutButton.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                dispose(); // Close current window
                new index();  // Redirect to Index page
            }
        });

        setVisible(true);
    }

    // Method to fetch patientId from userId
    private void fetchPatientId() {
    try {
        String query = "SELECT patient_id FROM patients WHERE user_id = ?";
        PreparedStatement pst = connectionClass.con.prepareStatement(query);
        pst.setInt(1, userId);  // Ensure userId is passed correctly from the login
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            patientId = rs.getInt("patient_id");  // Assign patientId
            System.out.println("Fetched patient ID: " + patientId);
        } else {
            System.out.println("No patient found for userId: " + userId); // Debugging line
            JOptionPane.showMessageDialog(this, "Patient record not found!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error fetching patient ID.", "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}



    private void viewAppointments() {
        JFrame frame = new JFrame("View Appointments");
        frame.setSize(600, 400);
        frame.setLocation(400, 200);
        frame.setLayout(new BorderLayout());

        String[] columns = {"Appointment ID", "Doctor ID", "Date", "Time", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        try {
            String query = "SELECT appointment_id, doctor_id, appointment_date, appointment_time, status " +
                           "FROM appointments WHERE patient_id = ?";
            PreparedStatement pst = connectionClass.con.prepareStatement(query);
            pst.setInt(1, patientId);  // Use correct patientId
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("appointment_id"),
                    rs.getInt("doctor_id"),
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
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close the frame properly
        frame.setVisible(true);
    }

    private void viewPrescriptions() {
        JFrame frame = new JFrame("View Prescriptions");
        frame.setSize(600, 400);
        frame.setLocation(400, 200);
        frame.setLayout(new BorderLayout());

        String[] columns = {"Prescription ID", "Appointment ID", "Doctor ID", "Prescription Date", "Prescription Given"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        try {
            String query = "SELECT prescription_id, appointment_id, doctor_id, prescription_date, prescription_text " +
                           "FROM prescriptions WHERE appointment_id IN " +
                           "(SELECT appointment_id FROM appointments WHERE patient_id = ?)";
            PreparedStatement pst = connectionClass.con.prepareStatement(query);
            pst.setInt(1, patientId);  // Use correct patientId
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("prescription_id"),
                    rs.getInt("appointment_id"),
                    rs.getInt("doctor_id"),
                    rs.getDate("prescription_date"),
                    rs.getString("prescription_text")
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error fetching prescription data.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close the frame properly
        frame.setVisible(true);
    }
}
