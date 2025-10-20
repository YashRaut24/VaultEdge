import javax.swing.*;
import java.awt.*;
import java.sql.*;

class CreditBillPayment extends JFrame {

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

    CreditBillPayment(String username) {

        // Database credentials
        String url = EnvLoader.get("DB_URL");
        String user = EnvLoader.get("DB_USER");
        String password = EnvLoader.get("DB_PASSWORD");

        // Credit bill payment panel
        JPanel creditBillPaymentPanel = new JPanel(null);
        creditBillPaymentPanel.setBackground(new Color(8, 20, 30));
        setContentPane(creditBillPaymentPanel);

        // Title Label
        JLabel titleLabel = new JLabel("Pay Credit Card Bill", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 230, 255));
        titleLabel.setBounds(0, 30, 700, 40);
        creditBillPaymentPanel.add(titleLabel);

        // Credit limit label
        JLabel creditLimitLabel = createLabel("", 100, 100, 500, 25, creditBillPaymentPanel, 14);

        // Credit used label
        JLabel creditUsedLabel = createLabel("", 100, 130, 500, 25, creditBillPaymentPanel, 14);

        // Interest label
        JLabel interestLabel = createLabel("", 100, 160, 500, 25, creditBillPaymentPanel, 14);

        // Total duel label
        JLabel totalDueLabel = createLabel("", 100, 190, 500, 25, creditBillPaymentPanel, 16);

        // Account balance label
        JLabel accountBalanceLabel = createLabel("", 100, 230, 500, 25, creditBillPaymentPanel, 14);

        // Payment amount label
        createLabel("Payment Amount:", 100, 280, 200, 25, creditBillPaymentPanel, 16);

        // Payment amount TextField
        JTextField paymentAmountTextField = createTextField(100, 310, 200, 35, creditBillPaymentPanel);

        // Full payment button
        JButton payFullButton = createButton("Pay Full", 320, 310, 120, 35, creditBillPaymentPanel);

        // Minimum payment button
        JButton payMinimumButton = createButton("Minimum (10%)", 460, 310, 150, 35, creditBillPaymentPanel);

        // Pay bill button
        JButton payBillButton = createButton("Pay Bill", 220, 380, 200, 45, creditBillPaymentPanel);

        // Back button
        JButton backButton = createButton("Back", 250, 450, 150, 40, creditBillPaymentPanel);
        backButton.addActionListener(e -> dispose());

