package server;

import com.google.gson.Gson;
import dataaccess.AlreadyTakenException;
import dataaccess.LocalSessionDAO;
import dataaccess.SessionDAO;
import dataaccess.UnauthorizedException;
import handler.*;
import io.javalin.*;
import io.javalin.http.Context;
import model.CreateGameResult;
import model.ListGameRequest;

import java.util.Map;

public class Server {
    public SessionDAO sessionDAO;
    private final Javalin javalin;

    public Server ()
    {
        this.sessionDAO = new LocalSessionDAO();
        final RegisterHandler regHandler = new RegisterHandler(sessionDAO);
        final ClearAppHandler clearAppHandler = new ClearAppHandler(sessionDAO);
        final LoginHandler loginHandler = new LoginHandler(sessionDAO);
        final LogoutHandler logoutHandler = new LogoutHandler(sessionDAO);
        final ListGamesHandler listGamesHandler = new ListGamesHandler(sessionDAO);
        final CreateGameHandler createGameHandler = new CreateGameHandler(sessionDAO);
        final JoinGameHandler joinGameHandler = new JoinGameHandler(sessionDAO);
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        // Register your endpoints and exception handlers here.
        javalin.post("/user", regHandler);
        javalin.delete("/db", clearAppHandler);
        javalin.post("/session", loginHandler);
        javalin.delete("/session", logoutHandler);
        javalin.get("/game", listGamesHandler);
        javalin.post("/game", createGameHandler);
        javalin.put("/game", joinGameHandler);
        javalin.exception(Exception.class, this::exceptionHandler);
    }

    private void exceptionHandler(Exception e, Context context) {
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
        if (e instanceof UnauthorizedException)
        {
            context.status(401);
        }
        else if (e instanceof AlreadyTakenException)
        {
            context.status(403);
        }
        else {
            context.status(500);
        }
        context.json(body);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
