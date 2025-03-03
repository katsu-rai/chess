package dataaccess;
import model.UserData;

public interface UserDAO {

    UserData getUser(String username) throws DataAccessException;
    void createUser(UserData user) throws Exception;
    boolean authenticate(String username, String password);
    void clear();
}
