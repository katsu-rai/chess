package websocket.messages;

public class WebSocketError extends ServerMessage {

    private final String errorMessage;

    public WebSocketError(String errorMessage) {
        super(ServerMessageType.ERROR);
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
