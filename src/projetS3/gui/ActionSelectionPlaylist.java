package projetS3.gui;

import projetS3.core.playlist.Playlist;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Listener qui détecte le changement de playlist sélectionnée dans la liste de gauche.
 * Elle rafraîchit automatiquement la liste des morceaux au centre pour afficher
 * le contenu de la nouvelle playlist sélectionnée.
 * @author Ayoub
 */

public class ActionSelectionPlaylist implements ListSelectionListener {

    private GUI fenetre;

    public ActionSelectionPlaylist(GUI gui) {
        this.fenetre = gui;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        // evite de faire le travail deux fois ( au moment de l'appuie et du relachement)
        if (!e.getValueIsAdjusting()) {
            Playlist p = fenetre.getListePlaylist().getSelectedValue();

            if (p != null) {
                fenetre.rafraichirListeMorceaux(p);
                fenetre.afficheMetadonnees(null);
            }
        }
    }
}