import javax.swing.*;
import java.awt.*;

class AdminLogin extends JFrame {

    // Creates styled label
    private JLabel createLabel(String text, int x, int y, int width, int height, JPanel panel) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(200, 240, 255));
        label.setBounds(x, y, width, height);
        panel.add(label);
        return label;
    }

    // Creates styled text field
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

    // Creates styled password field
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

    // Creates styled button
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

    AdminLogin() {
        // Solid background panel
        JPanel backgroundPanel = new JPanel(null);
        backgroundPanel.setBackground(new Color(8, 20, 30));
        backgroundPanel.setBounds(0, 0, 450, 500);
        setContentPane(backgroundPanel);


        // Title label
        JLabel title = new JLabel("Admin Access", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(new Color(0, 230, 255));
        title.setBounds(0, 40, 450, 45);
        backgroundPanel.add(title);

        // Subtitle
        JLabel subtitle = new JLabel("Secure administrator login", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(200, 240, 255));
        subtitle.setBounds(0, 90, 450, 20);
        backgroundPanel.add(subtitle);

        // Username
        createLabel("Username", 75, 150, 300, 20, backgroundPanel);
        JTextField usernameField = createTextField(75, 175, 300, 42, backgroundPanel);

        // Password
        createLabel("Password", 75, 235, 300, 20, backgroundPanel);
        JPasswordField passwordField = createPasswordField(75, 260, 300, 42, backgroundPanel);

        // Buttons
        JButton submitButton = createButton("Login", 125, 340, 200, 42, backgroundPanel);
        JButton backButton = createButton("Back", 125, 395, 200, 42, backgroundPanel);

        // Action listeners
        submitButton.addActionListener(a -> {
            String password = new String(passwordField.getPassword());
            if (password.equals("pass") && usernameField.getText().equals("admin")) {
                JOptionPane.showMessageDialog(null, "Admin Login Successful!");
                new AdminDashboard();
                dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Admin login failed");
            }
        });

        backButton.addActionListener(a -> {
            new LandingPage();
            dispose();
        });

        // Frame settings
        setTitle("VaultEdge - Admin Login");
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(450, 500);
        setLocationRelativeTo(null);
        setLayout(null);
    }

    public static void main(String[] args) {
        new AdminLogin();
    }
}