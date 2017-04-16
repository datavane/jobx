<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="cron"  uri="http://www.opencron.org"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<c:forEach var="w" items="${pageBean.result}" varStatus="index">
    <tr>
        <td id="name_${w.agentId}">${w.name}</td>
        <td>${w.ip}</td>
        <td id="port_${w.agentId}">${w.port}</td>
        <td id="agent_${w.agentId}">
            <c:if test="${w.status eq false}">
                <span class="label label-danger">&nbsp;&nbsp;失&nbsp;败&nbsp;&nbsp;</span>
            </c:if>
            <c:if test="${w.status eq true}">
                <span class="label label-success">&nbsp;&nbsp;成&nbsp;功&nbsp;&nbsp;</span>
            </c:if>
        </td>
        <td id="warning_${w.agentId}">
            <c:if test="${w.warning eq false}"><span class="label label-default" style="color: red;font-weight:bold">&nbsp;&nbsp;否&nbsp;&nbsp;</span>  </c:if>
            <c:if test="${w.warning eq true}"><span class="label label-warning" style="color: white;font-weight:bold">&nbsp;&nbsp;是&nbsp;&nbsp;</span> </c:if>
        </td>
        <td id="connType_${w.agentId}">
            <c:if test="${w.proxy eq 0}">直连</c:if>
            <c:if test="${w.proxy eq 1}">代理</c:if>
        </td>
        <td>
            <center>
                <div class="visible-md visible-lg hidden-sm hidden-xs action-buttons">
                    <a href="${contextPath}/job/addpage?id=${w.agentId}&csrf=${csrf}" title="新任务">
                        <i aria-hidden="true" class="fa fa-plus-square-o"></i>
                    </a>&nbsp;&nbsp;
                    <c:if test="${permission eq true}">
                        <a href="#" onclick="edit('${w.agentId}')" title="编辑">
                            <i aria-hidden="true" class="fa fa-edit"></i>
                        </a>&nbsp;&nbsp;
                        <a href="#" onclick="editPwd('${w.agentId}')" title="修改密码">
                            <i aria-hidden="true" class="fa fa-lock"></i>
                        </a>&nbsp;&nbsp;
                    </c:if>
                    <a href="${contextPath}/agent/detail?id=${w.agentId}&csrf=${csrf}" title="查看详情">
                        <i aria-hidden="true" class="fa fa-eye"></i>
                    </a>
                </div>
            </center>
        </td>
    </tr>
</c:forEach>