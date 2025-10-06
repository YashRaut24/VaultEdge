import javax.swing.*;
import java.awt.*;
import java.sql.*;

class Profile extends JFrame {

    // Styled label
    private JLabel createLabel(String text, int x, int y, int width, int height, JPanel panel) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setForeground(new Color(200, 240, 255));
        label.setBounds(x, y, width, height);
        panel.add(label);
        return label;
    }

    // Styled text field
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

    // Styled combo box
    private JComboBox<String> createComboBox(String[] items, int x, int y, int width, int height, JPanel panel) {
        JComboBox<String> box = new JComboBox<>(items);
        box.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        box.setBackground(new Color(15, 30, 40));
        box.setForeground(new Color(220, 235, 245));
        box.setBounds(x, y, width, height);
        panel.add(box);
        return box;
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

    Profile(String username) {
        // Background panel
        JPanel backgroundPanel = new JPanel(null);
        backgroundPanel.setBackground(new Color(8, 20, 30));
        setContentPane(backgroundPanel);

        // Title
        JLabel title = new JLabel("Profile Settings", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(0, 230, 255));
        title.setBounds(0, 20, 800, 40);
        backgroundPanel.add(title);

        // Labels and inputs
        createLabel("Select Field to Update:", 200, 100, 200, 30, backgroundPanel);
        JComboBox<String> fieldBox = createComboBox(new String[]{"Username", "Password", "Phone", "Email"}, 400, 100, 200, 30, backgroundPanel);

        createLabel("Enter New Value:", 200, 160, 200, 30, backgroundPanel);
        JTextField newValueField = createTextField(400, 160, 200, 30, backgroundPanel);

        // Buttons
        JButton updateBtn = createButton("Update", 250, 220, 120, 42, backgroundPanel);
        JButton backBtn = createButton("Back", 400, 220, 120, 42, backgroundPanel);

        // Button actions
        updateBtn.addActionListener(a -> {
            String field = fieldBox.getSelectedItem().toString().toLowerCase();
            String newValue = newValueField.getText();
            if (newValue.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Value cannot be empty");
                return;
            }
            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/3dec", "root", "your_password")) {
                String sql = "UPDATE users SET " + field + " = ? WHERE username = ?";
                try (PreparedStatement pst = con.prepareStatement(sql)) {
                    pst.setString(1, newValue);
                    pst.setString(2, username);
                    pst.executeUpdate();
                    newValueField.setText("");
                    JOptionPane.showMessageDialog(null, "Updated successfully!");
                    // Refresh page if username changed
                    if (field.equals("username")) {
                        dispose();
                        new Profile(newValue);
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        });

        backBtn.addActionListener(a -> {
            new HomePage(username);
            dispose();
        });

        // Frame settings
        setTitle("VaultEdge - Profile Settings");
        setSize(800, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Profile("User");
    }
}
