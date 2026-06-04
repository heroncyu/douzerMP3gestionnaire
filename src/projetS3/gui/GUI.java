package projetS3.gui;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import projetS3.core.audio.FichierMP3;
import projetS3.core.playlist.Playlist;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import java.awt.FlowLayout;
import java.awt.Image;


/**
 * Fenêtre principale de mon application Douzer.
 *
 * @author Ayoub
 * @author Sajid (Pour la méthode afficherResultat)
 */
public class GUI extends JFrame {

    // Mes composants
    private JList<Playlist> listePlaylist;
    private DefaultListModel<Playlist> modeleP;

    private JList<FichierMP3> listeMorceaux;
    private DefaultListModel<FichierMP3> modeleM;

    private JPanel panneauMetadonnees;
    private ArrayList<Playlist> playlists;

    private JButton jbScanner, jbExporter, jbImporter, jbCreerPlaylist, jbAjouter;
    private JLabel lblTitre, lblArtiste, lblAlbum,lblGenre,lblAnnee,lblImage;

    // Couleurs de l'appli
    private Color grisFonce = new Color(33, 33, 33);
    private Color orange = new Color(150, 70, 0);

    /**
     * Constructeur de la fenêtre.
     * Il initialise les listes et place les éléments sur l'écran.
     */
    public GUI() {
        super("Douzer");

        this.playlists = new ArrayList<>();
        this.modeleP = new DefaultListModel<>();
        this.modeleM = new DefaultListModel<>();

        this.setSize(1230, 830);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout(8, 11));

        this.getContentPane().setBackground(new Color(18, 18, 18));

        this.initComponents();
        this.setupLayout();

        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    /**
     * Crée les boutons, les listes et configure leur style.
     */
    private void initComponents() {
        jbScanner = new JButton("Scanner le Dossier ou Sélectionner Fichier");
        jbExporter = new JButton("Exporter la Playlist");
        jbImporter = new JButton("Importer la Playlist");
        jbCreerPlaylist = new JButton("Creer Une Nouvelle Playlist");
        jbAjouter = new JButton("Ajouter Les Morceaux a la Playlist");

        JButton[] btns = {jbScanner, jbExporter, jbImporter, jbCreerPlaylist,jbAjouter};
        for(JButton b : btns) {
            b.setBackground(orange);
            b.setForeground(Color.WHITE);
            b.setFocusPainted(false);
        }

        jbScanner.addActionListener(new ActionScanner(this));
        jbExporter.addActionListener(new ActionExporterPlaylist(this));
        jbImporter.addActionListener(new ActionChargerPlaylist(this));
        jbCreerPlaylist.addActionListener(new ActionCreerPlaylist(this));
        jbAjouter.addActionListener(new ActionAjouterMorceau(this));

        listePlaylist = new JList<>(modeleP);
        listePlaylist.setBackground(grisFonce);
        listePlaylist.setForeground(Color.WHITE);
        listePlaylist.setSelectionBackground(orange);
        listePlaylist.addListSelectionListener(new ActionSelectionPlaylist(this));

        listeMorceaux = new JList<>(modeleM);
        listeMorceaux.setBackground(grisFonce);
        listeMorceaux.setForeground(Color.WHITE);
        listeMorceaux.setSelectionBackground(orange);
        listeMorceaux.addListSelectionListener(new ActionSelectionMorceau(this));

        panneauMetadonnees = new JPanel();
        panneauMetadonnees.setLayout(new BorderLayout());
        panneauMetadonnees.setBackground(grisFonce);
        panneauMetadonnees.setBorder(new TitledBorder(BorderFactory.createLineBorder(orange), "Informations", 0, 0, null, orange));
        panneauMetadonnees.setPreferredSize(new Dimension(280, 0));

        lblTitre = new JLabel("Titre : -");
        lblArtiste = new JLabel("Artiste : -");
        lblAlbum = new JLabel("Album : -");
        lblGenre = new JLabel("Genre : -");
        lblAnnee = new JLabel("Année : -");

        lblTitre.setForeground(Color.WHITE);
        lblArtiste.setForeground(Color.WHITE);
        lblAlbum.setForeground(Color.WHITE);
        lblGenre.setForeground(Color.WHITE);
        lblAnnee.setForeground(Color.WHITE);

        lblImage = new JLabel("Aucune pochette", SwingConstants.CENTER);
        lblImage.setForeground(Color.WHITE);
        lblImage.setPreferredSize(new Dimension(200, 200));
        lblImage.setBorder(BorderFactory.createLineBorder(orange));

    }

