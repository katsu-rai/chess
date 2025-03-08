package dataaccess;

import model.UserData;

public class SQLUser implements UserDAO {

    @Override
    UserData getUser(String username) throws DataAccessException;

    @Override
    void createUser(UserData user) throws Exception;

    @Override
    boolean authenticate(String username, String password);

    @Override
    void clear();
}