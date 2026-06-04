package projetS3.cli;

import projetS3.core.audio.FichierMP3;
import projetS3.core.audio.FichierMP3Exception;

import projetS3.core.playlist.Playlist;
import projetS3.core.scan.ScanRepertoire;
import projetS3.exporter.ExporterJSPF;
import projetS3.exporter.ExporterM3U8;
import projetS3.exporter.ExporterPlaylist;
import projetS3.exporter.ExporterXSPF;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;

/**
 * Classe principal pour l'interface en ligne de commande (CLI)
 * <p>
 * Cette classe gère l'analyse des arguments passés au programme lors de son lancement
 * et dirige l'éxécution vers les fonctionnalités appropriées (affichage d'aide, analyse de fichier,
 * scan de dossier, export)
 * </p>
 *@author Sajid
 */
public class CLI {

    /**
     * Constructeur par défaut
     */
    public CLI() {
        //rien à initialiser pour l'instant
    }

    /**
     * Méthode principale qui regarde le premier argument afin de savoir comment procéder
     *
     * @param args Le tableau des arguments reçus depuis la ligne de commande
     */

    public void demarrer(String[] args) {
        // on vérifie le cas où aucun argument n'est fourni
        if (args == null || args.length == 0) {
            System.err.println("Erreur : Aucun paramètre spécifié");
            return;
        }
        String commande = args[0];

        if (commande.equals("-h") || commande.equals("--help")) {
            afficherAide();
        } else {
            if (commande.equals("-f")) {
                // Option -f : Analyse d'un fichier unique
                if (args.length < 2) {
                    System.err.println("Erreur : Chemin du fichier manquant après -f");
                } else {
                    afficherInfosFichier(args[1]);
                }
            } else {
                if (commande.equals("-d")) {
                    // Option -d : Scan d'un répertoire complet
                    if (args.length < 2) {
                        System.err.println("Erreur : Chemin du dossier manquant après -d");
                    } else {
                        //On passe tous les arguments pour gérer les options d'export éventuelles
                        scannerDossier(args);
                    }
                } else {
                    //Commande inconnue
                    System.err.println("Commande inconnue : " + commande);
                }
            }
        }


    }

    /**
     * Affiche le manuel d'utilisation du programme dans la console.
     * Liste toutes les options disponibles et leur syntaxe
     */

    private void afficherAide() {
        System.out.println("==========================================");
        System.out.println("       GESTIONNAIRE MP3 - AIDE CLI");
        System.out.println("==========================================");
        System.out.println("Usage :");
        System.out.println("  -h,--help               : Afficher cette aide.");
        System.out.println("  -f <fichier>      : Analyser et afficher les métadonnées d'un fichier MP3.");
        System.out.println("  -d <dossier>      : Scanner un répertoire pour lister les fichiers MP3.");
        System.out.println("Options d'export (à utiliser avec -d) :");
        System.out.println("  -o <nom_fichier>  : Exporter la playlist générée vers un fichier.");
        System.out.println("  --xspf            : Format de sortie XSPF.");
        System.out.println("  --m3u8            : Format de sortie M3U8.");
        System.out.println("  --jspf            : Format de sortie JSPF.");
        System.out.println("Exemple :");
        System.out.println("  java -jar cli.jar -d \"C:/Music\" -o \"MaListe\" --xspf");
        System.out.println("==========================================");
    }

    /**
     * Tente de lire un fichier MP3 spécifique et d'afficher ses métadonnées
     * Gère les erreurs si le fichier est introuvable ou corrompu
     *
     * @param chemin Le chemin absolu ou relatif vers le fichier MP3
     */

    private void afficherInfosFichier(String chemin) {
        System.out.println(">> Analyse du fichier : " + chemin);
        try {
            // tentative de création de l'objet FichierMP3 et gestions des erreurs possibles
            FichierMP3 mp3 = new FichierMP3(chemin);

            // Affichage formaté des résultats
            System.out.println("------------------------------------");
            System.out.println("[SUCCÈS] Fichier lu correctement.");
            System.out.println(" Chemin :"+ mp3.getChemin());
            System.out.println(" Titre   : " + mp3.getTitreAff());
            System.out.println(" Artiste : " + mp3.getArtisteAff());
            System.out.println(" Album   : " + mp3.getAlbumAff());
            System.out.println(" Année   : " + mp3.getAnneeAff());
            System.out.println(" Genre   : " + mp3.getGenreAff());
            System.out.println(" Durée   : " + mp3.getDuree() + " secondes");
            System.out.println("------------------------------------");
        } catch (FichierMP3Exception e) {
            // Erreur métier (fichier non MP3, corrompu, etc.)
            System.err.println("[ERREUR]");
            System.err.println("Raison : " + e.getMessage());
        } catch (Exception e) {
            //erreur inattendue
            System.err.println("[ERREUR INATTENDUE] " + e.getMessage());
        }
    }

    /**
     * Scanne un répertoire, génère une playlist en mémoire et gère l'exportation
     * si l'option -o est présente dans les arguments
     *
     * @param args Le Tableau complet des arguments pour analyser les options
     */

