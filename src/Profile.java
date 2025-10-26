import javax.swing.*;
import java.awt.*;
import java.sql.*;

class Profile extends JFrame {

    // Database credentials
    String url = EnvLoader.get("DB_URL");
    String user = EnvLoader.get("DB_USER");
    String password = EnvLoader.get("DB_PASSWORD");

    // Updates activities
    private void logActivity(String action, String targetUser, String adminUsername, String remarks) {
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String sql = "INSERT INTO activities (action, target_user, admin, remarks) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, action);
                pst.setString(2, targetUser);
                pst.setString(3, adminUsername);
                pst.setString(4, remarks);
                pst.executeUpdate();
            }
        } catch (SQLException ex) {
            System.err.println("Error logging activity: " + ex.getMessage());
        }
    }

    private JLabel createLabel(String text, int x, int y, int width, int height, JPanel panel) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setForeground(new Color(200, 240, 255));
        label.setBounds(x, y, width, height);
        panel.add(label);
        return label;
    }

    private JTextField createTextField(int x, int y, int width, int height, JPanel panel) {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setBackground(new Color(15, 30, 40));
        field.setForeground(new Color(220, 235, 245));
        field.setCaretColor(new Color(0, 230, 255));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 230, 255), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.setBounds(x, y, width, height);
        field.setEditable(false);
        panel.add(field);
        return field;
    }

    private JButton createButton(String text, int x, int y, int width, int height, JPanel panel) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(0, 230, 255), 2));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBounds(x, y, width, height);
        panel.add(button);
        return button;
    }

    Profile(String username) {

        // Profile panel
        JPanel profilePanel = new JPanel(null);
        profilePanel.setBackground(new Color(8, 20, 30));
        setContentPane(profilePanel);

        // Title label
        JLabel titleLabel = new JLabel("Profile Settings", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 230, 255));
        titleLabel.setBounds(0, 20, 800, 40);
        profilePanel.add(titleLabel);

        // Full name label
        createLabel("Full Name:", 150, 100, 150, 30, profilePanel);

        // Full name TextField
        JTextField nameTextField = createTextField(300, 100, 300, 30, profilePanel);

        // Email label
        createLabel("Email:", 150, 150, 150, 30, profilePanel);

        // Email TextField
        JTextField emailTextField = createTextField(300, 150, 300, 30, profilePanel);

        // Phone label
        createLabel("Phone:", 150, 200, 150, 30, profilePanel);

        // Phone TextField
        JTextField phoneTextField = createTextField(300, 200, 300, 30, profilePanel);

        // Account number label
        createLabel("Account No:", 150, 250, 150, 30, profilePanel);

        // Account number TextField
        JTextField accountTextField = createTextField(300, 250, 300, 30, profilePanel);

        // Balance label
        createLabel("Balance:", 150, 300, 150, 30, profilePanel);

        // Balance TextField
        JTextField balanceTextField = createTextField(300, 300, 300, 30, profilePanel);

        // Last login label
        createLabel("Last Login:", 150, 350, 150, 30, profilePanel);

        // Last login TextField
        JTextField lastLoginField = createTextField(300, 350, 300, 30, profilePanel);

        logActivity("View Profile", username, "System", "User accessed profile settings page");

        // Update button - WITH LOGGING
        JButton updateButton = createButton("Update Info", 180, 410, 150, 42, profilePanel);

        updateButton.addActionListener(a -> {
            nameTextField.setEditable(true);
            emailTextField.setEditable(true);
            phoneTextField.setEditable(true);

            int result = JOptionPane.showConfirmDialog(null, "Save changes?", "Confirm", JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION) {
                try (Connection con = DriverManager.getConnection(url, user, password)) {
                    String sql = "UPDATE users SET fullname=?, email=?, phone=? WHERE username=?";
                    try (PreparedStatement pst = con.prepareStatement(sql)) {
                        pst.setString(1, nameTextField.getText());
                        pst.setString(2, emailTextField.getText());
                        pst.setString(3, phoneTextField.getText());
                        pst.setString(4, username);
                        pst.executeUpdate();

                        String updateDetails = "Updated profile info - Name: " + nameTextField.getText() +
                                ", Email: " + emailTextField.getText() +
                                ", Phone: " + phoneTextField.getText();
                        logActivity("Update Profile", username, "System", updateDetails);

                        JOptionPane.showMessageDialog(null, "Profile updated successfully!");
                    }
                } catch (Exception e) {
                    logActivity("Update Profile Failed", username, "System",
                            "Error updating profile: " + e.getMessage());
                    JOptionPane.showMessageDialog(null, e.getMessage());
                }
            } else {
                logActivity("Update Profile Cancelled", username, "System",
                        "User cancelled profile update");
            }

            nameTextField.setEditable(false);
            emailTextField.setEditable(false);
            phoneTextField.setEditable(false);
        });

        JButton changePasswordButton = createButton("Change Password", 350, 410, 200, 42, profilePanel);

        changePasswordButton.addActionListener(a -> {
            String newPass = JOptionPane.showInputDialog("Enter new password:");

            if (newPass != null && !newPass.isEmpty()) {
                try (Connection con = DriverManager.getConnection(url, user, password)) {
                    String sql = "UPDATE users SET password=? WHERE username=?";
                    try (PreparedStatement pst = con.prepareStatement(sql)) {
                        pst.setString(1, newPass);
                        pst.setString(2, username);
                        pst.executeUpdate();

                        logActivity("Change Password", username, "System",
                                "User successfully changed password");

                        JOptionPane.showMessageDialog(null, "Password changed successfully!");
                    }
                } catch (Exception e) {
                    logActivity("Change Password Failed", username, "System",
                            "Error changing password: " + e.getMessage());
                    JOptionPane.showMessageDialog(null, e.getMessage());
                }
            } else {
                logActivity("Change Password Cancelled", username, "System",
                        "User cancelled password change");
            }
        });

        // Delete account button - WITH LOGGING
        JButton deleteButton = createButton("Delete Account", 180, 470, 150, 42, profilePanel);

        deleteButton.addActionListener(a -> {
            int result = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to delete your account?",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION) {
                try (Connection con = DriverManager.getConnection(url, user, password)) {
                    String sql = "DELETE FROM users WHERE username=?";
                    try (PreparedStatement pst = con.prepareStatement(sql)) {
                        pst.setString(1, username);
                        pst.executeUpdate();

                        logActivity("Delete Account", username, "System",
                                "User account permanently deleted");

                        JOptionPane.showMessageDialog(null, "Account deleted successfully!");
                        new LandingPage();
                        dispose();
                    }
                } catch (Exception e) {
                    logActivity("Delete Account Failed", username, "System",
                            "Error deleting account: " + e.getMessage());
                    JOptionPane.showMessageDialog(null, e.getMessage());
                }
            } else {
                logActivity("Delete Account Cancelled", username, "System",
                        "User cancelled account deletion");
            }
        });

        // Back button - WITH LOGGING
        JButton backButton = createButton("Back", 350, 470, 200, 42, profilePanel);

        backButton.addActionListener(a -> {
            logActivity("Exit Profile", username, "System", "User left profile settings page");

            new HomePage(username);
            dispose();
        });

        try (Connection con = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT fullname, email, phone, account_number, balance, last_login FROM users WHERE username=?";
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setString(1, username);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    nameTextField.setText(rs.getString("fullname"));
                    emailTextField.setText(rs.getString("email"));
                    phoneTextField.setText(rs.getString("phone"));
                    accountTextField.setText(rs.getString("account_number"));
                    balanceTextField.setText("₹" + rs.getDouble("balance"));
                    lastLoginField.setText(rs.getString("last_login"));
                }
            }
        } catch (Exception e) {
            logActivity("Load Profile Failed", username, "System",
                    "Error loading profile data: " + e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

        // Frame settings
        setTitle("VaultEdge - Profile Settings");
        setSize(800, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Profile("User");
    }
}