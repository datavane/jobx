package com.jobxhub.common.job;


public interface AgentJob {

    Response ping(Request request);

    Response path(Request request);

    Response monitor(Request request);

    Response execute(Request request);

    Response password(Request request);

    Response kill(Request request);

    Response proxy(Request request);

    Response guid(Request request);

    void restart(Request request);

    /**
     * agent 自动注册...
     *
     * @return
     */
    boolean register();
}
