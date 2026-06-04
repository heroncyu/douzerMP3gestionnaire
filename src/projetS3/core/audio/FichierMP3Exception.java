package projetS3.core.audio;

/**
 * Exception personnalisée pour la gestion des erreurs liées aux fichierMP3
 * <p>
 *     Cette classe permet d'encapsuler les erreurs techniques(IOException,InvalidDataException,etc.)
 *     dans une exception unique compréhensible pour le reste du projet
 * </p>
 */
public class FichierMP3Exception extends Exception {
    /**
     * Construit une exception avec un simple message
     * @param message le message d'erreur
     */
    public FichierMP3Exception(String message) {
        super(message);
    }
}
