package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PieceMoveOptions {
    private boolean wasCaptured = false;

    public Collection<ChessMove> kingPieceMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor col) {
        Collection<ChessMove> moves = new ArrayList<>();
        int start_row = myPosition.getRow();
        int start_col = myPosition.getColumn();
        ChessGame.TeamColor color = col;
        List<List<Integer>> move_masks= List.of(
                List.of(0, 1),
                List.of(1, 0),
                List.of(0, -1),
                List.of(-1, 0),
                List.of(1, 1),
                List.of(1, -1),
                List.of(-1, -1),
                List.of(-1, 1)
        );
        for (var mask : move_masks)
        {
            int row_adj = mask.getFirst();
            int col_adj = mask.getLast();
            int new_row = start_row + row_adj;
            int new_col = start_col + col_adj;
            ChessPosition newPos = new ChessPosition(new_row, new_col);
            if (isMoveValid(board, newPos, color)) {
                ChessMove newMove = new ChessMove(myPosition, newPos);
                moves.add(newMove);
            }
        }
        return moves;
    }

    public Collection<ChessMove> knightPieceMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor col) {
        Collection<ChessMove> moves = new ArrayList<>();
        int start_row = myPosition.getRow();
        int start_col = myPosition.getColumn();
        ChessGame.TeamColor color = col;
        List<List<Integer>> move_masks= List.of(
                List.of(2, 1),
                List.of(2, -1),
                List.of(-2, 1),
                List.of(-2, -1),
                List.of(1, 2),
                List.of(-1, 2),
                List.of(1, -2),
                List.of(-1, -2)
        );
        for (var mask : move_masks)
        {
            int row_adj = mask.getFirst();
            int col_adj = mask.getLast();
            int new_row = start_row + row_adj;
            int new_col = start_col + col_adj;
            ChessPosition newPos = new ChessPosition(new_row, new_col);
            if (isMoveValid(board, newPos, color)) {
                ChessMove newMove = new ChessMove(myPosition, newPos);
                moves.add(newMove);
            }
        }
        return moves;
    }

    public Collection<ChessMove> rookPieceMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor col) {
        Collection<ChessMove> moves = new ArrayList<>();
        int start_row = myPosition.getRow();
        int start_col = myPosition.getColumn();
        ChessGame.TeamColor color = col;
        List<List<Integer>> move_masks= List.of(
                List.of(0, 1),
                List.of(0, -1),
                List.of(1, 0),
                List.of(-1, 0)
        );
        for (var mask : move_masks)
        {
            int row_adj = mask.getFirst();
            int col_adj = mask.getLast();
            for (int mult = 1; mult < 8; mult ++) {
                int new_row = start_row + row_adj * mult;
                int new_col = start_col + col_adj * mult;
                ChessPosition newPos = new ChessPosition(new_row, new_col);
                if (isMoveValid(board, newPos, color)) {
                    ChessMove newMove = new ChessMove(myPosition, newPos);
                    moves.add(newMove);
                    if (wasCaptured) {
                        wasCaptured = false;
                        break;
                    }
                }
                else {break;}
            }
        }
        return moves;
    }

    public Collection<ChessMove> bishopPieceMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor col) {
        Collection<ChessMove> moves = new ArrayList<>();
        int start_row = myPosition.getRow();
        int start_col = myPosition.getColumn();
        ChessGame.TeamColor color = col;
        List<List<Integer>> move_masks= List.of(
                List.of(1, 1),
                List.of(1, -1),
                List.of(-1, 1),
                List.of(-1, -1)
        );
        for (var mask : move_masks)
        {
            int row_adj = mask.getFirst();
            int col_adj = mask.getLast();
            for (int mult = 1; mult < 8; mult ++) {
                int new_row = start_row + row_adj * mult;
                int new_col = start_col + col_adj * mult;
                ChessPosition newPos = new ChessPosition(new_row, new_col);
                if (isMoveValid(board, newPos, color)) {
                    ChessMove newMove = new ChessMove(myPosition, newPos);
                    moves.add(newMove);
                    if (wasCaptured) {
                        wasCaptured = false;
                        break;
                    }
                }
                else {break;}
            }
        }
        return moves;
    }

    public Collection<ChessMove> queenPieceMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor col) {
        Collection<ChessMove> rook_moves = rookPieceMoves(board, myPosition, col);
        Collection<ChessMove> bishop_moves = bishopPieceMoves(board, myPosition, col);
        bishop_moves.addAll(rook_moves);
        return bishop_moves;
    }

    public Collection<ChessMove> pawnPieceMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor col) {
        Collection<ChessMove> moves = new ArrayList<>();
        int start_row = myPosition.getRow();
        int start_col = myPosition.getColumn();
        ChessGame.TeamColor color = col;
        if (col == ChessGame.TeamColor.BLACK)
        {
            // first move
            if (start_row == 7) {
                int close_row = start_row - 1;
                ChessPosition closePos = new ChessPosition(close_row, start_col);
                int new_row = start_row - 2;
                ChessPosition newPos = new ChessPosition(new_row, start_col);
                if (board.getPiece(newPos) == null && board.getPiece(closePos) == null) {
                    ChessMove newMove = new ChessMove(myPosition, newPos);
                    moves.add(newMove);
                }
                moves.addAll(blackPawnValidMoves(board, myPosition, color));
            }
            //promotion
            else if (start_row == 2) {
                Collection<ChessMove> normMoves = blackPawnValidMoves(board, myPosition, color);
                for (var move : normMoves)
                {
                    ChessPosition endPosition = move.getEndPosition();
                    moves.add(new ChessMove(myPosition, endPosition, ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, endPosition, ChessPiece.PieceType.BISHOP));
                    moves.add(new ChessMove(myPosition, endPosition, ChessPiece.PieceType.KNIGHT));
                    moves.add(new ChessMove(myPosition, endPosition, ChessPiece.PieceType.QUEEN));
                }
            }
            //normal
            else {
                moves = blackPawnValidMoves(board, myPosition, color);
            }
        }
        else {
            // first move
            if (start_row == 2) {
                int close_row = start_row + 1;
                ChessPosition closePos = new ChessPosition(close_row, start_col);
                int new_row = start_row + 2;
                ChessPosition newPos = new ChessPosition(new_row, start_col);
                if (board.getPiece(newPos) == null && board.getPiece(closePos) == null) {
                    ChessMove newMove = new ChessMove(myPosition, newPos);
                    moves.add(newMove);
                }
                moves.addAll(whitePawnValidMoves(board, myPosition, color));
            }
            //promotion
            else if (start_row == 7) {
                Collection<ChessMove> normMoves = whitePawnValidMoves(board, myPosition, color);
                for (var move : normMoves)
                {
                    ChessPosition endPosition = move.getEndPosition();
                    moves.add(new ChessMove(myPosition, endPosition, ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, endPosition, ChessPiece.PieceType.BISHOP));
                    moves.add(new ChessMove(myPosition, endPosition, ChessPiece.PieceType.KNIGHT));
                    moves.add(new ChessMove(myPosition, endPosition, ChessPiece.PieceType.QUEEN));
                }
            }
            //normal
            else {
                moves = whitePawnValidMoves(board, myPosition, color);
            }
        }
        return moves;
    }

    public boolean isMoveInBounds(ChessPosition newPos)
    {
        int row = newPos.getRow();;
        int col = newPos.getColumn();
        if (row > 8 || row < 1) {return false;}
        if (col > 8 || col < 1) {return false;}
        return true;
    }

    public boolean isMoveValid(ChessBoard board, ChessPosition newPos, ChessGame.TeamColor color){
        // in bounds?
        if (!isMoveInBounds(newPos)) {return false;}

        ChessPiece newPiece = board.getPiece(newPos);
        // empty spot?
        if (newPiece == null) {return true;}
        // enemy spot?
        else if (newPiece.getTeamColor() != color) {
            wasCaptured = true;
            return true;}
        else {return false;}
    }

    public Collection<ChessMove> blackPawnValidMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color){
        Collection<ChessPosition> attackPositions = new ArrayList<>();
        Collection<ChessMove> moves = new ArrayList<>();

        int start_row = myPosition.getRow();
        int start_col = myPosition.getColumn();
        ChessPosition forward = new ChessPosition(start_row - 1, start_col);
        ChessPosition left_attack = new ChessPosition(start_row - 1, start_col - 1);
        ChessPosition right_attack = new ChessPosition(start_row - 1, start_col + 1);

        attackPositions.add(left_attack);
        attackPositions.add(right_attack);
        // check forward
        if (isMoveInBounds(forward) && board.getPiece(forward) == null) {moves.add(new ChessMove(myPosition, forward));}
        // check attacks
        for (var attack : attackPositions)
        {
            if (!isMoveInBounds(attack)) {continue;}
            ChessPiece newPiece = board.getPiece(attack);
            // enemy spot?
            if (newPiece != null && newPiece.getTeamColor() != color) {
                moves.add(new ChessMove(myPosition, attack));
            }
        }
        return moves;
    }

    public Collection<ChessMove> whitePawnValidMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color){
        Collection<ChessPosition> attackPositions = new ArrayList<>();
        Collection<ChessMove> moves = new ArrayList<>();

        int start_row = myPosition.getRow();
        int start_col = myPosition.getColumn();
        ChessPosition forward = new ChessPosition(start_row + 1, start_col);
        ChessPosition left_attack = new ChessPosition(start_row + 1, start_col - 1);
        ChessPosition right_attack = new ChessPosition(start_row + 1, start_col + 1);

        attackPositions.add(left_attack);
        attackPositions.add(right_attack);
        // check forward
        if (isMoveInBounds(forward) && board.getPiece(forward) == null) {moves.add(new ChessMove(myPosition, forward));}
        // check attacks
        for (var attack : attackPositions)
        {
            if (!isMoveInBounds(attack)) {continue;}
            ChessPiece newPiece = board.getPiece(attack);
            // enemy spot?
            if (newPiece != null && newPiece.getTeamColor() != color) {
                moves.add(new ChessMove(myPosition, attack));
            }
        }
        return moves;
    }
}
