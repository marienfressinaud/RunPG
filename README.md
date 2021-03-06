# RunPG

## Description

RunPG est un jeu type RPG utilisant la géolocalisation pour faire évoluer notre personnage.
Les quêtes consistent à parcourir un certain nombre de kilomètres en un minimum de temps. L'enregistrement se fait grâce à la version web de RunPG et utilise les possibilités de géolocalisation du téléphone et du HTML5.

## Contraintes

Il s'agit d'un projet web pour l'Ensimag, un certain nombre de contraintes sont imposées :

* Utilisation du framework Java Play (obligatoire) : OK
* Gestion de droits avec différents niveaux d'utilisateurs (obligatoire) : OK (visiteur, joueur, admin)
* Gestion d'un flux RSS (optionnel) : OK (classement et joueur)
* Utilisation d'un webservice (souhaitable) : OK (maps et données de OpenStreetMaps)
* Une partie du site en GWT (souhaitable) : ~NOK (choix assumé, remplacement par affichage JQuery + Ajax du profil utilisateur dans l'administration (?))
* Version mobile de l'application (optionnel) : OK (tout le site est full responsive design)

## État du projet : 100% terminé

* Page d'accueil / classement joueurs : OK
* Inscription utilisateurs : OK
* Connexion / déconnexion : OK
* Affichage profil : OK
* Affichage des quêtes : OK
* Enchaînement des quêtes : OK
* Responsive design : OK
* Page enregistrement parcours : OK
* Enregistrement parcours (client -> serveur) : OK % pas possible d'envoyer trop de points GPS
* Gain XP + évolution score : OK
* Gestion droits utilisateurs : OK
* Page administrateur : OK
* Affichage Ajax + JQuery du profil joueur dans l'admin : OK
* Flux RSS évolution joueurs : OK
* FlUX RSS classement joueurs : OK
* Tests : OK
* Création de quêtes : OK

## Fonctionnement du jeu

Le but du jeu est de terminer toutes les quêtes consistant à aller courir un certain nombre de kilomètres durant une certaine durée. L'enregistrement d'une quête se fait via l'application mobile utilisant les possibilités de géolocalisation d'un téléphone couplées au standard HTML5 mettant à disposition l'API adéquate.

Si la quête en cours est réalisée (les objectifs minimums sont atteints), la quête suivante est débloquée et le joueur gagne des points d'expérience (proportionnellement aux nombres réels de kilomètres parcourus et du temps passé à courir) et augmente son score (proportionnellement aux objectifs). Une fois une quête terminée, il n'est plus possible de la refaire.

Il existe deux types d'expérience : l'expérience en vitesse et celle en endurance. Plus on gagne de points d'expérience, plus on réduit les objectifs des quêtes. L'expérience en vitesse fait décroître les objectifs de distance (on court plus vite, on a l'impression de parcourir moins de kilomètres). L'expérience en endurance fait décroître les objectifs de durée (on tient mieux la distance, on ne voit plus le temps passer)

En plus des quêtes, un joueur peut aussi suivre un entraînement. Un entraînement permet de gagner deux fois plus d'expérience qu'une quête, mais ne permet pas d'augmenter son score.

## Rouages internes du jeu

Une quête en fonction d'un joueur possède 4 états différents :

* NOUVELLE : le joueur n'a pas encore pris connaissance des objectifs
* EN_COURS : le joueur a accepté la mission, il doit la réaliser pour passer à la suite
* TERMINEE : le joueur a validé les objectifs mais la mission suivante n'est pas débloquée
* VALIDEE : le joueur a pris connaissance que la mission est terminée. La mission suivante est débloquée

Règles de calcul des points d'expérience (entraînements) :

* XP vitesse = max(distance (km) * vitesse moyenne (km/h), 5)
* XP endurance = max(durée (h) * 10 * vitesse moyenne (km/h), 5)

Règles de calcul des points d'expérience (quêtes) :

* Suivent les mêmes règles qu'en entraînement, divisées par 2

Règles de décroissement des objectifs de quêtes :

* distance (km) = distance normale - (XP vitesse / 100)
* durée (mn) = durée normale - (XP endurance / 200)

Règle de gain de points (score) :

* score = (objectif distance * 30 + objectif durée) / 10

## Améliorations possibles

1. Après des tests, il n'est pas possible d'envoyer un parcours faisant environ 30 mn et plus (correspondant à environ 30 * 60 = 1800 points GPS). La supposition est que HTTP ne permet pas d'envoyer une en-tête assez grande pour tous ces points GPS.
On pourrait corriger ce problème en envoyant régulièrement (tous les 200 points GPS par exemple) les données au serveur. Néanmoins, Internet n'étant pas forcément disponible tout le long du parcours, on ne peut pas se contenter de ça. On peut ainsi mettre les points GPS sur le stockage du téléphone à l'aide de la technologie Local Storage (HTML5) et les synchroniser quand Internet est accessible.

2. Les différents états d'une quête doivent permettre plus de souplesse entre le passage d'une quête à une autre. Les possibilités ne sont pas réellement utilisées ici. On pourrait par exemple, au moment de passer une quête en EN_COURS, verrouiller les objectifs sans que l'expérience ne puisse plus influencer dessus. Avant de valider une quête, on pourrait aussi la refaire autant de fois que l'on veut mais en ne prenant en compte dans l'expérience que la meilleure course.

3. Au début et à la fin d'une quête, j'avais prévu que le message expliquant l'enchaînement des quêtes se fassent dans un écran "spécial" avec séquence de textes sur background illustrant l'histoire. Ce système s'appuyait d'ailleurs sur les différents états des quêtes pour lancer automatiquement la séquence.
Par exemple, la séquence de début d'une quête aurait dû ressembler à ceci en base de données (les triples tirets indique un changement de message dans la séquence) :
```
background: url('img/seq1_1.jpg')
Il était une fois dans un royaume très très lointain, un roi très très méchant.
---
background: url('img/seq1_2.jpg')
Ce roi avait aussi une fille très très belle.
---
background: url('img/seq1_3.jpg')
Tous les hommes du royaume n'avaient d'yeux que pour elle, ce qui embêtait bien le roi.
(etc.)
```

4. Les joueurs devraient être capable de pouvoir rentrer en contact entre eux (système de messages privés, amis, etc.) afin de rendre le site plus "sociale". Mais je n'ai pas jugé que c'était une fonctionnalité suffisamment importante pour que je me penche dessus.

5. La gestion du profil n'a pas fait l'objet d'une grande attention. Il devrait être possible au moins de modifier son avatar et son mot de passe.

6. L'inventaire n'a pas été implémenté

## Crédits

* Play Framework : http://www.playframework.com/
* Icônes : https://git.gnome.org/browse/gnome-icon-theme-symbolic
* OpenLayers + OpenStreetMap : http://www.openlayers.org/ http://www.openstreetmap.org/
* JQuery : http://jquery.com/
* PrettyPhoto : http://www.no-margin-for-errors.com/projects/prettyphoto-jquery-lightbox-clone
* Simulateur FirefoxOS (pour les tests) : https://addons.mozilla.org/en-US/firefox/addon/firefox-os-simulator/
* Des morceaux de codes Javascipt et Java :
	* Calcul distance entre 2 points GPS (JS) : http://www.clubic.com/forum/programmation/calcul-de-distance-entre-deux-coordonnees-gps-id178494-page1.html
	* Calcul distance entre 2 points GPS (Java) : http://stackoverflow.com/questions/837872/calculate-distance-in-meters-when-you-know-longitude-and-latitude-in-java
	* Différence entre deux dates en JS : http://www.finalclap.com/faq/88-javascript-difference-date
* Logo : http://www.iconshock.com/
* Avatars : http://www.iconshock.com/ et https://opendesktop.org/usermanager/search.php?username=mentalrey
