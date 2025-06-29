package chatWhatsappApplication;

import chatWhatsappApplication.client.ChatClient;

import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import com.google.gson.Gson;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@SuppressWarnings("serial")
public class DiscussionTile extends JPanel {
    private String name;
    private final String email;
    private final JLabel nameLabel;
    private final ChatClient wsClient;

    public DiscussionTile(String name,
                          String lastMessage,
                          String date,
                          Icon avatar,
                          JPanel mainPanel,
                          CardLayout cardLayout,
                          String email,
                          ChatClient wsClient) {
        this.name = name;
        this.email = email;
        this.wsClient = wsClient;

        setLayout(new BorderLayout(10, 5));
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Constants.WH_BACKGROUND.darker()));
        setBackground(Color.WHITE);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        // avatar
        JLabel iconLabel = new JLabel(avatar);
        iconLabel.setPreferredSize(new Dimension(40, 40));
        add(iconLabel, BorderLayout.WEST);

        // central info
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(Color.WHITE);
        nameLabel = new JLabel("<html><b>" + name + "</b></html>");
        JLabel msgLabel = new JLabel("<html><small>" + lastMessage + "</small></html>");
        info.add(nameLabel);
        info.add(msgLabel);
        add(info, BorderLayout.CENTER);

        // date
        JLabel dateLabel = new JLabel("<html><small>" + date + "</small></html>");
        dateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(dateLabel, BorderLayout.EAST);

        // clic pour ouvrir la discussion
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String panelName = "Discussion_" + email;
                boolean exists = false;
                for (Component c : mainPanel.getComponents()) {
                    if (panelName.equals(c.getName())) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    DiscussionDetail det = new DiscussionDetail(name, email, mainPanel, cardLayout, wsClient);
                    det.setName(panelName);
                    mainPanel.add(det, panelName);
                    Main.discussionPanels.put(email, det); 
                }
                cardLayout.show(mainPanel, panelName);
            }
        });
    }

    public String getEmail() {
        return email;
    }

    public void setContactName(String newName) {
        this.name = newName;
        nameLabel.setText("<html><b>" + newName + "</b></html>");
        repaint();
    }
}
