######## Projet Gestionnaire MP3 (Douzer) ########

### Informations sur l'équipe

# Membres du binôme :
EL HAJAM Ayoub
HERON Sajid

# Prérequis :
Java SE 21 
Bibliothèque externe : mp3agic (incluse dans le dossier lib/)

# La Structure du projet :
HERON_ELHAJAM_C10/
├── readme.md
├── rapport/
│   ├── rapportC10.odt
│   └── rapportC10.pdf
├── src/
│   └── projetS3/
│       └── (tous tes .java)
├── lib/
│   └── mp3agic-0.9.1.jar
├── doc/
│   └── (javadoc générée)
└── jar/
    ├── cli.jar
    └── gui.jar

########## L'Utilisation :

Pour le Mode Console (CLI) et le Mode Graphique (GUI) :
Les fichiers JAR se trouvent dans jar/

### Les Commandes Disponibles :

# Pour Afficher l'aide
java -jar jar/cli.jar -h

# Analyser un fichier MP3 et afficher ses métadonnées
java -jar jar/cli.jar -f fichier.mp3

# Explorer un dossier (et ses sous-dossiers) pour lister les MP3
java -jar jar/cli.jar -d ./music/

# Générer une playlist XSPF depuis un dossier
java -jar jar/cli.jar -d ./music/ --xspf -o playlist.xspf

# Générer une playlist M3U8 depuis un dossier
java -jar jar/cli.jar -d ./music/ --m3u8 -o playlist.m3u8

# Générer une playlist JSPF depuis un dossier
java -jar jar/cli.jar -d ./music/ --jspf -o playlist.jspf

### Les options pour le CLI :
- `-h`,--help : Affiche l'aide et les options disponibles
- `-f <fichier>` : Analyse un fichier MP3 spécifique
- `-d <dossier>` : Explore un répertoire (récursivement )
- `-o <fichier>` : Spécifie le fichier de sortie pour la playlist
- `--xspf` : Format de playlist XSPF (XML)
- `--m3u8` : Format de playlist M3U8 (UTF-8)
- `--jspf` : Format de playlist JSPF (JSON)

### Commande Pour le GUI :
java -jar jar/gui.jar

### Fonctionnalités GUI :

Visualiser les métadonnées : Selectionner un fichier MP3 et consulter toutes ses métadonnées (titre, artiste, album, durée, etc...) et visualisation de la cover extraite des métadonnées MP3

Scanner Dossier : Parcourir un dossier et sous-dossier complet et genere une playlist par defaut avec tous les fichiers MP3 scanner et valide 

Selectionner Morceau : Permet de selectionner un morceau directement a partir de la machine de l'utilisateur et l'ajouter a la playlist selectionner  sans passer par le Scan de dossier

Création de playlists personnalisées : Creer une playlist vide et sélectionner manuellement les morceaux à inclure dans une playlist

Sauvegarde de playlists : Exporter les playlists aux formats XSPF, M3U8 ou JSPF

Import de playlist : Importer la playlist aux formats XSPF, M3U8 ou JSPF

Ajouter des Morceaux : Permet de deplacer certains fichier(s) d'une playlist à une autre selon l'envie de l'utilisateur

### La Documentation :
La documentation JavaDoc complète est disponible dans le dossier doc/.
Ouvrir le fichier doc/index.html dans un navigateur pour y accéder.
