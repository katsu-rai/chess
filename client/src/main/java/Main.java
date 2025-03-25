import chess.*;
import ui.PreLoginUI;
import client.ServerFacade;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
//        System.out.println("♕ 240 Chess Client: " + piece);

//        PreLoginUI prelogin = new PreLoginUI();
//        prelogin.run();


        ServerFacade server = new ServerFacade("http://localhost:8080");

        System.out.println(server.register("rai", "password", "email"));
        System.out.println(server.login("rai", "password"));
        System.out.println(server.createGame("TestGame"));
        System.out.println(server.listGames());
        System.out.println(server.joinGame(1, "WHITE"));
        System.out.println(server.listGames());
    }
}