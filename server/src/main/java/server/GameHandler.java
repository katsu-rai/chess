package server;

import com.google.gson.Gson;
import model.GameData;
import service.GameService;
import spark.*;

import javax.swing.text.html.parser.Parser;
import java.text.ParseException;
import java.util.HashSet;

public class GameHandler {
    private final GameService gameService;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public Object getAllGames(Request req, Response res) throws Exception {
        String authToken = req.headers("Authorization");
        try {
            HashSet<GameData> games = gameService.getAllGames(authToken);
            res.status(200);
            return new Gson().toJson(games);

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public Object addGame(Request req, Response res) throws Exception {
        String authToken = req.headers("Authorization");
        String gameName = req.queryParams("gameID");

        try {
            int gameId = gameService.createGame(authToken, gameName);
            res.status(200);
            return new Gson().toJson(gameId);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public Object joinGame(Request req, Response res) throws Exception {
        String authToken = req.headers("Authorization");
        int gameID = Integer.parseInt(req.queryParams("gameID"));
        String color = req.queryParams("color");

        try {
            Boolean result = gameService.joinGame(authToken, gameID, color);
            res.status(200);
            return new Gson().toJson(result);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}