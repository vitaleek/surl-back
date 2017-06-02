
package link;

import java.sql.SQLException;
import java.util.ArrayList;
import javax.sql.DataSource;
import logic.Core;
import org.springframework.jdbc.core.JdbcTemplate;
import java.sql.ResultSet;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.dao.EmptyResultDataAccessException;

public class LinkJDBCTemplate implements LinkDAO {
    
   private DataSource dataSource;
   private JdbcTemplate jdbcTemplateObject;

   @Override
   public void setDataSource(DataSource dataSource) {
      this.dataSource = dataSource;
      this.jdbcTemplateObject = new JdbcTemplate(dataSource);
   }

   @Override
   public void createLink(String longLink, String description, String userLogin, ArrayList <String> userTags){
       String SQL = "INSERT INTO link (ID, LONG_LINK, SHORT_LINK, USER_LOGIN, DESCRIPTION,  REDIRECT) VALUES (?, ?, ?, ?, ?, ?)";
//---------- Формирование массива тегов для передачи в таблицу БД ----------------
       String SQLTags = "ARRAY [";
        int a = 0;
        for (String s: userTags){
            SQLTags=SQLTags+"\'"+s+"\'";
            a++;
            if (a!=userTags.size()){
                SQLTags = SQLTags+", ";
            }
            else { 
                SQLTags = SQLTags + "]";
            }
        }
        String shortLink = Core.generateShortLink();
        //------------------Check is in base----------------------
        ArrayList<String> list = new ArrayList<String>();
        boolean isInBase = false;
        SQL = "SELECT short_link FROM link";
            try{  
                jdbcTemplateObject.queryForObject(SQL, new RowMapper<ArrayList<String>>() {
                    
                    public ArrayList<String> mapRow(ResultSet rs, int i) throws SQLException {
                        while (rs.next()){
                            list.add(rs.getString("short_link"));
                        }
                        return list;
                    }
                    });
            }
            catch (Exception e){
                System.out.println("Error: "+ e.getMessage());
                e.getStackTrace();
            }
            for (int v = 0; v < list.size(); v++){
                if (list.get(v).equals(shortLink)){
                    isInBase = true;
                }
            }
        //--------------------------------
        if (isInBase) shortLink = Core.generateShortLink();
        int id = jdbcTemplateObject.queryForInt("select MAX(id) FROM link");// Получение индекса последней строки таблицы
        jdbcTemplateObject.update(SQL, ++id, longLink, Core.generateShortLink(),  userLogin,  description,  0);// Запись параметров в БД, кроме массива тегов
        String SQLT = "UPDATE link SET tags="+SQLTags +" WHERE ID=(?)";// Запись массива тегов
        jdbcTemplateObject.update(SQLT, id);// Запись массива тегов

       }
       
   @Override
   public String getLongLink(String shortLink){
           String SQL = "SELECT*FROM link WHERE short_link="+"\'"+shortLink+ "\'";
           Link l = new Link();
           try{  
                jdbcTemplateObject.queryForObject(SQL, new RowMapper<Link>() {
                    @Override
                    public Link mapRow(ResultSet rs, int i) throws SQLException {
                        l.setLongLink(rs.getString("LONG_LINK"));
                        l.setRedirect(rs.getInt("REDIRECT"));
                        return l;
                    }
                });   
           }
           catch (EmptyResultDataAccessException ex){
               System.out.println("No link in base!");
           }
           catch(Exception e){
               System.out.println(e.getMessage());
               e.printStackTrace();
           }
           if (l.getLongLink()!= null) {
                int a = l.getRedirect();
                a++;//Увеличивает количество переходов (статистику) на единицу
                jdbcTemplateObject.update("UPDATE link SET redirect ="+ a+ " WHERE short_link ="+"\'"+ shortLink +"\'");// Записывает статистику в базу
           }
          return l.getLongLink();
       }
       
