package org.opencron.common.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Created by benjobs on 2016/10/10.
 */
public class PingException extends BasicException {

    private static final long serialVersionUID = 2030063376333004234L;

    public PingException() {
        super();
    }

    public PingException(String msg) {
        super(msg);
    }

    public PingException(Throwable nestedThrowable) {
        super(nestedThrowable);
    }

    public PingException(String msg, Throwable nestedThrowable) {
        super(msg, nestedThrowable);
    }

    @Override
    public void printStackTrace() {
        super.printStackTrace();
    }

    @Override
    public void printStackTrace(PrintStream ps) {
        super.printStackTrace(ps);
    }

    @Override
    public void printStackTrace(PrintWriter pw) {
        super.printStackTrace(pw);
    }
}
