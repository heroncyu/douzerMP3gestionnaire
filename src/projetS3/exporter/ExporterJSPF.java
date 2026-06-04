package projetS3.exporter;

import projetS3.core.audio.FichierMP3;
import projetS3.core.playlist.Playlist;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
/**
 * On implémente  l'exportateur pour le  format JSPF.
 * On utilise des chemins relatifs pour la portabilité.
 * @author Ayoub
 */
public class ExporterJSPF implements ExporterPlaylist {

    /**Compteur des erreurs rencontrées pendant l'export*/
    private int erreurs;

    /**
     * Constructeur par défaut
     */
    public ExporterJSPF(){

    }

    @Override
    public int getErreurs() {
        return this.erreurs;
    }



    @Override
    public void exporter(Playlist playlist, String chemin) throws IOException {

        this.erreurs = 0;

        if (chemin == null || chemin.equals("")) {
            System.err.println("Chemin invalide");
            return;
        }
        if (playlist == null) {
            System.err.println("Playlist null");
            return;
        }

        // Calcul du dossier parent pour le chemin relatif
        Path cheminSortie = Paths.get(chemin).toAbsolutePath();
        Path dossierParent = cheminSortie.getParent();

        if (dossierParent == null) {
            dossierParent = cheminSortie;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(chemin))) {
            writer.write("{");
            writer.newLine();
            writer.write("  \"playlist\": {");
            writer.newLine();
            writer.write("    \"title\": \"" + echapperJSON(playlist.getNom()) + "\",");
            writer.newLine();
            writer.write("    \"track\": [");
            writer.newLine();

            int nbMorceaux = playlist.getTaille();
            for (int i = 0; i < nbMorceaux; i++) {
                FichierMP3 mp3 = playlist.getMorceaux().get(i);

                // on gere le chemin relatif
                Path cheminAbsoluMP3 = Paths.get(mp3.getChemin()).toAbsolutePath();
                String pathFinal;

                try {
                    Path cheminRelatif = dossierParent.relativize(cheminAbsoluMP3);
                    // On remplace les \ par / pour la compatibilité JSON/Web
                    pathFinal = cheminRelatif.toString().replace("\\", "/");

                    // Échapper JSON pour les caractères spéciaux dans le chemin
                    String pathEchappe = echapperJSON(pathFinal);

                    writer.write("      { \"location\": [\"" + pathEchappe + "\"] }");

                    if (i < nbMorceaux - 1) {
                        writer.write(",");
                    }
                    writer.newLine();

                } catch (IllegalArgumentException e) {

                    // Echec si les fichiers sont sur deux disques différents ou si le chemin relatif est impossible
                    System.err.println("Chemin relatif impossible, ignoré " + cheminAbsoluMP3);
                    erreurs++;
                }


            }

            writer.write("    ]");
            writer.newLine();
            writer.write("  }");
            writer.newLine();
            writer.write("}");

        } catch (IOException e) {
            System.err.println("Erreur lors de l'écriture JSPF : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public String getExtension() {
        return ".jspf";
    }


    /**
     * On nettoie le texte pour le format JSPF en utilisant des remplacements successifs.
     * @param texte Le texte brut à transformer.
     * @return Le texte sécurisé pour le JSPF.
     */
    private String echapperJSON(String texte) {
        if (texte == null) {
            return null;
        }

        String resultat = texte;

        resultat = resultat.replace("\\", "\\\\");
        resultat = resultat.replace("\"", "\\\"");

        return resultat;
    }
}