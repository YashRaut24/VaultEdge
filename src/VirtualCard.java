import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

class VirtualCard extends JFrame {

    // Database credentials (declared at class level for logging method)
    String url = EnvLoader.get("DB_URL");
    String user = EnvLoader.get("DB_USER");
    String password = EnvLoader.get("DB_PASSWORD");

    // ============================================
    // HELPER METHOD: Log activities to database
    // ============================================
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

    // Create TextFields
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

    // Generates Debit/Credit number
    private String generateCardNumber(String cardType) {
        Random randomNumber = new Random();
        StringBuilder number = new StringBuilder();

        if (cardType.equals("DEBIT")) {
            number.append("4532");
        } else {
            number.append("5412");
        }

        for (int i = 0; i < 12; i++) {
            number.append(randomNumber.nextInt(10));
        }

        String cardNumber = number.toString();
        return cardNumber.substring(0, 4) + " " + cardNumber.substring(4, 8) + " " + cardNumber.substring(8, 12) + " " + cardNumber.substring(12, 16);
    }

    // Generate 3-digit CVV
    private String generateCVV() {
        Random randomNumber = new Random();
        return String.format("%03d", randomNumber.nextInt(1000));
    }

    // Generate expiry date
    private String generateExpiryDate() {
        LocalDate futureDate = LocalDate.now().plusYears(5);
        return futureDate.format(DateTimeFormatter.ofPattern("MM/yy"));
    }

    // Calculate credit limit
    private double calculateCreditLimit(double accountBalance) {
        double limit = accountBalance * 3;
        return Math.max(limit, 10000);
    }

