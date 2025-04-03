package ui;

import chess.*;

import java.util.Collection;
import java.util.HashSet;

import static java.lang.System.out;
import static ui.EscapeSequences.*;

public class BoardPrinter {
    private final ChessBoard board;
    private final ChessGame game;

    public BoardPrinter(ChessGame game) {
        this.game = game;
        this.board = game.getBoard();
    }

    public void printBoard(ChessGame.TeamColor perspective, ChessPosition selectedPos) {
        StringBuilder output = new StringBuilder(SET_TEXT_BOLD);
        boolean reversed = (perspective == ChessGame.TeamColor.BLACK);

        // Get possible end positions for the selected piece (if any)
        HashSet<ChessPosition> possibleEndPositions = getPossibleEndPositions(selectedPos);

        output.append(startingRow(reversed));
        for (int i = 8; i > 0; i--) {
            output.append(boardRow(reversed ? (9 - i) : i, reversed, selectedPos, possibleEndPositions));
        }
        output.append(startingRow(reversed)).append(RESET_TEXT_BOLD_FAINT);
        out.println(output);
    }

    private HashSet<ChessPosition> getPossibleEndPositions(ChessPosition selectedPos) {
        HashSet<ChessPosition> possibleEndPositions = new HashSet<>();
        if (selectedPos != null) {
            Collection<ChessMove> moves = game.validMoves(selectedPos);
            for (ChessMove move : moves) {
                possibleEndPositions.add(move.getEndPosition());
            }
        }
        return possibleEndPositions;
    }

    private String startingRow(boolean reversed) {
        String rowLabel = reversed ? "h  g  f  e  d  c  b  a" : "a  b  c  d  e  f  g  h";
        return SET_BG_COLOR_BLACK + SET_TEXT_COLOR_BLUE + "    " + rowLabel + "    " + RESET_BG_COLOR + RESET_TEXT_COLOR + "\n";
    }

    private String boardRow(int row, boolean reversed, ChessPosition selectedPos, HashSet<ChessPosition> possibleSquares) {
        StringBuilder output = new StringBuilder(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_BLUE + " %d ".formatted(row));

        for (int i = 1; i <= 8; i++) {
            int column = reversed ? (9 - i) : i;
            ChessPosition pos = new ChessPosition(row, column);

            // Get highlight color for the square before appending the square
            String highlight = getSquareHighlight(selectedPos, possibleSquares, pos);

            if (highlight == ""){
                output.append(squareColor(row, column)).append(piece(row, column));
            } else {
                output.append(highlight).append(piece(row, column));  // Apply the highlight color
            }

            output.append(RESET_BG_COLOR); // Reset background color after each square
        }

        output.append(SET_BG_COLOR_BLACK).append(SET_TEXT_COLOR_BLUE)
                .append(" %d ".formatted(row)).append(RESET_BG_COLOR).append(RESET_TEXT_COLOR).append("\n");

        return output.toString();
    }

    private String getSquareHighlight(ChessPosition selectedPos, HashSet<ChessPosition> possibleSquares, ChessPosition pos) {
        // Check if the square is the selected one by comparing row and column directly
        if (selectedPos != null && selectedPos.getRow() == pos.getRow() && selectedPos.getColumn() == pos.getColumn()) {
            return SET_BG_COLOR_YELLOW; // Highlight selected position
        }

        // Check if the square is in the list of possible squares by comparing row and column
        for (ChessPosition possiblePos : possibleSquares) {
            if (possiblePos.getRow() == pos.getRow() && possiblePos.getColumn() == pos.getColumn()) {
                return SET_BG_COLOR_GREEN; // Highlight possible move square
            }
        }

        return ""; // No highlight
    }

    private String squareColor(int row, int column) {
        return ((row + column) % 2 == 0) ? SET_BG_COLOR_BLACK : SET_BG_COLOR_WHITE;
    }

    private String piece(int row, int column) {
        ChessPiece piece = board.getPiece(new ChessPosition(row, column));
        if (piece == null) {
            return EMPTY; // Empty square
        }

        String pieceColor = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? SET_TEXT_COLOR_BLUE : SET_TEXT_COLOR_RED;

        return pieceColor + getPieceSymbol(piece) + RESET_TEXT_COLOR;
    }

    private String getPieceSymbol(ChessPiece piece) {
        switch (piece.getPieceType()) {
            case QUEEN:
                return (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_QUEEN : BLACK_QUEEN;
            case KING:
                return (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_KING : BLACK_KING;
            case BISHOP:
                return (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_BISHOP : BLACK_BISHOP;
            case KNIGHT:
                return (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_KNIGHT : BLACK_KNIGHT;
            case ROOK:
                return (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_ROOK : BLACK_ROOK;
            case PAWN:
                return (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_PAWN : BLACK_PAWN;
            default:
                return "";
        }
    }
}
