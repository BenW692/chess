package client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.*;

public class ServerFacade {
    String currUserAuthToken = "";
    Map<String, String> header = new HashMap<>();
    private record CreateGameHttpRequest(String gameName) {}
    private record JoinGameHttpRequest(String playerColor, int gameID) {}
    String host;
    long port;
    HttpCommunicator httpCommunicator;
    WebSocketCommunicator webSocketCommunicator;

    public ServerFacade(String host, int port) throws Exception {
        this.host = host;
        this.port = port;
        httpCommunicator = new HttpCommunicator(host, port);
    }

    private HTTPRequestResponse sendHTTPRequest(Record httpBody, Map<String, String> httpHeader, String path, String httpMethod){
        return httpCommunicator.sendHTTPRequest(httpBody, httpHeader, path, httpMethod);
    }

    public HTTPRequestResponse register(RegisterRequest regReq) {
        return sendHTTPRequest(regReq, null, "/user", "post");
    }

    public HTTPRequestResponse login(LoginRequest loginReq) {
        HTTPRequestResponse response = sendHTTPRequest(loginReq, null, "/session", "post");
        if (response.success()){
            JsonObject httpBody = JsonParser.parseString(response.httpBody()).getAsJsonObject();
            currUserAuthToken = httpBody.get("authToken").getAsString();
            header.put("authorization", currUserAuthToken);
        }
        return response;
    }

    public HTTPRequestResponse logout() {
        if (header.isEmpty()) {
            return new HTTPRequestResponse(false, "You are unauthorized to do this action. Please login first.", null);
        }
        return sendHTTPRequest(null, header, "/session", "delete");
    }

    public HTTPRequestResponse listGames() {
        if (header.isEmpty()) {
            return new HTTPRequestResponse(false, "You are unauthorized to do this action. Please login first.", null);
        }
        return sendHTTPRequest(null, header, "/game", "get");
    }
    public HTTPRequestResponse createGame(String gameName) {
        if (header.isEmpty()) {
            return new HTTPRequestResponse(false, "You are unauthorized to do this action. Please login first.", null);
        }
        CreateGameHttpRequest body = new CreateGameHttpRequest(gameName);
        return sendHTTPRequest(body, header, "/game", "post");
    }

    public HTTPRequestResponse joinGame(String playerColor, int gameID) {
        if (header.isEmpty()) {
            return new HTTPRequestResponse(false, "You are unauthorized to do this action. Please login first.", null);
        }
        JoinGameHttpRequest gameReq = new JoinGameHttpRequest(playerColor.toUpperCase(), gameID);
        HTTPRequestResponse res = sendHTTPRequest(gameReq, header, "/game", "put");
        webSocketCommunicator = new WebSocketCommunicator(host, port);
        return res;
    }

    public void observeGame() {
    }

    public void clearApp() {
        HTTPRequestResponse result = sendHTTPRequest(null, null, "/db", "delete");
        header.clear();
        if (result.success()) {System.out.print(result.message());}
        else {System.out.print("There was an error clearing the database. Please ensure the server is still running.");}
    }
}

