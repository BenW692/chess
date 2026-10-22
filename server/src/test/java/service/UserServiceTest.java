package service;

import dataaccess.AlreadyTakenException;
import dataaccess.LocalSessionDAO;
import dataaccess.UnauthorizedException;
import dataaccess.UserNotFoundException;
import model.*;
import org.eclipse.jetty.util.log.Log;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    LocalSessionDAO sessionDao;
    UserService userService;
    UserData tempUserData;
    LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        sessionDao = new LocalSessionDAO();
        userService = new UserService(sessionDao);
        tempUserData = new UserData("testU", "test", "yungthug@myspace.com");
        loginRequest = new LoginRequest(tempUserData.username(), tempUserData.password());
    }

    @AfterEach
    void tearDown() {
        sessionDao.clearApp();
    }

    @Test
    void alreadyTakenRegister() {
        sessionDao.createUser(tempUserData);
        RegisterRequest tempRegisterRequest = new RegisterRequest(tempUserData.username(),
                tempUserData.password(), tempUserData.email());
        assertThrows(AlreadyTakenException.class, () -> {userService.register(tempRegisterRequest);});
    }

    @Test
    void goodRegister() {
        RegisterRequest tempRegisterRequest = new RegisterRequest(tempUserData.username(),
                tempUserData.password(), tempUserData.email());
        userService.register(tempRegisterRequest);
        assertNotNull(sessionDao.getUserWUsername(tempRegisterRequest.username()));
    }

    @Test
    void userNotFoundLogin() {
        assertThrows(UserNotFoundException.class, () -> {userService.login(loginRequest);});
    }

    @Test
    void badPasswordLogin() {
        sessionDao.createUser(tempUserData);
        LoginRequest badLoginRequest = new LoginRequest(tempUserData.username(), "wrongpassword");
        assertThrows(UnauthorizedException.class, () -> {userService.login(badLoginRequest);});
    }

    @Test
    void goodLogin() {
        sessionDao.createUser(tempUserData);
        LoginRequest loginRequest = new LoginRequest(tempUserData.username(), tempUserData.password());
        LoginResult loginResult = userService.login(loginRequest);
        assertNotNull(sessionDao.getAuth(loginResult.authToken()));
    }

    @Test
    void badAuthLogout() {
        String authToken = "1234";
        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        assertThrows(UnauthorizedException.class, () -> {userService.logout(logoutRequest);});
    }

    @Test
    void goodLogout() {
        RegisterRequest tempRegisterRequest = new RegisterRequest(tempUserData.username(),
                tempUserData.password(), tempUserData.email());
        String authToken = userService.register(tempRegisterRequest).authToken();
        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        userService.logout(logoutRequest);
        assertNull(sessionDao.getAuth(authToken));
    }


    @Test
    void badAuthenticateUser() {
        String authToken = "1234";
        assertThrows(UnauthorizedException.class, () -> {userService.authenticateUser(authToken);});
    }

    @Test
    void goodAuthenticateUser() {
        RegisterRequest tempRegisterRequest = new RegisterRequest(tempUserData.username(),
                tempUserData.password(), tempUserData.email());
        String authToken = userService.register(tempRegisterRequest).authToken();
        userService.authenticateUser(authToken);
    }
}