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


    public Object register(Request req, Response resp) throws Exception {

        UserData userData = new Gson().fromJson(req.body(), UserData.class);

        if (userData.username() == null || userData.password() == null) {
            System.out.println("Invalid username or password");
        }

        try {
            AuthData authData = userService.register(userData);
            resp.status(200);
            return new Gson().toJson(authData);
        } catch (Exception e) {
            resp.status(400);
            return "Error happened during registration process";
        }
    }

    public Object login(Request req, Response resp) throws Exception {
        UserData userData = new Gson().fromJson(req.body(), UserData.class);

        try {
            AuthData authData = userService.login(userData);
            resp.status(200);
            return new Gson().toJson(authData);
        } catch (Exception e) {
            resp.status(400);
            return "Error happened during login process";
        }
    }

    public Object logout(Request req, Response resp) throws Exception {
        String authToken = req.headers("authorization");

        try {
            userService.logout(authToken);
            resp.status(200);
            return "{}";
        } catch (Exception e) {
            resp.status(400);
            return "Error happened during logout process";
        }
    }
}
