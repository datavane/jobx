<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="cron"  uri="http://www.jobx.org"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%
    String port = request.getServerPort() == 80 ? "" : (":"+request.getServerPort());
    String path = request.getContextPath().replaceAll("/$","");
    String contextPath = request.getScheme()+"://"+request.getServerName()+port+path;
    pageContext.setAttribute("contextPath",contextPath);
%>

<c:forEach var="w" items="${pageBean.result}" varStatus="index">
    <tr>
        <td id="name_${w.agentId}">${w.name}</td>
        <td>${w.host}</td>
        <td id="port_${w.agentId}">${w.port}</td>
        <td id="agent_${w.agentId}">
            <c:if test="${w.status eq 0}">
                <span class="label label-danger">&nbsp;&nbsp;失&nbsp;联&nbsp;&nbsp;</span>
            </c:if>
            <c:if test="${w.status eq 1}">
                <span class="label label-success pong_${w.agentId}">&nbsp;&nbsp;正&nbsp;常&nbsp;&nbsp;</span>
            </c:if>
            <c:if test="${w.status eq 2}">
                <span class="label label-danger">&nbsp;密码错误&nbsp;</span>
            </c:if>
        </td>
        <td id="warning_${w.agentId}">
            <c:if test="${w.warning eq false}"><span class="label label-default" style="color: red;font-weight:bold">&nbsp;&nbsp;否&nbsp;&nbsp;</span>  </c:if>
            <c:if test="${w.warning eq true}"><span class="label label-warning" style="color: white;font-weight:bold">&nbsp;&nbsp;是&nbsp;&nbsp;</span> </c:if>
        </td>
        <td id="connType_${w.agentId}">
            <c:if test="${w.proxyId eq null}">直连</c:if>
            <c:if test="${w.proxyId ne null}">代理</c:if>
        </td>
        <td class="text-center">
            <div class="visible-md visible-lg hidden-sm hidden-xs action-buttons">
                <a href="${contextPath}/job/add.htm?id=${w.agentId}" title="新任务">
                    <i aria-hidden="true" class="fa fa-plus-square-o"></i>
                </a>&nbsp;&nbsp;
                <c:if test="${permission eq true}">
                    <a href="#" onclick="upload(${w.agentId})" title="上传文件"><i aria-hidden="true" class="fa fa-upload"></i></a>&nbsp;&nbsp;
                    <a href="#" onclick="edit('${w.agentId}')" title="编辑"><i aria-hidden="true" class="fa fa-edit"></i></a>&nbsp;&nbsp;
                    <a href="#" onclick="editPwd('${w.agentId}')" title="修改密码"><i aria-hidden="true" class="fa fa-lock"></i></a>&nbsp;&nbsp;
                    <a href="#" onclick="remove('${w.agentId}')" title="删除"><i aria-hidden="true" class="fa fa-times"></i></a>&nbsp;&nbsp;
                </c:if>
                <a href="${contextPath}/detail/${w.agentId}.htm" title="查看详情">
                    <i aria-hidden="true" class="fa fa-eye"></i>
                </a>
            </div>
        </td>
    </tr>
</c:forEach>