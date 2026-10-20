package handler;

import dataaccess.DataAccessException;
import dataaccess.SessionDAO;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class ClearAppHandler implements Handler {
    SessionDAO sessionDAO;
    public ClearAppHandler (SessionDAO sessionDAO){this.sessionDAO = sessionDAO;}

    @Override
    public void handle(@NotNull Context context) throws Exception {
        try {
            sessionDAO.clearApp();
            context.status(200);
        }
        catch (Exception e) {
            throw new DataAccessException("Miscellaneous error ", e);
        }
    }
}
