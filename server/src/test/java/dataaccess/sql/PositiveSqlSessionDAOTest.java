package dataaccess.sql;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.SqlSessionDAO;
import model.GameData;
import model.LoginRequest;
import model.LoginResult;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserService;

import java.util.Collection;
import java.util.List;
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
    void tearDown() {
        sessionDao.clearApp();
    }

    @Test
    void configureDatabase() {
    }

    @Test
    void createUser() {
    }

    @Test
    void getUserWUsername() {
    }

    @Test
    void getUserWAuth() throws DataAccessException {
        sessionDao.createUser(tempUserData);
        LoginRequest loginRequest = new LoginRequest(tempUserData.username(), tempUserData.password());
        LoginResult loginResult = userService.login(loginRequest);
        UserData userDataFromAuth = sessionDao.getUserWAuth(loginResult.authToken());
        assertNotNull(userDataFromAuth);
    }

    @Test
    void createAuth() {
    }

    @Test
    void getAuth() {
    }

    @Test
    void deleteAuth() {
    }

    @Test
    void createGame() {
        int gameID = sessionDao.createGame("chess");
        GameData gameData = sessionDao.getGame(gameID);
        assertNotNull(gameData);
    }

    @Test
    void getGame() {
    }

    @Test
    void listGames() {
        int gameID = sessionDao.createGame("chess");
        Collection<GameData> games = sessionDao.listGames();
        assertEquals(1, games.size());
        assertEquals(games.iterator().next().gameID(), gameID);
    }

    @Test
    void updateGame() {
        int gameID = sessionDao.createGame("chess");
        GameData ogGameData = sessionDao.getGame(gameID);
        GameData newGameData = new GameData(gameID, "new guy",
                "other guy", ogGameData.gameName(), new ChessGame());
        sessionDao.updateGame(newGameData, ogGameData);
        GameData datbaseNewGameData = sessionDao.getGame(gameID);
        assertEquals(datbaseNewGameData, newGameData);
    }


    @Test
    void clearApp() {
    }

    @Test
    void checkPassword() {
    }
}