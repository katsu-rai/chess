package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private ChessGame.TeamColor pieceColor;
    private PieceType pieceType;


    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.pieceType = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        String result = "";
        switch (pieceType) {
            case KING:
                result = pieceColor == ChessGame.TeamColor.WHITE ? "K" : "k";
                break;
            case QUEEN:
                result = pieceColor == ChessGame.TeamColor.WHITE ? "Q" : "q";
                break;
            case BISHOP:
                result = pieceColor == ChessGame.TeamColor.WHITE ? "B" : "b";
                break;
            case KNIGHT:
                result = pieceColor == ChessGame.TeamColor.WHITE ? "N" : "n";
                break;
            case ROOK:
                result = pieceColor == ChessGame.TeamColor.WHITE ? "R" : "r";
                break;
            case PAWN:
                result = pieceColor == ChessGame.TeamColor.WHITE ? "P" : "p";
                break;
            default:
                throw new IllegalArgumentException("Unexpected value: " + pieceType);
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || !(obj instanceof ChessPiece)) {
            return false;
        }

        ChessPiece otherPiece = (ChessPiece) obj;
        return this.pieceColor == otherPiece.pieceColor && this.pieceType == otherPiece.pieceType;
    }


    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, pieceType);
    }
}
