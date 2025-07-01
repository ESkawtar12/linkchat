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
        setBackground(Color.WHITE); // Make background white

        // Main container for centering
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setMaximumSize(new Dimension(420, Integer.MAX_VALUE));

        // App name styled
        JLabel appNameLabel = new JLabel("LinkChat");
        appNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 40));
        appNameLabel.setForeground(Constants.WH_GREEN);
        appNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        appNameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));

        // Title
        JLabel titleLabel = new JLabel("Connexion");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));

        // Form panel with rounded corners and subtle shadow
        JPanel formPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
                g2.setColor(new Color(0, 0, 0, 12));
                g2.fillRoundRect(3, getHeight() - 12, getWidth() - 6, 12, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(0,0,0,0));
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.setMaximumSize(new Dimension(350, Integer.MAX_VALUE));
        formPanel.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

        // Email field
        emailField = new JTextField(20);
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        emailField.setBackground(new Color(245, 249, 250));
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
            BorderFactory.createEmptyBorder(7, 12, 7, 12)
        ));

        // Password field
        passwordField = new JPasswordField(20);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        passwordField.setBackground(new Color(245, 249, 250));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
            BorderFactory.createEmptyBorder(7, 12, 7, 12)
        ));

        // Login button
        loginButton = new JButton("Se connecter");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setBackground(Constants.WH_GREEN);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setOpaque(true);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        loginButton.setPreferredSize(new Dimension(200, 40));
        loginButton.setMaximumSize(new Dimension(200, 40));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Register button (styled as link)
        registerButton = new JButton("Créer un compte");
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setBorderPainted(false);
        registerButton.setFocusPainted(false);
        registerButton.setContentAreaFilled(false);
        registerButton.setForeground(Constants.WH_GREEN.darker());
        registerButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        registerButton.setOpaque(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Error message
        messageLabel = new JLabel(" ");
        messageLabel.setForeground(new Color(220, 50, 50));
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add fields to form panel
        formPanel.add(createFormField("Email", emailField));
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(createFormField("Mot de passe", passwordField));
        formPanel.add(Box.createRigidArea(new Dimension(0, 22)));
        formPanel.add(loginButton);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(registerButton);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(messageLabel);

        // Add to center panel
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(appNameLabel);
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(formPanel);
        centerPanel.add(Box.createVerticalGlue());

        // Center everything
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.add(centerPanel);

        add(wrapper);

        // Event listeners
        setupEventListeners();
    }

    private JPanel createFormField(String label, JComponent field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setMaximumSize(new Dimension(300, 70));
        
        JLabel jLabel = new JLabel(label);
        jLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        jLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        jLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(300, 35));
        
        panel.add(jLabel);
        panel.add(field);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
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
