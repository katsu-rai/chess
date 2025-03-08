package dataaccess;

import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.sql.SQLException;

public class SQLUser implements UserDAO {

    public SQLUser(){
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException ex) {
            throw new RuntimeException(ex);
        }

        try (var connection = DatabaseManager.getConnection()) {
            var createTestTable = """            
                CREATE TABLE if NOT EXISTS user (
                                username VARCHAR(50) NOT NULL,
                                password VARCHAR(50) NOT NULL,
                                email VARCHAR(100),
                                PRIMARY KEY (username)
                                )""";
            try (var createTableStatement = connection.prepareStatement(createTestTable)) {
                createTableStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException{
        return null;
    }

    @Override
    public void createUser(UserData user) throws Exception{

    }

    @Override
    public boolean authenticate(String username, String password){
        return false;
    }

    @Override
    public void clear(){

    }
}