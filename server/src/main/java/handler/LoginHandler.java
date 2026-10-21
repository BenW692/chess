package handler;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.LoginRequest;
import model.LoginResult;
import model.RegisterResult;
import org.jetbrains.annotations.NotNull;
import service.UserService;

import java.util.Map;

public class LoginHandler implements Handler {
    SessionDAO sessionDAO;
    public LoginHandler (SessionDAO sessionDAO){this.sessionDAO = sessionDAO;}

    @Override
    public void handle(@NotNull Context context) throws Exception {
        LoginRequest loginRequest = null;
        try {
            var body = context.body();
            loginRequest = new Gson().fromJson(body, LoginRequest.class);
        }
        catch (Exception e) {
            throw new DataAccessException("Miscellaneous error ", e);
        }
        if (loginRequest.username() == null || loginRequest.password() == null){
            var body = new Gson().toJson(Map.of("message", "Error: bad request"));
            context.status(400);
            context.json(body);
            return;
        }
        UserService userService = new UserService(sessionDAO);
        try {
            LoginResult loginResult = userService.login(loginRequest);
            context.status(200).json(new Gson().toJson(loginResult));
        }
        catch (UserNotFoundException e)
        {
            var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
            context.status(401);
            context.json(body);
        }
        
    }
}
