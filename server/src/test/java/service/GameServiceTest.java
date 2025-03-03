package service;


import chess.ChessBoard;
import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.util.HashSet;

public class GameServiceTest {

    static String authToken;
    static UserDAO userDAO;
    static AuthDAO authDAO;
    static GameDAO gameDAO;
    static GameService gameService;
    static UserService userService;

    static UserData defaultUser = new UserData("Test", "test@test.com", "P@ssw0rd");
    static GameData defaultGame;

    @BeforeAll
    static void init(){
        userDAO = new MemoryUser();
        gameDAO = new MemoryGame();
        authDAO = new MemoryAuth();
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(gameDAO, authDAO);
        AuthData authData = userService.register(defaultUser);
        authToken = authData.authToken();
    }

    @BeforeEach
    void ready(){
        gameDAO.clear();
        defaultGame = new GameData(1, null, null, "Game1", new ChessGame());
    }

    @Test
    @DisplayName("Valid Create Game")
        void validCreateGame() throws Exception {
        gameService.createGame(authToken, defaultGame);
        Assertions.assertEquals(gameDAO.getGame(defaultGame.gameID()).gameID(), defaultGame.gameID());
    }

    @Test
    @DisplayName("Invalid Create Game")
        void invalidCreateGame() throws Exception {
        Assertions.assertThrows(Exception.class, () -> gameService.createGame("", defaultGame));
    }

    @Test
    @DisplayName("Valid getAllGames")
    void getAllGames() throws Exception {
        gameService.createGame(authToken, new GameData(1, null, null, "Game1", new ChessGame()));
        gameService.createGame(authToken, new GameData(2, null, null, "Game2", new ChessGame()));
        gameService.createGame(authToken, new GameData(3, null, null, "Game3", new ChessGame()));

        HashSet<GameData> allGames = gameService.getAllGames(authToken);
        Assertions.assertEquals(gameDAO.getAllGames(), allGames);
    }

    @Test
    @DisplayName("Invalid getAllGames")
    void invalidGetAllGames() throws Exception {
        gameService.createGame(authToken, new GameData(1, null, null, "Game1", new ChessGame()));
        gameService.createGame(authToken, new GameData(2, null, null, "Game2", new ChessGame()));
        gameService.createGame(authToken, new GameData(3, null, null, "Game3", new ChessGame()));

        Assertions.assertThrows(Exception.class, () -> gameService.getAllGames(""));
    }

    @Test
    @DisplayName("Valid Join Game")
    void validJoinGame() throws Exception {
        gameService.createGame(authToken, defaultGame);
        gameService.joinGame(authToken, 1, "WHITE");

        Assertions.assertEquals(1, gameDAO.getGame(defaultGame.gameID()).gameID());
        Assertions.assertEquals("Test", gameDAO.getGame(defaultGame.gameID()).whiteUsername());
        Assertions.assertNull(gameDAO.getGame(defaultGame.gameID()).blackUsername());
        Assertions.assertEquals("Game1", gameDAO.getGame(defaultGame.gameID()).gameName());
    }

    @Test
    @DisplayName("Invalid Join Game")
    void invalidJoinGame() throws Exception {
        gameService.createGame(authToken, defaultGame);

        Assertions.assertThrows(Exception.class, () -> gameService.joinGame(authToken, 1, "RED"));
    }


}
