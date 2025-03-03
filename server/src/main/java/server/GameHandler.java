package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.GameData;
import service.GameService;
import spark.*;

import javax.swing.text.html.parser.Parser;
import java.text.ParseException;
import java.util.HashSet;
import com.google.gson.JsonSyntaxException;

public class GameHandler {
    private final GameService gameService;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public Object getAllGames(Request req, Response res) throws Exception {
        String authToken = req.headers("authorization");
        try {
            HashSet<GameData> games = gameService.getAllGames(authToken);

            // Check if games is null and handle accordingly
            if (games == null) {
                games = new HashSet<>();  // Initialize an empty set if games is null
            }

            res.status(200);
            JsonObject responseJson = new JsonObject();
            responseJson.add("games", new Gson().toJsonTree(games));
            return responseJson.toString();

        } catch (Exception e) {
            res.status(401);
            return "{ \"message\": \"Error: unauthorized\" }";
        }
    }

    public Object createGame(Request req, Response res) throws Exception {
        String authToken = req.headers("authorization");
        GameData gameData = new Gson().fromJson(req.body(), GameData.class);

        if (authToken == null) {
            res.status(400);
            return "{ \"message\": \"Error: bad request\" }";
        }

        try {
            int gameId = gameService.createGame(authToken, gameData);
            res.status(200);
            return "{ \"gameID\": " + gameId + " }";
        } catch (Exception e) {
            res.status(401);
            return "{ \"message\": \"Error: unauthorized\" }";
        }
    }


    public String joinGame(Request req, Response res) {
        try {
            // Check if request body is empty or missing required fields
            if (req.body() == null || !req.body().contains("\"gameID\":") || !req.body().contains("\"playerColor\":")) {
                res.status(400);
                return "{ \"message\": \"Error: bad request\" }";
            }

            String authToken = req.headers("authorization");
            if (authToken == null || authToken.isEmpty()) {
                res.status(401);
                return "{ \"message\": \"Error: Missing or invalid authorization token\" }";
            }

            JsonObject requestBody;
            try {
                requestBody = JsonParser.parseString(req.body()).getAsJsonObject();
            } catch (JsonSyntaxException e) {
                res.status(400);
                return "{ \"message\": \"Error: bad request\" }";
            }

            int gameID;
            try {
                gameID = Integer.parseInt(requestBody.get("gameID").getAsString());
            } catch (NumberFormatException e) {
                res.status(400);
                return "{ \"message\": \"Error: bad request\" }";
            }

            String color = requestBody.get("playerColor").getAsString();
            boolean IsValidColor = color.equals("WHITE") || color.equals("BLACK");
            if (!IsValidColor) {
                res.status(400);
                return "{ \"message\": \"Error: bad request\" }";
            }

            // Call the service to join the game
            gameService.joinGame(authToken, gameID, color);

            res.status(200);
            return "{}";
        } catch (GameService.InvalidAuthTokenException e) {
            res.status(401);
            return "{ \"message\": \"Error: unauthorized\" }";

        } catch (GameService.UsernameAlreadyTakenException e ){
            res.status(403);
            return "{ \"message\": \"Error: already taken\" }";

        } catch (Exception e) {
            res.status(400);
            return "{ \"message\": \"Error: " + e.getMessage() + "\" }";
        }
    }

}