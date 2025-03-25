package ui;

import chess.*;

import static java.lang.System.out;
import static ui.EscapeSequences.*;

public class BoardPrinter {
    private final ChessBoard board;

    public BoardPrinter(ChessBoard board) {
        this.board = board;
    }

    public void printBoard() {
        StringBuilder output = new StringBuilder(SET_TEXT_BOLD);
        boolean reversed = true;

        for (int j = 0; j < 2; j++) {
            output.append(startingRow(reversed));

            for (int i = 8; i > 0; i--) {
                output.append(boardRow(reversed ? (9 - i) : i, reversed));
            }

            output.append(startingRow(reversed));
            if (j < 1) {output.append("\n");}
            reversed = false;
        }

        output.append(RESET_TEXT_BOLD_FAINT);
        out.println(output);
    }

    private String startingRow(boolean reversed) {
        return SET_BG_COLOR_BLACK + SET_TEXT_COLOR_BLUE +
                "    " + (reversed ? "h  g  f  e  d  c  b  a" : "a  b  c  d  e  f  g  h") + "    " +
                RESET_BG_COLOR + RESET_TEXT_COLOR + "\n";
    }

    private String boardRow(int row, boolean reversed) {
        StringBuilder output = new StringBuilder(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_BLUE + " %d ".formatted(row));

        for (int i = 1; i <= 8; i++) {
            int column = reversed ? (9 - i) : i;
            output.append(squareColor(row, column)).append(piece(row, column));
        }

        output.append(SET_BG_COLOR_BLACK).append(SET_TEXT_COLOR_BLUE)
                .append(" %d ".formatted(row)).append(RESET_BG_COLOR).append(RESET_TEXT_COLOR).append("\n");

        return output.toString();
    }

    private String squareColor(int row, int column) {
        return ((row + column) % 2 == 0) ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_RED;
    }

    private String piece(int row, int column) {
        ChessPiece piece = board.getPiece(new ChessPosition(row, column));
        if (piece == null) {return "   ";}

        String color = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? SET_TEXT_COLOR_WHITE : SET_TEXT_COLOR_BLACK;
        String symbol = switch (piece.getPieceType()) {
            case QUEEN -> " Q ";
            case KING -> " K ";
            case BISHOP -> " B ";
            case KNIGHT -> " N ";
            case ROOK -> " R ";
            case PAWN -> " P ";
        };

        return color + symbol;
    }
}