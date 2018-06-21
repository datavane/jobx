import org.junit.Test;
import com.jobxhub.common.util.SystemPropertyUtils;

import java.io.IOException;

public class SPITest {

    @Test
    public void testSpi() throws IOException {
        boolean xx =  SystemPropertyUtils.getBoolean("aaa",false);

        System.out.println(xx);
        //SystemPropertyUtils.setProperty("aaa","true");


        xx =  SystemPropertyUtils.getBoolean("aaa",false);

        System.out.println(xx);
    }

}
