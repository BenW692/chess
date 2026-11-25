package handler;

import com.google.gson.Gson;
import dataaccess.SessionDAO;
import dataaccess.UnauthorizedException;
import io.javalin.websocket.*;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.ServerMessage;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private SessionDAO sessionDAO;

    public WebSocketHandler(SessionDAO sessionDAO)
    {
        this.sessionDAO = sessionDAO;
    }

    @Override
    public void handleClose(@NotNull WsCloseContext wsCloseContext) throws Exception {

    }

    @Override
    public void handleConnect(@NotNull WsConnectContext wsConnectContext) throws Exception {

    }

    @Override
    public void handleMessage(WsMessageContext wsMessageContext) throws Exception {
        int gameId = -1;
        Session session = wsMessageContext.session;

        try {
            UserGameCommand command = new Gson().fromJson(
                    wsMessageContext.message(), UserGameCommand.class);
            gameId = command.getGameID();
            String username = sessionDAO.getUserWAuth(command.getAuthToken()).username();
            saveSession(gameId, session);

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, command);
                case MAKE_MOVE -> makeMove(session, username, (MakeMoveCommand) command);
                case LEAVE -> leaveGame(session, username, command);
                case RESIGN -> resign(session, username, command);
            }
        }
        catch (UnauthorizedException e) {
            sendMessage(session, gameId, new ErrorMessage("Error: unauthorized"));
        }
        catch (Exception e) {
            e.printStackTrace();
            sendMessage(session, gameId, new ErrorMessage("Error: " + e.getMessage()));
        }
    }

    public void sendMessage(Session session, int gameID, ServerMessage msg) throws Exception {
        session.getRemote().sendString(msg.toString());
    }

    private void connect(Session session, String username, UserGameCommand command) {
//        try {
//            var action = new Action(Action.Type.CONNECT, )
//        }
    }

    private void resign(Session session, String username, UserGameCommand command) {

    }

    private void leaveGame(Session session, String username, UserGameCommand command) {
    }

    private void makeMove(Session session, String username, MakeMoveCommand command) {
    }

    private String getUsername(String authToken) {
        return "";
    }

    private void saveSession(int gameId, Session session) {
    }
}