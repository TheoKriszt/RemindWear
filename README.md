# RemindWear - Todo-List et suivi de sport

## HMIN303 - Développement d'Applications Mobiles pour Wearables (Université de Montpellier)
----------------------------

- Gestion de tâches, rappels automatiques ponctuels ou récurrents
- Notifications Android et notifications étendues WEAR OS
- Plusieurs activités proposées selon les capteurs disponibles sur la montre
- Suivi des tâches sportives (GPS, nb de pas, vitesse, durée) sur la montre [Wear OS](https://wearos.google.com)

## Reconnaissance vocale:
### Lancer un suivi sportif :
Doit contenir un mot clé spécifique (vélo, marche, course) ou générique (suivi sportif, exercice, tracking)

    [Faire | Commence(r) | Lance(r)] [ le | un | du | de la ] [ exercice | tracking | suivi | sport ] <sportif> <de> [vélo | course | marche ] <à pied>

 __Exemples :__
> Lance un suivi sportif

> Faire du vélo

> Commencer un exercice de marche à pied

> etc.

### Créer et préremplir une tâche :
Doit commencer par une intention de rappel comme suit:

    rappelle-moi | [mets-moi | ajoute-moi | ajoute | mets] <une> [rappel | tâche]

> rappelle-moi

> mets-moi un rappel

> ajoute une tâche

Les intentions liées à la date, l'heure la catégorie et l'objet du rappel peuvent suivre :

 __date :__
  > demain

  > après demain

  > le 25

  > le 6 janvier

__heure :__
 > à midi

 > minuit

 > à 16h25

 __Catégorie :__

 > dans la catégorie nom_catégorie

 **Exemples complets:**
 > rappelle-moi dans la catégorie ``courses`` d'``acheter du beurre`` ``demain`` à ``16h``

 > mets-moi un rappel ``aller chercher ma convocation`` le ``29 janvier`` à ``midi``

 > ajoute une tâche ``soutenance de mobile`` ``demain`` à ``11h555``


-----------------
## TODO :
[x] Ajout de tâches à la voix

[x] Transfert des données vers le mobile

[x] Affichage des données (suivi sportif), côté téléphone

[x] Selection du type de sport

[x] Tâches de sport indépendantes de tâches normales (tracking sportif séparé)


### Tests à faire
- [x] Tester l'affichage de la montre sur vraie Wear
- [x] Vérifier le podomètre "classique" sur une vraie montre
- [x] Vérifier le podomètre "émulé" sur une vraie montre (et ajuster le threshold)

### Bugfix
- [x] En créant une catégorie, elle ne s'affiche qu'après être retournée par l'accueil
- [x] Le getDuration ne prend pas en compte le warningBefore
- [x] La reconnaissance vocale manque de finesse
- [x] Vocal : chercher un sport générique lance de la marche
- [x] Vocal : lancer un sport indispo sur la montre le lance quand même
- [x] Imposible d'afficher les details du sport
- [x] Wear : changement du bouton impossible : thread secondaire



