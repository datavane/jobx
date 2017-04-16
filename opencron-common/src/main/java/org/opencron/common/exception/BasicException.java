package org.opencron.common.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * 基础异常，继承RuntimeException
 * 
 * @author wanghuajie 2012.8.23
 * 
 */
public class BasicException extends RuntimeException {// Exception

	private static final long serialVersionUID = 1407617624583467962L;

	private Throwable nestedThrowable = null;

	public BasicException() {
		super();
	}

	public BasicException(String msg) {
		super(msg);
	}

	public BasicException(Throwable nestedThrowable) {
		this.nestedThrowable = nestedThrowable;
	}

	public BasicException(String msg, Throwable nestedThrowable) {
		super(msg);
		this.nestedThrowable = nestedThrowable;
	}

	public void printStackTrace() {
		super.printStackTrace();
		if (nestedThrowable != null) {
			nestedThrowable.printStackTrace();
		}
	}

	public void printStackTrace(PrintStream ps) {
		super.printStackTrace(ps);
		if (nestedThrowable != null) {
			nestedThrowable.printStackTrace(ps);
		}
	}

	public void printStackTrace(PrintWriter pw) {
		super.printStackTrace(pw);
		if (nestedThrowable != null) {
			nestedThrowable.printStackTrace(pw);
		}
	}
}
