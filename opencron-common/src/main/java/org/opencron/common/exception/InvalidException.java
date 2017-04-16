package org.opencron.common.exception;
/**
 * 参数异常
 * @author wanghuajie
 *
 */
public class InvalidException extends BasicException {

	private static final long serialVersionUID = 2513495667924595876L;

	public InvalidException() {
		super();
	}

	public InvalidException(String msg) {
		super(msg);
	}

	public InvalidException(Throwable nestedThrowable) {
		super(nestedThrowable);
	}

	public InvalidException(String msg, Throwable nestedThrowable) {
		super(msg, nestedThrowable);
	}
}
