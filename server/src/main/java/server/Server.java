package server;

import dataaccess.*;
import service.*;
import spark.*;

public class Server {
    UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;

    UserService userService;
    UserHandler userHandler;

    GameService gameService;
    GameHandler gameHandler;


    public Server() {
        userDAO = new MemoryUser();
        authDAO= new MemoryAuth();
        gameDAO = new MemoryGame();

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
            clear();
            res.status(200);
            return "";
        });

        Spark.post("/user", userHandler::register);
        Spark.post("/session", userHandler::login);
        Spark.delete("/session", userHandler::logout);
        Spark.get("/game", gameHandler::getAllGames);
        Spark.post("/game", gameHandler::addGame);
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


