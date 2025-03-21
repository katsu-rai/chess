package dataaccess;

import model.AuthData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLAuth implements AuthDAO {

    public SQLAuth() {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        try (Connection connection = DatabaseManager.getConnection()) {
            var createTestTable = """            
                    CREATE TABLE if NOT EXISTS auth (
                                    authToken VARCHAR(50) NOT NULL,
                                    username VARCHAR(50) NOT NULL,
                                    PRIMARY KEY (authToken)
                                    )""";

            try (var createTableStatement = connection.prepareStatement(createTestTable)) {
                createTableStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addAuth(AuthData authData) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO auth (username, authToken) VALUES(?, ?)")) {
                statement.setString(1, authData.username());
                statement.setString(2, authData.authToken());
                statement.executeUpdate();
            }
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAuth(String authToken){
        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM auth WHERE authToken = ?")) {
                statement.setString(1, authToken);
                statement.executeUpdate();
            }
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String sql = "SELECT username FROM auth WHERE authToken = ?";

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, authToken);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String username = resultSet.getString("username");
                    return new AuthData(username, authToken);
                } else {
                    throw new DataAccessException("This AuthToken does not exist: " + authToken);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Database error while retrieving auth data");
        }
    }


    @Override
    public void clear(){
        String sql = "TRUNCATE auth";
        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
                ) {

                 statement.executeUpdate();

        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}