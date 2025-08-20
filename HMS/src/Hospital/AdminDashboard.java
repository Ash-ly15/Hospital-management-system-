package Hospital;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AdminDashboard extends JFrame {
    private ConnectionClass connectionClass;

    // Components
    private JButton addDoctorButton;
    private JButton manageUsersButton;
    private JButton logoutButton;

    public AdminDashboard() {
        connectionClass = new ConnectionClass(); // Ensure this class establishes a valid database connection

        setTitle("Admin Dashboard");
        setSize(600, 400);
        setLocation(300, 100);
        setLayout(null);

        JLabel l1 = new JLabel("Admin Dashboard");
        l1.setFont(new Font("Serif", Font.BOLD, 24));
        l1.setBounds(200, 30, 300, 40);
        add(l1);

        addDoctorButton = new JButton("Add Doctor");
        addDoctorButton.setBounds(50, 100, 200, 40);
        add(addDoctorButton);

        manageUsersButton = new JButton("Manage Users");
        manageUsersButton.setBounds(50, 160, 200, 40);
        add(manageUsersButton);

        logoutButton = new JButton("Logout");
        logoutButton.setBounds(50, 220, 200, 40);
        add(logoutButton);

        // Action listeners for buttons
        addDoctorButton.addActionListener(e -> openAddDoctorFrame());
        manageUsersButton.addActionListener(e -> openManageUsersFrame());
        logoutButton.addActionListener(e -> logout());

        setVisible(true);
    }

    private void openAddDoctorFrame() {
        JFrame frame = new JFrame("Add Doctor");
        frame.setSize(400, 300);
        frame.setLocation(500, 300);
        frame.setLayout(new GridLayout(6, 2));

        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        JLabel nameLabel = new JLabel("Name:");
        JLabel specializationLabel = new JLabel("Specialization:");
        JLabel contactLabel = new JLabel("Contact:");

        JTextField usernameField = new JTextField();
        JTextField passwordField = new JPasswordField();
        JTextField nameField = new JTextField();
        JTextField specializationField = new JTextField();
        JTextField contactField = new JTextField();

        JButton submitButton = new JButton("Submit");
        JButton cancelButton = new JButton("Cancel");

        frame.add(usernameLabel);
        frame.add(usernameField);
        frame.add(passwordLabel);
        frame.add(passwordField);
        frame.add(nameLabel);
        frame.add(nameField);
        frame.add(specializationLabel);
        frame.add(specializationField);
        frame.add(contactLabel);
        frame.add(contactField);
        frame.add(submitButton);
        frame.add(cancelButton);

        // Action listener for the submit button
        submitButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(((JPasswordField) passwordField).getPassword());
            String name = nameField.getText();
            String specialization = specializationField.getText();
            String contact = contactField.getText();

            if (addDoctorToDatabase(username, password, name, specialization, contact)) {
                frame.dispose();
            }
        });

        cancelButton.addActionListener(e -> frame.dispose());

        frame.setVisible(true);
    }

    private boolean addDoctorToDatabase(String username, String password, String name, String specialization, String contact) {
        String query = "INSERT INTO users (username, password, role, name, specialization, contact) VALUES (?, ?, 'doctor', ?, ?, ?)";
        try (PreparedStatement pst = connectionClass.con.prepareStatement(query)) {
            pst.setString(1, username);
            pst.setString(2, password);
            pst.setString(3, name);
            pst.setString(4, specialization);
            pst.setString(5, contact);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Doctor added successfully!");
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error adding doctor: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    private void openManageUsersFrame() {
        JFrame frame = new JFrame("Manage Users");
        frame.setSize(800, 600);
        frame.setLocation(200, 100);
        frame.setLayout(new BorderLayout());

        String[] columns = {"User ID", "Username", "Role", "Action"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Only the "Action" column is editable (for delete button)
            }
        };
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        try {
            String query = "SELECT id, username, role FROM users";
            Statement stmt = connectionClass.con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("role"),
                    "Delete"
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error fetching user data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        table.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox(), this));

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public void deleteUser(int userId) {
        String query = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement pst = connectionClass.con.prepareStatement(query)) {
            pst.setInt(1, userId);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "User deleted successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error deleting user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void logout() {
        setVisible(false);
        new index(); // Assuming 'index' is the main login interface
    }

    public static void main(String[] args) {
        new AdminDashboard();
    }
}

// Custom ButtonRenderer for table cell
class ButtonRenderer extends JButton implements TableCellRenderer {
    public ButtonRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setText((value == null) ? "" : value.toString());
        return this;
    }
}

// Custom ButtonEditor for table cell
class ButtonEditor extends DefaultCellEditor {
    protected JButton button;
    private String label;
    private boolean isPushed;
    private AdminDashboard adminDashboard;
    private JTable table;

    public ButtonEditor(JCheckBox checkBox, AdminDashboard adminDashboard) {
        super(checkBox);
        this.adminDashboard = adminDashboard;
        button = new JButton();
        button.setOpaque(true);
        button.addActionListener(e -> fireEditingStopped());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.table = table;
        label = (value == null) ? "" : value.toString();
        button.setText(label);
        isPushed = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        if (isPushed) {
            int row = table.getSelectedRow();
            int userId = (int) table.getValueAt(row, 0);
            adminDashboard.deleteUser(userId); // Call deleteUser method
        }
        isPushed = false;
        return label;
    }

    @Override
    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }

    @Override
    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }
}