        try (Connection conn = DriverManager.getConnection(url, user, password)) {

            String cardQuery = "SELECT credit_limit, credit_used FROM virtual_cards WHERE username=? AND status='active' AND card_type='CREDIT'";

            try(PreparedStatement pst1 = conn.prepareStatement(cardQuery)){
                pst1.setString(1, username);
                ResultSet cardResult = pst1.executeQuery();

                if (!cardResult.next()) {
                    JOptionPane.showMessageDialog(this, "No active credit card found!");
                    dispose();
                    return;
                }

                double creditLimit = cardResult.getDouble("credit_limit");
                double creditUsed = cardResult.getDouble("credit_used");

                if (creditUsed == 0) {
                    JOptionPane.showMessageDialog(this, "No outstanding balance! Your credit is clear.");
                    dispose();
                    return;
                }

                double interestAmount = creditUsed * 0.02;
                double totalDue = creditUsed + interestAmount;

                String balanceQuery = "SELECT balance FROM users WHERE username=?";

                try(PreparedStatement pst2 = conn.prepareStatement(balanceQuery)){
                    pst2.setString(1, username);
                    ResultSet balanceResult = pst2.executeQuery();

                    double accountBalance = 0;
                    if (balanceResult.next()) {
                        accountBalance = balanceResult.getDouble("balance");
                    }

                    creditLimitLabel.setText("Credit Limit: ₹" + String.format("%.2f", creditLimit));
                    creditUsedLabel.setText("Credit Used: ₹" + String.format("%.2f", creditUsed));
                    interestLabel.setText("Interest (2% monthly): ₹" + String.format("%.2f", interestAmount));
                    totalDueLabel.setText("Total Amount Due: ₹" + String.format("%.2f", totalDue));
                    totalDueLabel.setForeground(new Color(255, 100, 100));
                    accountBalanceLabel.setText("Your Account Balance: ₹" + String.format("%.2f", accountBalance));

                    double minimumPayment = totalDue * 0.10;

                    double finalAccountBalance = accountBalance;
                    double finalTotalDue = totalDue;
                    double finalCreditUsed = creditUsed;
                    double finalInterestAmount = interestAmount;

                    payFullButton.addActionListener(e -> {
                        paymentAmountTextField.setText(String.format("%.2f", finalTotalDue));
                    });

                    payMinimumButton.addActionListener(e -> {
                        paymentAmountTextField.setText(String.format("%.2f", minimumPayment));
                    });

                    payBillButton.addActionListener(e -> {
                        String paymentText = paymentAmountTextField.getText().trim();

                        if (paymentText.isEmpty()) {
                            JOptionPane.showMessageDialog(this, "Please enter payment amount");
                            return;
                        }

                        double paymentAmount;
                        try {
                            paymentAmount = Double.parseDouble(paymentText);
                            if (paymentAmount <= 0) {
                                JOptionPane.showMessageDialog(this, "Please enter a valid amount");
                                return;
                            }
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(this, "Invalid amount format");
                            return;
                        }

                        if (paymentAmount > finalTotalDue) {
                            JOptionPane.showMessageDialog(this, "Payment amount exceeds total due!");
                            return;
                        }

                        if (finalAccountBalance < paymentAmount) {
                            JOptionPane.showMessageDialog(this, "Insufficient account balance!");
                            return;
                        }

                        try (Connection conn2 = DriverManager.getConnection(url, user, password)) {

                            double interestPayment = Math.min(paymentAmount, finalInterestAmount);
                            double principalPayment = paymentAmount - interestPayment;

                            double newCreditUsed = finalCreditUsed - principalPayment;
                            double newBalance = finalAccountBalance - paymentAmount;

                            String updateCardQuery = "UPDATE virtual_cards SET credit_used=? WHERE username=? AND status='active' AND card_type='CREDIT'";
                            try(PreparedStatement pst3 = conn2.prepareStatement(updateCardQuery)){
                                pst3.setDouble(1, newCreditUsed);
                                pst3.setString(2, username);
                                pst3.executeUpdate();

                                String updateBalanceQuery = "UPDATE users SET balance=? WHERE username=?";
                                try(PreparedStatement pst4 = conn2.prepareStatement(updateBalanceQuery)){
                                    pst4.setDouble(1, newBalance);
                                    pst4.setString(2, username);
                                    pst4.executeUpdate();

                                    String transactionDesc = "Credit card bill payment (Principal: ₹" +
                                            String.format("%.2f", principalPayment) +
                                            ", Interest: ₹" + String.format("%.2f", interestPayment) + ")";

                                    String transactionQuery = "INSERT INTO transactions (username, type, amount, description, balance_after, date) VALUES (?, 'Credit Bill Payment', ?, ?, ?, CURRENT_TIMESTAMP)";
                                    PreparedStatement transactionStmt = conn2.prepareStatement(transactionQuery);
                                    transactionStmt.setString(1, username);
                                    transactionStmt.setDouble(2, paymentAmount);
                                    transactionStmt.setString(3, transactionDesc);
                                    transactionStmt.setDouble(4, newBalance);
                                    transactionStmt.executeUpdate();

                                    String successMessage = "Payment Successful!\n\n" +
                                            "Payment Amount: ₹" + String.format("%.2f", paymentAmount) + "\n" +
                                            "Principal Paid: ₹" + String.format("%.2f", principalPayment) + "\n" +
                                            "Interest Paid: ₹" + String.format("%.2f", interestPayment) + "\n\n" +
                                            "Remaining Credit Balance: ₹" + String.format("%.2f", newCreditUsed) + "\n" +
                                            "Account Balance: ₹" + String.format("%.2f", newBalance);

                                    JOptionPane.showMessageDialog(this, successMessage);
                                    dispose();
                                }
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                        }
                    });
                }
            }
          } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }

        // Frame Settings
        setTitle("VaultEdge - Pay Credit Bill");
        setSize(700, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        CreditBillPayment object = new CreditBillPayment("TestUser");
    }
}