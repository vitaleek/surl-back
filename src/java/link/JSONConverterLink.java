package link;

import com.fasterxml.jackson.databind.ObjectMapper;
import link.Link;
 
import java.io.File;
import java.io.IOException;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import user.User;

public class JSONConverterLink {
    public static Writer toJSON(Link link) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Writer JSONwriter = new StringWriter();
        mapper.writeValue(JSONwriter, link);
        System.out.println("json created!");
        return JSONwriter;
    }
    
    public static Link toJavaObject(String message) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        
        return mapper.readValue(message, Link.class);
    }
}
