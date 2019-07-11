import java.net.URL;
import java.net.MalformedURLException;
import java.io.FileNotFoundException;


class BasicURLTest {
    void good() throws MalformedURLException {
        URL url = new URL("https://www.google.com");
    }

    void bad() throws MalformedURLException {
        // :: error: argument.type.incompatible
        URL url = new URL("http://www.google.com");
    }

}