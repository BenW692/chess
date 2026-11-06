package service;

import dataaccess.AlreadyTakenException;
import dataaccess.DataAccessException;
import dataaccess.GameNotFoundException;
import dataaccess.SessionDAO;
import model.*;

import java.util.Collection;

public class GameService {
    private SessionDAO sessionDAO;
    public GameService(SessionDAO sessionDAO) {this.sessionDAO = sessionDAO;}

    private GameData getGame(int gameID) throws DataAccessException {
        return sessionDAO.getGame(gameID);
    };

    private void addPlayer(GameData game, JoinGameRequest joinGameRequest) throws DataAccessException {
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

    public CreateGameResult createGame(CreateGameRequest req) throws DataAccessException {
        int gameID = sessionDAO.createGame(req.gameName());
        return new CreateGameResult(gameID);
    };

    private void checkGameAvailability(GameData game, String playerColor) {
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

    public ListGameResult listGames() throws DataAccessException {
        Collection<GameData> games = sessionDAO.listGames();
        return new ListGameResult(games);
    }

    public void joinGame(JoinGameRequest joinGameRequest) throws DataAccessException {
        GameData game = getGame(joinGameRequest.gameID());
        if (game == null){
            throw new GameNotFoundException("Supplied gameID does not exist in database");
        }
        checkGameAvailability(game, joinGameRequest.playerColor());
        addPlayer(game, joinGameRequest);
    }
}
