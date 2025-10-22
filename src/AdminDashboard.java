import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

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

    public AdminDashboard(String username) {

        // Colors
        Color backgroundColor = new Color(8, 20, 30);
        Color sidebarColor = new Color(10, 25, 40);
        Color cyan = new Color(0, 230, 255);

        // Top Panel
        JPanel topPanel = new JPanel(null);
        topPanel.setBackground(sidebarColor);
        topPanel.setBounds(0, 0, 900, 60);
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, cyan));
        add(topPanel);
        // VaultEdge (left)
        JLabel titleLeft = createLabel("VaultEdge", 25, 15, 200, 30, topPanel, 22, cyan);

        // Admin Dashboard (center)
        JLabel titleCenter = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        titleCenter.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleCenter.setForeground(Color.WHITE);
        titleCenter.setBounds(280, 15, 350, 30);
        topPanel.add(titleCenter);

        // Welcome Admin label
        JLabel welcomeLabel = createLabel("Welcome, Admin", 720, 18, 200, 25, topPanel, 15, cyan);

        // Side panel
        JPanel sidePanel = new JPanel(null);
        sidePanel.setBackground(backgroundColor);
        sidePanel.setBounds(0, 60, 200, 540);
        sidePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, cyan));
        add(sidePanel);

        // Overview button
        JButton overviewBtn = createButton("Overview", 0, 0, 199, 40, sidePanel);

        // Users button
        JButton usersButton = createButton("Users", 0, 40, 199, 40, sidePanel);

        // TransactionButton
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

        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(new Color(12, 25, 38));
        contentPanel.setBounds(200, 60, 700, 540);
        contentPanel.setLayout(null);
        add(contentPanel);
        
        // Frame settings
        setTitle("VaultEdge Admin Dashboard");
        setVisible(true);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
    }

    public static void main(String[] args) {
        new AdminDashboard("Admin");
    }
}
