package server;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.*;
import model.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.*;
import service.*;
import websocket.messages.*;
import websocket.messages.Error;


import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private static GameService gameService;
    private static UserService userService;

    public static void initializer(UserService userServiceParam, GameService gameServiceParam) {
        userService = userServiceParam;
        gameService = gameServiceParam;
    }


    public WebSocketHandler() {
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

        if (message.contains("CONNECT")) {
            Connect command = new Gson().fromJson(message, Connect.class);
            Server.gameSessions.replace(session, command.getGameID());
            handlePlayerConnect(session, command);
        }
        else if (message.contains("MAKE_MOVE")) {
            MakeMove command = new Gson().fromJson(message, MakeMove.class);
            handleMakeMove(session, command);
        }
        else if (message.contains("LEAVE")) {
            Leave command = new Gson().fromJson(message, Leave.class);
            handleLeave(session, command);
        }
        else if (message.contains("RESIGN")) {
            Resign command = new Gson().fromJson(message, Resign.class);
            handleResign(session, command);
        }
    }

    private void handlePlayerConnect(Session session, Connect command) throws Exception {
        AuthData auth = userService.getAuth(command.getAuthToken());
        if (auth == null) {
            // Send a structured JSON error message instead of just a string
            sendError(session, new Error("Invalid AuthToken"));
            return;
        }

        GameData game = gameService.getGameData(command.getAuthToken(), command.getGameID());
        if (game == null) {
            // Send a structured JSON error message instead of just a string
            sendError(session, new Error("Invalid Game ID"));
            return;
        }

        // Notify players of the connection
        Notification notif = new Notification("%s has joined the game as %s".formatted(auth.username(), command.getColor() != null ? command.getColor().toString() : "Observer"));
        broadcastMessage(session, notif);

        // Load the game and send it to the client
        LoadGame load = new LoadGame(game.game());
        sendMessage(session, load);
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


    private void handleMakeMove(Session session, MakeMove command) throws Exception {
        try {
            AuthData auth = userService.getAuth(command.getAuthToken());
            GameData game = gameService.getGameData(command.getAuthToken(), command.getGameID());
            ChessGame.TeamColor userColor = getTeamColor(auth.username(), game);
            if (userColor == null) {
                sendError(session, new Error("Error: You are observing this game"));
                return;
            }

            if (game.game().getGameOver()) {
                sendError(session, new Error("Error: can not make a move, game is over"));
                return;
            }

            if (game.game().getTeamTurn().equals(userColor)) {
                game.game().makeMove(command.getMove());

                Notification notif;
                ChessGame.TeamColor opponentColor = userColor == ChessGame.TeamColor.WHITE ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;

                if (game.game().isInCheckmate(opponentColor)) {
                    notif = new Notification("Checkmate! %s wins!".formatted(auth.username()));
                    game.game().setGameOver(true);
                }
                else if (game.game().isInStalemate(opponentColor)) {
                    notif = new Notification("Stalemate caused by %s's move! It's a tie!".formatted(auth.username()));
                    game.game().setGameOver(true);
                }
                else if (game.game().isInCheck(opponentColor)) {
                    notif = new Notification("A move has been made by %s, %s is now in check!".formatted(auth.username(), opponentColor.toString()));
                }
                else {
                    notif = new Notification("A move has been made by %s".formatted(auth.username()));
                }
                broadcastMessage(session, notif);

                gameService.updateGame(auth.authToken(), game);

                LoadGame load = new LoadGame(game.game());
                broadcastMessage(session, load, true);
            }
            else {
                sendError(session, new Error("Error: it is not your turn"));
            }
        }
        catch (UnauthorizedException e) {
            sendError(session, new Error("Error: Not authorized"));
        } catch (InvalidMoveException e) {
            System.out.println("****** error: " + e.getMessage() + " " + command.getMove().toString());
            sendError(session, new Error("Error: invalid move (you might need to specify a promotion piece)"));
        } catch (Exception e) {
            sendError(session, new Error("Error: invalid game"));
        }
    }

    private void handleLeave(Session session, Leave command) throws IOException {
        try {
            AuthData auth = userService.getAuth(command.getAuthToken());

            Notification notif = new Notification("%s has left the game".formatted(auth.username()));
            broadcastMessage(session, notif);

            session.close();
        } catch (RuntimeException e) {
            sendError(session, new Error("Error: Not authorized"));
        }
    }

    private void handleResign(Session session, Resign command) throws Exception {
        try {
            AuthData auth = userService.getAuth(command.getAuthToken());
            GameData game = gameService.getGameData(command.getAuthToken(), command.getGameID());
            ChessGame.TeamColor userColor = getTeamColor(auth.username(), game);

            String opponentUsername = userColor == ChessGame.TeamColor.WHITE ? game.blackUsername() : game.whiteUsername();

            if (userColor == null) {
                sendError(session, new Error("Error: You are observing this game"));
                return;
            }

            if (game.game().getGameOver()) {
                sendError(session, new Error("Error: The game is already over!"));
                return;
            }

            game.game().setGameOver(true);
            gameService.updateGame(auth.authToken(), game);
            Notification notif = new Notification("%s has forfeited, %s wins!".formatted(auth.username(), opponentUsername));
            broadcastMessage(session, notif, true);
        } catch (UnauthorizedException e) {
            sendError(session, new Error("Unauthorized"));
        } catch (BadRequestException e) {
            sendError(session, new Error("BadRequest"));
        }
    }

    public void sendMessage(Session session, ServerMessage message) throws IOException {
        session.getRemote().sendString(new Gson().toJson(message));
    }

    private void sendError(Session session, Error error) throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(error);
        session.getRemote().sendString(json);
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

class InvalidMoveException extends Exception {

    public InvalidMoveException() {}

    public InvalidMoveException(String message) {
        super(message);
    }
}