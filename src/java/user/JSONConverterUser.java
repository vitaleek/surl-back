package user;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class JSONConverterUser {
    public static Writer toJSON(User user) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Writer JSONwriter = new StringWriter();
        mapper.writeValue(JSONwriter, user);
        System.out.println("json created!");
        return JSONwriter;
    }
    
    public static User toJavaObject(String message) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        
        return mapper.readValue(message, User.class);
    }
}
