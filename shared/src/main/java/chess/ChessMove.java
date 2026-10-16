package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    private ChessPosition startPos;
    private ChessPosition endPos;
    private ChessPiece.PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition) {
        startPos = startPosition;
        endPos = endPosition;
        promotionPiece = null;
    }

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        startPos = startPosition;
        endPos = endPosition;
        this.promotionPiece = promotionPiece;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessMove chessMove = (ChessMove) o;
        return Objects.equals(startPos, chessMove.startPos) && Objects.equals(endPos, chessMove.endPos) && promotionPiece == chessMove.promotionPiece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPos, endPos, promotionPiece);
    }

    @Override
    public String toString() {
        String output_str = "(";
        if (promotionPiece == null) {
            output_str += startPos +"->" + endPos;
        }
        else {
            output_str += startPos +"->" + endPos +
                    " P:" + ChessPiece.PIECE_TYPE_CHARACTER_MAP.get(promotionPiece);
        }
        output_str += ")";
        return output_str;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {return startPos;}

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {return endPos;}

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {return promotionPiece;}

}
