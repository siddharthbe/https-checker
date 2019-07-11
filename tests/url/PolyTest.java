import java.net.URL;
import java.net.MalformedURLException;

import org.checkerframework.checker.https.qual.HTTPS;
import org.checkerframework.checker.https.qual.PolyHTTPS;

class PolyTest {

    @PolyHTTPS String testMethod(@PolyHTTPS String s){
        return s;
    }

    void makesUrl(@HTTPS String s) {
        @HTTPS String s1 = testMethod(s);
    }

    void doesntMakeUrl(String s) {
        String s1 = testMethod(s);
    }

    void testError(String s) {
        // :: error: assignment.type.incompatible
        @HTTPS String s1 = testMethod(s);
    }
}