   @Override
   public Link getAllForLink(String shortLink){
           Link l = new Link();
           String SQL = "SELECT*FROM LINK WHERE SHORT_LINK="+"\'"+shortLink+ "\'";
           try{  
                jdbcTemplateObject.queryForObject(SQL, new RowMapper<Link>() {
                    @Override
                    public Link mapRow(ResultSet rs, int i) throws SQLException {
                        l.setIdLink(rs.getInt("ID"));
                        l.setLongLink(rs.getString("LONG_LINK"));
                        l.setShortLink(rs.getString("SHORT_LINK"));
                        l.setUserLogin(rs.getString("USER_LOGIN"));
                        l.setDescription(rs.getString("DESCRIPTION"));
                        l.setRedirect(rs.getInt("REDIRECT"));
                        // ------- Извлечение массива тегов -------------
                        String[] a = (String[])rs.getArray("TAGS").getArray();
                        ArrayList<String> tags = new ArrayList<String>();
                        for (int q = 0; q < a.length; q++){
                            tags.add(a[q]);
                        }
                        l.setTags(tags);
                        return l;
                    }
                });   
           }
           catch(Exception e){
               System.out.println(e.getMessage());
               e.printStackTrace();
           }
           return l;
       }

   @Override
   public void updateLink(Link link){
       
       //--------------------
       String SQLTags = "ARRAY [";
        int a = 0;
        for (String s: link.getTags()){
            SQLTags=SQLTags+"\'"+s+"\'";
            a++;
            if (a!=link.getTags().size()){
                SQLTags = SQLTags+", ";
            }
            else { 
                SQLTags = SQLTags + "]";
            }
        }
       //---------------------
       String SQL = "UPDATE link SET description =\'" + link.getDescription()+ "\', tags = " + SQLTags+ " WHERE SHORT_LINK=\'"+ link.getShortLink()+"\'";
       System.out.println("SQL= "+ SQL);
       try{  
                jdbcTemplateObject.execute(SQL);
       } catch (Exception e){
           System.out.println(e.getMessage());
               e.printStackTrace();
       }
   }
   
   @Override
   public ArrayList<Link> getAllForLinkByLogin(String login){
           String SQL = "SELECT*FROM link WHERE user_login='"+ login+"';";
           ArrayList<Link> linksList = new ArrayList<Link>();
           try{  
                jdbcTemplateObject.queryForObject(SQL, new RowMapper<ArrayList<Link>>() {
                    @Override
                    public ArrayList<Link> mapRow(ResultSet rs, int i) throws SQLException {
                        Link k = new Link();
                        //---------Обработка  набора первой строки таблицы, которую почему-то пропускает перебор while(rs.next())
                        k.setDescription(rs.getString("description"));
                        k.setRedirect(rs.getInt("redirect"));
                        k.setLongLink(rs.getString("long_link"));
                        k.setShortLink(rs.getString("short_link"));
                        k.setIdLink(rs.getInt("ID"));
                        String[] b = (String[])rs.getArray("TAGS").getArray();
                        ArrayList<String> tags = new ArrayList<String>();
                        for (int w = 0; w < b.length; w++){
                            tags.add(b[w]);
                        }
                        k.setTags(tags);
                        linksList.add(k);
                        //---------Обработка  наборов остальных строк
                        while (rs.next()){
                            Link l = new Link();
                            l.setDescription(rs.getString("description"));
                            l.setRedirect(rs.getInt("redirect"));
                            l.setLongLink(rs.getString("long_link"));
                            l.setShortLink(rs.getString("short_link"));
                            l.setIdLink(rs.getInt("ID"));
                            b = (String[])rs.getArray("TAGS").getArray();
                            tags = new ArrayList<String>();
                            for (int w = 0; w < b.length; w++){
                                tags.add(b[w]);
                            }
                            l.setTags(tags);
                            linksList.add(l);
                            l = new Link();
                        }
                        return linksList;
                    }
                });   
           }
           catch(Exception e){
               System.out.println(e.getMessage());
               e.printStackTrace();
           }
           return linksList;
           
           
       }

