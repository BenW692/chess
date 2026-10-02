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
    private TeamColor _currTeamTurn = TeamColor.WHITE;
    private TeamColor _notCurrTeam = TeamColor.BLACK;
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
        return _currTeamTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        _currTeamTurn = team;
        if (team == TeamColor.BLACK) {_notCurrTeam = TeamColor.WHITE;}
        else {_notCurrTeam = TeamColor.BLACK;}
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
        if (!isInCheck(piece.getTeamColor())) {
            for (var move : potentialMoves) {
                // the move never threatened check so it can be added
                if (!doesMoveCauseOwnCheck(move)) {
                    validMoves.add(move);
                }
            }
        }
        else {
            // we have to get out of check
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


    public boolean doesMoveCauseOwnCheck(ChessMove move)
    {
        //make sure the move won't put us in check
        TeamColor teamColor = _gameBoard.getPiece(move.getStartPosition()).getTeamColor();
        // make a temp board where that move is completed. Is team in check with that move?
        ChessBoard temp_board = new ChessBoard(_gameBoard);
        makeTempMove(move, temp_board);
        // for loop over pieceMoves for all opposing pieces. If one of those moves is the team's king, break
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition posCheck = new ChessPosition(row, col);
                ChessPiece pieceCheck = temp_board.getPiece(posCheck);
                if (pieceCheck == null) {continue;}
                // the piece is the same team, so it doesn't threaten check
                else if (pieceCheck.getTeamColor() == teamColor) {
                    continue;
                }
                // piece is other team, so we need to check if it threatens check
                else {
                    Collection<ChessMove> enemyMoves = pieceCheck.pieceMoves(temp_board, posCheck);
                    for (var enemyMove : enemyMoves) {
                        if (enemyMove.getEndPosition().equals(getCurrKingPos())) {
                            return true;
                            }
                        // else we keep checking;
                        }
                    }
                }
            }
            return false;
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
        if (_currTeamTurn == TeamColor.WHITE) {return _whiteTeamKingPos;}
        else {return _blackTeamKingPos;}
    }

    private ChessPosition getOppKingPos() {
        if (_currTeamTurn == TeamColor.WHITE) {return _blackTeamKingPos;}
        else {return _whiteTeamKingPos;}
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
        else if (movingPiece.getTeamColor() != _currTeamTurn) {
            throw new InvalidMoveException("That piece cannot be moved because it is the other team's turn");
        }
        Collection<ChessMove> vMoves = validMoves(start_pos);
        for (var vMove : vMoves) {
            if (move.equals(vMove)) {
                _gameBoard.movePiece(move);
                // update team turns
                if (_currTeamTurn == TeamColor.WHITE) {setTeamTurn(TeamColor.BLACK);}
                else {setTeamTurn(TeamColor.WHITE);}
                return;
            }
        }
        throw new InvalidMoveException("The supplied move is not a valid move");
    }

    private void setOppTeamToCheck()
    {
        if (_currTeamTurn == TeamColor.WHITE) {_isBlackTeamInCheck = true;}
        else {_isWhiteTeamInCheck = true;}
    }
    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        // for loop over pieceMoves for all pieces. If one of those moves is the other team's king, break
        for (int row = 1; row < 9; row ++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition posCheck = new ChessPosition(row, col);
                ChessPiece pieceCheck = _gameBoard.getPiece(posCheck);
                if (pieceCheck == null) {
                    continue;
                }
                // the piece is the supplied team's color
                else if (pieceCheck.getTeamColor() == teamColor) {
                    continue;
                } else {
                    Collection<ChessMove> enemyMoves = pieceCheck.pieceMoves(_gameBoard, posCheck);
                    for (var newMove : enemyMoves) {
                        ChessPosition kingPos;
                        if (teamColor == TeamColor.WHITE) {kingPos = _whiteTeamKingPos;}
                        else {kingPos = _blackTeamKingPos;}
                        if (newMove.getEndPosition() == kingPos) {
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
