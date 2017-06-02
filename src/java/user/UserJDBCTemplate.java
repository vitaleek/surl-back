package user;

import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public class UserJDBCTemplate implements UserDAO {
    
   private DataSource dataSource;
   private JdbcTemplate jdbcTemplateObject;
   
   public void setDataSource(DataSource dataSource) {
      this.dataSource = dataSource;
      this.jdbcTemplateObject = new JdbcTemplate(dataSource);
   }
   @Override
   public void createUser(String login, String password){
      String SQL = "INSERT INTO users (id, login, password) VALUES (?, ?, ?)";
      int ii = jdbcTemplateObject.queryForInt("select MAX(id) FROM users");// Получение индекса последней строки таблицы
      jdbcTemplateObject.update(SQL, ii+1, login, password);
      System.out.println("Created Record User:  " + login + " password :  ********** ");
      return;
   }
   
   public User getUser(String login){
       String SQL = "select * from users where login = ?";
      User user = jdbcTemplateObject.queryForObject(SQL, 
                        new Object[]{login}, new UserMapper());
      return user;
   }
   
   public ArrayList<User> listUsers(){
       String SQL = "select * from users";
      ArrayList <User> users = (ArrayList<User>)jdbcTemplateObject.query(SQL, 
                                new UserMapper());
      return users;
   }

    
}
