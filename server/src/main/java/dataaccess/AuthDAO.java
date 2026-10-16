package dataaccess;

import model.AuthData;

public interface AuthDAO {
    boolean createAuth(); //will probably throw something
    AuthData getAuth();
    boolean deleteAuth();
}
