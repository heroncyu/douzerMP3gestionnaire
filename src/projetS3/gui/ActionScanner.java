package projetS3.gui;

import projetS3.core.scan.ScanRepertoire;
import projetS3.core.playlist.Playlist;
import projetS3.core.audio.FichierMP3;
import projetS3.core.audio.FichierMP3Exception;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * Cette classe fait le lien entre le bouton "Scanner" et ma classe ScanRepertoire.
 * Elle récupère le dossier via une fenêtre et met à jour l'affichage de la GUI.
 * elle peut aussi charger des fichiers MP3 individuels.
 * @author Ayoub
 */
public class ActionScanner implements ActionListener {

    private GUI fenetre;

    /**
     * Le constructeur reçoit la référence de la GUI pour pouvoir
     * mettre à jour les listes après le scan.
     * @param gui la fenêtre principale
     */
    public ActionScanner(GUI gui) {
        this.fenetre = gui;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        JFileChooser explorateur = new JFileChooser();
        explorateur.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        explorateur.setMultiSelectionEnabled(true);

        if (explorateur.showOpenDialog(fenetre) == JFileChooser.APPROVE_OPTION) {
            try {
                File[] selections = explorateur.getSelectedFiles();

                if (selections == null || selections.length == 0) {
                    return;
                }

                // Sélection d'un dossier unique
                if (selections.length == 1 && selections[0].isDirectory()) {
                    ScanRepertoire moteur = new ScanRepertoire();
                    File f = selections[0];

                    try {
                        Playlist p = moteur.scanner(f.getPath(), f.getName());
                        if (p != null) {
                            fenetre.getPlaylists().add(p);
                            fenetre.rafraichirListePlaylist();
                            fenetre.rafraichirListeMorceaux(p);

                            StringBuilder message = new StringBuilder();
                            message.append("Playlist créée : ").append(p.getNom()).append("\n");
                            message.append(p.getTaille()).append(" morceau(x) ajouté(s)");

                            // Envoie le message et le compteur d'erreurs à la GUI
                            fenetre.afficherResultat("Scan de dossier", message.toString(), moteur.getErreurs());
                        }
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(fenetre, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                    }

                    // Dans le cas de la sélection de fichiers individuels
                } else {
                    Playlist playlistCible = fenetre.getListePlaylist().getSelectedValue();

                    if (playlistCible == null) {
                        JOptionPane.showMessageDialog(fenetre, "Sélectionne une playlist d'abord !");
                        return;
                    }

                    int compteur = 0;
                    int erreurs = 0;

                    for (File fichier : selections) {
                        if (fichier.isFile()) {
                            // On vérifie l'extension avant de tenter la création
                            if (fichier.getName().toLowerCase().endsWith(".mp3")) {
                                try {
                                    FichierMP3 mp3 = new FichierMP3(fichier.getAbsolutePath());
                                    playlistCible.ajouter(mp3);
                                    compteur++;
                                } catch (FichierMP3Exception ex) {
                                    erreurs++;
                                } catch (Exception ex) {
                                    erreurs++;
                                }
                            } else {
                                erreurs++;
                            }
                        }
                    }

                    fenetre.rafraichirListeMorceaux(playlistCible);
                    fenetre.rafraichirListePlaylist();

                    StringBuilder message = new StringBuilder();
                    message.append(compteur).append(" fichier(s) ajouté(s) à la playlist ").append(playlistCible.getNom());

                    // Affiche le résultat avec le compteur d'erreurs
                    fenetre.afficherResultat("Ajout de fichiers", message.toString(), erreurs);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(fenetre, "Erreur lors de l'opération : " + ex.getMessage(), "Erreur système", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}