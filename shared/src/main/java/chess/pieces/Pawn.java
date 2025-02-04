package chess.pieces;
import chess.*;
import java.util.HashSet;
public class Pawn implements PieceMoveCalculator {

    public static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition currPosition) {
        HashSet<ChessMove> moves = HashSet.newHashSet(16);
        int currCol = currPosition.getColumn();
        int currRow = currPosition.getRow();
        ChessPiece.PieceType[] promotionPieces = new ChessPiece.PieceType[]{null};

        ChessGame.TeamColor team = board.getTeam(currPosition);
        int moveIncrement = team == ChessGame.TeamColor.WHITE ? 1 : -1;

        boolean promote = (team == ChessGame.TeamColor.WHITE && currRow == 7) || (team == ChessGame.TeamColor.BLACK && currRow == 2);
        if (promote) {
            promotionPieces = new ChessPiece.PieceType[]{ChessPiece.PieceType.ROOK, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.QUEEN};
        }

        for (ChessPiece.PieceType promotionPiece : promotionPieces) {

            ChessPosition secondFrontStep = new ChessPosition(currRow + moveIncrement, currCol);
            if (PieceMoveCalculator.IsValid(secondFrontStep)
                    && board.getPiece(secondFrontStep) == null) {
                moves.add(new ChessMove(currPosition, secondFrontStep, promotionPiece));
            }

            ChessPosition leftAttack = new ChessPosition(currRow + moveIncrement, currCol-1);
            if (PieceMoveCalculator.IsValid(leftAttack)
                    && board.getPiece(leftAttack) != null
                    && board.getTeam(leftAttack) != team) {
                moves.add(new ChessMove(currPosition, leftAttack, promotionPiece));
            }
            ChessPosition rightAttack = new ChessPosition(currRow + moveIncrement, currCol+1);

            if (PieceMoveCalculator.IsValid(rightAttack)
                    && board.getPiece(rightAttack) != null
                    && board.getTeam(rightAttack) != team) {
                moves.add(new ChessMove(currPosition, rightAttack, promotionPiece));
            }

            ChessPosition doubleForwardPosition = new ChessPosition(currRow + moveIncrement*2, currCol);
            if (PieceMoveCalculator.IsValid(doubleForwardPosition)
                    && ((team == ChessGame.TeamColor.WHITE && currRow == 2)
                    || (team == ChessGame.TeamColor.BLACK && currRow == 7))
                    && board.getPiece(doubleForwardPosition) == null
                    && board.getPiece(secondFrontStep) == null) {
                moves.add(new ChessMove(currPosition, doubleForwardPosition, promotionPiece));
            }

        }

        return moves;
    }

}