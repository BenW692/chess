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
    private boolean _isWhiteTeamInCheck = false;
    private boolean _isBlackTeamInCheck = false;

    // todo: These flags will not with the unit tests I have to pass. Can I sweep through initial test board and find King and set this position?
    private ChessPosition _whiteTeamKingPos = new ChessPosition(1, 5);
    private ChessPosition _blackTeamKingPos = new ChessPosition(8, 5);

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
        for (var move : potentialMoves)
        {
        //make sure the move options don't put us in check
            // make a temp board where that move is completed. Is team in check with that move?
            ChessBoard temp_board = new ChessBoard(_gameBoard);
            makeTempMove(move, temp_board);
            boolean doesMoveEnableOpposingCheck = false;
            // for loop over pieceMoves for all opposing pieces. If one of those moves is the team's king, break
            for (int row = 1; row < 9; row ++){
                for (int col = 1; col < 9; col ++)
                {
                    ChessPosition posCheck = new ChessPosition(row, col);
                    ChessPiece pieceCheck = temp_board.getPiece(posCheck);
                    if (pieceCheck == null) {continue;}
                    // the piece is the same team, so it doesn't threaten check
                    else if (pieceCheck.getTeamColor() == _teamTurn) {continue;}
                    // piece is other team, so we need to check if it threatens check
                    else {
                        Collection<ChessMove> enemyMoves = pieceCheck.pieceMoves(temp_board, posCheck);
                        for (var enemyMove : enemyMoves) {
                            if (enemyMove.getEndPosition().equals(getCurrKingPos()))
                            {
                                doesMoveEnableOpposingCheck = true;
                                break;
                            }
                            // else we keep checking;
                        }
                    }
                    if (doesMoveEnableOpposingCheck) {break;}
                }
                if (doesMoveEnableOpposingCheck) {break;}
            }
            // the move never threatened check so it can be added
            if (!doesMoveEnableOpposingCheck) {
                validMoves.add(move);
            }
        }
        return validMoves;

        // are we in check?
            // get out of check (those are the only valid moves)
        // else
            //make sure the move options don't put us in check
                // make a temp board where that move is completed. Is team in check with that move?
                // for loop over pieceMoves for all opposing pieces. If one of those moves is the team's king, break
                    // yes, skip the move
                    // no, keep the valid move
                        // check if the move ends on the other teams king, so we can mark the other team is in check
    }


    public void makeTempMove(ChessMove move, ChessBoard tempBoard) {
        // I do not need to check if this is an impossible move, because this function only gets called with good moves passed in
        ChessPosition start_pos = move.getStartPosition();
        ChessPosition target_pos = move.getEndPosition();
        ChessPiece target_piece = tempBoard.getPiece(target_pos);
        ChessPiece movingPiece = tempBoard.getPiece(start_pos);
        tempBoard.addPiece(target_pos, movingPiece);
        tempBoard.addPiece(start_pos, null);
    }

    private ChessPosition getCurrKingPos() {
        if (_teamTurn == TeamColor.WHITE) {return _whiteTeamKingPos;}
        else {return _blackTeamKingPos;}
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

                // after we move, we need to see if the new piece threatens the other king with check or checkmate

                // VALID MOVES WILL NOT LET MOVES THAT WOULD PUT THE TEAM'S KING IN JEOPARDY THROUGH
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
        if (teamColor == TeamColor.WHITE) {return _isWhiteTeamInCheck;}
        else {return _isBlackTeamInCheck;}
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
    public void setBoard(ChessBoard board) {_gameBoard = board;}

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {return _gameBoard;}
}
