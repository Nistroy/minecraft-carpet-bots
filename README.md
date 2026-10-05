# Bots Carpet

Mod Fabric 1.21.1 pour le serveur de nistroy : comportements en plus pour les bots de Carpet `1.4.147` (`/player`).

## `/omenbot <bot> on|off`

`/omenbot` seul affiche l'aide et la liste des bots activés.

Le bot boit une fiole sinistre (ominous bottle) dès que :

- aucun raid n'est actif à moins de 96 blocs de lui ;
- il n'a ni Mauvais présage ni Présage de raid ;
- il a une fiole dans son inventaire et la main gauche libre (ou déjà occupée par des fioles).

Il la boit de la main gauche, comme un joueur, et garde son épée en main droite : `/player <bot> attack interval 13`
continue pendant ce temps. Le bot ramasse les fioles lâchées par les capitaines qu'il tue, donc une ferme à raid peut
tourner seule.

Réservé aux ops (comme `/player`), refusé sur un vrai joueur. Activation oubliée quand le bot se déconnecte (mort,
`/kill`, redémarrage) : la refaire après `/player <bot> spawn`.

Côté serveur seulement.

## Tests

```
./gradlew build runGameTest
```
