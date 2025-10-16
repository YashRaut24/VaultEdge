import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.plot.PlotOrientation;

class Analysis extends JFrame {

    private JComboBox<String> monthCombo, yearCombo;
    private JLabel updatedLabel, insightLabel;

    // Create label
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

    // Constructor
    Analysis(String username) {

        // DB Credentials
        String url = EnvLoader.get("DB_URL");
        String user = EnvLoader.get("DB_USER");
        String password = EnvLoader.get("DB_PASSWORD");

        // Analytics panel
        JPanel analyticsPanel = new JPanel(null);
        analyticsPanel.setBackground(new Color(8, 20, 30));
        setContentPane(analyticsPanel);

        // Title label
        JLabel title = new JLabel("VaultEdge - View Analytics", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(0, 230, 255));
        title.setBounds(0, 20, 800, 40);
        analyticsPanel.add(title);

        // Month label
        createLabel("Month:", 40, 80, 80, 25, analyticsPanel, 16);

        // Months dropdown
        monthCombo = new JComboBox<>(new String[]{"All", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"});
        monthCombo.setBounds(110, 80, 120, 25);
        analyticsPanel.add(monthCombo);

        // Year label
        createLabel("Year:", 260, 80, 60, 25, analyticsPanel, 16);

        // Year dropdown
        yearCombo = new JComboBox<>(new String[]{"All", "2023", "2024", "2025"});
        yearCombo.setBounds(310, 80, 100, 25);
        analyticsPanel.add(yearCombo);

        // Filter button
        JButton filterButton = createButton("Filter", 430, 77, 100, 30, analyticsPanel);

        // Back button
        JButton backButton = createButton("Back", 650, 77, 100, 30, analyticsPanel);
        backButton.addActionListener(e -> {
            new HomePage(username);
            dispose();
        });

        // Last updated label
        updatedLabel = createLabel("Last Updated: " + new SimpleDateFormat("dd MMM yyyy").format(new Date()),550, 510, 300, 25, analyticsPanel, 13);

        // Draw charts & data
        drawCharts(username, analyticsPanel, url, user, password, "All", "All");

        filterButton.addActionListener(e -> {
            String selectedMonth = (String) monthCombo.getSelectedItem();
            String selectedYear = (String) yearCombo.getSelectedItem();
            getContentPane().removeAll();
            new Analysis(username).applyFilters(selectedMonth, selectedYear);
            dispose();
        });

        setTitle("VaultEdge - Analytics");
        setSize(820, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    // Reload analytics with selected filters
    private void applyFilters(String month, String year) {
        new Analysis("User").drawCharts("User", new JPanel(), EnvLoader.get("DB_URL"),
                EnvLoader.get("DB_USER"), EnvLoader.get("DB_PASSWORD"), month, year);
    }

    // Pie Chart: Deposit vs Withdrawal
    private void drawCharts(String username, JPanel analyticsPanel, String url, String user, String password, String monthFilter, String yearFilter) {
        // Storing data for pie chart
        DefaultPieDataset pieDataset = new DefaultPieDataset();

        try (Connection con = DriverManager.getConnection(url, user, password)) {
            String analyticsSql = "SELECT LOWER(type) AS type, SUM(amount) AS total FROM transactions WHERE username=? GROUP BY LOWER(type)";
            PreparedStatement pst = con.prepareStatement(analyticsSql);
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                pieDataset.setValue(rs.getString("type"), rs.getDouble("total"));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading pie chart: " + ex.getMessage());
        }

        // Creates Pie chart
        JFreeChart pieChart = ChartFactory.createPieChart("Deposit vs Withdrawal Ratio", pieDataset, true, true, false);
        pieChart.setBackgroundPaint(new Color(8, 20, 30));
        org.jfree.chart.plot.PiePlot plot = (org.jfree.chart.plot.PiePlot) pieChart.getPlot();
        plot.setBackgroundPaint(new Color(8, 20, 30));
        plot.setOutlineVisible(false);
        plot.setLabelGenerator(new org.jfree.chart.labels.StandardPieSectionLabelGenerator("{0}: ₹{1} ({2})"));

        // Pie chart panel
        ChartPanel piePanel = new ChartPanel(pieChart);
        piePanel.setBounds(40, 130, 340, 250);
        piePanel.setBackground(new Color(8, 20, 30));
        analyticsPanel.add(piePanel);

        // Stores data for bar chart
        DefaultCategoryDataset barDataset = new DefaultCategoryDataset();

        try (Connection con = DriverManager.getConnection(url, user, password)) {
            String sql = """
                SELECT MONTHNAME(date) AS month,SUM(CASE WHEN LOWER(type)='deposit' THEN amount ELSE 0 END) AS deposits,THEN amount ELSE 0 END) AS withdrawals
                FROM transactions WHERE username=?GROUP BY MONTH(date), MONTHNAME(date) ORDER BY MONTH(date)
                """;
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String month = rs.getString("month");
                barDataset.addValue(rs.getDouble("deposits"), "Deposits", month);
                barDataset.addValue(rs.getDouble("withdrawals"), "Withdrawals", month);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading bar chart: " + ex.getMessage());
        }

        // Creates bar chart
        JFreeChart barChart = ChartFactory.createBarChart("Monthly Deposit vs Withdrawal Trend", "Month", "Amount (₹)", barDataset,
                PlotOrientation.VERTICAL, true, true, false);
        barChart.setBackgroundPaint(new Color(8, 20, 30));
        barChart.getPlot().setBackgroundPaint(new Color(8, 20, 30));

        // Bar chart panel
        ChartPanel barPanel = new ChartPanel(barChart);
        barPanel.setBounds(410, 130, 360, 250);
        barPanel.setBackground(new Color(8, 20, 30));
        analyticsPanel.add(barPanel);

        // Info panel
        JPanel infoPanel = new JPanel(null);
        infoPanel.setBounds(40, 400, 730, 100);
        infoPanel.setBackground(new Color(10, 25, 35));
        infoPanel.setBorder(BorderFactory.createLineBorder(new Color(0, 230, 255), 1));
        analyticsPanel.add(infoPanel);

        double totalDeposits = 0, totalWithdrawals = 0;
        String mostActiveMonth = "-";

        try (Connection con = DriverManager.getConnection(url, user, password)) {
            String totalSql = """ 
                  SELECT SUM(CASE WHEN LOWER(type)='deposit' THEN amount ELSE 0 END) AS deposits,SUM(CASE WHEN LOWER(type)='withdrawal' THEN amount ELSE 0 END) 
                  AS withdrawals FROM transactions WHERE username=?
            """;
            PreparedStatement pst = con.prepareStatement(totalSql);
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                totalDeposits = rs.getDouble("deposits");
                totalWithdrawals = rs.getDouble("withdrawals");
            }

            // Most Active Month (highest total amount)
            String monthSql = """
                    SELECT MONTHNAME(date) AS month, SUM(amount) AS total FROM transactions WHERE username=? GROUP BY MONTH(date), MONTHNAME(date) ORDER BY total DESC LIMIT 1
            """;

            PreparedStatement pst2 = con.prepareStatement(monthSql);
            pst2.setString(1, username);
            ResultSet rs2 = pst2.executeQuery();
            if (rs2.next()) mostActiveMonth = rs2.getString("month");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading totals: " + ex.getMessage());
        }

        double netSavings = totalDeposits - totalWithdrawals;

        // Total deposits label
        createLabel("💵 Total Deposits: ₹" + String.format("%.2f", totalDeposits), 30, 20, 300, 25, infoPanel, 16);

        // Total withdrawals label
        createLabel("💸 Total Withdrawals: ₹" + String.format("%.2f", totalWithdrawals), 280, 20, 300, 25, infoPanel, 16);

        // Net savings label
        createLabel("📈 Net Savings: ₹" + String.format("%.2f", netSavings), 540, 20, 300, 25, infoPanel, 16);

        // Most active month label
        createLabel("📅 Most Active Month: " + mostActiveMonth, 30, 55, 400, 25, infoPanel, 16);

        String insightText;
        if (netSavings > 0) {
            insightText = "💡 You saved ₹" + String.format("%.2f", netSavings) + "! Great job managing your funds.";
        } else if (netSavings < 0) {
            insightText = "⚠️ You spent more than you saved. Try to control withdrawals next month.";
        } else {
            insightText = "ℹ️ Your spending and savings are balanced this month.";
        }

        insightLabel = createLabel(insightText, 40, 510, 700, 25, analyticsPanel, 15);
    }

    public static void main(String[] args) {
        new Analysis("User");
    }
}
