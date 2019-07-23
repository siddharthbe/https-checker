import org.checkerframework.checker.startswith.qual.*;

class TestTypeHierarchy{
    @StartsWith({"https://", "file://"}) String subtypingGood(@StartsWith({"https://"}) String s1,
                                                         @StartsWith({"file://"}) String s2, boolean condition ){
        if(condition){
            return s1;
        }else{
            return s2;
        }
    }

    @StartsWith({"https://", "file://"}) String subtypingBad(@StartsWith({"https://", "file://", "jar:https://",
            "jar:file://"}) String s1, @StartsWith({"file://", "http://"}) String s2, boolean condition){
        if(condition){
            // :: error: return.type.incompatible
            return s1;
        }else{
            // :: error: return.type.incompatible
            return s2;
        }
    }

    void testLeastUpperBoundGood(boolean condition){
        String s1;
        if(condition){
            s1 = "https://";
        }else{
            s1 = "file://";
        }
        @StartsWith({"https://", "file://"}) String s2 = s1;
    }

    void testLeastBoundBad(boolean condition){
        String s1;
        if(condition){
            s1 = "http://";
        }else{
            s1 = "file://";
        }
        // :: error: assignment.type.incompatible
        @StartsWith({"https://", "file://"}) String s2 = s1;
    }

    void testLeastBoundBad2(boolean condition){
        String s1;
        if(condition){
            s1 = "https://";
        }else{
            s1 = "files://";
        }
        // :: error: assignment.type.incompatible
        @StartsWith({"https://", "file://"}) String s2 = s1;
    }
}