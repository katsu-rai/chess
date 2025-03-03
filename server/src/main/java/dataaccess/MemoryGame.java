package dataaccess;

import model.GameData;

import java.util.HashSet;

public class MemoryGame implements GameDAO{
    private HashSet<GameData> database;

    public MemoryGame() {
        database = new HashSet<GameData>();
    }

    @Override
    public HashSet<GameData> getAllGames(){
        return database;
    }

    @Override
    public void addGame(GameData game) {
        database.add(game);
    }

    @Override
    public GameData getGame(int id) throws DataAccessException{
        for (GameData game: database) {
            if(game.gameID() == id) {
                return game;
            }
        }
        throw new DataAccessException("This Game ID does not exist: " + id);
    }

    @Override
    public void clear(){
        database = new HashSet<GameData>();
    }

    @Override
    public int getMaxId(){
        return database.size();
    }

    @Override
    public void updateGame(GameData newGameData) throws DataAccessException {
        for (GameData game: database) {
            if(game.gameID() == newGameData.gameID()) {
                database.remove(game);
                database.add(newGameData);
                return;
            }
        }
        throw new DataAccessException("This Game ID does not exist: " + newGameData.gameID());
    }
}
