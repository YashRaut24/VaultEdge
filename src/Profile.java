import javax.swing.*;
import java.awt.*;
import java.sql.*;

class Profile extends JFrame {

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
        JPanel backgroundPanel = new JPanel(null);
        backgroundPanel.setBackground(new Color(8, 20, 30));
        setContentPane(backgroundPanel);

        JLabel title = new JLabel("Profile Settings", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(0, 230, 255));
        title.setBounds(0, 20, 800, 40);
        backgroundPanel.add(title);

        createLabel("Full Name:", 150, 100, 150, 30, backgroundPanel);
        JTextField nameField = createTextField(300, 100, 300, 30, backgroundPanel);

        createLabel("Email:", 150, 150, 150, 30, backgroundPanel);
        JTextField emailField = createTextField(300, 150, 300, 30, backgroundPanel);

        createLabel("Phone:", 150, 200, 150, 30, backgroundPanel);
        JTextField phoneField = createTextField(300, 200, 300, 30, backgroundPanel);

        createLabel("Account No:", 150, 250, 150, 30, backgroundPanel);
        JTextField accountField = createTextField(300, 250, 300, 30, backgroundPanel);

        createLabel("Balance:", 150, 300, 150, 30, backgroundPanel);
        JTextField balanceField = createTextField(300, 300, 300, 30, backgroundPanel);

        createLabel("Last Login:", 150, 350, 150, 30, backgroundPanel);
        JTextField lastLoginField = createTextField(300, 350, 300, 30, backgroundPanel);

        JButton updateBtn = createButton("Update Info", 180, 410, 150, 42, backgroundPanel);
        JButton changePassBtn = createButton("Change Password", 350, 410, 200, 42, backgroundPanel);
        JButton deleteBtn = createButton("Delete Account", 180, 470, 150, 42, backgroundPanel);
        JButton backBtn = createButton("Back", 350, 470, 200, 42, backgroundPanel);

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/3dec", "root", "your_password")) {
            String sql = "SELECT fullname, email, phone, account_number, balance, last_login FROM users WHERE username=?";
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setString(1, username);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    nameField.setText(rs.getString("fullname"));
                    emailField.setText(rs.getString("email"));
                    phoneField.setText(rs.getString("phone"));
                    accountField.setText(rs.getString("account_number"));
                    balanceField.setText("₹" + rs.getDouble("balance"));
                    lastLoginField.setText(rs.getString("last_login"));
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

        updateBtn.addActionListener(a -> {
            nameField.setEditable(true);
            emailField.setEditable(true);
            phoneField.setEditable(true);
            int result = JOptionPane.showConfirmDialog(null, "Save changes?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/3dec", "root", "your_password")) {
                    String sql = "UPDATE users SET fullname=?, email=?, phone=? WHERE username=?";
                    try (PreparedStatement pst = con.prepareStatement(sql)) {
                        pst.setString(1, nameField.getText());
                        pst.setString(2, emailField.getText());
                        pst.setString(3, phoneField.getText());
                        pst.setString(4, username);
                        pst.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Profile updated successfully!");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e.getMessage());
                }
            }
            nameField.setEditable(false);
            emailField.setEditable(false);
            phoneField.setEditable(false);
        });

        changePassBtn.addActionListener(a -> {
            String newPass = JOptionPane.showInputDialog("Enter new password:");
            if (newPass != null && !newPass.isEmpty()) {
                try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/3dec", "root", "your_password")) {
                    String sql = "UPDATE users SET password=? WHERE username=?";
                    try (PreparedStatement pst = con.prepareStatement(sql)) {
                        pst.setString(1, newPass);
                        pst.setString(2, username);
                        pst.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Password changed successfully!");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e.getMessage());
                }
            }
        });

        deleteBtn.addActionListener(a -> {
            int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete your account?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/3dec", "root", "your_password")) {
                    String sql = "DELETE FROM users WHERE username=?";
                    try (PreparedStatement pst = con.prepareStatement(sql)) {
                        pst.setString(1, username);
                        pst.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Account deleted successfully!");
                        new LandingPage();
                        dispose();
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e.getMessage());
                }
            }
        });

        backBtn.addActionListener(a -> {
            new HomePage(username);
            dispose();
        });

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
