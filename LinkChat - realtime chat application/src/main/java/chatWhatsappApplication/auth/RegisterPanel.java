package chatWhatsappApplication.auth;

import chatWhatsappApplication.Constants;
import chatWhatsappApplication.model.User;
import chatWhatsappApplication.service.AuthService;
import chatWhatsappApplication.util.SimpleDocumentListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class RegisterPanel extends JPanel {
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField birthDateField;
    private JComboBox<String> genderComboBox;
    private JButton registerButton;
    private JButton backButton;
    private JLabel messageLabel;
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private String profileImage = "src/main/java/resources/default.jpg";

    public RegisterPanel(JPanel mainPanel, CardLayout cardLayout) {
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE); // Make background white

        // Main container for centering
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setMaximumSize(new Dimension(520, Integer.MAX_VALUE));

        // App name styled
        JLabel appNameLabel = new JLabel("LinkChat");
        appNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 40));
        appNameLabel.setForeground(Constants.WH_GREEN);
        appNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        appNameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));

        // Header
        backButton = new JButton();
        JLabel titleLabel = new JLabel("Créer un compte", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        try {
            ImageIcon backIcon = new ImageIcon("src/main/java/resources/angle-left.png");
            Image scaled = backIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            backButton.setIcon(new ImageIcon(scaled));
        } catch (Exception ex) {
            backButton.setText("<");
        }
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "login"));
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        header.add(backButton, BorderLayout.WEST);
        header.add(titleLabel, BorderLayout.CENTER);

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
        formPanel.setMaximumSize(new Dimension(480, Integer.MAX_VALUE));
        formPanel.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

        // Fields
        firstNameField = createFormattedTextField();
        lastNameField = createFormattedTextField();
        emailField = createFormattedTextField();
        passwordField = createPasswordField();
        confirmPasswordField = createPasswordField();
        birthDateField = createFormattedTextField();
        String[] genders = {"Sélectionner un genre", "Masculin", "Féminin", "Autre"};
        genderComboBox = new JComboBox<>(genders);
        genderComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        genderComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        genderComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        genderComboBox.setBackground(new Color(245, 249, 250));
        genderComboBox.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true));

        // --- Use GridLayout for two columns ---
        JPanel gridPanel = new JPanel(new GridLayout(0, 2, 16, 0));
        gridPanel.setOpaque(false);

        gridPanel.add(createFormField("Prénom", firstNameField));
        gridPanel.add(createFormField("Nom", lastNameField));
        gridPanel.add(createFormField("Email", emailField));
        gridPanel.add(createFormField("Date de naissance (AAAA-MM-JJ)", birthDateField));
        gridPanel.add(createFormField("Mot de passe", passwordField));
        gridPanel.add(createFormField("Confirmer mot de passe", confirmPasswordField));
        gridPanel.add(createFormField("Genre", genderComboBox));
        // Empty cell for alignment
        gridPanel.add(new JLabel(""));

        formPanel.add(gridPanel);

        // Choose image button
        JButton chooseImageBtn = new JButton("Choisir une photo de profil");
        chooseImageBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        chooseImageBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chooseImageBtn.setBackground(new Color(245, 249, 250));
        chooseImageBtn.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true));
        chooseImageBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chooseImageBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                profileImage = chooser.getSelectedFile().getAbsolutePath();
            }
        });
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(chooseImageBtn);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Register button & message
        registerButton = new JButton("S'inscrire");
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setBackground(Constants.WH_GREEN);
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        registerButton.setFocusPainted(false);
        registerButton.setBorderPainted(false);
        registerButton.setOpaque(true);
        registerButton.setEnabled(false);
        registerButton.setPreferredSize(new Dimension(200, 40));
        registerButton.setMaximumSize(new Dimension(200, 40));
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(e -> onRegister());

        messageLabel = new JLabel(" ");
        messageLabel.setForeground(Color.RED);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        formPanel.add(registerButton);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(messageLabel);

        // Add to center panel
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(appNameLabel);
        centerPanel.add(header);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(formPanel);
        centerPanel.add(Box.createVerticalGlue());

        // Center everything
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.add(centerPanel);

        add(wrapper);

        setupValidation();
    }

    private void setupValidation() {
        DocumentListener dl = (SimpleDocumentListener) this::validateForm;
        firstNameField.getDocument().addDocumentListener(dl);
        lastNameField.getDocument().addDocumentListener(dl);
        emailField.getDocument().addDocumentListener(dl);
        passwordField.getDocument().addDocumentListener(dl);
        confirmPasswordField.getDocument().addDocumentListener(dl);
        birthDateField.getDocument().addDocumentListener(dl);
        genderComboBox.addActionListener(e -> validateForm());
    }

    private void validateForm() {
        boolean ok = !firstNameField.getText().trim().isEmpty()
                && !lastNameField.getText().trim().isEmpty()
                && !emailField.getText().trim().isEmpty()
                && passwordField.getPassword().length > 0
                && confirmPasswordField.getPassword().length > 0
                && !birthDateField.getText().trim().isEmpty()
                && genderComboBox.getSelectedIndex() > 0;
        registerButton.setEnabled(ok);
    }

    private void onRegister() {
        try {
            String first = firstNameField.getText().trim();
            String last = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String pwd = new String(passwordField.getPassword());
            String confirm = new String(confirmPasswordField.getPassword());
            LocalDate dob = LocalDate.parse(birthDateField.getText().trim());
            String gender = (String) genderComboBox.getSelectedItem();

            if (!pwd.equals(confirm)) {
                messageLabel.setText("Les mots de passe ne correspondent pas.");
                return;
            }

            User user = new User(first, last, email, pwd, dob, gender, profileImage);
            boolean success = AuthService.getInstance().register(user);

            if (success) {
                cardLayout.show(mainPanel, "login");
                for (Component comp : mainPanel.getComponents()) {
                    if (comp instanceof LoginPanel lp) {
                        lp.setEmail(email);
                        break;
                    }
                }
                clearFields();
            } else {
                messageLabel.setText("Cet email est déjà utilisé.");
            }
        } catch (DateTimeParseException ex) {
            messageLabel.setText("Format de date invalide. AAAA-MM-JJ");
        } catch (Exception ex) {
            messageLabel.setText("Erreur : " + ex.getMessage());
        }
    }

    private void clearFields() {
        firstNameField.setText("");
        lastNameField.setText("");
        emailField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        birthDateField.setText("");
        genderComboBox.setSelectedIndex(0);
        messageLabel.setText(" ");
    }

    private JTextField createFormattedTextField() {
        JTextField f = new JTextField(20);
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        f.setBackground(new Color(245, 249, 250));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
            BorderFactory.createEmptyBorder(7, 12, 7, 12)
        ));
        return f;
    }

    private JPasswordField createPasswordField() {
        JPasswordField p = new JPasswordField(20);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        p.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        p.setBackground(new Color(245, 249, 250));
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
            BorderFactory.createEmptyBorder(7, 12, 7, 12)
        ));
        return p;
    }

    private JPanel createFormField(String label, JComponent field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(l);
        p.add(Box.createRigidArea(new Dimension(0, 3)));
        p.add(field);
        p.add(Box.createRigidArea(new Dimension(0, 10)));
        return p;
    }
}
