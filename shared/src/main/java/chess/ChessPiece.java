package chess;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    ChessGame.TeamColor _color;
    ChessPiece.PieceType _type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        _color = pieceColor;
        _type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return _color == that._color && _type == that._type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(_color, _type);
    }

    @Override
    public String toString() {
        String output;
        // prepend char with color
        if (_color == ChessGame.TeamColor.WHITE) {output = "W";}
        else {output = "B";}
        // put char for type after color
        output += PIECE_TYPE_CHARACTER_MAP.get(_type);
        return output;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    public static final Map<Character, PieceType> CHARACTER_PIECE_TYPE_MAP = Map.of(
            'r', ChessPiece.PieceType.ROOK,
            'n', ChessPiece.PieceType.KNIGHT,
            'b', ChessPiece.PieceType.BISHOP,
            'q', ChessPiece.PieceType.QUEEN,
            'k', ChessPiece.PieceType.KING,
            'p', ChessPiece.PieceType.PAWN);

    public static final Map<PieceType, Character> PIECE_TYPE_CHARACTER_MAP = Map.of(
            ChessPiece.PieceType.ROOK, 'r',
            ChessPiece.PieceType.KNIGHT, 'n',
            ChessPiece.PieceType.BISHOP, 'b',
            ChessPiece.PieceType.QUEEN, 'q',
            ChessPiece.PieceType.KING, 'k',
            ChessPiece.PieceType.PAWN, 'p');
    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {return _color;}

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {return _type;}

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {}


}
