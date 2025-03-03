package dataaccess;

import model.GameData;

import java.util.HashSet;

public interface GameDAO {

    HashSet<GameData> getAllGames();
    void addGame(GameData game) throws DataAccessException;
    GameData getGame(int id) throws DataAccessException;
    void clear();
    int getMaxId();
    void updateGame(GameData game) throws DataAccessException;
}
