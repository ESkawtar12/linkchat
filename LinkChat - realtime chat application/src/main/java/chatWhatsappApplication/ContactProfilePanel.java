package chatWhatsappApplication;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

@SuppressWarnings("unused")
public class ContactProfilePanel extends JPanel {

    private JPanel mainPanel;
    private CardLayout cardLayout;
    private String contactName;
    private String contactEmail;
    private String imagePath;
    private String previousCardName;

    public ContactProfilePanel(JPanel mainPanel, CardLayout cardLayout, String contactName, String contactEmail,
                                String imagePath, String previousCardName) {
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;
        this.contactName = contactName;
        this.contactEmail = contactEmail;
        this.imagePath = imagePath;
        this.previousCardName = previousCardName;

        setLayout(new BorderLayout());
        setBackground(Constants.WH_BACKGROUND);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Constants.WH_GREEN);
        topPanel.setPreferredSize(new Dimension(getWidth(), 50));

        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backButtonPanel.setBackground(Constants.WH_GREEN);
        backButtonPanel.setPreferredSize(new Dimension(60, 50));

        JButton backButton = new JButton();
        backButton.setIcon(new ImageIcon(
                new ImageIcon("src/main/java/resources/angle-left.png").getImage()
                        .getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> {
            if (previousCardName != null && !previousCardName.isEmpty()) {
                cardLayout.show(mainPanel, previousCardName);
            } else {
                cardLayout.show(mainPanel, "ChatList");
            }
        });

        backButtonPanel.add(backButton);

        JLabel titleLabel = new JLabel("Informations du contact");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        JPanel rightSpacePanel = new JPanel();
        rightSpacePanel.setBackground(Constants.WH_GREEN);
        rightSpacePanel.setPreferredSize(new Dimension(60, 50));

        topPanel.add(backButtonPanel, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(rightSpacePanel, BorderLayout.EAST);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Constants.WH_BACKGROUND);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        ImageIcon profileIcon = new ImageIcon(
                new ImageIcon(imagePath).getImage()
                        .getScaledInstance(100, 100, Image.SCALE_SMOOTH));
        JLabel profileImage = new JLabel(profileIcon);
        profileImage.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(2, 1, 10, 10));
        infoPanel.setBackground(Constants.WH_BACKGROUND);
        infoPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        infoPanel.setMaximumSize(new Dimension(350, 100));
        infoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel namePanel = new JPanel(new BorderLayout(10, 0));
        namePanel.setBackground(Constants.WH_BACKGROUND);
        JLabel nameLabel = new JLabel("Nom :");
        JTextField nameField = new JTextField(contactName);
        nameField.setEditable(false);
        JButton editNameBtn = new JButton();

        editNameBtn.setIcon(new ImageIcon(
                new ImageIcon("src/main/java/resources/pen.png").getImage()
                        .getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
        editNameBtn.setBorderPainted(false);
        editNameBtn.setContentAreaFilled(false);
        editNameBtn.setFocusPainted(false);
        editNameBtn.addActionListener(e -> {
            nameField.setEditable(true);
            nameField.requestFocus();
            nameField.selectAll();
        });

        nameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String newName = nameField.getText();
                ContactProfilePanel.this.contactName = newName;
                if (ChatListPanel.getInstance() != null) {
                    ChatListPanel.getInstance().renameContact(previousCardName.replace("Discussion_", ""), newName);
                }
                Component comp = null;
                for (Component c : mainPanel.getComponents()) {
                    if (c.getName() != null && c.getName().equals(previousCardName)) {
                        comp = c;
                        break;
                    }
                }
                if (comp instanceof DiscussionDetail) {
                    ((DiscussionDetail) comp).updateContactName(newName);
                }
                nameField.setEditable(false);
            }
        });

        JPanel nameFieldPanel = new JPanel(new BorderLayout());
        nameFieldPanel.add(nameField, BorderLayout.CENTER);
        nameFieldPanel.add(editNameBtn, BorderLayout.EAST);
        namePanel.add(nameLabel, BorderLayout.WEST);
        namePanel.add(nameFieldPanel, BorderLayout.CENTER);

        JPanel emailPanel = new JPanel(new BorderLayout(10, 0));
        emailPanel.setBackground(Constants.WH_BACKGROUND);
        JLabel emailLabel = new JLabel("Email :");
        JLabel emailValue = new JLabel(contactEmail);
        emailPanel.add(emailLabel, BorderLayout.WEST);
        emailPanel.add(emailValue, BorderLayout.CENTER);

        infoPanel.add(namePanel);
        infoPanel.add(emailPanel);

        JButton deleteButton = new JButton("Supprimer le contact");
        deleteButton.setBackground(new Color(220, 53, 69));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        deleteButton.setMaximumSize(new Dimension(200, 40));
        deleteButton.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(
                    this,
                    "Êtes-vous sûr de vouloir supprimer ce contact ?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (response == JOptionPane.YES_OPTION) {
                cardLayout.show(mainPanel, "ChatList");
                JOptionPane.showMessageDialog(this,
                        "Contact supprimé avec succès",
                        "Suppression",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        contentPanel.add(Box.createVerticalGlue());
        contentPanel.add(profileImage);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(infoPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(deleteButton);
        contentPanel.add(Box.createVerticalGlue());

        add(topPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }
   

}
