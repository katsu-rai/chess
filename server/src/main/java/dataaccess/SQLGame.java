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
                                gameName VARCHAR(50),
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

    }

    @Override
    public GameData getGame(int id) throws DataAccessException {
        return null;
    }

    @Override
    public void clear() {

    }

    @Override
    public int getMaxId() {
        return 0;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {

    }

    private String serializeGame(ChessGame game) {
        return new Gson().toJson(game);
    }

    private ChessGame deserializeGame(String serializedGame) {
        return new Gson().fromJson(serializedGame, ChessGame.class);
    }
}
