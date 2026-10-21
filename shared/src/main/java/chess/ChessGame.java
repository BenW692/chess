package chess;

import java.util.*;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor currTeamTurn = TeamColor.WHITE;
    private ChessBoard gameBoard;

    public ChessGame() {
        gameBoard = new ChessBoard();
        gameBoard.resetBoard();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return currTeamTurn == chessGame.currTeamTurn && Objects.equals(gameBoard, chessGame.gameBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currTeamTurn, gameBoard);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currTeamTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currTeamTurn = team;
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
        ChessPiece piece = gameBoard.getPiece(startPosition);
        if (piece == null) {return null;}
        else {
            potentialMoves = piece.pieceMoves(gameBoard, startPosition);
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
            for (var move : potentialMoves) {
                // the move never threatened check so it can be added
                if (doesMoveGetOutOfCheck(move)) {
                    validMoves.add(move);
                }
            }
        }
        return validMoves;
    }

    public Map<TeamColor, ChessPosition> updateKingPos(ChessBoard board) {
        Map<TeamColor, ChessPosition> kingPos = new HashMap<>();
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition posCheck = new ChessPosition(row, col);
                ChessPiece pieceCheck = board.getPiece(posCheck);
                if (pieceCheck == null) {continue;}
                if (pieceCheck.getPieceType() == ChessPiece.PieceType.KING) {
                    if (pieceCheck.getTeamColor() == TeamColor.WHITE) {
                        kingPos.put(TeamColor.WHITE, posCheck);
                    } else {
                        kingPos.put(TeamColor.BLACK, posCheck);
                    }
                    if (kingPos.size() == 2)
                    {
                        return kingPos;
                    }
                }
            }
        }
        return kingPos;
    }


    public boolean doesMoveCauseOwnCheck(ChessMove move)
    {
        //make sure the move won't put us in check
        TeamColor teamColor = gameBoard.getPiece(move.getStartPosition()).getTeamColor();
        // make a temp board where that move is completed. Is team in check with that move?
        ChessBoard tempBoard = new ChessBoard(gameBoard);
        makeTempMove(move, tempBoard);
        Map<TeamColor, ChessPosition> kingPos = updateKingPos(tempBoard);
        // for loop over pieceMoves for all opposing pieces. If one of those moves is the team's king, break
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition posCheck = new ChessPosition(row, col);
                ChessPiece pieceCheck = tempBoard.getPiece(posCheck);
                if (pieceCheck == null) {continue;}
                // the piece is the same team, so it doesn't threaten check
                else if (pieceCheck.getTeamColor() == teamColor) {continue;}
                // piece is other team, so we need to check if it threatens check
//                else {
                Collection<ChessMove> enemyMoves = pieceCheck.pieceMoves(tempBoard, posCheck);
                for (var enemyMove : enemyMoves) {
                    if (enemyMove.getEndPosition().equals(kingPos.get(teamColor))) {
                        return true;
                        }
                    // else we keep checking;
                    }
//                    }
                }
            }
            return false;
        }


    public boolean doesMoveGetOutOfCheck(ChessMove move) {
        TeamColor teamColor = gameBoard.getPiece(move.getStartPosition()).getTeamColor();
        // make a temp board where that move is completed
        ChessBoard tempBoard = new ChessBoard(gameBoard);
        makeTempMove(move, tempBoard);
        Map<TeamColor, ChessPosition> kingPos = updateKingPos(tempBoard);
        for (int row = 1; row < 9; row ++) {
            for (int col = 1; col < 9; col++) {
                // check every piece on the board
                ChessPosition posCheck = new ChessPosition(row, col);
                ChessPiece pieceCheck = tempBoard.getPiece(posCheck);
                if (pieceCheck == null) {continue;}
                // the piece is the supplied team's color
                else if (pieceCheck.getTeamColor() == teamColor) {continue;}
                // the piece is the other team
//                else {
                Collection<ChessMove> enemyMoves = pieceCheck.pieceMoves(tempBoard, posCheck);
                for (var newMove : enemyMoves) {
                    // can the other team's piece attack our king?
                    if (newMove.getEndPosition().equals(kingPos.get(teamColor))) {
                        return false;
                    }
//                    }
                }
            }
        }
        return true;
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
        ChessPiece target_piece = gameBoard.getPiece(target_pos);
        ChessPiece movingPiece = gameBoard.getPiece(start_pos);
        if (movingPiece == null) {
            throw new InvalidMoveException("There is no piece at the supplied starting position.");
        }
        else if (movingPiece.getTeamColor() != currTeamTurn) {
            throw new InvalidMoveException("That piece cannot be moved because it is the other team's turn");
        }
        Collection<ChessMove> vMoves = validMoves(start_pos);
        for (var vMove : vMoves) {
            if (move.equals(vMove)) {
                gameBoard.movePiece(move);
                if (move.getPromotionPiece() != null)
                {
                    movingPiece.promotePiece(move.getPromotionPiece());
                }
                // update team turns
                if (currTeamTurn == TeamColor.WHITE) {setTeamTurn(TeamColor.BLACK);}
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
        Map<TeamColor, ChessPosition> kingPos = updateKingPos(gameBoard);
        // for loop over pieceMoves for all pieces. If one of those moves is the other team's king, break
        for (int row = 1; row < 9; row ++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition posCheck = new ChessPosition(row, col);
                ChessPiece pieceCheck = gameBoard.getPiece(posCheck);
                if (pieceCheck == null) {continue;}
                // the piece is the supplied team's color
                else if (pieceCheck.getTeamColor() == teamColor) {continue;}
                // the piece is the other team
                Collection<ChessMove> enemyMoves = pieceCheck.pieceMoves(gameBoard, posCheck);
                for (var newMove : enemyMoves) {
                    // can this piece attack our king?
                    if (newMove.getEndPosition().equals(kingPos.get(teamColor))) {
                        return true;
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
        if (!isInCheck(teamColor)) {return false;}
        else {
            return canPlayerMove(teamColor);
        }
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {return false;}
        else {
            return canPlayerMove(teamColor);
        }
    }

    private boolean canPlayerMove(TeamColor teamColor) {
        Collection<ChessMove> totalValidMoves = new ArrayList<>();
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition posCheck = new ChessPosition(row, col);
                ChessPiece pieceCheck = gameBoard.getPiece(posCheck);
                if (pieceCheck == null) {continue;}
                // the piece is the other team, so it doesn't help us get out of check
                else if (pieceCheck.getTeamColor() != teamColor) {continue;}
                // piece is our team, so we need to check if can stop opponent check
                totalValidMoves.addAll(validMoves(posCheck));
                if (!totalValidMoves.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard = board;}

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {return gameBoard;}
}
