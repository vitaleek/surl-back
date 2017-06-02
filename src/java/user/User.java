package user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
    @JsonIgnore
    public static Integer userID = 0;
    @JsonProperty("Object")
    private String object = "User";
    @JsonIgnore
    private Integer idUser;
    @JsonProperty("Login")
    private String login;
    @JsonProperty("Password")
    private String password;
    
    public User() {
    }

    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
}
