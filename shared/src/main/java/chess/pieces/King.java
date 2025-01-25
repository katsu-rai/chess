package chess.pieces;
import chess.*;
import java.util.HashSet;

public class King implements PieceMoveCalculator {
    public static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition currPosition) {
        int[][] possibleMoves = {{-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}, {-1, 0}};
        return PieceMoveCalculator.StaticMoves(currPosition, possibleMoves, board);

    }

}