package chatWhatsappApplication;

import chatWhatsappApplication.auth.LoginPanel;
import chatWhatsappApplication.auth.RegisterPanel;
import chatWhatsappApplication.model.User;
import chatWhatsappApplication.service.AuthService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.imageio.ImageIO;

@SuppressWarnings("unused")
public class ProfilePanel extends JPanel {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JTextField nameField;
    private JLabel emailValue;
    private JTextArea bioArea;
    private JLabel profileImage; // Store label in field for updates
    private boolean profileModified = false;
    private String originalName;
    private String originalBio;
    
    public ProfilePanel(JPanel mainPanel, CardLayout cardLayout) {
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;
        
        setLayout(new BorderLayout());
        setBackground(Constants.WH_BACKGROUND);
        
        // Créer un conteneur principal avec BorderLayout pour utiliser tout l'espace
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(Constants.WH_BACKGROUND);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0)); // Ajout d'une marge en bas
        
        // Panel du haut avec bouton retour
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setPreferredSize(new Dimension(450,60));
        topPanel.setBackground(Constants.WH_GREEN);
        
        // Bouton retour avec icône
        JButton backButton = new JButton(new ImageIcon(
                new ImageIcon("src/resources/angle-left.png").getImage()
                        .getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e-> {
            // Vérifier si des modifications ont été apportées avant de quitter
            if (profileModified) {
                JOptionPane.showMessageDialog(this, 
                    "Modifications enregistrées avec succès", 
                    "Succès", 
                    JOptionPane.INFORMATION_MESSAGE);
                profileModified = false;
            }
            cardLayout.show(mainPanel, "ChatList");
        });
        
        JLabel titleLabel = new JLabel("Profil");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        
        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Panel pour le contenu du profil
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Constants.WH_BACKGROUND);
        contentPanel.setBorder(new EmptyBorder(20, 20, 40, 20)); // Plus de marge en bas pour le bouton
        contentPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Définir une largeur maximale mais pas de hauteur maximale
        contentPanel.setMaximumSize(new Dimension(600, Integer.MAX_VALUE));
        contentPanel.setPreferredSize(new Dimension(450, 600));
        contentPanel.setMinimumSize(new Dimension(450, 400)); // Hauteur minimale pour assurer la visibilité du bouton
        
        // Créer un panel pour l'image de profil
        JPanel imageContainer = new JPanel(new BorderLayout());
        imageContainer.setBackground(Constants.WH_BACKGROUND);
        
        // Charger l'image de profil par défaut
        String defaultImagePath = "src/resources/user.png";
        profileImage = createCircularAvatar(defaultImagePath);
        profileImage.setHorizontalAlignment(JLabel.CENTER);
        
        // Créer un panel pour centrer l'image
        JPanel imageCenterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        imageCenterPanel.setBackground(Constants.WH_BACKGROUND);
        imageCenterPanel.add(profileImage);
        
        // Bouton pour changer l'image
        JButton changeImageBtn = new JButton("Changer l'image");
        changeImageBtn.setBackground(Constants.WH_GREEN);
        changeImageBtn.setForeground(Color.WHITE);
        changeImageBtn.addActionListener(e-> changeProfileImage());
        changeImageBtn.setMargin(new Insets(5, 20, 5, 20));
        
        // Créer un panel pour le bouton
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Constants.WH_BACKGROUND);
        buttonPanel.add(changeImageBtn);
        
        // Ajouter les composants au conteneur principal
        imageContainer.add(imageCenterPanel, BorderLayout.CENTER);
        imageContainer.add(buttonPanel, BorderLayout.SOUTH);
        
        // Ajouter un espacement
        imageContainer.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Ajouter le conteneur d'image au panneau de contenu
        contentPanel.add(imageContainer);
        
        // Ajouter un espacement
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Panel pour les informations
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Constants.WH_BACKGROUND);
        infoPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        infoPanel.setMaximumSize(new Dimension(500, 250));
        infoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Style commun pour les labels
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);
        int fieldHeight = 35;
        int fieldMargin = 10;
        
        // Panel pour le nom (avec bouton d'édition)
        JPanel namePanel = new JPanel(new BorderLayout(10, 0));
        namePanel.setBackground(Constants.WH_BACKGROUND);
        namePanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 15, 0));
        
        // Récupérer le nom complet de l'utilisateur connecté ou utiliser "Utilisateur" par défaut
        String userName = "Utilisateur";
        if (AuthService.getInstance().getCurrentUser() != null) {
            User currentUser = AuthService.getInstance().getCurrentUser();
            userName = currentUser.getFirstName() + " " + currentUser.getLastName();
        }
        
        // Créer le panneau pour le label et le champ
        JPanel nameFieldContainer = new JPanel(new BorderLayout(10, 0));
        nameFieldContainer.setBackground(Constants.WH_BACKGROUND);
        
        // Ajouter le label
        JLabel nameLabel = new JLabel("Nom :");
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameLabel.setPreferredSize(new Dimension(80, 30));
        nameFieldContainer.add(nameLabel, BorderLayout.WEST);
        
        // Créer le champ de texte
        nameField = new JTextField(userName);
        nameField.setFont(fieldFont);
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        nameField.setPreferredSize(new Dimension(250, 35));
        originalName = nameField.getText();
        nameField.setEditable(false);
        
        // Ajouter un écouteur pour détecter les modifications
        nameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { checkNameModified(); }
            @Override public void removeUpdate(DocumentEvent e) { checkNameModified(); }
            @Override public void changedUpdate(DocumentEvent e) { checkNameModified(); }
            private void checkNameModified() {
                if (!nameField.getText().equals(originalName)) {
                    profileModified = true;
                }
            }
        });
        
        // Ajouter un écouteur de focus pour sauvegarder quand on quitte le champ
        nameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                saveProfile();
                nameField.setEditable(false);
            }
        });
        
        // Créer un panneau pour le champ et le bouton d'édition
        JPanel fieldAndButtonPanel = new JPanel(new BorderLayout(5, 0));
        fieldAndButtonPanel.setBackground(Constants.WH_BACKGROUND);
        fieldAndButtonPanel.add(nameField, BorderLayout.CENTER);
        
        // Ajouter le bouton d'édition
        JButton editNameBtn = createEditButton();
        editNameBtn.addActionListener(e -> editName());
        fieldAndButtonPanel.add(editNameBtn, BorderLayout.EAST);
        
        // Ajouter le panneau de champ et bouton au conteneur
        nameFieldContainer.add(fieldAndButtonPanel, BorderLayout.CENTER);
        namePanel.add(nameFieldContainer, BorderLayout.CENTER);
        
        // Panel pour l'email (non modifiable)
        JPanel emailPanel = new JPanel(new BorderLayout(10, 0));
        emailPanel.setBackground(Constants.WH_BACKGROUND);
        emailPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 15, 0));
        
        // Créer le panneau pour le label et le champ email
        JPanel emailFieldContainer = new JPanel(new BorderLayout(10, 0));
        emailFieldContainer.setBackground(Constants.WH_BACKGROUND);
        
        // Ajouter le label
        JLabel emailLabel = new JLabel("Email :");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailLabel.setPreferredSize(new Dimension(80, 30));
        emailFieldContainer.add(emailLabel, BorderLayout.WEST);
        
        // Récupérer l'email de l'utilisateur connecté
        String userEmail = AuthService.getInstance().getCurrentUser() != null ? 
                         AuthService.getInstance().getCurrentUser().getEmail() : "utilisateur@email.com";
        emailValue = new JLabel(userEmail);
        emailValue.setFont(fieldFont);
        emailValue.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        // Créer un panneau pour le champ email
        JPanel emailValuePanel = new JPanel(new BorderLayout());
        emailValuePanel.setBackground(Constants.WH_BACKGROUND);
        emailValuePanel.add(emailValue, BorderLayout.WEST);
        
        // Ajouter le champ email au conteneur
        emailFieldContainer.add(emailValuePanel, BorderLayout.CENTER);
        emailPanel.add(emailFieldContainer, BorderLayout.CENTER);
        
        // Panel pour la bio (avec bouton d'édition)
        JPanel bioPanel = new JPanel(new BorderLayout(10, 0));
        bioPanel.setBackground(Constants.WH_BACKGROUND);
        bioPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 15, 0));
        
        // Créer le panneau pour le label et la zone de texte
        JPanel bioContentContainer = new JPanel(new BorderLayout(10, 5));
        bioContentContainer.setBackground(Constants.WH_BACKGROUND);
        
        // Ajouter le label
        JLabel bioLabel = new JLabel("Bio :");
        bioLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        bioLabel.setPreferredSize(new Dimension(80, 30));
        bioLabel.setVerticalAlignment(SwingConstants.TOP);
        
        // Créer un panneau pour le label et le contenu de la bio
        JPanel bioContentPanel = new JPanel(new BorderLayout(10, 0));
        bioContentPanel.setBackground(Constants.WH_BACKGROUND);
        
        // Créer la zone de texte pour la bio
        bioArea = new JTextArea("Disponible", 3, 20);
        bioArea.setFont(fieldFont);
        bioArea.setLineWrap(true);
        bioArea.setWrapStyleWord(true);
        bioArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        bioArea.setEditable(false);
        originalBio = bioArea.getText();
        
        // Ajouter un écouteur pour détecter les modifications
        bioArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { checkBioModified(); }
            @Override public void removeUpdate(DocumentEvent e) { checkBioModified(); }
            @Override public void changedUpdate(DocumentEvent e) { checkBioModified(); }
            private void checkBioModified() {
                if (!bioArea.getText().equals(originalBio)) {
                    profileModified = true;
                }
            }
        });
        
        // Ajouter un écouteur de focus pour sauvegarder quand on quitte le champ
        bioArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                saveProfile();
                bioArea.setEditable(false);
            }
        });
        
        // Créer un panneau pour la zone de défilement et le bouton
        JPanel bioScrollPanel = new JPanel(new BorderLayout());
        bioScrollPanel.setBackground(Constants.WH_BACKGROUND);
        
        JScrollPane bioScroll = new JScrollPane(bioArea);
        bioScroll.setBorder(null);
        bioScroll.setPreferredSize(new Dimension(250, 80));
        
        // Ajouter le bouton d'édition
        JButton editBioBtn = createEditButton();
        editBioBtn.addActionListener(e -> editBio());
        
        // Créer un panneau pour le bouton d'édition de la bio
        JPanel bioButtonPanel = new JPanel(new BorderLayout());
        bioButtonPanel.setBackground(Constants.WH_BACKGROUND);
        bioButtonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        bioButtonPanel.add(editBioBtn, BorderLayout.NORTH);
        
        // Ajouter les composants au panneau de défilement
        bioScrollPanel.add(bioScroll, BorderLayout.CENTER);
        bioScrollPanel.add(bioButtonPanel, BorderLayout.EAST);
        
        // Ajouter les composants au panneau de contenu
        bioContentPanel.add(bioScrollPanel, BorderLayout.CENTER);
        
        // Ajouter le label et le contenu au conteneur principal
        bioContentContainer.add(bioLabel, BorderLayout.WEST);
        bioContentContainer.add(bioContentPanel, BorderLayout.CENTER);
        
        // Ajouter le conteneur principal au panneau bio
        bioPanel.add(bioContentContainer, BorderLayout.CENTER);
        
        // Ajout des panels au panel d'information
        infoPanel.add(namePanel);
        infoPanel.add(emailPanel);
        infoPanel.add(bioPanel);
        
        // Bouton de déconnexion
        JButton logoutButton = new JButton("Déconnexion");
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setMaximumSize(new Dimension(250, 45));
        logoutButton.setPreferredSize(new Dimension(250, 45));
        logoutButton.setMinimumSize(new Dimension(250, 45));
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setOpaque(true);
        logoutButton.setFont(logoutButton.getFont().deriveFont(Font.BOLD, 14f));
        logoutButton.addActionListener(e -> logout());
        
        // Ajouter un effet de survol
        logoutButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logoutButton.setBackground(new Color(200, 35, 51));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                logoutButton.setBackground(new Color(220, 53, 69));
            }
        });
        
        // Créer un conteneur pour le contenu principal avec un espacement vertical
        JPanel contentContainer = new JPanel();
        contentContainer.setLayout(new BoxLayout(contentContainer, BoxLayout.Y_AXIS));
        contentContainer.setBackground(Constants.WH_BACKGROUND);
        contentContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Groupe principal pour centrer verticalement
        JPanel centerGroup = new JPanel();
        centerGroup.setLayout(new BoxLayout(centerGroup, BoxLayout.Y_AXIS));
        centerGroup.setBackground(Constants.WH_BACKGROUND);
        centerGroup.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Conteneur pour l'image de profil
        JPanel imageWrapper = new JPanel();
        imageWrapper.setLayout(new BoxLayout(imageWrapper, BoxLayout.Y_AXIS));
        imageWrapper.setBackground(Constants.WH_BACKGROUND);
        imageWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
        profileImage.setAlignmentX(Component.CENTER_ALIGNMENT);
        imageWrapper.add(profileImage);
        imageWrapper.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Conteneur pour le bouton de changement d'image
        JPanel buttonContainer = new JPanel();
        buttonContainer.setBackground(Constants.WH_BACKGROUND);
        changeImageBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonContainer.add(changeImageBtn);
        
        // Conteneur pour les informations
        JPanel infoWrapper = new JPanel();
        infoWrapper.setLayout(new BoxLayout(infoWrapper, BoxLayout.Y_AXIS));
        infoWrapper.setBackground(Constants.WH_BACKGROUND);
        infoWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoWrapper.add(infoPanel);
        
        // Conteneur pour le bouton de déconnexion
        JPanel logoutContainer = new JPanel();
        logoutContainer.setBackground(Constants.WH_BACKGROUND);
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutContainer.add(logoutButton);
        
        // Ajout des composants au groupe central
        centerGroup.add(imageWrapper);
        centerGroup.add(buttonContainer);
        centerGroup.add(Box.createRigidArea(new Dimension(0, 20)));
        centerGroup.add(infoWrapper);
        centerGroup.add(Box.createRigidArea(new Dimension(0, 20)));
        centerGroup.add(logoutContainer);
        
        // Ajout du groupe central au conteneur principal
        contentContainer.add(centerGroup);
        
        // Ajouter le conteneur de contenu au panneau principal
        contentPanel.add(contentContainer);
        
        // Créer un conteneur principal avec un BorderLayout
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(Constants.WH_BACKGROUND);
        
        // Créer un conteneur pour le contenu défilable
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Constants.WH_BACKGROUND);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Vitesse de défilement plus fluide
        
        // Désactiver la barre de défilement horizontale
        scrollPane.setPreferredSize(new Dimension(450, 600)); // Taille par défaut
        
        // Ajouter le contenu défilable au wrapper principal
        contentWrapper.add(scrollPane, BorderLayout.CENTER);
        
        // Ajouter une marge en bas pour le bouton de déconnexion
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Ajouter les panels au conteneur principal
        mainContainer.add(contentWrapper, BorderLayout.CENTER);
        
        // Ajouter les panels au panel principal
        add(topPanel, BorderLayout.NORTH);
        add(mainContainer, BorderLayout.CENTER);
    }
    
    private void editName() {
        nameField.setEditable(true);
        nameField.requestFocus();
        nameField.selectAll();
    }
    
    private void editBio() {
        bioArea.setEditable(true);
        bioArea.requestFocus();
        bioArea.selectAll();
    }
    
    private void changeProfileImage() {
        // Vérifier les permissions du dossier resources
        File resourcesDir = new File("src/resources");
        System.out.println("Chemin du dossier resources : " + resourcesDir.getAbsolutePath());
        System.out.println("Le dossier resources existe : " + resourcesDir.exists());
        System.out.println("Le dossier est accessible en écriture : " + resourcesDir.canWrite());
        
        // Créer le dossier resources s'il n'existe pas
        if (!resourcesDir.exists()) {
            boolean created = resourcesDir.mkdirs();
            System.out.println("Dossier resources créé : " + created);
            if (!created) {
                JOptionPane.showMessageDialog(this, 
                    "Impossible de créer le dossier de ressources. Vérifiez les permissions.", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Sélectionner une image de profil");
        
        // Filtrer pour n'afficher que les fichiers image
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Images", "png", "jpg", "jpeg", "gif"));
        
        int result = fileChooser.showOpenDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                // Vérifier si le fichier est une image valide
                if (isImage(selectedFile)) {
                    // Utiliser le dossier resources déjà vérifié
                    
                    // Créer une copie de l'image dans le dossier de ressources
                    String destPath = "src/resources/user_" + System.currentTimeMillis() + ".png";
                    File destFile = new File(destPath);
                    
                    System.out.println("Tentative de copie vers : " + destFile.getAbsolutePath());
                    
                    // Copier le fichier
                    Files.copy(selectedFile.toPath(), destFile.toPath(), 
                             StandardCopyOption.REPLACE_EXISTING);
                             
                    System.out.println("Fichier copié avec succès. Taille : " + destFile.length() + " octets");
                    
                    // Mettre à jour l'image de profil
                    JLabel newImage = createCircularAvatar(destPath);
                    
                    // Remplacer l'ancienne image par la nouvelle
                    profileImage.setIcon(newImage.getIcon());
                    
                    // Forcer la mise à jour de l'affichage
                    profileImage.revalidate();
                    profileImage.repaint();

                    // Mettre à jour le champ profile_image dans la base de données
                    User currentUser = AuthService.getInstance().getCurrentUser();
                    if (currentUser != null) {
                        currentUser.setProfileImage(destPath); // Si votre User a ce setter
                        try (java.sql.Connection conn = chatWhatsappApplication.service.DatabaseConnection.getConnection();
                            java.sql.PreparedStatement stmt = conn.prepareStatement(
                                "UPDATE users SET profile_image = ? WHERE email = ?")) {
                            stmt.setString(1, destPath);
                            stmt.setString(2, currentUser.getEmail());
                            stmt.executeUpdate();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(this,
                                "Erreur lors de la mise à jour de la photo de profil en base de données.",
                                "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    
                    // Marquer le profil comme modifié
                    profileModified = true;
                    saveProfile();
                    
                    JOptionPane.showMessageDialog(this, 
                        "Photo de profil mise à jour avec succès", 
                        "Succès", 
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Le fichier sélectionné n'est pas une image valide", 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors du chargement de l'image: " + e.getMessage(), 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Méthode utilitaire pour vérifier si un fichier est une image valide
    private boolean isImage(File file) {
        try {
            Image image = ImageIO.read(file);
            return image != null;
        } catch (IOException e) {
            return false;
        }
    }
    
    private JLabel createCircularAvatar(String path) {
        try {
            System.out.println("Chargement de l'image depuis : " + path);
            
            // Charger l'image
            ImageIcon originalIcon;
            File file = new File(path);
            
            if (file.exists()) {
                originalIcon = new ImageIcon(file.getAbsolutePath());
            } else {
                // Essayer de charger depuis les ressources si le fichier n'existe pas
                URL res = getClass().getClassLoader().getResource("resources/" + path);
                if (res != null) {
                    originalIcon = new ImageIcon(res);
                } else {
                    // Si l'image n'est pas trouvée, utiliser une image par défaut
                    return createDefaultAvatar();
                }
            }
            
            // Vérifier si l'image a été chargée correctement
            if (originalIcon.getIconWidth() <= 0) {
                System.err.println("Largeur de l'image invalide");
                return createDefaultAvatar();
            }
            
            // Créer une image circulaire
            int size = Math.min(originalIcon.getIconWidth(), originalIcon.getIconHeight());
            BufferedImage circleImg = new BufferedImage(120, 120, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = circleImg.createGraphics();
            
            // Activer l'antialiasing pour un meilleur rendu
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Créer un masque circulaire
            g2.setClip(new Ellipse2D.Float(0, 0, 120, 120));
            
            // Dessiner l'image redimensionnée
            g2.drawImage(originalIcon.getImage(), 0, 0, 120, 120, null);
            g2.dispose();
            
            // Créer un JLabel avec l'image circulaire
            JLabel label = new JLabel(new ImageIcon(circleImg));
            label.setHorizontalAlignment(JLabel.CENTER);
            return label;
            
        } catch (Exception e) {
            System.err.println("Erreur dans createCircularAvatar: " + e.getMessage());
            e.printStackTrace();
            return createDefaultAvatar();
        }
    }
    
    private JLabel createDefaultAvatar() {
        // Créer une image par défaut
        BufferedImage img = new BufferedImage(120, 120, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        
        // Activer l'antialiasing
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Dessiner un cercle gris
        g2.setColor(new Color(230, 230, 230));
        g2.fillOval(0, 0, 120, 120);
        
        // Ajouter une bordure
        g2.setColor(Constants.WH_GREEN);
        g2.setStroke(new BasicStroke(3));
        g2.drawOval(1, 1, 116, 116);
        
        // Ajouter une icône d'utilisateur
        g2.setFont(new Font("Arial", Font.BOLD, 48));
        g2.setColor(Color.GRAY);
        
        // Centrer le texte
        String text = "U";
        FontMetrics fm = g2.getFontMetrics();
        int x = (120 - fm.stringWidth(text)) / 2;
        int y = ((120 - fm.getHeight()) / 2) + fm.getAscent();
        g2.drawString(text, x, y);
        
        g2.dispose();
        
        JLabel label = new JLabel(new ImageIcon(img));
        label.setHorizontalAlignment(JLabel.CENTER);
        return label;
    }
    
    // Méthode utilitaire pour créer un panneau de champ de formulaire avec le label à côté
    private JPanel createFormFieldPanel(String labelText) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(Constants.WH_BACKGROUND);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setPreferredSize(new Dimension(80, 30)); // Largeur fixe pour aligner les champs
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        
        panel.add(label, BorderLayout.WEST);
        return panel;
    }
    
    // Méthode utilitaire pour créer un bouton d'édition
    private JButton createEditButton() {
        JButton button = new JButton();
        button.setIcon(new ImageIcon(
            new ImageIcon("src/resources/pen.png").getImage()
                    .getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMargin(new Insets(0, 5, 0, 0));
        return button;
    }
    
    private void saveProfile() {
        // Ici, vous pourriez sauvegarder les informations dans une base de données
        // ou dans un fichier de configuration
        
        // Pour l'instant, on se contente d'afficher un message dans la console
        System.out.println("Profil sauvegardé :");
        System.out.println("Nom : " + nameField.getText());
        System.out.println("Bio : " + bioArea.getText());
        
        // Mettre à jour les valeurs originales
        originalName = nameField.getText();
        originalBio = bioArea.getText();
        
        // Réinitialiser le drapeau de modification
        profileModified = false;
    }
    
    private void logout() {
        int response = JOptionPane.showConfirmDialog(
                this,
                "Êtes-vous sûr de vouloir vous déconnecter ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        
        if (response == JOptionPane.YES_OPTION) {
            // Réinitialiser l'état des modifications
            profileModified = false;
            // Appeler la méthode de déconnexion de la classe Main
            Main.logout();
        }
    }
    
    public void resetModificationState() {
        profileModified = false;
        originalName = nameField.getText();
        originalBio = bioArea.getText();
    }
}