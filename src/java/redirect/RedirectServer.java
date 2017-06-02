package redirect;

import java.io.*;
import java.net.*;

import link.LinkJDBCTemplate;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RedirectServer extends Thread {
    
    Socket s;
    public static int threadNumber = 0;
    
    public static void main(String [] args){
        
        try {
            ServerSocket server = new ServerSocket(80, 0, InetAddress.getByName("localhost"));
            System.out.println("Server is started on http://localhost:80");
            while (true){
                new RedirectServer(threadNumber, server.accept());
                
            }
        }
        catch (Exception e){
            System.out.println("Init error*: "+ e);
        }
    }
    
    public RedirectServer(int num, Socket s){
        this.s = s;
        threadNumber++;
        System.out.println("Thread Number is: "+ threadNumber);
        setDaemon(true);
        setPriority(NORM_PRIORITY);
        start();
    }
    public void run(){
        
        
        //------------------Работа с БД --------------------
        ApplicationContext context
                = new ClassPathXmlApplicationContext("Beans.xml");

        LinkJDBCTemplate linkJDBCTemplate
                = (LinkJDBCTemplate) context.getBean("linkJDBCTemplate");
        //------------------------------------------------
        
        try{
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
            byte buf[] = new byte[64*1024];
            int r = is.read(buf);
            String response = null;
            String request = new String(buf);
            System.out.println("Request:\n"+request);
            
                
            
                String path = getPath(request);
                path = "http://sahar.uk"+path;
                System.out.println("Requested path is: "+ path);
                String longLink = linkJDBCTemplate.getLongLink(path);
                System.out.println("LongLink is: "+ longLink);
                
                if (longLink == null){
                    response = "HTTP/1.1 404 NotFound";
                    System.out.println("Response: \n"+response);
                } 
               else{
                    response = "HTTP/1.1 301 Moved Permanently\nLocation: "+longLink;
                    System.out.println("Response: \n"+response);
                }
            
            os.write(response.getBytes());
            s.close();
            return;
        }
        catch (Exception e){
            System.out.println("Error!  :"+e);
            e.printStackTrace();
        }
    }
    protected String extract(String str, String start, String end){
        int s = str.indexOf(start, 0), e = str.indexOf(end, 0);
        s = s + start.length()+1; e = e - 1;
            if ((e - s)< 0) return "";
            else return (str.substring(s, e));
    }
    protected String getPath(String header){
        String URI = extract(header, "GET", "HTTP");
        if (URI == null) URI = extract(header, "POST", "HTTP");
        return URI;
    }
}
