package projetS3.core.playlist;

import projetS3.core.audio.FichierMP3;

import java.util.ArrayList;

/**
 * Représente une collection (playlist) contenant des fichiers MP3
 * <p>
 *     Cette classe sert de conteneur pour manipuler des groupes de musiques
 * </p>
 * @author Sajid
 */
public class Playlist {
    //le nom de la playlist (ex : Mes favoris)
    private String nom;

    //La liste des fichiers MP3
    private final ArrayList<FichierMP3> morceaux;

    /**
     * Constructeur
     * @param nom Le nom initial de la playlist
     */
    public Playlist(String nom) {
        this.nom = nom;
        this.morceaux = new ArrayList<>();
    }

    /**
     * Recupère le nom de la playlist
     * @return le nom
     */
    public String getNom() {
        return nom;
    }

    /**
     * Modifie le nom de la playlist
     * @param nom le nouveau nom
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * Accède à la liste des morceaux
     * @return L'Arraylist des FichierMP3
     */
    public ArrayList<FichierMP3> getMorceaux() {
        return new ArrayList<>(morceaux);
    }

    /**
     * Ajoute un objet FichierMP3 dans notre playlist
     * @param mp3 l'objet FichierMP3 à ajouter
     * @throws NullPointerException si le fichier passé est null
     */
    public void ajouter(FichierMP3 mp3){
        if(mp3 == null) {
            throw new NullPointerException("impossible d'ajouter un fichier null");
        }
        morceaux.add(mp3);
    }

    /**
     * Retourne le nombre de morceaux dans la playlist
     * @return la taille de la playlist
     */
    public int getTaille() {
        return morceaux.size();
    }

    /**
     * Représentation textuelle de la playlist (pour l'affichage dans les listes GUI)
     * @return une chaîne format &quot;Nom (X morceaux)&quot;
     */
    @Override
    public String toString() {
        return nom + " (" + getTaille() + " morceaux)";
    }
}
