package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.*;

public class LocalSessionDAO implements SessionDAO {
    Map<String, UserData> userMap = new HashMap<>(); //username, UserData
    Collection<String> authTokens = new ArrayList<>();
    Map<Integer, GameData> gameMap = Map.of(); //gameID, gameData

    @Override
    public void createUser(UserData userData) {
        userMap.put(userData.username(), userData);
    }

    @Override
    public UserData getUser(String username) {
        return userMap.getOrDefault(username, null);
    }

    @Override
    public void createAuth(AuthData authData) {
        authTokens.add(authData.authToken());
    }

    @Override
    public AuthData getAuth() {
        return null;
    }

    @Override
    public boolean deleteAuth() {
        return false;
    }

    @Override
    public boolean createGame() {
        return false;
    }

    @Override
    public GameData getGame() {
        return null;
    }

    @Override
    public Collection<GameData> listGames() {
        return List.of();
    }

    @Override
    public boolean updateGame() {
        return false;
    }
}
