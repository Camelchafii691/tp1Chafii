# Vérification du TP1 Chafii avant remise

Vérification du 30 octobre 2026, à partir du sujet TP1, des sources fournies et du guide de remise de l’enseignant.

## Sources et structure

- Projet et packages identifiés par Chafii.
- `JsonAdapterPourGemini` et `RequeteException` comparés au texte des sources officielles : seule la déclaration du package diffère.
- Page, backing bean, CSS et JavaScript repris des fichiers fournis ; ajouts du TP1 et du rôle bonus.
- Client REST complété, sans SDK Gemini. Authentification par en-tête HTTP dans la construction de la cible ; clé lue dans `GEMINI_KEY`.
- Retrait des améliorations non demandées : annulation automatique de l’historique en cas d’erreur, extraction de plusieurs parties, traitement spécial des blocages et Ajax pour le switch debug.

## Construction et tests

- Java 25, Jakarta EE 11, PrimeFaces 16.0.1.
- Construction propre : `mvn clean verify` réussie.
- Quatre tests réussis : deux tours de conversation et authentification, caractères spéciaux, propagation d’une erreur HTTP 400, propagation d’une erreur HTTP 429.
- Serveur HTTP local utilisé pour ces tests ; aucune clé réelle ni réponse Google simulée présentée comme une preuve réelle.
- Descripteurs XML et page XHTML valides.

## Déploiement

Déploiement réussi sur Payara Micro 7.2026.10, dans une instance de contrôle distincte, sur le port 18082.

- Page HTTP 200 et quatre rôles visibles.
- Question vide : message de validation affiché.
- Switch debug puis soumission : deux zones JSON présentes.
- Nouveau chat : nouvelle vue et debug réinitialisé.

Aucun appel à Google n’a été effectué pendant ce contrôle de la nouvelle version. Les essais Google antérieurs de la première version ne sont pas présentés comme de nouveaux essais sur celle-ci.

## Remise et bonus

Le dépôt est public. L’historique est organisé en quatre étapes à la demande de l’étudiant. Le professeur demande les commits aux étapes importantes ; l’attribution du bonus reste de son ressort.

Les quatre preuves demandées pour l’email ne sont pas encore toutes collectées : conversation bonus réelle, JSON de la deuxième requête, deux erreurs API réelles et manipulation Git réellement effectuée après ces essais. Le fichier de préparation contient encore des emplacements à compléter.
