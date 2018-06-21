
import com.jobxhub.common.util.collection.HashMap;
import org.junit.Test;


public class TestDemo {

    @Test
    public void test1() throws Exception {

        HashMap<String,Object> map = new HashMap<String, Object>(0);
        map.put("a",null);
        Integer a = map.getInt("a");
        System.out.println(a);
    }

}
