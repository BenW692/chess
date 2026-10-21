package handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataaccess.DataAccessException;
import dataaccess.SessionDAO;
import dataaccess.UnauthorizedException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.CreateGameRequest;
import model.CreateGameResult;
import model.LogoutRequest;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.UserService;

import java.util.Map;

public class CreateGameHandler implements Handler {
    SessionDAO sessionDAO;
    public CreateGameHandler (SessionDAO sessionDAO){this.sessionDAO = sessionDAO;}

    @Override
    public void handle(@NotNull Context context) throws Exception {
        CreateGameRequest createGameRequest = null;
        try {
            String header = context.header("authorization");
            String body = context.body();
            if (!body.contains("gameName")){
                var err = new Gson().toJson(Map.of("message", "Error: bad request"));
                context.status(400);
                context.json(err);
                return;
            }
            com.google.gson.JsonObject json = new Gson().fromJson(body, JsonObject.class);
            String gameName = json.get("gameName").getAsString();
            createGameRequest = new CreateGameRequest(header, gameName);
        }
        catch (Exception e) {
            throw new DataAccessException("Miscellaneous error ", e);
        }
        UserService userService = new UserService(sessionDAO);
        GameService gameService = new GameService(sessionDAO);

        userService.authenticateUser(createGameRequest.authToken());
        CreateGameResult createGameResult = gameService.createGame(createGameRequest);
        context.json(new Gson().toJson(createGameResult));
        context.status(200);
        
    }
}
