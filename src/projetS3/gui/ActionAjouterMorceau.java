package projetS3.gui;

import projetS3.core.audio.FichierMP3;
import projetS3.core.playlist.Playlist;

import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Action qui permet d'ajouter un ou plusieurs morceaux sélectionnés dans une playlist choisie.
 * L'utilisateur sélectionne d'abord les morceaux dans la liste centrale,
 * puis choisit la playlist de destination à l'aide  d'une boîte de dialogue.
 * @author Ayoub
 */
public class ActionAjouterMorceau implements ActionListener {

    private GUI gui;

    public ActionAjouterMorceau(GUI gui) {
        this.gui = gui;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // recupere tous les morceaux selectionnes
        List<FichierMP3> morceauxSelectionnes = gui.getListeMorceaux().getSelectedValuesList();

        if (morceauxSelectionnes.isEmpty()) {
            JOptionPane.showMessageDialog(gui, "Sélectionnez au moins un morceau !");
            return;
        }

        ArrayList<Playlist> listeDesPlaylists = gui.getPlaylists();

        if (listeDesPlaylists.isEmpty()) {
            JOptionPane.showMessageDialog(gui, "Aucune playlist disponible.");
            return;
        }

        // prepare les noms pour le choix
        String[] nomsPlaylists = new String[listeDesPlaylists.size()];
        for (int i = 0; i < listeDesPlaylists.size(); i++) {
            nomsPlaylists[i] = listeDesPlaylists.get(i).getNom();
        }

        Object selection = JOptionPane.showInputDialog(
                gui,
                "Ajouter ces " + morceauxSelectionnes.size() + " morceaux dans :",
                "Ajout multiple",
                JOptionPane.QUESTION_MESSAGE,
                null,
                nomsPlaylists,
                nomsPlaylists[0]
        );

        if (selection != null) {
            String nomChoisi = selection.toString();

            // cherche la bonne playlist
            for (int j = 0; j < listeDesPlaylists.size(); j++) {
                Playlist p = listeDesPlaylists.get(j);

                if (p.getNom().equals(nomChoisi)) {
                    // ajoute tous les morceaux
                    for (int k = 0; k < morceauxSelectionnes.size(); k++) {
                        p.ajouter(morceauxSelectionnes.get(k));
                    }
                    System.out.println(morceauxSelectionnes.size() + " morceaux ajoutés dans " + p.getNom());
                }
            }
            // On rafraichit l'affichage
            Playlist playlistActuelle = gui.getListePlaylist().getSelectedValue();
            gui.rafraichirListeMorceaux(playlistActuelle);
            gui.rafraichirListePlaylist();
        }
    }
}