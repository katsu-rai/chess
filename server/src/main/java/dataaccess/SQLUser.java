package dataaccess;

import model.UserData;

public class SQLUser implements UserDAO {

    @Override
    public UserData getUser(String username) throws DataAccessException{
        return null;
    }

    @Override
    public void createUser(UserData user) throws Exception{

    }

    @Override
    public boolean authenticate(String username, String password){
        return false;
    }

    @Override
    public void clear(){

    }
}