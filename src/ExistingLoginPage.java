import javax.swing.*;
import java.awt.*;
import java.sql.*;

class ExistingLoginPage extends JFrame {

    // Database credentials
    String url = EnvLoader.get("DB_URL");
    String user = EnvLoader.get("DB_USER");
    String passwords = EnvLoader.get("DB_PASSWORD");

    // Updates activity
    private void logActivity(String action, String targetUser, String adminUsername, String remarks) {
        try (Connection conn = DriverManager.getConnection(url, user, passwords)) {
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

    // Create labels
    private JLabel createLabel(String text, int x, int y, int width, int height, JPanel panel) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(200, 240, 255));
        label.setBounds(x, y, width, height);
        panel.add(label);
        return label;
    }

    // Create TextFields
    private JTextField createTextField(int x, int y, int width, int height, JPanel panel) {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setBackground(new Color(15, 30, 40));
        field.setForeground(new Color(220, 235, 245));
        field.setCaretColor(new Color(0, 230, 255));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 230, 255), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        field.setBounds(x, y, width, height);
        panel.add(field);
        return field;
    }

    // Create PassFields
    private JPasswordField createPasswordField(int x, int y, int width, int height, JPanel panel) {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setBackground(new Color(15, 30, 40));
        field.setForeground(new Color(220, 235, 245));
        field.setCaretColor(new Color(0, 230, 255));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 230, 255), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        field.setBounds(x, y, width, height);
        panel.add(field);
        return field;
    }

    // Create buttons
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

    ExistingLoginPage() {

        // Existing login page panel
        JPanel existingLoginPagePanel = new JPanel(null);
        existingLoginPagePanel.setBackground(new Color(8, 20, 30));
        existingLoginPagePanel.setBounds(0, 0, 600, 400);
        setContentPane(existingLoginPagePanel);

        // Title label
        JLabel titleLabel = new JLabel("Welcome Back to VaultEdge", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 230, 255));
        titleLabel.setBounds(0, 40, 600, 40);
        existingLoginPagePanel.add(titleLabel);

        // Subtitle label
        JLabel subtitleLabel = new JLabel("Login to your account", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitleLabel.setForeground(new Color(200, 240, 255));
        subtitleLabel.setBounds(0, 90, 600, 20);
        existingLoginPagePanel.add(subtitleLabel);

        // Username label
        createLabel("Username", 150, 150, 300, 20, existingLoginPagePanel);

        // Username TextField
        JTextField usernameField = createTextField(150, 175, 300, 42, existingLoginPagePanel);

        // Password label
        createLabel("Password", 150, 240, 300, 20, existingLoginPagePanel);

        // Password PassField
        JPasswordField passwordField = createPasswordField(150, 265, 300, 42, existingLoginPagePanel);

        // Login button - WITH ACTIVITY LOGGING
        JButton loginButton = createButton("Login", 200, 330, 200, 42, existingLoginPagePanel);

        // Back button
        JButton backButton = createButton("Back", 200, 385, 200, 42, existingLoginPagePanel);

        loginButton.addActionListener(a -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter username and password!");
                return;
            }

            try (Connection con = DriverManager.getConnection(url, user, passwords)) {
                String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
                try (PreparedStatement pst = con.prepareStatement(sql)) {
                    pst.setString(1, username);
                    pst.setString(2, password);
                    ResultSet rs = pst.executeQuery();

                    if (rs.next()) {
                        logActivity("User Login", username, "System",
                                "User logged in successfully");

                        JOptionPane.showMessageDialog(null, "Login Successful!");
                        new HomePage(username);
                        dispose();
                    } else {
                        logActivity("Failed User Login", username, "System",
                                "Invalid credentials entered");

                        JOptionPane.showMessageDialog(null, "Invalid username or password");
                    }
                }
            } catch (Exception e) {
                logActivity("Login Error", username, "System",
                        "Database error during login: " + e.getMessage());

                JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage());
            }
        });

        backButton.addActionListener(a -> {
            new LandingPage();
            dispose();
        });

        // Frame settings
        setTitle("VaultEdge - Login");
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 480);
        setLocationRelativeTo(null);
        setLayout(null);
    }

    public static void main(String[] args) {
        new ExistingLoginPage();
    }
}