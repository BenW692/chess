package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;

public interface SessionDAO {
    void createUser(UserData userData); // this will probably throw an exception if we find a user when we try to create
    UserData getUserWUsername(String username);
    UserData getUserWAuth(String authToken);
    void createAuth(AuthData authData); //will probably throw something
    AuthData getAuth(String authToken);
    void deleteAuth(String authToken);
    int createGame(String gameName);
    GameData getGame(int gameID);
    Collection<GameData> listGames();
    void updateGame(GameData updatedGame, GameData originalGame);
    void clearApp();
}
