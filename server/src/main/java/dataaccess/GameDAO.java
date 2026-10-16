package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    boolean createGame();
    GameData getGame();
    Collection<GameData> listGames();
    boolean updateGame();
}