    VirtualCard(String username) {

        // Main panel
        JPanel virtualCardPanel = new JPanel(null);
        virtualCardPanel.setBackground(new Color(8, 20, 30));
        setContentPane(virtualCardPanel);

        // Title label
        JLabel titleLabel = new JLabel("Virtual Card Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 230, 255));
        titleLabel.setBounds(0, 25, 800, 40);
        virtualCardPanel.add(titleLabel);

        // Card panel
        JPanel cardDisplayPanel = new JPanel(null);
        cardDisplayPanel.setBackground(new Color(0, 100, 150));
        cardDisplayPanel.setBorder(BorderFactory.createLineBorder(new Color(0, 230, 255), 2));
        cardDisplayPanel.setBounds(150, 90, 500, 220);
        virtualCardPanel.add(cardDisplayPanel);

        // Bank name label
        JLabel bankNameLabel = new JLabel("VaultEdge Bank");
        bankNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        bankNameLabel.setForeground(Color.WHITE);
        bankNameLabel.setBounds(20, 20, 200, 30);
        cardDisplayPanel.add(bankNameLabel);

        // Card type label
        JLabel cardTypeLabel = new JLabel("DEBIT CARD");
        cardTypeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cardTypeLabel.setForeground(Color.WHITE);
        cardTypeLabel.setBounds(20, 50, 150, 20);
        cardDisplayPanel.add(cardTypeLabel);

        // Card number label
        JLabel cardNumberLabel = new JLabel("**** **** **** ****");
        cardNumberLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        cardNumberLabel.setForeground(Color.WHITE);
        cardNumberLabel.setBounds(20, 100, 460, 30);
        cardDisplayPanel.add(cardNumberLabel);

        // Card holder label
        JLabel cardHolderLabel = new JLabel("CARD HOLDER NAME");
        cardHolderLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cardHolderLabel.setForeground(Color.WHITE);
        cardHolderLabel.setBounds(20, 150, 300, 25);
        cardDisplayPanel.add(cardHolderLabel);

        // Expiry date label
        JLabel expiryDateLabel = new JLabel("VALID: **/**");
        expiryDateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        expiryDateLabel.setForeground(Color.WHITE);
        expiryDateLabel.setBounds(20, 180, 100, 20);
        cardDisplayPanel.add(expiryDateLabel);

        // CVV label
        JLabel cvvLabel = new JLabel("CVV: ***");
        cvvLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cvvLabel.setForeground(Color.WHITE);
        cvvLabel.setBounds(400, 180, 80, 20);
        cardDisplayPanel.add(cvvLabel);

        // Card holder name label
        createLabel("Enter Your Name:", 100, 340, 200, 25, virtualCardPanel, 14);

        // Name TextField
        JTextField nameTextField = createTextField(280, 340, 280, 35, virtualCardPanel);

        // Card type label
        createLabel("Card Type:", 100, 385, 200, 25, virtualCardPanel, 14);

        // Debit card RadioButton
        JRadioButton debitCardRadioButton = new JRadioButton("Debit Card");
        debitCardRadioButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        debitCardRadioButton.setForeground(new Color(200, 240, 255));
        debitCardRadioButton.setBackground(new Color(8, 20, 30));
        debitCardRadioButton.setBounds(280, 385, 120, 25);
        debitCardRadioButton.setSelected(true);
        virtualCardPanel.add(debitCardRadioButton);

        // Credit card RadioButton
        JRadioButton creditCardRadioButton = new JRadioButton("Credit Card");
        creditCardRadioButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        creditCardRadioButton.setForeground(new Color(200, 240, 255));
        creditCardRadioButton.setBackground(new Color(8, 20, 30));
        creditCardRadioButton.setBounds(420, 385, 120, 25);
        virtualCardPanel.add(creditCardRadioButton);

        // Card type selection at a time group
        ButtonGroup cardTypeGroup = new ButtonGroup();
        cardTypeGroup.add(debitCardRadioButton);
        cardTypeGroup.add(creditCardRadioButton);

        // Credit info label
        JLabel creditInfoLabel = createLabel("", 100, 420, 600, 25, virtualCardPanel, 13);
        creditInfoLabel.setVisible(false);

        // Status label
        JLabel statusLabel = createLabel("", 100, 455, 600, 25, virtualCardPanel, 14);

        // Generate card button
        JButton generateCardButton = createButton("Generate Card", 180, 500, 150, 40, virtualCardPanel);

        // Use card button
        JButton useCardButton = createButton("Use Card", 350, 500, 120, 40, virtualCardPanel);
        useCardButton.setEnabled(false);

        // Pay bill button (only for credit cards)
        JButton payBillButton = createButton("Pay Bill", 490, 500, 110, 40, virtualCardPanel);
        payBillButton.setEnabled(false);
        payBillButton.setVisible(false);

        // Block card button
        JButton blockCardButton = createButton("Block", 620, 500, 100, 40, virtualCardPanel);
        blockCardButton.setEnabled(false);

        // Back button
        JButton backButton = createButton("Back", 650, 30, 100, 40, virtualCardPanel);

        backButton.addActionListener(e -> {
            new HomePage(username);
            dispose();
        });

        debitCardRadioButton.addActionListener(e -> {
            cardTypeLabel.setText("DEBIT CARD");
            cardDisplayPanel.setBackground(new Color(0, 100, 150));
            creditInfoLabel.setVisible(false);
        });

        creditCardRadioButton.addActionListener(e -> {
            cardTypeLabel.setText("CREDIT CARD");
            cardDisplayPanel.setBackground(new Color(150, 80, 0));

            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                String balanceQuery = "SELECT balance FROM users WHERE username=?";
                try(PreparedStatement pst = conn.prepareStatement(balanceQuery);){
                    pst.setString(1, username);
                    ResultSet rs = pst.executeQuery();

                    if (rs.next()) {
                        double balance = rs.getDouble("balance");
                        double creditLimit = calculateCreditLimit(balance);
                        creditInfoLabel.setText("Your Credit Limit will be: ₹" + String.format("%.2f", creditLimit) + " (Pay 2% monthly interest on used amount)");
                        creditInfoLabel.setVisible(true);
                    }
                }

            } catch (Exception ex) {
                creditInfoLabel.setText("Credit card with borrowing facility");
                creditInfoLabel.setVisible(true);
            }
        });

        // ✅ LOG: User accessed Virtual Card page
        logActivity("View Virtual Card", username, "System",
                "User accessed virtual card management page");

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String checkCardQuery = "SELECT card_number, card_holder_name, expiry_date, cvv, card_type, credit_limit, credit_used, status FROM virtual_cards WHERE username=?";
            try(PreparedStatement pst = conn.prepareStatement(checkCardQuery);){
                pst.setString(1, username);
                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    String cardStatus = rs.getString("status");

                    if (cardStatus.equals("active")) {
                        String existingCardType = rs.getString("card_type");

                        cardNumberLabel.setText(rs.getString("card_number"));
                        cardHolderLabel.setText(rs.getString("card_holder_name"));
                        expiryDateLabel.setText("VALID: " + rs.getString("expiry_date"));
                        cvvLabel.setText("CVV: " + rs.getString("cvv"));
                        cardTypeLabel.setText(existingCardType + " CARD");

                        if (existingCardType.equals("CREDIT")) {
                            cardDisplayPanel.setBackground(new Color(150, 80, 0));
                            creditCardRadioButton.setSelected(true);

                            double creditLimit = rs.getDouble("credit_limit");
                            double creditUsed = rs.getDouble("credit_used");
                            double available = creditLimit - creditUsed;

                            creditInfoLabel.setText("Credit Limit: ₹" + String.format("%.2f", creditLimit) +
                                    " | Used: ₹" + String.format("%.2f", creditUsed) +
                                    " | Available: ₹" + String.format("%.2f", available));
                            creditInfoLabel.setVisible(true);

                            if (creditUsed > 0) {
                                payBillButton.setEnabled(true);
                                payBillButton.setVisible(true);
                            }
                        } else {
                            cardDisplayPanel.setBackground(new Color(0, 100, 150));
                            debitCardRadioButton.setSelected(true);
                        }

                        statusLabel.setText("Your " + existingCardType.toLowerCase() + " card is active!");
                        statusLabel.setForeground(new Color(100, 255, 100));

                        generateCardButton.setEnabled(false);
                        useCardButton.setEnabled(true);
                        blockCardButton.setEnabled(true);
                    } else {
                        statusLabel.setText("Your previous card was blocked. Create a new card.");
                        statusLabel.setForeground(new Color(255, 200, 100));
                    }
                } else {
                    statusLabel.setText("You don't have a card yet. Create one now!");
                    statusLabel.setForeground(new Color(255, 200, 100));
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }

        generateCardButton.addActionListener(e -> {
            String holderName = nameTextField.getText().trim().toUpperCase();
            String selectedCardType = debitCardRadioButton.isSelected() ? "DEBIT" : "CREDIT";

            if (holderName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your name");
                return;
            }

            try (Connection conn = DriverManager.getConnection(url, user, password)) {

                String checkQuery = "SELECT COUNT(*) FROM virtual_cards WHERE username=? AND status='active'";
                try(PreparedStatement pst = conn.prepareStatement(checkQuery);){
                    pst.setString(1, username);
                    ResultSet rs = pst.executeQuery();

                    if (rs.next() && rs.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(this, "You already have an active card!");
                        return;
                    }

                    String newCardNumber = generateCardNumber(selectedCardType);
                    String newCVV = generateCVV();
                    String newExpiry = generateExpiryDate();
                    double creditLimit = 0;

                    if (selectedCardType.equals("CREDIT")) {
                        String balanceQuery = "SELECT balance FROM users WHERE username=?";
                        PreparedStatement balanceStmt = conn.prepareStatement(balanceQuery);
                        balanceStmt.setString(1, username);
                        ResultSet balanceRs = balanceStmt.executeQuery();
                        if (balanceRs.next()) {
                            creditLimit = calculateCreditLimit(balanceRs.getDouble("balance"));
                        }
                    }

                    String insertQuery = "INSERT INTO virtual_cards (username, card_number, card_holder_name, expiry_date, cvv, card_type, credit_limit, credit_used, status, created_date) VALUES (?, ?, ?, ?, ?, ?, ?, 0, 'active', CURRENT_DATE)";
                    PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                    insertStmt.setString(1, username);
                    insertStmt.setString(2, newCardNumber);
                    insertStmt.setString(3, holderName);
                    insertStmt.setString(4, newExpiry);
                    insertStmt.setString(5, newCVV);
                    insertStmt.setString(6, selectedCardType);
                    insertStmt.setDouble(7, creditLimit);
                    insertStmt.executeUpdate();

                    // ✅ LOG: Card Created Successfully
                    String logDetails = selectedCardType + " card created. " +
                            "Card Number: " + newCardNumber.replaceAll("\\d(?=\\d{4})", "*") +
                            (selectedCardType.equals("CREDIT") ? ", Credit Limit: ₹" + String.format("%.2f", creditLimit) : "");
                    logActivity("Create Virtual Card", username, "System", logDetails);

                    cardNumberLabel.setText(newCardNumber);
                    cardHolderLabel.setText(holderName);
                    expiryDateLabel.setText("VALID: " + newExpiry);
                    cvvLabel.setText("CVV: " + newCVV);
                    cardTypeLabel.setText(selectedCardType + " CARD");

                    if (selectedCardType.equals("CREDIT")) {
                        cardDisplayPanel.setBackground(new Color(150, 80, 0));
                        creditInfoLabel.setText("Credit Limit: ₹" + String.format("%.2f", creditLimit) + " | Used: ₹0.00 | Available: ₹" + String.format("%.2f", creditLimit));
                        creditInfoLabel.setVisible(true);
                    } else {
                        cardDisplayPanel.setBackground(new Color(0, 100, 150));
                        creditInfoLabel.setVisible(false);
                    }

                    statusLabel.setText(selectedCardType + " card created successfully!");
                    statusLabel.setForeground(new Color(100, 255, 100));

                    generateCardButton.setEnabled(false);
                    useCardButton.setEnabled(true);
                    blockCardButton.setEnabled(true);

                    JOptionPane.showMessageDialog(this, selectedCardType + " card created successfully!");

                }
            } catch (Exception ex) {
                // ✅ LOG: Card Creation Failed
                logActivity("Card Creation Failed", username, "System",
                        "Failed to create " + selectedCardType + " card. Error: " + ex.getMessage());

                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        useCardButton.addActionListener(e -> {
            // ✅ LOG: User clicked Use Card
            logActivity("Use Virtual Card", username, "System",
                    "User accessed card payment page");

            new CardPayment(username);
        });

        payBillButton.addActionListener(e -> {
            // ✅ LOG: User clicked Pay Bill
            logActivity("Credit Bill Payment", username, "System",
                    "User accessed credit bill payment page");

            new CreditBillPayment(username);
        });

        blockCardButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to block this card?\nThis action cannot be undone.",
                    "Block Card",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DriverManager.getConnection(url, user, password)) {
                    String blockQuery = "UPDATE virtual_cards SET status='blocked' WHERE username=? AND status='active'";
                    try(PreparedStatement pst = conn.prepareStatement(blockQuery)){
                        pst.setString(1, username);
                        pst.executeUpdate();

                        // ✅ LOG: Card Blocked Successfully
                        logActivity("Block Virtual Card", username, "System",
                                "User blocked their virtual card");

                        cardNumberLabel.setText("**** **** **** ****");
                        cardHolderLabel.setText("CARD HOLDER NAME");
                        expiryDateLabel.setText("VALID: **/**");
                        cvvLabel.setText("CVV: ***");
                        cardTypeLabel.setText("DEBIT CARD");
                        cardDisplayPanel.setBackground(new Color(0, 100, 150));
                        creditInfoLabel.setVisible(false);

                        statusLabel.setText("Card has been blocked successfully.");
                        statusLabel.setForeground(new Color(255, 100, 100));

                        generateCardButton.setEnabled(true);
                        useCardButton.setEnabled(false);
                        payBillButton.setEnabled(false);
                        payBillButton.setVisible(false);
                        blockCardButton.setEnabled(false);

                        JOptionPane.showMessageDialog(this, "Card blocked successfully");

                    }
                } catch (Exception ex) {
                    // ✅ LOG: Card Block Failed
                    logActivity("Card Block Failed", username, "System",
                            "Failed to block card. Error: " + ex.getMessage());

                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            }
        });

        // Frame settings
        setTitle("VaultEdge - Virtual Card");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        VirtualCard object = new VirtualCard("TestUser");
    }
}