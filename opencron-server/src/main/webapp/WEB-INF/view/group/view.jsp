<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="cron"  uri="http://www.opencron.org"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<!DOCTYPE html>
<html lang="en">
<head></head>
<body>
<!-- Content -->
<section id="content" class="container">

    <!-- Messages Drawer -->
    <jsp:include page="/WEB-INF/layouts/message.jsp"/>

    <!-- Breadcrumb -->
    <ol class="breadcrumb hidden-xs">
        <li class="icon">&#61753;</li>
        当前位置：
        <li><a href="">opencron</a></li>
        <li><a href=""> 执行器组管理</a></li>
    </ol>
    <h4 class="page-title"><i aria-hidden="true" class="fa fa-group"></i>&nbsp;分组管理</h4>
    <div class="block-area" id="defaultStyle">

        <div style="float: left">
            <label>
                每页 <select size="1" class="select-opencron" id="size" style="width: 50px;">
                <option value="15">15</option>
                <option value="30" ${pageBean.pageSize eq 30 ? 'selected' : ''}>30</option>
                <option value="50" ${pageBean.pageSize eq 50 ? 'selected' : ''}>50</option>
                <option value="100" ${pageBean.pageSize eq 100 ? 'selected' : ''}>100</option>
            </select> 条记录
            </label>
        </div>

        <div style="float: right;margin-top: -10px">
            <a href="${contextPath}/group/add.htm?csrf=${csrf}" class="btn btn-sm m-t-10" style="margin-left: 50px;margin-bottom: 8px"><i class="icon">&#61943;</i>添加</a>
        </div>

        <table class="table tile textured">
            <thead>
            <tr>
                <th>分组名称</th>
                <th>机器台数</th>
                <th>创建人</th>
                <th>创建时间</th>
                <th>备注信息</th>
                <th><center>操作</center></th>
            </tr>
            </thead>

            <tbody>
                <c:forEach var="g" items="${pageBean.result}" varStatus="index">
                    <tr>
                        <td>${g.groupName}</td>
                        <td>${g.agentCount}</td>
                        <td>${g.userName}</td>
                        <td><fmt:formatDate value="${g.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                        <td>${g.comment}</td>
                        <td>
                            <center>
                                <a href="${contextPath}/group/edit/${g.groupId}.htm?csrf=${csrf}" title="编辑">
                                    <i class="glyphicon glyphicon-pencil"></i>
                                </a>&nbsp;&nbsp;
                                <a href="${contextPath}/group/detail/${g.groupId}.htm?csrf=${csrf}" title="查看详情">
                                    <i class="glyphicon glyphicon-eye-open"></i>
                                </a>
                            </center>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <cron:pager href="${contextPath}/group/view.htm?csrf=${csrf}" id="${pageBean.pageNo}" size="${pageBean.pageSize}" total="${pageBean.totalCount}"/>
    </div>

</section>

</body>
</html>
