package logic;

public class Core {
    
    public static String generateShortLink() {
        char [] source = {'a','b','c','d','e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 
            'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 
            'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1',
            '2', '3', '4', '5', '6', '7', '8', '9'};
        int q = 8;
        String shortLink = "";
        for (int i = 0; i < q; i++){
            shortLink = shortLink + source[(int)(Math.random()*62)];
        }
        
//        System.out.println("Your short-link is:  ");
//        System.out.println("http://sahar.uk/"+shortLink+"");
        return "http://sahar.uk/"+shortLink;
    }
    
}
