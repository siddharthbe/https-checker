import java.net.URL;
import java.net.MalformedURLException;

import org.checkerframework.checker.startswith.qual.*;

class PolyTest {

    @PolyStartsWith String testMethod(@PolyStartsWith String s){
        return s;
    }

    void makesUrl(@StartsWith({"https://", "file://"}) String s) {
        @StartsWith({"https://", "file://"}) String s1 = testMethod(s);
    }

    void doesntMakeUrl(String s) {
        String s1 = testMethod(s);
    }

    void testError(String s) {
        // :: error: assignment.type.incompatible
        @StartsWith({"https://", "file://"}) String s1 = testMethod(s);
    }
}