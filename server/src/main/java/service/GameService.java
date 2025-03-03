package service;

import chess.ChessBoard;
import chess.ChessGame;
import dataaccess.AuthDAO;
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

    public void joinGame(String authToken, int gameID, String color) throws Exception, InvalidAuthTokenException, UsernameAlreadyTakenException {
        AuthData authData;
        try {
            authData = authDAO.getAuth(authToken);
        } catch (Exception e) {
            throw new InvalidAuthTokenException(e.getMessage());
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

    public void clear(){
        gameDAO.clear();
    }

    public class InvalidAuthTokenException extends Exception {
        public InvalidAuthTokenException(String message) {
            super(message);
        }
    }

    public class UsernameAlreadyTakenException extends Exception {
        public UsernameAlreadyTakenException(String message) {
            super(message);
        }
    }
}

