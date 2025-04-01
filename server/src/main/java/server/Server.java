package server;

import dataaccess.*;
import org.eclipse.jetty.websocket.api.Session;
import service.*;
import spark.*;

import java.util.concurrent.ConcurrentHashMap;


public class Server {

    static ConcurrentHashMap<Session, Integer> gameSessions = new ConcurrentHashMap<org.eclipse.jetty.websocket.api.Session, Integer>();

    UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;

    UserService userService;
    UserHandler userHandler;

    GameService gameService;
    GameHandler gameHandler;


    public Server() {
        userDAO = new SQLUser();
        authDAO= new SQLAuth();
        gameDAO = new SQLGame();

        userService = new UserService(userDAO, authDAO);
        userHandler = new UserHandler(userService);

        gameService = new GameService(gameDAO, authDAO);
        gameHandler = new GameHandler(gameService);
    }

    public int run(int desiredPort) {

        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", (req, res) -> {
            try {
                clear();
                res.status(200);
                return "";
            } catch (Exception e) {
                res.status(500);
                return "Error: " + e.getMessage();
            }
        });

        Spark.staticFiles.location("web");

        Spark.webSocket("/connect", WebSocketHandler.class);

        Spark.post("/user", userHandler::register);
        Spark.post("/session", userHandler::login);
        Spark.delete("/session", userHandler::logout);
        Spark.get("/game", gameHandler::getAllGames);
        Spark.post("/game", gameHandler::createGame);
        Spark.put("/game", gameHandler::joinGame);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public void clear() {
        userService.clear();
        gameService.clear();
    }

}


