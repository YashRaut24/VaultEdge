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
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createLineBorder(borderColor, 2));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBounds(x, y, width, height);
        if (bgColor != null) {
            button.setBackground(bgColor);
            button.setOpaque(true);
        }
        panel.add(button);
        return button;
    }

    // Styled label
    private JLabel createLabel(String text, int x, int y, int width, int height, JPanel panel, int fontSize, boolean bold) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
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
        createLabel("Deposit Money", 0, 30, 800, 50, backgroundPanel, 28, true);

        // Amount label
        createLabel("Enter Amount:", 250, 120, 300, 25, backgroundPanel, 16, false);

        // Amount input
        JTextField amountField = createTextField(250, 160, 300, 35, backgroundPanel);

        // Buttons
        JButton depositButton = createButton("Deposit", 300, 220, 200, 40, backgroundPanel,
                new Color(0, 230, 255), Color.WHITE, new Color(0, 153, 76));

        JButton backButton = createButton("Back", 300, 280, 200, 40, backgroundPanel,
                new Color(0, 230, 255), Color.WHITE, new Color(255, 51, 51));

        backButton.addActionListener(e -> {
            new HomePage(username);
            dispose();
        });

        depositButton.addActionListener(e -> {
            double balance = 0.0;
            String url = "jdbc:mysql://localhost:3306/3dec";

            // Get current balance
            try (Connection con = DriverManager.getConnection(url, "root", "your_password")) {
                String sql = "SELECT balance FROM users WHERE username = ?";
                try (PreparedStatement pst = con.prepareStatement(sql)) {
                    pst.setString(1, username);
                    ResultSet rs = pst.executeQuery();
                    if (rs.next()) {
                        balance = rs.getDouble("balance");
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }

            String input = amountField.getText();
            if (input.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter amount");
            } else {
                double amount = Double.parseDouble(input);
                double total = balance + amount;
                try (Connection con = DriverManager.getConnection(url, "root", "your_password")) {
                    String sql = "UPDATE users SET balance = ? WHERE username = ?";
                    try (PreparedStatement pst = con.prepareStatement(sql)) {
                        pst.setDouble(1, total);
                        pst.setString(2, username);
                        pst.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Successfully Deposited");
                        amountField.setText("");
                        updatePassbook(username, "deposit", amount, total);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            }
        });

        // Frame settings
        setTitle("VaultEdge - Deposit Money");
        setSize(800, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    void updatePassbook(String username, String desc, double amount, double balance) {
        String url = "jdbc:mysql://localhost:3306/3dec";
        try (Connection con = DriverManager.getConnection(url, "root", "your_password")) {
            String sql = "INSERT INTO transactions(username,description,amount,balance) VALUES(?,?,?,?)";
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

    public static void main(String[] args) {
        new DepositPage("Yash24");
    }
}
