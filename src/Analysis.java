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
    private JLabel updatedLabel, insightLabel, noDataLabel;

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

        // Month Label
        createLabel("Month:", 40, 80, 80, 25, analyticsPanel, 16);

        // Month dropdown
        monthCombo = new JComboBox<>(new String[]{"All", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"});
        monthCombo.setBounds(110, 80, 120, 25);
        analyticsPanel.add(monthCombo);

        // Year Label
        createLabel("Year:", 260, 80, 60, 25, analyticsPanel, 16);

        // Year dropdown
        yearCombo = new JComboBox<>(new String[]{"All", "2023", "2024", "2025"});
        yearCombo.setBounds(310, 80, 100, 25);
        analyticsPanel.add(yearCombo);

        // Filter Button
        JButton filterButton = createButton("Filter", 430, 77, 100, 30, analyticsPanel);

        // Last Updated Label
        updatedLabel = createLabel("Last Updated: " + new SimpleDateFormat("dd MMM yyyy").format(new Date()), 550, 510, 300, 25, analyticsPanel, 13);

        filterButton.addActionListener(e -> {
            String month = (String) monthCombo.getSelectedItem();
            String year = (String) yearCombo.getSelectedItem();
            analyticsPanel.removeAll();
            recreateHeader(analyticsPanel, username, month, year);
            drawCharts(username, analyticsPanel, url, user, password, month, year);
            analyticsPanel.revalidate();
            analyticsPanel.repaint();
        });

        // Draw charts initially
        drawCharts(username, analyticsPanel, url, user, password, "All", "All");

        // Back Button
        JButton backButton = createButton("Back", 650, 77, 100, 30, analyticsPanel);

        backButton.addActionListener(e -> {
            new HomePage(username);
            dispose();
        });

        // Frame settings
        setTitle("VaultEdge - Analytics");
        setSize(820, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    // Recreate header UI on filter refresh
    private void recreateHeader(JPanel panel, String username, String month, String year) {
        panel.setBackground(new Color(8, 20, 30));

        // Title label
        JLabel title = new JLabel("VaultEdge - View Analytics", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(0, 230, 255));
        title.setBounds(0, 20, 800, 40);
        panel.add(title);

        // Month label
        createLabel("Month:", 40, 80, 80, 25, panel, 16);
        panel.add(monthCombo);
        panel.add(yearCombo);

        // Filter button
        JButton filterButton = createButton("Filter", 430, 77, 100, 30, panel);
        filterButton.addActionListener(e -> {
            String m = (String) monthCombo.getSelectedItem();
            String y = (String) yearCombo.getSelectedItem();
            panel.removeAll();
            recreateHeader(panel, username, m, y);
            drawCharts(username, panel, EnvLoader.get("DB_URL"), EnvLoader.get("DB_USER"), EnvLoader.get("DB_PASSWORD"), m, y);
            panel.revalidate();
            panel.repaint();
        });

        // Back button
        JButton backButton = createButton("Back", 650, 77, 100, 30, panel);

        backButton.addActionListener(e -> {
            new HomePage(username);
            dispose();
        });

        // Last updated label
        updatedLabel = createLabel("Last Updated: " + new SimpleDateFormat("dd MMM yyyy").format(new Date()), 550, 510, 300, 25, panel, 13);
    }

    // Creates Chart & Data
    private void drawCharts(String username, JPanel analyticsPanel, String url, String user, String password, String monthFilter, String yearFilter) {

        // Stores data for bar chart
        DefaultCategoryDataset barDataset = new DefaultCategoryDataset();

        // Stores data for line chart
        DefaultCategoryDataset lineDataset = new DefaultCategoryDataset();
        boolean hasData = false;

        try (Connection con = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT MONTHNAME(date) AS month, " +
                    "SUM(CASE WHEN LOWER(type)='deposit' THEN amount ELSE 0 END) AS deposits, " +
                    "SUM(CASE WHEN LOWER(type)='withdraw' THEN amount ELSE 0 END) AS withdrawals " +
                    "FROM transactions WHERE LOWER(username)=? " +
                    (!monthFilter.equals("All") ? " AND MONTHNAME(date)=? " : "") +
                    (!yearFilter.equals("All") ? " AND YEAR(date)=? " : "") +
                    " GROUP BY MONTH(date), MONTHNAME(date) ORDER BY MONTH(date)";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, username.toLowerCase());
            int idx = 2;
            if (!monthFilter.equals("All")) pst.setString(idx++, monthFilter);
            if (!yearFilter.equals("All")) pst.setString(idx, yearFilter);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                hasData = true;
                String month = rs.getString("month");
                double deposits = rs.getDouble("deposits");
                double withdrawals = rs.getDouble("withdrawals");

                System.out.println("Month: " + month + ", Deposits: " + deposits + ", Withdrawals: " + withdrawals);

                // Bar chart
                barDataset.addValue(deposits, "Deposits", month);
                barDataset.addValue(withdrawals, "Withdrawals", month);

                // Line chart
                lineDataset.addValue(deposits, "Deposits", month);
                lineDataset.addValue(withdrawals, "Withdrawals", month);
            }

            if (!hasData) {
                JLabel noData = new JLabel("No transactions found for selected period.", SwingConstants.CENTER);
                noData.setForeground(new Color(255, 120, 120));
                noData.setFont(new Font("Segoe UI", Font.BOLD, 17));
                noData.setBounds(0, 200, 800, 30);
                analyticsPanel.add(noData);
                return;
            }

            // Line Chart
            JFreeChart lineChart = ChartFactory.createLineChart(
                    "Monthly Deposit vs Withdrawal", "Month", "Amount (₹)", lineDataset,
                    PlotOrientation.VERTICAL, true, true, false
            );
            lineChart.setBackgroundPaint(new Color(8, 20, 30));
            lineChart.getTitle().setPaint(Color.WHITE);

            org.jfree.chart.plot.CategoryPlot linePlot = lineChart.getCategoryPlot();
            linePlot.setBackgroundPaint(new Color(8, 20, 30));
            linePlot.setDomainGridlinePaint(new Color(80, 80, 80));
            linePlot.setRangeGridlinePaint(new Color(80, 80, 80));
            linePlot.getRenderer().setSeriesPaint(0, new Color(0, 230, 255));
            linePlot.getRenderer().setSeriesPaint(1, new Color(255, 100, 100));
            linePlot.getDomainAxis().setTickLabelPaint(new Color(200, 200, 200));
            linePlot.getDomainAxis().setLabelPaint(Color.LIGHT_GRAY);
            linePlot.getRangeAxis().setTickLabelPaint(new Color(200, 200, 200));
            linePlot.getRangeAxis().setLabelPaint(Color.LIGHT_GRAY);

            // Legend text color
            if (lineChart.getLegend() != null) {
                lineChart.getLegend().setBackgroundPaint(new Color(8, 20, 30));
                lineChart.getLegend().setItemPaint(Color.WHITE);
            }

            ChartPanel linePanel = new ChartPanel(lineChart);
            linePanel.setBounds(40, 130, 340, 250);
            linePanel.setBackground(new Color(8, 20, 30));
            analyticsPanel.add(linePanel);

            // Bar Chart
            JFreeChart barChart = ChartFactory.createBarChart(
                    "Monthly Deposit vs Withdrawal Trend", "Month", "Amount (₹)", barDataset,
                    PlotOrientation.VERTICAL, true, true, false
            );
            barChart.setBackgroundPaint(new Color(8, 20, 30));
            barChart.getTitle().setPaint(Color.WHITE);

            org.jfree.chart.plot.CategoryPlot barPlot = barChart.getCategoryPlot();
            barPlot.setBackgroundPaint(new Color(8, 20, 30));
            barPlot.setDomainGridlinePaint(new Color(80, 80, 80));
            barPlot.setRangeGridlinePaint(new Color(80, 80, 80));
            barPlot.getRenderer().setSeriesPaint(0, new Color(0, 230, 255));
            barPlot.getRenderer().setSeriesPaint(1, new Color(255, 100, 100));
            barPlot.getDomainAxis().setTickLabelPaint(new Color(200, 200, 200));
            barPlot.getDomainAxis().setLabelPaint(Color.LIGHT_GRAY);
            barPlot.getRangeAxis().setTickLabelPaint(new Color(200, 200, 200));
            barPlot.getRangeAxis().setLabelPaint(Color.LIGHT_GRAY);

            if (barChart.getLegend() != null) {
                barChart.getLegend().setBackgroundPaint(new Color(8, 20, 30));
                barChart.getLegend().setItemPaint(Color.WHITE);
            }

            // Bar chart panel
            ChartPanel barPanel = new ChartPanel(barChart);
            barPanel.setBounds(410, 130, 360, 250);
            barPanel.setBackground(new Color(8, 20, 30));
            analyticsPanel.add(barPanel);

            // Info Panel
            JPanel infoPanel = new JPanel(null);
            infoPanel.setBounds(40, 400, 730, 100);
            infoPanel.setBackground(new Color(10, 25, 35));
            infoPanel.setBorder(BorderFactory.createLineBorder(new Color(0, 230, 255), 1));
            analyticsPanel.add(infoPanel);

            double totalDeposits = 0, totalWithdrawals = 0;
            String mostActiveMonth = "-";

            String totalSql = "SELECT SUM(CASE WHEN LOWER(type)='deposit' THEN amount ELSE 0 END) AS deposits, " +
                    "SUM(CASE WHEN LOWER(type)='withdraw' THEN amount ELSE 0 END) AS withdrawals " +
                    "FROM transactions WHERE LOWER(username)=? " +
                    (!monthFilter.equals("All") ? " AND MONTHNAME(date)=? " : "") +
                    (!yearFilter.equals("All") ? " AND YEAR(date)=? " : "");

            pst = con.prepareStatement(totalSql);
            pst.setString(1, username.toLowerCase());
            idx = 2;
            if (!monthFilter.equals("All")) pst.setString(idx++, monthFilter);
            if (!yearFilter.equals("All")) pst.setString(idx, yearFilter);

            rs = pst.executeQuery();
            if (rs.next()) {
                totalDeposits = rs.getDouble("deposits");
                totalWithdrawals = rs.getDouble("withdrawals");
            }

            String monthSql = "SELECT MONTHNAME(date) AS month, SUM(amount) AS total FROM transactions " +
                    "WHERE LOWER(username)=? " +
                    (!monthFilter.equals("All") ? " AND MONTHNAME(date)=? " : "") +
                    (!yearFilter.equals("All") ? " AND YEAR(date)=? " : "") +
                    " GROUP BY MONTH(date), MONTHNAME(date) ORDER BY total DESC LIMIT 1";

            pst = con.prepareStatement(monthSql);
            pst.setString(1, username.toLowerCase());
            idx = 2;
            if (!monthFilter.equals("All")) pst.setString(idx++, monthFilter);
            if (!yearFilter.equals("All")) pst.setString(idx, yearFilter);

            rs = pst.executeQuery();
            if (rs.next()) mostActiveMonth = rs.getString("month");

            double netSavings = totalDeposits - totalWithdrawals;

            // Total deposits label
            JLabel totalDepositsLabel = createLabel(
                    "💵 Total Deposits: ₹" + String.format("%.2f", totalDeposits),
                    30, 20, 300, 25, infoPanel, 16
            );
            totalDepositsLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));

            // Total withdrawals label
            JLabel totalWithdrawalsLabel = createLabel(
                    "💸 Total Withdrawals: ₹" + String.format("%.2f", totalWithdrawals),
                    280, 20, 300, 25, infoPanel, 16
            );
            totalWithdrawalsLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));

            // Net savings label
            JLabel netSavingsLabel = createLabel(
                    "📈 Net Savings: ₹" + String.format("%.2f", netSavings),
                    540, 20, 300, 25, infoPanel, 16
            );
            netSavingsLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));

            // Most active month label
            JLabel mostActiveMonthLabel = createLabel(
                    "📅 Most Active Month: " + mostActiveMonth,
                    30, 55, 400, 25, infoPanel, 16
            );
            mostActiveMonthLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));

            String insightText = netSavings > 0 ? "💡 You saved ₹" + String.format("%.2f", netSavings) + "! Great job managing your funds."
                    : netSavings < 0 ? "⚠️ You spent more than you saved. Try to control withdrawals next month."
                    : "ℹ️ Your spending and savings are balanced this month.";

            // Insight label
            JLabel insight = createLabel(
                    insightText, 40, 510, 700, 25, analyticsPanel, 15
            );
            insight.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading analytics: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        Analysis object = new Analysis("Yash24");
    }
}
