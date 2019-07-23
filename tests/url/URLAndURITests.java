import java.net.MalformedURLException;
import java.net.*;
import java.io.*;

class URLAndURITests{
    void goodURL() throws MalformedURLException {
        // creates a URL with string representation.
        URL url1 = new URL("https://some-host");

        // creates a URL with a protocol, hostname, and path
        URL url2 = new URL("https", "some-host", "/some-path-of-your-choice/");

        // creates a URL with a protocol, hostname, port, and path
        URL url3 = new URL("https", "some-host", 80, "/some-path-of-your-choice/");

        // creates a URL by parsing the given spec with the specified handler within a specified context.
        URL url4 = new URL(new URL("https://"), "https://www.google.com", new DummyURLStreamHandler());
    }

    void goodURI() throws URISyntaxException {
        // creates a URI with the given string
        URI uri1 = new URI("https://some-host");

        // creates a URI with the given scheme, hostname, path, and fragment
        URI uri2 = new URI("https", "some-host", "/some-path-of-your-choice/", "#some-fragment");

        // creates a URI with the given scheme, host, path, query, and fragment
        URI uri3 = new URI("https", "some-authority", "/some-path-of-your-choice/", "some-query", "#some-fragment");
    }

    void badURL() throws MalformedURLException {
        // creates a URL with string representation.
        // :: error: argument.type.incompatible
        URL url1 = new URL("httpss://some-host");

        // creates a URL with a protocol, hostname, and path
        // :: error: argument.type.incompatible
        URL url2 = new URL("http", "some-host", "/some-path-of-your-choice/");

        // creates a URL with a protocol, hostname, port, and path
        // :: error: argument.type.incompatible
        URL url3 = new URL("http", "some-host", 80, "/some-path-of-your-choice/");

        // creates a URL by parsing the given spec with the specified handler within a specified context.
        // :: error: argument.type.incompatible
        URL url4 = new URL(new URL("https:/"), "https://some-host", new DummyURLStreamHandler());
    }

    void badURI() throws URISyntaxException {
        // creates a URI with the given string
        // :: error: argument.type.incompatible
        URI uri1 = new URI("https:/some-host");

        // creates a URI with the given scheme, hostname, path, and fragment
        // :: error: argument.type.incompatible
        URI uri2 = new URI("http", "some-host", "/some-path-of-your-choice/", "#some-fragment");

        // creates a URI with the given scheme, host, path, query, and fragment
        // :: error: argument.type.incompatible
        URI uri3 = new URI("http", "some-authority", "/some-path-of-your-choice/", "some-query", "#some-fragment");
    }

    private static class DummyURLStreamHandler extends URLStreamHandler {

        @Override
        protected URLConnection openConnection(URL url) throws IOException {
            throw new UnsupportedOperationException();
        }
    }
}