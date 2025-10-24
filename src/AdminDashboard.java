import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.*;

public class AdminDashboard extends JFrame {

    // Database credentials
    String url = EnvLoader.get("DB_URL");
    String user = EnvLoader.get("DB_USER");
    String password = EnvLoader.get("DB_PASSWORD");

    private JPanel adminDashboardPanel;

    // Create labels
    private JLabel createLabel(String text, int x, int y, int width, int height, JPanel panel, int fontSize, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        label.setForeground(color);
        label.setBounds(x, y, width, height);
        panel.add(label);
        return label;
    }

    // Create buttons
    private JButton createButton(String text, int x, int y, int width, int height, JPanel panel) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(10, 25, 40));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBounds(x, y, width, height);
        panel.add(button);
        return button;
    }

    // Creates overview panel
    private JPanel createOverviewPanel() {

        // Overview panel
        JPanel overviewPanel = new JPanel();
        overviewPanel.setLayout(null);
        overviewPanel.setBackground(new Color(12, 25, 38));

        // Colors
        Color cyan = new Color(0, 230, 255);
        Color boxColor = new Color(10, 30, 50);

        // Users panel
        JPanel usersPanel = new JPanel(null);
        usersPanel.setBackground(boxColor);
        usersPanel.setBounds(40, 40, 180, 100);
        usersPanel.setBorder(BorderFactory.createLineBorder(cyan, 2));
        overviewPanel.add(usersPanel);

        // Total users label
        createLabel("Total Users", 35, 10, 150, 25, usersPanel, 15, Color.WHITE);

        // Balance label
        JPanel balanceLabel = new JPanel(null);
        balanceLabel.setBackground(boxColor);
        balanceLabel.setBounds(260, 40, 180, 100);
        balanceLabel.setBorder(BorderFactory.createLineBorder(cyan, 2));
        overviewPanel.add(balanceLabel);

        // Total balance label
        createLabel("Total Balance", 25, 10, 150, 25, balanceLabel, 15, Color.WHITE);

        // Transactions label
        JPanel transactionsLabel = new JPanel(null);
        transactionsLabel.setBackground(boxColor);
        transactionsLabel.setBounds(480, 40, 180, 100);
        transactionsLabel.setBorder(BorderFactory.createLineBorder(cyan, 2));
        overviewPanel.add(transactionsLabel);

        // Total transactions label
        createLabel("Total Transactions", 10, 10, 180, 25, transactionsLabel, 15, Color.WHITE);

        try (Connection conn = DriverManager.getConnection(url, user, password)) {

            String totalUserSql = "SELECT COUNT(DISTINCT username) AS totalUsers FROM transactions";
            try (PreparedStatement pst = conn.prepareStatement(totalUserSql);
                 ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {

                    // TU Count label
                    createLabel(rs.getString("totalUsers"), 60, 50, 100, 25, usersPanel, 18, cyan);
                }
            }

            String totalTransactionsSql = "SELECT COUNT(*) AS totalTransactions FROM transactions";
            try (PreparedStatement pst = conn.prepareStatement(totalTransactionsSql);
                 ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {

                    // TT Count
                    createLabel(rs.getString("totalTransactions"), 60, 50, 100, 25, transactionsLabel, 18, cyan);
                }
            }

            // Total balance
            String totalBalanceSql =
                    "SELECT SUM(balance_after) AS totalBalance FROM (" +
                            "SELECT username, MAX(balance_after) AS balance_after " +
                            "FROM transactions GROUP BY username) AS userBalances";
            try (PreparedStatement pst = conn.prepareStatement(totalBalanceSql);
                 ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {

                    // TB Count label
                    createLabel("₹"+rs.getString("totalBalance"), 40, 50, 150, 25, balanceLabel, 18, cyan);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Creates dataset for PieChart
        DefaultPieDataset pieDataset = new DefaultPieDataset();

        try (Connection con = DriverManager.getConnection(url, user, password)) {
            String totalSql = "SELECT SUM(total_deposits) AS total_deposits, " +
                    "SUM(total_withdrawals) AS total_withdrawals, " +
                    "SUM(total_transfers) AS total_transfers " +
                    "FROM transaction_summary";

            try (PreparedStatement pst = con.prepareStatement(totalSql);
                 ResultSet rs = pst.executeQuery()) {

                if (rs.next()) {
                    double deposits = rs.getDouble("total_deposits");
                    double withdrawals = rs.getDouble("total_withdrawals");
                    double transfers = rs.getDouble("total_transfers");

                    pieDataset.setValue("Deposits", deposits);
                    pieDataset.setValue("Withdrawals", withdrawals);
                    pieDataset.setValue("Transfers", transfers);
                } else {
                    pieDataset.setValue("Deposits", 0);
                    pieDataset.setValue("Withdrawals", 0);
                    pieDataset.setValue("Transfers", 0);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching overall summary: " + e.getMessage());
        }

        // Pie chart
        JFreeChart pieChart = ChartFactory.createPieChart(
                "Monthly Transactions", pieDataset, true, true, false
        );

        // Creates an object for pie chart
        org.jfree.chart.plot.PiePlot plot = (org.jfree.chart.plot.PiePlot) pieChart.getPlot();
        plot.setBackgroundPaint(new Color(12, 25, 38));
        plot.setOutlineVisible(false);
        plot.setLabelPaint(Color.WHITE);

        // Chart wrapper panel
        JPanel chartWrapperPanel = new JPanel(new BorderLayout());
        chartWrapperPanel.setBounds(40, 180, 620, 300);
        chartWrapperPanel.setBackground(new Color(12, 25, 38));

        // Chart panel
        ChartPanel chartPanel = new ChartPanel(pieChart);
        chartPanel.setOpaque(false);
        chartWrapperPanel.add(chartPanel, BorderLayout.CENTER);
        overviewPanel.add(chartWrapperPanel);

        return overviewPanel;
    }

    // Creates users panel
    private JPanel createUsersPanel() {

        // Users panel
        JPanel usersPanel = new JPanel();
        usersPanel.setLayout(null);
        usersPanel.setBackground(new Color(12, 25, 38));
        Color cyan = new Color(0, 230, 255);

        // User label
        createLabel("User Management", 250, 20, 400, 30, usersPanel, 22, cyan);

        // Search TextField
        JTextField searchTextField = new JTextField();
        searchTextField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        searchTextField.setBounds(40, 70, 300, 35);
        usersPanel.add(searchTextField);

        // Search button
        JButton searchButton = createButton("Search", 360, 70, 100, 35, usersPanel);

        // Column names for users table
        String[] columns = {"User ID", "Name", "Email", "Type", "Balance", "Status"};
        Object[][] data = {
                {"101", "John Doe", "john@example.com", "Customer", "$5200", "Active"},
                {"102", "Alice", "alice@example.com", "Customer", "$4300", "Active"},
                {"103", "Bob", "bob@example.com", "Employee", "$7200", "Inactive"},
                {"104", "Charlie", "charlie@example.com", "Customer", "$1100", "Active"}
        };

        // Creates users table
        JTable userTable = new JTable(new DefaultTableModel(data, columns));
        userTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userTable.setForeground(Color.WHITE);
        userTable.setBackground(new Color(15, 30, 45));
        userTable.setRowHeight(28);
        userTable.getTableHeader().setBackground(cyan);
        userTable.getTableHeader().setForeground(Color.BLACK);
        userTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Allows scrolling for users table
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBounds(40, 130, 620, 300);
        scrollPane.setBorder(BorderFactory.createLineBorder(cyan, 1));
        usersPanel.add(scrollPane);

        // View button
        JButton viewButton = createButton("View", 120, 460, 120, 40, usersPanel);

        // Edit button
        JButton editButton = createButton("Edit", 280, 460, 120, 40, usersPanel);

        // Delete button
        JButton deleteButton = createButton("Delete", 440, 460, 120, 40, usersPanel);

        return usersPanel;
    }

    // Creates transaction panel
    private JPanel createTransactionsPanel() {

        // Transactions panel
        JPanel transactionsPanel = new JPanel();
        transactionsPanel.setLayout(null);
        transactionsPanel.setBackground(new Color(12, 25, 38));

        Color cyan = new Color(0, 230, 255);

        // Title label
        createLabel("Transactions Overview", 230, 20, 400, 30, transactionsPanel, 22, cyan);

        // Search TextField
        JTextField searchTextField = new JTextField();
        searchTextField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        searchTextField.setBounds(40, 70, 300, 35);
        transactionsPanel.add(searchTextField);

        // Search button
        JButton searchBtn = createButton("Search", 360, 70, 100, 35, transactionsPanel);

        // Column names for transactions table
        String[] columns = {"Txn ID", "User", "Type", "Amount", "Date", "Status"};
        Object[][] data = {
                {"T101", "John Doe", "Deposit", "$500", "2025-10-21", "Success"},
                {"T102", "Alice", "Withdrawal", "$300", "2025-10-21", "Success"},
                {"T103", "Bob", "Transfer", "$150", "2025-10-22", "Pending"},
                {"T104", "Charlie", "Deposit", "$800", "2025-10-22", "Failed"},
                {"T105", "Yash", "Transfer", "$1200", "2025-10-22", "Success"},
                {"T106", "Meena", "Withdrawal", "$450", "2025-10-23", "Success"}
        };

        // Transaction table
        JTable transactionTable = new JTable(new DefaultTableModel(data, columns));
        transactionTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        transactionTable.setForeground(Color.WHITE);
        transactionTable.setBackground(new Color(15, 30, 45));
        transactionTable.setRowHeight(28);
        transactionTable.getTableHeader().setBackground(cyan);
        transactionTable.getTableHeader().setForeground(Color.BLACK);
        transactionTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Allows scrolling for transaction table
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBounds(40, 130, 620, 250);
        scrollPane.setBorder(BorderFactory.createLineBorder(cyan, 1));
        transactionsPanel.add(scrollPane);

        int depositsCount = 60;
        int withdrawalsCount = 25;
        int transfersCount = 15;

        addTransactionSummaryText(transactionsPanel, 400, depositsCount, withdrawalsCount, transfersCount);

        return transactionsPanel;
    }

    // Creates transaction summary
    private void addTransactionSummaryText(JPanel panel, int y, int deposits, int withdrawals, int transfers) {
        int total = deposits + withdrawals + transfers;
        if (total == 0) total = 1;

        Color textColor = Color.WHITE;
        Font font = new Font("Segoe UI", Font.BOLD, 16);

        int x = 40;
        int spacing = 20;

        // Deposits label
        JLabel depositsLabel = new JLabel("Deposits: " + (deposits * 100 / total) + "%");
        depositsLabel.setForeground(textColor);
        depositsLabel.setFont(font);
        depositsLabel.setBounds(x, y, 150, 30);
        panel.add(depositsLabel);

        // Withdrawals label
        JLabel withdrawalsLabel = new JLabel("Withdrawals: " + (withdrawals * 100 / total) + "%");
        withdrawalsLabel.setForeground(textColor);
        withdrawalsLabel.setFont(font);
        withdrawalsLabel.setBounds(x + 150 + spacing, y, 180, 30);
        panel.add(withdrawalsLabel);

        // Transfers label
        JLabel transfersLabel = new JLabel("Transfers: " + (transfers * 100 / total) + "%");
        transfersLabel.setForeground(textColor);
        transfersLabel.setFont(font);
        transfersLabel.setBounds(x + 150 + spacing + 180 + spacing, y, 150, 30);
        panel.add(transfersLabel);
    }

    // Creates logs panel
    private JPanel createLogsPanel() {

        // Logs panel
        JPanel logsPanel = new JPanel();
        logsPanel.setLayout(null);
        logsPanel.setBackground(new Color(12, 25, 38));

        Color cyan = new Color(0, 230, 255);

        // Title label
        createLabel("Activity Logs", 250, 20, 400, 30, logsPanel, 22, cyan);

        // Column names for logs table
        String[] columns = {"Timestamp", "Action", "Target User", "Admin", "Remarks"};
        Object[][] data = {
                {"2025-10-23 09:10", "Deleted User", "John Doe", "Admin", "User removed successfully"},
                {"2025-10-23 10:30", "Updated Withdrawal Limit", "Alice", "Admin", "Limit changed to $2000"},
                {"2025-10-23 11:00", "Login", "-", "Admin", "Admin logged in successfully"},
                {"2025-10-23 11:45", "Edited User", "Bob", "Admin", "Updated user role to Employee"}
        };

        // Logs table
        JTable logsTable = new JTable(new DefaultTableModel(data, columns));
        logsTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        logsTable.setForeground(Color.WHITE);
        logsTable.setBackground(new Color(15, 30, 45));
        logsTable.setRowHeight(28);
        logsTable.getTableHeader().setBackground(cyan);
        logsTable.getTableHeader().setForeground(Color.BLACK);
        logsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Allows scrolling for logs table
        JScrollPane scrollPane = new JScrollPane(logsTable);
        scrollPane.setBounds(40, 70, 620, 370);
        scrollPane.setBorder(BorderFactory.createLineBorder(cyan, 1));
        logsPanel.add(scrollPane);

        // Clear button
        JButton clearButton = createButton("Clear Logs", 250, 460, 180, 40, logsPanel);
        clearButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(logsPanel,
                    "Are you sure you want to clear all logs?",
                    "Confirm Clear Logs",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                DefaultTableModel model = (DefaultTableModel) logsTable.getModel();
                model.setRowCount(0);
                JOptionPane.showMessageDialog(logsPanel, "All logs cleared!");
            }
        });

        return logsPanel;
    }

    // Creates settings panel
    private JPanel createSettingsPanel() {

        // Settings panel
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(null);
        settingsPanel.setBackground(new Color(12, 25, 38));
        Color cyan = new Color(0, 230, 255);

        // Title label
        createLabel("Settings", 250, 20, 400, 30, settingsPanel, 22, cyan);

        Font labelFont = new Font("Segoe UI", Font.BOLD, 16);
        Font inputFont = new Font("Segoe UI", Font.PLAIN, 15);

        // Minimum Balance label
        createLabel("Minimum Balance:", 50, 80, 180, 30, settingsPanel, 16, Color.WHITE);

        // Minimum balance TextField
        JTextField minimumBalanceTextField = new JTextField();
        minimumBalanceTextField.setFont(inputFont);
        minimumBalanceTextField.setBounds(230, 80, 200, 30);
        settingsPanel.add(minimumBalanceTextField);

        // Max Withdrawal Limit label
        createLabel("Max Withdrawal Limit:", 50, 130, 200, 30, settingsPanel, 16, Color.WHITE);

        // Maximum withdrawal limit TextField
        JTextField maxWithdrawalTextField = new JTextField();
        maxWithdrawalTextField.setFont(inputFont);
        maxWithdrawalTextField.setBounds(230, 130, 200, 30);
        settingsPanel.add(maxWithdrawalTextField);

        // Add New Admin label
        createLabel("Add New Admin", 50, 190, 200, 30, settingsPanel, 18, cyan);

        // Name label
        createLabel("Name:", 50, 230, 100, 25, settingsPanel, 15, Color.WHITE);

        // Name TextField
        JTextField adminNameField = new JTextField();
        adminNameField.setBounds(150, 230, 200, 25);
        adminNameField.setFont(inputFont);
        settingsPanel.add(adminNameField);

        // Email label
        createLabel("Email:", 50, 270, 100, 25, settingsPanel, 15, Color.WHITE);

        // Email TextField
        JTextField adminEmailField = new JTextField();
        adminEmailField.setBounds(150, 270, 200, 25);
        adminEmailField.setFont(inputFont);
        settingsPanel.add(adminEmailField);

        // Password label
        createLabel("Password:", 50, 310, 100, 25, settingsPanel, 15, Color.WHITE);

        // Password TextField
        JPasswordField adminPasswordField = new JPasswordField();
        adminPasswordField.setBounds(150, 310, 200, 25);
        adminPasswordField.setFont(inputFont);
        settingsPanel.add(adminPasswordField);

        // Apply Button
        JButton applyButton = new JButton("Apply");
        applyButton.setBounds(150, 370, 100, 35);
        applyButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        applyButton.setForeground(Color.WHITE);
        applyButton.setBackground(new Color(10, 25, 40));
        applyButton.setFocusPainted(false);
        applyButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        applyButton.setBorder(BorderFactory.createLineBorder(cyan, 2));
        applyButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                applyButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                applyButton.setBorder(BorderFactory.createLineBorder(cyan, 2));
            }
        });
        applyButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(settingsPanel, "Settings Applied Successfully!");
        });
        settingsPanel.add(applyButton);

        // Save Button
        JButton saveButton = new JButton("Save");
        saveButton.setBounds(270, 370, 100, 35);
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        saveButton.setForeground(Color.WHITE);
        saveButton.setBackground(new Color(10, 25, 40));
        saveButton.setFocusPainted(false);
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.setBorder(BorderFactory.createLineBorder(cyan, 2));
        saveButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                saveButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                saveButton.setBorder(BorderFactory.createLineBorder(cyan, 2));
            }
        });
        saveButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(settingsPanel, "Settings Saved Successfully!");
        });
        settingsPanel.add(saveButton);

        return settingsPanel;
    }

    // Constructor
    public AdminDashboard(String username) {

        Color backgroundColor = new Color(8, 20, 30);
        Color sidebarColor = new Color(10, 25, 40);
        Color cyan = new Color(0, 230, 255);

        // Top Panel
        JPanel topPanel = new JPanel(null);
        topPanel.setBackground(sidebarColor);
        topPanel.setBounds(0, 0, 900, 60);
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, cyan));
        add(topPanel);

        // VaultEdge label
        createLabel("VaultEdge", 25, 15, 200, 30, topPanel, 22, cyan);

        // Title label
        JLabel titleLabel = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(280, 15, 350, 30);
        topPanel.add(titleLabel);

        // Welcome label
        createLabel("Welcome, Admin", 720, 18, 200, 25, topPanel, 15, cyan);

        // Side Panel
        JPanel sidePanel = new JPanel(null);
        sidePanel.setBackground(backgroundColor);
        sidePanel.setBounds(0, 60, 200, 540);
        sidePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, cyan));
        add(sidePanel);

        // Overview button
        JButton overviewButton = createButton("Overview", 0, 0, 199, 40, sidePanel);
        overviewButton.addActionListener(e -> showOverviewPanel());

        // Users button
        JButton usersButton = createButton("Users", 0, 40, 199, 40, sidePanel);
        usersButton.addActionListener(e -> showUsersPanel());

        // Transactions button
        JButton transactionsButton = createButton("Transactions", 0, 80, 199, 40, sidePanel);
        transactionsButton.addActionListener(e -> showTransactionsPanel());

        // Logs button
        JButton logsButton = createButton("Logs", 0, 120, 199, 40, sidePanel);
        logsButton.addActionListener(e -> showLogsPanel());

        // Settings button
        JButton settingsButton = createButton("Settings", 0, 160, 199, 40, sidePanel);
        settingsButton.addActionListener(e -> showSettingsPanel());

        // Logout button
        JButton logoutButton = createButton("Logout", 40, 450, 120, 40, sidePanel);

        logoutButton.addActionListener(e -> {
            dispose();
            new AdminLogin();
        });

        // Admin dashboard panel
        adminDashboardPanel = new JPanel(null);
        adminDashboardPanel.setBackground(new Color(12, 25, 38));
        adminDashboardPanel.setBounds(200, 60, 700, 540);
        add(adminDashboardPanel);

        showOverviewPanel();

        // Frame Settings
        setTitle("VaultEdge Admin Dashboard");
        setSize(900, 600);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    // Loads overview panel
    private void showOverviewPanel() {
        adminDashboardPanel.removeAll();
        JPanel overview = createOverviewPanel();
        overview.setBounds(0, 0, 700, 540);
        adminDashboardPanel.add(overview);
        adminDashboardPanel.revalidate();
        adminDashboardPanel.repaint();
    }

    // Loads users panel
    private void showUsersPanel() {
        adminDashboardPanel.removeAll();
        JPanel users = createUsersPanel();
        users.setBounds(0, 0, 700, 540);
        adminDashboardPanel.add(users);
        adminDashboardPanel.revalidate();
        adminDashboardPanel.repaint();
    }

    // Loads transaction panel
    private void showTransactionsPanel() {
        adminDashboardPanel.removeAll();
        JPanel transactions = createTransactionsPanel();
        transactions.setBounds(0, 0, 700, 540);
        adminDashboardPanel.add(transactions);
        adminDashboardPanel.revalidate();
        adminDashboardPanel.repaint();
    }

    // Loads log panel
    private void showLogsPanel() {
        adminDashboardPanel.removeAll();
        JPanel logs = createLogsPanel();
        logs.setBounds(0, 0, 700, 540);
        adminDashboardPanel.add(logs);
        adminDashboardPanel.revalidate();
        adminDashboardPanel.repaint();
    }

    // Loads settings panel
    private void showSettingsPanel() {
        adminDashboardPanel.removeAll();
        JPanel settings = createSettingsPanel();
        settings.setBounds(0, 0, 700, 540);
        adminDashboardPanel.add(settings);
        adminDashboardPanel.revalidate();
        adminDashboardPanel.repaint();
    }

    public static void main(String[] args) {
        new AdminDashboard("Admin");
    }
}
