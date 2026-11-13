package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ClientMain {

    enum ClientState {
        LOGGEDOUT,
        LOGGEDIN,
        DISPLAYGAME
    }

    private static ChessBoard gameBoard;
    private static ClientState clientState = ClientState.LOGGEDIN;;
    private static ChessGame.TeamColor userTeamColor;

    // Board dimensions.
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 3;
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 0;

    // Padded characters.
    private static final String THREESPACES = "   ";

    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.println("♕ Welcome to 240 Chess. Type help to see how gameplay works.");
        Scanner scanner = new Scanner(System.in);
        stateMachineHandler(out, scanner);
    }

    private static void stateMachineHandler(PrintStream out, Scanner scanner) {
        while (true) {
            switch (clientState) {
                case ClientState.LOGGEDOUT -> {
                    if (!loggedOutUI(out, scanner)) {
                        return;
                    }
                }
                case ClientState.LOGGEDIN -> {
                    if (!loggedInUI(out, scanner)) {
                        return;
                    }
                }
                case ClientState.DISPLAYGAME -> {
                    gameBoard = new ChessBoard();
                    gameBoard.resetBoard();
                    out.print(ERASE_SCREEN);
                    drawColDescr(out);
                    drawChessBoard(out);
                    drawColDescr(out);
                    out.print(SET_BG_COLOR_BLACK);
                    out.print(SET_TEXT_COLOR_WHITE);
                    if (!gameplayUI(out, scanner)) {
                        return;
                    }
                }
            }
        }
    }

    private static boolean loggedOutUI(PrintStream out, Scanner scanner) {
        out.print(RESET_TEXT_COLOR);
        out.print("[LOGGED_OUT]>>> ");

        String line = scanner.nextLine().toLowerCase();
        out.print(SET_TEXT_COLOR_BLUE);
        String helpMSG = "";
        if (line.equalsIgnoreCase("quit")) {
            return false;
        }
        else if (line.equalsIgnoreCase("help")) {
            helpMSG = "Please create an account and/or login using the following commands:\n" +
                    "   register <USERNAME> <PASSWORD> <EMAIL> - to create an account\n" +
                    "   login <USERNAME> <PASSWORD> - to play chess\n" +
                    "   quit - exit application\n" +
                    "   help - description possible commands\n";
        }
        else if (line.startsWith("register")) {
          String[] params =  line.split(" ");
          if (params.length != 4)
          {
              out.print(SET_TEXT_COLOR_RED);
              helpMSG = " Register parameters were not entered correctly. Please follow this syntax:\n" +
                      "   register <USERNAME> <PASSWORD> <EMAIL> - to create an account\n";
          }
          // send this list to server facade
        }
        else if (line.startsWith("login")) {
            String[] params =  line.split(" ");
            // send this list to server facade
            if (params.length != 3)
            {
                out.print(SET_TEXT_COLOR_RED);
                helpMSG = " Login parameters were not entered correctly. Please follow this syntax:\n" +
                        "   login <USERNAME> <PASSWORD> - to play chess\n";
            }
            else {clientState = ClientState.LOGGEDIN;}
        }
        out.print(helpMSG);
        return true;
    }

    private static boolean loggedInUI(PrintStream out, Scanner scanner) {
        out.print(RESET_TEXT_COLOR);
        out.print("[LOGGED_IN]>>> ");

        String line = scanner.nextLine().toLowerCase();
        out.print(SET_TEXT_COLOR_BLUE);
        String helpMSG = "";
        if (line.equalsIgnoreCase("quit")) {
            return false;
        }
        else if (line.equalsIgnoreCase("help")) {
            helpMSG = "Now that you are logged in these are the available commands:\n" +
                    "   create <NAME> - creates a new game\n" +
                    "   list - list all games\n" +
                    "   join <ID> [WHITE or BLACK] - join a game as the specified team color\n" +
                    "   observe <ID> - observe the specified game\n" +
                    "   logout - to finish game play\n" +
                    "   quit - exit application\n" +
                    "   help - description possible commands\n";
        }
        else if (line.startsWith("create")) {
            String[] params =  line.split(" ");
            if (params.length != 2)
            {
                out.print(SET_TEXT_COLOR_RED);
                helpMSG = " Create parameters were not entered correctly. Please follow this syntax:\n" +
                        "   create <NAME> - creates a new game\n";
            }
            // send this list to server facade
        }
        else if (line.startsWith("join")) {
            String[] params =  line.split(" ");
            // send this list to server facade
            if (params.length != 3)
            {
                out.print(SET_TEXT_COLOR_RED);
                helpMSG = " Join parameters were not entered correctly. Please follow this syntax:\n" +
                        "   join <ID> [WHITE or BLACK] - join a game as the specified team color\n";
            }
            else {
                if (params[2].equalsIgnoreCase("white")) {
                    userTeamColor = ChessGame.TeamColor.WHITE;
                } else {
                    userTeamColor = ChessGame.TeamColor.BLACK;
                }
                clientState = ClientState.DISPLAYGAME;
            }
        }
        else if (line.startsWith("observe")){
            String[] params =  line.split(" ");
            if (params.length != 2)
            {
                out.print(SET_TEXT_COLOR_RED);
                helpMSG = " Observe parameters were not entered correctly. Please follow this syntax:\n" +
                        "   observe <ID> - observe the specified game\n";
            }
            else {
                userTeamColor = ChessGame.TeamColor.WHITE;
                clientState = ClientState.DISPLAYGAME;
            }
        }
        else if (line.equalsIgnoreCase("logout")){
            clientState = ClientState.LOGGEDOUT;
        }
        out.print(helpMSG);
        return true;
    }

    private static boolean gameplayUI(PrintStream out, Scanner scanner) {
        out.print(RESET_TEXT_COLOR);
        out.print(RESET_BG_COLOR);
        out.print("[GAMEPLAY]>>> ");

        String line = scanner.nextLine().toLowerCase();
        out.print(SET_TEXT_COLOR_BLUE);
        String helpMSG = "";
        if (line.equalsIgnoreCase("quit")) {
            return false;
        }
        else if (line.equalsIgnoreCase("help")) {
            helpMSG = "   This is where gameplay control will be implemented\n" +
                    "   logout - to finish game play\n" +
                    "   quit - exit application\n" +
                    "   help - description possible commands\n";
        }
        else if (line.equalsIgnoreCase("logout")){
            clientState = ClientState.LOGGEDOUT;
        }
        out.print(helpMSG);
        return true;
    }

    private static void drawColDescr(PrintStream out) {

        setDarkGreen(out);
        out.print(THREESPACES);
        String[] headers = {" a ", " b ", " c ", " d ", " e ", " f ", " g ", " h "};
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            if (userTeamColor == ChessGame.TeamColor.WHITE){
                drawHeader(out, headers[boardCol]);
            }
            else {drawHeader(out, headers[BOARD_SIZE_IN_SQUARES - 1 - boardCol]);}
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
                        if (userTeamColor == ChessGame.TeamColor.WHITE) {
                            out.print(rowInd.get(boardRow));
                        }
                        else {out.print(rowInd.get(BOARD_SIZE_IN_SQUARES - 1 - boardRow));}
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
        ChessPiece chessPiece = null;
        if (userTeamColor == ChessGame.TeamColor.BLACK) {
            chessPiece = gameBoard.getPiece(new ChessPosition(boardRow+1, 8 - boardCol));
        }
        else {
            chessPiece = gameBoard.getPiece(new ChessPosition(8 - boardRow, boardCol+1));
        }
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
