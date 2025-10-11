import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

class NewloginPage extends JFrame {

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
        JPanel backgroundPanel = new JPanel(null);
        backgroundPanel.setBackground(new Color(8, 20, 30));
        setContentPane(backgroundPanel);

        // Title Label
        JLabel title = new JLabel("Create VaultEdge Account", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(0, 230, 255));
        title.setBounds(0, 30, 700, 40);
        backgroundPanel.add(title);

        JLabel subtitle = new JLabel("Your secure gateway to digital banking", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(200, 240, 255));
        subtitle.setBounds(0, 70, 700, 20);
        backgroundPanel.add(subtitle);

        // ===== Left Column =====
        createLabel("Full Name", 60, 120, 200, 20, backgroundPanel);
        JTextField nameField = createTextField(60, 140, 250, 35, backgroundPanel);

        createLabel("Address", 60, 190, 200, 20, backgroundPanel);
        JTextField addressField = createTextField(60, 210, 250, 35, backgroundPanel);

        createLabel("Password", 60, 260, 200, 20, backgroundPanel);
        JPasswordField passwordField = createPasswordField(60, 280, 250, 35, backgroundPanel);

        createLabel("Email", 60, 330, 200, 20, backgroundPanel);
        JTextField emailField = createTextField(60, 350, 250, 35, backgroundPanel);

        createLabel("Gender", 60, 400, 200, 20, backgroundPanel);
        JComboBox<String> genderBox = createComboBox(new String[]{"Male", "Female", "Other"}, 60, 420, 250, 35, backgroundPanel);

        // ===== Right Column =====
        createLabel("Date of Birth (YYYY-MM-DD)", 380, 120, 250, 20, backgroundPanel);
        JTextField dobField = createTextField(380, 140, 250, 35, backgroundPanel);

        createLabel("Username", 380, 190, 200, 20, backgroundPanel);
        JTextField usernameField = createTextField(380, 210, 250, 35, backgroundPanel);

        createLabel("Confirm Password", 380, 260, 200, 20, backgroundPanel);
        JPasswordField confirmField = createPasswordField(380, 280, 250, 35, backgroundPanel);

        createLabel("Phone Number", 380, 330, 200, 20, backgroundPanel);
        JTextField phoneField = createTextField(380, 350, 250, 35, backgroundPanel);

        createLabel("Account Type", 380, 400, 200, 20, backgroundPanel);
        JComboBox<String> accountTypeBox = createComboBox(new String[]{"Savings", "Current"}, 380, 420, 250, 35, backgroundPanel);

        // ===== Deposit =====
        createLabel("Initial Deposit", 255, 480, 195, 20, backgroundPanel);
        JTextField depositField = createTextField(255, 500, 195, 35, backgroundPanel);

        // ===== Buttons =====
        JButton submitButton = createButton("Create Account", 190, 560, 150, 40, backgroundPanel);
        JButton backButton = createButton("Back", 370, 560, 150, 40, backgroundPanel);

        // ===== Actions =====
        submitButton.addActionListener(a -> {
            String fullname = nameField.getText();
            String dob = dobField.getText();
            String address = addressField.getText();
            String username = usernameField.getText();
            String email = emailField.getText();
            String phone = phoneField.getText();
            String gender = genderBox.getSelectedItem().toString();
            String accountType = accountTypeBox.getSelectedItem().toString();
            String depositText = depositField.getText();
            String password = new String(passwordField.getPassword());
            String confirm = new String(confirmField.getPassword());

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

                String url = "jdbc:mysql://localhost:3306/3dec";
                try (Connection con = DriverManager.getConnection(url, "root", "your_password")) {
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
