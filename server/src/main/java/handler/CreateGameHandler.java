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
            com.google.gson.JsonObject json = new Gson().fromJson(body, JsonObject.class);
            String gameName = json.get("gameName").getAsString();
            createGameRequest = new CreateGameRequest(header, gameName);

        }
        catch (Exception e) {
            throw new DataAccessException("Miscellaneous error ", e);
        }
        if (createGameRequest.gameName() == null || createGameRequest.authToken() == null){
            var body = new Gson().toJson(Map.of("message", "Error: bad request"));
            context.status(500);
            context.json(body);
            return;
        }
        UserService userService = new UserService(sessionDAO);
        GameService gameService = new GameService(sessionDAO);
        try {
            userService.authenticateUser(createGameRequest.authToken());
            CreateGameResult createGameResult = gameService.createGame(createGameRequest);
            context.json(new Gson().toJson(createGameResult));
            context.status(200);
        }
        catch (UnauthorizedException e)
        {
            var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
            context.status(401);
            context.json(body);
        }
    }
}
