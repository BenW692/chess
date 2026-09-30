package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor _teamTurn = TeamColor.WHITE;
    private ChessBoard _gameBoard;

    public ChessGame() {
        _gameBoard = new ChessBoard();
        _gameBoard.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return _teamTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        _teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        Collection<ChessMove> potentialMoves = new ArrayList<>();
        ChessPiece piece = _gameBoard.getPiece(startPosition);
        if (piece == null) {return null;}
        else {
            potentialMoves = piece.pieceMoves(_gameBoard, startPosition);
        }
        // todo: Do I want validMoves to enforce checking if a move puts player in checkmate, or let makeMove do that?
        validMoves.addAll(potentialMoves);
        return validMoves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        // if the move would place king in check it needs to throw invalidmoveexception
        ChessPosition start_pos = move.getStartPosition();
        ChessPosition target_pos = move.getEndPosition();
        ChessPiece target_piece = _gameBoard.getPiece(target_pos);
        ChessPiece movingPiece = _gameBoard.getPiece(start_pos);

        // todo: do I need to keep track of what pieces got captured?
        if (movingPiece == null) {
            throw new InvalidMoveException("There is no piece at the supplied starting position.");
        }
        else if (movingPiece.getTeamColor() != _teamTurn) {
            throw new InvalidMoveException("That piece cannot be moved because it is the other team's turn");
        }

        Collection<ChessMove> vMoves = validMoves(start_pos);
        for (var vMove : vMoves) {
            if (move.equals(vMove)) {
                _gameBoard.addPiece(target_pos, movingPiece);
                _gameBoard.addPiece(start_pos, null);
                if (_teamTurn == TeamColor.WHITE) {setTeamTurn(TeamColor.BLACK);}
                else {setTeamTurn(TeamColor.WHITE);}
                return;
            }
        }
        throw new InvalidMoveException("The supplied move is not a valid move");

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        _gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return _gameBoard;
    }
}
