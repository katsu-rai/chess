package client;

import com.google.gson.Gson;
import model.GameData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServerFacade {
    private final String baseURL;
    private String authToken;

    public ServerFacade(String baseURL) {
        this.baseURL = baseURL;
    }

    public boolean register(String username, String password, String email) {
        var body = Map.of("username", username, "password", password, "email", email);
        Map resp = request("POST", "/user", new Gson().toJson(body));
        if (resp.containsKey("Error")) {
            return false;
        }
        authToken = (String) resp.get("authToken");
        return true;
    }

    public boolean login(String username, String password) {
        var body = Map.of("username", username, "password", password);
        Map resp = request("POST", "/session", new Gson().toJson(body));
        if (resp.containsKey("Error")) {
            return false;
        }
        authToken = (String) resp.get("authToken");
        return true;
    }

    public boolean logout() {
        Map resp = request("DELETE", "/session", null);
        if (resp.containsKey("Error")) {
            return false;
        }
        authToken = null;
        return true;
    }

    public boolean createGame(String gameName) {
        var body = Map.of("gameName", gameName);
        Map resp = request("POST", "/game", new Gson().toJson(body));
        return !resp.containsKey("Error");
    }

    public List<GameData> listGames() {
        Map resp = request("GET", "/game", null);
        if (resp.containsKey("Error")) {
            return new ArrayList<>();
        }
        return (List<GameData>) resp.get("games");
    }

    public boolean joinGame(int gameId, String playerColor) {
        var body = Map.of("gameID", gameId, "playerColor", playerColor);
        Map resp = request("PUT", "/game", new Gson().toJson(body));
        return !resp.containsKey("Error");
    }

    private Map request(String method, String endpoint, String body) {
        try {
            URI uri = new URI(baseURL + endpoint);
            HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
            http.setRequestMethod(method);

            if (authToken != null) {
                http.addRequestProperty("authorization", authToken);
            }

            if (body != null) {
                http.setDoOutput(true);
                http.addRequestProperty("Content-Type", "application/json");
                try (OutputStream outputStream = http.getOutputStream()) {
                    outputStream.write(body.getBytes());
                }
            }

            http.connect();

            if (http.getResponseCode() == 401) {
                return Map.of("Error", 401);
            }

            try (InputStream respBody = http.getInputStream();
                 InputStreamReader inputStreamReader = new InputStreamReader(respBody)) {
                return new Gson().fromJson(inputStreamReader, Map.class);
            }
        } catch (URISyntaxException | IOException e) {
            return Map.of("Error", e.getMessage());
        }
    }
}
