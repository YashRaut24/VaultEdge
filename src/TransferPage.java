import javax.swing.*;
import java.awt.*;
import java.sql.*;

class TransferPage extends JFrame {

    // Create labels
    private JLabel createLabel(String text, int x, int y, int width, int height, JPanel panel) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
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
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.setBounds(x, y, width, height);
        panel.add(field);
        return field;
    }

    // Create buttons
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

    // Constructor
    TransferPage(String username) {

        // DB credentials
        String url = EnvLoader.get("DB_URL");
        String user = EnvLoader.get("DB_USER");
        String password = EnvLoader.get("DB_PASSWORD");

        // Transfer panel
        JPanel transferPanel = new JPanel(null);
        transferPanel.setBackground(new Color(8, 20, 30));
        setContentPane(transferPanel);

        // Title label
        JLabel titleLabel = new JLabel("Transfer Funds", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 230, 255));
        titleLabel.setBounds(0, 20, 800, 40);
        transferPanel.add(titleLabel);

        double currentBalance = fetchBalance(username);

        // Balance label
        JLabel balanceLabel = createLabel("Current Balance: ₹" + currentBalance, 250, 70, 300, 25, transferPanel);

        // Receiver label
        createLabel("Receiver (Username):", 200, 120, 180, 30, transferPanel);

        // Receiver TextField
        JTextField receiverTextField = createTextField(400, 120, 200, 30, transferPanel);

        // Amount label
        createLabel("Amount (₹):", 200, 170, 180, 30, transferPanel);

        // Amount TextField
        JTextField amountTextField = createTextField(400, 170, 200, 30, transferPanel);

        // Note label
        createLabel("Note (optional):", 200, 220, 180, 30, transferPanel);

        // Note TextField
        JTextField noteTextField = createTextField(400, 220, 200, 30, transferPanel);

        // Status label
        JLabel statusLabel = createLabel("Status: Waiting for action...", 250, 280, 400, 30, transferPanel);
        statusLabel.setForeground(new Color(0, 200, 255));

        // Transfer button
        JButton transferButton = createButton("Transfer", 250, 330, 120, 42, transferPanel);

        // Back button
        JButton backButton = createButton("Back", 400, 330, 120, 42, transferPanel);

        backButton.addActionListener(e -> {
            new HomePage(username);
            dispose();
        });

        transferButton.addActionListener(e -> {
            String receiver = receiverTextField.getText().trim();
            String amtStr = amountTextField.getText().trim();
            String note = noteTextField.getText().trim();

            if (receiver.isEmpty() || amtStr.isEmpty()) {
                statusLabel.setText("❌ Fields cannot be empty");
                return;
            }

            if (!userExists(receiver)) {
                statusLabel.setText("❌ Receiver does not exist");
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amtStr);
            } catch (NumberFormatException ex) {
                statusLabel.setText("❌ Invalid amount");
                return;
            }

            double senderBalance = fetchBalance(username);
            if (amount <= 0 || amount > senderBalance) {
                statusLabel.setText("❌ Invalid or insufficient balance");
                return;
            }

            try (Connection con = DriverManager.getConnection(url, user, password)) {
                con.setAutoCommit(false);

                try (PreparedStatement pst = con.prepareStatement("UPDATE users SET balance=? WHERE username=?")) {
                    pst.setDouble(1, senderBalance - amount);
                    pst.setString(2, username);
                    pst.executeUpdate();
                }

                try (PreparedStatement pst = con.prepareStatement(
                        "INSERT INTO transactions(username, description, type, amount, balance, balance_after, note, date) " +
                                "VALUES(?,?,?,?,?,?,?,NOW())")) {
                    pst.setString(1, username);
                    pst.setString(2, "Transferred to " + receiver);
                    pst.setString(3, "Transfer");
                    pst.setDouble(4, amount);
                    pst.setDouble(5, senderBalance);
                    pst.setDouble(6, senderBalance - amount);
                    pst.setString(7, note);
                    pst.executeUpdate();
                }

                double receiverBalance = fetchBalance(receiver);
                try (PreparedStatement pst = con.prepareStatement("UPDATE users SET balance=? WHERE username=?")) {
                    pst.setDouble(1, receiverBalance + amount);
                    pst.setString(2, receiver);
                    pst.executeUpdate();
                }

                try (PreparedStatement pst = con.prepareStatement(
                        "INSERT INTO transactions(username, description, type, amount, balance, balance_after, note, date) " +
                                "VALUES(?,?,?,?,?,?,?,NOW())")) {
                    pst.setString(1, receiver);
                    pst.setString(2, "Received from " + username);
                    pst.setString(3, "Transfer");
                    pst.setDouble(4, amount);
                    pst.setDouble(5, receiverBalance);
                    pst.setDouble(6, receiverBalance + amount);
                    pst.setString(7, note);
                    pst.executeUpdate();
                }

                try (PreparedStatement pst = con.prepareStatement(
                        "UPDATE transaction_summary SET total_transfers = total_transfers + ? WHERE id = 1")) {
                    pst.setDouble(1, amount);
                    pst.executeUpdate();
                }

                con.commit();
                statusLabel.setText("Transfer Successful");
                balanceLabel.setText("Current Balance: ₹" + (senderBalance - amount));
                receiverTextField.setText("");
                amountTextField.setText("");
                noteTextField.setText("");

            } catch (Exception ex) {
                statusLabel.setText("❌ Transfer failed: " + ex.getMessage());
            }
        });

        // Frame settings
        setTitle("VaultEdge - Transfer Funds");
        setSize(800, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    // Checks user
    private boolean userExists(String username) {
        String url = EnvLoader.get("DB_URL");
        String user = EnvLoader.get("DB_USER");
        String password = EnvLoader.get("DB_PASSWORD");

        try (Connection con = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT username FROM users WHERE username=?";
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setString(1, username);
                ResultSet rs = pst.executeQuery();
                return rs.next();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            return false;
        }
    }

    // Fetches balance
    private double fetchBalance(String username) {
        String url = EnvLoader.get("DB_URL");
        String user = EnvLoader.get("DB_USER");
        String password = EnvLoader.get("DB_PASSWORD");

        double balance = 0.0;
        try (Connection con = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT balance FROM users WHERE username=?";
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

    public static void main(String[] args) {
        new TransferPage("User");
    }
}
