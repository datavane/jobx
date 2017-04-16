package org.opencron.common.exception;

/**
 * Service层异常类.
 * 
 * 继承自RuntimeException,在函数中抛出会触发Spring的事务.
 * 
 */
public class ServiceException extends RuntimeException {
	
	private static final long serialVersionUID = 4146548369811741865L;

	public ServiceException() {
		super();
	}

	public ServiceException(String message) {
		super(message);
	}

	public ServiceException(Throwable cause) {
		super(cause);
	}

	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}
}
