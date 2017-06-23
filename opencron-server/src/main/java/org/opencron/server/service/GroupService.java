/**
 * Copyright 2016 benjobs
 * <p>
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.opencron.server.service;

import org.opencron.common.utils.CommonUtils;
import org.opencron.server.dao.QueryDao;
import org.opencron.server.domain.Agent;
import org.opencron.server.domain.Group;
import org.opencron.server.tag.PageBean;
import org.opencron.server.vo.AgentGroupVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.opencron.common.utils.CommonUtils.notEmpty;

@Service
@Transactional
public class GroupService {

    @Autowired
    private QueryDao queryDao;

    @Autowired
    private AgentService agentService;

    public PageBean<Group> getGroupPage(PageBean pageBean) {
        pageBean = queryDao.getPageBySql(pageBean,Group.class,"SELECT G.*,U.userName FROM T_GROUP AS G INNER JOIN T_USER AS U ON G.userId=U.userId");
        List<Group> groups = pageBean.getResult();
        if (CommonUtils.notEmpty(groups)) {
            String sql = "SELECT COUNT(1) FROM T_AGENT_GROUP WHERE groupId=?";
            for (Group group : groups) {
                Long count = queryDao.getCountBySql(sql,group.getGroupId());
                group.setAgentCount(count);
            }
        }
        pageBean.setResult(groups);
        return pageBean;
    }

    public List<Group> getAll() {
        return queryDao.getAll(Group.class);
    }

    public List<Group> getGroupforAgent() {
        String sql = "SELECT T.groupId,G.groupName,A.agentId,A.`name` AS agentName,A.ip AS agentIp " +
                " FROM T_AGENT A " +
                " LEFT JOIN T_AGENT_GROUP AS T " +
                " ON A.agentId = T.agentId" +
                " LEFT JOIN T_GROUP AS G" +
                " ON T.groupId = G.groupId" +
                " WHERE A.deleted=0 "+
                " ORDER BY G.createTime ";

        Group noGroup = new Group();
        noGroup.setGroupName("未分组");
        noGroup.setGroupId(0L);

        Map<Long,Group> groupMap = new HashMap<Long, Group>(0);

        List<AgentGroupVo> agentGroupVos = queryDao.sqlQuery(AgentGroupVo.class,sql);
        if (CommonUtils.notEmpty(agentGroupVos))  {
            for(AgentGroupVo agentGroup:agentGroupVos){
                Agent agent = new Agent();
                agent.setAgentId(agentGroup.getAgentId());
                agent.setName(agentGroup.getAgentName());
                agent.setIp(agentGroup.getAgentIp());

                if (agentGroup.getGroupId()==null) {
                    noGroup.getAgents().add(agent);
                }else {
                    if (groupMap.get(agentGroup.getGroupId()) == null) {
                        Group group = new Group();
                        group.setGroupId(agentGroup.getGroupId());
                        group.setGroupName(agentGroup.getGroupName());
                        group.getAgents().add(agent);
                        groupMap.put(agentGroup.getGroupId(),group);
                    }else {
                        groupMap.get(agentGroup.getGroupId()).getAgents().add(agent);
                    }
                }
            }
        }

        List<Group> groups = new ArrayList<Group>(0);
        groups.add(noGroup);
        for(Map.Entry<Long,Group> entry:groupMap.entrySet()){
            groups.add(entry.getValue());
        }
        return groups;
    }

    public void merge(Group group) {
        queryDao.merge(group);
    }

    public boolean existsName(Long id, String name) {
        String sql = "SELECT COUNT(1) FROM T_GROUP WHERE groupName=? ";
        if (notEmpty(id)) {
            sql += " AND groupId != " + id;
        }
        return (queryDao.getCountBySql(sql, name)) > 0L;
    }

    public Group getById(Long groupId) {
        Group group = queryDao.get(Group.class,groupId);
        String sql = "SELECT A.* FROM T_GROUP AS G INNER JOIN T_AGENT_GROUP AS T ON G.groupId = T.groupId AND G.groupId=? INNER JOIN T_AGENT AS A ON T.agentId = A.agentId";
        List<Agent> agents = queryDao.sqlQuery(Agent.class,sql,groupId);
        group.getAgents().addAll(agents);
        return group;
    }

}
