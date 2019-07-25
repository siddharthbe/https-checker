import java.net.URL;
import java.net.MalformedURLException;

class TestTransfer{
    void good(String s) throws MalformedURLException{
        if(s.startsWith("https://")){
            URL url = new URL(s);
        }
    }

    void good2(String s) throws MalformedURLException{
        String t = "file://";
        if(s.startsWith(t)){
            URL url = new URL(s);
        }
    }

    void bad(String s) throws MalformedURLException{
        String t = "file";
        if(s.startsWith(t)){
            // :: error: argument.type.incompatible
            URL url = new URL(s);
        }
    }
}