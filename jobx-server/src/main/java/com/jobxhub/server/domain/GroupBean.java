/**
 * Copyright (c) 2015 The JobX Project
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

package com.jobxhub.server.domain;

import com.google.common.base.Function;
import com.jobxhub.server.dto.Group;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * Created by th on 2017/5/8.
 */
public class GroupBean {

    private Long groupId;

    private String groupName;

    private String comment;//备注信息

    private Long userId;//创建人

    private Date createTime;


    public GroupBean() {}

    public GroupBean(Group group){
        BeanUtils.copyProperties(group,this);
    }

    public static Function<? super Group, ? extends GroupBean> transfer = new Function<Group, GroupBean>() {
        @Override
        public GroupBean apply(Group input) {
            return new GroupBean(input);
        }
    };

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
}
