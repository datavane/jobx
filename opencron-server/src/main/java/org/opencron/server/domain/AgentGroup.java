package org.opencron.server.domain;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by th on 2017/5/8.
 */
@Entity
@Table(name = "T_AGENTGROUP")
public class AgentGroup {

    @Id
    @GeneratedValue
    private Long groupId;

    private String groupName;

    private String comment;//备注信息

    private Long userId;//创建人

    private Date createTime;

    @Transient
    private String userName;//显示用户名的冗余字段

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
}
