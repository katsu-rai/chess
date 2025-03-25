package ui;

import chess.*;

import static java.lang.System.out;
import static ui.EscapeSequences.*;

public class BoardPrinter {
    private final ChessBoard board;

    public BoardPrinter(ChessBoard board) {
        this.board = board;
    }

    public void printBoard(ChessGame.TeamColor perspective) {
        StringBuilder output = new StringBuilder(SET_TEXT_BOLD);
        boolean reversed = (perspective == ChessGame.TeamColor.BLACK);

        output.append(startingRow(reversed));

        for (int i = 8; i > 0; i--) {
            output.append(boardRow(reversed ? (9 - i) : i, reversed));
        }

        output.append(startingRow(reversed));
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
        return ((row + column) % 2 == 0) ? SET_BG_COLOR_BLACK : SET_BG_COLOR_WHITE;
    }


    private String piece(int row, int column) {
        ChessPiece piece = board.getPiece(new ChessPosition(row, column));
        if (piece == null) { return EMPTY; }

        String pieceColor = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? SET_TEXT_COLOR_BLUE : SET_TEXT_COLOR_RED;

        return pieceColor + switch (piece.getPieceType()) {
            case QUEEN -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_QUEEN : BLACK_QUEEN;
            case KING -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KING : BLACK_KING;
            case BISHOP -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_BISHOP : BLACK_BISHOP;
            case KNIGHT -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KNIGHT : BLACK_KNIGHT;
            case ROOK -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_ROOK : BLACK_ROOK;
            case PAWN -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_PAWN : BLACK_PAWN;
        } + RESET_TEXT_COLOR;
    }
}
