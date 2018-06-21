<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="cron"  uri="http://www.jobx.org"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <script type="text/javascript" src="${contextPath}/static/js/clipboard.js?resId=${resourceId}"></script> <!-- jQuery Library -->

    <script type="text/javascript">
        token = "${job.token}";
        $(document).ready(function () {
            var clipboard =  new Clipboard('#copy-btn',{
                text:function () {
                    return "${contextPath}/api/run?jobId=${job.jobId}&token="+token+"&url=";
                }
            });
            clipboard.on('success', function (e) {
                e.clearSelection();
                $("#copy-btn").text(" 已复制");
                setTimeout(function () {
                    $("#copy-btn").text("");
                }, 2000);
            });
        });

        function changeToken(jobId) {
            swal({
                title: "",
                text: "您确定要更新该任务的token吗?",
                type: "warning",
                showCancelButton: true,
                closeOnConfirm: false,
                confirmButtonText: "更新"
            },function () {
                ajax({
                    type: "post",
                    url: "${contextPath}/job/token.do",
                    data: {
                        "jobId": jobId,
                    }
                },function (data) {
                    alertMsg("更新token成功!");
                    token = data.token;
                    $("#token").text(data.token);
                });
            });
        }
    </script>
</head>

<body>
<!-- Content -->
<section id="content" class="container">

    <!-- Messages Drawer -->
    <jsp:include page="/WEB-INF/layouts/message.jsp"/>

    <!-- Breadcrumb -->
    <ol class="breadcrumb hidden-xs">
        <li class="icon">&#61753;</li>
        当前位置：
        <li><a href="">JobX</a></li>
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
                <td><a href="${contextPath}/agent/detail/${job.agentId}.htm">${job.agentName}</a></td>
            </tr>

            <tr>
                <td class="item"><i class="glyphicon glyphicon-filter"></i>&nbsp;时间规则：</td>
                <td>${job.cronExp}</td>
            </tr>

            <tr>
                <td class="item"><i class="glyphicon glyphicon-link"></i>&nbsp;API&nbsp;地 址：</td>
                <td>
                    ${contextPath}/api/run?jobId=${job.jobId}&token=<span id="token">${job.token}</span>
                        &nbsp;&nbsp;&nbsp;&nbsp;
                        <c:if test="${job.jobType eq 0}">
                            <a title="更新token" href="#" >
                                <i class="glyphicon glyphicon-refresh" onclick="changeToken(${job.jobId})" title="更新token"></i>
                            </a>&nbsp;&nbsp;
                        </c:if>
                        <i class="fa fa-copy" id="copy-btn" data-clipboard-action="copy" aria-label="已复制"></i>
                </td>
            </tr>

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

</body>

</html>
