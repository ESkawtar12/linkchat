package chatWhatsappApplication;

import chatWhatsappApplication.client.ChatClient;

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

        setLayout(new BorderLayout(16, 0));
        setBackground(Color.WHITE);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Rounded corners and shadow
        setOpaque(false);

        // Avatar
        JLabel iconLabel = new JLabel(avatar);
        iconLabel.setPreferredSize(new Dimension(48, 48));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 12));
        add(iconLabel, BorderLayout.WEST);

        // Central info
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(new Color(34, 40, 49));

        JLabel msgLabel = new JLabel(lastMessage);
        msgLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        msgLabel.setForeground(new Color(120, 130, 140));

        info.add(nameLabel);
        info.add(Box.createVerticalStrut(4));
        info.add(msgLabel);
        add(info, BorderLayout.CENTER);

        // Date
        JLabel dateLabel = new JLabel(date);
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setForeground(new Color(180, 180, 180));
        dateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(dateLabel, BorderLayout.EAST);

        // Hover effect
        addMouseListener(new MouseAdapter() {
            Color normalBg = Color.WHITE;
            Color hoverBg = new Color(245, 249, 250);

            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(hoverBg);
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(normalBg);
                repaint();
            }

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

    @Override
    protected void paintComponent(Graphics g) {
        // Draw rounded panel with shadow
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = 18;
        int shadowGap = 2;
        int shadowAlpha = 30;

        // Shadow
        g2.setColor(new Color(0, 0, 0, shadowAlpha));
        g2.fillRoundRect(shadowGap, shadowGap, getWidth() - shadowGap * 2, getHeight() - shadowGap * 2, arc, arc);

        // Background
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth() - shadowGap, getHeight() - shadowGap, arc, arc);

        g2.dispose();
        super.paintComponent(g);
    }

    public String getEmail() {
        return email;
    }

    public void setContactName(String newName) {
        this.name = newName;
        nameLabel.setText(newName);
        repaint();
    }
}
