package projetS3.exporter;

import projetS3.core.audio.FichierMP3;
import projetS3.core.playlist.Playlist;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * On implémente  l'exportateur pour le  format M3U8.
 * On utilise des chemins relatifs pour la portabilité.
 * @author Ayoub
 */
public class ExporterM3U8 implements ExporterPlaylist {

    /**Compteur des erreurs rencontrées pendant l'export*/
    private int erreurs;

    /**
     * Constructeur par défaut
     */
    public ExporterM3U8(){

    }

    @Override
    public int getErreurs() {
        return this.erreurs;
    }

    @Override
    public void exporter(Playlist playlist, String chemin) throws IOException {

        this.erreurs = 0;

        if (chemin == null || chemin.isEmpty()) {
            System.err.println("Chemin invalide");
            return;
        }

        if (playlist == null) {
            System.err.println("Playlist null !");
            return;
        }

        if (playlist.getTaille() == 0) {
            System.err.println("Playlist vide.");
            return;
        }

        Path cheminSortie = Paths.get(chemin).toAbsolutePath();
        Path dossierParent = cheminSortie.getParent();

        if (dossierParent == null) {
            dossierParent = cheminSortie;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(chemin))) {
            // entete du fichier M3U8
            writer.write("#EXTM3U");
            writer.newLine();

            // on parcourt tous les morceaux
            for (int i = 0; i < playlist.getTaille(); i++) {
                FichierMP3 mp3 = playlist.getMorceaux().get(i);

                Path cheminAbsoluMP3 = Paths.get(mp3.getChemin()).toAbsolutePath();

                try {
                    // calcul du chemin relatif
                    Path cheminRelatif = dossierParent.relativize(cheminAbsoluMP3);

                    // on remplace les \ par / pour la compatibilité
                    String pathFinal = cheminRelatif.toString().replace("\\", "/");

                    writer.write(pathFinal);
                    writer.newLine();

                } catch (IllegalArgumentException e) {
                    // si les fichiers sont sur des disques différents
                    System.err.println("Chemin relatif impossible, ignoré " + cheminAbsoluMP3);
                    erreurs++;
                }
            }

        } catch (IOException e) {
            System.err.println("Erreur lors de l'écriture du fichier : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public String getExtension() {
        return ".m3u8";
    }
}