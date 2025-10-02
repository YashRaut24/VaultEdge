import javax.swing.*;
import java.awt.*;
import java.sql.*;

class NewloginPage extends JFrame {

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

    // Creates styled combobox
    private JComboBox<String> createGenderComboBox(int x, int y, int width, int height, JPanel panel) {
        JComboBox<String> genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderBox.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        genderBox.setBackground(new Color(15, 30, 40));
        genderBox.setForeground(new Color(220, 235, 245));
        genderBox.setBorder(BorderFactory.createLineBorder(new Color(0, 230, 255), 1));
        genderBox.setBounds(x, y, width, height);
        panel.add(genderBox);
        return genderBox;
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

    NewloginPage() {
        // Solid background panel
        JPanel backgroundPanel = new JPanel(null);
        backgroundPanel.setBackground(new Color(8, 20, 30));
        backgroundPanel.setBounds(0, 0, 500, 600);
        setContentPane(backgroundPanel);

        // Title label
        JLabel title = new JLabel("Create Account", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(new Color(0, 230, 255));
        title.setBounds(0, 20, 500, 40);
        backgroundPanel.add(title);

        // Subtitle
        JLabel subtitle = new JLabel("Join VaultEdge for secure banking", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(200, 240, 255));
        subtitle.setBounds(0, 65, 500, 20);
        backgroundPanel.add(subtitle);

        // Username
        createLabel("Username", 50, 100, 400, 20, backgroundPanel);
        JTextField usernameField = createTextField(50, 120, 400, 38, backgroundPanel);

        // Password
        createLabel("Password", 50, 168, 400, 20, backgroundPanel);
        JPasswordField passwordField = createPasswordField(50, 188, 400, 38, backgroundPanel);

        // Confirm Password
        createLabel("Confirm Password", 50, 236, 400, 20, backgroundPanel);
        JPasswordField confirmField = createPasswordField(50, 256, 400, 38, backgroundPanel);

        // Email Address
        createLabel("Email Address", 50, 304, 400, 20, backgroundPanel);
        JTextField emailField = createTextField(50, 324, 400, 38, backgroundPanel);

        // Phone Number (left half)
        createLabel("Phone Number", 50, 372, 195, 20, backgroundPanel);
        JTextField phoneField = createTextField(50, 392, 195, 38, backgroundPanel);

        // Gender (right half)
        createLabel("Gender", 255, 372, 195, 20, backgroundPanel);
        JComboBox<String> genderBox = createGenderComboBox(255, 392, 195, 38, backgroundPanel);

        // Buttons
        JButton submitButton = createButton("Create Account", 80, 450, 160, 40, backgroundPanel);
        JButton backButton = createButton("Back", 260, 450, 160, 40, backgroundPanel);

        // Action listeners
        submitButton.addActionListener(a -> {
            String password = new String(passwordField.getPassword());
            String confirm = new String(confirmField.getPassword());

            if (password.equals(confirm)) {
                String url = "jdbc:mysql://localhost:3306/3dec";
                try (Connection con = DriverManager.getConnection(url, "root", "your_password")) {
                    String sql = "INSERT INTO users(username,password,phone,email,gender) VALUES(?, ?, ?, ?, ?)";
                    try (PreparedStatement pst = con.prepareStatement(sql)) {
                        pst.setString(1, usernameField.getText());
                        pst.setString(2, password);
                        pst.setString(3, phoneField.getText());
                        pst.setString(4, emailField.getText());
                        pst.setString(5, genderBox.getSelectedItem().toString());

                        pst.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Signup Successful!");
                        new HomePage(usernameField.getText());
                        dispose();
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(null, "Passwords do not match");
            }
        });

        backButton.addActionListener(a -> {
            new LandingPage();
            dispose();
        });

        // Frame settings
        setTitle("VaultEdge - Signup");
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);
        setLayout(null);
    }

    public static void main(String[] args) {
        new NewloginPage();
    }
}