package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.LoginRequest;
import model.RegisterRequest;

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
    //server facade instance
    private static ServerFacade serverFacade;
    private static ChessBoard gameBoard;
    private static ClientState clientState = ClientState.LOGGEDOUT;;
    private static ChessGame.TeamColor userTeamColor;

    // Board dimensions.
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 3;
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 0;

    // Padded characters.
    private static final String THREESPACES = "   ";

    public static void main(String[] args) throws Exception {
        // create an instance of server facade and create an http client
        serverFacade = new ServerFacade("localhost", 8080);
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.println("♕ Welcome to 240 Chess. Type help to see how gameplay works.");
        Scanner scanner = new Scanner(System.in);
        stateMachineHandler(out, scanner);
        serverFacade.clearApp();
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
        String[] response = {};
        if (line.equalsIgnoreCase("quit")) {
            return false;
        }
        else if (line.equalsIgnoreCase("help")) {
            response = new String[]{"Please create an account and/or login using the following commands:",
                            "register <USERNAME> <PASSWORD> <EMAIL> - to create an account", 
                            "login <USERNAME> <PASSWORD> - to play chess", 
                            "quit - exit application", 
                            "help - description possible commands"};
        }
        else if (line.startsWith("register")) {
            String[] params =  line.split(" ");
            if (params.length != 4)
            {
                out.print(SET_TEXT_COLOR_RED);
                response = new String[]{"Register parameters were not entered correctly. Please follow this syntax:",
                      "register <USERNAME> <PASSWORD> <EMAIL> - to create an account"};
            }
            // send this list to server facade
            else {
                client.HTTPRequestResponse reqResponse = serverFacade.register(new RegisterRequest(params[1], params[2], params[3]));
                if (!reqResponse.success()) {
                    out.print(SET_TEXT_COLOR_RED);
                }
                response = new String[]{reqResponse.message()};
            }
        }
        else if (line.startsWith("login")) {
            String[] params =  line.split(" ");
            // send this list to server facade
            if (params.length != 3)
            {
                out.print(SET_TEXT_COLOR_RED);
                response = new String[]{"Login parameters were not entered correctly. Please follow this syntax:",
                        "login <USERNAME> <PASSWORD> - to play chess"};
            }
            else {
                client.HTTPRequestResponse reqResponse = serverFacade.login(new LoginRequest(params[1], params[2]));
                if (!reqResponse.success()) {
                    out.print(SET_TEXT_COLOR_RED);
                }
                else {clientState = ClientState.LOGGEDIN;}
                response = new String[]{reqResponse.message()};
            }
        }
        printWIndent(out, response);
        return true;
    }

    private static boolean loggedInUI(PrintStream out, Scanner scanner) {
        out.print(RESET_TEXT_COLOR);
        out.print("[LOGGED_IN]>>> ");

        String line = scanner.nextLine().toLowerCase();
        out.print(SET_TEXT_COLOR_BLUE);
        String[] response = new String[]{""};
        if (line.equalsIgnoreCase("quit")) {
            return false;
        }
        else if (line.equalsIgnoreCase("help")) {
            response = new String[]{"Now that you are logged in these are the available commands:",
                    "create <NAME> - creates a new game",
                    "list - list all games",
                    "join <ID> [WHITE or BLACK] - join a game as the specified team color",
                    "observe <ID> - observe the specified game",
                    "logout - to finish game play",
                    "quit - exit application",
                    "help - description possible commands"};
        }
        else if (line.startsWith("create")) {
            String[] params =  line.split(" ");
            if (params.length != 2)
            {
                out.print(SET_TEXT_COLOR_RED);
                response = new String[]{"Create parameters were not entered correctly. Please follow this syntax:",
                        "create <NAME> - creates a new game"};
            }
            else {
                client.HTTPRequestResponse reqResponse = serverFacade.createGame(params[1]);
                if (!reqResponse.success()) {
                    out.print(SET_TEXT_COLOR_RED);
                    response = new String[]{reqResponse.message()};
                }
                else {
                    JsonObject httpBody = JsonParser.parseString(reqResponse.httpBody()).getAsJsonObject();
                    String gameID = httpBody.get("gameID").getAsString();
                    response = new String[]{reqResponse.message(), "The gameID on the game you just created is: " + gameID};
                }
            }
        }
        else if (line.startsWith("join")) {
            String[] params =  line.split(" ");
            // send this list to server facade
            if (params.length != 3)
            {
                out.print(SET_TEXT_COLOR_RED);
                response = new String[]{"Join parameters were not entered correctly. Please follow this syntax:",
                        "join <ID> [WHITE or BLACK] - join a game as the specified team color"};
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
                response = new String[]{"Observe parameters were not entered correctly. Please follow this syntax:",
                        "observe <ID> - observe the specified game"};
            }
            else {
                userTeamColor = ChessGame.TeamColor.WHITE;
                clientState = ClientState.DISPLAYGAME;
            }
        }
        else if (line.equalsIgnoreCase("logout")){
            client.HTTPRequestResponse reqResponse = serverFacade.logout();
            if (!reqResponse.success()) {
                out.print(SET_TEXT_COLOR_RED);
            }
            else {clientState = ClientState.LOGGEDOUT;}
            response = new String[]{reqResponse.message()};
        }
        printWIndent(out, response);
        return true;
    }

    private static boolean gameplayUI(PrintStream out, Scanner scanner) {
        out.print(RESET_TEXT_COLOR);
        out.print(RESET_BG_COLOR);
        out.print("[GAMEPLAY]>>> ");

        String line = scanner.nextLine().toLowerCase();
        out.print(SET_TEXT_COLOR_BLUE);
        String[] response = new String[]{""};
        if (line.equalsIgnoreCase("quit")) {
            return false;
        }
        else if (line.equalsIgnoreCase("help")) {
            response = new String[]{"This is where gameplay control will be implemented",
                    "logout - to finish game play",
                    "quit - exit application",
                    "help - description possible commands"};
        }
        else if (line.equalsIgnoreCase("logout")){
            clientState = ClientState.LOGGEDOUT;
        }
        printWIndent(out, response);
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
    
    private static void printWIndent(PrintStream out, String[] msg) {
        for (var line : msg){
            out.print("   " + line + "\n");
        }
    }

}
