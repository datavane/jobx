package org.opencron.common.exception;

/**
 * 
 * 未授权异常
 * wanghuajie
 */
public class UnauthorizedException extends BasicException {

	private static final long serialVersionUID = 8442904549771007810L;

	public UnauthorizedException() {
		super();
	}

	public UnauthorizedException(String msg) {
		super(msg);
	}

	public UnauthorizedException(Throwable nestedThrowable) {
		super(nestedThrowable);
	}

	public UnauthorizedException(String msg, Throwable nestedThrowable) {
		super(msg, nestedThrowable);
	}
}
