package com.jobxhub.common.api;

import com.jobxhub.common.job.Request;
import com.jobxhub.common.job.Response;

public interface AgentJob {

    Response ping(Request request);

    Response path(Request request);

    Response listPath(Request request);

    Response monitor(Request request);

    Response execute(Request request);

    Response password(Request request);

    Response kill(Request request);

    Response proxy(Request request);

    Response macId(Request request);

    void restart(Request request);

}
