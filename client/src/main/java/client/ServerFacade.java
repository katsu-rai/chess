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
    private int lastResponseCode; // Stores the last HTTP response code

    public ServerFacade(String baseURL) {
        this.baseURL = baseURL;
    }

    public int getLastResponseCode() {
        return lastResponseCode;
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

    public int createGame(String gameName) {
        var body = Map.of("gameName", gameName);
        Map resp = request("POST", "/game", new Gson().toJson(body));

        if (!resp.containsKey("gameID")) {
            System.err.println("Failed to create game. Server response: " + resp);
            return -1; // Indicating failure
        }

        return ((Number) resp.get("gameID")).intValue();
    }

    public List<GameData> listGames() {
        Map resp = request("GET", "/game", null);
        if (resp.containsKey("Error")) {
            return new ArrayList<>();
        }

        List<?> rawGames = (List<?>) resp.get("games");
        List<GameData> games = new ArrayList<>();

        Gson gson = new Gson();
        for (Object obj : rawGames) {
            String json = gson.toJson(obj);
            GameData game = gson.fromJson(json, GameData.class);
            games.add(game);
        }

        return games;
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
            lastResponseCode = http.getResponseCode(); // Store the last response code

            if (lastResponseCode >= 400) { // Error codes
                return Map.of("Error", lastResponseCode);
            }

            try (InputStream respBody = http.getInputStream();
                 InputStreamReader inputStreamReader = new InputStreamReader(respBody)) {
                return new Gson().fromJson(inputStreamReader, Map.class);
            }
        } catch (URISyntaxException | IOException e) {
            lastResponseCode = 500; // Internal error
            return Map.of("Error", e.getMessage());
        }
    }
}
