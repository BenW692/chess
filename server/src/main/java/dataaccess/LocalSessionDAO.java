package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.*;

public class LocalSessionDAO implements SessionDAO {
    Map<String, UserData> userMap = new HashMap<>(); //username, UserData
    Map<String, AuthData> authTokens = new HashMap<>();
    Map<Integer, GameData> gameMap = new HashMap<>(); //gameID, gameData

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
        authTokens.put(authData.authToken(), authData);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return authTokens.getOrDefault(authToken, null);
    }

    @Override
    public void deleteAuth(String authToken) {
        authTokens.remove(authToken);
    }

    @Override
    public int createGame(String gameName) {
        int gameID = new Random().nextInt(9000) + 1000;
        ChessGame chessGame = new ChessGame();
        GameData gameData = new GameData(gameID, null, null, gameName, chessGame);
        gameMap.put(gameID, gameData);
        return gameID;
    }

    @Override
    public GameData getGame(int gameID) {
        return gameMap.getOrDefault(gameID, null);
    }

    @Override
    public Collection<GameData> listGames() {
        return List.of();
    }

    @Override
    public boolean updateGame() {
        return false;
    }

    public void clearApp() {
        gameMap.clear();
        authTokens.clear();
        userMap.clear();
    }
}
