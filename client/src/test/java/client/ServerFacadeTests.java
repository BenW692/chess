package client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import model.GameData;
import model.LoginRequest;
import model.RegisterRequest;
import org.junit.jupiter.api.*;
import server.Server;

import java.util.Collection;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;
    private RegisterRequest regReq = new RegisterRequest("test", "test", "test@gmail.com");

    @BeforeAll
    public static void init() throws Exception {
        int port = 8080;
        server = new Server();
        var serverPort = server.run(port);
        System.out.println("Started test HTTP server on " + serverPort);
        String host = "localhost";
        serverFacade = new ServerFacade(host, port);
    }

    @AfterEach
    void clearServer() {
        serverFacade.clearApp();
    }

    @AfterAll
    static void stopServer() {
        serverFacade.clearApp();
        server.stop();
    }


    @Test
    void goodRegister() {
        HTTPRequestResponse res = serverFacade.register(regReq);
        assertTrue(res.success());
    }

    @Test
    void userAlreadyExistsRegister() {
        goodRegister();
        HTTPRequestResponse res = serverFacade.register(regReq);
        assertFalse(res.success());
        assertTrue(res.message().contains("username has been taken"));
    }

    @Test
    void goodLogin() {
        goodRegister();
        LoginRequest logReq = new LoginRequest("test", "test");
        HTTPRequestResponse res = serverFacade.login(logReq);
        assertTrue(res.success());
    }

    @Test
    void loginBeforeRegistering() {
        LoginRequest logReq = new LoginRequest("test", "test");
        HTTPRequestResponse res = serverFacade.login(logReq);
        assertFalse(res.success());
    }

    @Test
    void goodLogout() {
        goodLogin();
        HTTPRequestResponse res = serverFacade.logout();
        assertTrue(res.success());
    }

    @Test
    void neverLoggedInLogout() {
        HTTPRequestResponse res = serverFacade.logout();
        assertFalse(res.success());
        assertTrue(res.message().contains("unauthorized"));
    }

    @Test
    void emptyListGames() {
        goodLogin();
        HTTPRequestResponse res = serverFacade.listGames();
        assertTrue(res.success());
        JsonObject httpBody = JsonParser.parseString(res.httpBody()).getAsJsonObject();
        Map<String, Collection<GameData>> games = new Gson().fromJson(httpBody, new TypeToken<Map<String,Collection<GameData>>>(){}.getType());
        assertEquals(games.get("games").size(), 0);
    }

    @Test
    void noLoginListGames() {
        HTTPRequestResponse res = serverFacade.listGames();
        assertFalse(res.success());
        assertTrue(res.message().contains("unauthorized"));
    }

    @Test
    void oneGameListGames() {
        goodCreateGame();
        HTTPRequestResponse res = serverFacade.listGames();
        assertTrue(res.success());
        JsonObject httpBody = JsonParser.parseString(res.httpBody()).getAsJsonObject();
        Map<String, Collection<GameData>> games = new Gson().fromJson(httpBody, new TypeToken<Map<String,Collection<GameData>>>(){}.getType());
        assertEquals(games.get("games").size(),     1);
    }

    @Test
    void goodCreateGame() {
        goodLogin();
        HTTPRequestResponse res = serverFacade.createGame("test");
        assertTrue(res.success());
    }

    @Test
    void noLoginCreateGame() {
        HTTPRequestResponse res = serverFacade.createGame("test");
        assertFalse(res.success());
        assertTrue(res.message().contains("unauthorized"));
    }

    @Test
    void goodJoinGame() {
        goodLogin();
        HTTPRequestResponse res = serverFacade.createGame("test");
        JsonObject httpBody = JsonParser.parseString(res.httpBody()).getAsJsonObject();
        int gameID = httpBody.get("gameID").getAsInt();
        res = serverFacade.joinGame("white", gameID);
        assertTrue(res.success());
    }

    @Test
    void noLoginJoinGame() {
        HTTPRequestResponse res = serverFacade.joinGame("test", 1234);
        assertFalse(res.success());
        assertTrue(res.message().contains("unauthorized"));
    }

}
