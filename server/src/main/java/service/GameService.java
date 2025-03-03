package service;

import chess.ChessBoard;
import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import model.GameData;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public HashSet<GameData> getAllGames(String authToken) throws Exception {
        try {
            authDAO.getAuth(authToken);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        return gameDAO.getAllGames();
    }

    public int createGame(String authToken, String gameName) throws Exception {
        try {
            authDAO.getAuth(authToken);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        int gameID = gameDAO.getMaxId() + 1;

        try {
            ChessBoard board = new ChessBoard();
            ChessGame game = new ChessGame();

            board.resetBoard();
            game.setBoard(board);

            GameData gameData = new GameData(gameID, null, null, gameName, game);
            gameDAO.addGame(gameData);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return gameID;
    }

    public GameData getGameData(String authToken, int gameID) throws Exception {
        try {
            authDAO.getAuth(authToken);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        try {
            GameData gameData = gameDAO.getGame(gameID);
            return gameData;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public boolean joinGame(String authToken, int gameID, String color) throws Exception {
        try {
            authDAO.getAuth(authToken);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        GameData gameData;
        try {
            gameData = gameDAO.getGame(gameID);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        String whiteUser = gameData.whiteUsername();
        String blackUser = gameData.blackUsername();

        if (color.equals("WHITE")) {
            whiteUser = gameData.whiteUsername();
        } else {
            blackUser = gameData.blackUsername();
        }

        try {
            gameDAO.updateGame(new GameData(gameID, whiteUser, blackUser, gameData.gameName(), gameData.game()));
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return true;
    }

    public void clear(){
        gameDAO.clear();
        authDAO.clear();
    }
}
