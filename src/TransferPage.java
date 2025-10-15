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

    // Create TextField
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

    TransferPage(String username) {

        // DB Credentials
        String url = EnvLoader.get("DB_URL");
        String user = EnvLoader.get("DB_USER");
        String password = EnvLoader.get("DB_PASSWORD");

        // Transfer page panel
        JPanel transferPagePanel = new JPanel(null);
        transferPagePanel.setBackground(new Color(8, 20, 30));
        setContentPane(transferPagePanel);

        // Title label
        JLabel titleLabel = new JLabel("Transfer Funds", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 230, 255));
        titleLabel.setBounds(0, 20, 800, 40);
        transferPagePanel.add(titleLabel);

        double currentBalance = fetchBalance(username);
        JLabel balanceLabel = createLabel("Current Balance: ₹" + currentBalance, 250, 70, 300, 25, transferPagePanel);

        // Receiver label
        createLabel("Receiver (Username):", 200, 120, 180, 30, transferPagePanel);

        // Receiver TextField
        JTextField receiverTextField = createTextField(400, 120, 200, 30, transferPagePanel);

        // Amount label
        createLabel("Amount (₹):", 200, 170, 180, 30, transferPagePanel);

        // Amount TextField
        JTextField amountField = createTextField(400, 170, 200, 30, transferPagePanel);

        // Note label
        createLabel("Note (optional):", 200, 220, 180, 30, transferPagePanel);

        // Note TextField
        JTextField noteTextField = createTextField(400, 220, 200, 30, transferPagePanel);

        // Status label
        JLabel statusLabel = createLabel("", 200, 280, 400, 30, transferPagePanel);

        // Transfer button
        JButton backButton = createButton("Back", 400, 320, 120, 42, transferPagePanel);

        // Button actions
        backButton.addActionListener(a -> {
            new HomePage(username);
            dispose();
        });

        JButton transferButton = createButton("Transfer", 250, 320, 120, 42, transferPagePanel);

        transferButton.addActionListener(a -> {
            String receiver = receiverTextField.getText();
            String amountStr = amountField.getText();
            String note = noteTextField.getText();

            if (receiver.isEmpty() || amountStr.isEmpty()) {
                statusLabel.setText("❌ Fields cannot be empty");
                return;
            }

            if (!userExists(receiver)) {
                statusLabel.setText("❌ Receiver does not exist");
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                statusLabel.setText("❌ Invalid amount");
                return;
            }

            double senderBalance = fetchBalance(username);
            if (amount > senderBalance) {
                statusLabel.setText("❌ Insufficient balance");
                return;
            }

            try (Connection con = DriverManager.getConnection(url, user, password)) {
                con.setAutoCommit(false);

                // Deduct sender
                try (PreparedStatement pst = con.prepareStatement("UPDATE users SET balance=? WHERE username=?")) {
                    pst.setDouble(1, senderBalance - amount);
                    pst.setString(2, username);
                    pst.executeUpdate();
                }

                try (PreparedStatement pst = con.prepareStatement("INSERT INTO transactions(username, description, amount, balance) VALUES(?,?,?,?)")) {
                    pst.setString(1, username);
                    pst.setString(2, "Transferred to " + receiver + (note.isEmpty() ? "" : " (" + note + ")"));
                    pst.setDouble(3, -amount);
                    pst.setDouble(4, senderBalance - amount);
                    pst.executeUpdate();
                }

                double receiverBalance = fetchBalance(receiver);
                try (PreparedStatement pst = con.prepareStatement("UPDATE users SET balance=? WHERE username=?")) {
                    pst.setDouble(1, receiverBalance + amount);
                    pst.setString(2, receiver);
                    pst.executeUpdate();
                }

                try (PreparedStatement pst = con.prepareStatement("INSERT INTO transactions(username, description, amount, balance) VALUES(?,?,?,?)")) {
                    pst.setString(1, receiver);
                    pst.setString(2, "Received from " + username + (note.isEmpty() ? "" : " (" + note + ")"));
                    pst.setDouble(3, amount);
                    pst.setDouble(4, receiverBalance + amount);
                    pst.executeUpdate();
                }

                con.commit(); // Commit transaction
                statusLabel.setText("✅ Transfer Successful");
                receiverTextField.setText("");
                amountField.setText("");
                noteTextField.setText("");
                balanceLabel.setText("Current Balance: ₹" + fetchBalance(username));

            } catch (Exception e) {
                statusLabel.setText("❌ Transfer failed: " + e.getMessage());
            }
        });


        // Frame settings
        setTitle("VaultEdge - Transfer Funds");
        setSize(800, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    // Checks if user exists
    private boolean userExists(String username) {
        // DB Credentials
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

    // Updates passbook
    private void updatePassbook(String username, String desc, double amount, double balance) {
        // DB Credentials
        String url = EnvLoader.get("DB_URL");
        String user = EnvLoader.get("DB_USER");
        String password = EnvLoader.get("DB_PASSWORD");

        try (Connection con = DriverManager.getConnection(url, user, password)) {
            String sql = "INSERT INTO transactions(username, description, amount, balance) VALUES(?,?,?,?)";
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setString(1, username);
                pst.setString(2, desc);
                pst.setDouble(3, amount);
                pst.setDouble(4, balance);
                pst.executeUpdate();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    // Updates balance
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

    // Fetches balances
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

    public static void main(String[] args) {
        new TransferPage("User");
    }
}
