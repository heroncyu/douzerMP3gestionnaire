package projetS3.core.audio;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.mpatric.mp3agic.InvalidDataException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

/**
 * Représente un fichier MP3 physique sur le disque et ses métadonnées (Tags ID3)
 * <p>
 *     Cette classe gère la complexité de la lecture binaire via la librairie mp3agic
 *     Elle garantit que l'objet ne peut être créé que si le fichier est un MP3 valide et accessible
 *     Elle gère automatiquement l'extraction des tags
 * </p>
 * @author Sajid
 */
public class FichierMP3 {

    // Attributs représentant les métadonnées principales

    //Chemin absolu du fichier sur le disque
    private String chemin;

    //Titre du morceau, issu des tags ou du nom du fichier
    private String titre;

    //Nom de l'artiste
    private String artiste;

    //Nom de l'album
    private String album;

    //Anéee de sortie
    private String annee;

    //Genre de la musique
    private String genre;

    //Durée en secondes
    private int duree;

    //Image de couverture
    private byte[] imageCouverture;



    /**
     * Constructeur qui tente d'ouvrir le fichier et d'extraire ses informations
     * <p>
     *     C'est ici que toute la validation est effectuée. Si le fichier n'est pas conforme,
     *     l'objet n'est pas crée et une exception personnalisée est levée.
     *     Le constructeur effectue plusieurs validations :
     *     - Vérifie que le chemin n'est pas null
     *     - Vérifie que le fichier existe
     *     - Vérifie le type MIME
     *     - Parse le fichier avec mp3agic
     *     - Extrait les métadonnées disponibles
     * </p>
     *
     *
     *
     * @param chemin le chemin absolu vers le fichier
     * @throws FichierMP3Exception si le fichier est introuvable, illisible, corrompu ou n'est pas un MP3
     */
    public FichierMP3(String chemin) throws FichierMP3Exception{
        //vérification de base : mauvais chemin
        if(chemin == null || chemin.trim().isEmpty()){
            throw new FichierMP3Exception("le chemin ne peut être null ou vide");
        }

        Path path = Path.of(chemin).toAbsolutePath().normalize();
        this.chemin = path.toString();


        //verification d'existence du fichier
        if(!Files.exists(path)){
            throw new FichierMP3Exception("Fichier introuvable : "+chemin);
        }

        //verification du type MIME (pour rejeter les faux fichiers .mp3 qui sont en réalité des .txt)
        try{
            String mime = Files.probeContentType(path);
            if(!"audio/mpeg".equals(mime)){
                throw new FichierMP3Exception("n'est pas MP3 valide : "+chemin);
            }

        }catch(IOException e){
            throw new FichierMP3Exception("erreur lecture : "+chemin);
        }

        //extraction des métadonnées avec la librarie externe
        try{
            Mp3File mp3 = new Mp3File(path); // c'est ici que mp3agic parcourt notre fichier, c'est cette ligne qui génère les exceptions
            this.duree = (int)mp3.getLengthInSeconds();

            // priorité aux tags récents (v2)
            if(mp3.hasId3v2Tag()){
                ID3v2 tag = mp3.getId3v2Tag();
                extraireTags(tag);
                try{
                    this.imageCouverture = tag.getAlbumImage();
                }catch (Exception e){
                    this.imageCouverture = null;
                }
            }
            // tags (v1) sinon
            else{
                if(mp3.hasId3v1Tag()){
                    ID3v1 tag = mp3.getId3v1Tag();
                    extraireTags(tag);
                    this.imageCouverture = null;
                }

            }
            //si aucun tag n'est trouvé, on se contente d'utiliser le nom du fichier comme titre par défaut
            if(titre == null) {
                this.titre = nomFichier();
            }
        }
        catch(InvalidDataException e){
            throw new FichierMP3Exception("Fichier MP3 corrompu : "+chemin);
        }
        catch(UnsupportedTagException e){
            throw new FichierMP3Exception("Les tags du fichier sont dans un format non supporté: "+chemin);
        }
        catch(IllegalArgumentException e) {
            throw new FichierMP3Exception("Chemin invalide : "+chemin);
        }
        catch(IOException e){
            throw new FichierMP3Exception("Erreur de lecture : "+chemin);
        }

    }

