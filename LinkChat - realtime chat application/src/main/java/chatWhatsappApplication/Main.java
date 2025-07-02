package chatWhatsappApplication;

import chatWhatsappApplication.auth.LoginPanel;
import chatWhatsappApplication.auth.RegisterPanel;
import chatWhatsappApplication.client.ChatClient;
import chatWhatsappApplication.service.AuthService;
import chatWhatsappApplication.service.DatabaseConnection;
  


import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import com.google.gson.Gson;


import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;

import java.util.HashMap;
import java.util.Map;

public class Main {
    private static JFrame frame;
    private static JPanel mainPanel;
    private static CardLayout cardLayout;
    private static ChatListPanel chatListPanel;
    private static JSplitPane splitPane;
    private static ChatClient wsClient;

    public static void main(String[] args) {
        testDatabaseConnection();

        frame = new JFrame("WhatsApp Java");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null);
        frame.setMinimumSize(new Dimension(800, 500));

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(Constants.WH_BACKGROUND);

        createAuthPanels();

        JPanel appPanel = new JPanel(new BorderLayout());
        mainPanel.add(appPanel, "app");

        cardLayout.show(mainPanel, "login");

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private static void testDatabaseConnection() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            System.out.println("✅ Connexion à MySQL réussie !");
            conn.close();
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion à MySQL : " + e.getMessage());
        }
    }

    private static void createAuthPanels() {
        LoginPanel loginPanel = new LoginPanel(mainPanel, cardLayout);
        mainPanel.add(loginPanel, "login");

        RegisterPanel registerPanel = new RegisterPanel(mainPanel, cardLayout);
        mainPanel.add(registerPanel, "register");
    }

    public static Map<String, DiscussionDetail> discussionPanels = new HashMap<>();


    public static void initializeApplication() {
        try {
            wsClient = new ChatClient(
                new URI("ws://localhost:8887"),
                AuthService.getInstance().getCurrentUser().getEmail()
            );
            wsClient.setMessageListener((from, content) -> {
                DiscussionDetail panel = discussionPanels.get(from);
                if (panel != null) {
                    SwingUtilities.invokeLater(() -> panel.receiveMessage(content));
                }
            });
            wsClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        CardLayout rightCardLayout = new CardLayout();
        JPanel rightPanel = new JPanel(rightCardLayout);

        JPanel placeholderPanel = new JPanel(new BorderLayout());
        placeholderPanel.setBackground(Color.WHITE);

        // Title styled like LoginPanel
        JLabel titleLabel = new JLabel("Welcome to LinkChat", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 40));
        titleLabel.setForeground(Constants.WH_GREEN);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));

        // Subtitle message
        JLabel subtitleLabel = new JLabel(
            "<html><div style='text-align:center;'>Start a conversation by selecting a friend.<br>Enjoy real-time messaging with your friends!</div></html>",
            SwingConstants.CENTER
        );
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Vertical layout for title and subtitle
        Box box = Box.createVerticalBox();
        box.add(Box.createVerticalGlue());
        box.add(titleLabel);
        box.add(subtitleLabel);
        box.add(Box.createVerticalGlue());

        placeholderPanel.add(box, BorderLayout.CENTER);
        rightPanel.add(placeholderPanel, "ChatList");

        chatListPanel = new ChatListPanel(rightPanel, rightCardLayout, wsClient);
        chatListPanel.loadFriends();

        ProfilePanel profilePanel = new ProfilePanel(rightPanel, rightCardLayout);
        rightPanel.add(profilePanel, "Profile");

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, chatListPanel, rightPanel);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0);
        splitPane.setDividerSize(2);
        splitPane.setBorder(null);

        mainPanel.removeAll();
        mainPanel.add(splitPane, "app");
        mainPanel.revalidate();
        mainPanel.repaint();

        cardLayout.show(mainPanel, "app");
    }

    public static void logout() {
        AuthService authService = AuthService.getInstance();
        authService.logout();

        mainPanel.removeAll();
        createAuthPanels();
        cardLayout.show(mainPanel, "login");
    }
}
