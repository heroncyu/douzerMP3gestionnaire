package projetS3.importer;

import projetS3.core.playlist.Playlist;

import java.io.IOException;
import java.io.FileNotFoundException;

/**
 * Interface définissant le contrat pour les importateurs de fichiers playlist
 * <p>
 *     Chaque format de playlist (M3U8,XSPF,JSPF) doit implémenter cette interface
 *     pour permettre l'import de fichiers playlist dans l'application
 * </p>
 *
 * @author Sajid
 * @see ImporterM3U8
 * @see ImporterXSPF
 * @see ImporterJSPF
 */
public interface ImporterPlaylist {
    /**
     * Importe une playlist depuis un fichier
     * @param chemin le chemin vers le fichier playlist à importer
     * @return la playlist construite avec ses morceaux VALIDES
     * @throws FileNotFoundException si le fichier playlist n'existe pas
     * @throws IOException si une erreur de lecture survient
     */
    public Playlist importer(String chemin) throws FileNotFoundException, IOException;

    /**
     * Retourne l'extension de fichier gérée par cet importateur
     * @return l'extension avec le point
     */
    public String getExtension();

    /**
     * Classe permettant d'accéder au compteur d'erreurs
     * @return le compteur d'erreurs obtenues pendant l'importation
     */
    public int getErreurs();



}
