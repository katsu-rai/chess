package ui;

import chess.*;
import client.ServerFacade;
import model.GameData;
import java.util.Scanner;
import static java.lang.System.out;
import static ui.EscapeSequences.*;

public class GamePlayUI {

    private final ServerFacade server;
    private final int gameID;
    private static ChessGame.TeamColor color;
    private static BoardPrinter boardPrinter;

    public GamePlayUI(ServerFacade server, GameData gameData, ChessGame.TeamColor color) {
        this.server = server;
        ChessGame game = gameData.game();
        this.gameID = gameData.gameID();
        GamePlayUI.color = color;
        boardPrinter = new BoardPrinter(game.getBoard());
    }

    public void run() {
        boolean inGame = true;
        out.print(RESET_TEXT_COLOR + RESET_BG_COLOR);

        while (inGame) {
            String[] input = getUserInput();
            switch (input[0]) {
                case "help" -> printHelpMenu();
                case "redraw" -> redraw();
                case "leave" -> {
                    inGame = false;
                    server.leave(gameID);
                }
                case "move" -> handleMakeMove(input);
                case "resign" -> handleResign();
                case "highlight" -> handleHighlight(input);
                default -> {
                    out.println("Command not recognized, please try again");
                    printHelpMenu();
                }
            }
        }

        new PostLoginUI(server).run();
    }

    private String[] getUserInput() {
        out.printf("\n[IN-GAME] >>> ");
        return new Scanner(System.in).nextLine().split(" ");
    }

    private void printHelpMenu() {
        out.println("redraw - redraw the game board");
        out.println("leave - leave the current game");
        out.println("move <from> <to> <promotion_piece> - make a move (only for pawn promotion)");
        out.println("resign - forfeit this game");
        out.println("highlight <coordinate> - highlight all legal moves for the given piece");
        out.println("help - show this menu");
    }

    private void redraw() {
        boardPrinter.printBoard(color);
    }

    private void handleResign() {
        out.println("Are you sure you want to forfeit? (yes/no)");
        if (getUserInput()[0].equalsIgnoreCase("yes")) {
            server.resign(gameID);
        } else {
            out.println("Resignation cancelled");
        }
    }

    private void handleHighlight(String[] input) {
        if (input.length == 2 && input[1].matches("[a-h][1-8]")) {
//            ChessPosition position = new ChessPosition(input[1].charAt(1) - '0', input[1].charAt(0) - ('a' - 1));
            boardPrinter.printBoard(color);
        } else {
            out.println("Please provide a valid coordinate (e.g., 'c3')");
        }
    }

    private void handleMakeMove(String[] input) {
        if (input.length >= 3 && input[1].matches("[a-h][1-8]") && input[2].matches("[a-h][1-8]")) {
            ChessPosition from = new ChessPosition(input[1].charAt(1) - '0', input[1].charAt(0) - ('a' - 1));
            ChessPosition to = new ChessPosition(input[2].charAt(1) - '0', input[2].charAt(0) - ('a' - 1));
            ChessPiece.PieceType promotion = input.length == 4 ? getPieceType(input[3]) : null;

            if (input.length == 4 && promotion == null) {
                out.println("Invalid promotion piece (options: queen, knight, bishop, rook)");
                return;
            }
            server.makeMove(gameID, new ChessMove(from, to, promotion));
        } else {
            out.println("Invalid move format. Use: move <from> <to> <promotion_piece>");
        }
    }

    private ChessPiece.PieceType getPieceType(String name) {
        return switch (name.toUpperCase()) {
            case "QUEEN" -> ChessPiece.PieceType.QUEEN;
            case "BISHOP" -> ChessPiece.PieceType.BISHOP;
            case "KNIGHT" -> ChessPiece.PieceType.KNIGHT;
            case "ROOK" -> ChessPiece.PieceType.ROOK;
            case "PAWN" -> ChessPiece.PieceType.PAWN;
            default -> null;
        };
    }
}
