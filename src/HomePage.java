import javax.swing.*;
import java.awt.*;
import java.sql.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.category.DefaultCategoryDataset;

class HomePage extends JFrame {

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

        // Home page panel
        JPanel homePagePanel = new JPanel(null);
        homePagePanel.setBackground(new Color(8, 20, 30));
        setContentPane(homePagePanel);

        // Title label
        JLabel titleLabel = new JLabel("Welcome, " + username, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 230, 255));
        titleLabel.setBounds(0, 25, 700, 40);
        homePagePanel.add(titleLabel);

        // Account type label
        JLabel accountTypeLabel = createLabel("Account Type: ---", 40, 90, 300, 25, homePagePanel, 16);

        // Account number label
        JLabel accountNumberLabel = createLabel("Account No: ---", 40, 120, 300, 25, homePagePanel, 16);

        // Balance label
        JLabel balanceLabel = createLabel("Balance: ₹0.00", 40, 150, 300, 25, homePagePanel, 16);

        // Deposit button
        JButton depositButton = createButton("Deposit", 40, 200, 150, 42, homePagePanel);
        depositButton.addActionListener(a -> { new DepositPage(username); dispose(); });

        // Withdraw button
        JButton withdrawButton = createButton("Withdraw", 215, 200, 150, 42, homePagePanel);
        withdrawButton.addActionListener(a -> { new Withdraw(username); dispose(); });

        // Transfer button
        JButton transferButton = createButton("Transfer", 215, 270, 150, 42, homePagePanel);
        transferButton.addActionListener(a -> new TransferPage(username));

        // VirtualCard button
        JButton virtualCardButton = createButton("Virtual Card", 40, 270, 150, 42, homePagePanel);
        virtualCardButton.addActionListener(a -> new VirtualCard(username));

        // Passbook button
        JButton passbookButton = createButton("Passbook", 430, 300, 150, 42, homePagePanel);
        passbookButton.addActionListener(a -> new PassbookPage(username));

        // Settings button
        JButton settingsButton = createButton("Profile Settings", 590, 30, 150, 42, homePagePanel);
        settingsButton.addActionListener(a -> new Profile(username));

        // View analytics button
        JButton viewAnalyticsButton = createButton("View Analytics", 590, 300, 150, 42, homePagePanel);
        viewAnalyticsButton.addActionListener(a -> new Analysis(username));

        // Creates data for pie chart
        DefaultPieDataset pieDataset = new DefaultPieDataset();

        try (Connection con = DriverManager.getConnection(url, user, password)) {
            String chartSql = "SELECT type, SUM(amount) AS total FROM transactions WHERE username=? AND MONTH(date) = MONTH(CURRENT_DATE) GROUP BY type";
            try (PreparedStatement pst = con.prepareStatement(chartSql)){
                pst.setString(1, username);

                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        String type = rs.getString("type");
                        double total = rs.getDouble("total");
                        pieDataset.setValue(type, total);
                    }
                }

            } catch (SQLException pstEx) {
                pstEx.printStackTrace();
                JOptionPane.showMessageDialog(this, "PreparedStatement Error: " + pstEx.getMessage());
            }

        } catch (SQLException connEx) {
            connEx.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Connection Error: " + connEx.getMessage());
        }

        // Displays pie chart
        JFreeChart pieChart = ChartFactory.createPieChart("Monthly Transactions", pieDataset, true, true, false);

        org.jfree.chart.plot.PiePlot plot = (org.jfree.chart.plot.PiePlot) pieChart.getPlot();
        plot.setLabelGenerator(new org.jfree.chart.labels.StandardPieSectionLabelGenerator("{0}: ₹{1} ({2})"));
        plot.setBackgroundPaint(new Color(8, 20, 30));
        plot.setOutlineVisible(false);

        // Creates space for pie chart
        ChartPanel chartPanel = new ChartPanel(pieChart);
        chartPanel.setBounds(490, 90, 250, 200);
        chartPanel.setBackground(new Color(8, 20, 30));
        homePagePanel.add(chartPanel);

        // Logout button
        JButton logoutButton = createButton("Logout", 620, 510, 120, 42, homePagePanel);
        logoutButton.addActionListener(a -> {
            logActivity("User Logout", username, "System",
                    "User logged out successfully");

            new LandingPage();
            dispose();
        });

        // Recent transaction label
        createLabel("Recent Transactions", 40, 325, 300, 25, homePagePanel, 17);

        // Transaction table
        JTable transactionTable = createTransactionTable(40, 350, 700, 150, homePagePanel);

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
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        new HomePage("User");
    }
}