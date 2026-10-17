package server;

import com.google.gson.Gson;
import dataaccess.LocalSessionDAO;
import handler.RegisterHandler;
import io.javalin.*;
import io.javalin.http.Context;

import java.util.Map;

public class Server {
    // todo: instantiate everything here at the beginning and pass it all through
    public LocalSessionDAO userDAO = new LocalSessionDAO();
    final RegisterHandler regHandler = new RegisterHandler(userDAO);
    private final Javalin javalin;

    public Server() {
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
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage()), "success", false));
        context.status(500);
        context.json(body);
    }

    public int run(int desiredPort) {
        // TODO: for phase 3, I need to instantiate all DAOs here in the server and pass them down through
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
