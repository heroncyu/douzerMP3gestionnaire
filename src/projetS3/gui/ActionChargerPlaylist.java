package projetS3.gui;

import projetS3.core.playlist.Playlist;
import projetS3.importer.ImporterJSPF;
import projetS3.importer.ImporterM3U8;
import projetS3.importer.ImporterPlaylist;
import projetS3.importer.ImporterXSPF;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Action qui permet d'ouvrir un fichier de playlist (M3U8, XSPF ou JSPF).
 * Elle fait le lien entre le JFileChooser et les classes du package importer.
 * @author Sajid
 */
public class ActionChargerPlaylist implements ActionListener {
    //référence vers la fenêtre principale
    private GUI fenetre;

    /**
     * Constructeur
     * @param gui la fenêtre principale
     */
    public ActionChargerPlaylist(GUI gui) {
        this.fenetre = gui;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // 1. Création et configuration du sélecteur de fichier
        JFileChooser selecteur = new JFileChooser();
        selecteur.setDialogTitle("Sélectionner une playlist à charger");

        // Filtre par type de fichier
        selecteur.addChoosableFileFilter(new FileNameExtensionFilter("Playlist M3U8 (*.m3u8)","m3u8"));
        selecteur.addChoosableFileFilter(new FileNameExtensionFilter("Playlist XSPF (*.xspf)","xspf"));
        selecteur.addChoosableFileFilter(new FileNameExtensionFilter("Playlist JSPF (*.jspf)", "jspf"));

        // Empêche de choisir n'importe quel type de fichier
        selecteur.setAcceptAllFileFilterUsed(false);

        // 2. Affichage du dialogue
        if (selecteur.showOpenDialog(fenetre) != JFileChooser.APPROVE_OPTION) {
            return; //l'utilisateur a annulé
        }

        String chemin = selecteur.getSelectedFile().getAbsolutePath();
        ImporterPlaylist importeur = null;

        //3. Selection de l'importateur selon l'extension
        String cheminLower = chemin.toLowerCase();

        if (cheminLower.endsWith(".m3u8")) {
            importeur = new ImporterM3U8();
        }
        if (cheminLower.endsWith(".xspf")) {
            importeur = new ImporterXSPF();
        }
        if (cheminLower.endsWith(".jspf")) {
            importeur = new ImporterJSPF();
        }
        if(importeur == null){
            JOptionPane.showMessageDialog(fenetre, "Format de fichier non reconnu", "Erreur",JOptionPane.ERROR_MESSAGE);
            return;
        }

        //4. Import de la playlist
        try {
            Playlist playlistLue = importeur.importer(chemin);

            // 4. Si la playlist a bien été créée en mémoire
            if (playlistLue != null) {
                // On l'ajoute à la liste des playlists de la GUI
                fenetre.getPlaylists().add(playlistLue);
                fenetre.rafraichirListePlaylist();

                StringBuilder message = new StringBuilder();
                message.append("Playlist importée : ").append(playlistLue.getNom()).append("\n");
                message.append(playlistLue.getTaille()).append(" morceau(x)");
                //affichage du resultat avec les erreurs éventuelles
                fenetre.afficherResultat("Import",message.toString(),importeur.getErreurs());
                }

            }
        catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(fenetre,
                    "Fichier introuvable : " + chemin,
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE
            );
            }
        catch (IOException ex){
            JOptionPane.showMessageDialog(fenetre,
                    "Erreur de lecture : " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE
            );
        }

    }

}
