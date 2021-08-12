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
