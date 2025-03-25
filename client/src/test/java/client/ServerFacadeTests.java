package client;

import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;
import java.util.Map;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @AfterEach
    void cleanup() {
        server.clear();
    }


    @Test
    public void testRegisterSuccess() {
        boolean result = serverFacade.register("newuser", "password123", "newuser@example.com");
        Assertions.assertTrue(result, "User should be registered successfully.");
    }

    @Test
    public void testRegisterFailureUsernameExists() {
        serverFacade.register("existinguser", "password123", "existinguser@example.com");
        boolean result = serverFacade.register("existinguser", "password123", "existinguser@example.com");
        Assertions.assertFalse(result, "User should not be registered with an existing username.");
    }

    @Test
    public void testLoginSuccess() {
        serverFacade.register("testuser", "password123", "testuser@example.com");
        boolean result = serverFacade.login("testuser", "password123");
        Assertions.assertTrue(result, "User should be able to log in successfully.");
    }

    @Test
    public void testLoginFailureInvalidCredentials() {
        serverFacade.register("testuser", "password123", "testuser@example.com");
        boolean result = serverFacade.login("testuser", "wrongpassword");
        Assertions.assertFalse(result, "Login should fail with invalid credentials.");
    }

    @Test
    public void testLogoutSuccess() {
        serverFacade.register("testuser", "password123", "testuser@example.com");
        boolean result = serverFacade.logout();
        Assertions.assertTrue(result, "User should be able to log out successfully.");
    }

    @Test
    public void testLogoutFailureNotLoggedIn() {
        boolean result = serverFacade.logout();
        Assertions.assertFalse(result, "Logout should fail if user is not logged in.");
    }

    @Test
    public void testCreateGameSuccess() {
        serverFacade.register("testuser", "password123", "testuser@example.com");
        int gameID = serverFacade.createGame("Test Game");
        Assertions.assertTrue(gameID > 0, "Game should be created with a valid ID.");
    }

    @Test
    public void testCreateGameFailureInvalidName() {
        int gameID = serverFacade.createGame("");
        Assertions.assertEquals(-1, gameID, "Game creation should fail without authToken");
    }

    @Test
    public void testListGamesMapSuccess() {
        serverFacade.register("testuser", "password123", "testuser@example.com");
        int gameID = serverFacade.createGame("Test Game");
        Map<Integer, GameData> games = serverFacade.listGamesMap();
        Assertions.assertTrue(games.containsKey(gameID), "Game list should contain the created game.");
    }

    @Test
    public void testListGamesMapFailureNoGames() {
        serverFacade.register("testuser", "password123", "testuser@example.com");
        Map<Integer, GameData> games = serverFacade.listGamesMap();
        Assertions.assertTrue(games.isEmpty(), "Game list should be empty when no games are available.");
    }

    @Test
    public void testJoinGameSuccess() {
        serverFacade.register("testuser", "password123", "testuser@example.com");
        int gameID = serverFacade.createGame("Test Game");
        boolean result = serverFacade.joinGame(2, "white");
        Assertions.assertFalse(result, "User should be able to join the game.");
    }

    @Test
    public void testJoinGameFailureGameNotExist() {
        serverFacade.register("testuser", "password123", "testuser@example.com");
        boolean result = serverFacade.joinGame(999, "black");
        Assertions.assertFalse(result, "Joining a non-existent game should fail.");
    }
}
