import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

class NewloginPage extends JFrame {

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

    // Create passwordField
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

    // Create dropdowns
    private JComboBox<String> createComboBox(String[] items, int x, int y, int width, int height, JPanel panel) {
        JComboBox<String> box = new JComboBox<>(items);
        box.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        box.setBackground(new Color(15, 30, 40));
        box.setForeground(new Color(220, 235, 245));
        box.setBorder(BorderFactory.createLineBorder(new Color(0, 230, 255), 1));
        box.setBounds(x, y, width, height);
        panel.add(box);
        return box;
    }

    // Creates buttons
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

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 255), 3));
            }
            public void mouseExited(MouseEvent e) {
                button.setBorder(BorderFactory.createLineBorder(new Color(0, 230, 255), 2));
            }
        });

        panel.add(button);
        return button;
    }

    NewloginPage() {

        // DB Credentials
        String url = EnvLoader.get("DB_URL");
        String user = EnvLoader.get("DB_USER");
        String passwords = EnvLoader.get("DB_PASSWORD");

        // New login page panel
        JPanel newLoginPagePanel = new JPanel(null);
        newLoginPagePanel.setBackground(new Color(8, 20, 30));
        setContentPane(newLoginPagePanel);

        // Title Label
        JLabel titleLabel = new JLabel("Create VaultEdge Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 230, 255));
        titleLabel.setBounds(0, 30, 700, 40);
        newLoginPagePanel.add(titleLabel);

        // Subtitle label
        JLabel subtitleLabel = new JLabel("Your secure gateway to digital banking", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(200, 240, 255));
        subtitleLabel.setBounds(0, 70, 700, 20);
        newLoginPagePanel.add(subtitleLabel);

        // Full name label
        createLabel("Full Name", 60, 120, 200, 20, newLoginPagePanel);

        // Full name TextField
        JTextField nameTextField = createTextField(60, 140, 250, 35, newLoginPagePanel);

        // Address label
        createLabel("Address", 60, 190, 200, 20, newLoginPagePanel);

        // Address TextField
        JTextField addressTextField = createTextField(60, 210, 250, 35, newLoginPagePanel);

        // Password label
        createLabel("Password", 60, 260, 200, 20, newLoginPagePanel);

        // Password PasswordField
        JPasswordField passwordField = createPasswordField(60, 280, 250, 35, newLoginPagePanel);

        // Email label
        createLabel("Email", 60, 330, 200, 20, newLoginPagePanel);

        // Email TextField
        JTextField emailTextField = createTextField(60, 350, 250, 35, newLoginPagePanel);

        // Gender label
        createLabel("Gender", 60, 400, 200, 20, newLoginPagePanel);

        // Gender dropdown
        JComboBox<String> genderBox = createComboBox(new String[]{"Male", "Female", "Other"}, 60, 420, 250, 35, newLoginPagePanel);

        // Date of birth label
        createLabel("Date of Birth (YYYY-MM-DD)", 380, 120, 250, 20, newLoginPagePanel);

        // Date of birth TextField
        JTextField dobTextField = createTextField(380, 140, 250, 35, newLoginPagePanel);

        // Username label
        createLabel("Username", 380, 190, 200, 20, newLoginPagePanel);

        // Username TextField
        JTextField usernameTextField = createTextField(380, 210, 250, 35, newLoginPagePanel);

        // Confirm password label
        createLabel("Confirm Password", 380, 260, 200, 20, newLoginPagePanel);

        // Confirm password field
        JPasswordField confirmPassField = createPasswordField(380, 280, 250, 35, newLoginPagePanel);

        // Phone number label
        createLabel("Phone Number", 380, 330, 200, 20, newLoginPagePanel);

        // PhoneNumber text field
        JTextField phoneNumberTextField = createTextField(380, 350, 250, 35, newLoginPagePanel);

        // Account type label
        createLabel("Account Type", 380, 400, 200, 20, newLoginPagePanel);

        // Account type dropdown
        JComboBox<String> accountTypeBox = createComboBox(new String[]{"Savings", "Current"}, 380, 420, 250, 35, newLoginPagePanel);

        // Deposit label
        createLabel("Initial Deposit", 255, 480, 195, 20, newLoginPagePanel);

        // Deposit TextField
        JTextField depositTextField = createTextField(255, 500, 195, 35, newLoginPagePanel);

        // Submit button
        JButton submitButton = createButton("Create Account", 190, 560, 150, 40, newLoginPagePanel);

        // Back button
        JButton backButton = createButton("Back", 370, 560, 150, 40, newLoginPagePanel);

        submitButton.addActionListener(a -> {
            String fullname = nameTextField.getText();
            String dob = dobTextField.getText();
            String address = addressTextField.getText();
            String username = usernameTextField.getText();
            String email = emailTextField.getText();
            String phone = phoneNumberTextField.getText();
            String gender = genderBox.getSelectedItem().toString();
            String accountType = accountTypeBox.getSelectedItem().toString();
            String depositText = depositTextField.getText();
            String password = new String(passwordField.getPassword());
            String confirm = new String(confirmPassField.getPassword());

            if (fullname.isEmpty() || dob.isEmpty() || address.isEmpty() || username.isEmpty() ||
                    password.isEmpty() || email.isEmpty() || phone.isEmpty() || depositText.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill all fields!");
                return;
            }

            if (!password.equals(confirm)) {
                JOptionPane.showMessageDialog(null, "Passwords do not match!");
                return;
            }

            try {
                double deposit = Double.parseDouble(depositText);
                if (deposit < 0) {
                    JOptionPane.showMessageDialog(null, "Deposit amount cannot be negative!");
                    return;
                }

                try (Connection con = DriverManager.getConnection(url, user, passwords)) {
                    String sql = "INSERT INTO users(fullname, dob, address, username, password, email, phone, gender, account_type, balance) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement pst = con.prepareStatement(sql);
                    pst.setString(1, fullname);
                    pst.setString(2, dob);
                    pst.setString(3, address);
                    pst.setString(4, username);
                    pst.setString(5, password); // plain password
                    pst.setString(6, email);
                    pst.setString(7, phone);
                    pst.setString(8, gender);
                    pst.setString(9, accountType);
                    pst.setDouble(10, deposit);

                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Signup Successful! Welcome to VaultEdge.");
                    new HomePage(username);
                    dispose();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid deposit amount!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            }
        });

        backButton.addActionListener(a -> {
            new LandingPage();
            dispose();
        });

        // Frame
        setTitle("VaultEdge - Create Account");
        setSize(700, 670);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        new NewloginPage();
    }
}
