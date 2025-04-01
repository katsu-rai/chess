package websocket.messages;

public class Notification extends ServerMessage{

    String notifiMessage;

    public Notification(String notifiMessage) {
        super(ServerMessageType.NOTIFICATION);
        this.notifiMessage = notifiMessage;
    }
}
