package service;

import dataaccess.*;
import model.*;

import java.util.HashSet;

public class GameService {

    GameDAO gameDAO;
    AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public HashSet<GameData> getAllGames(String authToken) {
        try {
            authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return gameDAO.getAllGames();
    }

//    public void joinGame(String authToken, GameData gameData) {
//        try {
//            authDAO.getAuth(authToken);
//        } catch (DataAccessException e) {
//            throw new RuntimeException(e);
//        }
//
//        try {
//            gameDAO.(authToken);
//        } catch (DataAccessException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
