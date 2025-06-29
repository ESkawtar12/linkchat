package chatWhatsappApplication.util;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Un adapter fonctionnel pour écouter tous les changements d’un Document
 * avec une seule méthode update().
 */
@FunctionalInterface
public interface SimpleDocumentListener extends DocumentListener {
    /** sera appelé à chaque insertion, suppression ou changement d’attribut */
    void update();

    @Override
    default void insertUpdate(DocumentEvent e) {
        update();
    }

    @Override
    default void removeUpdate(DocumentEvent e) {
        update();
    }

    @Override
    default void changedUpdate(DocumentEvent e) {
        update();
    }
}
