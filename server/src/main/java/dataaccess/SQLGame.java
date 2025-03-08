package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashSet;
import java.sql.*;
import com.google.gson.Gson;

public class SQLGame implements GameDAO {

    public SQLGame() {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException ex) {
            throw new RuntimeException(ex);
        }

        try (var connection = DatabaseManager.getConnection()) {
            var createTestTable = """            
                CREATE TABLE if NOT EXISTS game (
                                gameID INT NOT NULL,
                                whiteUsername VARCHAR(50),
                                blackUsername VARCHAR(50),
                                gameName VARCHAR(50) NOT NULL,
                                chessGame TEXT,
                                PRIMARY KEY (gameID)
                                )""";
            try (var createTableStatement = connection.prepareStatement(createTestTable)) {
                createTableStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public HashSet<GameData> getAllGames() {
        HashSet<GameData> resultGames = new HashSet<>();
        String sql = "SELECT * FROM game";

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                resultGames.add(new GameData(
                        rs.getInt("gameID"),
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                        rs.getString("gameName"),
                        deserializeGame(rs.getString("chessGame"))
                ));
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }

        return resultGames;
    }


    @Override
    public void addGame(GameData game) throws DataAccessException {
        String sql = "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, chessGame) VALUES (?, ?, ?, ?, ?)";
        try (var connection = DatabaseManager.getConnection();
             var stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, game.gameID());
            stmt.setString(2, game.whiteUsername());
            stmt.setString(3, game.blackUsername());
            stmt.setString(4, game.gameName());
            stmt.setString(5, serializeGame(game.game()));
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Error inserting game: " + e.getMessage());
        }
    }

    @Override
    public GameData getGame(int id) throws DataAccessException {
        String sql = "SELECT * FROM game WHERE gameID = ?";
        try (Connection connection = DatabaseManager.getConnection();
             var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new GameData(
                            rs.getInt("gameID"),
                            rs.getString("whiteUsername"),
                            rs.getString("blackUsername"),
                            rs.getString("gameName"),
                            deserializeGame(rs.getString("chessGame")));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving game: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void clear() {
        String sql = "DELETE FROM game";
        try (var connection = DatabaseManager.getConnection();
             var stmt = connection.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getMaxId() {
        String sql = "SELECT MAX(gameID) AS maxID FROM game";
        try (var connection = DatabaseManager.getConnection();
             var stmt = connection.prepareStatement(sql);
             var rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("maxID");
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        String sql = "UPDATE game SET whiteUsername = ?, blackUsername = ?, gameName = ?, chessGame = ? WHERE gameID = ?";
        try (var connection = DatabaseManager.getConnection();
             var stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, game.whiteUsername());
            stmt.setString(2, game.blackUsername());
            stmt.setString(3, game.gameName());
            stmt.setString(4, serializeGame(game.game()));
            stmt.setInt(5, game.gameID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error updating game: " + e.getMessage());
        }
    }
    
    private String serializeGame(ChessGame game) {
        return new Gson().toJson(game);
    }

    private ChessGame deserializeGame(String serializedGame) {
        return new Gson().fromJson(serializedGame, ChessGame.class);
    }
}
