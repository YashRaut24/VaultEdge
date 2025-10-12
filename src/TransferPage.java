import javax.swing.*;
import java.awt.*;
import java.sql.*;

class TransferPage extends JFrame {

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

    // Styled button
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
        // Background panel
        JPanel backgroundPanel = new JPanel(null);
        backgroundPanel.setBackground(new Color(8, 20, 30));
        setContentPane(backgroundPanel);

        // Title
        JLabel title = new JLabel("Transfer Funds", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(0, 230, 255));
        title.setBounds(0, 20, 800, 40);
        backgroundPanel.add(title);

        // Current balance label
        double currentBalance = fetchBalance(username);
        JLabel balanceLabel = createLabel("Current Balance: ₹" + currentBalance, 250, 70, 300, 25, backgroundPanel);

        // Labels and fields
        createLabel("Receiver (Username):", 200, 120, 180, 30, backgroundPanel);
        JTextField receiverField = createTextField(400, 120, 200, 30, backgroundPanel);

        createLabel("Amount (₹):", 200, 170, 180, 30, backgroundPanel);
        JTextField amountField = createTextField(400, 170, 200, 30, backgroundPanel);

        createLabel("Note (optional):", 200, 220, 180, 30, backgroundPanel);
        JTextField noteField = createTextField(400, 220, 200, 30, backgroundPanel);

        // Status label
        JLabel statusLabel = createLabel("", 200, 280, 400, 30, backgroundPanel);

        // Buttons
        JButton transferBtn = createButton("Transfer", 250, 320, 120, 42, backgroundPanel);
        JButton backBtn = createButton("Back", 400, 320, 120, 42, backgroundPanel);

        // Button actions
        backBtn.addActionListener(a -> {
            new HomePage(username);
            dispose();
        });

        transferBtn.addActionListener(a -> {
            String receiver = receiverField.getText();
            String amountStr = amountField.getText();
            String note = noteField.getText();

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

            // Deduct from sender
            updateBalance(username, senderBalance - amount);
            updatePassbook(username, "Transferred to " + receiver + (note.isEmpty() ? "" : " (" + note + ")"), -amount, senderBalance - amount);

            // Add to receiver
            double receiverBalance = fetchBalance(receiver);
            updateBalance(receiver, receiverBalance + amount);
            updatePassbook(receiver, "Received from " + username + (note.isEmpty() ? "" : " (" + note + ")"), amount, receiverBalance + amount);

            statusLabel.setText("✅ Transfer Successful");
            receiverField.setText("");
            amountField.setText("");
            noteField.setText("");

            // Update current balance display
            balanceLabel.setText("Current Balance: ₹" + fetchBalance(username));
        });

        // Frame settings
        setTitle("VaultEdge - Transfer Funds");
        setSize(800, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private boolean userExists(String username) {
        String url = "jdbc:mysql://localhost:3306/3dec";
        try (Connection con = DriverManager.getConnection(url, "root", "your_password")) {
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

    private void updatePassbook(String username, String desc, double amount, double balance) {
        String url = "jdbc:mysql://localhost:3306/3dec";
        try (Connection con = DriverManager.getConnection(url, "root", "your_password")) {
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

    private void updateBalance(String username, double balance) {
        String url = "jdbc:mysql://localhost:3306/3dec";
        try (Connection con = DriverManager.getConnection(url, "root", "your_password")) {
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

    private double fetchBalance(String username) {
        double balance = 0.0;
        String url = "jdbc:mysql://localhost:3306/3dec";
        try (Connection con = DriverManager.getConnection(url, "root", "your_password")) {
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
