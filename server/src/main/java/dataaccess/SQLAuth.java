package dataaccess;

import model.AuthData;

public class SQLAuth implements AuthDAO {

    @Override
    void addAuth(AuthData authData) throws DataAccessException;

    @Override
    void deleteAuth(String authToken);

    @Override
    AuthData getAuth(String authToken) throws DataAccessException;

    @Override
    void clear();
}