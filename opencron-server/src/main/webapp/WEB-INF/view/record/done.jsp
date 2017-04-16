<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="cron"  uri="http://www.opencron.org"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/common/resource.jsp"/>

    <style type="text/css">
        table{
            border-collapse: collapse;
        }

        .down{
            height: 12px;
            margin-bottom: 5px;
            margin-top: 0;
            width: 30px
        }
        .div-circle{
            width:15px;
            height:15px;
            background-color:#66c2a5;
            margin-top: 1px;
            margin-bottom: 1px;
            -moz-border-radius: 25px !important;
            -webkit-border-radius: 25px !important;
            border-radius:25px !important;
        }
        .span-circle{
            height:15px;
            line-height:15px;
            display:block;
            color:#FFF;
            text-align:center;
            font-size: 10px;
        }
        .opencron_command{display: none;overflow:hidden; text-overflow:ellipsis; white-space: nowrap;}
    </style>

    <script type="text/javascript">
        function rewidth() {
            var width = $(window).width();
            $(".opencron_command").show().css("width",500+(width-1500)+"px");
        }

        $(document).ready(function(){
            $("#size").change(function(){doUrl();});
            $("#success").change(function(){doUrl();});
            $("#agentId").change(function(){doUrl();});
            $("#jobId").change(function(){doUrl();});
            $("#execType").change(function(){doUrl();});
            rewidth();
            $(window).resize(rewidth);
        });

        function doUrl() {
            var pageSize = $("#size").val();
            var queryTime = $("#queryTime").val();
            var success = $("#success").val();
            var agentId = $("#agentId").val();
            var jobId = $("#jobId").val();
            var execType = $("#execType").val();
            window.location.href = "${contextPath}/record/done?queryTime=" + queryTime + "&success=" + success + "&agentId=" + agentId + "&jobId=" + jobId + "&execType=" + execType + "&pageSize=" + pageSize+"&csrf=${csrf}";
        }

        function showRedo(id,length,groupId,count){

            try {
                hideRedo();
                if (redoRecord && redoRecord.id==id){
                    redoRecord = null;
                    return;
                }
            }catch (e) {
            }

            var redoIcon = $("#redoIcon_"+id);
            var rowGroup = $("#row_"+ (groupId ? groupId : id));
            var row = rowGroup.attr("rowspan");

            redoRecord = {
                id:id,
                groupId:groupId,
                length: parseInt(length),
                count: parseInt(count),
            };

            redoIcon.removeClass("fa-chevron-down").addClass("fa-chevron-up");
            redoIcon.attr("redoOpen","on");
            rowGroup.attr("rowspan",parseInt(row) + parseInt(length));
            $(".redoIndex_"+id).show();

            var tbodyObj =  $(".tbody_"+(groupId ? groupId : id));
            if(tbodyObj.attr("index")=="0"){
                tbodyObj.css({"background-color":"rgba(0,0,0,0.35)"});
            }else {
                tbodyObj.css({"background-color":"rgba(225,225,225,0.15)"});
            }

            $(".redoGroup_"+id).show();

            if (count%2==0){
                $(".tbody_"+count).css("{background-color:rgba(0,0,0,0.35)}")
            }else {
                $(".tbody_"+count).css("{background-color:rgba(200,200,0,0.35)}")
            }

            if (groupId){
                $(".tr-flow_"+(parseInt(groupId)+parseInt(count))).addClass("tr-next");
            }
        }
        
        function hideRedo() {
            try {
                if( redoRecord ) {
                    var id = redoRecord.id;
                    var groupId = redoRecord.groupId;
                    var length = redoRecord.length;
                    var count = redoRecord.count;

                    var redoIcon = $("#redoIcon_"+id);
                    var rowGroup = $("#row_"+ (groupId ? groupId : id));
                    var row = rowGroup.attr("rowspan");

                    redoIcon.removeClass("fa-chevron-up").addClass("fa-chevron-down");
                    redoIcon.attr("redoOpen","off");
                    rowGroup.attr("rowspan",row - length);
                    if (rowGroup.attr("rowspan") == 1){
                        $(".tbody_"+(groupId ? groupId : id)).css({"background-color":"", "border-top":"none"});
                    }
                    if (groupId){
                        $(".tr-flow_"+(parseInt(groupId)+count)).removeClass("tr-next");
                    }
                    $(".redoIndex_"+id).hide();
                    $(".redoGroup_"+id).hide();
                }
            }catch(e) {
            }
        }

        function showFlow(id,length,groupId){
            var flowIcon = $("#flowIcon_"+id);
            var flowGroup = $(".flowGroup_"+id);
            var rowGroup = $("#row_"+ groupId);
            var row = rowGroup.attr("rowspan");

            if (flowIcon.attr("childOpen") == "off"){
                flowIcon.removeClass("fa-angle-double-down").addClass("fa-angle-double-up");
                flowIcon.attr("childOpen","on");
                rowGroup.attr("rowspan",parseInt(row) + parseInt(length));
                $(".flowIndex_"+id).show();
                var tbodyObj =  $(".tbody_"+groupId);
                tbodyObj.css({"background-color":"rgba(0,0,0,0.35)"});

                flowGroup.show();

            }else {
                flowIcon.removeClass("fa-angle-double-up").addClass("fa-angle-double-down");
                flowIcon.attr("childOpen","off");
                rowGroup.attr("rowspan",1);
                $(".flowIndex_"+id).hide();
                flowGroup.css("background-color","");
                flowGroup.removeClass("tr-next");
                flowGroup.hide();

                //收起所有子记录的重跑记录
                var groupIcon = $(".groupIcon_"+groupId);
                groupIcon.removeClass("fa-chevron-up").addClass("fa-chevron-down");
                groupIcon.attr("redoOpen","off");
                $(".groupIndex_"+groupId).hide();
                $(".tbody_"+groupId).css({"background-color":"","border-top":"none"});
                $(".groupRecord_"+groupId).hide();
            }
        }

    </script>
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
        <li><a href="#">opencron</a></li>
        <li><a href="#">调度记录</a></li>
        <li><a href="#">已完成</a></li>
    </ol>
    <h4 class="page-title"><i class="fa fa-check-circle" aria-hidden="true"></i>&nbsp;已完成</h4>
    <div class="block-area" id="defaultStyle">

        <div>
            <div style="float: left">
                <label>
                    每页 <select size="1" class="select-self" id="size" style="width: 50px;">
                    <option value="15">15</option>
                    <option value="30" ${pageBean.pageSize eq 30 ? 'selected' : ''}>30</option>
                    <option value="50" ${pageBean.pageSize eq 50 ? 'selected' : ''}>50</option>
                    <option value="100" ${pageBean.pageSize eq 100 ? 'selected' : ''}>100</option>
                </select> 条记录
                </label>
            </div>

            <div style="float: right;margin-bottom: 10px">
                <label for="agentId">执行器：</label>
                <select id="agentId" name="agentId" class="select-self" style="width: 110px;">
                    <option value="">全部</option>
                    <c:forEach var="d" items="${agents}">
                        <option value="${d.agentId}" ${d.agentId eq agentId ? 'selected' : ''}>${d.name}</option>
                    </c:forEach>
                </select>
                &nbsp;&nbsp;&nbsp;
                <label for="jobId">任务名称：</label>
                <select id="jobId" name="jobId" class="select-self" style="width: 110px;">
                    <option value="">全部</option>
                    <c:forEach var="t" items="${jobs}">
                        <option value="${t.jobId}" ${t.jobId eq jobId ? 'selected' : ''}>${t.jobName}&nbsp;</option>
                    </c:forEach>
                </select>
                &nbsp;&nbsp;&nbsp;
                <label for="success">执行状态：</label>
                <select id="success" name="success" class="select-self" style="width: 80px;">
                    <option value="">全部</option>
                    <option value="1" ${success eq 1 ? 'selected' : ''}>成功</option>
                    <option value="0" ${success eq 0 ? 'selected' : ''}>失败</option>
                    <option value="2" ${success eq 2 ? 'selected' : ''}>被杀</option>
                    <option value="3" ${success eq 3 ? 'selected' : ''}>超时</option>
                </select>
                &nbsp;&nbsp;&nbsp;
                <label for="execType">执行方式：</label>
                <select id="execType" name="execType" class="select-self" style="width: 80px;">
                    <option value="">全部</option>
                    <option value="0" ${execType eq 0 ? 'selected' : ''}>自动</option>
                    <option value="1" ${execType eq 1 ? 'selected' : ''}>手动</option>
                    <option value="2" ${execType eq 2 ? 'selected' : ''}>重跑</option>
                    <option value="3" ${execType eq 3 ? 'selected' : ''}>现场</option>
                </select>
                &nbsp;&nbsp;&nbsp;
                <label for="queryTime">开始时间：</label>
                <input type="text" id="queryTime" name="queryTime" value="${queryTime}" onfocus="WdatePicker({onpicked:function(){doUrl(); },dateFmt:'yyyy-MM-dd'})" class="Wdate select-self" style="width: 90px"/>
            </div>
        </div>

        <table class="table tile textured">
            <thead>
            <tr>
                <th>任务名称</th>
                <th>执行器</th>
                <th>执行命令</th>
                <th>开始时间</th>
                <th>运行时长</th>
                <th>运行状态</th>
                <th>执行方式</th>
                <th>任务类型</th>
                <th><center>操作</center></th>
            </tr>
            </thead>

            <%--父记录--%>
            <c:forEach var="r" items="${pageBean.result}" varStatus="index">
                <tbody class="tbody_${empty r.groupId ? r.recordId : r.groupId} tbody_${index.index}" style="border-top: none">

                    <tr class="tr-flow_${empty r.groupId ? "" : r.groupId}">
                        <c:if test="${r.jobType eq 0}">
                            <td id="row_${r.recordId}" rowspan="1">
                                <center>
                                    ${empty r.jobName ? 'batchJob' : r.jobName}
                                    <c:forEach var="c" items="${r.childRecord}" varStatus="index">
                                        <div style="display: none" class="redoIndex_${r.recordId}">
                                            <div class="div-circle"><span class="span-circle">${index.count}</span></div>${c.jobName}
                                        </div>
                                    </c:forEach>
                                </center>
                            </td>
                        </c:if>
                        <c:if test="${r.jobType eq 1}">
                            <td id="row_${r.groupId}" rowspan="1">
                                <center>
                                    ${r.jobName}
                                    <c:if test="${r.redoCount ne 0}">
                                        <c:forEach var="rc" items="${r.childRecord}" varStatus="index">
                                            <div class="redoIndex_${r.recordId} groupIndex_${r.groupId}" style="display: none">
                                                <div class="div-circle"><span class="span-circle">${index.count}</span></div>${rc.jobName}
                                            </div>
                                        </c:forEach>
                                    </c:if>
                                    <c:forEach var="t" items="${r.childJob}" varStatus="index">
                                        <div class="flowIndex_${r.recordId} " style="display: none">
                                            <div class="down"><i class="fa fa-arrow-down" style="font-size:14px" aria-hidden="true"></i></div>${t.jobName}
                                        </div>
                                        <c:if test="${t.redoCount ne 0}">
                                            <c:forEach var="tc" items="${t.childRecord}" varStatus="count">
                                                <div class="redoIndex_${t.recordId} groupIndex_${r.groupId}" style="display: none">
                                                    <div class="div-circle"><span class="span-circle">${count.count}</span></div>${tc.jobName}
                                                </div>
                                            </c:forEach>
                                        </c:if>
                                    </c:forEach>
                                </center>
                            </td>
                        </c:if>
                        <td>${r.agentName}</td>

                        <td title="${r.command}"><div class="opencron_command">${r.command}</div></td>
                        <td><fmt:formatDate value="${r.startTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                        <td>${cron:diffdate(r.startTime,r.endTime)}</td>
                        <td>
                            <c:if test="${r.success eq 1}">
                                <span class="label label-success">&nbsp;&nbsp;成&nbsp;功&nbsp;&nbsp;</span>
                            </c:if>
                            <c:if test="${r.success eq 0}">
                                <span class="label label-danger">&nbsp;&nbsp;失&nbsp;败&nbsp;&nbsp;</span>
                            </c:if>
                            <c:if test="${r.success eq 2}">
                                <span class="label label-warning">&nbsp;&nbsp;被&nbsp;杀&nbsp;&nbsp;</span>
                            </c:if>
                            <c:if test="${r.success eq 3}">
                                <span class="label label-warning">&nbsp;&nbsp;超&nbsp;时&nbsp;&nbsp;</span>
                            </c:if>
                        </td>
                        <td>
                            <c:if test="${r.execType eq 0}"><span class="label label-default">&nbsp;&nbsp;自&nbsp;动&nbsp;&nbsp;</span></c:if>
                            <c:if test="${r.execType eq 1}"><span class="label label-info">&nbsp;&nbsp;手&nbsp;动&nbsp;&nbsp;</span></c:if>
                            <c:if test="${r.execType eq 2}"><span class="label label-warning">&nbsp;&nbsp;重&nbsp;跑&nbsp;&nbsp;</span></c:if>
                            <c:if test="${r.execType eq 3}"><span class="label label-default" style="color: green;font-weight:bold">&nbsp;&nbsp;现&nbsp;场&nbsp;&nbsp;</span></c:if>
                        </td>
                        <td>
                            <c:if test="${r.jobType eq 1}">流程任务</c:if>
                            <c:if test="${r.jobType eq 0}">单一任务</c:if>
                        </td>
                        <td>
                            <center>
                                <div class="visible-md visible-lg hidden-sm hidden-xs action-buttons">
                                    <c:if test="${r.jobType eq 1 and r.childJob ne null}">
                                        <a href="#" title="流程任务" onclick="showFlow(${r.recordId},'${fn:length(r.childJob)}','${r.groupId}')">
                                            <i aria-hidden="true" class="fa fa-angle-double-down" style="font-size:15px;" childOpen="off" id="flowIcon_${r.recordId}"></i>
                                        </a>&nbsp;&nbsp;
                                    </c:if>
                                    <c:if test="${r.redoCount ne 0}">
                                        <a href="#" title="重跑记录" onclick="showRedo('${r.recordId}','${fn:length(r.childRecord)}',${empty r.groupId ? false : r.groupId},'1')">
                                            <i aria-hidden="true" class="fa fa-chevron-down groupIcon_${r.groupId}" redoOpen="off" id="redoIcon_${r.recordId}"></i>
                                        </a>&nbsp;&nbsp;
                                    </c:if>
                                    <a href="${contextPath}/record/detail?id=${r.recordId}&csrf=${csrf}" title="查看详情">
                                        <i class="glyphicon glyphicon-eye-open"></i>
                                    </a>&nbsp;&nbsp;
                                </div>
                            </center>
                        </td>
                    </tr>
                    <%--父记录重跑记录--%>
                    <c:if test="${r.redoCount ne 0}">
                        <c:forEach var="rc" items="${r.childRecord}" varStatus="index">
                            <tr class="redoGroup_${r.recordId} groupRecord_${r.groupId}" style="display: none;">
                                <td class="${index.count eq 1 ? (r.redoCount eq index.count ? "redo-first" : "redo-first-top") : (r.redoCount eq index.count ? "redo-first-bottom" : "")}" >${rc.agentName}</td>
                                <td title="${rc.command}"><div class="opencron_command">${rc.command}</div> </td>
                                <td><fmt:formatDate value="${rc.startTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                                <td>${cron:diffdate(rc.startTime,rc.endTime)}</td>
                                <td>
                                    <c:if test="${rc.success eq 1}">
                                        <span class="label label-success">&nbsp;&nbsp;成&nbsp;功&nbsp;&nbsp;</span>
                                    </c:if>
                                    <c:if test="${rc.success eq 0}">
                                        <span class="label label-danger">&nbsp;&nbsp;失&nbsp;败&nbsp;&nbsp;</span>
                                    </c:if>
                                    <c:if test="${rc.success eq 2}">
                                        <span class="label label-warning">&nbsp;&nbsp;被&nbsp;杀&nbsp;&nbsp;</span>
                                    </c:if>
                                    <c:if test="${rc.success eq 3}">
                                        <span class="label label-warning">&nbsp;&nbsp;超&nbsp;时&nbsp;&nbsp;</span>
                                    </c:if>
                                </td>
                                <td><span class="label label-warning">&nbsp;&nbsp;重&nbsp;跑&nbsp;&nbsp;</span></td>
                                <td>
                                    <c:if test="${rc.jobType eq 1}">流程任务</c:if>
                                    <c:if test="${rc.jobType eq 0}">单一任务</c:if>
                                </td>
                                <td class="${index.count eq 1 ? (r.redoCount eq index.count ? "redo-last" : "redo-last-top") : (r.redoCount eq index.count ? "redo-last-bottom" : "")}" >
                                    <center>
                                        <div class="visible-md visible-lg hidden-sm hidden-xs action-buttons">
                                                <a href="${contextPath}/record/detail?id=${rc.recordId}&csrf=${csrf}" title="查看详情">
                                                    <i class="glyphicon glyphicon-eye-open"></i>
                                                </a>&nbsp;&nbsp;
                                        </div>
                                    </center>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:if>
                    <%--流程子任务--%>
                    <c:if test="${r.jobType eq 1}">
                        <c:forEach var="t" items="${r.childJob}" varStatus="index">

                            <tr class="flowGroup_${r.recordId} tr-flow_${empty r.groupId ? "" : r.groupId+index.count}" style="display: none;">
                                <td>${t.agentName}</td>
                                <td title="${t.command}"><div class="opencron_command">${t.command}</div></td>
                                <td><fmt:formatDate value="${t.startTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                                <td>${cron:diffdate(t.startTime,t.endTime)}</td>
                                <td>
                                    <c:if test="${t.success eq 1}">
                                        <span class="label label-success">&nbsp;&nbsp;成&nbsp;功&nbsp;&nbsp;</span>
                                    </c:if>
                                    <c:if test="${t.success eq 0}">
                                        <span class="label label-danger">&nbsp;&nbsp;失&nbsp;败&nbsp;&nbsp;</span>
                                    </c:if>
                                    <c:if test="${t.success eq 2}">
                                        <span class="label label-warning">&nbsp;&nbsp;被&nbsp;杀&nbsp;&nbsp;</span>
                                    </c:if>
                                    <c:if test="${t.success eq 3}">
                                        <span class="label label-warning">&nbsp;&nbsp;超&nbsp;时&nbsp;&nbsp;</span>
                                    </c:if>
                                </td>
                                <td>
                                    <c:if test="${t.execType eq 0}"><span class="label label-default">&nbsp;&nbsp;自&nbsp;动&nbsp;&nbsp;</span></c:if>
                                    <c:if test="${t.execType eq 1}"><span class="label label-info">&nbsp;&nbsp;手&nbsp;动&nbsp;&nbsp;</span></c:if>
                                    <c:if test="${t.execType eq 2}"><span class="label label-warning">&nbsp;&nbsp;重&nbsp;跑&nbsp;&nbsp;</span></c:if>
                                </td>
                                <td>流程任务</td>
                                <td>
                                    <center>
                                        <div class="visible-md visible-lg hidden-sm hidden-xs action-buttons">
                                            <c:if test="${t.redoCount ne 0}">
                                                <a href="#" title="重跑记录" onclick="showRedo('${t.recordId}','${fn:length(t.childRecord)}','${t.groupId}',${index.count+1})">
                                                    <i aria-hidden="true" class="fa fa-chevron-down groupIcon_${r.groupId}" redoOpen="off" id="redoIcon_${t.recordId}"></i>
                                                </a>&nbsp;&nbsp;
                                            </c:if>
                                            <a href="${contextPath}/record/detail?id=${t.recordId}&csrf=${csrf}" title="查看详情">
                                                <i class="glyphicon glyphicon-eye-open"></i>
                                            </a>&nbsp;&nbsp;
                                        </div>
                                    </center>
                                </td>
                            </tr>
                            <%--流程子任务的重跑记录--%>
                            <c:if test="${t.redoCount ne 0}">
                                <c:forEach var="tc" items="${t.childRecord}" varStatus="index">
                                    <tr class="redoGroup_${t.recordId} groupRecord_${r.groupId}" style="display: none;">
                                        <td class="${index.count eq 1 ? (t.redoCount eq index.count ? "redo-first" : "redo-first-top") : (t.redoCount eq index.count ? "redo-first-bottom" : "")} ">${tc.agentName}</td>
                                        <td title="${tc.command}"><div class="opencron_command">${tc.command}</div></td>
                                        <td><fmt:formatDate value="${tc.startTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                                        <td>${cron:diffdate(tc.startTime,tc.endTime)}</td>
                                        <td>
                                            <c:if test="${tc.success eq 1}">
                                                <span class="label label-success">&nbsp;&nbsp;成&nbsp;功&nbsp;&nbsp;</span>
                                            </c:if>
                                            <c:if test="${tc.success eq 0}">
                                                <span class="label label-danger">&nbsp;&nbsp;失&nbsp;败&nbsp;&nbsp;</span>
                                            </c:if>
                                            <c:if test="${tc.success eq 2}">
                                                <span class="label label-warning">&nbsp;&nbsp;被&nbsp;杀&nbsp;&nbsp;</span>
                                            </c:if>
                                            <c:if test="${tc.success eq 3}">
                                                <span class="label label-warning">&nbsp;&nbsp;超&nbsp;时&nbsp;&nbsp;</span>
                                            </c:if>
                                        </td>
                                        <td><span class="label label-warning">&nbsp;&nbsp;重&nbsp;跑&nbsp;&nbsp;</span></td>
                                        <td>流程任务</td>
                                        <td class="${index.count eq 1 ? (t.redoCount eq index.count ? "redo-last" : "redo-last-top") : (t.redoCount eq index.count ? "redo-last-bottom" : "")}" >
                                            <center>
                                                <div class="visible-md visible-lg hidden-sm hidden-xs action-buttons">
                                                        <a href="${contextPath}/record/detail?id=${tc.recordId}&csrf=${csrf}" title="查看详情">
                                                            <i class="glyphicon glyphicon-eye-open"></i>
                                                        </a>&nbsp;&nbsp;
                                                </div>
                                            </center>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:if>
                        </c:forEach>
                    </c:if>
                </tbody>
            </c:forEach>
        </table>
        <cron:pager href="${contextPath}/record/done?queryTime=${queryTime}&success=${success}&agentId=${agentId}&jobId=${jobId}&execType=${execType}&csrf=${csrf}" id="${pageBean.pageNo}" size="${pageBean.pageSize}" total="${pageBean.totalCount}"/>
    </div>

</section>
<br/><br/>

<jsp:include page="/WEB-INF/common/footer.jsp"/>