   @Override
   public ArrayList<Link> getLinksByTag(String tag){
           String SQL = "SELECT*FROM link WHERE '"+tag+"' = any (tags);";
           ArrayList<Link> linksList = new ArrayList<Link>();
           try{  
                jdbcTemplateObject.queryForObject(SQL, new RowMapper<ArrayList<Link>>() {
                    @Override
                    public ArrayList<Link> mapRow(ResultSet rs, int i) throws SQLException {
                        Link k = new Link();
                        //---------Обработка  набора первой строки таблицы, которую почему-то пропускает перебор while(rs.next())
                        k.setDescription(rs.getString("description"));
                        k.setRedirect(rs.getInt("redirect"));
                        k.setLongLink(rs.getString("long_link"));
                        k.setShortLink(rs.getString("short_link"));
                        k.setIdLink(rs.getInt("ID"));
                        String[] b = (String[])rs.getArray("TAGS").getArray();
                        ArrayList<String> tags = new ArrayList<String>();
                        for (int w = 0; w < b.length; w++){
                            tags.add(b[w]);
                        }
                        k.setTags(tags);
                        linksList.add(k);
                        //---------Обработка  наборов остальных строк
                        while (rs.next()){
                            Link l = new Link();
                            l.setDescription(rs.getString("description"));
                            l.setRedirect(rs.getInt("redirect"));
                            l.setLongLink(rs.getString("long_link"));
                            l.setShortLink(rs.getString("short_link"));
                            l.setIdLink(rs.getInt("ID"));
                            b = (String[])rs.getArray("TAGS").getArray();
                            tags = new ArrayList<String>();
                            for (int w = 0; w < b.length; w++){
                                tags.add(b[w]);
                            }
                            l.setTags(tags);
                            linksList.add(l);
                            l = new Link();
                        }
                        return linksList;
                    }
                });   
           }
           catch(Exception e){
               System.out.println(e.getMessage());
               e.printStackTrace();
           }
           return linksList;
       }
   
   @Override
   public boolean linkInBase(String longLink){
        ArrayList<String> list = new ArrayList<String>();
        boolean isInBase = false;
        String SQL = "SELECT long_link FROM link";
            try{  
                jdbcTemplateObject.queryForObject(SQL, new RowMapper<ArrayList<String>>() {
                    public ArrayList<String> mapRow(ResultSet rs, int i) throws SQLException {
                        while (rs.next()){
                            list.add(rs.getString("long_link"));
                        }
                        return list;
                    }
                    });
            }
            catch (Exception e){
                System.out.println("Error: "+ e.getMessage());
                e.getStackTrace();
            }
            for (int v = 0; v < list.size(); v++){
                if (list.get(v).equals(longLink)){
                    isInBase = true;
                }
            }
            return isInBase;
   }
    
   @Override
   public boolean linkInBase(String shortLink, String longLink){
        ArrayList<String> list = new ArrayList<String>();
        boolean isInBase = false;
        String SQL = "SELECT short_link FROM link";
            try{  
                jdbcTemplateObject.queryForObject(SQL, new RowMapper<ArrayList<String>>() {
                    
                    public ArrayList<String> mapRow(ResultSet rs, int i) throws SQLException {
                        while (rs.next()){
                            list.add(rs.getString("short_link"));
                        }
                        return list;
                    }
                    });
            }
            catch (Exception e){
                System.out.println("Error: "+ e.getMessage());
                e.getStackTrace();
            }
            for (int v = 0; v < list.size(); v++){
                if (list.get(v).equals(shortLink)){
                    isInBase = true;
                }
            }
            return isInBase;
   }
    
   @Override
   public void deleteLink(String longLink){
       jdbcTemplateObject.execute("DELETE FROM link WHERE long_link =\'"+longLink+"\'");
   }
  
}
