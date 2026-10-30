# TP1 Chafii — Accès direct à Gemini

Application Java 25, Jakarta EE 11, JSF et PrimeFaces. Projet : `tp1Chafii`. Packages : `ma.emsi.chafii.tp1`.

## Lancer le projet

1. Ouvrir `pom.xml` dans IntelliJ IDEA et choisir le JDK 25.
2. Recharger Maven et construire le projet avec `mvn clean verify`.
3. Configurer Payara 7 et ajouter le WAR `target/tp1Chafii.war` au déploiement.
4. Définir `GEMINI_KEY` dans l’environnement du processus Payara, puis redémarrer le serveur.
5. Ouvrir l’URL correspondant au contexte `/tp1Chafii/`.

Le modèle utilisé est `gemini-3.5-flash-lite`, comme dans le sujet consulté. Le fichier `.env.example` indique le nom de la variable ; il n’est pas chargé automatiquement par Java.

Avec Payara Micro 7.2026.10, après compilation et configuration de la clé :

```sh
java -jar payara-micro-7.2026.10.jar --deploy target/tp1Chafii.war --contextroot tp1Chafii --port 18080 --nohazelcast
```

Ouvrir `http://localhost:18080/tp1Chafii/`. Le port doit être libre. Le Terminal reste occupé tant que le serveur fonctionne ; Ctrl+C l’arrête.

## Code du TP

- `index.xhtml`, les ressources CSS et JavaScript et le backing bean reprennent les fichiers du TP0.
- `Bb` est complété par l’injection de l’adaptateur, le traitement fourni pour appeler Gemini, les propriétés debug et le rôle bonus.
- `JsonAdapterPourGemini` et `RequeteException` reprennent exactement les fichiers du sujet, hors nom du package.
- `LlmClientPourGemini` reprend le squelette du sujet. Les deux parties à compléter lisent la variable d’environnement et définissent la cible REST. La cible enregistre un filtre qui ajoute la clé dans l’en-tête `x-goog-api-key` ; la méthode d’envoi du squelette reste inchangée.
- `LlmInteraction` est le record à trois membres demandé : `questionJson`, `reponseJson`, `reponseExtraite`.

L’historique envoyé à Gemini est conservé dans `JsonAdapterPourGemini.requeteJson`. Les questions et réponses précédentes sont renvoyées à chaque nouvelle question.

Le mode debug suit le composant fourni dans le sujet : activer le switch puis soumettre une question pour afficher les JSON. Aucun comportement Ajax supplémentaire n’a été ajouté.

## Rôle bonus

« Mécanicien des idées casablancais » explique l’informatique avec des analogies de tramway, de souk ou d’atelier. Les réponses comportent une image concrète, l’explication exacte et un mini-défi. La solution du mini-défi n’est donnée que si elle est demandée.

## Historique Git

La reprise est organisée en quatre commits, créés lors de cette reprise, sans antidater les étapes :

1. État initial : configuration et fichiers du TP0 copiés. Le service du TP0 n’est pas repris ; cette étape contient donc volontairement la référence non résolue décrite dans le sujet du TP1.
2. Appel REST à Gemini, adaptation du backing bean et historique JSON.
3. Interface debug et rôle bonus.
4. Tests et documentation.

Le projet final compile. Les essais d’erreurs volontaires du sujet ne doivent pas être committés. Les restaurer depuis le dernier commit correct avant la remise.

## Vérifications et remise

`mvn clean verify` construit le WAR et lance les quatre tests : historique sur deux tours, caractères spéciaux, erreur HTTP 400 et erreur HTTP 429. Ils utilisent une API locale fictive, sans clé réelle ni appel à Google.

Le projet final a également été déployé sous Payara Micro : page, rôles, validation d’une question vide, affichage debug après soumission et nouveau chat contrôlés.

Les éléments du bonus doivent être dans le **corps de l’email** : conversation réelle de deux requêtes avec le rôle bonus, JSON réel de la deuxième question du sujet expliqué, deux erreurs réelles et explication du retour au code correct avec Git. `COMPTE_RENDU_A_COMPLETER.md` prépare ces éléments, mais n’est pas un compte rendu terminé.

Ne mettre aucune clé réelle dans le code, le dépôt ou l’email. Le répertoire `target/`, les fichiers propres à l’IDE et les secrets locaux sont exclus de Git.

## Sources

- [Sujet du TP1](http://richard.grin.free.fr/emsi/casablanca-ia/tp/tp1-api-direct/index.html)
- [TP0 et fichiers fournis](http://richard.grin.free.fr/emsi/casablanca-ia/tp/tp0-jakartaee/index.html)
- [Consignes générales des TPs](http://richard.grin.free.fr/emsi/casablanca-ia/tp/index.html)
- [Guide d’envoi des projets](http://richard.grin.free.fr/emsi/casablanca-ia/tp/guideenvoiprojet.html)
