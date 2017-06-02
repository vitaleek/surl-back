package link;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.springframework.jdbc.core.RowMapper;

public class LinkMapper implements RowMapper<Link>{
    public Link mapRow(ResultSet rs, int rowNum) throws SQLException {
      Link link = new Link();
      link.setIdLink(rs.getInt("ID"));
      link.setLongLink(rs.getString("LONG_LINK"));
      link.setShortLink(rs.getString("SHORT_LINK"));
      link.setUserLogin(rs.getString("USER_LOGIN"));
      link.setDescription(rs.getString("DESCRIPTION"));
      link.setRedirect(rs.getInt("REDIRECT"));
      // ------- Извлечение массива тегов -------------
      Array a = rs.getArray("TAGS");
      ArrayList<String> tags = new ArrayList<String>();
      tags = (ArrayList<String>)a;
      link.setTags(tags);
      
      
      
      return link;
   }
}
