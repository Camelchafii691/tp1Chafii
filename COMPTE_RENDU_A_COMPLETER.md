# Éléments à placer dans le texte de l’email du TP1

Ce document est une préparation. Les zones entre crochets doivent être remplacées par les résultats réels obtenus avec votre clé et votre application. Le sujet demande que ces éléments soient dans le **corps de l’email**, pas uniquement en pièce jointe. Aucun email n’a été envoyé.

## 1. Présentation du rôle bonus et conversation

J’ai ajouté le rôle « Mécanicien des idées casablancais ». Il explique les concepts informatiques à l’aide d’images concrètes inspirées du tramway, du souk ou d’un atelier. Chaque réponse comporte trois rubriques : Image concrète, Explication exacte et Mini-défi. Le rôle distingue les limites de l’analogie de la réalité technique et ne donne la solution du défi que si l’utilisateur la demande.

Dans un nouveau chat avec ce rôle, envoyer exactement ces deux questions, par exemple :

1. « Explique une API REST avec une analogie du tramway de Casablanca. »
2. « Pourquoi dois-tu recevoir l’historique à chaque nouvelle question ? »

[Coller ici la conversation réelle avec exactement deux requêtes utilisateur, obtenue avec le bouton Copier conversation. Ne pas inventer les réponses.]

## 2. JSON de la deuxième requête du sujet

Faire un **nouveau chat**, rôle Assistant. Poser successivement les deux questions visibles sur la capture du sujet :

1. « Quelle est la capitale de la France ? »
2. « Et celle des USA ? »

Activer le debug, puis cliquer sur Copier requête JSON après la deuxième réponse.

[Coller ici le JSON réel de la deuxième requête. Il contient notamment la réponse réellement reçue à la première question.]

Explication :

- `system_instruction` contient les consignes du rôle choisi. Son tableau `parts` contient un objet dont `text` est le texte des consignes.
- `contents` contient les messages successifs de la conversation. Au deuxième envoi, il contient trois messages : la première question, la première réponse et la deuxième question.
- Chaque message porte un `role` : `user` pour l’utilisateur et `model` pour Gemini.
- Le tableau `parts` de chaque message contient ses parties ; pour une question textuelle, `text` contient le texte saisi.
- Le premier message utilisateur est répété car l’appel REST `generateContent` ne conserve pas automatiquement notre conversation. Il faut retransmettre le contexte pour que Gemini comprenne « Et celle des USA ? ».
- Dans mon projet, la classe `JsonAdapterPourGemini` conserve cet historique dans son champ `requeteJson`. La méthode `extractReponse` ajoute la réponse de Gemini et `ajouteQuestionDansJsonRequete` ajoute la question suivante. `Bb` conserve séparément le texte destiné à l’affichage.
- Au moment où le deuxième appel est envoyé, la réponse à la deuxième question n’existe pas encore : elle ne fait donc pas partie de cette requête.

## 3. Les deux erreurs demandées

Avant les modifications, enregistrer la version correcte dans un commit Git. Modifier **un cas à la fois**, reconstruire et redéployer. Faire un nouveau chat avant chaque essai.

### Clé invalide

Dans le constructeur de `LlmClientPourGemini`, remplacer temporairement `this.key = System.getenv("GEMINI_KEY");` par `this.key = "cle-volontairement-invalide";`. La clé réelle reste dans la variable d’environnement.

[Coller le message exact affiché par l’application, puis indiquer : « Ce message a été provoqué par une clé API volontairement invalide. »]

Restaurer le fichier du client avant le deuxième essai.

### Rôle system invalide dans contents

Dans `JsonAdapterPourGemini.creerRequeteJson`, remplacer temporairement `.add("role", "user")` par `.add("role", "system")`. Ne pas modifier le champ `system_instruction`, qui est l’emplacement prévu pour les consignes système.

[Coller le message exact affiché et indiquer : « Ce message a été provoqué par le rôle system utilisé à la place de user dans un message de contents. »]

Les codes et textes exacts dépendent de l’API et de la configuration du compte : ils ne sont pas préremplis. Les erreurs HTTP simulées dans les tests ne remplacent pas ces deux observations.

## 4. Retour au code correct

Depuis le dossier du projet, après les essais et sans les committer :

```sh
git diff
git restore --source=HEAD -- src/main/java/ma/emsi/chafii/tp1/llm/LlmClientPourGemini.java src/main/java/ma/emsi/chafii/tp1/llm/JsonAdapterPourGemini.java
git diff
mvn clean verify
```

Adapter les chemins si les packages ont été renommés. La commande `git restore --source=HEAD` restaure les deux fichiers depuis le dernier commit correct, tout en conservant l’historique Git. Elle supprime les modifications non committées de ces deux fichiers : ne l’utiliser que pour les essais que l’on souhaite annuler. Redéployer le WAR reconstruit.

[Après avoir réellement effectué la manipulation, décrire les étapes réalisées et le résultat : différence vide, tests réussis et application de nouveau fonctionnelle.]

Enfin, pousser le code correct sur GitHub et inclure le lien du dépôt dans le corps de l’email.
