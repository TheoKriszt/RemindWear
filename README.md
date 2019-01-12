# RemindWear - Todo-List et suivi de sport

## HMIN303 - Développement d'Applications Mobiles pour Wearables (Université de Montpellier)
----------------------------

- Gestion de tâches, rappels automatiques ponctuels ou récurrents
- Notifications Android et notifications étendues WEAR OS
- Plusieurs activités proposées selon les capteurs disponibles sur la montre
- Suivi des tâches sportives (GPS, nb de pas, vitesse, durée) sur la montre [Wear OS](https://wearos.google.com)

## Reconnaissance vocale:
-----------------------------
### Lancer un suivi sportif :
Doit contenir un mot clé spécifique (vélo, marche, course) ou générique (suivi sportif, exercice, tracking)
> Je veux faire du sport
> Lance un suivi sportif
> Je vais faire du vélo

### Créer et préremplir une tâche :
Doit contenir suivre le format et l'ordre suivant :

` <formule de rappel> [dans la catégorie <nom catégorie>]  de <nom de la tâche> <date> à <heure> `

Formules de rappel :
 > rappelle-moi

 > ajoute un rappel

 > ajoute une tâche

 date :
  > demain

  > aujourd'hui

  > le XX <un mois de l'année>

heure :
 > midi

 > minuit

 > XX heures, minutes

 Exemples :
 > rappelle-moi dans la catégorie course d acheter du beurre demain à 16h

 > ajoute un rappel aller chercher ma convocation le 29 janvier à midi

 > ajoute une tâche soutenance de mobile demain à 9h50


-----------------
## TODO :
- [ ] Relire le sujet et vérifier que tous les points ont été traités
- [x] Ajout de tâches à la voix
- [x] Transfert des données vers le mobile
- [x] Affichage des données (suivi sportif), côté téléphone
- [x] Selection du type de sport
- [x] Tâches de sport indépendantes de tâches normales (tracking sportif séparé)


### Tests à faire
- [ ] Tester l'affichage de la montre sur vraie Wear
- [ ] Vérifier le podomètre "classique" sur une vraie montre
- [ ] Vérifier le podomètre "émulé" sur une vraie montre (et ajuster le threshold)

### Bugfix
- [ ] En créant une catégorie, elle ne s'affiche qu'après être retourné par l'accueil
- [x] Le getDuration ne prend pas en compte le warningBefore
- [ ] La reconnaissance vocale manque de finesse
- [ ] Vocal : chercher un sport générique lance de la marche
- [ ] Imposible d'afficher les details du sport
- [x] Wear : changement du bouton impossible : thread secondaire

