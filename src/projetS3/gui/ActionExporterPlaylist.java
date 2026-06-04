package projetS3.gui;

import projetS3.core.playlist.Playlist;
import projetS3.exporter.ExporterPlaylist;
import projetS3.exporter.ExporterXSPF;
import projetS3.exporter.ExporterJSPF;
import projetS3.exporter.ExporterM3U8;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * Classe qui gère le clic sur le bouton d'exportation.
 * Elle permet de choisir entre les formats M3U8, XSPF et JSPF.
 * @author Ayoub,Sajid
 */
public class ActionExporterPlaylist implements ActionListener {

    private GUI fenetre;

    public ActionExporterPlaylist(GUI gui) {
        this.fenetre = gui;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // On récupère la playlist qui est sélectionnée dans la liste à gauche
        Playlist aExporter = fenetre.getListePlaylist().getSelectedValue();

        // Si l'utilisateur n'a rien sélectionné, on lui dit
        if (aExporter == null) {
            JOptionPane.showMessageDialog(fenetre, "Merci de choisir une playlist dans la liste avant d'exporter.", "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // On demande à l'utilisateur de choisir son format
        String[] formats = {"M3U8", "XSPF", "JSPF"};
        String choixFormat = (String) JOptionPane.showInputDialog(fenetre, "Choisissez le format d'export :", "Format", JOptionPane.QUESTION_MESSAGE, null, formats, formats[0]);

        // Si l'utilisateur clique sur annuler dans la petite liste
        if (choixFormat == null) {
            return;
        }

        ExporterPlaylist exporteur = creerExporteur(choixFormat);


        JFileChooser selecteur = new JFileChooser();
        selecteur.setDialogTitle("Enregistrer la playlist");

        // On prépare un nom de fichier par défaut (nom de la playlist + extension)
        String nomParDefaut = aExporter.getNom() + exporteur.getExtension();
        selecteur.setSelectedFile(new File(nomParDefaut));


        if (selecteur.showSaveDialog(fenetre) == JFileChooser.APPROVE_OPTION) {
            String chemin = selecteur.getSelectedFile().getAbsolutePath();

            // On s'assure que le fichier finit bien par la bonne extension (ex: .m3u8)
            if (!chemin.toLowerCase().endsWith(exporteur.getExtension())) {
                chemin += exporteur.getExtension();
            }

            try {
                // On lance l'exportation réelle
                exporteur.exporter(aExporter, chemin);

                // On prépare le message de succès
                StringBuilder message = new StringBuilder();
                message.append("Playlist exportée : ").append(aExporter.getNom());


                // On passe exporteur.getErreurs() au cas où certains morceaux n'auraient pas pu être écrits
                fenetre.afficherResultat("Export", message.toString(), exporteur.getErreurs());

            } catch (IOException ex) {
                // En cas de problème d'écriture sur le disque
                JOptionPane.showMessageDialog(
                        fenetre,
                        "Erreur lors de l'export : " + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    /**
     * Méthode utilitaire pour fabriquer le bon objet exportateur.
     * @param format Le nom du format (M3U8, XSPF ou JSPF).
     * @return L'objet exportateur correspondant.
     */
    private ExporterPlaylist creerExporteur(String format) {
        if (format.equals("XSPF")) {
            return new ExporterXSPF();
        }
        if (format.equals("JSPF")) {
            return new ExporterJSPF();
        }
        // Par défaut on prend le M3U8
        return new ExporterM3U8();
    }
}