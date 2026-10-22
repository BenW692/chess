package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.SessionDAO;
import dataaccess.UnauthorizedException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.ListGameRequest;
import model.ListGameResult;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.UserService;

import java.util.List;
import java.util.Map;

public class ListGamesHandler implements Handler {
    SessionDAO sessionDAO;
    public ListGamesHandler (SessionDAO sessionDAO){this.sessionDAO = sessionDAO;}

    @Override
    public void handle(@NotNull Context context) throws Exception {
        ListGameRequest listGameRequest = null;
        try {
            String header = context.header("authorization");
            listGameRequest = new ListGameRequest(header);
        }
        catch (Exception e) {
            throw new DataAccessException("Miscellaneous error ", e);
        }
        UserService userService = new UserService(sessionDAO);
        GameService gameService = new GameService(sessionDAO);

        userService.authenticateUser(listGameRequest.authToken());
        ListGameResult listGameResult = gameService.listGames();
        context.json(new Gson().toJson(listGameResult));
        context.status(200);
        
    }
}
