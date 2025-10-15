import javax.swing.*;
import java.awt.*;
import java.sql.*;

class HomePage extends JFrame {

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

    // Create transaction table
    private JTable createTransactionTable(int x, int y, int width, int height, JPanel panel) {
        String[] columns = {"Date", "Description", "Type", "Amount", "Balance"};
        String[][] data = new String[5][5];
        JTable table = new JTable(data, columns);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setBackground(new Color(15, 30, 40));
        table.setForeground(new Color(220, 235, 245));
        table.setGridColor(new Color(0, 230, 255));
        table.setRowHeight(25);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(x, y, width, height);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0, 230, 255), 1));
        panel.add(scroll);

        return table;
    }

    HomePage(String username) {

        // DB Credentials
        String url = EnvLoader.get("DB_URL");
        String user = EnvLoader.get("DB_USER");
        String password = EnvLoader.get("DB_PASSWORD");

        // Home page panel
        JPanel homePagePanel = new JPanel(null);
        homePagePanel.setBackground(new Color(8, 20, 30));
        setContentPane(homePagePanel);

        // Title label
        JLabel titleLabel = new JLabel("Welcome, " + username, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 230, 255));
        titleLabel.setBounds(0, 25, 900, 40);
        homePagePanel.add(titleLabel);

        // Account type label
        JLabel accountTypeLabel = createLabel("Account Type: ---", 120, 90, 300, 25, homePagePanel, 16);

        // Account number label
        JLabel accountNumberLabel = createLabel("Account No: ---", 120, 120, 300, 25, homePagePanel, 16);

        // Balance label
        JLabel balanceLabel = createLabel("Balance: ₹0.00", 120, 150, 300, 25, homePagePanel, 16);

        // Deposit button
        JButton depositButton = createButton("Deposit", 120, 210, 200, 42, homePagePanel);
        depositButton.addActionListener(a -> { new DepositPage(username); dispose(); });

        // Withdraw button
        JButton withdrawButton = createButton("Withdraw", 370, 210, 200, 42, homePagePanel);
        withdrawButton.addActionListener(a -> { new Withdraw(username); dispose(); });

        // Transfer button
        JButton transferButton = createButton("Transfer", 620, 210, 200, 42, homePagePanel);
        transferButton.addActionListener(a -> new TransferPage(username));

        // Passbook button
        JButton passbookButton = createButton("Passbook", 120, 280, 200, 42, homePagePanel);
        passbookButton.addActionListener(a -> new PassbookPage(username));

        // Settings button
        JButton settingsButton = createButton("Profile Settings", 370, 280, 200, 42, homePagePanel);
        settingsButton.addActionListener(a -> new Profile(username));

        // Logout button
        JButton logoutButton = createButton("Logout", 620, 280, 200, 42, homePagePanel);
        logoutButton.addActionListener(a -> { new LandingPage(); dispose(); });

        // Recent transaction label
        createLabel("Recent Transactions", 120, 340, 300, 25, homePagePanel, 17);

        // Transaction table
        JTable transactionTable = createTransactionTable(120, 370, 700, 150, homePagePanel);


        try (Connection con = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT account_type, balance, account_number FROM users WHERE username=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                accountTypeLabel.setText("Account Type: " + rs.getString("account_type"));
                accountNumberLabel.setText("Account No: " + rs.getString("account_number"));
                balanceLabel.setText("Balance: ₹" + rs.getDouble("balance"));
            }

            String txnQuery = "SELECT date, description, type, amount, balance_after FROM transactions WHERE username=? ORDER BY date DESC LIMIT 5";
            PreparedStatement txnStmt = con.prepareStatement(txnQuery);
            txnStmt.setString(1, username);
            ResultSet txnRs = txnStmt.executeQuery();

            int i = 0;
            while (txnRs.next() && i < 5) {
                transactionTable.setValueAt(txnRs.getString("date"), i, 0);
                transactionTable.setValueAt(txnRs.getString("description"), i, 1);
                transactionTable.setValueAt(txnRs.getString("type"), i, 2);
                transactionTable.setValueAt("₹" + txnRs.getDouble("amount"), i, 3);
                transactionTable.setValueAt("₹" + txnRs.getDouble("balance_after"), i, 4);
                i++;
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }

        setTitle("VaultEdge - Home");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        new HomePage("User");
    }
}
