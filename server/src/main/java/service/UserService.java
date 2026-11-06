package service;

import dataaccess.*;
import model.*;

import java.util.UUID;

public class UserService {
    private SessionDAO sessionDao;
    public UserService(SessionDAO sessionDAO) {
        sessionDao = sessionDAO;
    }

    public RegisterResult register(RegisterRequest req) throws AlreadyTakenException, DataAccessException {
        UserData user = sessionDao.getUserWUsername(req.username());
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

    private AuthData createAndSaveAuthToken(String username) throws DataAccessException {
        String authToken = generateToken();
        AuthData newAuthData = new AuthData(authToken, username);
        sessionDao.createAuth(newAuthData);
        return newAuthData;
    }

    public LoginResult login(LoginRequest req) throws UserNotFoundException, DataAccessException {
        UserData user = sessionDao.getUserWUsername(req.username());
        if (user == null)
        {
            throw new UserNotFoundException("bad request. User was not found in the database.");
        }
        // check if provided password matches password in the database
        if (!sessionDao.checkPassword(req.password(), req.username())){
            throw new UnauthorizedException("unauthorized. Password was incorrect.");
        }
        AuthData newAuthData = createAndSaveAuthToken(req.username());
        return new LoginResult(newAuthData.username(), newAuthData.authToken());
    }

    public void logout(LogoutRequest req) throws UnauthorizedException, DataAccessException {
        authenticateUser(req.authToken());
        sessionDao.deleteAuth(req.authToken());
    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public void authenticateUser(String authToken) throws DataAccessException {
        AuthData authData = sessionDao.getAuth(authToken);
        if (authData == null)
        {
            throw new UnauthorizedException("unauthorized. This user is not logged in.");
        }
    }
}
