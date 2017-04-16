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

import org.opencron.common.utils.CommonUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.opencron.common.utils.CommonUtils.toLong;


@SuppressWarnings("unchecked")
@Transactional(readOnly = true)
public class BaseDao<T, PK extends Serializable> extends HibernateDao {


    /**
     * 当前实体对应的泛型类
     */
    protected Class<T> entityClass = null;

    public BaseDao() {
        entityClass = (Class<T>) CommonUtils.getGenericType(this.getClass());
    }

    /**
     * 获取实体
     * @param id
     * @return
     */
    public T get(final PK id) {
        return this.get(entityClass, id);
    }


    /**
     * 获取全部实体列表
     * @return
     */
    public List<T> getAll() {
        return this.getAll(entityClass);
    }


    /**
     * 保存实体
     */
    @SuppressWarnings({"hiding"})
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public <T> T save(T entity) {
        return super.save(entity);
    }

    /**
     * 删除实体
     * @param id
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void delete(final PK id) {
        this.delete(entityClass, id);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void delete(Object entity) {
        super.delete(entity);
    }

    /**
     * 根据sql获取总数
     * @param sql 需要保证sql为查询总数的语句
     * @return
     */
    public Long getCountBySql(String sql, Object... params) {
        Object result = createSQLQuery(sql, params).uniqueResult();
        return toLong(result);
    }

    /**
     * 执行count查询获得本次Hql查询所能获得的对象总数
     * @param hql
     * @param values
     * @return
     */
    public Long getCountByHql(String hql, final Object... values) {
        hql = preparedCount(hql);
        return toLong(createQuery(hql, values).uniqueResult());
    }

    public static String preparedCount(String sql) {
        Pattern pattern = Pattern.compile("\\((.*?)\\)");
        Matcher matcher = pattern.matcher(sql);

        String tmpSql = sql.toLowerCase();
        while (matcher.find()) {
            String strFinded = matcher.group(1);
            String strReplace = strFinded.replace("from", "####");
            tmpSql = tmpSql.replace(strFinded, strReplace);
        }

        Pattern groupPattern = Pattern.compile(".from.*group\\s+{1,}by\\s+{1,}.*");
        Matcher groupMatcher = groupPattern.matcher(sql.toLowerCase());

        if (groupMatcher.find()) {
            sql = "select count(1) as total from ( " + sql + " ) as t ";
        } else {
            int startIndex = tmpSql.indexOf("select");
            int endIndex = tmpSql.indexOf(" from ");
            String repaceSql = sql.substring(startIndex + 6, endIndex);
            sql = sql.replace(repaceSql, " count(1) as total ");
        }
        return sql;
    }

}
