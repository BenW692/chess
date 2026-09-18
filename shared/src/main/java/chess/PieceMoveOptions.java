package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class PieceMoveOptions{
    public Collection<ChessMove> rookPieceMoves(ChessBoard board, ChessPosition myPosition) {
        /*
        * Options:
        * loop negative and positive from start position and abort when we bump into something
        * check every spot on row and column I am on
        * */
        List<ChessMove> moves = new ArrayList<>();
        int start_row = myPosition.getRow();
        int start_col = myPosition.getColumn();
        // check horizontal options in positive direction
        for (int new_col = start_col + 1; new_col < 9; new_col ++) {
            ChessPosition new_pos = new ChessPosition(start_row, new_col);
            if (!moveValid(board, myPosition, new_pos, moves)) {
                break;
            }
        }
        // check horizontal options in negative direction
        for (int new_col = start_col - 1; new_col > 0; new_col --) {
            ChessPosition new_pos = new ChessPosition(start_row, new_col);
            if (!moveValid(board, myPosition, new_pos, moves)) {
                break;
            }
        }
        //check vertical options in positive direction
        for (int new_row = start_row + 1; new_row < 9; new_row ++) {
            ChessPosition new_pos = new ChessPosition(new_row, start_col);
            if (!moveValid(board, myPosition, new_pos, moves)) {
                break;
            }
        }
        //check vertical options in negative direction
        for (int new_row = start_row - 1; new_row > 0; new_row --) {
            ChessPosition new_pos = new ChessPosition(new_row, start_col);
            if (!moveValid(board, myPosition, new_pos, moves)) {
                break;
            }
        }
        return moves;
    }

    public Collection<ChessMove> knightPieceMoves(ChessBoard board, ChessPosition myPosition) {
        /*
         * Options:
         * list out vector options and loop through all of them
         * go cardinal directions and try two options at each cardinal direction
         */
        List<ChessMove> moves = new ArrayList<>();
        int start_row = myPosition.getRow();
        int start_col = myPosition.getColumn();
        // the option mask is (row, column)
        List<List<Integer>> option_mask = List.of(
                //east
                List.of(1, 2),
                List.of(-1, 2),
                //west
                List.of(1, -2),
                List.of(-1, -2),
                //south
                List.of(-2, 1),
                List.of(-2, -1),
                //north
                List.of(2, 1),
                List.of(2, -1)
        );
        for (var mask : option_mask) {
            int row_adj = mask.getFirst();
            int col_adj = mask.getLast();
            ChessPosition new_pos = new ChessPosition(start_row + row_adj, start_col + col_adj);
            moveValid(board, myPosition, new_pos, moves);
        }
        return moves;
    }

    public Collection<ChessMove> bishopPieceMoves(ChessBoard board, ChessPosition myPosition) {
        /*
         * Options:
         * use a multiplier on masks for NW, NE, SW, SE
         * hard code for loops
         * combine the two
         * */
        List<ChessMove> moves = new ArrayList<>();
        int start_row = myPosition.getRow();
        int start_col = myPosition.getColumn();
        List<List<Integer>> option_mask = List.of(
                //northeast
                List.of(1, 1),
                //northwest
                List.of(1, -1),
                //southeast
                List.of(-1, 1),
                //southwest
                List.of(-1, -1)
        );
        for (var mask : option_mask) {
            int row_adj = mask.getFirst();
            int col_adj = mask.getLast();
            for (int mult = 1; mult < 8; mult++) {
                // todo: I need to add the adjustment to the original position I cant just multiply
                int new_row = start_row + row_adj * mult;
                int new_col = start_col + col_adj * mult;
                ChessPosition new_pos = new ChessPosition(new_row, new_col);
                if (!moveValid(board, myPosition, new_pos, moves)) {
                    break;
                }
            }
        }
        return moves;
    }

    public Collection<ChessMove> queenPieceMoves(ChessBoard board, ChessPosition myPosition) {
        /*
         * Options:
         * call rook and bishop back to back and combine move lists
         * */
        Collection<ChessMove> rook_moves = rookPieceMoves(board, myPosition);
        Collection<ChessMove> bishop_moves = bishopPieceMoves(board, myPosition);
        // I won't make an additional collection to save compute
        bishop_moves.addAll(rook_moves);
        return bishop_moves;
    }

    public Collection<ChessMove> kingPieceMoves(ChessBoard board, ChessPosition myPosition) {
        /*
         * Options:
         * use a multiplier on masks for NW, NE, SW, SE
         * hard code for loops
         * combine the two
         * */
        List<ChessMove> moves = new ArrayList<>();
        int start_row = myPosition.getRow();
        int start_col = myPosition.getColumn();
        List<List<Integer>> option_mask = List.of(
                //northeast
                List.of(1, 1),
                //northwest
                List.of(1, -1),
                //southeast
                List.of(-1, 1),
                //southwest
                List.of(-1, -1),
                //north
                List.of(1, 0),
                //west
                List.of(0, -1),
                //south
                List.of(-1, 0),
                //east
                List.of(0, 1)
        );
        for (var move : option_mask)
        {
            int row_adj = move.getFirst();
            int col_adj = move.getLast();
            int new_row = start_row + row_adj;
            int new_col = start_col + col_adj;
            ChessPosition new_pos = new ChessPosition(new_row, new_col);
            moveValid(board, myPosition, new_pos, moves);
        }
        return moves;
    }

    public Collection<ChessMove> pawnPieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece myPiece) {
        /*
         * Options:
         *
         * */
        List<ChessMove> moves = new ArrayList<>();
        int start_row = myPosition.getRow();
        int start_col = myPosition.getColumn();
        /* WHITE */
        //initial move check
        if (myPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            if (myPosition.getRow() == 2) {
                ChessPosition close_pos = new ChessPosition(start_row + 1, start_col);
                if (checkCellEmpty(board, close_pos)) {
                    ChessMove close_move = new ChessMove(myPosition, close_pos);
                    moves.add(close_move);
                    ChessPosition far_pos = new ChessPosition(start_row + 2, start_col);
                    if (checkCellEmpty(board, far_pos)) {
                        ChessMove far_move = new ChessMove(myPosition, far_pos);
                        moves.add(far_move);
                    }
                }

            }
            // promotion
            else if (myPosition.getRow() == 7) {
                ChessPosition prom_pos = new ChessPosition(start_row + 1, start_col);
                if (checkCellEmpty(board, prom_pos)) {
                    for (var type : ChessPiece.PieceType.values()) {
                        if (type == ChessPiece.PieceType.PAWN || type == ChessPiece.PieceType.KING) {continue;}
                        ChessMove prom_move = new ChessMove(myPosition, prom_pos, type);
                        moves.add(prom_move);
                    }
                }
            }
            // normal move to empty space
            else {
                ChessPosition norm_pos = new ChessPosition(start_row + 1, start_col);
                if (checkCellEmpty(board, norm_pos)) {
                    ChessMove close_move = new ChessMove(myPosition, norm_pos);
                    moves.add(close_move);
                }
            }
            List<ChessMove> attack_moves = whiteDiagonalAttack(board, myPosition);
            moves.addAll(attack_moves);
        }
        /* BLACK */
        //initial move check
        else if (myPiece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            if (myPosition.getRow() == 7) {
                ChessPosition close_pos = new ChessPosition(start_row - 1, start_col);
                if (checkCellEmpty(board, close_pos)) {
                    ChessMove close_move = new ChessMove(myPosition, close_pos);
                    moves.add(close_move);
                    ChessPosition far_pos = new ChessPosition(start_row - 2, start_col);
                    if (checkCellEmpty(board, far_pos)) {
                        ChessMove far_move = new ChessMove(myPosition, far_pos);
                        moves.add(far_move);
                    }
                }

            }
            // promotion
            else if (myPosition.getRow() == 2) {
                ChessPosition prom_pos = new ChessPosition(start_row - 1, start_col);
                if (checkCellEmpty(board, prom_pos)) {
                    for (var type : ChessPiece.PieceType.values()) {
                        if (type == ChessPiece.PieceType.PAWN || type == ChessPiece.PieceType.KING) {continue;}
                        ChessMove prom_move = new ChessMove(myPosition, prom_pos, type);
                        moves.add(prom_move);
                    }
                }
            }
            // normal move to empty space
            else {
                ChessPosition norm_pos = new ChessPosition(start_row - 1, start_col);
                if (checkCellEmpty(board, norm_pos)) {
                    ChessMove close_move = new ChessMove(myPosition, norm_pos);
                    moves.add(close_move);
                }
            }
            List<ChessMove> attack_moves = blackDiagonalAttack(board, myPosition);
            moves.addAll(attack_moves);
        }
        return moves;
    }

    public boolean moveValid(ChessBoard board, ChessPosition myPosition, ChessPosition new_pos, List<ChessMove> moves) {
        // TODO: maybe change the name because we have to return false on caputring a piece when the move is valid
        if (board.isOutofBounds(new_pos)) {return false;}
        if (board.getPiece(new_pos) == null)
        {
            ChessMove move = new ChessMove(myPosition, new_pos);
            moves.add(move);
            return true;
        }
        else if (board.getPiece(new_pos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
            ChessMove move = new ChessMove(myPosition, new_pos);
            moves.add(move);
            return false;
        }
        else {return false;}
    }

    public boolean checkCellEmpty(ChessBoard board, ChessPosition pos){
        return board.getPiece(pos) == null;
    }
    
    public List<ChessMove> whiteDiagonalAttack (ChessBoard board, ChessPosition myPosition){
        List<ChessMove> moves = new ArrayList<>();
        int start_row = myPosition.getRow();
        int start_col = myPosition.getColumn();
        ChessPosition left_pos = new ChessPosition(start_row + 1, start_col -1);
        ChessPosition right_pos = new ChessPosition(start_row + 1, start_col + 1);
        if (!board.isOutofBounds(left_pos) && !checkCellEmpty(board, left_pos)) {
            if (board.getPiece(left_pos).getTeamColor() != board.getPiece(myPosition).getTeamColor()
                    && !board.isOutofBounds(left_pos)) {
                if (start_row == 7){
                    for (var type : ChessPiece.PieceType.values()) {
                        if (type == ChessPiece.PieceType.PAWN || type == ChessPiece.PieceType.KING) {continue;}
                        ChessMove prom_move = new ChessMove(myPosition, left_pos, type);
                        moves.add(prom_move);
                    }
                }
                else {
                    ChessMove move = new ChessMove(myPosition, left_pos);
                    moves.add(move);
                }
            }
        }
        if (!board.isOutofBounds(right_pos) && !checkCellEmpty(board, right_pos)) {
            if (board.getPiece(right_pos).getTeamColor() != board.getPiece(myPosition).getTeamColor()
                    && !board.isOutofBounds(right_pos)) {
                if (start_row == 7){
                    for (var type : ChessPiece.PieceType.values()) {
                        if (type == ChessPiece.PieceType.PAWN || type == ChessPiece.PieceType.KING) {continue;}
                        ChessMove prom_move = new ChessMove(myPosition, right_pos, type);
                        moves.add(prom_move);
                    }
                }
                else {
                    ChessMove move = new ChessMove(myPosition, right_pos);
                    moves.add(move);
                }
            }
        }
        return moves;
    }

    public List<ChessMove> blackDiagonalAttack (ChessBoard board, ChessPosition myPosition){
        List<ChessMove> moves = new ArrayList<>();
        int start_row = myPosition.getRow();
        int start_col = myPosition.getColumn();
        ChessPosition left_pos = new ChessPosition(start_row - 1, start_col -1);
        ChessPosition right_pos = new ChessPosition(start_row - 1, start_col + 1);
        if (!board.isOutofBounds(left_pos) && !checkCellEmpty(board, left_pos)) {
            if (board.getPiece(left_pos).getTeamColor() != board.getPiece(myPosition).getTeamColor()
                    && !board.isOutofBounds(left_pos)) {
                if (start_row == 2){
                    for (var type : ChessPiece.PieceType.values()) {
                        if (type == ChessPiece.PieceType.PAWN || type == ChessPiece.PieceType.KING) {continue;}
                        ChessMove prom_move = new ChessMove(myPosition, left_pos, type);
                        moves.add(prom_move);
                    }
                }
                else {
                    ChessMove move = new ChessMove(myPosition, left_pos);
                    moves.add(move);
                }
            }
        }
        if (!board.isOutofBounds(right_pos) && !checkCellEmpty(board, right_pos)) {
            if (board.getPiece(right_pos).getTeamColor() != board.getPiece(myPosition).getTeamColor()
                    && !board.isOutofBounds(right_pos)) {
                if (start_row == 2){
                    for (var type : ChessPiece.PieceType.values()) {
                        if (type == ChessPiece.PieceType.PAWN || type == ChessPiece.PieceType.KING) {continue;}
                        ChessMove prom_move = new ChessMove(myPosition, right_pos, type);
                        moves.add(prom_move);
                    }
                }
                else {
                    ChessMove move = new ChessMove(myPosition, right_pos);
                    moves.add(move);
                }
            }
        }
        return moves;
    }
}
