package dataaccess;

import model.AuthData;

import java.util.HashSet;

public class MemoryAuth implements AuthDAO {
    HashSet<AuthData> database;

    public MemoryAuth(){
        database = new HashSet<AuthData>();
    }

    @Override
    public void addAuth(AuthData authData){
        database.add(authData);
    }

    @Override
    public void deleteAuth(String authToken){
        for (AuthData authData : database){
            if(authData.authToken().equals(authToken)){
                database.remove(authData);
                break;
            }
        }
    }

    @Override
    public AuthData getAuth(String token) throws DataAccessException {
        for (AuthData authData : database){
            if(authData.authToken().equals(token)){
                return authData;
            }
        }
        throw new DataAccessException("This AuthToken does not exist: " + token);
    }

    @Override
    public void clear(){
        database = new HashSet<AuthData>();
    }
}
