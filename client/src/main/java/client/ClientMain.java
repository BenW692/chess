package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

import static ui.EscapeSequences.*;

public class ClientMain {
    private static ChessBoard gameBoard;

//    public static void main(String[] args) {
//        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
//        System.out.println("♕ 240 Chess Client: " + piece);
//    }

    // Board dimensions.
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 3;
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 0;

    // Padded characters.
    private static final String THREESPACES = "   ";


    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        gameBoard = new ChessBoard();
        gameBoard.resetBoard();

        out.print(ERASE_SCREEN);

        drawColDescr(out);


        drawChessBoard(out);

        drawColDescr(out);

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void drawColDescr(PrintStream out) {

        setDarkGreen(out);
        out.print(THREESPACES);
        String[] headers = {" a ", " b ", " c ", " d ", " e ", " f ", " g ", " h "};
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            drawHeader(out, headers[boardCol]);

            if (boardCol < BOARD_SIZE_IN_SQUARES - 1) {
                out.print(THREESPACES.repeat(LINE_WIDTH_IN_PADDED_CHARS));
            }
        }
        out.print(THREESPACES);
        setBlack(out);
        out.println();
    }

    private static void drawHeader(PrintStream out, String headerText) {
        int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
        int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;

        out.print(THREESPACES.repeat(prefixLength));
        out.print(headerText);
        out.print(THREESPACES.repeat(suffixLength));
    }

    private static void drawChessBoard(PrintStream out) {

        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {
            drawRowOfSquares(out, boardRow);
        }
    }

    private static void drawRowOfSquares(PrintStream out, int boardRow) {
        List<String> rowInd = List.of(" 8 ", " 7 ", " 6 ", " 5 ", " 4 ", " 3 ", " 2 ", " 1 ");
        for (int squareRow = 0; squareRow < SQUARE_SIZE_IN_PADDED_CHARS; ++squareRow) {
            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES+2; ++boardCol) {
                if (boardCol == 0 || boardCol == BOARD_SIZE_IN_SQUARES+1){
                    setDarkGreen(out);
                    if (squareRow == SQUARE_SIZE_IN_PADDED_CHARS / 2) {
                        out.print(rowInd.get(boardRow));
                    }
                    else {out.print(THREESPACES);}
                }
                else {
                    int boardRowType = boardRow % 2;
                    if (boardRowType == 0) {
                        if (boardCol % 2 == 0) {
                            setLightGray(out);
                        } else {
                            setDarkGray(out);
                        }
                    } else {
                        if (boardCol % 2 == 0) {
                            setDarkGray(out);
                        } else {
                            setLightGray(out);
                        }
                    }

                    if (squareRow == SQUARE_SIZE_IN_PADDED_CHARS / 2) {
                        int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
                        int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
                        out.print(THREESPACES.repeat(prefixLength));
                        // we have to subtract 1 off the column for the first col that is the number label
                        placePiece(out, boardRow, boardCol-1);
                        out.print(THREESPACES.repeat(suffixLength));
                    } else {
                        out.print(THREESPACES.repeat(SQUARE_SIZE_IN_PADDED_CHARS));

                    }
                }
                setBlack(out);
            }
            out.println();
        }
    }

    private static void placePiece(PrintStream out, int boardRow, int boardCol) {
        ChessPiece chessPiece = gameBoard.getPiece(new ChessPosition(boardRow+1, boardCol+1));
        if (chessPiece == null){
            out.print("   ");
        }
        else {
            if (chessPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                out.print(SET_TEXT_COLOR_WHITE);
            }
            else {out.print(SET_TEXT_COLOR_BLACK);}
            out.print(" " + chessPiece.toString().toUpperCase().charAt(1) + " ");
        }
    }

    private static void setLightGray(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setRed(PrintStream out) {
        out.print(SET_BG_COLOR_RED);
        out.print(SET_TEXT_COLOR_RED);
    }

    private static void setDarkGray(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREY);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setDarkGreen(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREEN);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

}
