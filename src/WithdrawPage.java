import javax.swing.*;
import java.awt.*;
import java.sql.*;

class Withdraw extends JFrame {

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

    Withdraw(String username) {
        // Background panel
        JPanel backgroundPanel = new JPanel(null);
        backgroundPanel.setBackground(new Color(8, 20, 30));
        setContentPane(backgroundPanel);

        // Title
        JLabel title = new JLabel("Withdraw Money", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(0, 230, 255));
        title.setBounds(0, 20, 800, 40);
        backgroundPanel.add(title);

        // Label and input
        createLabel("Enter Amount:", 250, 120, 200, 30, backgroundPanel);
        JTextField amountField = createTextField(250, 160, 300, 30, backgroundPanel);

        // Buttons
        JButton withdrawBtn = createButton("Withdraw", 250, 220, 120, 42, backgroundPanel);
        JButton backBtn = createButton("Back", 430, 220, 120, 42, backgroundPanel);

        // Button actions
        backBtn.addActionListener(a -> {
            new HomePage(username);
            dispose();
        });

        withdrawBtn.addActionListener(a -> {
            double balance = fetchBalance(username);
            double wlimit = fetchLimit(username);

            String amtStr = amountField.getText();
            if (amtStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter an amount");
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amtStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid amount");
                return;
            }

            if (amount > balance) {
                JOptionPane.showMessageDialog(null, "Amount is greater than balance");
            } else if (amount > wlimit) {
                JOptionPane.showMessageDialog(null, "Withdrawal limit exceeded");
            } else {
                updateBalance(username, balance - amount);
                updatePassbook(username, "Withdrawn", amount, balance - amount);
                JOptionPane.showMessageDialog(null, "Successfully Withdrawn");
                amountField.setText("");
            }
        });

        // Frame settings
        setTitle("VaultEdge - Withdraw Money");
        setSize(800, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
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

    private double fetchLimit(String username) {
        double limit = 0.0;
        String url = "jdbc:mysql://localhost:3306/3dec";
        try (Connection con = DriverManager.getConnection(url, "root", "your_password")) {
            String sql = "SELECT wlimit FROM users WHERE username = ?";
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setString(1, username);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) limit = rs.getDouble("wlimit");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return limit;
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

    public static void main(String[] args) {
        new Withdraw("User");
    }
}
