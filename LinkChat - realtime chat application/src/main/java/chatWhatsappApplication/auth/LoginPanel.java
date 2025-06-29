package chatWhatsappApplication.auth;

import chatWhatsappApplication.Main;
import chatWhatsappApplication.service.AuthService;
import chatWhatsappApplication.Constants;

import javax.swing.*;
import java.awt.*;


@SuppressWarnings("unused")
public class LoginPanel extends JPanel {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel messageLabel;
    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    public LoginPanel(JPanel mainPanel, CardLayout cardLayout) {
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;
        initializeUI();
    }
    
    /**
     * Définit l'email dans le champ de connexion
     * @param email L'email à afficher dans le champ
     */


    private void initializeUI() {
        setLayout(new GridBagLayout());
        setBackground(Constants.WH_BACKGROUND);

        // Conteneur principal avec GridBagLayout pour un centrage parfait
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        centerPanel.setBackground(Constants.WH_BACKGROUND);
        centerPanel.setMaximumSize(new Dimension(400, Integer.MAX_VALUE));

        // Titre
        JLabel titleLabel = new JLabel("Connexion");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        // Panel du formulaire
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Constants.WH_BACKGROUND);
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.setMaximumSize(new Dimension(350, Integer.MAX_VALUE));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Champs de formulaire
        emailField = new JTextField(20);
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        passwordField = new JPasswordField(20);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // Boutons
        loginButton = new JButton("Se connecter");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setBackground(Constants.WH_GREEN);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setOpaque(true);
        loginButton.setPreferredSize(new Dimension(200, 40));
        loginButton.setMaximumSize(new Dimension(200, 40));
        
        registerButton = new JButton("Créer un compte");
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setBorderPainted(false);
        registerButton.setFocusPainted(false);
        registerButton.setContentAreaFilled(false);
        registerButton.setForeground(Constants.WH_GREEN);
        registerButton.setOpaque(false);

        // Message d'erreur
        messageLabel = new JLabel(" ");
        messageLabel.setForeground(Color.RED);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Ajout des composants au formulaire
        formPanel.add(createFormField("Email", emailField));
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(createFormField("Mot de passe", passwordField));
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(loginButton);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(registerButton);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(messageLabel);

        // Ajout des composants au panel principal
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(formPanel);
        centerPanel.add(Box.createVerticalGlue());
        
        // Création d'un conteneur intermédiaire pour le centrage
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(Constants.WH_BACKGROUND);
        wrapper.add(centerPanel);
        
        add(wrapper);

        // Ajout des écouteurs d'événements
        setupEventListeners();
    }


    private JPanel createFormField(String label, JComponent field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Constants.WH_BACKGROUND);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setMaximumSize(new Dimension(300, 70));
        
        JLabel jLabel = new JLabel(label);
        jLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        jLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(300, 35));
        
        panel.add(jLabel);
        panel.add(field);
        
        return panel;
    }

    private void setupEventListeners() {
        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> cardLayout.show(mainPanel, "register"));
        
        // Permettre la connexion avec la touche Entrée
        emailField.addActionListener(e -> handleLogin());
        passwordField.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            showMessage("Veuillez remplir tous les champs");
            return;
        }

        AuthService authService = AuthService.getInstance();
        if (authService.login(email, password)) {
            // Connexion réussie, initialiser l'application
            Main.initializeApplication();
            // Réinitialiser les champs
            emailField.setText("");
            passwordField.setText("");
            messageLabel.setText("");
        } else {
            showMessage("Email ou mot de passe incorrect");
        }
    }



    /**
     * Définit l'email dans le champ de connexion
     * @param email L'email à afficher dans le champ
     */
    public void setEmail(String email) {
        if (email != null && !email.isEmpty()) {
            emailField.setText(email);
            // Donner le focus au champ de mot de passe après avoir défini l'email
            passwordField.requestFocusInWindow();
        }
    }

    private void showMessage(String message) {
        messageLabel.setText(message);
        Timer timer = new Timer(3000, e -> messageLabel.setText(" "));
        timer.setRepeats(false);
        timer.start();
    }
}
