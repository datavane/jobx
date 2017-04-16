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

import java.util.Collections;
import java.util.List;

import org.opencron.common.utils.CommonUtils;
import org.opencron.server.dao.QueryDao;
import org.opencron.server.domain.User;
import org.opencron.server.job.OpencronTools;
import org.opencron.server.tag.PageBean;
import org.apache.commons.codec.digest.DigestUtils;
import org.opencron.common.job.Opencron;
import org.opencron.server.domain.Agent;
import org.opencron.server.vo.JobVo;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

import static org.opencron.common.utils.CommonUtils.isEmpty;
import static org.opencron.common.utils.CommonUtils.notEmpty;

@Service
public class AgentService {

    @Autowired
    private QueryDao queryDao;

    @Autowired
    private ExecuteService executeService;

    @Autowired
    private JobService jobService;

    @Autowired
    private SchedulerService schedulerService;

    @Autowired
    private UserService userService;

    public List<Agent> getAgentByConnType(Opencron.ConnType connType) {
        return queryDao.sqlQuery(Agent.class,"SELECT * FROM T_AGENT WHERE status = 1 AND proxy = "+connType.getType());
    }

    public List<Agent> getAll() {
        List<Agent> agents = OpencronTools.CACHE.get(OpencronTools.CACHED_AGENT_ID,List.class);
        if (CommonUtils.isEmpty(agents)) {
            flushAgent();
        }
       return OpencronTools.CACHE.get(OpencronTools.CACHED_AGENT_ID,List.class);
    }

    private void flushAgent(){
        OpencronTools.CACHE.put(OpencronTools.CACHED_AGENT_ID,queryDao.getAll(Agent.class));
    }

    public List<Agent> getOwnerAgentByStatus(HttpSession session,int status){
        String sql = "SELECT * FROM T_AGENT WHERE status=?";
        if (!OpencronTools.isPermission(session)) {
            User user = OpencronTools.getUser(session);
            sql += " AND agentId in ("+user.getAgentIds()+")";
        }
        return queryDao.sqlQuery(Agent.class,sql,status);
    }

    public PageBean getOwnerAgent(HttpSession session,PageBean pageBean) {
        String sql = "SELECT * FROM T_AGENT";
        if (!OpencronTools.isPermission(session)) {
            User user = OpencronTools.getUser(session);
            sql += " WHERE agentId IN ("+user.getAgentIds()+")";
        }
        pageBean.verifyOrderBy("name","name","ip","port");
        sql += " ORDER By "+ pageBean.getOrderBy() + " " + pageBean.getOrder();
        queryDao.getPageBySql(pageBean, Agent.class, sql);
        return pageBean;
    }

    public Agent getAgent(Long id) {
        Agent agent =  queryDao.get(Agent.class, id);
        if (agent!=null) {
            agent.setUsers(getAgentUsers(agent.getAgentId()));
        }
        return agent;
    }

    private List<User> getAgentUsers(Long agentId){
        String sql = "SELECT * FROM T_USER WHERE FIND_IN_SET(?,AGENTIDS)";
        List<User> users = queryDao.sqlQuery(User.class,sql,agentId);
        return isEmpty(users)? Collections.<User>emptyList():users;
    }
    public void addOrUpdate(Agent agent) {
        /**
         * 修改过agent
         */
        boolean update = false;
        if (agent.getAgentId()!=null) {
            update = true;
        }

        /**
         * fix bug.....
         * 修改了agent要刷新所有在任务队列里对应的作业,
         * 否则一段端口改变了,任务队列里的还是更改前的连接端口,
         * 当作业执行的时候就会连接失败...
         *
         */
        if (update) {
            queryDao.save(agent);
            /**
             * 获取该执行器下所有的自动执行,并且是quartz类型的作业
             */
            List<JobVo> jobVos = jobService.getJobVoByAgentId(agent, Opencron.ExecType.AUTO, Opencron.CronType.QUARTZ);
            try {
                schedulerService.put(jobVos,this.executeService);
            } catch (SchedulerException e) {
                /**
                 * 创新任务列表失败,抛出异常,整个事务回滚...
                 */
                throw new RuntimeException(e.getCause());
            }
        }else {
            queryDao.save(agent);
        }

        /**
         * 同步缓存...
         */
        flushAgent();

    }

    public String checkName(Long id, String name) {
        String sql = "SELECT COUNT(1) FROM T_AGENT WHERE name=? ";
        if (notEmpty(id)) {
            sql += " AND agentId != " + id;
        }
        return (queryDao.getCountBySql(sql, name)) > 0L ? "no" : "yes";
    }


    public String checkhost(Long id, String host) {
        String sql = "SELECT COUNT(1) FROM T_AGENT WHERE ip=? ";
        if (notEmpty(id)) {
            sql += " AND agentId != " + id;
        }
        return (queryDao.getCountBySql(sql, host)) > 0L ? "no" : "yes";
    }


    public String editPwd(Long id, String pwd0, String pwd1, String pwd2) {
        Agent agent = this.getAgent(id);
        String password = DigestUtils.md5Hex(pwd0);
        if (password.equals(agent.getPassword())) {
            if (pwd1.equals(pwd2)) {
                pwd1 = DigestUtils.md5Hex(pwd1);
                agent.setPassword(pwd1);
                Boolean flag = executeService.password(agent, pwd1);
                if (flag) {
                    this.addOrUpdate(agent);
                    return "success";
                } else {
                    return "failure";
                }
            } else {
                return "two";
            }
        } else {
            return "one";
        }
    }


    public List<Agent> getOwnerAgents(HttpSession session) {
        String sql = "SELECT * FROM T_AGENT ";
        if (!OpencronTools.isPermission(session)) {
            User user = OpencronTools.getUser(session);
            sql += " WHERE agentId IN ("+user.getAgentIds()+")";
        }
        return queryDao.sqlQuery(Agent.class,sql);
    }

    public Agent getByHost(String host) {
        String sql = "SELECT * FROM T_AGENT WHERE ip=?";
        Agent agent = queryDao.sqlUniqueQuery(Agent.class,sql,host);
        if (agent!=null) {
            agent.setUsers(getAgentUsers(agent.getAgentId()));
        }
        return agent;
    }
}
