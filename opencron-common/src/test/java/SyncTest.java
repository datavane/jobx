
import org.apache.commons.exec.*;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by benjobs on 2016/9/10.
 */
public class SyncTest {


    public static void main(String[] args) throws Exception {

        final CommandLine cmdLine = CommandLine.parse("C:\\Developer\\workspace\\bat\\hello.bat");
        final ExecuteWatchdog watchdog = new ExecuteWatchdog(Integer.MAX_VALUE);

        final Timer timer = new Timer();

        final DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler(){
            @Override
            public void onProcessComplete(int exitValue) {
                super.onProcessComplete(exitValue);
                watchdog.stop();
                timer.cancel();
            }

            @Override
            public void onProcessFailed(ExecuteException e) {
                super.onProcessFailed(e);
                watchdog.stop();
                timer.cancel();
            }
        };

        DefaultExecutor executor = new DefaultExecutor();

        executor.setWatchdog(watchdog);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //超时,kill...
                if (watchdog.isWatching()) {
                    watchdog.stop();
                    System.out.println(watchdog.isWatching());
                    timer.cancel();
                    System.out.println("kill....");
                }
            }
        },5*1000);

        executor.execute(cmdLine, resultHandler);
        System.out.println("dog is running?"+watchdog.isWatching());

    }


}
