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

package com.jobxhub.service.model;

import com.google.common.base.Function;
import com.jobxhub.service.entity.GroupEntity;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by th on 2017/5/8.
 */
@Data
public class Group implements Serializable {

    private Long groupId;

    private String groupName;

    private String comment;//备注信息

    private Long userId;//创建人

    private Date createTime;

    private String[] agentIds;

    //当前组所有的agent
    private List<Agent> agentList = new ArrayList<Agent>(0);

    private Integer agentCount;


    public Group() {

    }

    public static Function<? super GroupEntity, ? extends Group> transferModel = new Function<GroupEntity, Group>() {
        @Override
        public Group apply(GroupEntity input) {
            Group group = new Group();
            BeanUtils.copyProperties(input,group);
            return group;
        }
    };

    public static Function<? super Group, ? extends GroupEntity> transferEntity = new Function<Group, GroupEntity>() {
        @Override
        public GroupEntity apply(Group input) {
            GroupEntity model = new GroupEntity();
            BeanUtils.copyProperties(input,model);
            return model;
        }
    };


}
