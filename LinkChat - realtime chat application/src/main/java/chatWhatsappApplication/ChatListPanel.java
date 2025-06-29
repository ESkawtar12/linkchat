package chatWhatsappApplication;

import chatWhatsappApplication.service.AuthService;
import chatWhatsappApplication.service.ContactService;
import chatWhatsappApplication.service.DatabaseConnection;
import chatWhatsappApplication.client.ChatClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
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
        top.add(new JLabel(new ImageIcon("src/resources/search.png")), BorderLayout.WEST);
        top.add(search, BorderLayout.CENTER);

        JButton btnOptions = new JButton(new ImageIcon(
            new ImageIcon("src/resources/more.png").getImage()
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
        loadFriends();
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

    public void loadFriends() {
        tiles.clear();
        tileContainer.removeAll();
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
            while (rs.next()) {
                String nom    = rs.getString("first_name") + " " + rs.getString("last_name");
                String email  = rs.getString("email");
                String img    = rs.getString("profile_image");
                addTile(nom, "Connecté", "Aujourd'hui", img, email);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        revalidate();
        repaint();
    }

    private void addTile(String name, String msg, String date, String imgPath, String email) {
        ImageIcon icon = new ImageIcon(
            new ImageIcon(imgPath).getImage()
            .getScaledInstance(40,40,Image.SCALE_SMOOTH)
        );
        DiscussionTile tile = new DiscussionTile(name, msg, date, icon, mainPanel, cardLayout, email, wsClient);
        tiles.add(tile);
        tileContainer.add(tile);
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
