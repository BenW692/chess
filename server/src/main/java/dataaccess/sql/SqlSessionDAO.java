package dataaccess.sql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.ResponseException;
import dataaccess.SessionDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.jetbrains.annotations.NotNull;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SqlSessionDAO implements SessionDAO {
    public boolean databaseConnection = false;

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

    public enum SqlCommandType {
        QUERY,
        UPDATE
    }

    public List<List<Object>> sendSqlCommand(String statement, SqlCommandType type, List<Object> params) throws DataAccessException {
        List<List<Object>> rows = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                for (int i = 0; i < params.size(); i++) {
                    preparedStatement.setObject(i+1, params.get(i));
                }
                if (type == SqlCommandType.QUERY) {
                    var rs = preparedStatement.executeQuery();
                    parseQueryResponse(rs, rows);
                } else {
                    preparedStatement.executeUpdate();
                }
            }
        }
        catch (DataAccessException e) {
            setDatabaseConnection(false);
        }
        catch (Exception e) {
            System.out.println(e);
        }
        return rows;
    }

    private static void parseQueryResponse(ResultSet rs, List<List<Object>> rows) throws SQLException {
        int cols = rs.getMetaData().getColumnCount();
        while (rs.next()) {
            List<Object> row = new ArrayList<>();
            for (int i = 1; i <= cols; i++) {
                row.add(rs.getObject(i));   // NULL becomes Java null
            }
            rows.add(row);
        }
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
            setDatabaseConnection(false);
            throw new DataAccessException("Database could not connect.");
        }
        catch (Exception e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setDatabaseConnection(boolean bool) throws DataAccessException {
        databaseConnection = bool;
        if (!bool)
        {
            throw new DataAccessException("Datbase connection lost");
        }
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        String statement = "INSERT INTO user VALUES (?, ?, ?)";
        String encrypted_pass = encryptPassword(userData.password());
        List<Object> params = List.of(userData.username(), encrypted_pass, userData.email());
        sendSqlCommand(statement, SqlCommandType.UPDATE, params);
    }

    String encryptPassword(String clearTextPassword) {
        return BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
    }

    @Override
    public UserData getUserWUsername(String username) throws DataAccessException {
        String statement = "SELECT * FROM user WHERE username = ?";
        List<Object> params = List.of(username);
        List<List<Object>> rs = sendSqlCommand(statement, SqlCommandType.QUERY, params);
        if (rs.isEmpty()){
            return null;
        }
        List<Object>  rsRow = rs.getFirst();
        return new UserData(rsRow.get(0).toString(), rsRow.get(1).toString(), rsRow.get(2).toString());
    }

    @Override
    public UserData getUserWAuth(String authToken) throws DataAccessException {
        String statement = "SELECT username FROM auth WHERE authToken = ?";
        List<Object> params = List.of(authToken);
        List<List<Object>> rs = sendSqlCommand(statement, SqlCommandType.QUERY, params);
        if (rs.isEmpty()){
            return null;
        }
        List<Object>  rsRow = rs.getFirst();
        // auth will return two things (authToken, username)
        String username = rsRow.getFirst().toString();
        return getUserWUsername(username);
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        String statement = "INSERT INTO auth VALUES (?, ?)";
        List<Object> params = List.of(authData.authToken(), authData.username());
        sendSqlCommand(statement, SqlCommandType.UPDATE, params);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String statement = "SELECT username FROM auth WHERE authToken = ?";
        List<Object> params = List.of(authToken);
        List<List<Object>> rs = sendSqlCommand(statement, SqlCommandType.QUERY, params);
        if (rs.isEmpty()){
            return null;
        }
        List<Object>  rsRow = rs.getFirst();
        return new AuthData(authToken, rsRow.getFirst().toString());
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String statement = "DELETE FROM auth WHERE authToken = ?";
        List<Object> params = List.of(authToken);
        sendSqlCommand(statement, SqlCommandType.UPDATE, params);
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        int gameID = new Random().nextInt(9000) + 1000;
        ChessGame chessGame = new ChessGame();
        String jsonChessGame = new Gson().toJson(chessGame);
        String statement = "INSERT INTO games VALUES (?, NULL, NULL, ?, ?)";
        List<Object> params = List.of(gameID, gameName, jsonChessGame);
        sendSqlCommand(statement, SqlCommandType.UPDATE, params);
        return gameID;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String statement = "SELECT * FROM games WHERE gameID = ?";
        List<Object> params = List.of(gameID);
        List<List<Object>> rs = sendSqlCommand(statement, SqlCommandType.QUERY, params);
        if (rs.isEmpty()){
            return null;
        }
        List<Object>  rsRow = rs.getFirst();
        return parseGameQueryResponse(rsRow);
    }

    @NotNull
    private static GameData parseGameQueryResponse(List<Object> rsRow) {
        ChessGame chessGame = new Gson().fromJson(rsRow.get(4).toString(), ChessGame.class);
        var wUsername = rsRow.get(1);
        var bUsername = rsRow.get(2);
        String wUsernameStr;
        String bUsernameStr;
        if (wUsername != null) {wUsernameStr = wUsername.toString();}
        else {wUsernameStr = null;}
        if (bUsername != null) {bUsernameStr = bUsername.toString();}
        else {bUsernameStr = null;}
        return new GameData((Integer) rsRow.get(0), wUsernameStr, bUsernameStr, rsRow.get(3).toString(), chessGame);
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        Collection<GameData> games = new ArrayList<>();
        String statement = "SELECT * FROM games";
        List<Object> params = List.of();
        List<List<Object>> rs = sendSqlCommand(statement, SqlCommandType.QUERY, params);
        for (var row : rs) {
            GameData gameData = parseGameQueryResponse(row);
            games.add(gameData);
        }
        return games;
    }

    @Override
    public void updateGame(GameData updatedGame, GameData originalGame) throws DataAccessException {
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
        sendSqlCommand(statement, SqlCommandType.UPDATE, params);
    }

    @Override
    public void clearApp() throws DataAccessException {
        List<String> clearStatements = List.of(
                "TRUNCATE TABLE user",
                "TRUNCATE TABLE auth",
                "TRUNCATE TABLE games"
        );
        for (var statement : clearStatements) {
            sendSqlCommand(statement, SqlCommandType.UPDATE, emtpyList);
        }
    }

    @Override
    public boolean checkPassword(String inputPassword, String username) throws DataAccessException {
        var hashedpassword = getUserWUsername(username).password();
        return BCrypt.checkpw(inputPassword, hashedpassword);
    }

}
