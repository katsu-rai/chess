package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.SQLException;

public class SQLUser implements UserDAO {

    public SQLUser() {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException ex) {
            throw new RuntimeException(ex);
        }

        try (var connection = DatabaseManager.getConnection()) {
            var createTestTable = """
                CREATE TABLE if NOT EXISTS user (
                                username VARCHAR(50) NOT NULL,
                                password VARCHAR(255) NOT NULL,
                                email VARCHAR(100),
                                PRIMARY KEY (username)
                                )""";
            try (var createTableStatement = connection.prepareStatement(createTestTable)) {
                createTableStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            var sql = "SELECT * FROM user WHERE username = ?";
            try (var statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                try (var resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        String userName = resultSet.getString("username");
                        String password = resultSet.getString("password");
                        String email = resultSet.getString("email");

                        return new UserData(userName, email, password);
                    }
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("Error retrieving user data");
        }

        return null;
    }

    @Override
    public void createUser(UserData user) throws Exception {
        String encodedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());

        try (var connection = DatabaseManager.getConnection()) {
            var sql = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
            try (var statement = connection.prepareStatement(sql)) {
                statement.setString(1, user.username());
                statement.setString(2, encodedPassword);
                statement.setString(3, user.email());

                int rowsAffected = statement.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Failed to insert user data.");
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("Error creating new user");
        }
    }

    @Override
    public boolean authenticate(String username, String password) {
        try {
            UserData user = getUser(username);
            if (user != null) {
                return BCrypt.checkpw(password, user.password());
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    @Override
    public void clear() {
        try (var connection = DatabaseManager.getConnection()) {
            var sql = "DELETE FROM user";
            try (var statement = connection.prepareStatement(sql)) {
                statement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Error clearing user data", e);
        }
    }
}