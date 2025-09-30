import javax.swing.*;
import java.awt.*;

class LandingPage extends JFrame {
    LandingPage() {
        Font f = new Font("futura", Font.BOLD, 40);
        Font f2 = new Font("Calibri", Font.PLAIN, 22);

        // Load the image and scale it to frame size
        ImageIcon original = new ImageIcon("images/LandingPageImage.jpg");
        Image scaled = original.getImage().getScaledInstance(800, 550, Image.SCALE_SMOOTH);
        ImageIcon bg = new ImageIcon(scaled);

        // Create a JLabel for background
        JLabel background = new JLabel(bg);
        background.setBounds(0, 0, 800, 550);
        background.setLayout(null); // allows adding components on top

        // Components
        JLabel l1 = new JLabel("Virtual Banking System", JLabel.CENTER);
        JButton b1 = new JButton("Admin");
        JButton b2 = new JButton("Existing Customer");
        JButton b3 = new JButton("New Customer");

        l1.setFont(f);
        b1.setFont(f2);
        b2.setFont(f2);
        b3.setFont(f2);

        // Set positions
        l1.setBounds(150, 50, 500, 50);
        b1.setBounds(300, 150, 200, 50);
        b2.setBounds(300, 230, 200, 50);
        b3.setBounds(300, 310, 200, 50);

        // Add components to background
        background.add(l1);
        background.add(b1);
        background.add(b2);
        background.add(b3);

        // Add background to frame
        add(background);

        // Button actions
        b1.addActionListener(a -> { new AdminLogin(); dispose(); });
        b2.addActionListener(a -> { new ExistingLoginPage(); dispose(); });
        b3.addActionListener(a -> { new NewloginPage(); dispose(); });

        setSize(700, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Landing Page");
        setVisible(true);
    }

    public static void main(String[] args) {
        new LandingPage();
    }
}
