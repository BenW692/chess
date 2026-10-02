package chess;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece [][] _board;

    public ChessBoard(ChessBoard other) {
        this._board = new ChessPiece[other._board.length][];
        for (int row = 0; row < other._board.length; row ++)
        {
            this._board[row] = Arrays.copyOf(other._board[row], other._board[row].length);
        }
    }

    public ChessBoard() {
        _board = new ChessPiece[8][8];
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(_board, that._board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(_board);
    }

    @Override
    public String toString() {
        String output = "";
        for (int row = 7; row >= 0; row --)
        {
            output += "|";
            for (var item : _board[row])
            {
                if (item == null)
                {
                    output += "  ";
                }
                else {
                    output += item.toString();
                }
                output += "|";
            }
            output += "\n";
        }
        return "ChessBoard{\n" + output +
                '}';
    }
    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        int row = position.getRow();
        int col = position.getColumn();
        _board[row-1][col-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
        return _board[row-1][col-1];
    }

    public ChessPiece[][] getBoardArray()
    {
        return _board;
    }

    public void movePiece(ChessMove move) {
        ChessPosition start_pos = move.getStartPosition();
        ChessPosition end_pos = move.getEndPosition();
        ChessPiece movingPiece = getPiece(start_pos);
        removePiece(start_pos);
        addPiece(end_pos, movingPiece);
    }

    public void removePiece(ChessPosition pos)
    {
        // todo: I cannot get original piece to be deleted
        int row = pos.getRow();
        int col = pos.getColumn();
        this._board[row-1][col-1] = null;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        String board_text = """
        |r|n|b|q|k|b|n|r|
        |p|p|p|p|p|p|p|p|
        | | | | | | | | |
        | | | | | | | | |
        | | | | | | | | |
        | | | | | | | | |
        |P|P|P|P|P|P|P|P|
        |R|N|B|Q|K|B|N|R|
        """;
        int row = 1;
        int col = 1;
        ChessGame.TeamColor color;
        for (var c : board_text.toCharArray())
        {
            switch (c) {
                case '\n' -> {
                    row ++;
                    col = 1;
                }
                case '|' -> {}
                case ' ' -> {
                    _board[row-1][col-1] = null;
                    col ++;
                }
                default -> {
                    if (Character.isLowerCase(c)) {color = ChessGame.TeamColor.WHITE;}
                    else {color = ChessGame.TeamColor.BLACK;}
                    ChessPiece.PieceType type = ChessPiece.CHARACTER_PIECE_TYPE_MAP.get(Character.toLowerCase(c));
                    ChessPosition position = new ChessPosition(row, col);
                    ChessPiece piece = new ChessPiece(color, type);
                    addPiece(position, piece);
                    col ++;
                }

            }
        }
    }

    public boolean isOutofBounds(ChessPosition pos)
    {
        int row = pos.getRow();
        int col = pos.getColumn();
        if (row > 8 || row < 1) {return true;}
        if (col > 8 || col < 1) {return true;}
        // else both the row and col are in the bounds of the board
        return false;
    }
}
