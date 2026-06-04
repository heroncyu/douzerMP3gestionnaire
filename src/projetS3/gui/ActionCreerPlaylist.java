package projetS3.gui;

import projetS3.core.playlist.Playlist;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

/**
 * Action déclenchée par le bouton de création de playlist.
 * Demande à l'utilisateur de saisir un nom grace à  une boîte de dialogue,
 * puis crée et ajoute la nouvelle playlist vide à l'application.
 * @author Ayoub
 */

public class ActionCreerPlaylist implements ActionListener {

    private GUI fenetre;

    public ActionCreerPlaylist(GUI gui) {
        this.fenetre = gui;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // demande le nom de la playlist
        String nom = JOptionPane.showInputDialog(fenetre, "Nom de la playlist :");

        if (nom == null || nom.equals("")) {
            return;
        }

        try {
            Playlist nouvelleP = new Playlist(nom);
            fenetre.getPlaylists().add(nouvelleP);
            fenetre.rafraichirListePlaylist();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(fenetre, "Erreur : " + ex.getMessage());
        }
    }
}