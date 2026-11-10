package client;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

import static ui.EscapeSequences.*;

public class ClientMain {
//    public static void main(String[] args) {
//        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
//        System.out.println("♕ 240 Chess Client: " + piece);
//    }

    // Board dimensions.
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 3;
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 0;

    // Padded characters.
    private static final String EMPTY = "   ";
    private static final String X = " X ";
    private static final String O = " O ";

    private static Random rand = new Random();


    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        drawColDescr(out);

        drawChessBoard(out);

        drawColDescr(out);

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void drawColDescr(PrintStream out) {

        setDarkGreen(out);
        out.print(EMPTY);
        String[] headers = {" a ", " b ", " c ", " d ", " e ", " f ", " g ", " h "};
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            drawHeader(out, headers[boardCol]);

            if (boardCol < BOARD_SIZE_IN_SQUARES - 1) {
                out.print(EMPTY.repeat(LINE_WIDTH_IN_PADDED_CHARS));
            }
        }
        out.print(EMPTY);
        setBlack(out);
        out.println();
    }

    private static void drawHeader(PrintStream out, String headerText) {
        int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
        int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;

        out.print(EMPTY.repeat(prefixLength));
        printHeaderText(out, headerText);
        out.print(EMPTY.repeat(suffixLength));
    }

    private static void printHeaderText(PrintStream out, String player) {
//        out.print(SET_BG_COLOR_BLACK);
//        out.print(SET_TEXT_COLOR_GREEN);

        out.print(player);

//        setBlack(out);
    }

    private static void drawChessBoard(PrintStream out) {

        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {
            drawRowOfSquares(out, boardRow);

            if (boardRow < BOARD_SIZE_IN_SQUARES - 1) {
                // Draw horizontal row separator.
                drawHorizontalLine(out);
                setDarkGray(out);
            }
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
                    else {out.print(EMPTY);}
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
//                out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));

                    if (squareRow == SQUARE_SIZE_IN_PADDED_CHARS / 2) {
                        int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
                        int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;

                        out.print(EMPTY.repeat(prefixLength));
                        printPlayer(out, rand.nextBoolean() ? X : O);
                        out.print(EMPTY.repeat(suffixLength));
                    } else {
                        out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
                    }
                }
                setBlack(out);
            }
            out.println();
        }
    }

    private static void drawHorizontalLine(PrintStream out) {

        int boardSizeInSpaces = BOARD_SIZE_IN_SQUARES * SQUARE_SIZE_IN_PADDED_CHARS +
                (BOARD_SIZE_IN_SQUARES - 1) * LINE_WIDTH_IN_PADDED_CHARS;

        for (int lineRow = 0; lineRow < LINE_WIDTH_IN_PADDED_CHARS; ++lineRow) {
            setRed(out);
            out.print(EMPTY.repeat(boardSizeInSpaces));

            setDarkGray(out);
            out.println();
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

    private static void printPlayer(PrintStream out, String player) {
//        out.print(SET_BG_COLOR_WHITE);
//        out.print(SET_TEXT_COLOR_BLACK);

        out.print(player);

//        setLightGray(out);
    }
}
