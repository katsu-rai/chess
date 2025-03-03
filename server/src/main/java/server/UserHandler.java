package server;

import com.google.gson.Gson;
import service.*;
import model.*;
import spark.*;

public class UserHandler {
     UserService userService;

     public UserHandler(UserService userService){
         this.userService = userService;
     }


    public Object register(Request req, Response res) throws Exception {

        UserData userData = new Gson().fromJson(req.body(), UserData.class);

        if (userData.username() == null || userData.password() == null) {
            res.status(400);
            return "{ \"message\": \"Error: bad request\" }";
        }
        try {
            AuthData authData = userService.register(userData);
            res.status(200);
            return new Gson().toJson(authData);
        } catch (Exception e) {
            res.status(403);
            return "{ \"message\": \"Error: already taken\" }";
        }
    }

    public Object login(Request req, Response res) throws Exception {
        UserData userData = new Gson().fromJson(req.body(), UserData.class);
        AuthData authData = userService.login(userData);

        if  (authData != null) {
            res.status(200);
            return new Gson().toJson(authData);
        } else {
            res.status(401);
            return "{ \"message\": \"Error: unauthorized\" }";
        }
    }

    public Object logout(Request req, Response res) throws Exception {
        String authToken = req.headers("authorization");

        try {
            userService.logout(authToken);
            res.status(200);
            return "{}";
        } catch (Exception e) {
            res.status(401);
            return "{ \"message\": \"Error: unauthorized\" }";
        }
    }
}
