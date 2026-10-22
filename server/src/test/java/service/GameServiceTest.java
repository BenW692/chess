package service;

import dataaccess.AlreadyTakenException;
import dataaccess.GameNotFoundException;
import dataaccess.LocalSessionDAO;
import dataaccess.UnauthorizedException;
import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    LocalSessionDAO sessionDao;
    UserService userService;
    GameService gameService;
    UserData tempUserData;
    CreateGameRequest createGameRequest;
    String tempAuthToken;

    @BeforeEach
    void setUp() {
        sessionDao = new LocalSessionDAO();
        userService = new UserService(sessionDao);
        gameService = new GameService(sessionDao);
        tempUserData = new UserData("testU", "test", "yungthug@myspace.com");
        tempAuthToken = createTempUser();
        createGameRequest = new CreateGameRequest(tempAuthToken, "testGame");
    }

    @AfterEach
    void tearDown() {
        sessionDao.clearApp();
    }

    @Test
    void createGame() {
        CreateGameResult result = gameService.createGame(createGameRequest);
        assertNotNull(sessionDao.getGame(result.gameID()));
    }

    @Test
    void listGames() {
        gameService.createGame(createGameRequest);
        ListGameResult result = gameService.listGames();
        assertFalse(result.games().isEmpty());
    }

    @Test
    void noGameJoinGame() {
        JoinGameRequest req = new JoinGameRequest(tempAuthToken, "WHITE", 1234);
        assertThrows(GameNotFoundException.class, () -> {gameService.joinGame(req);});
    }

    @Test
    void unavailableJoinGame() {
        CreateGameResult res = gameService.createGame(createGameRequest);
        JoinGameRequest req = new JoinGameRequest(tempAuthToken, "WHITE", res.gameID());
        gameService.joinGame(req);
        assertThrows(AlreadyTakenException.class, () -> {gameService.joinGame(req);});
        JoinGameRequest req2 = new JoinGameRequest(tempAuthToken, "BLACK", res.gameID());
        gameService.joinGame(req2);
        assertThrows(AlreadyTakenException.class, () -> {gameService.joinGame(req2);});
    }

    @Test
    void goodJoinGame() {
        CreateGameResult res = gameService.createGame(createGameRequest);
        JoinGameRequest req = new JoinGameRequest(tempAuthToken, "WHITE", res.gameID());
        gameService.joinGame(req);
    }

    String createTempUser() {
        RegisterRequest tempRegisterRequest = new RegisterRequest(tempUserData.username(),
                tempUserData.password(), tempUserData.email());
        return userService.register(tempRegisterRequest).authToken();
    }
}