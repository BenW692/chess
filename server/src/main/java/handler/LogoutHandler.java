package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.SessionDAO;
import dataaccess.UnauthorizedException;
import dataaccess.UserNotFoundException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.LogoutRequest;
import org.jetbrains.annotations.NotNull;
import service.UserService;

import java.util.Map;

public class LogoutHandler implements Handler {
    SessionDAO sessionDAO;
    public LogoutHandler (SessionDAO sessionDAO){this.sessionDAO = sessionDAO;}

    @Override
    public void handle(@NotNull Context context) throws Exception {
        LogoutRequest logoutRequest = null;
        try {
            var header = context.header("authorization");
            logoutRequest = new LogoutRequest(header);
        }
        catch (Exception e) {
            throw new DataAccessException("Miscellaneous error ", e);
        }
        if (logoutRequest == null){
            var body = new Gson().toJson(Map.of("message", "Error: bad request"));
            context.status(500);
            context.json(body);
            return;
        }
        UserService userService = new UserService(sessionDAO);
        userService.logout(logoutRequest);
        context.status(200);
    }
}
