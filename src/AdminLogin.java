import javax.swing.*;
import java.awt.*;

class AdminLogin extends JFrame {

    // ===== Helper Methods =====
    private JLabel createLabel(String text, int x, int y, int width, int height, JPanel panel) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
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
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        field.setBounds(x, y, width, height);
        panel.add(field);
        return field;
    }

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

        // Hover effect
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

    // ===== Constructor =====
    AdminLogin() {
        JPanel backgroundPanel = new JPanel(null);
        backgroundPanel.setBackground(new Color(8, 20, 30));
        setContentPane(backgroundPanel);

        // ===== Title =====
        JLabel title = new JLabel("Admin Login", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(new Color(0, 230, 255));
        title.setBounds(0, 40, 600, 40);
        backgroundPanel.add(title);

        JLabel subtitle = new JLabel("Access control for VaultEdge administrators", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(200, 240, 255));
        subtitle.setBounds(0, 85, 600, 20);
        backgroundPanel.add(subtitle);

        // ===== Fields =====
        createLabel("Username", 150, 160, 300, 20, backgroundPanel);
        JTextField usernameField = createTextField(150, 185, 300, 42, backgroundPanel);

        createLabel("Password", 150, 250, 300, 20, backgroundPanel);
        JPasswordField passwordField = createPasswordField(150, 275, 300, 42, backgroundPanel);

        // ===== Buttons =====
        JButton loginButton = createButton("Login", 180, 360, 110, 40, backgroundPanel);
        JButton backButton = createButton("Back", 310, 360, 110, 40, backgroundPanel);

        // ===== Button Logic =====
        loginButton.addActionListener(a -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (username.equals("admin") && password.equals("pass")) {
                JOptionPane.showMessageDialog(null, "Admin Login Successful!");
                new AdminDashboard();
                dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Invalid credentials! Try again.");
            }
        });

        backButton.addActionListener(a -> {
            new LandingPage();
            dispose();
        });

        // ===== Frame Settings =====
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
