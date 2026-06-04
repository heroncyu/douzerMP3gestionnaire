package projetS3.gui;

import javax.swing.SwingUtilities;

/**
 * La Classe de démarrage (Le Main) pour l'interface graphique.
 * Cette classe, elle est séparée de la logique console pour permettre
 * de génerer le  fichier JAR  (gui.jar).
 * @author Ayoub
 */
public class LancerGUI {

    /**
     * Point d'entrée principal pour le mode GUI.
     * @param args Arguments de la ligne de commande.
     */
    public static void main(String[] args) {

        // j'utillise SwingUtilities.invokeLater pour  que la création de l'interface
        // se fasse dans le thread Swing dédié à l'affichage (l'EDT ).
        // pour éviter les freeze et les  bugs visuels.

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // La Création et l'affichage de la fenêtre principale
                new GUI();
            }
        });
    }
}