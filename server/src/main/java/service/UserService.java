package service;

import dataaccess.*;
import model.*;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class UserService {
    private SessionDAO sessionDao;
    public UserService(SessionDAO sessionDAO) {
        sessionDao = sessionDAO;
    }

    public void validatePassword() {}
    public void authenticateUser() {}

    public RegisterResult register(RegisterRequest req) throws AlreadyTakenException {
        UserData user = sessionDao.getUser(req.username());
        if (user != null)
        {
            throw new AlreadyTakenException("username already taken");
        }
        UserData newUserData = new UserData(req.username(), req.password(), req.email());
        sessionDao.createUser(newUserData);
        // we need to create authData
        AuthData newAuthData = createAndSaveAuthToken(req.username());
        return new RegisterResult(newAuthData.username(), newAuthData.authToken());
    }

    private AuthData createAndSaveAuthToken(String username) {
        String authToken = generateToken();
        AuthData newAuthData = new AuthData(authToken, username);
        sessionDao.createAuth(newAuthData);
        return newAuthData;
    }

    public LoginResult login(LoginRequest req) throws UserNotFoundException {
        UserData user = sessionDao.getUser(req.username());
        if (user == null)
        {
            throw new UserNotFoundException("bad request. User was not found in the database.");
        }
        // check if provided password matches password in the database
        if (!req.password().equals(user.password())){
            throw new UnauthorizedException("unauthorized. Password was incorrect.");
        }
        AuthData newAuthData = createAndSaveAuthToken(req.username());
        return new LoginResult(newAuthData.username(), newAuthData.authToken());
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
