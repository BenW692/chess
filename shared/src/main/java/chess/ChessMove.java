package chess;

import java.util.List;
import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    private ChessPosition _start_pos;
    private ChessPosition _end_pos;
    private ChessPiece.PieceType _promotion_piece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition) {
        _start_pos = startPosition;
        _end_pos = endPosition;
        _promotion_piece = null;
    }

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        _start_pos = startPosition;
        _end_pos = endPosition;
        _promotion_piece = promotionPiece;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessMove chessMove = (ChessMove) o;
        return Objects.equals(_start_pos, chessMove._start_pos) && Objects.equals(_end_pos, chessMove._end_pos) && _promotion_piece == chessMove._promotion_piece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(_start_pos, _end_pos, _promotion_piece);
    }

    @Override
    public String toString() {
        return "ChessMove{" +
                "_start_pos=" + _start_pos +
                ", _end_pos=" + _end_pos +
                ", _promotion_piece=" + ChessPiece.PIECE_TYPE_CHARACTER_MAP.get(_promotion_piece) +
                '}';
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {return _start_pos;}

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {return _end_pos;}

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {return _promotion_piece;}

}
