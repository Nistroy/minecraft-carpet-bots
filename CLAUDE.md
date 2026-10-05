# minecraft-carpet-bots — instructions agents

Mod Fabric 1.21.1, serveur seul. Serveur `Nistroy/minecraft-server` (`MODS.md`). Public, GPL-3.0. Docs `.md` = notes
denses pour agents, sauf `README.md`. Dépend de Carpet `1.4.147` + `Nistroy/minecraft-carpet-piston-fix` côté serveur
(crash pistons avec Supplementaries), pas déclaré en dépendance.

## But (nistroy 2026-10-05)
Ferme à raid avec bot Carpet : bot boit une fiole sinistre seulement hors raid (boire pendant un raid = niveau du raid
monte). Mod séparé du correctif piston, activation par commande par bot (choix nistroy 2026-10-05).

## Carte
- `OmenDrinker.tryDrink` — conditions : vivant, n'utilise rien, ni `BAD_OMEN` ni `RAID_OMEN`,
  `ServerLevel.getRaidAt` null (raid actif ≤ 96 blocs du centre, vanilla), fiole en main gauche ou main gauche vide
  (pile déplacée depuis `inventory.items`). Boit via `gameMode.useItem` → fin vanilla (32 ticks).
- `OmenBots` — `Set<UUID>` en mémoire, tick toutes les 20 ticks (`CarpetBots`), bot hors ligne → oublié.
- `OmenBotCommand` — `/omenbot` = aide + bots activés en ligne ; `/omenbot <bot> on|off`, niveau 2, refus si pas
  `carpet.patches.EntityPlayerMPFake`.

## Faits vérifiés (test-server 2026-10-05)
- Carpet `use continuous` enchaîne toute la pile de fioles ; `use once` annule la gorgée → raison de ce mod.
- Bot avec Mauvais présage dans un village généré → Présage de raid → raid. Lit posé par `setblock` dans le ciel ≠ village.
- Hardcore Revival : bot KO 120 s puis mort → Carpet le déconnecte → activation perdue.

## Tests — TDD obligatoire
- `JAVA_HOME=/opt/homebrew/opt/openjdk@21 ./gradlew build runGameTest` (CI idem). Rouge d'abord.
- Résultat dans `build/run/gameTest/logs/latest.log` (`All N required tests passed`) ; sortie console filtrée par rtk.
- Joueur fictif `makeMockServerPlayerInLevel` (créatif, pas tické par le serveur → `doTick()` à la main pour finir de boire).
- Raid de test : `createOrExtendRaid` + `stop()` en `finally`, batch `raid` à part (rayon 96 blocs).
- Commande testée via `performPrefixedCommand` + `CommandSource` qui capture les messages (`OmenBotCommandGameTest`).
- Commande sur vrai bot Carpet : pas testable en gametest (profil Mojang) → test manuel `test-server/`.

## Release
Tag `vX.Y.Z` = `version` de `gradle.properties` → workflow `release` → jar sur la release GitHub.
