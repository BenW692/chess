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
import service.UserService;

import java.util.Map;

public class RegisterHandler implements Handler{
    private SessionDAO sessionDAO;
    public RegisterHandler (SessionDAO sessionDAO) {
        this.sessionDAO = sessionDAO;
    }
    @Override
    public void handle(Context ctx) throws DataAccessException {
        RegisterRequest request = null;
        try {
            var body = ctx.body();
            request = new Gson().fromJson(body, RegisterRequest.class);
        }
        catch (Exception e) {
            throw new DataAccessException("Miscellaneous error ", e);
        }
        if (request.username() == null || request.password() == null || request.email() == null){
            var body = new Gson().toJson(Map.of("message", "Error: bad request"));
            ctx.status(400);
            ctx.json(body);
            return;
        }
        // this is where I would call the service
        UserService userService = new UserService(sessionDAO);
        try {
            AuthData authData = userService.register(request);
            ctx.status(200).json(new Gson().toJson(authData));
        }
        catch (AlreadyTakenException e)
        {
            var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
            ctx.status(403);
            ctx.json(body);
        }

    }
}
