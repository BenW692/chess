package handler;
import com.google.gson.Gson;
import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import dataaccess.SessionDAO;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.AuthData;
import model.RegisterRequest;
import service.UserService;

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
//            ctx.status(500).result("Error: Bad Request");
            throw new DataAccessException("Miscellaneous error ", e);
        }
        if (request.username() == null || request.password() == null || request.email() == null){
            throw new BadRequestException("Http register request did not have sufficient data in the body.");
        }
        // this is where I would call the service
        UserService userService = new UserService(sessionDAO);
        AuthData authData = userService.register(request);
        ctx.status(200).result(new Gson().toJson(authData));
    }
}
