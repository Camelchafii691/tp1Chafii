package ma.emsi.chafii.tp1.llm;

import com.sun.net.httpserver.HttpServer;
import jakarta.json.*;
import org.junit.jupiter.api.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/** Vérifie le vrai client REST contre une API locale ; aucun appel Google. */
class ConversationTest {
    private HttpServer server;
    private LlmClientPourGemini client;
    private JsonAdapterPourGemini adapter;
    private final Queue<Retour> retours = new ArrayDeque<>();
    private final List<JsonObject> requetes = new ArrayList<>();
    private String cleRecue;
    private String typeRecu;
    record Retour(int statut, String json) {}

    @BeforeEach void initialiser() throws Exception {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/generateContent", exchange -> {
            cleRecue = exchange.getRequestHeaders().getFirst("x-goog-api-key");
            typeRecu = exchange.getRequestHeaders().getFirst("Content-Type");
            try (JsonReader reader = Json.createReader(exchange.getRequestBody())) {
                requetes.add(reader.readObject());
            }
            Retour retour = retours.remove();
            byte[] corps = retour.json().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
            exchange.sendResponseHeaders(retour.statut(), corps.length);
            exchange.getResponseBody().write(corps);
            exchange.close();
        });
        server.start();
        client = new LlmClientPourGemini();
        var champClient = LlmClientPourGemini.class.getDeclaredField("clientRest");
        champClient.setAccessible(true);
        var rest = (jakarta.ws.rs.client.Client) champClient.get(client);
        var champTarget = LlmClientPourGemini.class.getDeclaredField("target");
        champTarget.setAccessible(true);
        champTarget.set(client, rest.target("http://127.0.0.1:" + server.getAddress().getPort()
                + "/generateContent").register((jakarta.ws.rs.client.ClientRequestFilter)
                        request -> request.getHeaders().putSingle("x-goog-api-key", "cle-fictive-test")));
        adapter = new JsonAdapterPourGemini();
        var champ = JsonAdapterPourGemini.class.getDeclaredField("geminiClient");
        champ.setAccessible(true);
        champ.set(adapter, client);
        adapter.setSystemRole("Assistant de test");
    }
    @AfterEach void fermer() { client.closeClient(); server.stop(0); }

    private void repondre(String texte) {
        retours.add(new Retour(200, Json.createObjectBuilder().add("candidates", Json.createArrayBuilder()
                .add(Json.createObjectBuilder().add("content", Json.createObjectBuilder()
                        .add("role", "model").add("parts", Json.createArrayBuilder()
                                .add(Json.createObjectBuilder().add("text", texte)))))).build().toString()));
    }
    private JsonObject json(String texte) {
        try (JsonReader reader = Json.createReader(new StringReader(texte))) { return reader.readObject(); }
    }
    @Test void historiqueDeuxToursEtAuthentification() throws Exception {
        repondre("La capitale de la France est Paris.");
        var premiere = adapter.envoyerRequete("Quelle est la capitale de la France ?");
        repondre("La capitale des USA est Washington, D.C.");
        var deuxieme = adapter.envoyerRequete("Et celle des USA ?");
        JsonArray contents = requetes.get(1).getJsonArray("contents");
        assertEquals(3, contents.size());
        assertEquals("user", contents.getJsonObject(0).getString("role"));
        assertEquals("model", contents.getJsonObject(1).getString("role"));
        assertEquals(premiere.reponseExtraite(), contents.getJsonObject(1).getJsonArray("parts").getJsonObject(0).getString("text"));
        assertEquals("Et celle des USA ?", contents.getJsonObject(2).getJsonArray("parts").getJsonObject(0).getString("text"));
        assertEquals("Assistant de test", requetes.get(1).getJsonObject("system_instruction").getJsonArray("parts").getJsonObject(0).getString("text"));
        assertEquals(requetes.get(1), json(deuxieme.questionJson()));
        assertEquals("cle-fictive-test", cleRecue);
        assertTrue(typeRecu.startsWith("application/json"));
    }
    @Test void caracteresSpeciauxSontPreserves() throws Exception {
        repondre("Réponse\n\"test\" \\ é");
        String question = "Une question \"citée\"\navec \\ et é";
        var interaction = adapter.envoyerRequete(question);
        assertEquals(question, requetes.getFirst().getJsonArray("contents").getJsonObject(0).getJsonArray("parts").getJsonObject(0).getString("text"));
        assertEquals("Réponse\n\"test\" \\ é", interaction.reponseExtraite());
    }
    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.ValueSource(ints = {400, 429})
    void erreurApiPropageStatutEtRequete(int statut) throws Exception {
        String erreur = "{\"error\":{\"code\":" + statut + ",\"message\":\"Erreur simulée de test\"}}";
        retours.add(new Retour(statut, erreur));
        var exception = assertThrows(RequeteException.class, () -> adapter.envoyerRequete("Question de test"));
        assertTrue(exception.getMessage().contains(Integer.toString(statut)));
        assertTrue(exception.getMessage().contains(erreur));
        var contents = json(exception.getRequeteJson()).getJsonArray("contents");
        assertEquals("Question de test", contents.getJsonObject(0).getJsonArray("parts").getJsonObject(0).getString("text"));
    }
}
