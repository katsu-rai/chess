package service;

import dataaccess.*;
import model.*;

import java.util.UUID;

public class UserService {

    UserDAO userDAO;
    AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData register(UserData user) {
        try {
            userDAO.createUser(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String authToken = UUID.randomUUID().toString();
        AuthData newAuthData = new AuthData(user.username(), authToken);
        try {
            authDAO.addAuth(newAuthData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new AuthData(user.username(), authToken);
    }

    public AuthData login(UserData userData) {
        boolean isUserAuthenticated = userDAO.authenticate(userData.username(), userData.password());
//        Check if the user has matched password
        if(isUserAuthenticated) {
//            Generate authToken
            String authToken = UUID.randomUUID().toString();
            AuthData authData = new AuthData(userData.username(), authToken);
            try {
                authDAO.addAuth(authData);
            } catch(DataAccessException e) {
                return null;
            }

            return authData;
        } else {
            return null;
        }
    }

    public void logout(String authToken) {
        try {
            authDAO.getAuth(authToken);
        } catch(DataAccessException e) {
            throw new RuntimeException(e);
        }

        authDAO.deleteAuth(authToken);
    }

    public void clear(){
        userDAO.clear();
        authDAO.clear();
    }
}

