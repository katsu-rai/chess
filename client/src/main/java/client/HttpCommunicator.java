package client;

import com.google.gson.Gson;
import model.GameData;
import model.GamesList;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Map;

public class HttpCommunicator {

    private final String baseURL;
    private final ServerFacade facade;

    public HttpCommunicator(ServerFacade facade, String serverDomain) {
        this.baseURL = "http://" + serverDomain;
        this.facade = facade;
    }

    public boolean register(String username, String password, String email) {
        return authenticateUser("POST", "/user", Map.of("username", username, "password", password, "email", email));
    }

    public boolean login(String username, String password) {
        return authenticateUser("POST", "/session", Map.of("username", username, "password", password));
    }

    public boolean logout() {
        return request("DELETE", "/session") == null;
    }

    public int createGame(String gameName) {
        Map<String, Object> response = request("POST", "/game", Map.of("gameName", gameName));
        return response != null ? ((Double) response.get("gameID")).intValue() : -1;
    }

    public HashSet<GameData> listGames() {
        String response = requestString("GET", "/game");
        return response.contains("Error") ? new HashSet<>() : new Gson().fromJson(response, GamesList.class).games();
    }

    public boolean joinGame(int gameId, String playerColor) {
        Map<String, Object> body = playerColor != null ? Map.of("gameID", gameId, "playerColor", playerColor) : Map.of("gameID", gameId);
        return request("PUT", "/game", body) != null;
    }

    private boolean authenticateUser(String method, String endpoint, Map<String, Object> body) {
        Map<String, Object> response = request(method, endpoint, body);
        if (response != null) {
            facade.setAuthToken((String) response.get("authToken"));
            return true;
        }
        return false;
    }

    private Map<String, Object> request(String method, String endpoint) {
        return request(method, endpoint, null);
    }

    private Map<String, Object> request(String method, String endpoint, Map<String, Object> body) {
        try {
            HttpURLConnection http = makeConnection(method, endpoint, body);
            if (http.getResponseCode() == 401) return null;
            try (InputStream respBody = http.getInputStream()) {
                return new Gson().fromJson(new InputStreamReader(respBody), Map.class);
            }
        } catch (IOException | URISyntaxException e) {
            return null;
        }
    }

    private HttpURLConnection makeConnection(String method, String endpoint, Map<String, Object> body) throws URISyntaxException, IOException {
        URI uri = new URI(baseURL + endpoint);
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod(method);
        if (facade.getAuthToken() != null) {
            http.addRequestProperty("authorization", facade.getAuthToken());
        }
        if (body != null) {
            http.setDoOutput(true);
            http.addRequestProperty("Content-Type", "application/json");
            try (var outputStream = http.getOutputStream()) {
                outputStream.write(new Gson().toJson(body).getBytes());
            }
        }
        http.connect();
        return http;
    }

    private String requestString(String method, String endpoint) {
        return requestString(method, endpoint, null);
    }

    private String requestString(String method, String endpoint, Map<String, Object> body) {
        try {
            HttpURLConnection http = makeConnection(method, endpoint, body);
            if (http.getResponseCode() == 401) return "Error: 401";
            try (InputStream respBody = http.getInputStream()) {
                return new String(respBody.readAllBytes());
            }
        } catch (IOException | URISyntaxException e) {
            return "Error: " + e.getMessage();
        }
    }
}
