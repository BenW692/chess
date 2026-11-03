package dataaccess;

import com.mysql.cj.xdevapi.UpdateType;
import model.AuthData;
import model.GameData;
import model.UserData;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

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

    public SqlSessionDAO (){
        try {
            configureDatabase();
        }
        catch (ResponseException e) {
            System.out.println(e);
        }
        catch (DataAccessException e) {
            System.out.println(e);
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    public enum sqlCommandType {
        QUERY,
        UPDATE
    }

    public void main() throws DataAccessException {
       UserData ud = new UserData("ben", "yungthug", "sparkleboi@x.com");
       createUser(ud);
       UserData user = getUserWUsername("ben");
       System.out.println(user);
       AuthData ad = new AuthData("1234_5678", "ben");
       createAuth(ad);
       UserData auth_user = getUserWAuth(ad.authToken());
       System.out.println(auth_user);
       clearApp();
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
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public void createUser(UserData userData) {
        String statement = "INSERT INTO user VALUES (?, ?, ?)";
        List<Object> params = List.of(userData.username(), userData.password(), userData.email());
        sendSqlCommand(statement, sqlCommandType.UPDATE, params);
    }

    @Override
    public UserData getUserWUsername(String username) {
        String statement = "SELECT * FROM user WHERE username = ?";
        List<Object> params = List.of(username);
        List<List<Object>> rs = sendSqlCommand(statement, sqlCommandType.QUERY, params);
        List<Object>  rs_row = rs.getFirst();
        return new UserData(rs_row.get(0).toString(), rs_row.get(1).toString(), rs_row.get(2).toString());
    }

    @Override
    public UserData getUserWAuth(String authToken) {
        String statement = "SELECT username FROM auth WHERE authToken = ?";
        List<Object> params = List.of(authToken);
        List<List<Object>> rs = sendSqlCommand(statement, sqlCommandType.QUERY, params);
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
        return null;
    }

    @Override
    public void deleteAuth(String authToken) {

    }

    @Override
    public int createGame(String gameName) {
        return 0;
    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public Collection<GameData> listGames() {
        return List.of();
    }

    @Override
    public void updateGame(GameData updatedGame, GameData originalGame) {

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
}
