import java.net.URL;
import java.net.MalformedURLException;
import org.checkerframework.checker.startswith.qual.*;

class BasicURLTest {
    void good() throws MalformedURLException {
        URL url = new URL("https://www.google.com");
    }

    void good2() throws MalformedURLException {
        URL url = new URL("file://2ndFile.html");
    }

    void bad() throws MalformedURLException {
        // :: error: argument.type.incompatible
        URL url = new URL("http://www.google.com");
    }
}