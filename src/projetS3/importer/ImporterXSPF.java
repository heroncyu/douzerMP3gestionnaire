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
 *     - Les balises doivent être sur une seule ligne
 *     - Seules les balises title et location sont lus
 * </p>
 *
 * @author Sajid
 */
public class ImporterXSPF implements ImporterPlaylist{
    /**Liste des erreurs rencontrées pendant l'import*/
    private int erreurs;

    public ImporterXSPF(){

    }


    @Override
    public int getErreurs(){
        return this.erreurs;
    }

    /**
     * Le coeur de la classe
     * Création d'un objet Playlist à partir du parcours d'un fichier XSPF
     * dont le chemin est donné en paramètre
     * @param cheminFichierPlaylist le chemin vers le fichier XSPF à importer
     * @return la playlist construite avec ses morceaux
     * @throws FileNotFoundException si le fichier XSPF n'existe pas
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

        Path pathPlaylist = Path.of(cheminFichierPlaylist);

        //au cas où le .xspf, n'aurait pas de balises <title>, on prend le nom de fichier
        //en tant que titre pour notre application
        // sera remplacé si une balise <title> est trouvée

        String nomFichier = pathPlaylist.getFileName().toString();
        String nomPlaylist = enleverExtension(nomFichier);

        //On recupère le dossier parent de notre playlist (base pour la reconstruction des chemins relatifs des mp3)
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
            boolean aTitre = false;

            //lecture ligne par ligne du fichier XSPF
            while((ligne = reader.readLine()) != null){
                ligne = ligne.trim();

                //pour se protéger des fichiers créés sur windows et autrement que avec nos exportateurs
                ligne = ligne.replace("\\", "/");

                //extraction du titre de la playlist + mise à jour du nom playlist
                if(ligne.contains("<title>") && ligne.contains("</title>") && !aTitre){
                    int debut = ligne.indexOf("<title>")+7;
                    int fin = ligne.indexOf("</title>");
                    nomPlaylist = deechapperXML(ligne.substring(debut,fin));
                    playlist.setNom(nomPlaylist);
                    aTitre = true;
                }

                //extraction des chemins des fichiers mp3
                if (ligne.contains("<location>") && ligne.contains("</location>")){
                    int debut = ligne.indexOf("<location>") + 10;
                    int fin = ligne.indexOf("</location>");
                    String chemin = deechapperXML(ligne.substring(debut,fin));

                    //ignore les chemins absolus (non portables)
                    if (chemin.startsWith("file:") || chemin.contains(":/")) {
                        erreurs++;
                        continue;
                    }

                    //Reconstruire le chemin complet par rapport au dossier de la playlist
                    Path cheminReconstruit = dossierParentPlaylist.resolve(chemin).normalize();

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
     * @return ".xspf"
     */
    @Override
    public String getExtension() {
        return ".xspf";
    }

    /**
     * méthode utilitaires pour dé-échapper les caractères spéciaux en XML
     * @param texte le texte à dé-echapper
     * @return le texte avec les caractères normaux
     */
    private String deechapperXML(String texte){
        if(texte == null){
            return "";
        }
        return texte.replace("&amp;","&").replace("&lt;","<").replace("&gt;",">").replace("&quot;","\"").replace("&apos;","'");
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
