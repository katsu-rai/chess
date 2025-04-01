package server;

import chess.ChessGame;
import com.google.gson.Gson;
import model.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.*;
import service.*;
import websocket.messages.*;


import java.io.IOException;
import java.util.Objects;

@WebSocket
public class WebSocketHandler {

    private final GameService gameService;
    private final UserService userService;

    public WebSocketHandler(GameService gameService, UserService userService) {
        this.gameService = gameService;
        this.userService = userService;
    }

    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {
        Server.gameSessions.put(session, 0);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        Server.gameSessions.remove(session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {

        System.out.printf("Received: %s\n", message);

        if (message.contains("\"commandType\":\"JOIN_PLAYER\"")) {
            Connect command = new Gson().fromJson(message, Connect.class);
            Server.gameSessions.replace(session, command.getGameID());
            handlePlayerConnect(session, command);
        }
        else if (message.contains("\"commandType\":\"JOIN_OBSERVER\"")) {
            Connect command = new Gson().fromJson(message, Connect.class);
            Server.gameSessions.replace(session, command.getGameID());
            handleObserverConnect(session, command);
        }
        else if (message.contains("\"commandType\":\"MAKE_MOVE\"")) {
            MakeMove command = new Gson().fromJson(message, MakeMove.class);
//            handleMakeMove(session, command);
        }
        else if (message.contains("\"commandType\":\"LEAVE\"")) {
            Leave command = new Gson().fromJson(message, Leave.class);
//            handleLeave(session, command);
        }
        else if (message.contains("\"commandType\":\"RESIGN\"")) {
            Resign command = new Gson().fromJson(message, Resign.class);
//            handleResign(session, command);
        }
    }

    private void handlePlayerConnect(Session session, Connect command) throws Exception {

        try {
            AuthData auth = userService.getAuth(command.getAuthToken());
            GameData game = gameService.getGameData(command.getAuthToken(), command.getGameID());

            ChessGame.TeamColor joiningColor = command.getColor().toString().equalsIgnoreCase("white") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;

            boolean correctColor;
            if (joiningColor == ChessGame.TeamColor.WHITE) {
                correctColor = Objects.equals(game.whiteUsername(), auth.username());
            }
            else {
                correctColor = Objects.equals(game.blackUsername(), auth.username());
            }

            if (!correctColor) {
                Error error = new Error("Error: attempting to join with wrong color");
                sendError(session, error);
                return;
            }

            Notification notif = new Notification("%s has joined the game as %s".formatted(auth.username(), command.getColor().toString()));
            broadcastMessage(session, notif);

            LoadGame load = new LoadGame(game.game());
            sendMessage(session, load);
        } catch (RuntimeException e) {
            sendError(session, new Error("Error: Not authorized"));
        } catch (Exception e) {
            sendError(session, new Error("Error: Not a valid game"));
        }

    }

    private void handleObserverConnect(Session session, Connect command) throws Exception {
        try {
            AuthData auth = userService.getAuth(command.getAuthToken());
            GameData game = gameService.getGameData(command.getAuthToken(), command.getGameID());

            Notification notif = new Notification("%s has joined the game as an observer".formatted(auth.username()));
            broadcastMessage(session, notif);

            LoadGame load = new LoadGame(game.game());
            sendMessage(session, load);
        }
        catch (RuntimeException e) {
            sendError(session, new Error("Error: Not authorized"));

        } catch (Exception e) {
            sendError(session, new Error("Error: Not a valid game"));
        }
    }


    // Send the notification to all clients on the current game except the currSession
    public void broadcastMessage(Session currSession, ServerMessage message) throws IOException {
        broadcastMessage(currSession, message, false);
    }

    // Send the notification to all clients on the current game
    public void broadcastMessage(Session currSession, ServerMessage message, boolean toSelf) throws IOException {

        System.out.printf("Broadcasting (toSelf: %s): %s%n", toSelf, new Gson().toJson(message));

        for (Session session : Server.gameSessions.keySet()) {

            boolean inAGame = Server.gameSessions.get(session) != 0;
            boolean sameGame = Server.gameSessions.get(session).equals(Server.gameSessions.get(currSession));
            boolean isSelf = session == currSession;

            if ((toSelf || !isSelf) && inAGame && sameGame) {
                sendMessage(session, message);
            }
        }
    }

    public void sendMessage(Session session, ServerMessage message) throws IOException {
        session.getRemote().sendString(new Gson().toJson(message));
    }

    private void sendError(Session session, Error error) throws IOException {
        System.out.printf("Error: %s%n", new Gson().toJson(error));
        session.getRemote().sendString(new Gson().toJson(error));
    }

    private ChessGame.TeamColor getTeamColor(String username, GameData game) {
        if (username.equals(game.whiteUsername())) {
            return ChessGame.TeamColor.WHITE;
        }
        else if (username.equals(game.blackUsername())) {
            return ChessGame.TeamColor.BLACK;
        }
        else return null;
    }

}