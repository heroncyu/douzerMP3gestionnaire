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
 * importateur de fichier playlist au format M3U8
 * <p>
 *     Cette classe lit un fichier XSPF et reconstruit un objet playlist avec les morceaux indiqués
 *     Les chemins des mp3 sont reconstruits relativement par rapport au dossier contenant la playlist
 * </p>
 * <p>
 *     Limitations :
 *     - Les chemins absolus sont ignorés
 *     - TOUTE les lignes commençant par # sont ignorées
 * </p>
 *
 * @author Sajid
 */
public class ImporterM3U8 implements ImporterPlaylist{
    /**Liste des erreurs rencontrées pendant l'import*/
    private int erreurs;

    /**
     * Constructeur par défaut
     */
    public ImporterM3U8(){

    }


    @Override
    public int getErreurs(){
        return this.erreurs;
    }

    /**
     * Le coeur de la classe
     * Création d'un objet Playlist à partir du parcours d'un fichier M3U8
     * dont le chemin est donné en paramètre
     * @param cheminFichierPlaylist le chemin vers le fichier M3U8 à importer/parcourir
     * @return la playlist construite avec ses morceaux
     * @throws FileNotFoundException si le fichier M3U8 n'existe pas
     * @throws IOException si une erreur de lecture survient
     * @throws NullPointerException si le chemin est null ou vide
     */
    @Override
    public Playlist importer(String cheminFichierPlaylist) throws FileNotFoundException,IOException {
        //remet à 0 les erreurs pour chaque nouvel import
        this.erreurs = 0;

        if(cheminFichierPlaylist == null || cheminFichierPlaylist.isEmpty()){
            throw new NullPointerException("Le chemin ne peut pas être null ou vide");
        }

        //Comme le format m3u8 ne gère pas le titre de la playlist
        //On utilise donc le nom du fichier comme titre

        Path pathPlaylist = Path.of(cheminFichierPlaylist);
        String nomFichier = pathPlaylist.getFileName().toString();
        String nomPlaylist = enleverExtension(nomFichier);

        //On recupère le dossier parent de notre playlist (base pour la reconstruction des chemins relatifs)
        Path dossierParentPlaylist = pathPlaylist.getParent();

        //sécurité si le chemin donné en paramètre était C:/ ou /
        if(dossierParentPlaylist == null){
            dossierParentPlaylist = Paths.get(".");
        }

        Playlist playlist = new Playlist(nomPlaylist);

        BufferedReader reader = null;

        try{
            reader = new BufferedReader(new FileReader(cheminFichierPlaylist));
            String ligne;

            //lecture ligne par ligne du fichier M3U8
            while((ligne = reader.readLine()) != null){
                ligne = ligne.trim();

                //pour se protéger des fichiers créés sur windows et autrement que avec nos exportateurs
                ligne = ligne.replace("\\", "/");

                // Ignorer lignes vides et commentaires
                if(ligne.isEmpty() || ligne.startsWith("#")){
                    continue;
                }

                //ignore les chemins absolus (non portables)
                if (ligne.startsWith("file:") || ligne.contains(":/")) {
                    erreurs++;
                    continue;
                }
                //Reconstruire le chemin complet par rapport au dossier de la playlist
                Path cheminReconstruit = dossierParentPlaylist.resolve(ligne).normalize();

                //tentative de création du FichierMP3
                try{
                    FichierMP3 mp3 = new FichierMP3(cheminReconstruit.toString());
                    playlist.ajouter(mp3);
                }
                catch (FichierMP3Exception e){
                    erreurs++;
                }
            }

        }
        catch (FileNotFoundException e) {
            throw new FileNotFoundException("Fichier playlist introuvable :" + cheminFichierPlaylist);
        }
        catch (IOException e){
            throw new IOException("Erreur lecture :"+ cheminFichierPlaylist,e);
        }
        finally {
            // Fermeture du reader dans tous les cas
            if(reader != null){
                try{
                    reader.close();
                } catch (IOException e) {
                    System.err.println("Erreur fermeture fichier");
                }
            }

        }

        return playlist;
    }

    /**
     * Retourne l'extension de fichier gérée par cet importateur
     * @return ".m3u8"
     */
    @Override
    public String getExtension() {
        return ".m3u8";
    }

    /**
     * méthode qui permet de retirer proprement l'extension d'un fichier quelconque
     * @param nomFichier le nom ou chemin sur lequel le travail sera réalisé
     * @return le nom ou chemin propre, sans extension
     */
    private String enleverExtension(String nomFichier) {
        //On cherche le dernier point
        int dernierPoint = nomFichier.lastIndexOf(".");
        if (dernierPoint > 0) {
            return nomFichier.substring(0, dernierPoint);
        }
        return nomFichier;
    }
}
