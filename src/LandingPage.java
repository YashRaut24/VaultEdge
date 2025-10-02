import javax.swing.*;
import java.awt.*;

class LandingPage extends JFrame {

    // Creates feature cards
    private JPanel createFeatureCard(String iconEmoji, String titleText, String descriptionText) {
        // Card panel
        JPanel cardPanel = new JPanel(null);
        cardPanel.setOpaque(true);
        cardPanel.setBackground(new Color(10, 25, 35, 180));
        cardPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        cardPanel.setPreferredSize(new Dimension(400, 90));

        // Icon label
        JLabel iconLabel = new JLabel(iconEmoji);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        iconLabel.setForeground(new Color(0, 230, 255));
        iconLabel.setBounds(15, 20, 50, 50);
        cardPanel.add(iconLabel);

        // Title label
        JLabel titleLabel = new JLabel(titleText);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 230, 255));
        titleLabel.setBounds(80, 10, 300, 25);
        cardPanel.add(titleLabel);

        // Description Label
        JLabel descriptionLabel = new JLabel("<html><body style='width:300px;'>" + descriptionText + "</body></html>");
        descriptionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descriptionLabel.setForeground(new Color(200, 240, 255));
        descriptionLabel.setBounds(80, 35, 300, 40);
        cardPanel.add(descriptionLabel);

        return cardPanel;
    }

    // Creates buttons
    private JButton createButton(String text) {
        // Button
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 18));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(new Color(0, 230, 255), 3));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    LandingPage() {

        // Background image panel
        JPanel backgroundImagePanel = new JPanel(null) {
            Image bg = new ImageIcon("images/LandingPageImage.jpg").getImage();
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundImagePanel.setBounds(0, 0, 900, 600);
        setContentPane(backgroundImagePanel);

        // Title label
        JLabel title = new JLabel("VaultEdge", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 46));
        title.setForeground(new Color(0, 230, 255)); // bright cyan
        title.setBounds(0, 20, 900, 55);
        backgroundImagePanel.add(title);

        // Welcome label
        JLabel welcome = new JLabel("Welcome to VaultEdge – Your Virtual Banking Assistant", SwingConstants.CENTER);
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        welcome.setForeground(new Color(200, 240, 255)); // light cyan
        welcome.setBounds(0, 80, 900, 30);
        backgroundImagePanel.add(welcome);

        // About button
        JButton aboutButton = createButton("About");
        aboutButton.setBounds(775, 25, 90, 35);
        backgroundImagePanel.add(aboutButton);

        aboutButton.addActionListener(a->JOptionPane.showMessageDialog(this,"About VaultEdge"));

        // Signup button
        JButton singupButton = createButton("Signup");
        singupButton.setBounds(25, 494, 200, 50);
        backgroundImagePanel.add(singupButton);

        singupButton.addActionListener(a -> {
            new NewloginPage();
            dispose();
        });

        // Login button
        JButton loginButton = createButton("Login");
        loginButton.setBounds(340, 494, 200, 50);
        backgroundImagePanel.add(loginButton);

        loginButton.addActionListener(a -> {
            new ExistingLoginPage();
            dispose();
        });

        // Admin button
        JButton adminButton = createButton("Admin");
        adminButton.setBounds(660, 494, 200, 50);
        backgroundImagePanel.add(adminButton);

        adminButton.addActionListener(a -> {
            new AdminLogin();
            dispose();
        });

        // Feature1 panel
        JPanel feature1Panel = createFeatureCard("💳", "Instant Fund Transfer", "Quickly and securely transfer money.");
        feature1Panel.setBounds(25, 135, 400, 90);
        backgroundImagePanel.add(feature1Panel);

        // Feature2 panel
        JPanel feature2Panel = createFeatureCard("📊", "Smart Dashboard", "View balance, transactions, and account details.");
        feature2Panel.setBounds(25, 250, 400, 90);
        backgroundImagePanel.add(feature2Panel);

        // Feature3 panel
        JPanel feature3Panel = createFeatureCard("🔒", "Secure Login & Authentication", "Safe access with password validation.");
        feature3Panel.setBounds(450, 135, 410, 90);
        backgroundImagePanel.add(feature3Panel);

        // Feature4 panel
        JPanel feature4Panel = createFeatureCard("📝", "Transaction History", "Browse past deposits, withdrawals, and transfers.");
        feature4Panel.setBounds(450, 250, 410, 90);
        backgroundImagePanel.add(feature4Panel);

        // Stat1 label
        JLabel stats1Label = new JLabel("👥 Serving 1200+ Virtual Customers");
        stats1Label.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
        stats1Label.setForeground(Color.WHITE); // cyan
        stats1Label.setBounds(50, 370, 400, 30);
        backgroundImagePanel.add(stats1Label);

        // Stat2 label
        JLabel stats2Label = new JLabel("✅ Processed 10,000+ Transactions Securely");
        stats2Label.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
        stats2Label.setForeground(Color.WHITE); // cyan
        stats2Label.setBounds(450, 370, 450, 30);
        backgroundImagePanel.add(stats2Label);

        // Frame settings
        setTitle("VaultEdge");
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(null);
    }

    public static void main(String[] args) {
        LandingPage object1 = new LandingPage();
    }
}