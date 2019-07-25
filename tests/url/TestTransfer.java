import java.net.URL;
import java.net.MalformedURLException;
import org.checkerframework.checker.startswith.qual.*;

class TestTransfer{
    void checkString(String s) throws MalformedURLException{
        if(s.startsWith("https://")){
            URL url = new URL(s);
        }
    }

    void checkStringWithLocalVariable(String s) throws MalformedURLException{
        String t = "file://";
        if(s.startsWith(t)){
            URL url = new URL(s);
        }
    }

    void checkElseStore(String s) throws MalformedURLException{
        if(s.startsWith("https://")){
            URL url = new URL(s);
        }else{
            // :: error: assignment.type.incompatible
            @StartsWith({"https"}) String t = s;
        }
    }

    void checkWhileLoop(String s) throws MalformedURLException{
        while(s.startsWith("https://")){
            URL url = new URL(s);
        }
    }

    void checkOr(String s) throws MalformedURLException{
        if(s.startsWith("https://") || s.startsWith("jar://")){
            @StartsWith({"https://", "jar://"}) String t = s;
            // :: error: assignment.type.incompatible
            @StartsWith({"https://"}) String k = s;
            // :: error: assignment.type.incompatible
            @StartsWith({"jar://"}) String m = s;
        }
    }

    void checkOrWithURL(String s) throws MalformedURLException{
        if(s.startsWith("https://") || s.startsWith("http://")){
            // :: error: argument.type.incompatible
            URL url = new URL(s);
        }
    }

    void checkAnd(String s){
        if(s.startsWith("https://") && s.startsWith("jar://")){
            @StartsWith({"https://"}) String t = s;
            @StartsWith({"jar://"}) String k = s;
            @StartsWith({"arb"}) String m = s;
            @StartsWithBottom String n = s;
        }
    }

    void checkAndTwoReceivers(String s, String t) throws MalformedURLException{
        if(s.startsWith("https://") && t.startsWith("https://")){
            URL url = new URL(s);
            URL url2 = new URL(t);
        }
    }

    void checkAndTwoReceiversFail(String s, String t) throws MalformedURLException{
        if(s.startsWith("https://") && t.startsWith("http://")){
            URL url = new URL(s);
            // :: error: argument.type.incompatible
            URL url2 = new URL(t);
        }
    }

    void checkAndOneReceiver(String s) throws MalformedURLException{
        if(s.startsWith("https://") && s.startsWith("http")){
            URL url = new URL(s);
        }
    }

    void checkMethodInvocationRightOperandFail(String s) throws MalformedURLException{
        if(s.startsWith("https://") && true){
            URL url = new URL(s);
        }
    }

    void checkAnd2(String s){
        if(true && s.startsWith("https://")){
            @StartsWith({"https://"}) String t = s;
        }
    }

    void checkInverse(String s){
        if(!s.startsWith("https://")){
            @StartsWithUnknown String t = s;
        }else{
            @StartsWith({"https://"}) String t = s;
        }
    }

    void checkFail(String s, String t) throws MalformedURLException{
        if(s.startsWith(t)){
            // :: error: argument.type.incompatible
            URL url = new URL(s);
        }
    }

    void checkFailWithLocalVariable(String s) throws MalformedURLException{
        String t = "file";
        if(s.startsWith(t)){
            // :: error: argument.type.incompatible
            URL url = new URL(s);
        }
    }
}