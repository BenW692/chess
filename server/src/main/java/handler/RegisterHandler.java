package handler;
import com.google.gson.Gson;
import dataaccess.AlreadyTakenException;
import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import dataaccess.SessionDAO;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.AuthData;
import model.RegisterRequest;
import model.RegisterResult;
import service.UserService;

import java.util.Map;

public class RegisterHandler implements Handler{
    private SessionDAO sessionDAO;
    public RegisterHandler (SessionDAO sessionDAO) {
        this.sessionDAO = sessionDAO;
    }
    @Override
    public void handle(Context context) throws DataAccessException {
        RegisterRequest registerRequest = null;
        try {
            var body = context.body();
            registerRequest = new Gson().fromJson(body, RegisterRequest.class);
        }
        catch (Exception e) {
            throw new DataAccessException("Miscellaneous error ", e);
        }
        if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null){
            var body = new Gson().toJson(Map.of("message", "Error: bad request"));
            context.status(400);
            context.json(body);
            return;
        }
        UserService userService = new UserService(sessionDAO);
        try {
            RegisterResult registerResult = userService.register(registerRequest);
            context.status(200).json(new Gson().toJson(registerResult));
        }
        catch (AlreadyTakenException e)
        {
            var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
            context.status(403);
            context.json(body);
        }

    }
}
