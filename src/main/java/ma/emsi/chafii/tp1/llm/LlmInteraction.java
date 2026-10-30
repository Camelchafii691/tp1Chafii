package ma.emsi.chafii.tp1.llm;

/** Les deux documents JSON échangés et la réponse affichable. */
public record LlmInteraction(String questionJson, String reponseJson, String reponseExtraite) {}
