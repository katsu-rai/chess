package websocket.messages;

import chess.ChessGame;

public class LoadGame extends ServerMessage {

    ChessGame game;

    public LoadGame(ChessGame game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }
}
