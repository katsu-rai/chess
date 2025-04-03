package client;

import chess.ChessGame;
import com.google.gson.Gson;

import websocket.messages.*;
import websocket.messages.Error;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static ui.EscapeSequences.ERASE_LINE;

public class WebSocketCommunicator extends Endpoint {

    private Session session;
    private static final Gson GSON = new Gson();

    public WebSocketCommunicator(String serverDomain) throws Exception {
        try {
            if (serverDomain.startsWith("https://")) {
                serverDomain = serverDomain.replaceFirst("https://", "wss://");
            } else if (serverDomain.startsWith("http://")) {
                serverDomain = serverDomain.replaceFirst("http://", "ws://");
            } else if (serverDomain.startsWith("localhost:8080//")) {
                serverDomain = serverDomain.replaceFirst("localhost:8080//", "ws://");
            } else if (!serverDomain.startsWith("ws://") && !serverDomain.startsWith("wss://")) {
                serverDomain = "ws://" + serverDomain;
            }

            URI uri = new URI(serverDomain + "/ws");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, uri);
            this.session.addMessageHandler((MessageHandler.Whole<String>) this::handleMessage);
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new WebSocketConnectionException("Failed to connect to WebSocket: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        System.out.println("WebSocket connection opened.");
    }

    private void handleMessage(String message) {
        ServerMessage.ServerMessageType messageType = extractMessageType(message);
        if (messageType == null) {
            System.err.println("Received unknown message: " + message);
            return;
        }

        switch (messageType) {
            case NOTIFICATION -> printNotification(GSON.fromJson(message, Notification.class).getNotifiMessage());
            case ERROR -> printNotification(GSON.fromJson(message, Error.class).getErrorMessage());
            case LOAD_GAME -> printLoadedGame(GSON.fromJson(message, LoadGame.class).game());
            default -> System.err.println("Unhandled message type: " + messageType);
        }
    }

    private ServerMessage.ServerMessageType extractMessageType(String message) {
        if (message.contains("\"serverMessageType\":\"NOTIFICATION\"")) {
            return ServerMessage.ServerMessageType.NOTIFICATION;

        } else if (message.contains("\"serverMessageType\":\"ERROR\"")) {
            return ServerMessage.ServerMessageType.ERROR;

        } else if (message.contains("\"serverMessageType\":\"LOAD_GAME\"")) {
            return ServerMessage.ServerMessageType.LOAD_GAME;
        }

        return null;
    }

    private void printNotification(String message) {
        System.out.print(ERASE_LINE + '\r');
        System.out.printf("\n%s\n[IN-GAME] >>> ", message);
    }

    private void printLoadedGame(ChessGame game) {
        System.out.print(ERASE_LINE + "\r\n");
//        BoardPrinter.updateGame(game);
//        BoardPrinter.printBoard(GamePlayUI.);
        System.out.print("[IN-GAME] >>> ");
    }

    public void sendMessage(String message) {
        if (session == null || !session.isOpen()) {
            System.err.println("WebSocket session is closed. Cannot send message.");
            return;
        }
        session.getAsyncRemote().sendText(message);
    }
}

