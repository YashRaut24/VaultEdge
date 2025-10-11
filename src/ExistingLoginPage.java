import javax.swing.*;
import java.awt.*;
import java.sql.*;

class ExistingLoginPage extends JFrame {

    // Create styled label
    private JLabel createLabel(String text, int x, int y, int width, int height, JPanel panel) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(200, 240, 255));
        label.setBounds(x, y, width, height);
        panel.add(label);
        return label;
    }

    // Create styled text field
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

    // Create styled password field
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

    // Create styled button
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

        // Background panel
        JPanel backgroundPanel = new JPanel(null);
        backgroundPanel.setBackground(new Color(8, 20, 30));
        backgroundPanel.setBounds(0, 0, 600, 400);
        setContentPane(backgroundPanel);

        // Title
        JLabel title = new JLabel("Welcome Back to VaultEdge", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(0, 230, 255));
        title.setBounds(0, 40, 600, 40);
        backgroundPanel.add(title);

        // Subtitle
        JLabel subtitle = new JLabel("Login to your account", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(new Color(200, 240, 255));
        subtitle.setBounds(0, 90, 600, 20);
        backgroundPanel.add(subtitle);

        // Username field
        createLabel("Username", 150, 150, 300, 20, backgroundPanel);
        JTextField usernameField = createTextField(150, 175, 300, 42, backgroundPanel);

        // Password field
        createLabel("Password", 150, 240, 300, 20, backgroundPanel);
        JPasswordField passwordField = createPasswordField(150, 265, 300, 42, backgroundPanel);

        // Buttons
        JButton loginButton = createButton("Login", 200, 330, 200, 42, backgroundPanel);
        JButton backButton = createButton("Back", 200, 385, 200, 42, backgroundPanel);

        // Action listener for login
        loginButton.addActionListener(a -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter username and password!");
                return;
            }

            String url = "jdbc:mysql://localhost:3306/3dec"; // Change DB name as needed
            try (Connection con = DriverManager.getConnection(url, "root", "your_password")) {
                String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
                try (PreparedStatement pst = con.prepareStatement(sql)) {
                    pst.setString(1, username);
                    pst.setString(2, password);
                    ResultSet rs = pst.executeQuery();

                    if (rs.next()) {
                        JOptionPane.showMessageDialog(null, "Login Successful!");
                        new HomePage(username);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid username or password");
                    }
                }
            } catch (Exception e) {
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
