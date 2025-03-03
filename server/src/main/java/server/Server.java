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


    public Server() {
        userDAO = new MemoryUser();
        authDAO= new MemoryAuth();
        gameDAO = new MemoryGame();

        userService = new UserService(userDAO, authDAO);
        userHandler = new UserHandler(userService);

        gameService = new GameService(gameDAO, authDAO);
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

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public void clear() {
        userService.clear();
    }

}


