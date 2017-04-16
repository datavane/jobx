<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="cron"  uri="http://www.opencron.org"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/common/resource.jsp"/>
</head>
<jsp:include page="/WEB-INF/common/top.jsp"/>

<!-- Content -->
<section id="content" class="container">

    <!-- Messages Drawer -->
    <jsp:include page="/WEB-INF/common/message.jsp"/>

    <!-- Breadcrumb -->
    <ol class="breadcrumb hidden-xs">
        <li class="icon">&#61753;</li>
        当前位置：
        <li><a href="">opencron</a></li>
        <li><a href="">调度记录</a></li>
        <li><a href="">记录详情</a></li>
    </ol>
    <h4 class="page-title"><i aria-hidden="true" class="fa fa-print"></i>&nbsp;记录详情</h4>
    <div class="block-area" id="defaultStyle">
        <button type="button" onclick="history.back()" class="btn btn-sm m-t-10" style="float: right;margin-bottom: 8px;"><i class="icon">&#61740;</i>&nbsp;返回</button>

        <table class="table tile textured">
            <tbody id="tableContent">
            <tr>
                <td><i class="glyphicon glyphicon-tasks"></i>&nbsp;任务名称</td>
                <td>
                    <c:if test="${empty record.jobName}">batchJob</c:if>
                    <c:if test="${!empty record.jobName}"><a href="${contextPath}/job/detail?id=${record.jobId}&csrf=${csrf}">${record.jobName}</a></c:if>
                </td>
                <td><i class="glyphicon glyphicon-th-large"></i>&nbsp;执行命令</td>
                <td>${record.command}</td>
            </tr>

            <tr>
                <td><i class="glyphicon glyphicon-leaf"></i>&nbsp;执&nbsp;&nbsp;行&nbsp;&nbsp;器</td>
                <td><a href="${contextPath}/agent/detail?id=${record.agentId}&csrf=${csrf}">${record.agentName}</a></td>

                <td><i class="glyphicon glyphicon-user"></i>&nbsp;作&nbsp;&nbsp;业&nbsp;&nbsp;人</td>
                <td>
                    <c:if test="${permission eq true}"><a href="${contextPath}/user/detail?userId=${record.userId}&csrf=${csrf}">${record.operateUname}</a></c:if>
                    <c:if test="${permission eq false}">${record.operateUname}</c:if></td>
            </tr>
            <tr>
                <td><i class="glyphicon glyphicon-hdd"></i>&nbsp;机&nbsp;&nbsp;器&nbsp;&nbsp;IP</td>
                <td>${record.ip}</td>
                <td><i class="glyphicon glyphicon-play"></i>&nbsp;开始时间</td>
                <td><fmt:formatDate value="${record.startTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
            </tr>
            <tr>
                <td><i class="glyphicon glyphicon-question-sign"></i>&nbsp;执行状态</td>
                <td>
                    <c:if test="${record.success eq 1}">
                        <span class="label label-success">&nbsp;&nbsp;成&nbsp;功&nbsp;&nbsp;</span>
                    </c:if>
                    <c:if test="${record.success eq 0}">
                        <span class="label label-danger">&nbsp;&nbsp;失&nbsp;败&nbsp;&nbsp;</span>
                    </c:if>
                    <c:if test="${record.success eq 2}">
                        <span class="label label-warning">&nbsp;&nbsp;被&nbsp;杀&nbsp;&nbsp;</span>
                    </c:if>
                    <c:if test="${record.success eq 3}">
                        <span class="label label-warning">&nbsp;&nbsp;超&nbsp;时&nbsp;&nbsp;</span>
                    </c:if>
                </td>

                <td><i class="glyphicon glyphicon-stop"></i>&nbsp;结束时间</td>
                <td><fmt:formatDate value="${record.endTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
            </tr>
            <tr>
                <td><i class="glyphicon glyphicon-filter"></i>&nbsp;返&nbsp;&nbsp;回&nbsp;&nbsp;值</td>
                <td>${record.returnCode}</td>

                <td><i class="glyphicon glyphicon-tint"></i>&nbsp;运行时长</td>
                <td>${cron:diffdate(record.startTime,record.endTime)}</td>
            </tr>
            <tr>
                <td><i class="glyphicon glyphicon-info-sign"></i>&nbsp;执行方式</td>
                <td>
                    <c:if test="${record.execType eq 0}">自动</c:if>
                    <c:if test="${record.execType eq 1}">手动</c:if>
                    <c:if test="${record.execType eq 2}">重跑</c:if>
                    <c:if test="${record.execType eq 3}">现场</c:if>
                </td>

                <td><i class="glyphicon glyphicon-repeat"></i>&nbsp;重跑次数</td>
                <td>${record.redoCount}</td>
            </tr>

            <tr>
                <td colspan="4">
                    <i class="glyphicon glyphicon-envelope"></i>&nbsp;<strong>返回信息</strong></p>
                    <pre id="pre" style="font-size:11px;color:#FFF;border: none;background: none;white-space: pre-wrap;word-wrap: break-word;">${record.message}</pre>
                </td>
            </tr>
            </tbody>

        </table>
    </div>

</section>
<br/><br/>

<jsp:include page="/WEB-INF/common/footer.jsp"/>
