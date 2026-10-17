package service;

import dataaccess.*;
import model.*;

import java.util.UUID;

public class UserService {
    private SessionDAO sessionDao;
    public UserService(SessionDAO sessionDAO) {
        sessionDao = sessionDAO;
    }

    public void validatePassword() {}
    public void authenticateUser() {}

    public AuthData register(RegisterRequest req) throws DataAccessException {
        UserData user = sessionDao.getUser(req.username());
        if (user != null)
        {
            throw new AlreadyTakenException("username already taken");
        }
        UserData newUserData = new UserData(req.username(), req.password(), req.email());
        sessionDao.createUser(newUserData);
        // we need to create authData
        String authToken = generateToken();
        AuthData newAuthData = new AuthData(authToken, req.username());
        sessionDao.createAuth(newAuthData);
        return newAuthData;
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
