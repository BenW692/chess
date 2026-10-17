package handler;

import dataaccess.SessionDAO;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class CreateGameHandler implements Handler {
    SessionDAO sessionDAO;
    public CreateGameHandler (SessionDAO sessionDAO){this.sessionDAO = sessionDAO;}

    @Override
    public void handle(@NotNull Context context) throws Exception {

    }
}
