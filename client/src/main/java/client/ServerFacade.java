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
import model.LoginRequest;
import model.RegisterRequest;

public class ServerFacade {
    String host;
    long port;
    String currUserAuthToken = "";

    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public ServerFacade(String host, int port) throws Exception {
        this.host = host;
        this.port = port;
    }

    public HTTPRequestResponse sendHTTPRequest(Record httpBody, Map<String, String> httpHeader, String path, String httpMethod) {
        HTTPRequestResponse result = null;
        String body = null;
        try {
            String urlString = String.format(Locale.getDefault(), "http://%s:%d%s", host, port, path);
            // create a starter http request
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(new URI(urlString))
                    .timeout(java.time.Duration.ofMillis(5000));

            if (httpHeader != null) {
                for (Map.Entry<String, String> entry : httpHeader.entrySet()) {
                    builder.header(entry.getKey(), entry.getValue());
                }
            }
            if (httpBody != null) {
                body = new Gson().toJson(httpBody);

            }
            switch (httpMethod.toLowerCase()) {
                case "post" -> {
                    builder.POST(HttpRequest.BodyPublishers.ofString(body));
                }
                case "delete" -> {
                    builder.DELETE();
                }
                case "put" -> {
                    builder.PUT(HttpRequest.BodyPublishers.ofString(body));
                }
                case "get" -> {
                    builder.GET();
                }
            }
            HttpRequest request = builder.build();

            HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (httpResponse.statusCode() == 200) {
                result = new HTTPRequestResponse(true, "Action was successful.", httpResponse.body());
            }
            else if (httpResponse.statusCode() == 400) {
                result = new HTTPRequestResponse(false, "The parameters " +
                        "you used are not the correct syntax. " +
                        "Please type help to see the correct command structure.", null);
            }
            else if (httpResponse.statusCode() == 401) {
                result = new HTTPRequestResponse(false, "You are not authorized to do this action. Please register and login first.", null);
            }
            else if (httpResponse.statusCode() == 403) {
                JsonObject obj = JsonParser.parseString(httpResponse.body()).getAsJsonObject();
                String message = obj.get("message").getAsString();      // "Error: something broke"
                String errorText = message.replaceFirst("^Error: ", "");  // "something broke"
                result = new HTTPRequestResponse(false, "That request cannot be completed because " + errorText + ". Please use a different username.", null);
            }
            else {result = new HTTPRequestResponse(false, httpResponse.body(), null);}
        }
        catch (IOException e) {result = new HTTPRequestResponse(false, "Please make sure the the chess server is running.", null);}
        catch (InterruptedException e) {result = new HTTPRequestResponse(false, "Connection to the server was lost. Please try again.", null);}
        catch (URISyntaxException e) {result = new HTTPRequestResponse(false, "Could not connect to the app. Please reach out to the app creator.", null);}
        catch (Exception e) {result = new HTTPRequestResponse(false, e.getMessage(), null);}
        finally {return result;}
    }

    public HTTPRequestResponse register(RegisterRequest regReq) {
        return sendHTTPRequest(regReq, null, "/user", "post");
    }

    public HTTPRequestResponse login(LoginRequest loginReq) {
        HTTPRequestResponse response = sendHTTPRequest(loginReq, null, "/session", "post");
        JsonObject httpBody = JsonParser.parseString(response.httpBody()).getAsJsonObject();
        currUserAuthToken = httpBody.get("authToken").getAsString();
        return response;
    }

    public HTTPRequestResponse logout() {
        Map<String, String> header = new HashMap<>();
        header.put("authorization", currUserAuthToken);
        return sendHTTPRequest(null, header, "/session", "delete");
    }
    public void listGames() {

    }
    public void createGame() {

    }
    public void joinGame() {

    }
    public void observeGame() {

    }

    public void clearApp() {
        HTTPRequestResponse result = sendHTTPRequest(null, null, "/db", "delete");
        System.out.print(result.message());
    }
}

