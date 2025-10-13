import javax.swing.*;
import java.awt.*;

class AdminPage extends JFrame {

    // Styled button
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

    // Styled label
    private JLabel createLabel(String text, int x, int y, int width, int height, JPanel panel, int fontSize, boolean bold) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", bold ? Font.BOLD : Font.PLAIN, fontSize));
        label.setForeground(new Color(0, 230, 255));
        label.setBounds(x, y, width, height);
        panel.add(label);
        return label;
    }

    AdminPage() {

        JPanel backgroundPanel = new JPanel(null);
        backgroundPanel.setBackground(new Color(8, 20, 30));
        setContentPane(backgroundPanel);

        // Title
        createLabel("Welcome Admin", 0, 30, 800, 50, backgroundPanel, 32, true);

        // Status label (for messages)
        JLabel statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setForeground(new Color(200, 240, 255));
        statusLabel.setBounds(250, 220, 300, 30);
        backgroundPanel.add(statusLabel);

        // Buttons
        JButton showUsersButton = createButton("Show All Users", 250, 100, 300, 40, backgroundPanel,
                new Color(0, 230, 255), Color.WHITE, new Color(0, 153, 76));

        JButton logoutButton = createButton("Logout", 250, 160, 300, 40, backgroundPanel,
                new Color(0, 230, 255), Color.WHITE, new Color(255, 51, 51));

        logoutButton.addActionListener(e -> {
            new AdminLogin();
            dispose();
        });

        showUsersButton.addActionListener(e -> {
            new AdminDashboard("Admin"); // Opens the styled dashboard
            dispose();
        });

        // Frame settings
        setTitle("VaultEdge - Admin Page");
        setSize(800, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        new AdminPage();
    }
}
