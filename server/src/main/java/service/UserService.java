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
        userDAO.createUser(user);
        String authToken = UUID.randomUUID().toString();
        return new AuthData(user.username(), authToken);
    }

    public AuthData login(UserData userData) {
//        Check if the user has matched password
        if(userDAO.authenticate(userData.username(), userData.password())) {

//            Generate authToken
            String authToken = UUID.randomUUID().toString();
            AuthData authData = new AuthData(userData.username(), authToken);
            try {
                authDAO.addAuth(authData);
            } catch(DataAccessException e) {
                return null;
            }

            return authData;
        }

        return null;
    }

    public void logout(String authToken) {
        try {
            authDAO.deleteAuth(authToken);
        } catch(DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void clear(){
        userDAO.clear();
        authDAO.clear();
    }
}

