package dataaccess;

import dataaccess.SQLAuth;
import model.AuthData;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

class SQLAuthTest {

    private SQLAuth authDAO;
    private final AuthData testAuth = new AuthData("TestUser", "12345TOKEN");

    @BeforeEach
    void setUp() throws SQLException, DataAccessException {
        authDAO = new SQLAuth();
        authDAO.clear();
    }

    @AfterEach
    void tearDown() throws SQLException, DataAccessException {
        authDAO.clear();
    }

    @Test
    void testAddAuthPositive() throws DataAccessException {
        assertDoesNotThrow(() -> authDAO.addAuth(testAuth));
        AuthData retrieved = authDAO.getAuth(testAuth.authToken());
        assertEquals(testAuth.username(), retrieved.username());
    }

    @Test
    void testAddAuthNegativeDuplicateAuthToken() throws DataAccessException {
        authDAO.addAuth(testAuth);
        assertThrows(RuntimeException.class, () -> authDAO.addAuth(testAuth));
    }

    @Test
    void testDeleteAuthPositive() throws DataAccessException {
        authDAO.addAuth(testAuth);
        authDAO.deleteAuth(testAuth.authToken());
        assertThrows(DataAccessException.class, () -> authDAO.getAuth(testAuth.authToken()));
    }

    @Test
    void testDeleteAuthNegativeNonExistentAuthToken() {
        assertDoesNotThrow(() -> authDAO.deleteAuth("NONEXISTENTTOKEN"));
    }


    @Test
    void testGetAuthPositive() throws DataAccessException {
        authDAO.addAuth(testAuth);
        AuthData retrieved = authDAO.getAuth(testAuth.authToken());
        assertEquals(testAuth.username(), retrieved.username());
    }

    @Test
    void testGetAuthNegativeNonExistentAuthToken() {
        assertThrows(DataAccessException.class, () -> authDAO.getAuth("INVALIDTOKEN"));
    }

    @Test
    void testClearPositive() throws DataAccessException {
        authDAO.addAuth(testAuth);
        authDAO.clear();
        assertThrows(DataAccessException.class, () -> authDAO.getAuth(testAuth.authToken()));
    }

    @Test
    void testClearNegativeEmptyTable() {
        assertDoesNotThrow(() -> authDAO.clear());
    }
}
