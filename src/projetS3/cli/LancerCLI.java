package projetS3.cli;

/**
 * Point d'entrée pour l'application en mode Console
 * <p>
 *     Cette classe ne contient aucune logique métier.
 *     Son seul rôle est d'instancier la classe CLI qui contient la logique métier
 *     et de lui passer les arguments reçus
 * </p>
 * @author Sajid
 */
public class LancerCLI {
    /**
     * Méthode main standard de java
     * @param args Le tableau des arguments de la ligne de commande (ex : [&quot;-d&quot;,&quot;Music&quot;])
     */
    public static void main(String[] args){
        //création de l'instance du CLI
        CLI cli = new CLI();

        //démarrage de l'analyse des arguments
        cli.demarrer(args);
    }
}
