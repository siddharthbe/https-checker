import java.net.URL;

class BasicURLTest {
    void good() {
        URL url = new URL("https://www.google.com");
    }

    void bad() {
        // :: error: assignment.type.incompatible
        URL url = new URL("http://www.google.com");
    }
}