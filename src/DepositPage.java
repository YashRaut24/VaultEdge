import javax.swing.*;
import java.awt.*;
import java.sql.*;

class DepositPage extends JFrame {

    // Create TextFields
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

    // Create buttons
    private JButton createButton(String text, int x, int y, int width, int height, JPanel panel,
                                 Color borderColor, Color textColor, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(textColor);
        button.setOpaque(true);
        button.setBackground(bgColor);
        button.setBorder(BorderFactory.createLineBorder(borderColor, 2));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBounds(x, y, width, height);
        panel.add(button);
        return button;
    }

    // Create labels
    private JLabel createLabel(String text, int x, int y, int width, int height, JPanel panel, int fontSize, boolean bold) {
        JLabel label = new JLabel(text, SwingConstants.LEFT);
        label.setFont(new Font("Segoe UI", bold ? Font.BOLD : Font.PLAIN, fontSize));
        label.setForeground(new Color(0, 230, 255));
        label.setBounds(x, y, width, height);
        panel.add(label);
        return label;
    }

    // Constructor
    DepositPage(String username) {

        // Database credentials
        String url = EnvLoader.get("DB_URL");
        String user = EnvLoader.get("DB_USER");
        String password = EnvLoader.get("DB_PASSWORD");

        // Deposit page panel
        JPanel depositPagePanel = new JPanel(null);
        depositPagePanel.setBackground(new Color(8, 20, 30));
        setContentPane(depositPagePanel);

        // Title label
        JLabel titleLabel = createLabel("💰 Deposit Money", 0, 40, 800, 40, depositPagePanel, 28, true);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Balance label
        JLabel balanceLabel = createLabel("Current Balance: ₹0.00", 250, 110, 300, 25, depositPagePanel, 16, false);

        double balance = 0.0;
        try (Connection con = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT balance FROM users WHERE username = ?";
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setString(1, username);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    balance = rs.getDouble("balance");
                    balanceLabel.setText("Current Balance: ₹" + balance);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

        // Enter amount label
        createLabel("Enter Amount:", 250, 160, 200, 25, depositPagePanel, 16, false);

        // Amount TextField
        JTextField amountField = createTextField(250, 190, 300, 35, depositPagePanel);

        // Payment method label
        createLabel("Payment Method:", 250, 240, 200, 25, depositPagePanel, 16, false);

        // Stores dropdown values
        String[] methods = {"VaultEdge Wallet", "UPI Transfer", "Credit/Debit Card", "Net Banking"};

        // Creates payment method dropdown
        JComboBox<String> paymentMethod = new JComboBox<>(methods);
        paymentMethod.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        paymentMethod.setBackground(new Color(15, 30, 40));
        paymentMethod.setForeground(new Color(220, 235, 245));
        paymentMethod.setBounds(250, 270, 300, 35);
        depositPagePanel.add(paymentMethod);

        // Note label
        createLabel("Note (optional):", 250, 320, 200, 25, depositPagePanel, 16, false);

        // Note TextField
        JTextField noteTextField = createTextField(250, 350, 300, 35, depositPagePanel);

        // Deposit button
        JButton depositButton = createButton("Deposit", 250, 410, 140, 40, depositPagePanel,
                new Color(0, 230, 255), Color.WHITE, new Color(0, 153, 76));

        // Cancel button
        JButton cancelButton = createButton("Cancel", 410, 410, 140, 40, depositPagePanel,
                new Color(0, 230, 255), Color.WHITE, new Color(255, 51, 51));

        cancelButton.addActionListener(e -> {
            new HomePage(username);
            dispose();
        });

        // Status label
        JLabel statusLabel = createLabel("", 250, 470, 400, 25, depositPagePanel, 16, false);

        double finalBalance = balance;
        depositButton.addActionListener(e -> {
            String input = amountField.getText();
            String method = (String) paymentMethod.getSelectedItem();
            String note = noteTextField.getText();

            if (input.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter amount");
                return;
            }

            try {
                double amount = Double.parseDouble(input);
                double total = finalBalance + amount;

                try (Connection con = DriverManager.getConnection(url, user, password)) {
                    String balanceSql = "UPDATE users SET balance = ? WHERE username = ?";
                    try (PreparedStatement pst = con.prepareStatement(balanceSql)) {
                        pst.setDouble(1, total);
                        pst.setString(2, username);
                        pst.executeUpdate();
                    }

                    updatePassbook(username, "Deposit via " + method, amount, total, note);

                    updateTotalDeposits(username, amount);

                    statusLabel.setText("Deposit Successful via " + method);
                    balanceLabel.setText("Current Balance: ₹" + total);
                    amountField.setText("");
                    noteTextField.setText("");

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }

            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(null, "Invalid amount entered");
            }
        });

        // Frame settings
        setTitle("VaultEdge - Deposit Money");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    // Updates passbook
    void updatePassbook(String username, String desc, double amount, double balance, String note) {

        String url = EnvLoader.get("DB_URL");
        String user = EnvLoader.get("DB_USER");
        String password = EnvLoader.get("DB_PASSWORD");

        try (Connection con = DriverManager.getConnection(url, user, password)) {
            String passbookSql = "INSERT INTO transactions(username, description, amount, balance, note, type, balance_after) VALUES(?,?,?,?,?,?,?)";
            try (PreparedStatement pst = con.prepareStatement(passbookSql)) {
                pst.setString(1, username);
                pst.setString(2, desc);
                pst.setDouble(3, amount);
                pst.setDouble(4, balance);
                pst.setString(5, note);
                pst.setString(6, "Deposit");
                pst.setDouble(7, balance);
                pst.executeUpdate();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    // Updates total deposits
    void updateTotalDeposits(String username, double depositAmount) {
        String url = EnvLoader.get("DB_URL");
        String user = EnvLoader.get("DB_USER");
        String password = EnvLoader.get("DB_PASSWORD");

        try (Connection con = DriverManager.getConnection(url, user, password)) {
            String updateDepositSQL = "UPDATE transaction_summary SET total_deposits = total_deposits + ?, last_updated = CURRENT_TIMESTAMP WHERE username = ?";
            try (PreparedStatement pst = con.prepareStatement(updateDepositSQL)) {
                pst.setDouble(1, depositAmount);
                pst.setString(2, username);
                int rows = pst.executeUpdate();

                if (rows == 0) {
                    String depositSql = "INSERT INTO transaction_summary(username, total_deposits, total_withdrawals, total_transfers) VALUES(?, ?, 0, 0)";
                    try (PreparedStatement insertPst = con.prepareStatement(depositSql)) {
                        insertPst.setString(1, username);
                        insertPst.setDouble(2, depositAmount);
                        insertPst.executeUpdate();
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Summary Update Failed: " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        new DepositPage("Yash24");
    }
}
