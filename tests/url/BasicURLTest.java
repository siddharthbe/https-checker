import java.net.URL;
import java.net.MalformedURLException;
import checkerframework.checker.https.qual.*;

class BasicURLTest {
    void good() throws MalformedURLException {
        @StartsWith({"https", "file", "path"}) String s = "https://www.google.com";
        URL url = new URL(s);
    }

    void bad() throws MalformedURLException {
        // :: error: argument.type.incompatible
        URL url = new URL("http://www.google.com");
    }
}