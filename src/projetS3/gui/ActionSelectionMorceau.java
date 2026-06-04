package projetS3.gui;

import projetS3.core.audio.FichierMP3;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Listener qui réagit à la sélection d'un morceau dans la liste centrale.
 * Met automatiquement à jour le panneau de droite avec les métadonnées
 * du fichier MP3 sélectionné (titre, artiste, album, etc.).
 * @author Ayoub
 */
public class ActionSelectionMorceau implements ListSelectionListener {

    private GUI fenetre;

    public ActionSelectionMorceau(GUI gui) {
        this.fenetre = gui;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        // evite de faire le travail deux fois ( au moment de l'appuie et du relachement)
        if (!e.getValueIsAdjusting()) {
            FichierMP3 mp3 = fenetre.getListeMorceaux().getSelectedValue();

            if (mp3 != null) {
                fenetre.afficheMetadonnees(mp3);
            }
        }
    }
}