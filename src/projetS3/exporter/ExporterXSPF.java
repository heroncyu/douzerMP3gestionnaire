package projetS3.exporter;

import projetS3.core.audio.FichierMP3;
import projetS3.core.playlist.Playlist;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

/**
 * Implémentation de l'exportateur pour le format XSPF (XML shareable playlist format)
 * <p>
 *     Cette classe génère un fichier XSPF contenant la liste des pistes.
 *     Privilégie les chemins relatifs pour la portabilité (si le fichier est sur le même disque)
 * </p>
 * @author Sajid
 */

public class ExporterXSPF implements ExporterPlaylist {
    private int erreurs;

    /**
     * Constructeur par défaut
     */
    public ExporterXSPF(){

    }

    @Override
    public String getExtension() {
        return ".xspf";
    }

    @Override
    public int getErreurs() {
        return this.erreurs;
    }

    /**
     * Exporte la playlist donnée vers un fichier playlist XSPF sur le disque
     * @param playlist La playlist contenant les morceaux à sauvegarder
     * @param cheminFichierSortie Le chemin complet du fichier de destination
     * @throws IOException En cas d'erreur d'écriture (disque plein,s droit d'accès)
     */
    @Override
    public void exporter(Playlist playlist, String cheminFichierSortie) throws IOException {
        this.erreurs = 0;

        if (cheminFichierSortie == null || cheminFichierSortie.isEmpty()) {
            System.err.println("Chemin invalide");
            return;
        }

        if (playlist == null) {
            System.err.println("Playlist null !");
            return;
        }

        BufferedWriter writer = null;

        try {
            //Conversion en chemin absolu puis récupération du dossier parent
            Path cheminSortie = Paths.get(cheminFichierSortie).toAbsolutePath();
            Path dossierParent = cheminSortie.getParent();

            // securité pour la racine du disque (C:/ n'a pas de dossier parent)
            if (dossierParent == null) {
                dossierParent = cheminSortie;
            }

            // ouverture du flux d'écriture
            writer = new BufferedWriter(new FileWriter(cheminFichierSortie));

            // ecriture de l'en tête
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            writer.newLine();
            writer.write("<playlist version=\"1\" xmlns=\"http://xspf.org/ns/0/\">");
            writer.newLine();
            writer.write("  <title>" + echapperXML(playlist.getNom()) + "</title>");
            writer.newLine();
            writer.write("  <trackList>");
            writer.newLine();

            // parcours des morceaux de notre playlist
            Iterator<FichierMP3> it = playlist.getMorceaux().iterator();

            while (it.hasNext()) {
                FichierMP3 mp3 = it.next();

                //chemin absolu du mp3
                Path cheminAbsoluMP3 = Paths.get(mp3.getChemin()).toAbsolutePath();

                String cheminRelatifMP3;

                try {
                    // tentative de calcul du chemin relatif, si impossible, génère une IllegalArgumentException
                    Path cheminRelatif = dossierParent.relativize(cheminAbsoluMP3);
                    cheminRelatifMP3 = cheminRelatif.toString().replace("\\", "/");

                    writer.write("    <track>");
                    writer.newLine();

                    writer.write("      <location>" + echapperXML(cheminRelatifMP3) + "</location>");
                    writer.newLine();


                    //duree en millisecondes pour XSPF
                    writer.write("      <duration>" + (mp3.getDuree() * 1000) + "</duration>");
                    writer.newLine();

                    if (mp3.getTitre() != null) {
                        writer.write("      <title>" + echapperXML(mp3.getTitre()) + "</title>");
                        writer.newLine();
                    }

                    if (mp3.getArtiste() != null) {
                        writer.write("      <creator>" + echapperXML(mp3.getArtiste()) + "</creator>");
                        writer.newLine();
                    }
                    if (mp3.getAlbum() != null) {
                        writer.write("      <album>" + echapperXML(mp3.getAlbum()) + "</album>");
                        writer.newLine();
                    }

                    writer.write("    </track>");
                    writer.newLine();

                } catch (IllegalArgumentException e) {
                    // (echec) si les fichiers sont sur deux disques différents, relatif impossible
                    System.err.println("Chemin relatif impossible, ignoré " + cheminAbsoluMP3);
                    erreurs++;
                }



            }

            // fermeture des balises
            writer.write("  </trackList>");
            writer.newLine();
            writer.write("</playlist>");
            writer.newLine();

        } catch (IOException e) {
            System.err.println("Erreur export : " + e.getMessage());
            throw e;
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * méthode utilitaires pour échapper les caractères spéciaux en XML
     * remplace & < > " ' par leurs equivalents en XML
     * @param texte La chaîne brute
     * @return La chaîne sécurisée pour le XML
     */
    private String echapperXML(String texte) {
        if (texte == null) {
            return "";
        }
        return texte.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&apos;");
    }
}