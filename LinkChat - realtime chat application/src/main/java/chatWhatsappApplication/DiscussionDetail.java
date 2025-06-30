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

    public DiscussionDetail(String name, String email, JPanel mainPanel, CardLayout cardLayout, ChatClient wsClient) {
        this.contactName = name;
        this.contactEmail = email;
        this.imagePath = "src/resources/default_profile.png"; // à personnaliser si besoin
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;
        this.wsClient = wsClient;

        setLayout(new BorderLayout());
        setBackground(Constants.WH_BACKGROUND);
        // Top bar
        ImageIcon icon = new ImageIcon(
            new ImageIcon(imagePath).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)
        );
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backButton = new JButton(new ImageIcon("src/resources/angle-left.png"));
        backButton.setBackground(Constants.WH_GREEN);
        backButton.setBorder(null);
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "ChatList"));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        iconLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showContactProfile();
            }
        });

        nameLabel = new JLabel(contactName);
        nameLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showContactProfile();
            }
        });

        topBar.add(backButton);
        topBar.add(iconLabel);
        topBar.add(nameLabel);
        topBar.setBackground(Constants.WH_GREEN);

        // Message area
        messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setBackground(Constants.WH_BACKGROUND);
        JScrollPane scrollPane = new JScrollPane(messagePanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);

        // Input area
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        inputField.addActionListener(this::sendMessage);
        JButton sendButton = new JButton(new ImageIcon("src/resources/send_icon.png"));
        sendButton.setBackground(Constants.WH_GREEN);
        sendButton.addActionListener(this::sendMessage);
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        // After initializing UI components, load previous messages:
        loadPreviousMessages();
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
            : "#FFFFFF";
        String html = "<html><div style='width:220px; word-wrap:break-word; padding:6px 10px; background-color:" + bgColor + "; border:1px solid #ddd; border-radius:12px;'>"
                + "<div style='font-size:12px; color:black;'>" + text + "</div>"
                + "<div style='font-size:10px; color:gray; text-align:right; margin-top:4px;'>" + time + "</div>"
                + "</div></html>";
        JLabel lbl = new JLabel(html);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lbl.setMaximumSize(new Dimension(300, Integer.MAX_VALUE));
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
            : "#FFFFFF";
        String html = "<html><div style='width:220px; word-wrap:break-word; padding:6px 10px; background-color:" + bgColor + "; border:1px solid #ddd; border-radius:12px;'>"
                + "<div style='font-size:12px; color:black;'>" + text + editedMark + "</div>"
                + "<div style='font-size:10px; color:gray; text-align:right; margin-top:4px;'>" + time + "</div>"
                + "</div></html>";
        JLabel lbl = new JLabel(html);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lbl.setMaximumSize(new Dimension(300, Integer.MAX_VALUE));
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
        wsClient.sendMessage(contactEmail, text);
        inputField.setText("");
        // Instead of adding the bubble directly, reload all messages:
        loadPreviousMessages();
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
