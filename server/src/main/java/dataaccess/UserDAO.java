package dataaccess;

import model.UserData;

public interface UserDAO {
    void createUser(); // this will probably throw an exception if we find a user when we try to create
    UserData getUser();
}
