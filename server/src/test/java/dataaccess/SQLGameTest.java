package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.*;

import javax.xml.crypto.Data;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class SQLGameTest {

    private SQLGame gameDAO;
    private final GameData defaultGame = new GameData(
            1,
            "whiteUser",
            "blackUser",
            "Test Game",
            new ChessGame());

    @BeforeEach
    void setUp() {
        gameDAO = new SQLGame();
        gameDAO.clear();
    }

    @AfterEach
    void tearDown() {
        gameDAO.clear();
    }

    @Test
    void positiveGetAllGames() throws DataAccessException {
        // Adding three game entries to the database
        GameData game1 = new GameData(1, "whiteUser1", "blackUser1", "Game 1", new ChessGame());
        GameData game2 = new GameData(2, "whiteUser2", "blackUser2", "Game 2", new ChessGame());
        GameData game3 = new GameData(3, "whiteUser3", "blackUser3", "Game 3", new ChessGame());

        gameDAO.addGame(game1);
        gameDAO.addGame(game2);
        gameDAO.addGame(game3);

        HashSet<GameData> allGames = gameDAO.getAllGames();

        assertNotNull(allGames);
        assertEquals(3, allGames.size());

        assertTrue(allGames.stream().anyMatch(
                game ->game.gameID() == 1
                        && game.whiteUsername().equals("whiteUser1")
                        && game.blackUsername().equals("blackUser1")
                        && game.gameName().equals("Game 1")
        ));
        assertTrue(allGames.stream().anyMatch(
                game ->game.gameID() == 2
                        && game.whiteUsername().equals("whiteUser2")
                        && game.blackUsername().equals("blackUser2")
                        && game.gameName().equals("Game 2")
        ));
        assertTrue(allGames.stream().anyMatch(
                game ->game.gameID() == 3
                        && game.whiteUsername().equals("whiteUser3")
                        && game.blackUsername().equals("blackUser3")
                        && game.gameName().equals("Game 3")
        ));
    }

    @Test
    void negativeGetAllGames() {
        HashSet<GameData> allGames = gameDAO.getAllGames();
        assertNotNull(allGames);
        assertTrue(allGames.isEmpty());
    }

    @Test
    void positiveAddGame() throws DataAccessException {
        gameDAO.addGame(defaultGame);
        GameData result = gameDAO.getGame(1);

        assertEquals(defaultGame.gameID(), result.gameID());
        assertEquals(defaultGame.whiteUsername(), result.whiteUsername());
        assertEquals(defaultGame.blackUsername(), result.blackUsername());
        assertEquals(defaultGame.gameName(), result.gameName());
    }

    @Test
    void negativeAddGame(){
        GameData invalidGame = new GameData(0, null, null,null, null);
        assertThrows(DataAccessException.class, () -> gameDAO.addGame(invalidGame), "Adding an invalid game should throw an exception");
    }

    @Test
    void positiveGetGame() throws DataAccessException {
        gameDAO.addGame(defaultGame);

        GameData result = gameDAO.getGame(1);

        assertNotNull(result);
        assertEquals(1, result.gameID());
        assertEquals("whiteUser", result.whiteUsername());
        assertEquals("blackUser", result.blackUsername());
        assertEquals("Test Game", result.gameName());
    }

    @Test
    void negativeGetGameGameNotFound() throws DataAccessException {
        GameData result = gameDAO.getGame(999);
        assertNull(result);
    }

    @Test
    void positiveClear() throws DataAccessException {
        gameDAO.addGame(new GameData(1, "whiteUser1", "blackUser1", "Game 1", new ChessGame()));
        gameDAO.addGame(new GameData(2, "whiteUser2", "blackUser2", "Game 2", new ChessGame()));

        gameDAO.clear();

        HashSet<GameData> allGames = gameDAO.getAllGames();
        assertTrue(allGames.isEmpty());
    }

    @Test
    void positiveGetMaxId() throws DataAccessException {
        gameDAO.addGame(new GameData(1, "whiteUser1", "blackUser1", "Game 1", new ChessGame()));
        gameDAO.addGame(new GameData(2, "whiteUser2", "blackUser2", "Game 2", new ChessGame()));

        int maxId = gameDAO.getMaxId();
        assertEquals(2, maxId);
    }

    @Test
    void negativeGetMaxIdEmptyDatabase() throws DataAccessException {
        gameDAO.clear();

        int maxId = gameDAO.getMaxId();
        assertEquals(0, maxId);
    }

    @Test
    void positiveUpdateGame() throws DataAccessException {
        gameDAO.addGame(defaultGame);

        GameData game = new GameData(1, "whiteUserUpdated", "blackUserUpdated", "Updated Game", new ChessGame());
        gameDAO.updateGame(game);

        GameData updatedGame = gameDAO.getGame(1);
        assertNotNull(updatedGame);
        assertEquals("whiteUserUpdated", updatedGame.whiteUsername());
        assertEquals("blackUserUpdated", updatedGame.blackUsername());
        assertEquals("Updated Game", updatedGame.gameName());
    }

    @Test
    void negativeUpdateGameGameNotFound() throws DataAccessException {
        GameData nonExistingGame = new GameData(999, "whiteUser1", "blackUser1", "Non Existing Game", new ChessGame());
        assertThrows(DataAccessException.class, () -> gameDAO.updateGame(nonExistingGame), "Updating a non-existing game should throw an exception");
    }

}
