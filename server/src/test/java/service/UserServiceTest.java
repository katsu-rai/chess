package service;


import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;

public class UserServiceTest {

    static UserDAO userDAO;
    static AuthDAO authDAO;
    static UserService userService;

    static UserData defaultUser;

    @BeforeAll
    static void init(){
        userDAO = new MemoryUser();
        authDAO = new MemoryAuth();
        userService = new UserService(userDAO,authDAO);
    }

    @BeforeEach
    void ready(){
        userDAO.clear();
        authDAO.clear();
        defaultUser = new UserData("Test", "test@test.com", "P@ssw0rd");
    }

    @Test
    @DisplayName("Create Valid User")
    void createValidUser() throws DataAccessException {
        AuthData result = userService.register(defaultUser);
        Assertions.assertEquals(authDAO.getAuth(result.authToken()), result);
    }

    @Test
    @DisplayName("Create Invalid User")
    void createInvalidUser() throws DataAccessException {
        userService.register(new UserData("Test", "test@test.com", "P@ssw0rd"));
        Assertions.assertThrows(RuntimeException.class, () -> {
            userService.register(new UserData("Test", "test@test.com", "P@ssw0rd"));
        });
    }

    @Test
    @DisplayName("Valid Login")
    void validLogin() throws DataAccessException {
        userService.register(defaultUser);
        AuthData result = userService.login(defaultUser);
        Assertions.assertEquals(authDAO.getAuth(result.authToken()), result);
    }

    @Test
    @DisplayName("Invalid Login")
    void inValidLogin() throws DataAccessException {
        Assertions.assertNull(userService.login(new UserData("InvalidUser", "test@test.com", "P@ssw0rd")));
    }

    @Test
    @DisplayName("Valid Logout")
    void validLogout() throws DataAccessException {
        AuthData authData = userService.register(defaultUser);
        userService.logout(authData.authToken());
        Assertions.assertThrows(DataAccessException.class, () -> {
            authDAO.getAuth(authData.authToken());
        });
    }

    @Test
    @DisplayName("Valid Logout")
    void inValidLogout() throws DataAccessException {
        AuthData authData = userService.register(defaultUser);
        userService.logout(authData.authToken());
        Assertions.assertThrows(DataAccessException.class, () -> {
            authDAO.getAuth(authData.authToken());
        });
    }

    @Test
    @DisplayName("Valid Clear")
    void validClear() throws DataAccessException {
        AuthData authData = userService.register(defaultUser);
        userService.clear();
        Assertions.assertThrows(DataAccessException.class, () -> {
            authDAO.getAuth(authData.authToken());
        });
        Assertions.assertThrows(DataAccessException.class, () -> {
            userDAO.getUser(defaultUser.username());
        });
    }

    @Test
    @DisplayName("No Exception")
    void invalidClear() {
        Assertions.assertDoesNotThrow(() -> userService.clear());
    }
}