    /**
     * Extrait le nom du fichier sans son extension .mp3
     * @return le titre, basé sur le nom du fichier
     */
    private String nomFichier(){
        Path path = Path.of(chemin);
        String nom = path.getFileName().toString();

        //On cherche le dernier point
        int dernierPoint = nom.lastIndexOf(".");

        if(dernierPoint > 0){
            return nom.substring(0,dernierPoint); //on coupe juste l'extension
        }
        return nom;
    }

    /**
     * Remplit les attributs à partir d'un tag reçu (que ça soit ID3v1Tag,ID3v22Tag,etc)
     * @param tag le tag à extraire
     */
    private void extraireTags(ID3v1 tag){
        this.titre = normaliserTag(tag.getTitle());
        this.artiste = normaliserTag(tag.getArtist());
        this.album = normaliserTag(tag.getAlbum());
        this.annee = normaliserTag(tag.getYear());
        this.genre = normaliserTag(tag.getGenreDescription());
    }

    /**
     * Nettoie une chaîne issue des tags ID3
     * <p>
     *     permet de gérer les espaces en trop et retourne null si vide
     * </p>
     * @param valeur la chaîne brute
     * @return la chaîne nettoyée ou null si vide
     */
    private String normaliserTag(String valeur) {
        if(valeur == null){
            return null;
        }
        String trim = valeur.trim();
        if(trim.isEmpty()) {
            return null;
        }
        return trim;

    }

    //GETTERS

    /**
     *
     * @return le chemin du fichier
     */
    public String getChemin() {
        return this.chemin;
    }

    /**
     *
     * @return le titre du morceau (peut être null)
     */
    public String getTitre() {
        return this.titre;
    }

    /**
     *
     * @return le nom de l'artiste du morceau (peut être null)
     */
    public String getArtiste() {
        return this.artiste;
    }

    /**
     *
     * @return le nom de l'album du morceau (peut être null)
     */
    public String getAlbum() {
        return this.album;
    }

    /**
     *
     * @return l'année de sortie du morceau(peut être null)
     */
    public String getAnnee() {
        return this.annee;
    }

    /**
     *
     * @return le genre musical du morceau (peut être null)
     */
    public String getGenre() {
        return this.genre;
    }

    /**
     *
     * @return la durée en secondes
     */
    public int getDuree() {
        return this.duree;
    }

    public byte[] getImageCouverture() {
        return imageCouverture;
    }

    // GETTERS D'AFFICHAGE
    // Ces méthodes sont utilisées par le GUI,le toString et le CLI
    // pour éviter d'afficher "null" à l'écran

    /**
     *
     * @return l'artiste pour affichage, "Inconnu" si null
     */
    public String getArtisteAff() {
        if(artiste != null) {
            return this.artiste;
        }
        return "Inconnu";
    }
    /**
     *
     * @return le titre pour affichage, "Inconnu" si null
     */
    public String getTitreAff() {
        if(titre != null) {
            return this.titre;
        }
        return "Inconnu";
    }
    /**
     *
     * @return l'album pour affichage, "Inconnu" si null
     */
    public String getAlbumAff() {
        if(album != null) {
            return this.album;
        }
        return "Inconnu";
    }
    /**
     *
     * @return le genre pour affichage, "Inconnu" si null
     */
    public String getGenreAff() {
        if(genre != null) {
            return this.genre;
        }
        return "Inconnu";
    }
    /**
     *
     * @return l'année pour affichage, "Inconnu" si null
     */
    public String getAnneeAff() {
        if(annee != null) {
            return this.annee;
        }
        return "Inconnu";
    }

    @Override
    /**
     * Retourne une représentation textuelle du fichier MP3
     * @return une chaîne contenant le titre,l'artiste et la durée en secondes
     */
    public String toString() {
        return getArtisteAff() + " - " + getTitreAff() + " (" + getDuree() + "s" + ")";
    }
}