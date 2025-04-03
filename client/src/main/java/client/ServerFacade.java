package client;

import chess.*;
import com.google.gson.Gson;
import model.GameData;
import websocket.commands.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerFacade {
    private final String baseURL;
    private String authToken;
    private int lastResponseCode;
    private WebSocketCommunicator ws;

    public ServerFacade(String baseURL) {
        this.baseURL = baseURL;
    }

    public int getLastResponseCode() {
        return lastResponseCode;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    public boolean register(String username, String password, String email) {
        var body = Map.of("username", username, "password", password, "email", email);
        Map<String, Object> resp = request("POST", "/user", new Gson().toJson(body));

        if (resp.containsKey("Error")) {
            return false;
        }
        authToken = (String) resp.get("authToken");
        return true;
    }

    public boolean login(String username, String password) {
        var body = Map.of("username", username, "password", password);
        Map<String, Object> resp = request("POST", "/session", new Gson().toJson(body));

        if (resp.containsKey("Error")) {
            return false;
        }
        authToken = (String) resp.get("authToken");
        return true;
    }

    public boolean logout() {
        Map<String, Object> resp = request("DELETE", "/session", null);

        if (resp.containsKey("Error")) {
            return false;
        }
        authToken = null;
        return true;
    }

    public int createGame(String gameName) {
        var body = Map.of("gameName", gameName);
        Map<String, Object> resp = request("POST", "/game", new Gson().toJson(body));

        if (!resp.containsKey("gameID")) {
            System.err.println("Failed to create game. Server response: " + resp);
            return -1;
        }
        return ((Number) resp.get("gameID")).intValue();
    }

    public Map<Integer, GameData> listGamesMap() {
        Map<String, Object> resp = request("GET", "/game", null);
        if (resp.containsKey("Error")) {
            return Map.of();
        }

        List<Map<String, Object>> gameList = (List<Map<String, Object>>) resp.get("games");
        Map<Integer, GameData> gameMap = new HashMap<>();

        for (Map<String, Object> gameDataMap : gameList) {
            int gameID = ((Number) gameDataMap.get("gameID")).intValue();
            String gameName = (String) gameDataMap.get("gameName");
            String whiteUser = (String) gameDataMap.getOrDefault("whiteUsername", null);
            String blackUser = (String) gameDataMap.getOrDefault("blackUsername", null);

            String gameJson = new Gson().toJson(gameDataMap.get("game"));
            ChessGame game = new Gson().fromJson(gameJson, ChessGame.class);

            gameMap.put(gameID, new GameData(gameID, whiteUser, blackUser, gameName, game));
        }
        return gameMap;
    }

    public boolean joinGame(int gameId, String playerColor) {
        var body = Map.of("gameID", gameId, "playerColor", playerColor);
        Map<String, Object> resp = request("PUT", "/game", new Gson().toJson(body));
        return !resp.containsKey("Error");
    }

    private Map<String, Object> request(String method, String endpoint, String body) {
        try {
            URL url = new URI(baseURL + endpoint).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);

            if (authToken != null) {
                http.setRequestProperty("Authorization", authToken);
            }

            if (body != null) {
                http.setDoOutput(true);
                http.setRequestProperty("Content-Type", "application/json");
                try (OutputStream outputStream = http.getOutputStream()) {
                    outputStream.write(body.getBytes());
                }
            }

            http.connect();
            lastResponseCode = http.getResponseCode();

            if (lastResponseCode >= 400) {
                return Map.of("Error", lastResponseCode);
            }

            try (InputStream respBody = http.getInputStream();
                 InputStreamReader reader = new InputStreamReader(respBody)) {
                return new Gson().fromJson(reader, Map.class);
            }
        } catch (URISyntaxException | IOException e) {
            lastResponseCode = 500;
            return Map.of("Error", e.getMessage());
        }
    }

    public void connectWS() {
        try {
            ws = new WebSocketCommunicator(baseURL);
        } catch (Exception e) {
            System.err.println("Failed to connect to WebSocket: " + e.getMessage());
        }
    }

    public void sendCommand(UserGameCommand command) {
        if (ws == null) {
            System.err.println("WebSocket is not connected. Call connectWS() first.");
            return;
        }
        ws.sendMessage(new Gson().toJson(command));
    }

    public void connect(int gameID) {
        sendCommand(new Connect(authToken, gameID));
    }

    public void makeMove(int gameID, ChessMove move) {
        sendCommand(new MakeMove(authToken, gameID, move));
    }

    public void leave(int gameID) {
        sendCommand(new Leave(authToken, gameID));
    }

    public void resign(int gameID) {
        sendCommand(new Resign(authToken, gameID));
    }
}