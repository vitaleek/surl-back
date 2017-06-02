package EchoChamber;

import java.io.*;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import java.util.ArrayList;
import link.LinkJDBCTemplate;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import user.UserJDBCTemplate;
import user.User;
import user.JSONConverterUser;

import link.Link;
import link.JSONConverterLink;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

@ServerEndpoint("/echo")
public class EchoServer {

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Соединение установлено");
    }

    @OnMessage
    public void onMessage(String message, Session session) {

        //------------------Работа с БД --------------------
        ApplicationContext context
                = new ClassPathXmlApplicationContext("Beans.xml");

        UserJDBCTemplate userJDBCTemplate
                = (UserJDBCTemplate) context.getBean("userJDBCTemplate");

        LinkJDBCTemplate linkJDBCTemplate
                = (LinkJDBCTemplate) context.getBean("linkJDBCTemplate");
        //------------------------------------------------

        String response = "";
        String obj = "";
        System.out.println("Сообщение принято от " + session.getId() + ", сообщение:   " + message);
        if (message.indexOf("USER_rtubcxedhhb") > 0) {
            obj = "USER";
        } else if (message.indexOf("LINK_fdlkhferhjhvt") > 0) {
            obj = "LINK";
        } else if (message.indexOf("List") > 0) {
            obj = "LIST";
        } else if (message.indexOf("STATISTICS_ghjklhg") > 0) {
            obj = "STATISTICS";
        } else if (message.indexOf("LINKREQUEST_alryeudh") > 0) {
            obj = "LINK_REQUEST";
        } else if (message.indexOf("LINKFIX_rtedfred") > 0) {
            obj = "LINK_FIX";
        } else if (message.indexOf("USER_reg") > 0) {
            obj = "USER_REG";
        } else if (message.indexOf("URL_getinfo") > 0) {
            obj = "URL_GET_INFO";
        } else if (message.indexOf("TAG_get_list") > 0) {
            obj = "TAG_GET_LIST";
        }
        
        
        switch (obj) {
            case "USER":
                User user = new User();
                try {
                    user = JSONConverterUser.toJavaObject(message);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
//                System.out.println("Decoded user:  ");
//                System.out.println(user.toString());
//                System.out.println("Login" + user.getLogin());
//                System.out.println("Password" + user.getPassword());
                User userInBase = new User();
                boolean inBase = false;
                ArrayList<User> users = (ArrayList<User>) userJDBCTemplate.listUsers();
                for (User uu : users) {
                    if (uu.getLogin().equals(user.getLogin())) {
                        inBase = true;
                        userInBase = uu;
                    }
                }
                if (!inBase) {
                    response = "NO_USER";
                } else {
                    if (user.getPassword().equals(userInBase.getPassword())) {
                        response = "SUCCESSFUL";
                    } else {
                        response = "WRONG_PASS";
                    }
                }
                System.out.println("Сообщение передано:  " + response);
                try {
                    session.getBasicRemote().sendText(response);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                break;
                
            case "LINK":
                Link link = new Link();
                response = "";
                try {
                    link = JSONConverterLink.toJavaObject(message);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                if (linkJDBCTemplate.linkInBase(link.getLongLink())) {
                    response = "IN_BASE";
                } else {
                    linkJDBCTemplate.createLink(link.getLongLink(), link.getDescription(), link.getUserLogin(), link.getTags());
                    System.out.println("Link added to database");
                    response = "SUCCESSFUL";
                }
                System.out.println("Сообщение передано:  " + response);
                try {
                    session.getBasicRemote().sendText(response);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                break;
                
            case "STATISTICS":
                response = "";
                String login = "";
                for (int i = 40; i <= (message.length() - 3); i++) {
                    login += message.charAt(i);
                    System.out.print(message.charAt(i));
                }
                //System.out.println(login);
                //---------------JSON-simple-------------
                JSONArray ar = new JSONArray();
                ArrayList<Link> list = new ArrayList<Link>();
                list = linkJDBCTemplate.getAllForLinkByLogin(login);
                for (Link l : list) {
                    try {
                        JSONObject resultJson = new JSONObject();
                        resultJson.put("LongLink", l.getLongLink());
                        resultJson.put("ShortLink", l.getShortLink());
                        resultJson.put("UserLogin", l.getUserLogin());
                        resultJson.put("Description", l.getDescription());
                        resultJson.put("Tags", l.getTags());
                        resultJson.put("Redirect", l.getRedirect());

                        ar.add(resultJson);
                        System.out.println("JSON converted");

                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                }
                System.out.println("JSON array is ready to be sent");
                try {
                    session.getBasicRemote().sendObject(ar);
                    System.out.println("JSON sent");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            
//                    //---------------JSON-simple-end---------
            break;
            
            case "LINK_REQUEST":
                link = new Link();
                int index = message.indexOf("Shortlink");
                String shortLink = "";
                //System.out.println("Index is "+(index));
                //System.out.println(newLink.getShortLink());
                for (int num = (index+ 12); num < (message.length() - 2); num++){
                    shortLink += message.charAt(num);
                }
                //System.out.println(shLink);
                
                link = linkJDBCTemplate.getAllForLink(shortLink);
                //System.out.println(link.getShortLink());
                JSONObject ob = new JSONObject();
                ob.put("LongLink", link.getLongLink());
                ob.put("ShortLink", link.getShortLink());
                ob.put("UserLogin", link.getUserLogin());
                ob.put("Description", link.getDescription());
                ob.put("Tags", link.getTags());
                ob.put("Redirect", link.getRedirect());
                
                System.out.println("JSON object is ready to be sent");
                try {
                    session.getBasicRemote().sendObject(ob);
                    System.out.println("JSON sent");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            break;
                
            case "LINK_FIX":
                link = new Link();
                try {
                    link = JSONConverterLink.toJavaObject(message);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                
                //System.out.println(link.getShortLink());
                linkJDBCTemplate.updateLink(link);
            break;
                
            case "USER_REG":
                response = "";
                user = new User();
                try {
                    user = JSONConverterUser.toJavaObject(message);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
//                System.out.println("Login" + user.getLogin());
//                System.out.println("Password" + user.getPassword());
                
                inBase = false;
                ArrayList<User> usersList = (ArrayList<User>) userJDBCTemplate.listUsers();
                for (User u : usersList) {
                    if (u.getLogin().equals(user.getLogin())) {
                        inBase = true;    
                    }
                }
                if (!inBase) {
                    response = "NO_USER";
                    userJDBCTemplate.createUser(user.getLogin(), user.getPassword());
                } else {
                    response = "USER_IN_BASE";
                }
                
                try {
                    session.getBasicRemote().sendText(response);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                System.out.println("Сообщение передано:  " + response);
            break;
                
            case "URL_GET_INFO":
                link = new Link();
                 
                try {
                    link = JSONConverterLink.toJavaObject(message);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                
                shortLink = link.getShortLink();
                if (linkJDBCTemplate.linkInBase(shortLink, "")){
                    link = linkJDBCTemplate.getAllForLink(shortLink);

                    ob = new JSONObject();
                    ob.put("LongLink", link.getLongLink());
                    ob.put("ShortLink", link.getShortLink());
                    ob.put("UserLogin", link.getUserLogin());
                    ob.put("Description", link.getDescription());
                    ob.put("Tags", link.getTags());
                    ob.put("Redirect", link.getRedirect());
                }
                else{
                    ob = new JSONObject();
                    ob.put("LongLink", "");
                }
                System.out.println("JSON object is ready to be sent");
                try {
                    session.getBasicRemote().sendObject(ob);
                    System.out.println("JSON sent");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            break;
                
            case "TAG_GET_LIST":
                link = new Link();
                 
                try {
                    link = JSONConverterLink.toJavaObject(message);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                
                String tag = link.getTags().get(0);
                ar = new JSONArray();
                list = linkJDBCTemplate.getLinksByTag(tag);
                for (Link l : list) {
                    try {
                        ob = new JSONObject();
                        ob.put("LongLink", l.getLongLink());
                        ob.put("ShortLink", l.getShortLink());
                        ob.put("UserLogin", l.getUserLogin());
                        ob.put("Description", l.getDescription());
                        ob.put("Tags", l.getTags());
                        ob.put("Redirect", l.getRedirect());

                        ar.add(ob);
                        System.out.println("JSON converted");

                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                }
                System.out.println("JSON array is ready to be sent");
                try {
                    session.getBasicRemote().sendObject(ar);
                    System.out.println("JSON sent");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
                System.out.println("JSON array sent");
            break;
                
        }

    }

    /**
     * The user closes the connection.
     *
     * Note: you can't send messages to the client from this method
     */
    @OnClose
    public void onClose(Session session) {
        System.out.println("Session " + session.getId() + " has ended");
        System.out.println("Соединение закрыто.");
    }
}
