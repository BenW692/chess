package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Rook implements ChessPiece{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        /*
        * Options:
        * loop negative and positive from start position and abort when we bump into something
        * check every spot on row and column I am on
        * */
        List<ChessMove> moves = new ArrayList<>();
        int start_row = myPosition.getRow();
        int start_col = myPosition.getColumn();
        // check horizontal options in positive direction
        for (int new_col = start_col + 1; new_col < 7; new_col ++) {
            ChessPosition new_pos = new ChessPosition(start_row, new_col);
            if (check_pos(board, myPosition, new_pos, moves)) {
                break;
            }
        }
        // check horizontal options in negative direction
        for (int new_col = start_col - 1; new_col > 0; new_col --) {
            ChessPosition new_pos = new ChessPosition(start_row, new_col);
            if (check_pos(board, myPosition, new_pos, moves)) {
                break;
            }
        }
        //check vertical options in positive direction
        for (int new_row = start_row + 1; new_row < 7; new_row ++) {
            ChessPosition new_pos = new ChessPosition(new_row, start_col);
            if (check_pos(board, myPosition, new_pos, moves)) {
                break;
            }
        }
        //check vertical options in negative direction
        for (int new_row = start_row - 1; new_row > 0; new_row --) {
            ChessPosition new_pos = new ChessPosition(new_row, start_col);
            if (check_pos(board, myPosition, new_pos, moves)) {
                break;
            }
        }
        return moves;
    }

    private boolean check_pos(ChessBoard board, ChessPosition myPosition, ChessPosition new_pos, List<ChessMove> moves) {
        if (board.getPiece(new_pos) == null)
        {
            ChessMove move = new ChessMove(myPosition, new_pos);
            moves.add(move);
        }
        else if (board.getPiece(new_pos).getTeamColor() != _color) {
            ChessMove move = new ChessMove(myPosition, new_pos);
            moves.add(move);
            return true;
        }
        else {
            return true;
        }
        return false;
    }

}
