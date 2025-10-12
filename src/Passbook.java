import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

class PassbookPage extends JFrame {

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

    PassbookPage(String username) {
        // Background panel
        JPanel backgroundPanel = new JPanel(null);
        backgroundPanel.setBackground(new Color(8, 20, 30));
        setContentPane(backgroundPanel);

        // Title
        JLabel title = new JLabel("Passbook", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(0, 230, 255));
        title.setBounds(0, 20, 800, 40);
        backgroundPanel.add(title);

        // Table setup
        String[] columnNames = {"Date & Time", "Description", "Type", "Amount (₹)", "Balance (₹)"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        table.getTableHeader().setBackground(new Color(0, 230, 255));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setGridColor(new Color(80, 80, 80));
        table.setBackground(new Color(15, 30, 40));
        table.setForeground(new Color(220, 235, 245));
        table.setSelectionBackground(new Color(0, 230, 255, 80));
        table.setSelectionForeground(Color.WHITE);

        // Center align columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(40, 80, 720, 350);
        scrollPane.getViewport().setBackground(new Color(15, 30, 40));
        backgroundPanel.add(scrollPane);

        // Buttons
        JButton refreshButton = createButton("Refresh", 220, 450, 160, 42, backgroundPanel);
        JButton backButton = createButton("Back", 420, 450, 160, 42, backgroundPanel);

        backButton.addActionListener(e -> {
            new HomePage(username);
            dispose();
        });

        refreshButton.addActionListener(e -> {
            loadTransactions(username, tableModel);
        });

        // Initial load
        loadTransactions(username, tableModel);

        // Frame settings
        setTitle("VaultEdge - Passbook");
        setSize(800, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void loadTransactions(String username, DefaultTableModel tableModel) {
        tableModel.setRowCount(0); // Clear existing rows

        String url = "jdbc:mysql://localhost:3306/3dec";
        try (Connection con = DriverManager.getConnection(url, "root", "your_password")) {
            String sql = "SELECT * FROM transactions WHERE username=? ORDER BY date DESC";
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setString(1, username);
                ResultSet rs = pst.executeQuery();

                while (rs.next()) {
                    String date = rs.getString("date");
                    String desc = rs.getString("description");
                    double amount = rs.getDouble("amount");
                    double balance = rs.getDouble("balance");

                    // Determine type based on description or amount sign
                    String type = desc.toLowerCase().contains("deposit") || desc.toLowerCase().contains("credit")
                            ? "Credit" : "Debit";

                    tableModel.addRow(new Object[]{date, desc, type, amount, balance});
                }
            }

            // Color coding (green = credit, red = debit)
            JTable tempTable = new JTable(tableModel) {
                @Override
                public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                    Component comp = super.prepareRenderer(renderer, row, column);
                    String type = (String) getValueAt(row, 2);
                    if (type.equalsIgnoreCase("Credit")) {
                        comp.setForeground(new Color(0, 255, 0));
                    } else if (type.equalsIgnoreCase("Debit")) {
                        comp.setForeground(new Color(255, 80, 80));
                    } else {
                        comp.setForeground(new Color(220, 235, 245));
                    }
                    return comp;
                }
            };

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public static void main(String[] args) {
        new PassbookPage("User");
    }
}
