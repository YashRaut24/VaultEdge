import javax.swing.*;
import java.awt.*;
import java.sql.*;

class Withdraw extends JFrame {

    // Creates labels
    private JLabel createLabel(String text, int x, int y, int width, int height, JPanel panel) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
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
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.setBounds(x, y, width, height);
        panel.add(field);
        return field;
    }

    // Create dropdowns
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

    // Create buttons
    private JButton createButton(String text, int x, int y, int width, int height, JPanel panel,
                                 Color borderColor, Color textColor, Color bgColor) {
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

    // Constructor
    Withdraw(String username) {

        // DB Credentials
        String url = EnvLoader.get("DB_URL");
        String user = EnvLoader.get("DB_USER");
        String password = EnvLoader.get("DB_PASSWORD");

        // Withdraw panel
        JPanel withrawPanel = new JPanel(null);
        withrawPanel.setBackground(new Color(8, 20, 30));
        setContentPane(withrawPanel);

        // Title label
        JLabel titleLabel = new JLabel("Withdraw Money 💸", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 230, 255));
        titleLabel.setBounds(0, 25, 800, 40);
        withrawPanel.add(titleLabel);

        double currentBalance = fetchBalance(username);
        JLabel balanceLabel = createLabel("Current Balance: ₹" + currentBalance, 270, 90, 300, 30, withrawPanel);

        // Amount label
        createLabel("Enter Amount:", 180, 150, 200, 30, withrawPanel);

        // Amount TextField
        JTextField amountTextField = createTextField(350, 150, 250, 35, withrawPanel);

        // Withdrawal label
        createLabel("Withdrawal Method:", 180, 210, 200, 30, withrawPanel);
        String[] methods = {"VaultEdge Wallet", "UPI Transfer", "Virtual Card Transfer"};

        // Withdrawal method dropdown
        JComboBox<String> methodBox = createComboBox(methods, 350, 210, 250, 35, withrawPanel);

        // Note label
        createLabel("Note (optional):", 180, 270, 200, 30, withrawPanel);

        // Note TextField
        JTextField noteTextField = createTextField(350, 270, 250, 35, withrawPanel);

        // Status label
        JLabel statusLabel = createLabel("Status: Waiting for action...", 270, 400, 400, 30, withrawPanel);
        statusLabel.setForeground(new Color(0, 200, 255));

        // Withdraw button
        JButton withdrawButton = createButton("Withdraw", 230, 330, 150, 42, withrawPanel,
                new Color(0, 230, 255), Color.WHITE, new Color(0, 153, 76));

        // Cancel button
        JButton cancelButton = createButton("Cancel", 430, 330, 150, 42, withrawPanel,
                new Color(0, 230, 255), Color.WHITE, new Color(255, 51, 51));

        cancelButton.addActionListener(a -> {
            new HomePage(username);
            dispose();
        });

        withdrawButton.addActionListener(a -> {
            String withdrawAmmount = amountTextField.getText().trim();
            if (withdrawAmmount.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter an amount");
                return;
            }

            try {
                double amount = Double.parseDouble(withdrawAmmount);
                double balance = fetchBalance(username);
                String method = methodBox.getSelectedItem().toString();
                String note = noteTextField.getText().trim();

                if (amount <= 0) {
                    statusLabel.setText("Status: Invalid amount ❌");
                    return;
                }

                if (amount > balance) {
                    statusLabel.setText("Status: Insufficient Balance ❌");
                    return;
                }

                try (Connection con = DriverManager.getConnection(url, user, password)) {
                    con.setAutoCommit(false);

                    try (PreparedStatement pst = con.prepareStatement(
                            "UPDATE users SET balance = ? WHERE username = ?")) {
                        pst.setDouble(1, balance - amount);
                        pst.setString(2, username);
                        pst.executeUpdate();
                    }

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

                    updateTotalWithdrawals(username, amount);

                    con.commit();

                    amountTextField.setText("");
                    noteTextField.setText("");
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

    // Fetch user balance
    private double fetchBalance(String username) {
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

    // Update withdrawal total
    private void updateTotalWithdrawals(String username, double withdrawalAmount) {
        String url = EnvLoader.get("DB_URL");
        String user = EnvLoader.get("DB_USER");
        String password = EnvLoader.get("DB_PASSWORD");

        try (Connection con = DriverManager.getConnection(url, user, password)) {
            String updateSQL = "UPDATE transaction_summary SET total_withdrawals = total_withdrawals + ?, last_updated = CURRENT_TIMESTAMP WHERE username = ?";
            try (PreparedStatement pst = con.prepareStatement(updateSQL)) {
                pst.setDouble(1, withdrawalAmount);
                pst.setString(2, username);
                int rows = pst.executeUpdate();

                if (rows == 0) {
                    String insertSQL = "INSERT INTO transaction_summary(username, total_deposits, total_withdrawals, total_transfers) VALUES(?, 0, ?, 0)";
                    try (PreparedStatement insertPst = con.prepareStatement(insertSQL)) {
                        insertPst.setString(1, username);
                        insertPst.setDouble(2, withdrawalAmount);
                        insertPst.executeUpdate();
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Summary Update Failed: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new Withdraw("User");
    }
}
