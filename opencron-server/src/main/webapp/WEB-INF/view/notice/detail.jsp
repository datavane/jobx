<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="cron"  uri="http://www.opencron.org"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/common/resource.jsp"/>
    <style type="text/css">
        ::selection {
            background:#d3d3d3;
            color:#555;
        }
        ::-moz-selection {
            background:#d3d3d3;
            color:#555;
        }

    </style>

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
        <li><a href="">告警日志</a></li>
    </ol>
    <h4 class="page-title"><i class="glyphicon glyphicon-eye-open"></i>&nbsp;告警详情</h4>

    <div class="block-area" id="defaultStyle">
        <button type="button" onclick="history.back()" class="btn btn-sm m-t-10" style="float: right;margin-bottom: 8px;"><i class="icon">&#61740;</i>&nbsp;返回</button>
        <table class="table tile textured">
            <tbody id="tableContent">
            <tr>
                <td width="150px;">
                    <c:if test="${log.type eq 0}"><i style="font-Size:15px;" class="glyphicons glyphicons-message-plus"></i></c:if>
                    <c:if test="${log.type eq 1}"><i style="font-Size:15px;" class="glyphicons glyphicons-chat"></i></c:if>
                    <c:if test="${log.type eq 2}"><i style="font-Size:15px;" class="glyphicons glyphicon-link"></i></c:if>
                    &nbsp;通知类型
                </td>
                <td>
                    <c:if test="${log.type eq 0}">邮件</c:if>
                    <c:if test="${log.type eq 1}">短信</c:if>
                    <c:if test="${log.type eq 2}">站内信</c:if>
                </td>
            </tr>

            <tr>
                <td><i style="font-Size:15px;" class="glyphicons glyphicons-alarm"></i>&nbsp;&nbsp;发送时间</td>
                <td><fmt:formatDate value="${log.sendTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
            </tr>

            <tr>
                <td width="150px;"><i style="font-Size:15px;" class="glyphicons glyphicons-user-add"></i>
                    <c:if test="${log.type eq 0}">&nbsp;收&nbsp;&nbsp;件&nbsp;&nbsp;人</c:if>
                    <c:if test="${log.type ne 0}">&nbsp;收&nbsp;&nbsp;信&nbsp;&nbsp;人</c:if>
                <td>${log.receiver}</td>
            </tr>

            <tr>
                <td colspan="2">
                    <i  style="font-Size:15px;" class="glyphicons glyphicons-eye-open"></i>&nbsp;&nbsp;<strong>&nbsp;信&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;息</strong></p>
                    <pre id="pre" style="font-size:11px;color:#FFF;border: none;background: none;white-space: pre-wrap;word-wrap: break-word;">${log.message}</pre>
                </td>
            </tr>
            </tbody>
        </table>
    </div>

</section>
<br/><br/>

<jsp:include page="/WEB-INF/common/footer.jsp"/>
