package projetS3.importer;

import projetS3.core.audio.FichierMP3;
import projetS3.core.audio.FichierMP3Exception;
import projetS3.core.playlist.Playlist;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * importateur de fichier playlist au format XSPF
 * <p>
 *     Cette classe lit un fichier XSPF et reconstruit un objet playlist avec les morceaux indiqués
 *     Les chemins des mp3 sont reconstruits relativement par rapport au dossier contenant la playlist
 * </p>
 * <p>
 *     Limitations :
 *     - Les chemins absolus sont ignorés
 *     - Le JSPF doit être formaté avec une propriété pr ligne
 *     - Seules les balises title et location sont lus
 * </p>
 *
 * @author Sajid
 */
public class ImporterJSPF implements ImporterPlaylist {
    /** Liste des erreurs rencontrées pendant l'import */
    private int erreurs;

    /**
     * Constructeur par défaut
     */
    public ImporterJSPF(){

    }


    @Override
    public int getErreurs() {
        return this.erreurs;
    }

    /**
     * Le coeur de la classe
     * Création d'un objet Playlist à partir du parcours d'un fichier JSPF
     * dont le chemin est donné en paramètre
     * @param cheminFichierPlaylist le chemin vers le fichier JSPF à importer
     * @return la playlist construite avec ses morceaux
     * @throws FileNotFoundException si le fichier JSPF n'existe pas
     * @throws IOException si une erreur de lecture survient
     * @throws NullPointerException si le chemin est null ou vide
     */
    @Override
    public Playlist importer(String cheminFichierPlaylist) throws FileNotFoundException, IOException {

        //remet à 0 les erreurs pour chaque nouvel import
        this.erreurs = 0;

        if (cheminFichierPlaylist == null || cheminFichierPlaylist.isEmpty()) {
            throw new NullPointerException("Le chemin ne peut pas être null ou vide");
        }

        Path pathPlaylist = Path.of(cheminFichierPlaylist);

        //au cas où le .jspf, n'aurait pas de balises <title>, on prend le nom de fichier
        //en tant que titre pour notre application
        // sera remplacé si une balise <title> est trouvée
        String nomFichier = pathPlaylist.getFileName().toString();
        String nomPlaylist = enleverExtension(nomFichier);

        //On recupère le dossier parent de notre playlist (base pour la reconstruction des chemins relatifs des mp3)
        Path dossierParentPlaylist = pathPlaylist.getParent();

        //sécurité si le chemin donné en paramètre était C:/ ou /
        if (dossierParentPlaylist == null) {
            dossierParentPlaylist = Paths.get(".");
        }

        Playlist playlist = new Playlist(nomPlaylist);
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(cheminFichierPlaylist));
            String ligne;

            // Lecture ligne par ligne du fichier JSPF
            while ((ligne = reader.readLine()) != null) {
                ligne = ligne.trim();

                //extraction du titre de la playlist + mise à jour du nom playlist
                if (ligne.contains("\"title\"")) {
                    String titre = extraireTitre(ligne);
                    if (titre != null && !titre.isEmpty()) {
                        nomPlaylist = titre;
                        playlist.setNom(nomPlaylist);
                    }
                }

                // Extraction des chemins de fichiers MP3
                if (ligne.contains("\"location\"")) {
                    String chemin = extraireLocation(ligne);

                    if (chemin == null || chemin.isEmpty()) {
                        continue;
                    }

                    //pour se protéger des fichiers créés sur windows et autrement que avec nos exportateurs
                    chemin = chemin.replace("\\", "/");

                    // Ignorer les chemins absolus (non portables)
                    if (chemin.startsWith("file:") || chemin.contains(":/")) {
                        erreurs++;
                        continue;
                    }

                    // Reconstruire le chemin complet par rapport au dossier de la playlist
                    Path cheminReconstruit = dossierParentPlaylist.resolve(chemin).normalize();

                    // Tentative de création du FichierMP3
                    try {
                        FichierMP3 mp3 = new FichierMP3(cheminReconstruit.toString());
                        playlist.ajouter(mp3);
                    } catch (FichierMP3Exception e) {
                        erreurs++;
                    }
                }
            }

        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Fichier playlist introuvable : " + cheminFichierPlaylist);
        } catch (IOException e) {
            throw new IOException("Erreur lecture : " + cheminFichierPlaylist, e);
        } finally {
            // Fermeture du reader dans tous les cas
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.err.println("Erreur fermeture fichier");
                }
            }
        }

        return playlist;
    }

    /**
     * Retourne l'extension de fichier gérée par cet importateur.
     *
     * @return ".jspf"
     */
    @Override
    public String getExtension() {
        return ".jspf";
    }

    /**
     * Enlève l'extension d'un nom de fichier import
     * @param nomFichier le nom du fichier avec extension
     * @return le nom sans extension
     */
    private String enleverExtension(String nomFichier) {
        //On cherche le dernier point
        int dernierPoint = nomFichier.lastIndexOf(".");
        if (dernierPoint > 0) {
            return nomFichier.substring(0, dernierPoint);
        }
        return nomFichier;
    }


    /**
     *
     * @param ligne la ligne parcourue actuellement
     * @return le chemin relatif correct
     */
    private String extraireLocation(String ligne) {
        int debut = ligne.indexOf("\"location\": [\"");
        if (debut != -1) {
            debut += 14;
            int fin = ligne.indexOf("\"]", debut);
            if (fin != -1) {
                return ligne.substring(debut, fin);
            }
        }
        return null;
    }

    /**
     *
     * @param ligne la ligne parcourue actuellement
     * @return le titre de la playlist correct
     */
    private String extraireTitre(String ligne) {
        int debut = ligne.indexOf("\"title\": \"");
        if (debut != -1) {
            debut += 10;
            int fin = ligne.indexOf("\"", debut);
            if (fin != -1) {
                return ligne.substring(debut, fin);
            }
        }
        return null;
    }
}