import java.util.*;
import java.util.function.Function;

class TestingLowerBound<V, X extends Exception>{
    final Function<? super Exception, X> mapper;

    TestingLowerBound(Function<? super Exception, X> mapper){
        this.mapper = mapper;
    }

     public X mapException(Exception e) {
        return mapper.apply(e);
    }
}