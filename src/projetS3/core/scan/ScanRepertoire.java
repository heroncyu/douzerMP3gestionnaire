package projetS3.core.scan;

import projetS3.core.audio.FichierMP3;
import projetS3.core.audio.FichierMP3Exception;

import projetS3.core.playlist.Playlist;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Classe Scan qui permet de scanner un répertoire du système de fichiers
 * pour identifier et extraire les fichiers audio au format MP3.
 * Le scan est récursif, il parcourt donc également tous les sous-dossiers
 * du répertoire cible.
 * @author Ayoub
 */
public class ScanRepertoire {

    /**Compteur des erreurs rencontrées pendant le scan*/
    private int erreurs;

    /**
     * Constructeur par défaut
     */
    public ScanRepertoire(){

    }

    /**
     * Retourne le nombre d'erreurs rencontrées lors du dernier scan.
     * @return le nombre d'erreurs
     */
    public int getErreurs(){
        return this.erreurs;
    }

    /**
     * On scanne un répertoire donné et on génère une {@link Playlist} qui contient tous les fichiers MP3 valides trouvés.
     * @param cheminRepertoire Le chemin vers le dossier à scanner.
     * @param nomPlaylist      Le nom de la  playlist en sortie .
     * @return Une playlist de {@link Playlist} qui contient  les fichiers de {@link FichierMP3}.
     * @throws IllegalArgumentException Si le chemin fourni n'existe pas ou n'est pas un répertoire.
     * @throws NullPointerException  Si on a l'un des arguments qui est null.
     */

    public Playlist scanner(String cheminRepertoire, String nomPlaylist){

        this.erreurs = 0;

        if (cheminRepertoire == null || nomPlaylist == null){
            throw new NullPointerException("Le chemin ou le nom ne peuvent pas être null");
        }

        File doss = new File(cheminRepertoire);
        if (!doss.exists() || !doss.isDirectory()) {
            throw new IllegalArgumentException("Le Chemin est invalide : " + cheminRepertoire);
        }

        Playlist playlist = new Playlist(nomPlaylist);
        scannerRecursif(doss, playlist);

        if(playlist.getTaille() == 0 && erreurs == 0){
            System.err.println("Aucun fichier MP3 trouvé dans le dossier");
        }

        return playlist;
    }

    /**
     * Méthode privée qui parcourt les dossiers et sous-dossiers.
     * Elle filtre les fichiers par extension et vérifie leur type MIME avant de les ajouter.
     * @param dossier  Le dossier actuel en cours d'exploration.
     * @param playlist La playlist en cours de remplissage.
     */
    private void scannerRecursif(File dossier, Playlist playlist) {
        if (dossier == null || playlist == null) {
            return;
        }
        if (!dossier.isDirectory()) {
            return;
        }

        File[] contenu = dossier.listFiles();
        if (contenu == null) {
            return;
        }

        for (File element : contenu) {
            if (element.isDirectory()) {
                scannerRecursif(element, playlist);
            } else if (element.isFile()) {
                if (aExtensionMp3(element)) {
                    Path path = element.toPath();
                    String chemin = path.toString();

                    try {
                        String mime = Files.probeContentType(path);
                        if (mime != null && !"audio/mpeg".equalsIgnoreCase(mime)) {
                            String msg = "on a un MP3 pas valide : " + chemin;
                            System.err.println(msg);
                            erreurs++;
                            continue;
                        }

                        playlist.ajouter(new FichierMP3(chemin));

                    } catch (IOException e) {
                        String msg = " Erreur de  lecture : " + chemin;
                        System.err.println(msg);
                        erreurs++;
                    }
                    catch(FichierMP3Exception e) {
                        System.err.println(e.getMessage());
                        erreurs++;
                    }
                }
            }
        }
    }

    /**
     * Vérifie si un fichier possède l'extension ".mp3" (qui est  insensible à la casse).
     * @param f Le fichier a tester.
     * @return true si l'extension est .mp3, false si ce n'est pas un mp3.
     */
    private boolean aExtensionMp3(File f) {
        String nom = f.getName().toLowerCase();
        return nom.endsWith(".mp3");
    }
}