    /**
     * Organise les panneaux dans la fenêtre.
     */
    private void setupLayout() {
        JPanel pnlGauche = new JPanel(new BorderLayout(5, 5));
        pnlGauche.setOpaque(false);
        pnlGauche.setPreferredSize(new Dimension(320, 0));

        JPanel pnlBoutons = new JPanel(new GridLayout(5, 1, 5, 5));
        pnlBoutons.setOpaque(false);
        pnlBoutons.add(jbScanner);
        pnlBoutons.add(jbImporter);
        pnlBoutons.add(jbCreerPlaylist);
        pnlBoutons.add(jbExporter);
        pnlBoutons.add(jbAjouter);

        // la liste de gauche
        JScrollPane sp1 = new JScrollPane(listePlaylist);

        sp1.setBorder(new TitledBorder(BorderFactory.createLineBorder(orange), "Playlists", 0, 0, null, orange));
        sp1.getViewport().setBackground(grisFonce);

        pnlGauche.add(pnlBoutons, BorderLayout.NORTH);
        pnlGauche.add(sp1, BorderLayout.CENTER);

        // au centre : la liste des morceaux
        JScrollPane sp2 = new JScrollPane(listeMorceaux);
        sp2.setBorder(new TitledBorder(BorderFactory.createLineBorder(orange), "Morceaux", 0, 0, null, orange));
        sp2.getViewport().setBackground(grisFonce);

        // a droite on a le  Textes en haut
        JPanel pnlInfosTexte = new JPanel();
        pnlInfosTexte.setLayout(new BoxLayout(pnlInfosTexte, BoxLayout.Y_AXIS));
        pnlInfosTexte.setOpaque(false);
        pnlInfosTexte.add(Box.createVerticalStrut(10)); // Petit espace en haut
        pnlInfosTexte.add(lblTitre);
        pnlInfosTexte.add(Box.createVerticalStrut(5));
        pnlInfosTexte.add(lblArtiste);
        pnlInfosTexte.add(Box.createVerticalStrut(5));
        pnlInfosTexte.add(lblAlbum);
        pnlInfosTexte.add(Box.createVerticalStrut(5));
        pnlInfosTexte.add(lblGenre);
        pnlInfosTexte.add(Box.createVerticalStrut(5));
        pnlInfosTexte.add(lblAnnee);

        panneauMetadonnees.add(pnlInfosTexte, BorderLayout.NORTH);

        JPanel pnlImageContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlImageContainer.setOpaque(false);
        pnlImageContainer.add(lblImage);
        panneauMetadonnees.add(pnlImageContainer, BorderLayout.SOUTH);

        // Ajout final des 3 zones à la fenêtre
        this.add(pnlGauche, BorderLayout.WEST);
        this.add(sp2, BorderLayout.CENTER);
        this.add(panneauMetadonnees, BorderLayout.EAST);
    }

    /**
     * Vide et recharge la liste des noms de playlists à gauche.
     */
    public void rafraichirListePlaylist() {
        modeleP.clear();
        for (Playlist p : playlists) {
            modeleP.addElement(p);
        }
    }
    /**
     * Affiche les morceaux d'une playlist  dans la liste au centre .
     * @param playlist la playlist selectionné
     */
    public void rafraichirListeMorceaux(Playlist playlist) {
        modeleM.clear();
        if (playlist != null) {
            for (FichierMP3 mp3 : playlist.getMorceaux()) modeleM.addElement(mp3);
        }
    }
    /**
     * Met à jour les textes des labels à droite avec les infos et metadonnees d'un MP3.
     * @param mp3 Le fichier MP3 sélectionné
     */
    public void afficheMetadonnees(FichierMP3 mp3) {
        if (mp3 == null) {
            lblTitre.setText("Titre : -");
            lblArtiste.setText("Artiste : -");
            lblAlbum.setText("Album : -");
            lblGenre.setText("Genre : -");
            lblAnnee.setText("Année : -");
            lblImage.setIcon(null);
            lblImage.setText("Aucune pochette");

            return;
        }

        lblTitre.setText("Titre : " + mp3.getTitreAff());
        lblArtiste.setText("Artiste : " + mp3.getArtisteAff());
        lblAlbum.setText("Album : " + mp3.getAlbumAff());
        lblGenre.setText("Genre : " + mp3.getGenreAff());
        lblAnnee.setText("Année : "+ mp3.getAnneeAff());

        // Affichage de l'image de couverture
        try{
            byte[] imageData = mp3.getImageCouverture();

            if (imageData != null) {
                ImageIcon icon = new ImageIcon(imageData);
                Image img = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                lblImage.setIcon(new ImageIcon(img));
                lblImage.setText("");
            } else {
                lblImage.setIcon(null);
                lblImage.setText("Aucune pochette");
            }
        }catch (Exception e){
            lblImage.setIcon(null);
            lblImage.setText("Erreur image");
        }

    }
    // Getters pour que les classes Actions puissent lire les données
    /**
     * @return la liste des morceaux de l'interface graphique.
     */
    public JList<FichierMP3> getListeMorceaux() {
        return listeMorceaux;
    }
    /**
     * @return la liste des playlists de la GUI.
     */
    public JList<Playlist> getListePlaylist() {
        return listePlaylist;
    }
    /**
     * @return La ArrayList contenant les objets playlists.
     */
    public ArrayList<Playlist> getPlaylists() {
        return playlists;
    }

    /**
     * Affiche une fenêtre de résumé après un scan, un import, un export.
     * S'il y a des erreurs, leur nombre est affiché après le message original passé par l'ActionListener en question
     * @param titre Titre de la fenêtre de message.
     * @param message Texte passé par les ActionListener
     * @param nbErreurs compteur du nombre d'erreurs
     */
    public void afficherResultat(String titre, String message, int nbErreurs){
        if(nbErreurs == 0){
            JOptionPane.showMessageDialog(this,message,titre,JOptionPane.INFORMATION_MESSAGE);
        }
        else{
            StringBuilder messageComplet = new StringBuilder();
            messageComplet.append(message).append("\n").append(nbErreurs).append(" erreur(s) sur fichiers .mp3 ignorée(s)");
            JOptionPane.showMessageDialog(this,messageComplet.toString(),titre,JOptionPane.WARNING_MESSAGE);

        }
    }
}