import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AdminDashboard extends JFrame {

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

        // TU Count label
        createLabel("1024", 60, 50, 100, 25, usersPanel, 18, cyan);

        // Balance label
        JPanel balanceLabel = new JPanel(null);
        balanceLabel.setBackground(boxColor);
        balanceLabel.setBounds(260, 40, 180, 100);
        balanceLabel.setBorder(BorderFactory.createLineBorder(cyan, 2));
        overviewPanel.add(balanceLabel);

        // Total balance label
        createLabel("Total Balance", 25, 10, 150, 25, balanceLabel, 15, Color.WHITE);

        // TB Count label
        createLabel("$452,300", 40, 50, 150, 25, balanceLabel, 18, cyan);

        // Transactions label
        JPanel transactionsLabel = new JPanel(null);
        transactionsLabel.setBackground(boxColor);
        transactionsLabel.setBounds(480, 40, 180, 100);
        transactionsLabel.setBorder(BorderFactory.createLineBorder(cyan, 2));
        overviewPanel.add(transactionsLabel);

        // Total transactions label
        createLabel("Total Transactions", 10, 10, 180, 25, transactionsLabel, 15, Color.WHITE);

        // TT Count
        createLabel("8476", 60, 50, 100, 25, transactionsLabel, 18, cyan);

        // Creates dataset for PieChart
        DefaultPieDataset pieDataset = new DefaultPieDataset();
        pieDataset.setValue("Deposits", 300000);
        pieDataset.setValue("Withdrawals", 120000);
        pieDataset.setValue("Transfers", 32000);

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
        JPanel usersPanel = new JPanel();
        usersPanel.setLayout(null);
        usersPanel.setBackground(new Color(12, 25, 38));
        Color cyan = new Color(0, 230, 255);

        createLabel("User Management", 250, 20, 400, 30, usersPanel, 22, cyan);

        JTextField searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        searchField.setBounds(40, 70, 300, 35);
        usersPanel.add(searchField);

        JButton searchBtn = createButton("Search", 360, 70, 100, 35, usersPanel);

        // Table
        String[] columns = {"User ID", "Name", "Email", "Type", "Balance", "Status"};
        Object[][] data = {
                {"101", "John Doe", "john@example.com", "Customer", "$5200", "Active"},
                {"102", "Alice", "alice@example.com", "Customer", "$4300", "Active"},
                {"103", "Bob", "bob@example.com", "Employee", "$7200", "Inactive"},
                {"104", "Charlie", "charlie@example.com", "Customer", "$1100", "Active"}
        };

        JTable userTable = new JTable(new DefaultTableModel(data, columns));
        userTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userTable.setForeground(Color.WHITE);
        userTable.setBackground(new Color(15, 30, 45));
        userTable.setRowHeight(28);
        userTable.getTableHeader().setBackground(cyan);
        userTable.getTableHeader().setForeground(Color.BLACK);
        userTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBounds(40, 130, 620, 300);
        scrollPane.setBorder(BorderFactory.createLineBorder(cyan, 1));
        usersPanel.add(scrollPane);

        // Buttons
        JButton viewBtn = createButton("View", 120, 460, 120, 40, usersPanel);
        JButton editBtn = createButton("Edit", 280, 460, 120, 40, usersPanel);
        JButton deleteBtn = createButton("Delete", 440, 460, 120, 40, usersPanel);

        return usersPanel;
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

        // Logs button
        JButton logsButton = createButton("Logs", 0, 120, 199, 40, sidePanel);

        // Settings button
        JButton settingsButton = createButton("Settings", 0, 160, 199, 40, sidePanel);

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

    // Handles overviewPanel
    private void showOverviewPanel() {
        adminDashboardPanel.removeAll();
        JPanel overview = createOverviewPanel();
        overview.setBounds(0, 0, 700, 540);
        adminDashboardPanel.add(overview);
        adminDashboardPanel.revalidate();
        adminDashboardPanel.repaint();
    }

    private void showUsersPanel() {
        adminDashboardPanel.removeAll();
        JPanel users = createUsersPanel();
        users.setBounds(0, 0, 700, 540);
        adminDashboardPanel.add(users);
        adminDashboardPanel.revalidate();
        adminDashboardPanel.repaint();
    }
    public static void main(String[] args) {
        new AdminDashboard("Admin");
    }
}
