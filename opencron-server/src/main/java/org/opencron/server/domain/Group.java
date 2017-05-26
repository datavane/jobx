package org.opencron.server.domain;

import javax.persistence.*;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by th on 2017/5/8.
 */
@Entity
@Table(name = "T_GROUP")
public class Group {

    @Id
    @GeneratedValue
    private Long groupId;

    private String groupName;

    private String comment;//备注信息

    private Long userId;//创建人

    @ManyToMany(cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "T_AGENT_GROUP",
            joinColumns =@JoinColumn(name = "groupId"),
            inverseJoinColumns = @JoinColumn(name = "agentId")
    )
    private Set<Agent> agents = new HashSet<Agent>(0);


    private Date createTime;

    @Transient
    private String userName;//显示用户名的冗余字段

    @Transient
    private Long agentCount;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Set<Agent> getAgents() {
        return agents;
    }

    public void setAgents(Set<Agent> agents) {
        this.agents = agents;
    }

    public Long getAgentCount() {
        return agentCount;
    }

    public void setAgentCount(Long agentCount) {
        this.agentCount = agentCount;
    }
}
