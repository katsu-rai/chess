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
        boardPrinter = new BoardPrinter(game);
    }

    public void run() {
        boolean inGame = true;
        boardPrinter.printBoard(color, null);

        while (inGame) {
            printHelpMenu();
            String[] input = getUserInput();

            switch (input[0]) {
                case "help":
                    continue;

                case "redraw":
                    redraw();
                    continue;

                case "leave":
                    server.leave(gameID);
                    return;

                case "move":
                    handleMakeMove(input);
                    continue;

                case "resign":
                    handleResign();
                    break;

                case "highlight":
                    handleHighlight(input);
                    continue;

                default: {
                    out.println("Command not recognized, please try again");
                }
            }
        }
    }

    private String[] getUserInput() {
        out.print("\n[IN-GAME] >>> ");
        return new Scanner(System.in).nextLine().split(" ");
    }

    private void printHelpMenu() {
        out.println(SET_TEXT_COLOR_YELLOW + "\nAvailable Commands:" + RESET_TEXT_COLOR);
        out.println(SET_TEXT_COLOR_BLUE + "redraw" + RESET_TEXT_COLOR + " - Redraw the game board");
        out.println(SET_TEXT_COLOR_BLUE + "move <FROM> <TO> <PROMOTION PIECE>" + RESET_TEXT_COLOR +
                " - Make a move (<PROMOTION PIECE> only for pawn promotion)");
        out.println(SET_TEXT_COLOR_BLUE + "highlight <COORDINATE>" + RESET_TEXT_COLOR + " - Highlight all legal moves for the given piece");
        out.println(SET_TEXT_COLOR_RED + "leave" + RESET_TEXT_COLOR + " - Leave the current game");
        out.println(SET_TEXT_COLOR_RED + "resign" + RESET_TEXT_COLOR + " - Forfeit this game");
        out.println(SET_TEXT_COLOR_GREEN + "help" + RESET_TEXT_COLOR + " - Show this menu");
    }

    private void redraw() {
        GameData updatedGameData = server.getGameData(gameID);
        ChessGame updatedGame = updatedGameData.game();

        boardPrinter = new BoardPrinter(updatedGame);

        boardPrinter.printBoard(color, null);
    }

    private void handleResign() {
        out.println("Are you sure you want to give up? (yes/no)");
        if (getUserInput()[0].equalsIgnoreCase("yes")) {
            server.resign(gameID);
        } else {
            out.println("Resignation cancelled");
        }
    }

    private void handleHighlight(String[] input) {
        if (input.length == 2 && input[1].matches("[a-h][1-8]")) {
            ChessPosition position = new ChessPosition(input[1].charAt(1) - '0', input[1].charAt(0) - ('a' - 1));
            boardPrinter.printBoard(color, position);
        } else {
            out.println("Please provide a valid coordinate (e.g., 'a2')");
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

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            GameData updatedGameData = server.getGameData(gameID);
            ChessGame updatedGame = updatedGameData.game();

            // Update the board printer with the updated game state
            boardPrinter = new BoardPrinter(updatedGame);

            boardPrinter.printBoard(color, null);
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
