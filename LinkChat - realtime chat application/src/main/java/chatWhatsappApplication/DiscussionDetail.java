package chatWhatsappApplication;

import chatWhatsappApplication.client.ChatClient;
import com.google.gson.Gson;
import chatWhatsappApplication.service.AuthService;

import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import com.google.gson.Gson;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import chatWhatsappApplication.service.MessageService;


@SuppressWarnings("unused")
public class DiscussionDetail extends JPanel {
    private final JPanel messagePanel;
    private final JTextField inputField;
    private final JButton backButton;
    private final String contactName;
    private final String contactEmail;
    private final String imagePath;
    private final JPanel mainPanel;
    private final CardLayout cardLayout;
    private final JLabel nameLabel;
    private final ChatClient wsClient;

    private final Gson gson = new Gson();
    private final JLabel typingLabel = new JLabel(""); // <-- Typing label

    public DiscussionDetail(String name, String email, JPanel mainPanel, CardLayout cardLayout, ChatClient wsClient) {
        this.contactName = name;
        this.contactEmail = email;
        this.imagePath = "src/main/java/resources/default_profile.png";
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;
        this.wsClient = wsClient;

        setLayout(new BorderLayout());
        setBackground(Constants.WH_BACKGROUND);

        // --- Top bar with rounded corners and shadow ---
        ImageIcon icon = new ImageIcon(
            new ImageIcon(imagePath).getImage().getScaledInstance(36, 36, Image.SCALE_SMOOTH)
        );
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 8)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Constants.WH_GREEN);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                // subtle shadow
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fillRoundRect(2, getHeight() - 8, getWidth() - 4, 8, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        topBar.setOpaque(false);

        backButton = new JButton(new ImageIcon("src/main/java/resources/angle-left.png"));
        backButton.setBackground(new Color(0,0,0,0));
        backButton.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setFocusable(false);
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "ChatList"));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 12));
        iconLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showContactProfile();
            }
        });

        nameLabel = new JLabel(contactName);
        nameLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 12));
        nameLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showContactProfile();
            }
        });

        topBar.add(backButton);
        topBar.add(iconLabel);
        topBar.add(nameLabel);

        // --- Message area ---
        messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setBackground(Constants.WH_BACKGROUND);
        messagePanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        JScrollPane scrollPane = new JScrollPane(messagePanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // --- Input area with rounded corners and shadow ---
        JPanel inputPanel = new JPanel(new BorderLayout(8, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                // subtle shadow
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fillRoundRect(2, getHeight() - 8, getWidth() - 4, 8, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        inputPanel.setOpaque(false);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        inputField = new JTextField();
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inputField.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        inputField.setBackground(new Color(245, 249, 250));
        inputField.setForeground(new Color(34, 40, 49));
        inputField.addActionListener(this::sendMessage);

        // --- Add typing event ---
        inputField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private long lastTyped = 0;
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { sendTyping(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { sendTyping(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { sendTyping(); }
            private void sendTyping() {
                long now = System.currentTimeMillis();
                if (wsClient != null && now - lastTyped > 500) { // throttle to avoid spamming
                    wsClient.sendTypingMessage(contactEmail);
                    lastTyped = now;
                }
            }
        });

        // --- Fix send button icon and style ---
        ImageIcon sendIcon;
        try {
            Image img = new ImageIcon("src/main/java/resources/send_icon.png").getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH);
            sendIcon = new ImageIcon(img);
        } catch (Exception ex) {
            sendIcon = new ImageIcon("src/main/java/resources/send_icon.png");
        }
        JButton sendButton = new JButton(sendIcon);
        sendButton.setBackground(Constants.WH_GREEN);
        sendButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendButton.setFocusable(false);
        sendButton.setContentAreaFilled(true);
        sendButton.setOpaque(true);
        sendButton.setBorderPainted(false);
        sendButton.addActionListener(this::sendMessage);

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // --- Typing label settings ---
        typingLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        typingLabel.setForeground(new Color(120, 120, 120));
        typingLabel.setVisible(false);

        JPanel inputWrapper = new JPanel();
        inputWrapper.setLayout(new BorderLayout());
        inputWrapper.setOpaque(false);
        inputWrapper.add(typingLabel, BorderLayout.NORTH);
        inputWrapper.add(inputPanel, BorderLayout.CENTER);

        add(topBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(inputWrapper, BorderLayout.SOUTH);

        // After initializing UI components, load previous messages:
        loadPreviousMessages();

        // Register typing listener
        if (wsClient != null) {
            wsClient.setTypingListener(from -> {
                if (contactEmail.equals(from)) {
                    SwingUtilities.invokeLater(() -> {
                        typingLabel.setText(contactName + " est en train d'écrire...");
                        typingLabel.setVisible(true);
                        // Hide after 2 seconds if no new typing event
                        Timer timer = new Timer(2000, e -> typingLabel.setVisible(false));
                        timer.setRepeats(false);
                        timer.start();
                    });
                }
            });
        }
    }

    private void showContactProfile() {
        String panelName = "Profile_" + contactEmail;
        ContactProfilePanel profilePanel = new ContactProfilePanel(
            mainPanel, cardLayout, contactName, contactEmail, imagePath, panelName
        );
        mainPanel.add(profilePanel, panelName);
        cardLayout.show(mainPanel, panelName);
    }

    private JComponent createMessageBubble(String text, boolean isSent) {
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        text = text.replace("\n", "<br>");
        String bgColor = isSent
            ? String.format("#%02x%02x%02x", Colors.MESSAGE_BUBBLE.getRed(), Colors.MESSAGE_BUBBLE.getGreen(), Colors.MESSAGE_BUBBLE.getBlue())
            : "#F5F9FA";
        String borderColor = isSent ? "#B2DFDB" : "#E0E0E0";
        // --- Make bubble smaller and more rounded ---
        String html = "<html><div style='width:170px; word-wrap:break-word; padding:8px 14px; background-color:" + bgColor + "; border:1px solid " + borderColor + "; border-radius:24px;'>"
                + "<div style='font-size:12px; color:#222831;'>" + text + "</div>"
                + "<div style='font-size:9px; color:gray; text-align:right; margin-top:3px;'>" + time + "</div>"
                + "</div></html>";
        JLabel lbl = new JLabel(html);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setMaximumSize(new Dimension(220, Integer.MAX_VALUE));
        JPanel wrapper = new JPanel(new FlowLayout(isSent ? FlowLayout.RIGHT : FlowLayout.LEFT, 0, 0));
        wrapper.setBackground(Constants.WH_BACKGROUND);
        wrapper.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        wrapper.add(lbl);
        return wrapper;
    }

    private JComponent createMessageBubble(MessageService.Message msg, boolean isSent) {
        String text = msg.deleted ? "<i>Message supprimé</i>" : msg.content;
        String time = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
        text = text.replace("\n", "<br>");
        String editedMark = msg.edited ? " <span style='color:gray;'>(édité)</span>" : "";
        String bgColor = isSent
            ? String.format("#%02x%02x%02x", Colors.MESSAGE_BUBBLE.getRed(), Colors.MESSAGE_BUBBLE.getGreen(), Colors.MESSAGE_BUBBLE.getBlue())
            : "#F5F9FA";
        String borderColor = isSent ? "#B2DFDB" : "#E0E0E0";
        // --- Make bubble smaller and more rounded ---
        String html = "<html><div style='width:170px; word-wrap:break-word; padding:8px 14px; background-color:" + bgColor + "; border:1px solid " + borderColor + "; border-radius:24px;'>"
                + "<div style='font-size:12px; color:#222831;'>" + text + editedMark + "</div>"
                + "<div style='font-size:9px; color:gray; text-align:right; margin-top:3px;'>" + time + "</div>"
                + "</div></html>";
        JLabel lbl = new JLabel(html);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setMaximumSize(new Dimension(220, Integer.MAX_VALUE));
        JPanel wrapper = new JPanel(new FlowLayout(isSent ? FlowLayout.RIGHT : FlowLayout.LEFT, 0, 0));
        wrapper.setBackground(Constants.WH_BACKGROUND);
        wrapper.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        wrapper.add(lbl);

        if (isSent && !msg.deleted) {
            lbl.setComponentPopupMenu(createDeleteMenu(msg));
        }
        return wrapper;
    }

    private void sendMessage(ActionEvent e) {
        System.out.println("Send button clicked. wsClient=" + wsClient + ", contactEmail=" + contactEmail);
        String text = inputField.getText().trim();
        if (text.isEmpty() || wsClient == null) {
            System.out.println("Message not sent: empty or wsClient is null");
            return;
        }

        // Optimistically add the message bubble to the UI
        MessageService.Message myMsg = new MessageService.Message(
            -1, // No DB id yet
            AuthService.getInstance().getCurrentUser().getEmail(),
            contactEmail,
            text,
            false,
            false
        );
        messagePanel.add(createMessageBubble(myMsg, true));
        messagePanel.revalidate();
        messagePanel.repaint();
        scrollToBottom();

        wsClient.sendMessage(contactEmail, text);
        inputField.setText("");
    }

    public void receiveMessage(String text) {
        // Play notification sound
        try {
            java.awt.Toolkit.getDefaultToolkit().beep(); // Simple beep, or use a custom sound (see below)
            // For a custom sound, use:
            // javax.sound.sampled.AudioSystem.getClip().open(AudioSystem.getAudioInputStream(new File("src/resources/notify.wav"))).start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Show a toast-like notification (non-blocking)
        javax.swing.SwingUtilities.invokeLater(() -> {
            JWindow toast = new JWindow();
            JPanel panel = new JPanel();
            panel.setBackground(new Color(60, 63, 65, 220));
            panel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
            JLabel label = new JLabel("<html><b>Nouveau message</b><br>" + text + "</html>");
            label.setForeground(Color.WHITE);
            panel.add(label);
            toast.add(panel);
            toast.setSize(250, 70);
            // Position bottom right of the screen
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            toast.setLocation(screenSize.width - 270, screenSize.height - 120);
            toast.setAlwaysOnTop(true);
            toast.setVisible(true);

            // Hide after 2.5 seconds
            new Timer(2500, e -> toast.dispose()).start();
        });

        messagePanel.add(createMessageBubble(text, false));
        messagePanel.revalidate();
        messagePanel.repaint();
        scrollToBottom();
    }

    public void updateContactName(String newName) {
        nameLabel.setText(newName);
    }

    public void loadPreviousMessages() {
        messagePanel.removeAll();
        String myEmail = AuthService.getInstance().getCurrentUser().getEmail();
        java.util.List<MessageService.Message> messages = MessageService.getMessages(myEmail, contactEmail);
        for (MessageService.Message msg : messages) {
            boolean isSent = msg.sender.equals(myEmail);
            messagePanel.add(createMessageBubble(msg, isSent));
        }
        // Mark as read
        MessageService.markAsRead(contactEmail, myEmail);
        // Update chat list UI
        ChatListPanel.getInstance().loadFriends();
        messagePanel.revalidate();
        messagePanel.repaint();
        scrollToBottom();
    }

    private JPopupMenu createDeleteMenu(MessageService.Message msg) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem deleteItem = new JMenuItem("Supprimer");
        deleteItem.addActionListener(e -> {
            MessageService.deleteMessage(msg.id);
            msg.deleted = true;
            if (wsClient != null) {
                wsClient.sendDeleteMessage(contactEmail, msg.id);
            }
            loadPreviousMessages();
        });
        menu.add(deleteItem);

        // Add edit option
        JMenuItem editItem = new JMenuItem("Modifier");
        editItem.addActionListener(e -> {
            String newContent = JOptionPane.showInputDialog(this, "Modifier le message :", msg.content);
            if (newContent != null && !newContent.trim().isEmpty() && !msg.deleted) {
                MessageService.editMessage(msg.id, newContent);
                msg.content = newContent;
                msg.edited = true;
                if (wsClient != null) {
                    wsClient.sendEditMessage(contactEmail, msg.id, newContent);
                }
                loadPreviousMessages();
            }
        });
        menu.add(editItem);

        return menu;
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            Container parent = messagePanel.getParent();
            while (parent != null && !(parent instanceof JScrollPane)) {
                parent = parent.getParent();
            }
            if (parent instanceof JScrollPane scrollPane) {
                JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
                verticalBar.setValue(verticalBar.getMaximum());
            }
        });
    }
}
