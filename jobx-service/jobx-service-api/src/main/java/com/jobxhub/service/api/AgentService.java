package com.jobxhub.service.api;

import com.jobxhub.service.model.Agent;

import java.util.List;

public interface AgentService {
    List<Agent> getAll();

    Agent getAgent(Long proxyId);

    void updateStatus(Agent agent);

    List<Agent> getByGroup(Long groupId);

    void doConnect(String agent);

    void doDisconnect(String child);
}
