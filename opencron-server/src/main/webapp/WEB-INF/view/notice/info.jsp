<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="cron"  uri="http://www.opencron.org"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%
    String port = request.getServerPort() == 80 ? "" : (":"+request.getServerPort());
    String path = request.getContextPath().replaceAll("/$","");
    String contextPath = request.getScheme()+"://"+request.getServerName()+port+path;
    pageContext.setAttribute("contextPath",contextPath);
%>

<c:forEach var="m" items="${message}" varStatus="index">

    <div class="media">
        <div class="pull-left">
            <i class="icon" style="font-size: 28px;color:rgba(255,255,255,0.75)">
            <c:if test="${m.type eq 0}">&#61880;</c:if>
            <c:if test="${m.type eq 1}">&#61704;</c:if>
            <c:if test="${m.type eq 2}">&#61884;</c:if>
            </i>
        </div>
        <div class="media-body">
            <small class="text-muted">opencron告警 - <fmt:formatDate value="${m.sendTime}" pattern="yyyy-MM-dd HH:mm:ss"/></small><br>
            <a class="t-overflow" href="${contextPath}/notice/detail/${m.logId}.htm?csrf=${csrf}">${cron:escapeHtml(m.message)}</a>
        </div>
    </div>

</c:forEach>
