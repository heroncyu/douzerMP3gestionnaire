package projetS3.exporter;
import projetS3.core.playlist.Playlist;
import java.io.IOException;

/**
 * L'Interface qui définit le contrat pour tout les exportateurs de playlist
 * Chaque classe implémentant cette interface doit être capable de générer un fichier
 * de playlist dans un format spécifique. (M3U8,XSPF,JSPF,etc...)
 * @author Ayoub
 */
public interface ExporterPlaylist {
    /**
     * Exporte la playlist donnée vers un fichier sur le disque
     * @param playlist La playlist contenant les morceaux à sauvegarder
     * @param chemin Le chemin complet du fichier de destination
     * @throws IOException En cas d'erreur d'écriture (disque pleins droit d'accès)
     */
    public void exporter(Playlist playlist,String chemin) throws IOException;

    /**
     * On retourne l'extension de fichier associée au format d'export
     * @return l'extension sous forme de chaîne de caractères
     */
    public String getExtension();

    /**
     * Méthode permettant d'accéder au compteur d'erreurs
     * @return le compteur d'erreurs obtenues pendant l'exportation
     */
    public int getErreurs();

}