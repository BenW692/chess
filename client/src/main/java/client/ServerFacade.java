package client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;
import java.util.Map;

import com.google.gson.Gson;
import model.LoginRequest;
import model.RegisterRequest;

public class ServerFacade {
    String host;
    long port;
    public ServerFacade(String host, int port) throws Exception {
        this.host = host;
        this.port = port;
    }

    public HTTPRequestResponse register(RegisterRequest regReq) {
        HTTPRequestResponse result = null;
        try {
            String urlString = String.format(Locale.getDefault(), "http://%s:%d%s", host, port, "/user");
            var body = new Gson().toJson(regReq);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(urlString))
                    .timeout(java.time.Duration.ofMillis(5000))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (httpResponse.statusCode() == 200) {
                result = new HTTPRequestResponse(true, "You registered successfully.");
            } else if (httpResponse.statusCode() == 400) {
                result = new HTTPRequestResponse(false, "The parameters " +
                        "you tried to register with are not the correct syntax. " +
                        "Please type help to see the correct command structure.");
            }
            else if (httpResponse.statusCode() == 403) {
                result = new HTTPRequestResponse(false, "Someone has already registered with that username. Please use a different username.");
            }
            else {result = new HTTPRequestResponse(false, httpResponse.body());}
        }
        catch (IOException e) {result = new HTTPRequestResponse(false, "Please make sure the the chess server is running.");}
        catch (InterruptedException e) {result = new HTTPRequestResponse(false, "Connection to the server was lost. Please try again.");}
        catch (URISyntaxException e) {result = new HTTPRequestResponse(false, "Could not connect to the app. Please reach out to the app creator.");}
        catch (Exception e) {result = new HTTPRequestResponse(false, e.getMessage());}
        finally {return result;}
    }

    public HTTPRequestResponse login(LoginRequest loginReq) {
        HTTPRequestResponse result = null;
        try {
            String urlString = String.format(Locale.getDefault(), "http://%s:%d%s", host, port, "/user");
            var body = new Gson().toJson(loginReq);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(urlString))
                    .timeout(java.time.Duration.ofMillis(5000))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (httpResponse.statusCode() == 200) {
                result = new HTTPRequestResponse(true, "You registered successfully.");
            } else if (httpResponse.statusCode() == 400) {
                result = new HTTPRequestResponse(false, "The parameters " +
                        "you tried to register with are not the correct syntax. " +
                        "Please type help to see the correct command structure.");
            }
            else if (httpResponse.statusCode() == 403) {
                result = new HTTPRequestResponse(false, "Someone has already registered with that username. Please use a different username.");
            }
            else {result = new HTTPRequestResponse(false, httpResponse.body());}
        }
        catch (IOException e) {result = new HTTPRequestResponse(false, "Please make sure the the chess server is running.");}
        catch (InterruptedException e) {result = new HTTPRequestResponse(false, "Connection to the server was lost. Please try again.");}
        catch (URISyntaxException e) {result = new HTTPRequestResponse(false, "Could not connect to the app. Please reach out to the app creator.");}
        catch (Exception e) {result = new HTTPRequestResponse(false, e.getMessage());}
        finally {return result;}
    }
    public void logout() {

    }
    public void listGames() {

    }
    public void createGame() {

    }
    public void joinGame() {

    }
    public void observeGame() {

    }

    // Create an HttpClient for making requests
    // This should be long-lived and shared, so a static final field is good here
    private static final HttpClient httpClient = HttpClient.newHttpClient();

//    public static void main(String[] args) throws Exception {
//        new ServerFacade().get("localhost", 8080, "/name");
//    }

    public void clearApp() {
        String result = "";
        try {
            String urlString = String.format(Locale.getDefault(), "http://%s:%d%s", host, port, "/db");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(urlString))
                    .timeout(java.time.Duration.ofMillis(5000))
                    .DELETE()
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (httpResponse.statusCode() == 200) {
                result = "The application database was cleared";
            }
        }
        catch (IOException e) {result = "Please make sure the the chess server is running.";}
        catch (InterruptedException e) {result = "Connection to the server was lost. Please try again.";}
        catch (URISyntaxException e) {result = "Could not connect to the app. Please reach out to the app creator.";}
        catch (Exception e) {result = e.getMessage();}
        finally {System.out.print(result);}
    }
}

