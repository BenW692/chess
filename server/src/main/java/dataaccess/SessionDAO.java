package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;

public interface SessionDAO {
    void setDatabaseConnection(boolean bool) throws DataAccessException;
    void createUser(UserData userData) throws DataAccessException; // this will probably throw an exception if we find a user when we try to create
    UserData getUserWUsername(String username) throws DataAccessException;
    UserData getUserWAuth(String authToken) throws DataAccessException;
    void createAuth(AuthData authData) throws DataAccessException; //will probably throw something
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    int createGame(String gameName) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    void updateGame(GameData updatedGame, GameData originalGame) throws DataAccessException;
    void clearApp() throws DataAccessException;
    boolean checkPassword(String inputPassword, String username) throws DataAccessException;
}
