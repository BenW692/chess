package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PieceMoveOptions {
    private boolean wasCaptured = false;

    public Collection<ChessMove> kingPieceMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor col) {
        Collection<ChessMove> moves = new ArrayList<>();
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();
        ChessGame.TeamColor color = col;
        List<List<Integer>> moveMasks= List.of(
                List.of(0, 1),
                List.of(1, 0),
                List.of(0, -1),
                List.of(-1, 0),
                List.of(1, 1),
                List.of(1, -1),
                List.of(-1, -1),
                List.of(-1, 1)
        );
        return linearMoveMask(board, myPosition, moveMasks, startRow, startCol, color, moves);
    }

    private Collection<ChessMove> linearMoveMask(ChessBoard board, ChessPosition myPosition,
                                                 List<List<Integer>> moveMasks, int startRow, int startCol,
                                                 ChessGame.TeamColor color, Collection<ChessMove> moves) {
        for (var mask : moveMasks)
        {
            int row_adj = mask.getFirst();
            int col_adj = mask.getLast();
            int new_row = startRow + row_adj;
            int new_col = startCol + col_adj;
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
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();
        ChessGame.TeamColor color = col;
        List<List<Integer>> moveMasks= List.of(
                List.of(2, 1),
                List.of(2, -1),
                List.of(-2, 1),
                List.of(-2, -1),
                List.of(1, 2),
                List.of(-1, 2),
                List.of(1, -2),
                List.of(-1, -2)
        );
        return linearMoveMask(board, myPosition, moveMasks, startRow, startCol, color, moves);
    }

    public Collection<ChessMove> rookPieceMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor col) {
        Collection<ChessMove> moves = new ArrayList<>();
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();
        ChessGame.TeamColor color = col;
        List<List<Integer>> moveMasks= List.of(
                List.of(0, 1),
                List.of(0, -1),
                List.of(1, 0),
                List.of(-1, 0)
        );
        return multiplierMoveMask(board, myPosition, moveMasks, startRow, startCol, color, moves);
    }

    public Collection<ChessMove> bishopPieceMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor col) {
        Collection<ChessMove> moves = new ArrayList<>();
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();
        ChessGame.TeamColor color = col;
        List<List<Integer>> moveMasks= List.of(
                List.of(1, 1),
                List.of(1, -1),
                List.of(-1, 1),
                List.of(-1, -1)
        );
        return multiplierMoveMask(board, myPosition, moveMasks, startRow, startCol, color, moves);
    }

    private Collection<ChessMove> multiplierMoveMask(ChessBoard board, ChessPosition myPosition,
                                                     List<List<Integer>> moveMasks, int startRow, int startCol,
                                                     ChessGame.TeamColor color, Collection<ChessMove> moves) {
        for (var mask : moveMasks)
        {
            int row_adj = mask.getFirst();
            int col_adj = mask.getLast();
            for (int mult = 1; mult < 8; mult ++) {
                int new_row = startRow + row_adj * mult;
                int new_col = startCol + col_adj * mult;
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
        int sign_multiplier;
        if (col == ChessGame.TeamColor.BLACK)
        {
            sign_multiplier = -1;
        }
        else {sign_multiplier = 1;}
        Collection<ChessMove> moves = new ArrayList<>();
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();
        if ((startRow == 2 && col == ChessGame.TeamColor.WHITE) ||
                (col == ChessGame.TeamColor.BLACK && startRow == 7)) {
            int close_row = startRow + 1 * sign_multiplier;
            ChessPosition closePos = new ChessPosition(close_row, startCol);
            int new_row = startRow + 2 * sign_multiplier;
            ChessPosition newPos = new ChessPosition(new_row, startCol);
            if (board.getPiece(newPos) == null && board.getPiece(closePos) == null) {
                ChessMove newMove = new ChessMove(myPosition, newPos);
                moves.add(newMove);
            }
            moves.addAll(pawnValidMoves(board, myPosition, col));
        }
        //promotion
        else if ((startRow == 7 && col == ChessGame.TeamColor.WHITE) ||
                (col == ChessGame.TeamColor.BLACK && startRow == 2)) {
            Collection<ChessMove> normMoves = pawnValidMoves(board, myPosition, col);
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
            moves = pawnValidMoves(board, myPosition, col);
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

    public Collection<ChessMove> pawnValidMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color){
        int sign_multiplier;
        if (color == ChessGame.TeamColor.BLACK)
        {
            sign_multiplier = -1;
        }
        else {sign_multiplier = 1;}
        Collection<ChessPosition> attackPositions = new ArrayList<>();
        Collection<ChessMove> moves = new ArrayList<>();

        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();
        ChessPosition forward = new ChessPosition(startRow + sign_multiplier, startCol);
        ChessPosition left_attack = new ChessPosition(startRow + sign_multiplier, startCol - 1);
        ChessPosition right_attack = new ChessPosition(startRow + sign_multiplier, startCol + 1);

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
