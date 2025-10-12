import javax.swing.*;
import java.awt.*;
import java.sql.*;

class DepositPage extends JFrame {

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
    private JButton createButton(String text, int x, int y, int width, int height, JPanel panel, Color borderColor, Color textColor, Color bgColor) {
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

    // Styled label
    private JLabel createLabel(String text, int x, int y, int width, int height, JPanel panel, int fontSize, boolean bold) {
        JLabel label = new JLabel(text, SwingConstants.LEFT);
        label.setFont(new Font("Segoe UI", bold ? Font.BOLD : Font.PLAIN, fontSize));
        label.setForeground(new Color(0, 230, 255));
        label.setBounds(x, y, width, height);
        panel.add(label);
        return label;
    }

    DepositPage(String username) {
        JPanel backgroundPanel = new JPanel(null);
        backgroundPanel.setBackground(new Color(8, 20, 30));
        setContentPane(backgroundPanel);

        // Title
        JLabel title = createLabel("💰 Deposit Money", 0, 40, 800, 40, backgroundPanel, 28, true);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        // Current balance label
        JLabel balanceLabel = createLabel("Current Balance: ₹0.00", 250, 110, 300, 25, backgroundPanel, 16, false);

        // Get balance from DB
        double balance = 0.0;
        String url = "jdbc:mysql://localhost:3306/3dec";
        try (Connection con = DriverManager.getConnection(url, "root", "your_password")) {
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

        // Enter amount
        createLabel("Enter Amount:", 250, 160, 200, 25, backgroundPanel, 16, false);
        JTextField amountField = createTextField(250, 190, 300, 35, backgroundPanel);

        // Payment method dropdown
        createLabel("Payment Method:", 250, 240, 200, 25, backgroundPanel, 16, false);
        String[] methods = {
                "VaultEdge Wallet",
                "Linked Bank Account",
                "UPI Transfer",
                "Credit/Debit Card",
                "Net Banking"
        };
        JComboBox<String> paymentMethod = new JComboBox<>(methods);
        paymentMethod.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        paymentMethod.setBackground(new Color(15, 30, 40));
        paymentMethod.setForeground(new Color(220, 235, 245));
        paymentMethod.setBounds(250, 270, 300, 35);
        backgroundPanel.add(paymentMethod);

        // Note field
        createLabel("Note (optional):", 250, 320, 200, 25, backgroundPanel, 16, false);
        JTextField noteField = createTextField(250, 350, 300, 35, backgroundPanel);

        // Buttons
        JButton depositButton = createButton("Deposit", 250, 410, 140, 40, backgroundPanel,
                new Color(0, 230, 255), Color.WHITE, new Color(0, 153, 76));

        JButton cancelButton = createButton("Cancel", 410, 410, 140, 40, backgroundPanel,
                new Color(0, 230, 255), Color.WHITE, new Color(255, 51, 51));

        // Status label
        JLabel statusLabel = createLabel("", 250, 470, 400, 25, backgroundPanel, 16, false);

        // Cancel button action
        cancelButton.addActionListener(e -> {
            new HomePage(username);
            dispose();
        });

        // Deposit logic
        double finalBalance = balance;
        depositButton.addActionListener(e -> {
            String input = amountField.getText();
            String method = (String) paymentMethod.getSelectedItem();
            String note = noteField.getText();

            if (input.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter amount");
                return;
            }

            try {
                double amount = Double.parseDouble(input);
                double total = finalBalance + amount;

                try (Connection con = DriverManager.getConnection(url, "root", "your_password")) {
                    String sql = "UPDATE users SET balance = ? WHERE username = ?";
                    try (PreparedStatement pst = con.prepareStatement(sql)) {
                        pst.setDouble(1, total);
                        pst.setString(2, username);
                        pst.executeUpdate();
                    }

                    updatePassbook(username, "Deposit via " + method, amount, total, note);
                    statusLabel.setText("Deposit Successful via " + method);
                    balanceLabel.setText("Current Balance: ₹" + total);
                    amountField.setText("");
                    noteField.setText("");

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

    // Record transaction in passbook
    void updatePassbook(String username, String desc, double amount, double balance, String note) {
        String url = "jdbc:mysql://localhost:3306/3dec";
        try (Connection con = DriverManager.getConnection(url, "root", "your_password")) {
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
        new DepositPage("Yash24");
    }
}
