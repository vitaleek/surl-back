package link;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Link {
    public static int linkID = 0;
    @JsonProperty("Object")
    public String obj = "Link";
    
    @JsonIgnore
    private int idLink;
    @JsonProperty("LongLink")
    private String longLink;
    @JsonProperty("ShortLink")
    private String shortLink;
    @JsonProperty("UserLogin")
    private String userLogin;
    @JsonProperty("Description")
    private String description;
    @JsonProperty("Tags")
    private ArrayList<String> tags  = new ArrayList<String>();
    @JsonProperty("Redirect")
    private int redirect = 0;

    
    public Link(){
        
    }
    public String toString(){
        return "Link #"+this.getIdLink()+" shorted by user #"+this.getUserLogin()+
                " description: "+this.getDescription()+"; redirected "+this.getRedirect()+" times, "+
                "your shorted-link is    "+ this.getShortLink();
    }
    
    public String getLongLink() {
        return longLink;
    }

    public void setLongLink(String longLink) {
        this.longLink = longLink;
    }

    public String getShortLink() {
        return shortLink;
    }

    public void setShortLink(String shortLink) {
        this.shortLink = shortLink;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public int getRedirect() {
        return redirect;
    }

    public void setRedirect(int redirect) {
        this.redirect = redirect;
    }

    public int getIdLink() {
        return idLink;
    }

    public void setIdLink(int idLink) {
        this.idLink = idLink;
    }
    
    
    

    
}
