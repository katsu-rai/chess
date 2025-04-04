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
    boolean gameOver;
    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        teamTurn = ChessGame.TeamColor.WHITE;
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
            throw new InvalidMoveException("Valid Move " + move + " is empty");
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
            throw new InvalidMoveException("Invalid Move");
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
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                if (piece == null || piece.getTeamColor() == teamColor) {
                    continue;
                }

                if (isKingInCheckByEnemy(piece, position, kingPosition)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isKingInCheckByEnemy(ChessPiece enemyPiece, ChessPosition position, ChessPosition kingPosition) {
        Collection<ChessMove> enemyMoves = enemyPiece.pieceMoves(board, position);

        for (ChessMove enemyMove : enemyMoves) {
            if (enemyMove.getEndPosition().equals(kingPosition)) {
                return true;
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
        if(!isInCheck(teamColor)){
            return false;
        }

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition currentPosition = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(currentPosition);

                if(piece == null
                        || piece.getTeamColor() != teamColor){
                    continue;
                }


                Collection<ChessMove> safeMoves = validMoves(currentPosition);

                for (ChessMove move : safeMoves) {
                    ChessPiece movingPiece = board.getPiece(currentPosition);
                    ChessPiece temp = board.getPiece(move.getEndPosition());

                    board.addPiece(currentPosition, null);
                    board.addPiece(move.getEndPosition(), movingPiece);
                    if(!isInCheck(teamColor)){
                        return false;
                    }

                    board.addPiece(currentPosition, movingPiece);
                    board.addPiece(move.getEndPosition(), temp);
                }
            }
        }

        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(isInCheck(teamColor)){
            return false;
        }

        ArrayList<ChessMove> allValidMoves = new ArrayList<>();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition currentPosition = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(currentPosition);

                if(piece == null
                        || piece.getTeamColor() != teamColor){
                    continue;
                }


                Collection<ChessMove> safeMoves = validMoves(currentPosition);
                allValidMoves.addAll(safeMoves);
            }
        }

        if(!allValidMoves.isEmpty()){
            return false;
        }

        return true;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public boolean getGameOver() {
        return gameOver;
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
