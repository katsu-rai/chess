package chess.pieces;
import chess.*;
import java.util.HashSet;

public class Knight implements PieceMoveCalculator {

    public static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition currPosition) {
        int[][] possibleMoves = {{-2, 1}, {-1, 2}, {1, 2}, {2, 1}, {2, -1}, {1, -2}, {-1, -2}, {-2, -1}};
        return PieceMoveCalculator.StaticMoves(currPosition, possibleMoves, board);
    }

}