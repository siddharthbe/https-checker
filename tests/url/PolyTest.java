import java.net.URL;
import java.net.MalformedURLException;

import org.checkerframework.checker.https.qual.*;

class PolyTest {

    @PolyStartsWith String testMethod(@PolyStartsWith String s){
        return s;
    }

    void makesUrl(@StartsWith({"https", "path"}) String s) {
        @StartsWith({"https", "path"}) String s1 = testMethod(s);
    }

    void doesntMakeUrl(String s) {
        String s1 = testMethod(s);
    }

    void testError(String s) {
        // :: error: assignment.type.incompatible
        @StartsWith({"https", "path"}) String s1 = testMethod(s);
    }
}