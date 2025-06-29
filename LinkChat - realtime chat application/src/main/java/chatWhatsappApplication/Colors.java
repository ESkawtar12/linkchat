package chatWhatsappApplication;

import java.awt.Color;

/** Central colour palette */
public final class Colors {
    private Colors(){}

    // Palette Bleue Moderne - Basée sur #0374a4
    public static final Color PRIMARY = new Color(0x0374A4);             // Bleu principal (#0374a4)
    public static final Color PRIMARY_DARK = new Color(0x025D84);       // Bleu plus foncé
    public static final Color PRIMARY_LIGHT = new Color(0x5CB3D1);       // Bleu plus clair
    public static final Color MESSAGE_BUBBLE = new Color(0x0374A4);      // Couleur des bulles de messages

    public static final Color BG_LIGHT = new Color(0xF5F9FA);            // Blanc cassé bleuté
    public static final Color BG_WHITE = Color.WHITE;                    // Fond bulles reçues

    public static final Color TEXT_PRIMARY = new Color(0x1C1C1C);        // Texte principal
    public static final Color TEXT_SECONDARY = new Color(0x5A6A7A);      // Texte secondaire

    public static final Color BORDER_LIGHT = new Color(0xD1E0E8);        // Bordures bleutées

    public static final Color BUBBLE_SENT = new Color(0x1479A4);         // Couleur spécifique pour les bulles envoyées (#1479a4)
    public static final Color BUBBLE_RECEIVED = Color.WHITE;             // Fond message reçu
}
