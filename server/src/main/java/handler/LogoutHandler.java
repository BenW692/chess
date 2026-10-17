package handler;

import dataaccess.SessionDAO;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class LogoutHandler implements Handler {
    SessionDAO sessionDAO;
    public LogoutHandler (SessionDAO sessionDAO){this.sessionDAO = sessionDAO;}

    @Override
    public void handle(@NotNull Context context) throws Exception {

    }
}
