package user;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class UserMapper implements RowMapper<User> {
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
      User user = new User();
      user.setIdUser(rs.getInt("id"));
      user.setLogin(rs.getString("login"));
      user.setPassword(rs.getString("password"));
      return user;
   }
}
