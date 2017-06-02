package link;

import java.util.ArrayList;
import javax.sql.DataSource;

public interface LinkDAO {
    
    public void setDataSource(DataSource ds);
    
    public void createLink(String longLink, String description, String userLogin, ArrayList <String> userTags);
    
    public String getLongLink(String shortLink);
    
    public Link getAllForLink(String shortLink);
    
    public ArrayList<Link> getAllForLinkByLogin(String login);
    
    public ArrayList<Link> getLinksByTag(String tag);

    public boolean linkInBase(String longLink);
    
    public boolean linkInBase(String shortLink, String longLink);
    
    public void updateLink(Link link);
    
    public void deleteLink(String longLink);
}
