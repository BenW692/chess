package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import model.GameData;
import model.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static ui.EscapeSequences.*;

public class ClientMain {
    private static String host = "localhost";
    private static int port = 8080;

    public ClientMain(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public ClientMain() {
        host = "localhost";
        port = 8080;
    }

    public static void main(String[] args) throws Exception {
        // create an instance of server facade and create an http client
        Client client = new Client(host, port);
    }
}