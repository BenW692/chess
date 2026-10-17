package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;

public interface SessionDAO {
    void createUser(UserData userData); // this will probably throw an exception if we find a user when we try to create
    UserData getUser(String username);
    void createAuth(AuthData authData); //will probably throw something
    AuthData getAuth();
    boolean deleteAuth();
    boolean createGame();
    GameData getGame();
    Collection<GameData> listGames();
    boolean updateGame();
}
