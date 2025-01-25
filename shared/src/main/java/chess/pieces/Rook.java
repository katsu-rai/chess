package chess.pieces;
import chess.*;
import java.util.HashSet;

public class Rook implements PieceMoveCalculator {

    public static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition currPosition) {
        int currCol = currPosition.getColumn();
        int currRow = currPosition.getRow();
        int[][] moveDirections = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

        ChessGame.TeamColor team = board.getTeam(currPosition);

        return PieceMoveCalculator.DirectionalSteps(board, currPosition, moveDirections, currRow, currCol, team);
    }

}