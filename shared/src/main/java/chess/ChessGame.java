package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    ChessGame.TeamColor teamTurn;
    ChessBoard board;
    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }


    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if(piece == null){
            return Collections.emptyList();
        } else {
            Collection<ChessMove> actualMoves = new ArrayList<ChessMove>();
            Collection<ChessMove> possibleMoves = board.getPiece(startPosition).pieceMoves(board, startPosition);
            for (ChessMove move : possibleMoves) {
                ChessPiece temp = board.getPiece(move.getEndPosition());
                ChessGame.TeamColor teamColor = piece.getTeamColor();

                board.addPiece(startPosition, null);
                board.addPiece(move.getEndPosition(), piece);

                if(!isInCheck(teamColor)){ actualMoves.add(move); }

                board.addPiece(startPosition, piece);
                board.addPiece(move.getEndPosition(), temp);
            }

            return actualMoves;
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> safeMoves = validMoves(move.getStartPosition());
        ChessPosition startPosition = move.getStartPosition();
        if(safeMoves.isEmpty()){
            throw new InvalidMoveException();
        }

        ChessPiece movingPiece = board.getPiece(startPosition);
        if(board.getTeam(startPosition) == teamTurn
                && safeMoves.contains(move)){

            if(move.getPromotionPiece() != null){
                movingPiece = new ChessPiece(teamTurn, move.getPromotionPiece());
            }

            board.addPiece(move.getStartPosition(), null);
            board.addPiece(move.getEndPosition(), movingPiece);
            setTeamTurn(teamTurn == TeamColor.BLACK ? TeamColor.WHITE : TeamColor.BLACK);
        } else {
            throw new InvalidMoveException();
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = null;
        outloop:
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                if (piece != null
                        && piece.getPieceType() == ChessPiece.PieceType.KING
                        && piece.getTeamColor() == teamColor) {
                    kingPosition = new ChessPosition(row, col);
                    break outloop;
                }
            }
        }

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                if(board.getPiece(new ChessPosition(row, col)) != null
                        && board.getPiece(new ChessPosition(row, col)).getTeamColor() == teamColor) {
                    continue;
                } else {
                    ChessPiece enemyPiece = board.getPiece(new ChessPosition(row, col));

                    if(enemyPiece == null){
                        continue;
                    }

                    Collection<ChessMove> enemyMoves = enemyPiece.pieceMoves(board, new ChessPosition(row, col));

                    for(ChessMove enemyMove: enemyMoves){
                        if(enemyMove.getEndPosition().equals(kingPosition)){
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        ChessPosition kingPosition = null;
        outloop:
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                if (piece != null
                        && piece.getPieceType() == ChessPiece.PieceType.KING
                        && piece.getTeamColor() == teamColor) {
                    kingPosition = new ChessPosition(row, col);
                    break outloop;
                }
            }
        }
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
