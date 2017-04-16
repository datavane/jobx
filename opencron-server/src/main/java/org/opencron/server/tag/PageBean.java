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


package org.opencron.server.tag;

import org.opencron.common.utils.CommonUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class PageBean<T> implements Serializable {

    private static final long serialVersionUID = 8199494836903931588L;

    // -- 公共变量 --//
    public static final String ASC = "asc";
    public static final String DESC = "desc";

    // -- 分页参数 --//
    protected Serializable pageNo = 1;
    protected Serializable pageSize = 15;
    protected String orderBy = null;
    protected String order = null;
    protected boolean autoCount = true;

    // -- 返回结果 --//
    protected List<T> result = Collections.emptyList();
    protected long totalCount = -1;

    // -- 构造函数 --//
    public PageBean() {
    }

    public PageBean(final int pageSize) {
        setPageSize(pageSize);
    }


    public PageBean(final Integer pageNo, final Integer pageSize) {
        if (pageNo != null) {
            setPageNo(pageNo);
        }
        if (pageSize != null) {
            setPageSize(pageSize);
        }
    }

    public PageBean(final int pageSize, final boolean autoCount) {
        setPageSize(pageSize);
        setAutoCount(autoCount);
    }

    // -- 访问查询参数函数 --//

    /**
     * 获得当前页的页号,序号从1开始,默认为1.
     */
    public int getPageNo() {
        return CommonUtils.toInt(pageNo,1);
    }

    /**
     * 设置当前页的页号,序号从1开始,低于1时自动调整为1.
     */
    public void setPageNo(final Serializable pageNo) {
        this.pageNo = pageNo;
        if (CommonUtils.toInt(pageNo,1) < 1) {
            this.pageNo = 1;
        }
    }

    /**
     * 获得每页的记录数量,默认为15.
     */
    public int getPageSize() {
        return CommonUtils.toInt(this.pageSize,15);
    }

    /**
     * 设置每页的记录数量,低于1时自动调整为1.
     */
    public void setPageSize(final Serializable pageSize) {
        this.pageSize = pageSize;

        if (CommonUtils.toInt(pageSize,15) < 1) {
            this.pageSize = 1;
        }
    }

    /**
     * 根据pageNo和pageSize计算当前页第一条记录在总结果集中的位置,序号从1开始.
     */
    public int getFirst() {
        return ((CommonUtils.toInt(pageNo,1) - 1) * CommonUtils.toInt(pageSize,15)) + 1;
    }

    /**
     * 获得排序字段,无默认值.多个排序字段时用','分隔.
     */
    public String getOrderBy() {
        return orderBy;
    }

    /**
     * 设置排序字段,多个排序字段时用','分隔.
     */
    public void setOrderBy(final String orderBy) {
        this.orderBy = orderBy;
    }

    /**
     * 是否已设置排序字段,无默认值.
     */
    public boolean isOrderBySetted() {
        return CommonUtils.notEmpty(orderBy,order);
    }

    /**
     * 获得排序方向.
     */
    public String getOrder() {
        return order == null?ASC:order.equals(ASC)?ASC:DESC;
    }

    /**
     * 设置排序方式向.
     *
     * @param order
     *            可选值为desc或asc,多个排序字段时用','分隔.
     */
    public void setOrder(final String order) {
        this.order = StringUtils.lowerCase(order);
    }

    /**
     * 查询对象时是否自动另外执行count查询获取总记录数, 默认为false.
     */
    public boolean isAutoCount() {
        return autoCount;
    }

    /**
     * 查询对象时是否自动另外执行count查询获取总记录数.
     */
    public void setAutoCount(final boolean autoCount) {
        this.autoCount = autoCount;
    }

    // -- 访问查询结果函数 --//

    /**
     * 取得页内的记录列表.
     */
    public List<T> getResult() {
        return result;
    }

    /**
     * 设置页内的记录列表.
     */
    public void setResult(final List<T> result) {
        this.result = result;
    }

    /**
     * 取得总记录数, 默认值为-1.
     */
    public long getTotalCount() {
        return totalCount;
    }

    /**
     * 设置总记录数.
     */
    public void setTotalCount(final long totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * 根据pageSize与totalCount计算总页数, 默认值为-1.
     */
    public long getTotalPages() {
        if (totalCount < 0)
            return -1;

        long count = totalCount / CommonUtils.toInt(pageSize);
        if (totalCount % CommonUtils.toInt(pageSize) > 0) {
            count++;
        }
        return count;
    }

    /**
     * 是否还有下一页.
     */
    public boolean hasNext() {
        return (CommonUtils.toInt(pageNo) + 1 <= getTotalPages());
    }

    /**
     * 取得下页的页号, 序号从1开始. 当前页为尾页时仍返回尾页序号.
     */
    public int getNextPage() {
        if (hasNext())
            return CommonUtils.toInt(pageNo) + 1;
        else
            return CommonUtils.toInt(pageNo);
    }

    /**
     * 是否还有上一页.
     */
    public boolean hasPre() {
        return (CommonUtils.toInt(pageNo) - 1 >= 1);
    }

    /**
     * 取得上页的页号, 序号从1开始. 当前页为首页时返回首页序号.
     */
    public int getPrePage() {
        if (hasPre())
            return CommonUtils.toInt(pageNo) - 1;
        else
            return CommonUtils.toInt(pageNo);
    }

    public void verifyOrderBy(String defultOrderBy, String...orderBys) {
        if (this.getOrderBy() == null) {
            this.setOrderBy(defultOrderBy);
        }else {
            String orderBy = this.getOrderBy();
            for(String field:orderBys){
                if (orderBy.equals(field)) {
                    this.setOrderBy(orderBy);
                    return;
                }
            }
            this.setOrderBy(defultOrderBy);
        }
    }

}