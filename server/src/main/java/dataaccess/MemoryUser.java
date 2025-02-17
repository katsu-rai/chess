package dataaccess;

import model.UserData;

import java.util.HashSet;

public class MemoryUser implements UserDAO {
    private HashSet<UserData> database;

    public MemoryUser() {
        database = new HashSet<UserData>();
    }

    @Override
    public UserData getUser(String username) throws DataAccessException{
        for (UserData userData : database) {
            if(userData.username().equals(username)){
                return userData;
            }
        }
        throw new DataAccessException("This username does not exist: " + username);
    }

    @Override
    public void createUser(UserData userData){
        database.add(userData);
    }

    @Override
    public boolean authenticate(String username, String password){
        for (UserData userData : database) {
            if(userData.username().equals(username) && userData.password().equals(password)){
                return true;
            }
        }
        return false;
    }

    @Override
    public void clear(){
        database = new HashSet<UserData>();
    }
}
