package chatWhatsappApplication;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/** A panel with rounded corners and optional shadow. */
public class RoundedPanel extends JPanel {
    private final int cornerRadius;

    public RoundedPanel(int radius) {
        super();
        this.cornerRadius = radius;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Shape clip = new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        g2.setColor(getBackground());
        g2.fill(clip);
        g2.dispose();
        super.paintComponent(g);
    }
}
