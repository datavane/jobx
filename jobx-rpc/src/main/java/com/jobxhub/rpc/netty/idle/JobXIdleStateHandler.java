package com.jobxhub.rpc.netty.idle;

import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author Autorun
 */
public class JobXIdleStateHandler extends IdleStateHandler {

	public JobXIdleStateHandler(){
		this(10, 10, 10, TimeUnit.SECONDS);
	}

	public JobXIdleStateHandler(int readerIdleTimeSeconds, int writerIdleTimeSeconds, int allIdleTimeSeconds) {
		super(readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds);
	}

	public JobXIdleStateHandler(long readerIdleTime, long writerIdleTime, long allIdleTime, TimeUnit unit) {
		super(readerIdleTime, writerIdleTime, allIdleTime, unit);
	}

	public JobXIdleStateHandler(boolean observeOutput, long readerIdleTime, long writerIdleTime, long allIdleTime, TimeUnit unit) {
		super(observeOutput, readerIdleTime, writerIdleTime, allIdleTime, unit);
	}
}
