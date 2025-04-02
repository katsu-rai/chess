package service;

import chess.ChessBoard;
import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
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

    public int createGame(String authToken, GameData gameData) throws Exception {
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

            GameData newGameData = new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
            gameDAO.addGame(newGameData);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return gameID;
    }

    public void joinGame(String authToken, int gameID, String color) throws Exception {
        AuthData authData;
        try {
            authData = authDAO.getAuth(authToken);
        } catch (Exception e) {
            throw new InvalidAuthTokenException(e.getMessage());
        }

        if (!color.equals("BLACK")&& !color.equals("WHITE")) {
            throw new Exception();
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
            if (whiteUser != null) {
                throw new UsernameAlreadyTakenException("White user is already taken");
            }
            whiteUser = authData.username();
        } else {
            if (blackUser != null) {
                throw new UsernameAlreadyTakenException("Black user is already taken");
            }
            blackUser = authData.username();
        }

        try {
            gameDAO.updateGame(new GameData(gameID, whiteUser, blackUser, gameData.gameName(), gameData.game()));
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public GameData getGameData(String authToken, Integer gameID) throws Exception {
        try {
            authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new Exception(e.getMessage());
        }

        try {
            return gameDAO.getGame(gameID);
        } catch (DataAccessException e) {
            throw new Exception(e.getMessage());
        }
    }

    public void updateGame(String authToken, GameData gameData) throws RuntimeException {
        try {
            authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }

        try {
            gameDAO.updateGame(gameData);
        } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void clear(){
        gameDAO.clear();
    }

    public static class InvalidAuthTokenException extends Exception {
        public InvalidAuthTokenException(String message) {
            super(message);
        }
    }

    public static class UsernameAlreadyTakenException extends Exception {
        public UsernameAlreadyTakenException(String message) {
            super(message);
        }
    }
}

