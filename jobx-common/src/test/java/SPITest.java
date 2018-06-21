import com.jobxhub.common.util.collection.HashMap;
import org.junit.Test;

import java.util.Map;


public class SPITest {

    @Test
    public void testInstance() {

      String execUser = "hadoop , hdfs , hfdsaf,88";
        System.out.println(execUser.replaceAll("\\s+,\\s+",","));

    }

}
