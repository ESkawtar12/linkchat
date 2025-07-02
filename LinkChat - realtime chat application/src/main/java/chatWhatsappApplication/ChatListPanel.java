package chatWhatsappApplication;

import chatWhatsappApplication.service.AuthService;
import chatWhatsappApplication.service.ContactService;
import chatWhatsappApplication.service.DatabaseConnection;
import chatWhatsappApplication.service.MessageService;
import chatWhatsappApplication.client.ChatClient;
import chatWhatsappApplication.DiscussionTile;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChatListPanel extends JPanel {
    private static ChatListPanel instance;
    private final JPanel tileContainer;
    private final List<DiscussionTile> tiles = new ArrayList<>();
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final JPanel optionsPanel;
    private final ChatClient wsClient;
    private boolean optionsVisible = false;
    private final List<FriendInfo> allFriends = new ArrayList<>(); // Add this to store all friends

    public ChatListPanel(JPanel mainPanel, CardLayout cardLayout, ChatClient wsClient) {
        instance = this;
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;
        this.wsClient = wsClient;

        setLayout(new BorderLayout());
        setBackground(Constants.WH_BACKGROUND);

        // --- Top bar (recherche + bouton options) ---
        JPanel top = new JPanel(new BorderLayout(8, 0));
        top.setBackground(new Color(0xF0F2F5));
        top.setBorder(new MatteBorder(0, 0, 1, 0, new Color(0xE0E0E0)));

        JTextField search = new JTextField();
        search.putClientProperty("JTextField.placeholderText", "Rechercher ou démarrer une discussion");
        search.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        top.add(new JLabel(new ImageIcon("src/main/java/resources/search.png")), BorderLayout.WEST);
        top.add(search, BorderLayout.CENTER);

        // Add this listener for live filtering
        search.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterContacts(search.getText()); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterContacts(search.getText()); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterContacts(search.getText()); }
        });
        JButton btnOptions = new JButton(new ImageIcon(
            new ImageIcon("src/main/java/resources/more.png").getImage()
            .getScaledInstance(24, 24, Image.SCALE_SMOOTH)
        ));
        btnOptions.setBorderPainted(false);
        btnOptions.setContentAreaFilled(false);
        btnOptions.setFocusPainted(false);
        btnOptions.addActionListener(this::toggleOptionsPanel);
        top.add(btnOptions, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);

        // --- Options panel ---
        optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setBackground(new Color(240,240,240));
        optionsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0,1,0,0,Color.LIGHT_GRAY),
            new EmptyBorder(10,10,10,10)
        ));
        optionsPanel.setVisible(false);

        JButton btnVoirProfil = new JButton("VOIR Profil");
        styleOptionButton(btnVoirProfil);
        btnVoirProfil.addActionListener(e -> {
            cardLayout.show(mainPanel, "Profile");
            optionsPanel.setVisible(false);
        });

        JButton btnAjouter = new JButton("Ajouter un contact");
        styleOptionButton(btnAjouter);
        btnAjouter.addActionListener(e -> {
            optionsPanel.setVisible(false);
            showAddDialog(e);
        });

        optionsPanel.add(btnVoirProfil);
        optionsPanel.add(Box.createRigidArea(new Dimension(0,8)));
        optionsPanel.add(btnAjouter);
        add(optionsPanel, BorderLayout.WEST);

        // --- Container des tuiles de discussion ---
        tileContainer = new JPanel();
        tileContainer.setLayout(new BoxLayout(tileContainer, BoxLayout.Y_AXIS));
        tileContainer.setBorder(new EmptyBorder(8,0,8,0));
        tileContainer.setBackground(Constants.WH_BACKGROUND);
        add(new JScrollPane(tileContainer), BorderLayout.CENTER);

        // Chargement initial des contacts
        // loadFriends();
        wsClient.setOnlineStatusListener(this::updateOnlineStatus);
        wsClient.requestOnlineList(); // Force refresh after UI is ready
    }

    private void styleOptionButton(JButton btn) {
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setBackground(Constants.WH_GREEN);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
    }

    private void toggleOptionsPanel(ActionEvent e) {
        optionsVisible = !optionsVisible;
        optionsPanel.setVisible(optionsVisible);
        revalidate();
    }

    private void showAddDialog(ActionEvent e) {
        JTextField nomFld = new JTextField(20);
        JTextField emailFld = new JTextField(20);
        JPanel panel = new JPanel(new GridLayout(0,1));
        panel.add(new JLabel("Nom :"));    panel.add(nomFld);
        panel.add(new JLabel("Email :"));  panel.add(emailFld);

        int res = JOptionPane.showConfirmDialog(
            this, panel,
            "Ajouter un nouveau contact",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        if (res == JOptionPane.OK_OPTION) {
            String nom   = nomFld.getText().trim();
            String email = emailFld.getText().trim();
            if (nom.isEmpty() || !email.contains("@")) {
                JOptionPane.showMessageDialog(
                    this, "Nom ou email invalide",
                    "Erreur", JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            int userId = AuthService.getInstance().getCurrentUser().getId();
            boolean added = ContactService.getInstance().addFriendByEmail(userId, email);
            if (added) {
                loadFriends();
                JOptionPane.showMessageDialog(
                    this, "Contact ajouté !",
                    "Succès", JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                    this, "Impossible d'ajouter ce contact.",
                    "Erreur", JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private List<String> lastOnlineEmails = new ArrayList<>();

    private void updateOnlineStatus(List<String> onlineEmails) {
        lastOnlineEmails = onlineEmails;
         loadFriends();
    }

    // Helper class to store friend info
    private static class FriendInfo {
        String displayName, status, date, img, email;
        FriendInfo(String displayName, String status, String date, String img, String email) {
            this.displayName = displayName;
            this.status = status;
            this.date = date;
            this.img = img;
            this.email = email;
        }
    }

    // Update loadFriends to fill allFriends and filter
    public void loadFriends() {
        tiles.clear();
        tileContainer.removeAll();
        allFriends.clear(); // Clear before loading
        int userId = AuthService.getInstance().getCurrentUser().getId();

        String sql = """
            SELECT u.first_name,u.last_name,u.email,u.profile_image
            FROM friends f
            JOIN users u ON u.id = f.friend_id
            WHERE f.user_id = ?
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            java.util.Set<String> seenEmails = new java.util.HashSet<>();
            while (rs.next()) {
                String nom    = rs.getString("first_name") + " " + rs.getString("last_name");
                String email  = rs.getString("email");
                String img    = rs.getString("profile_image");
                if (seenEmails.add(email)) {
                    boolean isOnline = lastOnlineEmails.contains(email);
                    String status = isOnline ? "En ligne" : "Hors ligne";
                    int unreadCount = MessageService.getUnreadCount(email, AuthService.getInstance().getCurrentUser().getEmail());
                    String displayName = nom;
                    if (unreadCount > 0) {
                        displayName += " (" + unreadCount + ")";
                    }
                    allFriends.add(new FriendInfo(displayName, status, "Aujourd'hui", img, email));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        filterContacts(""); // Show all by default
        revalidate();
        repaint();
    }

    // New method to filter and display contacts
    private void filterContacts(String query) {
        tiles.clear();
        tileContainer.removeAll();
        String lower = query.toLowerCase();
        // Make a copy to avoid ConcurrentModificationException
        List<FriendInfo> friendsSnapshot = new ArrayList<>(allFriends);
        for (FriendInfo f : friendsSnapshot) {
            if (f.displayName.toLowerCase().contains(lower) || f.email.toLowerCase().contains(lower)) {
                addTile(f.displayName, f.status, f.date, f.img, f.email);
            }
        }
        revalidate();
        repaint();
    }

    private void addTile(String displayName, String status, String date, String img, String email) {
        // Create the avatar icon
        Icon avatar = new ImageIcon(
            new ImageIcon(img).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)
        );

        // Create a DiscussionTile instead of JPanel
        DiscussionTile tile = new DiscussionTile(
            displayName,         // name
            status,              // lastMessage or status
            date,                // date
            avatar,              // avatar icon
            mainPanel,           // mainPanel
            cardLayout,          // cardLayout
            email,               // email
            wsClient             // wsClient
        );

        tileContainer.add(tile);
        tiles.add(tile);
    }

    private void showContactMenu(Component parent, String contactEmail) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem deleteItem = new JMenuItem("Supprimer le contact");
        deleteItem.addActionListener(e -> {
            int userId = AuthService.getInstance().getCurrentUser().getId();
            boolean removed = chatWhatsappApplication.service.ContactService.getInstance()
                .removeFriendByEmail(userId, contactEmail);
            if (removed) {
                loadFriends();
                JOptionPane.showMessageDialog(this, "Contact supprimé !");
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        menu.add(deleteItem);
        menu.show(parent, 0, parent.getHeight());
    }

    public static ChatListPanel getInstance() {
        return instance;
    }

    public void renameContact(String email, String newName) {
        tiles.stream()
             .filter(t -> t.getEmail().equals(email))
             .findFirst()
             .ifPresent(t -> t.setContactName(newName));
    }
    
}
