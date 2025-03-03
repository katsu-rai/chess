package chess.pieces;
import chess.*;
import java.util.HashSet;

public class Bishop implements PieceMoveCalculator {

    public static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition currPosition) {
        int currCol = currPosition.getColumn();
        int currRow = currPosition.getRow();
        int[][] moveDirections = {{-1, 1}, {1, 1}, {1, -1}, {-1, -1}};
        ChessGame.TeamColor team = board.getTeam(currPosition);

        return PieceMoveCalculator.dynamicMoves(board, currPosition, moveDirections, currRow, currCol, team);
    }

}