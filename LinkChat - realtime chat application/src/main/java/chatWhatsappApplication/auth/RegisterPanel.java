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
    private String profileImage = "src/resources/default.jpg";

    public RegisterPanel(JPanel mainPanel, CardLayout cardLayout) {
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Constants.WH_BACKGROUND);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Constants.WH_BACKGROUND);

        // Header
        backButton = new JButton();
        JLabel titleLabel = new JLabel("Créer un compte", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        try {
            ImageIcon backIcon = new ImageIcon("src/resources/angle-left.png");
            Image scaled = backIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            backButton.setIcon(new ImageIcon(scaled));
        } catch (Exception ex) {
            backButton.setText("<");
        }
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "login"));
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Constants.WH_BACKGROUND);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        header.add(backButton, BorderLayout.WEST);
        header.add(titleLabel, BorderLayout.CENTER);

        // Fields
        firstNameField = createFormattedTextField();
        lastNameField = createFormattedTextField();
        emailField = createFormattedTextField();
        passwordField = createPasswordField();
        confirmPasswordField = createPasswordField();
        birthDateField = createFormattedTextField();
        String[] genders = {"Sélectionner un genre", "Masculin", "Féminin", "Autre"};
        genderComboBox = new JComboBox<>(genders);
        genderComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        genderComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Assemble scroll content
        JPanel scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setBackground(Constants.WH_BACKGROUND);
        scrollContent.add(header);
        scrollContent.add(createFormField("Prénom", firstNameField));
        scrollContent.add(createFormField("Nom", lastNameField));
        scrollContent.add(createFormField("Email", emailField));
        scrollContent.add(createFormField("Mot de passe", passwordField));
        scrollContent.add(createFormField("Confirmer mot de passe", confirmPasswordField));
        scrollContent.add(createFormField("Date de naissance (AAAA-MM-JJ)", birthDateField));
        scrollContent.add(createFormField("Genre", genderComboBox));

        // Choose image button
        JButton chooseImageBtn = new JButton("Choisir une photo de profil");
        chooseImageBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        chooseImageBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                profileImage = chooser.getSelectedFile().getAbsolutePath();
            }
        });
        scrollContent.add(Box.createRigidArea(new Dimension(0, 10)));
        scrollContent.add(chooseImageBtn);
        scrollContent.add(Box.createRigidArea(new Dimension(0, 20)));

        // Register button & message
        registerButton = new JButton("S'inscrire");
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setBackground(Constants.WH_GREEN);
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setFocusPainted(false);
        registerButton.setBorderPainted(false);
        registerButton.setOpaque(true);
        registerButton.setEnabled(false);
        registerButton.addActionListener(e -> onRegister());
        messageLabel = new JLabel(" ");
        messageLabel.setForeground(Color.RED);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        scrollContent.add(registerButton);
        scrollContent.add(Box.createRigidArea(new Dimension(0, 10)));
        scrollContent.add(messageLabel);

        JScrollPane scrollPane = new JScrollPane(scrollContent);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Constants.WH_BACKGROUND);
        add(scrollPane, BorderLayout.CENTER);

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
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        return f;
    }

    private JPasswordField createPasswordField() {
        JPasswordField p = new JPasswordField(20);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        return p;
    }

    private JPanel createFormField(String label, JComponent field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Constants.WH_BACKGROUND);
        JLabel l = new JLabel(label);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(l);
        p.add(field);
        p.add(Box.createRigidArea(new Dimension(0, 10)));
        return p;
    }
}
