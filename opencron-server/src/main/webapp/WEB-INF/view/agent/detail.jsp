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
        <li><a href="">执行器管理</a></li>
        <li><a href="">执行器详情</a></li>
    </ol>
    <h4 class="page-title"><i aria-hidden="true" class="fa fa-eye"></i>&nbsp;执行器详情</h4>
    <div class="block-area" id="defaultStyle">
        <button type="button" onclick="history.back()" class="btn btn-sm m-t-10" style="float: right;margin-bottom: 8px;"><i class="icon">&#61740;</i>&nbsp;返回</button>

        <table class="table tile textured">
            <tbody id="tableContent">
            <tr>
                <td class="item"><i class="glyphicon glyphicon-leaf"></i>&nbsp;执行器名：</td>
                <td>${agent.name}</td>
            </tr>

            <tr>
                <td class="item"><i class="glyphicon glyphicon-tag"></i>&nbsp;机&nbsp;器&nbsp;&nbsp;IP：</td>
                <td>${agent.ip}</td>
            </tr>
            <tr>
                <td class="item"><i class="glyphicon glyphicon-question-sign"></i>&nbsp;端&nbsp;口&nbsp;&nbsp;号：</td>
                <td>${agent.port}</td>
            </tr>
            <tr>
                <td class="item"><i class="glyphicon glyphicon-signal"></i>&nbsp;通信状态：</td>
                <td><c:if test="${agent.status eq false}">
                        <span class="label label-danger">&nbsp;&nbsp;失&nbsp;败&nbsp;&nbsp;</span>
                        <span style="font-size: 12px">&nbsp;&nbsp;&nbsp;&nbsp;失败时间：<fmt:formatDate value="${agent.failTime}" pattern="yyyy-MM-dd HH:mm:ss"/></span>
                    </c:if>
                    <c:if test="${agent.status eq true}">
                        <span class="label label-success">&nbsp;&nbsp;成&nbsp;功&nbsp;&nbsp;</span>
                    </c:if>
                </td>
            </tr>
            <tr>
                <td class="item"><i class="glyphicon glyphicon-warning-sign"></i>&nbsp;失联报警：</td>
                <td>
                    <c:if test="${agent.warning eq false}"><font color="red"><span class="span-self">否</span></font></c:if>
                    <c:if test="${agent.warning eq true}"><font color="green"><span class="span-self">是</span></font></c:if>
                </td>
            </tr>

            <tr>
                <td class="item"><i class="glyphicon glyphicon-comment"></i>&nbsp;报警手机：</td>
                <td>${agent.mobiles}</td>
            </tr>

            <tr>
                <td class="item"><i class="glyphicon glyphicon-envelope"></i>&nbsp;报警邮箱：</td>
                <td>${agent.emailAddress}</td>
            </tr>

            <tr>
                <td class="item"><i class="glyphicon glyphicon-magnet"></i>&nbsp;简&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;介：</td>
                <td>${agent.comment}</td>
            </tr>
            </tbody>

        </table>
    </div>
    </div>

</section>
<br/><br/>

<jsp:include page="/WEB-INF/common/footer.jsp"/>
