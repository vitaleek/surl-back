package user;

import java.util.List;
import javax.sql.DataSource;

public interface UserDAO {
    
    public void setDataSource(DataSource ds);
    
    public void createUser(String login, String password);
    
    public User getUser(String login);
    
    public List<User> listUsers();
    
    
}
