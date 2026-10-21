package service;

import dataaccess.SessionDAO;
import model.*;

import java.util.Collection;

public class GameService {
    private SessionDAO sessionDAO;
    public GameService(SessionDAO sessionDAO) {this.sessionDAO = sessionDAO;}

    public void getGame(int gameID) {
        GameData game = sessionDAO.getGame(gameID);
    };

    public void addPlayer() {};

    public CreateGameResult createGame(CreateGameRequest req) {
        int gameID = sessionDAO.createGame(req.gameName());
        return new CreateGameResult(gameID);
    };

    public void checkGameAvailability() {};

    public ListGameResult listGames(ListGameRequest listGameRequest) {
        Collection<GameData> games = sessionDAO.listGames();
        return new ListGameResult(games);
    }
}
