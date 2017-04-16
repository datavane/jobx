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

import org.apache.commons.lang3.StringUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

public class PagerTag extends SimpleTagSupport {

    private String href;

    private int id;// 当前页
    private int size;// 每页条数
    private int total;// 总页数

    private String idParameterName;
    private String sizeParameterName;


    @Override
    public void doTag() throws JspException, IOException {
        JspWriter out = getJspContext().getOut();

        if (StringUtils.isEmpty(idParameterName)) {
            idParameterName = "pageNo";
        }
        if (StringUtils.isEmpty(sizeParameterName)) {
            sizeParameterName = "pageSize";
        }

        if (!href.endsWith("?") && !href.endsWith("&")) {
            if (href.indexOf("?") == -1) {
                href = href + "?";
            } else {
                href = href + "&";
            }
        }

        int pageTotal = 0;
        if ((total % size) == 0) {
            pageTotal = total / size;
        } else {
            pageTotal = (total / size) + 1;
        }

        if (id <= 0) {
            id = 1;
        } else if (id > pageTotal) {
            id = pageTotal;
        }

        //不显示分页
        if (getTotal() <= 0) {
            super.doTag();
            return;
        }
        out.append("<ul class='pagination fr mr20'>");
        // 首页
        if (id == 1) {
            wrapSpan(out, "首页", -1);
        } else {
            wrapLink(out, href, 1, "首页");
        }


        // 上一页
        if (id == 1) {
            wrapSpan(out, "上一页", -1);
        } else {
            wrapLink(out, href, id - 1, "上一页");
        }

        int offsetNum = 5;
        int startId;
        if (id < offsetNum * 2 + 1) {
            startId = 1;
        } else {
            startId = id - offsetNum;
        }

        int endId = id + offsetNum;
        if (endId > pageTotal) {
            endId = pageTotal;
        }

        for (int i = startId; i <= endId; i++) {
            if (i == id) {
                wrapSpan(out, String.valueOf(i), 1);
            } else {
                wrapLink(out, href, i, String.valueOf(i));
            }
        }


        // 下一页
        if (id == pageTotal) {
            wrapSpan(out, "下一页", -1);
        } else {
            wrapLink(out, href, id + 1, "下一页");
        }

        // 末页
        if (id == pageTotal) {
            wrapSpan(out, "末页", -1);
        } else {
            wrapLink(out, href, pageTotal, "末页");
        }
        out.append("</ul>");
        super.doTag();
    }

    private void wrapLink(JspWriter out, String href, int curr, String title)
            throws IOException {
        out.append("<li><a href=\"").append(href).append(idParameterName).append("=")
                .append("" + curr).append("&").append(sizeParameterName).append("=")
                .append("" + size).append("\">").append(title).append("</a></li>");
    }

    private void wrapSpan(JspWriter out, String title, int status) throws IOException {
        if (status == 1) {
            out.append("<li class='active'>");
        } else if (status == -1) {
            out.append("<li class='disabled'>");
        } else {
            out.append("<li>");
        }
        out.append("<a href='javascript:void(0);'>");
        out.append(title);
        out.append("</a></li>");
    }


    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }


    public String getIdParameterName() {
        return idParameterName;
    }

    public void setIdParameterName(String idParameterName) {
        this.idParameterName = idParameterName;
    }

    public String getSizeParameterName() {
        return sizeParameterName;
    }

    public void setSizeParameterName(String sizeParameterName) {
        this.sizeParameterName = sizeParameterName;
    }

}
