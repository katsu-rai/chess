package chess.pieces;


import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public interface PieceMoveCalculator {

    static boolean isValid(ChessPosition position) {
        int row = position.getRow();
        int column = position.getColumn();
        return row >= 1 && row <= 8 && column >= 1 && column <= 8;
    }

    static HashSet<ChessMove> StaticMoves(ChessPosition startPosition, int[][] offsetMoves, ChessBoard chessBoard) {
        HashSet<ChessMove> maxMoves = new HashSet<>(8);

        int startCol = startPosition.getColumn();
        int startRow = startPosition.getRow();

        ChessGame.TeamColor playerTeam = chessBoard.getTeam(startPosition);

        for (int[] offset : offsetMoves) {
            int targetRow = startRow + offset[1];
            int targetCol = startCol + offset[0];

            ChessPosition targetPosition = new ChessPosition(targetRow, targetCol);

            if (isValid(targetPosition) && chessBoard.getTeam(targetPosition) != playerTeam) {
                maxMoves.add(new ChessMove(startPosition, targetPosition, null));
            }
        }

        return maxMoves;
    }

    static HashSet<ChessMove> DirectionalSteps(ChessBoard chessBoard, ChessPosition startPosition, int[][] directions, int startRow, int startCol, ChessGame.TeamColor playerTeam) {
        HashSet<ChessMove> directionalMoves = new HashSet<>(27); // Allocate for larger sets of moves

        for (int[] direction : directions) {
            boolean stopSearch = false;
            int step = 1;

            while (!stopSearch) {
                int targetRow = startRow + direction[1] * step;
                int targetCol = startCol + direction[0] * step;

                ChessPosition targetPosition = new ChessPosition(targetRow, targetCol);

                if (!isValid(targetPosition)) {
                    stopSearch = true;
                } else if (chessBoard.getPiece(targetPosition) == null) {
                    directionalMoves.add(new ChessMove(startPosition, targetPosition, null));
                } else if (chessBoard.getTeam(targetPosition) != playerTeam) {
                    directionalMoves.add(new ChessMove(startPosition, targetPosition, null));
                    stopSearch = true;
                } else {
                    stopSearch = true;
                }

                step++;
            }
        }

        return directionalMoves;
    }
}
