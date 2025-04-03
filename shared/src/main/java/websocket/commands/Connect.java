package websocket.commands;

import chess.ChessGame;

public class Connect extends UserGameCommand {

    ChessGame.TeamColor playerColor;

    public Connect(String authToken, int gameID) {
        super(CommandType.CONNECT, authToken, gameID);
    }

    public ChessGame.TeamColor getColor() {
        return playerColor;
    }


}