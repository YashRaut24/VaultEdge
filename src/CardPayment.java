import javax.swing.*;
import java.awt.*;
import java.sql.*;

class CardPayment extends JFrame {

    // Database credentials
    String url = EnvLoader.get("DB_URL");
    String user = EnvLoader.get("DB_USER");
    String password = EnvLoader.get("DB_PASSWORD");

    // Updates activity
    private void logActivity(String action, String targetUser, String adminUsername, String remarks) {
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String sql = "INSERT INTO activities (action, target_user, admin, remarks) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, action);
                pst.setString(2, targetUser);
                pst.setString(3, adminUsername);
                pst.setString(4, remarks);
                pst.executeUpdate();
            }
        } catch (SQLException ex) {
            System.err.println("Error logging activity: " + ex.getMessage());
        }
    }

    // Create labels
    private JLabel createLabel(String text, int x, int y, int width, int height, JPanel panel, int fontSize) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
        label.setForeground(new Color(200, 240, 255));
        label.setBounds(x, y, width, height);
        panel.add(label);
        return label;
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

    // Create TextField
    private JTextField createTextField(int x, int y, int width, int height, JPanel panel) {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
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

    CardPayment(String username) {

        // Card payment panel
        JPanel cardPaymentPanel = new JPanel(null);
        cardPaymentPanel.setBackground(new Color(8, 20, 30));
        setContentPane(cardPaymentPanel);

        // Title Label
        JLabel titleLabel = new JLabel("Make Payment", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 230, 255));
        titleLabel.setBounds(0, 30, 700, 40);
        cardPaymentPanel.add(titleLabel);

        // Card info label
        JLabel cardInfoLabel = createLabel("", 100, 100, 500, 25, cardPaymentPanel, 14);

        // Balance label
        JLabel balanceLabel = createLabel("", 100, 130, 500, 25, cardPaymentPanel, 14);

        // Merchant/Shop name label
        createLabel("Merchant/Shop Name:", 100, 180, 200, 25, cardPaymentPanel, 16);

        // Merchant/Shop name TextField
        JTextField merchantTextField = createTextField(100, 210, 400, 35, cardPaymentPanel);

        // Amount label
        createLabel("Payment Amount:", 100, 260, 200, 25, cardPaymentPanel, 16);

        // Amount TextField
        JTextField amountTextField = createTextField(100, 290, 200, 35, cardPaymentPanel);

        // Description label
        createLabel("Description (Optional):", 100, 340, 200, 25, cardPaymentPanel, 16);

        // Description TextField
        JTextField descriptionTextField = createTextField(100, 370, 400, 35, cardPaymentPanel);

        // Make payment button
        JButton makePaymentButton = createButton("Make Payment", 220, 440, 200, 45, cardPaymentPanel);

        // Back button
        JButton backButton = createButton("Back", 250, 510, 150, 40, cardPaymentPanel);
        backButton.addActionListener(e -> dispose());

        logActivity("View Card Payment", username, "System",
                "User accessed card payment page");

        try (Connection conn = DriverManager.getConnection(url, user, password)) {

            String cardQuery = "SELECT card_number FROM virtual_cards WHERE username=? AND status='active'";

            try(PreparedStatement pst1 = conn.prepareStatement(cardQuery);){
                pst1.setString(1, username);
                ResultSet rs = pst1.executeQuery();

                if (rs.next()) {
                    String cardNumber = rs.getString("card_number");
                    String lastFourDigits = cardNumber.substring(cardNumber.length() - 4);
                    cardInfoLabel.setText("Debit Card ending in: " + lastFourDigits);
                } else {
                    JOptionPane.showMessageDialog(this, "No active card found!");
                    dispose();
                    return;
                }

                // Get account balance
                String balanceQuery = "SELECT balance FROM users WHERE username=?";
                PreparedStatement pst2 = conn.prepareStatement(balanceQuery);
                pst2.setString(1, username);
                ResultSet balanceResult = pst2.executeQuery();

                if (balanceResult.next()) {
                    double currentBalance = balanceResult.getDouble("balance");
                    balanceLabel.setText("Available Balance: ₹" + String.format("%.2f", currentBalance));
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }

        makePaymentButton.addActionListener(e -> {
            String merchantName = merchantTextField.getText().trim();
            String amountText = amountTextField.getText().trim();
            String description = descriptionTextField.getText().trim();

            if (merchantName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter merchant/shop name");
                return;
            }

            if (amountText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter payment amount");
                return;
            }

            double paymentAmount;
            try {
                paymentAmount = Double.parseDouble(amountText);
                if (paymentAmount <= 0) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid amount");
                    return;
                }
            } catch (NumberFormatException ex) {
                logActivity("Invalid Card Payment", username, "System",
                        "Invalid amount entered: " + amountText);

                JOptionPane.showMessageDialog(this, "Invalid amount format");
                return;
            }

            try (Connection conn = DriverManager.getConnection(url, user, password)) {

                String balanceQuery = "SELECT balance FROM users WHERE username=?";
                try(PreparedStatement pst2 = conn.prepareStatement(balanceQuery);){
                    pst2.setString(1, username);
                    ResultSet balanceResult = pst2.executeQuery();

                    if (balanceResult.next()) {
                        double currentBalance = balanceResult.getDouble("balance");

                        if (currentBalance < paymentAmount) {
                            logActivity("Card Payment Failed", username, "System",
                                    "Insufficient balance. Required: ₹" + String.format("%.2f", paymentAmount) +
                                            ", Available: ₹" + String.format("%.2f", currentBalance));

                            JOptionPane.showMessageDialog(this,
                                    "Insufficient balance!\nAvailable: ₹" + String.format("%.2f", currentBalance) +
                                            "\nRequired: ₹" + String.format("%.2f", paymentAmount));
                            return;
                        }

                        double newBalance = currentBalance - paymentAmount;

                        String updateBalanceQuery = "UPDATE users SET balance=? WHERE username=?";
                        try(PreparedStatement pst3 = conn.prepareStatement(updateBalanceQuery)){
                            pst3.setDouble(1, newBalance);
                            pst3.setString(2, username);
                            pst3.executeUpdate();

                            String transactionDesc = "Paid to " + merchantName;
                            if (!description.isEmpty()) {
                                transactionDesc += " - " + description;
                            }

                            String transactionQuery = "INSERT INTO transactions (username, type, amount, description, balance_after, date) VALUES (?, 'Debit Card Payment', ?, ?, ?, CURRENT_TIMESTAMP)";
                            try(PreparedStatement pst4 = conn.prepareStatement(transactionQuery)){
                                pst4.setString(1, username);
                                pst4.setDouble(2, paymentAmount);
                                pst4.setString(3, transactionDesc);
                                pst4.setDouble(4, newBalance);
                                pst4.executeUpdate();

                                String logDetails = "Card payment of ₹" + String.format("%.2f", paymentAmount) +
                                        " to " + merchantName + ". New balance: ₹" + String.format("%.2f", newBalance);
                                if (!description.isEmpty()) {
                                    logDetails += " - " + description;
                                }
                                logActivity("Card Payment Success", username, "System", logDetails);

                                String successMessage = "Payment Successful!\n\n" +
                                        "Merchant: " + merchantName + "\n" +
                                        "Amount Paid: ₹" + String.format("%.2f", paymentAmount) + "\n" +
                                        "Previous Balance: ₹" + String.format("%.2f", currentBalance) + "\n" +
                                        "New Balance: ₹" + String.format("%.2f", newBalance);

                                JOptionPane.showMessageDialog(this, successMessage);
                                dispose();
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "User account not found!");
                    }
                }
            } catch (Exception ex) {
                logActivity("Card Payment Error", username, "System",
                        "Payment failed to " + merchantName + ". Error: " + ex.getMessage());

                JOptionPane.showMessageDialog(this, "Payment Error: " + ex.getMessage());
            }
        });

        // Frame settings
        setTitle("VaultEdge - Card Payment");
        setSize(700, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        CardPayment object = new CardPayment("TestUser");
    }
}