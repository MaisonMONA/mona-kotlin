# Latest MONA 1.0 ALPHA Version:

Initial internal app launch for testing within a specific range of people. Mona is a Kotlin 
native application used to locate, search and explore artworks and cultural places in Montreal.
The basic app features are:
 - Navigating through a MapView via OpenStreetMap
 - Sorting and filtering through the list of data in terms of attributes and calculations such as distance from user
 - Capturing pictures of items such as artworks and cultural places
 - Collect merit badges based upon captured items

This code uses:
1) [Android Jetpack Navigation](https://developer.android.com/guide/navigation)
2) [Android Architecture Components](https://developer.android.com/topic/libraries/architecture)
3) [Data Binding](https://developer.android.com/topic/libraries/data-binding)

Définition of done:

0) git checkout dans une branche de type « bugfix/ » ou « feature/ » 
1) implémentation de fonctionnalité
2) Vérifier sur sonarlint si il n’y a pas de code smells, s’il y en a les corrigés.
3) Vérifier si aucun test n’est affecté. (Rouler les tests du fichier test et androidTest)
4) Faire les tests unitaire, instrumental, UI associé avec ce qui est implémenté. (note : les tests unitaires vont dans le fichier test et les tests instrumentaux et UI vont dans le fichier androidTest.)
5) Faire un message de commit descriptif, pour les autres développeurs ou les prochains. 
6) Rebase les commits si certains commit sont triviaux et sont liés à la même tache.
7) Ensuite, fusionner la branche avec le master.

Avant de commencer 

Télécharger le plug-in de Sonarlint et Spotbug dans les plug-ins d’androïde studio.
S’il y a une version créée un APK et le mettre dans le dossier APK, cela permettra de débugger certains comportements plus facilement.
Il est fortement recommandé de télécharger un émulateur et aussi tester sur son appareil physique.
prendre connaissance du schéma de la structure du code.

Procédure de déploiement google play

1. Vérifier si le code se déploie dans un emulateur ou un appareil physique
2. Si c'est pour déployer en production, faire une pull request sur le master
3. S'il y a un développeur actif, le développeur doit approuver la pull request
4. Ensuite, vérifier le numéro de version dans le build.gradle:app la valeur, version code. On peut aussi changer le versionName pour augmenter la version sur google play.
5. Après cliquer en haut sur build
6. cliquer sur Generate signed bundle APK
7. choisir Android App bundle et cliquer sur next
8. Remplir les champs, Module: mona.app, le password et alias
username: key_MONA
motdepasse: mtl2019UdeM
9. Build variants: release
10. attendre que le APK se génère
11. Trouver le APK dans le dossier, normalement Android studio sort un pop up lorsque c'est généré et propose d'ouvrir le dossier contenant le apk.
12. aller sur google console
13. Pour les tests internes aller dans son onglet et créér une version, suivre les étapes à l'écran
14. Pour la production aller dans son onglet et créer une version, toujours bon d'ajouter une description et de mettre à jour le versionName pour la production.
15. la plupartz