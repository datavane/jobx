package com.jobxhub.service.api;

import com.jobxhub.service.model.UserAgent;

import java.util.List;

public interface UserAgentService {

    void update(Long userId, List<Long> agentIds);

    boolean save(UserAgent userAgent);

    List<UserAgent> getUserAgent(Long userId);
}
