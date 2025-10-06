import javax.swing.*;
import java.awt.*;
import java.sql.*;

class HomePage extends JFrame {

    // Styled label
    private JLabel createLabel(String text, int x, int y, int width, int height, JPanel panel) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setForeground(new Color(200, 240, 255));
        label.setBounds(x, y, width, height);
        panel.add(label);
        return label;
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

    HomePage(String username) {
        // Background panel
        JPanel backgroundPanel = new JPanel(null);
        backgroundPanel.setBackground(new Color(8, 20, 30));
        setContentPane(backgroundPanel);

        // Title
        JLabel title = new JLabel("Welcome " + username, SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(0, 230, 255));
        title.setBounds(0, 30, 800, 40);
        backgroundPanel.add(title);

        // Balance label
        JLabel balanceLabel = new JLabel("Balance: ₹0.00", SwingConstants.CENTER);
        balanceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        balanceLabel.setForeground(new Color(200, 240, 255));
        balanceLabel.setBounds(0, 80, 800, 30);
        backgroundPanel.add(balanceLabel);

        // Buttons
        JButton depositBtn = createButton("Deposit", 150, 150, 200, 42, backgroundPanel);
        JButton withdrawBtn = createButton("Withdraw", 450, 150, 200, 42, backgroundPanel);
        JButton profileBtn = createButton("Profile Settings", 150, 220, 200, 42, backgroundPanel);
        JButton transferBtn = createButton("Transfer", 450, 220, 200, 42, backgroundPanel);
        JButton passbookBtn = createButton("Passbook", 150, 290, 200, 42, backgroundPanel);
        JButton logoutBtn = createButton("Logout", 450, 290, 200, 42, backgroundPanel);

        // Action listeners
        depositBtn.addActionListener(a -> { new DepositPage(username); dispose(); });
        withdrawBtn.addActionListener(a -> { new Withdraw(username); dispose(); });
        transferBtn.addActionListener(a -> new TransferPage(username));
        passbookBtn.addActionListener(a -> new PassbookPage(username));
        logoutBtn.addActionListener(a -> { new LandingPage(); dispose(); });

        // Fetch balance from DB
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/3dec", "root", "your_password")) {
            String sql = "SELECT balance FROM users WHERE username=?";
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setString(1, username);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    double balance = rs.getDouble("balance");
                    balanceLabel.setText("Balance: ₹" + balance);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

        // Frame settings
        setTitle("VaultEdge - Home");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        new HomePage("User");
    }
}
