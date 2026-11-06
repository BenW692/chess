package dataaccess.sql;

import chess.ChessGame;
import dataaccess.DataAccessException;
import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import service.UserService;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class PositiveSqlSessionDAOTest {
    SqlSessionDAO sessionDao;
    UserService userService;
    UserData tempUserData;
    LoginRequest loginRequest;

    @BeforeEach
    void setUp() throws DataAccessException {
        sessionDao = new SqlSessionDAO();
        userService = new UserService(sessionDao);
        tempUserData = new UserData("testU", "test", "yungthug@myspace.com");
        loginRequest = new LoginRequest(tempUserData.username(), tempUserData.password());
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        sessionDao.clearApp();
    }

    @Test
    void getUserWAuthTest() throws DataAccessException {
        sessionDao.createUser(tempUserData);
        LoginRequest loginRequest = new LoginRequest(tempUserData.username(), tempUserData.password());
        LoginResult loginResult = userService.login(loginRequest);
        UserData userDataFromAuth = sessionDao.getUserWAuth(loginResult.authToken());
        assertNotNull(userDataFromAuth);
    }

    @Test
    void getAuthTest() throws DataAccessException {
        sessionDao.createUser(tempUserData);
        LoginRequest loginRequest = new LoginRequest(tempUserData.username(), tempUserData.password());
        LoginResult loginResult = userService.login(loginRequest);
        AuthData authData = sessionDao.getAuth(loginResult.authToken());
        assertEquals(tempUserData.username(), authData.username());
    }

    @Test
    void deleteAuthTest() throws DataAccessException {
        sessionDao.createUser(tempUserData);
        LoginRequest loginRequest = new LoginRequest(tempUserData.username(), tempUserData.password());
        LoginResult loginResult = userService.login(loginRequest);
        sessionDao.deleteAuth(loginResult.authToken());
        assertNull(sessionDao.getAuth(loginResult.authToken()));
    }

    @Test
    void createGameTest() throws DataAccessException {
        int gameID = sessionDao.createGame("chess");
        GameData gameData = sessionDao.getGame(gameID);
        assertNotNull(gameData);
    }

    @Test
    void listGamesTest() throws DataAccessException {
        int gameID = sessionDao.createGame("chess");
        Collection<GameData> games = sessionDao.listGames();
        assertEquals(1, games.size());
        assertEquals(games.iterator().next().gameID(), gameID);
    }

    @ParameterizedTest
    @CsvSource({", test",
                "test,",
                "new guy, other guy"})
    void updateGameTest(String wUsername, String bUsername) throws DataAccessException {
        int gameID = sessionDao.createGame("chess");
        GameData ogGameData = sessionDao.getGame(gameID);
        GameData newGameData = new GameData(gameID, wUsername,
                bUsername, ogGameData.gameName(), new ChessGame());
        sessionDao.updateGame(newGameData, ogGameData);
        GameData datbaseNewGameData = sessionDao.getGame(gameID);
        assertEquals(datbaseNewGameData, newGameData);
    }
}