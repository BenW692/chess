package service;

import dataaccess.AlreadyTakenException;
import dataaccess.GameNotFoundException;
import dataaccess.SessionDAO;
import model.*;

import java.util.Collection;

public class GameService {
    private SessionDAO sessionDAO;
    public GameService(SessionDAO sessionDAO) {this.sessionDAO = sessionDAO;}

    public GameData getGame(int gameID) {
        return sessionDAO.getGame(gameID);
    };

    public void addPlayer(GameData game, JoinGameRequest joinGameRequest) {
        // we can delete the original game and then recreate it
        UserData userData = sessionDAO.getUserWAuth(joinGameRequest.authToken());
        String username = userData.username();
        GameData updatedGame = null;
        if (joinGameRequest.playerColor().equalsIgnoreCase("white")) {
            updatedGame = game.setWhiteUsername(username);
        }
        else {
            updatedGame = game.setBlackUsername(username);
        }
        sessionDAO.updateGame(updatedGame, game);
    };

    public CreateGameResult createGame(CreateGameRequest req) {
        int gameID = sessionDAO.createGame(req.gameName());
        return new CreateGameResult(gameID);
    };

    public void checkGameAvailability(GameData game, String playerColor) {
        if (playerColor.equalsIgnoreCase("white")) {
            if (game.whiteUsername() != null){
                throw new AlreadyTakenException("Already taken");
            }
        }
        else {
            if (game.blackUsername() != null){
                throw new AlreadyTakenException("Already taken");
            }
        }
    }

    public ListGameResult listGames(ListGameRequest listGameRequest) {
        Collection<GameData> games = sessionDAO.listGames();
        return new ListGameResult(games);
    }

    public void joinGame(JoinGameRequest joinGameRequest)
    {
        GameData game = getGame(joinGameRequest.gameID());
        if (game == null){
            throw new GameNotFoundException("Supplied gameID does not exist in database");
        }
        checkGameAvailability(game, joinGameRequest.playerColor());
        addPlayer(game, joinGameRequest);
    }
}
