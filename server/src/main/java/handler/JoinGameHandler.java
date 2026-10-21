package handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataaccess.*;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.JoinGameRequest;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.UserService;

import java.util.Map;

public class JoinGameHandler implements Handler {
    SessionDAO sessionDAO;
    public JoinGameHandler (SessionDAO sessionDAO){this.sessionDAO = sessionDAO;}

    @Override
    public void handle(@NotNull Context context) throws Exception {
        JoinGameRequest joinGameRequest = null;
        try {
            String header = context.header("authorization");
            String body = context.body();
            if (!body.contains("playerColor") || !body.contains("gameID") ||
                    (!body.contains("BLACK") && !body.contains("WHITE")))
            {
                var err = new Gson().toJson(Map.of("message", "Error: bad request"));
                context.status(400);
                context.json(err);
                return;
            }
            com.google.gson.JsonObject json = new Gson().fromJson(body, JsonObject.class);
            String playerColor = json.get("playerColor").getAsString();
            int gameID = json.get("gameID").getAsInt();
            joinGameRequest = new JoinGameRequest(header, playerColor, gameID);
        }
        catch (Exception e) {
            throw new DataAccessException("Miscellaneous error ", e);
        }
        UserService userService = new UserService(sessionDAO);
        GameService gameService = new GameService(sessionDAO);
        try {
            userService.authenticateUser(joinGameRequest.authToken());
            gameService.joinGame(joinGameRequest);
        }
        catch (UnauthorizedException e)
        {
            var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
            context.status(401);
            context.json(body);
        }
        catch (GameNotFoundException e)
        {
            var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
            context.status(400);
            context.json(body);
        }
        catch (AlreadyTakenException e)
        {
            var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
            context.status(403);
            context.json(body);
        }
    }
}
