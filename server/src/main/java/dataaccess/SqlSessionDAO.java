package dataaccess;

import chess.ChessGame;
import chess.ChessPiece;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.mysql.cj.xdevapi.UpdateType;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.jetbrains.annotations.NotNull;
import org.mindrot.jbcrypt.BCrypt;

import javax.management.RuntimeErrorException;
import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class SqlSessionDAO implements SessionDAO{
    private final List<String> createStatements = List.of(
            """
            CREATE TABLE IF NOT EXISTS user (
            `username` varchar(25) NOT NULL,
            `password` varchar(128) NOT NULL,
            `email` varchar(50) NOT NULL,
            PRIMARY KEY (`username`),
            INDEX(password)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS auth (
            `authToken` varchar(256) NOT NULL,
            `username` varchar(25) NOT NULL,
            PRIMARY KEY (`authToken`),
            INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS games (
            `gameID` int NOT NULL,
            `whiteUsername` varchar(25),
            `blackUsername` varchar(25),
            `gameName` varchar(50) NOT NULL,
            `game` TEXT DEFAULT NULL,
            PRIMARY KEY (`gameID`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """);

    private List<Object> emtpyList = List.of();

    public SqlSessionDAO () throws DataAccessException {
        try {
            configureDatabase();
        }
        catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public enum sqlCommandType {
        QUERY,
        UPDATE
    }

    public List<List<Object>> sendSqlCommand(String statement, sqlCommandType type, List<Object> params) {
        List<List<Object>> rows = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    for (int i = 0; i < params.size(); i++) {
                        preparedStatement.setObject(i+1, params.get(i));
                    }
                    if (type == sqlCommandType.QUERY) {
                        var rs = preparedStatement.executeQuery();
                        int cols = rs.getMetaData().getColumnCount();
                        while (rs.next()) {
                            List<Object> row = new ArrayList<>();
                            for (int i = 1; i <= cols; i++) {
                                row.add(rs.getObject(i));   // NULL becomes Java null
                            }
                            rows.add(row);
                        }
                    } else {
                        preparedStatement.executeUpdate();
                    }
                }
            }
        catch (Exception e) {
            System.out.println(e);
        }
        return rows;
    }

    public void configureDatabase() throws ResponseException, DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new ResponseException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
        catch (DataAccessException e){
            throw new DataAccessException("Database could not connect.");
        }
        catch (Exception e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createUser(UserData userData) {
        String statement = "INSERT INTO user VALUES (?, ?, ?)";
        String encrypted_pass = encryptPassword(userData.password());
        List<Object> params = List.of(userData.username(), encrypted_pass, userData.email());
        sendSqlCommand(statement, sqlCommandType.UPDATE, params);
    }

    String encryptPassword(String clearTextPassword) {
        return BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
    }

    @Override
    public UserData getUserWUsername(String username) {
        String statement = "SELECT * FROM user WHERE username = ?";
        List<Object> params = List.of(username);
        List<List<Object>> rs = sendSqlCommand(statement, sqlCommandType.QUERY, params);
        if (rs.isEmpty()){
            return null;
        }
        List<Object>  rs_row = rs.getFirst();
        return new UserData(rs_row.get(0).toString(), rs_row.get(1).toString(), rs_row.get(2).toString());
    }

    @Override
    public UserData getUserWAuth(String authToken) {
        String statement = "SELECT username FROM auth WHERE authToken = ?";
        List<Object> params = List.of(authToken);
        List<List<Object>> rs = sendSqlCommand(statement, sqlCommandType.QUERY, params);
        if (rs.isEmpty()){
            return null;
        }
        List<Object>  rs_row = rs.getFirst();
        // auth will return two things (authToken, username)
        String username = rs_row.getFirst().toString();
        return getUserWUsername(username);
    }

    @Override
    public void createAuth(AuthData authData) {
        String statement = "INSERT INTO auth VALUES (?, ?)";
        List<Object> params = List.of(authData.authToken(), authData.username());
        sendSqlCommand(statement, sqlCommandType.UPDATE, params);
    }

    @Override
    public AuthData getAuth(String authToken) {
        String statement = "SELECT username FROM auth WHERE authToken = ?";
        List<Object> params = List.of(authToken);
        List<List<Object>> rs = sendSqlCommand(statement, sqlCommandType.QUERY, params);
        if (rs.isEmpty()){
            return null;
        }
        List<Object>  rs_row = rs.getFirst();
        return new AuthData(authToken, rs_row.getFirst().toString());
    }

    @Override
    public void deleteAuth(String authToken) {
        String statement = "DELETE FROM auth WHERE authToken = ?";
        List<Object> params = List.of(authToken);
        sendSqlCommand(statement, sqlCommandType.UPDATE, params);
    }

    @Override
    public int createGame(String gameName) {
        int gameID = new Random().nextInt(9000) + 1000;
        ChessGame chessGame = new ChessGame();
        String jsonChessGame = new Gson().toJson(chessGame);
        String statement = "INSERT INTO games VALUES (?, NULL, NULL, ?, ?)";
        List<Object> params = List.of(gameID, gameName, jsonChessGame);
        sendSqlCommand(statement, sqlCommandType.UPDATE, params);
        return gameID;
    }

    @Override
    public GameData getGame(int gameID) {
        String statement = "SELECT * FROM games WHERE gameID = ?";
        List<Object> params = List.of(gameID);
        List<List<Object>> rs = sendSqlCommand(statement, sqlCommandType.QUERY, params);
        if (rs.isEmpty()){
            return null;
        }
        List<Object>  rs_row = rs.getFirst();
        return parseGameQueryResponse(rs_row);
    }

    @NotNull
    private static GameData parseGameQueryResponse(List<Object> rs_row) {
        ChessGame chessGame = new Gson().fromJson(rs_row.get(4).toString(), ChessGame.class);
        var wUsername = rs_row.get(1);
        var bUsername = rs_row.get(2);
        String wUsernameStr;
        String bUsernameStr;
        if (wUsername != null) {wUsernameStr = wUsername.toString();}
        else {wUsernameStr = null;}
        if (bUsername != null) {bUsernameStr = bUsername.toString();}
        else {bUsernameStr = null;}
        return new GameData((Integer) rs_row.get(0), wUsernameStr, bUsernameStr, rs_row.get(3).toString(), chessGame);
    }

    @Override
    public Collection<GameData> listGames() {
        Collection<GameData> games = new ArrayList<>();
        String statement = "SELECT * FROM games";
        List<Object> params = List.of();
        List<List<Object>> rs = sendSqlCommand(statement, sqlCommandType.QUERY, params);
        for (var row : rs) {
            GameData gameData = parseGameQueryResponse(row);
            games.add(gameData);
        }
        return games;
    }

    @Override
    public void updateGame(GameData updatedGame, GameData originalGame) {
        String jsonChessGame = new Gson().toJson(updatedGame.game());
        String wUsername = updatedGame.whiteUsername();
        String bUsername = updatedGame.blackUsername();
        String statement;
        List<Object> params;
        if (wUsername != null && bUsername != null){
            statement = "UPDATE games SET whiteUsername = ?, blackUsername = ?, game = ? WHERE gameID=?";
            params = List.of(wUsername, bUsername, jsonChessGame, originalGame.gameID());
        }
        // Since join game only lets one player join at a time I can check which player is not null and respond appropriately
        else if (wUsername != null){
            statement = "UPDATE games SET whiteUsername = ?, game = ? WHERE gameID=?";
            params = List.of(wUsername, jsonChessGame, originalGame.gameID());
        }
        else {
            statement = "UPDATE games SET blackUsername = ?, game = ? WHERE gameID=?";
            params = List.of(bUsername, jsonChessGame, originalGame.gameID());
        }
        sendSqlCommand(statement, sqlCommandType.UPDATE, params);
    }

    @Override
    public void clearApp() {
        List<String> clearStatements = List.of(
                "TRUNCATE TABLE user",
                "TRUNCATE TABLE auth",
                "TRUNCATE TABLE games"
        );
        for (var statement : clearStatements) {
            sendSqlCommand(statement, sqlCommandType.UPDATE, emtpyList);
        }
    }

    @Override
    public boolean checkPassword(String inputPassword, String username) {
        var hashedpassword = getUserWUsername(username).password();
        return BCrypt.checkpw(inputPassword, hashedpassword);
    }

}
