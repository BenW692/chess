package dataaccess.sql;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.SqlSessionDAO;
import model.*;
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
    void tearDown() throws DataAccessException {
        sessionDao.clearApp();
    }

//    @Test
//    void configureDatabase() {
//    }
//
//    @Test
//    void createUser() {
//    }
//
//    @Test
//    void getUserWUsername() {
//    }

    @Test
    void getUserWAuth() throws DataAccessException {
        sessionDao.createUser(tempUserData);
        LoginRequest loginRequest = new LoginRequest(tempUserData.username(), tempUserData.password());
        LoginResult loginResult = userService.login(loginRequest);
        UserData userDataFromAuth = sessionDao.getUserWAuth(loginResult.authToken());
        assertNotNull(userDataFromAuth);
    }

    // todo: Write a few more tests for this file so I get the test coverage without having to use the integration sql that is copied code.
//    @Test
//    void createAuth() {
//    }

    @Test
    void getAuth() throws DataAccessException {
        sessionDao.createUser(tempUserData);
        LoginRequest loginRequest = new LoginRequest(tempUserData.username(), tempUserData.password());
        LoginResult loginResult = userService.login(loginRequest);
        AuthData authData = sessionDao.getAuth(loginResult.authToken());
        assertEquals(tempUserData.username(), authData.username());
    }

//    @Test
//    void deleteAuth() {
//    }

    @Test
    void createGame() throws DataAccessException {
        int gameID = sessionDao.createGame("chess");
        GameData gameData = sessionDao.getGame(gameID);
        assertNotNull(gameData);
    }

//    @Test
//    void getGame() {
//    }

    @Test
    void listGames() throws DataAccessException {
        int gameID = sessionDao.createGame("chess");
        Collection<GameData> games = sessionDao.listGames();
        assertEquals(1, games.size());
        assertEquals(games.iterator().next().gameID(), gameID);
    }

    @Test
    void updateGame() throws DataAccessException {
        int gameID = sessionDao.createGame("chess");
        GameData ogGameData = sessionDao.getGame(gameID);
        GameData newGameData = new GameData(gameID, "new guy",
                "other guy", ogGameData.gameName(), new ChessGame());
        sessionDao.updateGame(newGameData, ogGameData);
        GameData datbaseNewGameData = sessionDao.getGame(gameID);
        assertEquals(datbaseNewGameData, newGameData);
    }

//    @Test
//    void clearApp() {
//    }
//
//    @Test
//    void checkPassword() {
//    }
}