import com.jobxhub.common.util.collection.HashMap;
import org.junit.Test;

import java.util.Map;


public class SPITest {

    @Test
    public void testInstance() {

        Map<String,String> map = new HashMap<String,String>();
        map.put("xx",null);
        System.out.println(map.get("xx"));

    }

}
