import javax.swing.*;
import java.awt.*;
import java.sql.*;

class Withdraw extends JFrame {

    // Styled label
    private JLabel createLabel(String text, int x, int y, int width, int height, JPanel panel) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setForeground(new Color(200, 240, 255));
        label.setBounds(x, y, width, height);
        panel.add(label);
        return label;
    }

    // Styled text field
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
        panel.add(field);
        return field;
    }

    // Styled combo box
    private JComboBox<String> createComboBox(String[] items, int x, int y, int width, int height, JPanel panel) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        combo.setBackground(new Color(15, 30, 40));
        combo.setForeground(new Color(220, 235, 245));
        combo.setBorder(BorderFactory.createLineBorder(new Color(0, 230, 255)));
        combo.setBounds(x, y, width, height);
        panel.add(combo);
        return combo;
    }

    // Styled button
    private JButton createButton(String text, int x, int y, int width, int height, JPanel panel, Color borderColor, Color textColor, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(textColor);
        button.setBackground(bgColor);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorder(BorderFactory.createLineBorder(borderColor, 2));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBounds(x, y, width, height);
        panel.add(button);
        return button;
    }

    Withdraw(String username) {

        // DB Credentials
        String url = EnvLoader.get("DB_URL");
        String user = EnvLoader.get("DB_USER");
        String password = EnvLoader.get("DB_PASSWORD");

        // Withdraw page
        JPanel backgroundPanel = new JPanel(null);
        backgroundPanel.setBackground(new Color(8, 20, 30));
        setContentPane(backgroundPanel);

        // Title label
        JLabel titleLabel = new JLabel("Withdraw Money 💸", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 230, 255));
        titleLabel.setBounds(0, 25, 800, 40);
        backgroundPanel.add(titleLabel);

        double currentBalance = fetchBalance(username);
        JLabel balanceLabel = createLabel("Current Balance: ₹" + currentBalance, 270, 90, 300, 30, backgroundPanel);

        // Amount label
        createLabel("Enter Amount:", 180, 150, 200, 30, backgroundPanel);

        // Amount TextFields
        JTextField amountTextField = createTextField(350, 150, 250, 35, backgroundPanel);

        // Withdrawal label
        createLabel("Withdrawal Method:", 180, 210, 200, 30, backgroundPanel);

        // Withdrawal TextField
        String[] methods = {"VaultEdge Wallet", "UPI Transfer", "Virtual Card Transfer"};
        JComboBox<String> methodBox = createComboBox(methods, 350, 210, 250, 35, backgroundPanel);

        // Note (optional)
        createLabel("Note (optional):", 180, 270, 200, 30, backgroundPanel);
        JTextField noteField = createTextField(350, 270, 250, 35, backgroundPanel);

        // Status Label
        JLabel statusLabel = createLabel("Status: Waiting for action...", 270, 400, 400, 30, backgroundPanel);
        statusLabel.setForeground(new Color(0, 200, 255));

        // Withdraw button
        JButton withdrawButton = createButton("Withdraw", 230, 330, 150, 42, backgroundPanel,
                new Color(0, 230, 255), Color.WHITE, new Color(0, 153, 76));

        // Cancel button
        JButton cancelButton = createButton("Cancel", 430, 330, 150, 42, backgroundPanel,
                new Color(0, 230, 255), Color.WHITE, new Color(255, 51, 51));

        // Button Actions
        cancelButton.addActionListener(a -> {
            new HomePage(username);
            dispose();
        });

        withdrawButton.addActionListener(a -> {
            String amtStr = amountTextField.getText().trim();
            if (amtStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter an amount");
                return;
            }

            try {
                double amount = Double.parseDouble(amtStr);
                double balance = fetchBalance(username);
                String method = methodBox.getSelectedItem().toString();
                String note = noteField.getText().trim();

                if (amount <= 0) {
                    statusLabel.setText("Status: Invalid amount ❌");
                    return;
                }

                if (amount > balance) {
                    statusLabel.setText("Status: Insufficient Balance ❌");
                    return;
                }

                try (Connection con = DriverManager.getConnection(url, user, password)) {
                    con.setAutoCommit(false); // Start transaction

                    // Deduct balance
                    try (PreparedStatement pst = con.prepareStatement("UPDATE users SET balance = ? WHERE username = ?")) {
                        pst.setDouble(1, balance - amount);
                        pst.setString(2, username);
                        pst.executeUpdate();
                    }

                    // Insert into passbook
                    // Insert into transactions/passbook
                    try (PreparedStatement pst = con.prepareStatement(
                            "INSERT INTO transactions(username, description, type, amount, balance, balance_after, note, date) " +
                                    "VALUES(?,?,?,?,?,?,?,NOW())")) {
                        pst.setString(1, username);
                        pst.setString(2, "Withdraw via " + method);
                        pst.setString(3, "Withdraw");
                        pst.setDouble(4, amount);
                        pst.setDouble(5, balance);
                        pst.setDouble(6, balance - amount);
                        pst.setString(7, note);
                        pst.executeUpdate();
                    }


                    con.commit();

                    amountTextField.setText("");
                    noteField.setText("");
                    balanceLabel.setText("Current Balance: ₹" + (balance - amount));
                    statusLabel.setText("Status: Withdrawal Successful ✅");

                } catch (Exception e) {
                    statusLabel.setText("Status: Withdrawal Failed ❌");
                    JOptionPane.showMessageDialog(null, e.getMessage());
                }

            } catch (NumberFormatException e) {
                statusLabel.setText("Status: Invalid input ❌");
            }
        });

        // Frame settings
        setTitle("VaultEdge - Withdraw Money");
        setSize(800, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }


    // Fetches balance amount
    private double fetchBalance(String username) {

        // DB Credentials
        String url = EnvLoader.get("DB_URL");
        String user = EnvLoader.get("DB_USER");
        String password = EnvLoader.get("DB_PASSWORD");

        double balance = 0.0;
        try (Connection con = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT balance FROM users WHERE username = ?";
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setString(1, username);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) balance = rs.getDouble("balance");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return balance;
    }

    private void updateBalance(String username, double balance) {

        // DB Credentials
        String url = EnvLoader.get("DB_URL");
        String user = EnvLoader.get("DB_USER");
        String password = EnvLoader.get("DB_PASSWORD");

        try (Connection con = DriverManager.getConnection(url, user, password)) {
            String sql = "UPDATE users SET balance = ? WHERE username = ?";
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setDouble(1, balance);
                pst.setString(2, username);
                pst.executeUpdate();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    private void updatePassbook(String username, String desc, double amount, double balance, String note) {

        // DB Credentials
        String url = EnvLoader.get("DB_URL");
        String user = EnvLoader.get("DB_USER");
        String password = EnvLoader.get("DB_PASSWORD");

        try (Connection con = DriverManager.getConnection(url, user, password)) {
            String sql = "INSERT INTO transactions(username, description, amount, balance, note) VALUES(?,?,?,?,?)";
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setString(1, username);
                pst.setString(2, desc);
                pst.setDouble(3, amount);
                pst.setDouble(4, balance);
                pst.setString(5, note);
                pst.executeUpdate();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public static void main(String[] args) {
        new Withdraw("User");
    }
}
