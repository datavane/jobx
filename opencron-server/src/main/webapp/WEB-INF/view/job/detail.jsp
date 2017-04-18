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
        <li><a href="">作业管理</a></li>
        <li><a href="">作业详情</a></li>
    </ol>
    <h4 class="page-title"><i class="fa fa-eye" aria-hidden="true"></i>&nbsp;作业详情</h4>

    <div style="float: right;margin-top: 5px">
        <a onclick="history.back();" class="btn btn-sm m-t-10" style="margin-right: 16px;margin-bottom: -4px"><i class="fa fa-mail-reply" aria-hidden="true"></i>&nbsp;返回</a>
    </div>

    <div class="block-area" id="defaultStyle">

        <table class="table tile textured">
            <tbody id="tableContent">
            <tr>
                <td class="item"><i class="glyphicon glyphicon-tasks"></i>&nbsp;作业名称：</td>
                <td>${job.jobName}</td>
            </tr>

            <tr>
                <td class="item"><i class="glyphicon  glyphicon-random"></i>&nbsp;作业类型：</td>
                <td>
                    <c:if test="${job.jobType eq 0}"><span class="span-self">单一作业</span></c:if>
                    <c:if test="${job.jobType eq 1}"><span class="span-self">流程作业</span></c:if>
                </td>
            </tr>
            <tr>
                <td class="item"><i class="glyphicon glyphicon-leaf"></i>&nbsp;执&nbsp;行&nbsp;&nbsp;器：</td>
                <td>${job.agentName}</td>
            </tr>
            <tr>
                <td class="item"><i class="glyphicon glyphicon-info-sign"></i>&nbsp;运行模式：</td>
                <td>
                    <c:if test="${job.execType eq 0}"><font color="green"><span class="span-self">自动模式</span></font></c:if>
                    <c:if test="${job.execType eq 1}"><font color="red"><span class="span-self">手动模式</span></font></c:if>
                </td>
            </tr>

            <c:if test="${job.execType eq 0}">
            <tr>
                <td class="item"><i class="glyphicon glyphicon-bookmark"></i>&nbsp;规则类型：</td>
                <td>
                    <c:if test="${job.cronType eq 0}"><span class="span-self">crontab</span></c:if>
                    <c:if test="${job.cronType eq 1}"><span class="span-self">quartz</span></c:if>
                </td>
            </tr>

            <tr>
                <td class="item"><i class="glyphicon glyphicon-filter"></i>&nbsp;时间规则：</td>
                <td>${job.cronExp}</td>
            </tr>
            </c:if>

            <tr>
                <td class="item"><i class="glyphicon glyphicon-th-large"></i>&nbsp;执行命令：</td>
                <td>${cron:escapeHtml(job.command)}</td>
            </tr>

            <tr>
                <td class="item"><i class="glyphicon  glyphicon glyphicon-forward"></i>&nbsp;重新执行：</td>
                <td>
                    <c:if test="${job.redo eq 0}"><font color="green"><span class="span-self">否</span></font></c:if>
                    <c:if test="${job.redo eq 1}"><font color="red"><span class="span-self">是</span></font></c:if>
                </td>
            </tr>

            <c:if test="${job.redo eq 1}">
            <tr>
                <td class="item"><i class="glyphicon glyphicon-repeat"></i>&nbsp;重跑次数：</td>
                <td>${job.runCount}</td>
            </tr>
            </c:if>

            <tr>
                <td class="item"><i class="glyphicon glyphicon-magnet"></i>&nbsp;描&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;述：</td>
                <td>${job.comment}</td>
            </tr>
            </tbody>

        </table>
    </div>

</section>
<br/><br/>

<jsp:include page="/WEB-INF/common/footer.jsp"/>
