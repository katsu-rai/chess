package dataaccess;

import model.UserData;
import org.junit.jupiter.api.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class SQLUserTest {

    private SQLUser sqlUser;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        sqlUser = new SQLUser();
        sqlUser.clear();
    }

    @AfterEach
    void tearDown(){
        sqlUser.clear();
    }
    @Test
    void testCreateUserSuccess() throws Exception {
        UserData user = new UserData("testUser", "test@example.com", "password123");
        sqlUser.createUser(user);

        UserData retrievedUser = sqlUser.getUser("testUser");
        assertNotNull(retrievedUser);
        assertEquals("testUser", retrievedUser.username());
        assertEquals("test@example.com", retrievedUser.email());
        assertTrue(passwordEncoder.matches("password123", retrievedUser.password()));
    }

    @Test
    void testCreateUserDuplicateUsername() {
        UserData user1 = new UserData("testUser", "password123", "test@example.com");
        UserData user2 = new UserData("testUser", "newpassword", "other@example.com");

        assertDoesNotThrow(() -> sqlUser.createUser(user1));
        Exception exception = assertThrows(Exception.class, () -> sqlUser.createUser(user2));
        assertTrue(exception.getMessage().contains("Error creating new user"));
    }

    @Test
    void testGetUserExistingUser() throws Exception {
        UserData user = new UserData("testUser", "test@example.com", "password123");
        sqlUser.createUser(user);

        UserData retrievedUser = sqlUser.getUser("testUser");
        assertNotNull(retrievedUser);
        assertEquals("testUser", retrievedUser.username());
        assertEquals("test@example.com", retrievedUser.email());
    }

    @Test
    void testGetUserNonExistentUser() throws DataAccessException{
        UserData retrievedUser = sqlUser.getUser("nonExistentUser");
        assertNull(retrievedUser);
    }

    @Test
    void testAuthenticateSuccess() throws Exception {
        UserData user = new UserData("testUser", "test@example.com", "password123");
        sqlUser.createUser(user);

        assertTrue(sqlUser.authenticate("testUser", "password123"));
    }

    @Test
    void testAuthenticateInvalidPassword() throws Exception {
        UserData user = new UserData("testUser", "test@example.com", "password123");
        sqlUser.createUser(user);

        assertFalse(sqlUser.authenticate("testUser", "wrongpassword"));
    }

    @Test
    void testAuthenticateNonExistentUser() {
        assertFalse(sqlUser.authenticate("nonExistentUser", "password123"));
    }

    @Test
    void testClearSuccess() throws Exception {
        UserData user = new UserData("testUser", "test@example.com", "password123");
        sqlUser.createUser(user);

        sqlUser.clear();
        assertNull(sqlUser.getUser("testUser"));
    }

    @Test
    void testClearEmptyTable() {
        assertDoesNotThrow(() -> sqlUser.clear());
    }
}
