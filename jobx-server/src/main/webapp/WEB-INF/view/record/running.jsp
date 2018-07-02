<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="cron"  uri="http://www.jobx.org"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <script type="text/javascript">
        $(document).ready(function() {

            setInterval(function() {

                $("#highlight").fadeOut(3000,function(){
                    $(this).show();
                });

                ajax({
                    type: "post",
                    url:"${contextPath}/record/refresh.htm",
                    data:{
                        "size":"${size}",
                        "queryDate":"${record.queryDate}",
                        "agentId":"${record.agentId}",
                        "jobId":"${record.jobId}",
                        "execType":"${record.execType}",
                        "pageNo":${pageBean.pageNo},
                        "pageSize":${pageBean.pageSize}
                    },
                    dataType:"html"
                },function (data) {
                    //解决子页面登录失联,不能跳到登录页面的bug
                    if(data.indexOf("login")>-1){
                        window.location.href="${contextPath}";
                    }else {
                        $("#tableContent").html(data);
                    }
                });

            },5000);

            $("#size").change(function(){doUrl();});
            $("#agentId").change(function(){doUrl();});
            $("#jobId").change(function(){doUrl();});
            $("#execType").change(function(){doUrl();});
        });
        function doUrl() {
            var pageSize = $("#size").val()||${pageBean.pageSize};
            var queryDate = $("#queryDate").val();
            var agentId = $("#agentId").val();
            var jobId = $("#jobId").val();
            var execType = $("#execType").val();
            window.location.href = "${contextPath}/record/running.htm?queryDate=" + queryDate + "&agentId=" + agentId + "&jobId=" + jobId + "&execType=" + execType + "&pageSize=" + pageSize;
        }

        function killJob(id){
            swal({
                title: "",
                text: "您确定要结束这个作业吗？",
                type: "warning",
                showCancelButton: true,
                closeOnConfirm: false,
                confirmButtonText: "结束",
            }, function() {
                $("#process_"+id).html("停止中");
                ajax({
                    type: "post",
                    url:"${contextPath}/record/kill.do",
                    data:{"recordId":id}
                })
                alertMsg("结束请求已发送");
            });

        }

        function restartJob(id,jobId){
            swal({
                title: "",
                text: "您确定要结束并重启这个作业吗？",
                type: "warning",
                showCancelButton: true,
                closeOnConfirm: false,
                confirmButtonText: "重启",
            }, function() {
                $("#process_"+id).html("停止中");
                ajax({
                    type: "post",
                    url:"${contextPath}/record/kill.do",
                    data:{"recordId":id}
                },function (result) {
                    if (result.status){
                        ajax({
                            type: "post",
                            url:"${contextPath}/job/execute.do",
                            data:{"id":jobId}
                        })
                    }
                });
                alertMsg( "该作业已重启,正在执行中.");
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
        <li><a href="#">JobX</a></li>
        <li><a href="#">调度记录</a></li>
        <li><a href="#">正在运行</a></li>
    </ol>
    <h4 class="page-title"><i aria-hidden="true" class="fa fa-play-circle"></i>&nbsp;正在运行&nbsp;&nbsp;<span id="highlight" style="font-size: 14px"><img src='${contextPath}/static/img/icon-loader.gif' style="width: 14px;height: 14px">&nbsp;调度作业持续进行中...</span></h4>
    <div class="block-area" id="defaultStyle">

        <div>
            <div class="opt-bar" style="margin-bottom: 10px;margin-top: 0px;">
                <label for="agentId">执行器：</label>
                <select id="agentId" name="agentId" class="select-jobx w120">
                    <option value="">全部</option>
                    <c:forEach var="d" items="${agents}">
                        <option value="${d.agentId}" ${d.agentId eq record.agentId ? 'selected' : ''}>${d.name}</option>
                    </c:forEach>
                </select>
                &nbsp;&nbsp;&nbsp;
                <label for="jobId">作业名称：</label>
                <select id="jobId" name="jobId" class="select-jobx w80">
                    <option value="">全部</option>
                    <c:forEach var="t" items="${jobs}">
                        <option value="${t.jobId}" ${t.jobId eq record.jobId ? 'selected' : ''}>${t.jobName}&nbsp;</option>
                    </c:forEach>
                </select>
                &nbsp;&nbsp;&nbsp;
                <label for="execType">执行方式：</label>
                <select id="execType" name="execType" class="select-jobx w80">
                    <option value="">全部</option>
                    <option value="0" ${record.execType eq 0 ? 'selected' : ''}>自动</option>
                    <option value="1" ${record.execType eq 1 ? 'selected' : ''}>手动</option>
                    <option value="2" ${record.execType eq 2 ? 'selected' : ''}>接口</option>
                    <option value="3" ${record.execType eq 3 ? 'selected' : ''}>重跑</option>
                    <option value="4" ${record.execType eq 4 ? 'selected' : ''}>现场</option>
                </select>
                &nbsp;&nbsp;&nbsp;
                <label for="queryDate">开始时间：</label>
                <input type="text" id="queryDate" name="queryDate" value="${record.queryDate}" onfocus="WdatePicker({onpicked:function(){doUrl(); },dateFmt:'yyyy-MM-dd'})" class="Wdate select-jobx w90"/>
            </div>
        </div>

        <table class="table tile textured">
            <thead>
            <tr>
                <th>作业名称</th>
                <th>执行器</th>
                <th>运行状态</th>
                <th>执行方式</th>
                <th>执行命令</th>
                <th>开始时间</th>
                <th>运行时长</th>
                <th>作业类型</th>
                <th class="text-center">操作</th>
            </tr>
            </thead>

            <tbody id="tableContent">

            <c:forEach var="r" items="${pageBean.result}" varStatus="index">
                <tr>
                    <td>
                        <c:if test="${r.execType ne 4}"><a href="${contextPath}/job/detail/${r.jobId}.htm">${r.jobName}</a></c:if>
                        <c:if test="${r.execType eq 4}"><span class="label label-primary">&nbsp;&nbsp;现&nbsp;场&nbsp;&nbsp;</span></c:if>
                    </td>
                    <td><a href="${contextPath}/agent/detail/${r.agentId}.htm">${r.agentName}</a></td>
                    <td>
                        <div class="progress progress-striped progress-success active" style="margin-top:3px;width: 80%;height: 14px;" >
                            <div style="width:100%;height: 100%;" class="progress-bar">
                                <span id="process_${r.recordId}">
                                    <c:if test="${r.status eq 0}">运行中</c:if>
                                    <c:if test="${r.status eq 2}">停止中</c:if>
                                    <c:if test="${r.status eq 4}">重跑中</c:if>
                                </span>
                            </div>
                        </div>
                    </td>
                    <td>
                        <c:if test="${r.execType eq 0}"><span class="label label-default">&nbsp;&nbsp;自&nbsp;动&nbsp;&nbsp;</span></c:if>
                        <c:if test="${r.execType eq 1}"><span class="label label-default">&nbsp;&nbsp;手&nbsp;动&nbsp;&nbsp;</span></c:if>
                        <c:if test="${r.execType eq 2}"><span class="label label-default">&nbsp;&nbsp;接&nbsp;口&nbsp;&nbsp;</span></c:if>
                        <c:if test="${r.execType eq 3}"><span class="label label-default">&nbsp;&nbsp;重&nbsp;跑&nbsp;&nbsp;</span></c:if>
                        <c:if test="${r.execType eq 4}"><span class="label label-default">&nbsp;&nbsp;现&nbsp;场&nbsp;&nbsp;</span></c:if>
                    </td>

                    <td style="width: 25%" title="${cron:escapeHtml(r.command)}">
                        <div class="jobx_command">${cron:escapeHtml(r.command)}</div>
                    </td>

                    <td><fmt:formatDate value="${r.startTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                    <td>${cron:diffdate(r.startTime,r.endTime)}</td>
                    <td>
                        <c:if test="${r.jobType eq 1}">流程作业</c:if>
                        <c:if test="${r.jobType eq 0}">单一作业</c:if>
                    </td>
                    <td class="text-center">
                        <div class="visible-md visible-lg hidden-sm hidden-xs action-buttons">
                            <a href="#" onclick="killJob('${r.recordId}')" title="kill">
                                <i class="glyphicon glyphicon-stop"></i>
                            </a>&nbsp;&nbsp;
                            <c:if test="${r.status ne 4}">
                                <a href="#" onclick="restartJob('${r.recordId}','${r.jobId}')" title="结束并重启">
                                    <i class="glyphicon glyphicon-refresh"></i>
                                </a>&nbsp;&nbsp;
                            </c:if>
                        </div>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>

        <cron:pager href="${contextPath}/record/running.htm?queryDate=${record.queryDate}&agentId=${record.agentId}&jobId=${record.jobId}&execType=${record.execType}" id="${pageBean.pageNo}" size="${pageBean.pageSize}" total="${pageBean.totalCount}"/>

    </div>

</section>

</body>

</html>