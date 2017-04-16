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


package org.opencron.server.dao;

import org.opencron.server.tag.PageBean;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by benjobs on 15/10/18.
 */
@Repository
public class QueryDao extends BaseDao {

    /**
     * 获取分页结果,
     * @param beanClass 支持任意Bean，按结果集映射
     * @return
     */
    public <E> PageBean<E> getPageBySql(PageBean<E> pageBean, Class<E> beanClass, String sql, Object... parameters) {
        Query query = createSQLQuery(sql, parameters).setResultTransformer(BeanResultTransFormer.get(beanClass));
        pageQuery(query, pageBean);

        //总记录数
        sql = preparedCount(sql);
        Long count = this.getCountBySql(sql, parameters);
        if (count == null) {
            count = 0L;
        }
        pageBean.setTotalCount(count);
        return pageBean;
    }

    /**
     * 分页查询
     *
     * @param query
     * @return
     */
    public static PageBean pageQuery(Query query, PageBean pageBean) {
        pageBean.setResult(pageQuery(query, pageBean.getPageNo(), pageBean.getPageSize()));
        return pageBean;
    }

}
