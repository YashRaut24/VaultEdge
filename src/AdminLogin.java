import javax.swing.*;
import java.awt.*;
import java.sql.*;

class AdminLogin extends JFrame {

    // Creates labels
    private JLabel createLabel(String text, int x, int y, int width, int height, JPanel panel) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(200, 240, 255));
        label.setBounds(x, y, width, height);
        panel.add(label);
        return label;
    }

    // Creates TextFields
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

    // Create PasswordFields
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

    // Creates Buttons
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

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 255), 3));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBorder(BorderFactory.createLineBorder(new Color(0, 230, 255), 2));
            }
        });

        panel.add(button);
        return button;
    }

        AdminLogin() {

        // DB Credentials
        String url = EnvLoader.get("DB_URL");
        String user = EnvLoader.get("DB_USER");
        String password = EnvLoader.get("DB_PASSWORD");

        // AdminLogin Page
        JPanel adminLoginPanel = new JPanel(null);
        adminLoginPanel.setBackground(new Color(8, 20, 30));
        setContentPane(adminLoginPanel);

        // Title label
        JLabel titleLabel = new JLabel("Admin Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titleLabel.setForeground(new Color(0, 230, 255));
        titleLabel.setBounds(0, 40, 600, 40);
        adminLoginPanel.add(titleLabel);

        // Subtitle Label
        JLabel subtitleLabel = new JLabel("Access control for VaultEdge administrators", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(200, 240, 255));
        subtitleLabel.setBounds(0, 85, 600, 20);
        adminLoginPanel.add(subtitleLabel);

        // Username label
        createLabel("Username", 150, 160, 300, 20, adminLoginPanel);

        // Username TextField
        JTextField usernameTextField = createTextField(150, 185, 300, 42, adminLoginPanel);

        // Password label
        createLabel("Password", 150, 250, 300, 20, adminLoginPanel);

        // Password TextField
        JPasswordField passwordTextField = createPasswordField(150, 275, 300, 42, adminLoginPanel);

        // Login button
        JButton loginButton = createButton("Login", 180, 360, 110, 40, adminLoginPanel);

        // Back Button
        JButton backButton = createButton("Back", 310, 360, 110, 40, adminLoginPanel);

            loginButton.addActionListener(a -> {
                String username = usernameTextField.getText();
                String passwordText = new String(passwordTextField.getPassword());

                String loginSqlQuery = "SELECT * FROM admin_login WHERE username=? AND password=?";

                try (Connection con = DriverManager.getConnection(url, user, password)) {
                    // Nested try-with-resources for PreparedStatement
                    try (PreparedStatement pst = con.prepareStatement(loginSqlQuery)) {
                        pst.setString(1, username);
                        pst.setString(2, passwordText);

                        try (ResultSet rs = pst.executeQuery()) {
                            if (rs.next()) {
                                JOptionPane.showMessageDialog(null, "Admin Login Successful!");
                                new AdminDashboard(username);
                                dispose();
                            } else {
                                JOptionPane.showMessageDialog(null, "Invalid credentials! Try again.");
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage());
                }
            });


            backButton.addActionListener(a -> {
            new LandingPage();
            dispose();
        });

        // Frame Settings
        setTitle("VaultEdge - Admin Login");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        new AdminLogin();
    }
}
