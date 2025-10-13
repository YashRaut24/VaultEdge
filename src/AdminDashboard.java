import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

class AdminDashboard extends JFrame {

    // Creates labels
    private JLabel createLabel(String text, int x, int y, int width, int height, JPanel panel) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(200, 240, 255));
        label.setBounds(x, y, width, height);
        panel.add(label);
        return label;
    }

    // Creates TextFields
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

    // Creates buttons
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

    AdminDashboard() {

        // Database Credentials
        String url = EnvLoader.get("DB_URL");
        String user = EnvLoader.get("DB_USER");
        String password = EnvLoader.get("DB_PASSWORD");

        // AdminDashboardPanel
        JPanel adminDashboardPanel = new JPanel(null);
        adminDashboardPanel.setBackground(new Color(8, 20, 30));
        setContentPane(adminDashboardPanel);

        // Title label
        JLabel titleLabel = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 230, 255));
        titleLabel.setBounds(0, 20, 900, 40);
        adminDashboardPanel.add(titleLabel);

        // Minimum balance label
        createLabel("Min Balance:", 50, 80, 100, 25, adminDashboardPanel);

        // Minimum balance TestField
        JTextField minField = createTextField(160, 80, 150, 30, adminDashboardPanel);

        // Maximum balance label
        createLabel("Max Balance:", 350, 80, 100, 25, adminDashboardPanel);

        // Maximum balance TextField
        JTextField maxField = createTextField(460, 80, 150, 30, adminDashboardPanel);

        // Filter button
        JButton filterButton = createButton("Filter", 650, 80, 120, 30, adminDashboardPanel,
                new Color(0, 230, 255), Color.WHITE, new Color(0, 153, 76));

        // Back button
        JButton backButton = createButton("Back", 780, 500, 100, 35, adminDashboardPanel,
                new Color(0, 230, 255), Color.WHITE, new Color(255, 51, 51));

        backButton.addActionListener(e -> {
            new AdminLogin();
            dispose();
        });

        // User transactions table column names
        String[] columnNames = {"Username", "Balance", "Phone", "Email", "Gender", "WLimit"};

        // Data table which stores data
        DefaultTableModel transactionsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Transactions which shows ui for that data
        JTable transactionsTable = new JTable(transactionsTableModel);
        transactionsTable.getTableHeader().setReorderingAllowed(false);
        transactionsTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        transactionsTable.setRowHeight(25);
        transactionsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        transactionsTable.getTableHeader().setBackground(new Color(0, 230, 255));
        transactionsTable.getTableHeader().setForeground(Color.BLACK);
        transactionsTable.setGridColor(new Color(50, 50, 50));

        // Allows table scrolling
        JScrollPane transactionsTableScrollPane = new JScrollPane(transactionsTable);
        transactionsTableScrollPane.setBounds(50, 130, 830, 350);
        adminDashboardPanel.add(transactionsTableScrollPane);

        filterButton.addActionListener(a -> {
            transactionsTableModel.setRowCount(0);
            double min = minField.getText().isEmpty() ? 0 : Double.parseDouble(minField.getText());
            double max = maxField.getText().isEmpty() ? Double.MAX_VALUE : Double.parseDouble(maxField.getText());
            try (Connection con = DriverManager.getConnection(url, user, password)) {
                String sql = "SELECT * FROM users WHERE balance BETWEEN ? AND ?";
                try (PreparedStatement pst = con.prepareStatement(sql)) {
                    pst.setDouble(1, min);
                    pst.setDouble(2, max);
                    ResultSet rs = pst.executeQuery();
                    while (rs.next()) {
                        transactionsTableModel.addRow(new Object[]{
                                rs.getString("username"),
                                rs.getDouble("balance"),
                                rs.getString("phone"),
                                rs.getString("email"),
                                rs.getString("gender"),
                                rs.getDouble("wlimit")
                        });
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        });

        try (Connection con = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT * FROM users";
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                ResultSet rs = pst.executeQuery();
                while (rs.next()) {
                    transactionsTableModel.addRow(new Object[]{
                            rs.getString("username"),
                            rs.getDouble("balance"),
                            rs.getString("phone"),
                            rs.getString("email"),
                            rs.getString("gender"),
                            rs.getDouble("wlimit")
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

        setTitle("VaultEdge - Admin Dashboard");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        new AdminDashboard();
    }
}
