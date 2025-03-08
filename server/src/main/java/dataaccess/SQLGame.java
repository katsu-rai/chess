package dataaccess;

import model.GameData;

import java.util.HashSet;

public class SQLGame implements GameDAO {

    @Override
    public HashSet<GameData> getAllGames(){
        return new HashSet<>();
    }

    @Override
    public void addGame(GameData game) throws DataAccessException{

    }

    @Override
    public GameData getGame(int id) throws DataAccessException{
        return null;
    }

    @Override
    public void clear(){

    }

    @Override
    public int getMaxId(){
        return 0;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException{

    }
}