    private void scannerDossier(String[] args) {
        String cheminDossier = args[1];
        System.out.println(">> Scan du répertoire : " + cheminDossier);
        // 1. Exécution du scan

        ScanRepertoire scanner = new ScanRepertoire();
        // Le nom de la playlist est temporaire, il sera remplacé par le nom du fichier si export

        Playlist playlist;

        try{
            playlist = scanner.scanner(cheminDossier, "PlaylistTemp");
        }
        catch(IllegalArgumentException e){
            System.err.println("Erreur : " + e.getMessage());
            return;
        }


        System.out.println("[INFO] Scan terminé.");
        System.out.println(playlist.getTaille() + " fichiers MP3 valides trouvés.");
        if(scanner.getErreurs() > 0){
            System.out.println("[ATTENTION] "+scanner.getErreurs()+ " fichier(s) ignoré(s)");
        }

        //2. Analyse des arguments suivants
        // On cherche -o et le format, peu importe l'ordre

        String cheminFichierSortie = null;
        ExporterPlaylist exporteurChoisi = null;

        // On utilise un booléen pour savoir si on doit continuer
        boolean erreurDetectee = false;

        // On commence à l'index 2 (après -d Dossier)
        int i = 2;

        // Tant que on a des arguments et qu'il n'y a pas d'erreur
        while(i < args.length && !erreurDetectee) {
            String arg = args[i];

            //Cas option -o
            if (arg.equals("-o")) {
                // on verifie s'il reste un argument après, pour le nom/localisation de l'export
                if (i + 1 < args.length) {
                    cheminFichierSortie = args[i + 1];
                    i = i+2;
                }
                else {
                    erreurDetectee = true;
                }
            }
            else {
                if (arg.equals("--xspf")) {
                    exporteurChoisi = new ExporterXSPF();
                    i++;
                }
                else {
                    if (arg.equals("--m3u8")) {
                        exporteurChoisi = new ExporterM3U8();
                        i++;
                    }
                    else {
                        if (arg.equals("--jspf")) {
                            exporteurChoisi = new ExporterJSPF();
                            i++;
                        }
                        else{
                            // Dans le cas où l'on rencontre un argument inconnu (on l'ignore et avance)
                            i++;
                        }
                    }
                }
            }


        }
        if(erreurDetectee){
            System.err.println("Erreur : nom de fichier manquant après l'option -o");
        }

        else{
            if(cheminFichierSortie != null){
                // export demandé correctement
                lancerExport(playlist,cheminFichierSortie,exporteurChoisi);
            }
            else{
                if(exporteurChoisi != null){
                    // on ne peut exporter un fichier .xspf/.m3u8/.jspf si on a pas fait -o !
                    System.err.println("Erreur : vous avez demandé un format d'export("+exporteurChoisi.getExtension() +
                            "), mais vous avez oublié de spécifier l'option -o");
                }
                else{
                    // ni nom, ni format -> scan simple réussi

                    Iterator<FichierMP3> it = playlist.getMorceaux().iterator();

                    while(it.hasNext()){
                        FichierMP3 mp3 = it.next();
                        System.out.println(mp3.getArtisteAff() + " - " + mp3.getTitreAff() + " (" + mp3.getDuree() + "s)");
                    }
                }



            }
        }

    }


    /**
     * Gère la logique finale de l'exportation (nommage, extension, écriture).
     * @param playlist la playlist à exporter
     * @param cheminFichierSortie le chemin de destination
     * @param exporteur l'exportateur utilisé
     */
    private void lancerExport(Playlist playlist, String cheminFichierSortie, ExporterPlaylist exporteur) {

        // 1. Gestion du format par défaut (m3u8)
        if (exporteur == null) {
            System.out.println("Aucun format précisé (--xspf, etc.), export par défaut en M3U8.");
            exporteur = new ExporterM3U8();
        }

        // 2. Gestion de l'extension
        // Si l'utilisateur a oublié ".xspf", on le rajoute
        if (!cheminFichierSortie.toLowerCase().endsWith(exporteur.getExtension())) {
            cheminFichierSortie += exporteur.getExtension();
        }

        // 3. MISE À JOUR DU NOM DE LA PLAYLIST
        // Si le chemin était "C:/Demo/soiree.xspf, la playlist s'appellera "Soiree"

        Path path = Path.of(cheminFichierSortie);
        String nomSeul = path.getFileName().toString();
        String titrePropre = enleverExtension(nomSeul);

        playlist.setNom(titrePropre);

        // 4. Écriture sur le disque
        try {
            System.out.println(">> Création du fichier : " + titrePropre);

            // C'est ici que le fichier est créé ou écrasé
            exporteur.exporter(playlist, cheminFichierSortie);

            System.out.println("[SUCCÈS] Playlist exportée correctement !");

        }
        catch (IOException e) {
            System.err.println("[ERREUR] Impossible d'écrire le fichier : " + e.getMessage());
        }

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



