package dataaccess;

import model.GameData;

import java.util.HashSet;

public class SQLGame implements GameDAO {

    @Override
    HashSet<GameData> getAllGames();

    @Override
    void addGame(GameData game) throws DataAccessException;

    @Override
    GameData getGame(int id) throws DataAccessException;

    @Override
    void clear();

    @Override
    int getMaxId();

    @Override
    void updateGame(GameData game) throws DataAccessException;
}