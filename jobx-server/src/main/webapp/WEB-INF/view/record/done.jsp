<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="cron"  uri="http://www.jobx.org"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<!DOCTYPE html>
<html lang="en">
<head>
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

        .div-circle {
            width: 15px;
            height: 15px;
            background-color: #d9534f;
            -moz-border-radius: 25px !important;
            -webkit-border-radius: 25px !important;
            border-radius: 25px !important;
            position: relative;
            top: 21px;
        }

        .span-circle{
            height:15px;
            line-height:15px;
            display:block;
            color:white;
            text-align:center;
            font-size: 10px;
        }
    </style>

    <script type="text/javascript">

        $(document).ready(function(){
            $("#size").change(function(){doUrl();});
            $("#success").change(function(){doUrl();});
            $("#agentId").change(function(){doUrl();});
            $("#jobName").change(function(){doUrl();});
            $("#execType").change(function(){doUrl();});
        });

        function doUrl() {
            var pageSize = $("#size").val()||${pageBean.pageSize};
            var queryDate = $("#queryDate").val();
            var success = $("#success").val();
            var agentId = $("#agentId").val();
            var jobName = $("#jobName").val().trim();
            var execType = $("#execType").val();
            window.location.href = "${contextPath}/record/done.htm?queryDate=" + queryDate + "&success=" + success + "&agentId=" + agentId + "&jobName=" + jobName + "&execType=" + execType + "&pageSize=" + pageSize;
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
            $(".redoNum_"+id).show();

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
                    $(".redoNum_"+id).hide();
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
        <li><a href="#">已完成</a></li>
    </ol>
    <h4 class="page-title"><i class="fa fa-check-circle" aria-hidden="true"></i>&nbsp;已完成</h4>
    <div class="block-area" id="defaultStyle">

        <div>


            <div class="opt-bar" style="margin-bottom: 10px;margin-top: 0px;">
                <label for="agentId">执行器：</label>
                <select id="agentId" name="agentId" class="select-jobx w110">
                    <option value="">全部</option>
                    <c:forEach var="d" items="${agents}">
                        <option value="${d.agentId}" ${d.agentId eq record.agentId ? 'selected' : ''}>${d.name}</option>
                    </c:forEach>
                </select>
                &nbsp;&nbsp;&nbsp;
                <label for="jobName">任务名称：</label>
                <input id="jobName" name="jobName" type="text" value="${record.jobName}" class="w110" placeholder="根据名称搜索"/>
                &nbsp;&nbsp;&nbsp;
                <label for="success">执行状态：</label>
                <select id="success" name="success" class="select-jobx w80">
                    <option value="">全部</option>
                    <option value="1" ${record.success eq 1 ? 'selected' : ''}>成功</option>
                    <option value="0" ${record.success eq 0 ? 'selected' : ''}>失败</option>
                    <option value="2" ${record.success eq 2 ? 'selected' : ''}>被杀</option>
                    <option value="3" ${record.success eq 3 ? 'selected' : ''}>超时</option>
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
                <th>任务名称</th>
                <th>执行器</th>
                <th>执行命令</th>
                <th>开始时间</th>
                <th>运行时长</th>
                <th>运行状态</th>
                <th>执行方式</th>
                <th>任务类型</th>
                <th class="text-center">操作</th>
            </tr>
            </thead>

            <%--父记录--%>
            <c:forEach var="r" items="${pageBean.result}" varStatus="index">
                <tbody class="tbody_${empty r.groupId ? r.recordId : r.groupId} tbody_${index.index}" style="border-top: none">
                    <tr class="tr-flow_${empty r.groupId ? "" : r.groupId}">
                        <c:if test="${r.jobType eq 0}">
                            <td id="row_${r.recordId}" rowspan="1">
                                        <c:if test="${r.execType ne 4}"><a href="${contextPath}/job/detail/${r.jobId}.htm">${r.jobName}</a></c:if>
                                        <c:if test="${r.execType eq 4}"><span class="label label-primary">&nbsp;&nbsp;现&nbsp;场&nbsp;&nbsp;</span></c:if>
                                        <c:forEach var="c" items="${r.redoList}" varStatus="index">
                                        <div style="display: none" class="redoNum_${r.recordId}">
                                            <div class="div-circle"><span class="span-circle">${c.redoNum}</span></div>
                                            <c:if test="${c.execType ne 4}"><a href="${contextPath}/job/detail/${c.jobId}.htm">${c.jobName}</a></c:if>
                                            <c:if test="${c.execType eq 4}"><span class="label label-primary">&nbsp;&nbsp;现&nbsp;场&nbsp;&nbsp;</span></c:if>
                                        </div>
                                    </c:forEach>
                            </td>
                        </c:if>
                        <c:if test="${r.jobType eq 1}">
                            <td id="row_${r.groupId}" rowspan="1">
                                    <c:if test="${r.execType ne 4}"><a href="${contextPath}/job/detail/${r.jobId}.htm">${r.jobName}</a></c:if>
                                    <c:if test="${r.execType eq 4}"><span class="label label-primary">&nbsp;&nbsp;现&nbsp;场&nbsp;&nbsp;</span></c:if>
                                    <c:if test="${r.redoCount ne 0}">
                                    <c:forEach var="rc" items="${r.redoList}" varStatus="index">
                                        <div class="redoNum_${r.recordId} groupIndex_${r.groupId}" style="display: none">
                                            <span class="span-circle">${rc.redoNum}</span>
                                            <c:if test="${rc.execType ne 4}"><a href="${contextPath}/job/detail/${rc.jobId}.htm">${rc.jobName}</a></c:if>
                                            <c:if test="${rc.execType eq 4}"><span class="label label-primary">&nbsp;&nbsp;现&nbsp;场&nbsp;&nbsp;</span></c:if>
                                        </div>
                                    </c:forEach>
                                </c:if>
                            </td>
                        </c:if>
                        <td>${r.agentName}</td>
                        <td style="width: 25%" title="${cron:escapeHtml(r.command)}">
                            <div class="jobx_command">${cron:escapeHtml(r.command)}</div>
                        </td>
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
                                <span class="label label-primary">&nbsp;&nbsp;超&nbsp;时&nbsp;&nbsp;</span>
                            </c:if>
                            <c:if test="${r.success eq 4}">
                                <span class="label label-warning">&nbsp;&nbsp;失&nbsp;联&nbsp;&nbsp;</span>
                            </c:if>
                        </td>
                        <td>
                            <c:if test="${r.execType eq 0}"><span class="label label-default">&nbsp;&nbsp;自&nbsp;动&nbsp;&nbsp;</span></c:if>
                            <c:if test="${r.execType eq 1}"><span class="label label-default">&nbsp;&nbsp;手&nbsp;动&nbsp;&nbsp;</span></c:if>
                            <c:if test="${r.execType eq 2}"><span class="label label-default">&nbsp;&nbsp;接&nbsp;口&nbsp;&nbsp;</span></c:if>
                            <c:if test="${r.execType eq 3}"><span class="label label-default">&nbsp;&nbsp;重&nbsp;跑&nbsp;&nbsp;</span></c:if>
                            <c:if test="${r.execType eq 4}"><span class="label label-default">&nbsp;&nbsp;现&nbsp;场&nbsp;&nbsp;</span></c:if>
                        </td>
                        <td>
                            <c:if test="${r.jobType eq 1}">流程任务</c:if>
                            <c:if test="${r.jobType eq 0}">单一任务</c:if>
                        </td>
                        <td class="text-center">
                            <div class="visible-md visible-lg hidden-sm hidden-xs action-buttons">
                                <c:if test="${r.redoCount>0}">
                                    <a href="#" title="重跑记录" onclick="showRedo('${r.recordId}','${fn:length(r.redoList)}',${empty r.groupId ? false : r.groupId},'1')">
                                        <i aria-hidden="true" class="fa fa-chevron-down groupIcon_${r.groupId}" redoOpen="off" id="redoIcon_${r.recordId}"></i>
                                    </a>&nbsp;&nbsp;
                                </c:if>
                                <a href="${contextPath}/record/detail/${r.recordId}.htm" title="查看详情">
                                    <i class="glyphicon glyphicon-eye-open"></i>
                                </a>&nbsp;&nbsp;
                            </div>
                        </td>
                    </tr>
                    <%--父记录重跑记录--%>
                    <c:if test="${r.redoCount ne 0}">
                        <c:forEach var="rc" items="${r.redoList}" varStatus="index">
                            <tr class="redoGroup_${r.recordId} groupRecord_${r.groupId}" style="display: none;">
                                <td class="${index.count eq 1 ? (r.redoCount eq index.count ? "redo-first" : "redo-first-top") : (r.redoCount eq index.count ? "redo-first-bottom" : "")}" >${rc.agentName}</td>
                                <td style="width: 25%" title="${cron:escapeHtml(rc.command)}">
                                    <div class="jobx_command">${cron:escapeHtml(rc.command)}</div>
                                </td>
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
                                        <span class="label label-primary">&nbsp;&nbsp;超&nbsp;时&nbsp;&nbsp;</span>
                                    </c:if>
                                </td>
                                <td><span class="label label-warning">&nbsp;&nbsp;重&nbsp;跑&nbsp;&nbsp;</span></td>
                                <td>
                                    <c:if test="${rc.jobType eq 1}">流程任务</c:if>
                                    <c:if test="${rc.jobType eq 0}">单一任务</c:if>
                                </td>
                                <td class="text-center ${index.count eq 1 ? (r.redoCount eq index.count ? "redo-last" : "redo-last-top") : (r.redoCount eq index.count ? "redo-last-bottom" : "")}" >
                                    <div class="visible-md visible-lg hidden-sm hidden-xs action-buttons">
                                            <a href="${contextPath}/record/detail/${rc.recordId}.htm" title="查看详情">
                                                <i class="glyphicon glyphicon-eye-open"></i>
                                            </a>&nbsp;&nbsp;
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:if>
                </tbody>
            </c:forEach>
        </table>
        <cron:pager href="${contextPath}/record/done.htm?queryDate=${record.queryDate}&success=${record.success}&agentId=${record.agentId}&jobName=${record.jobName}&execType=${record.execType}" id="${pageBean.pageNo}" size="${pageBean.pageSize}" total="${pageBean.totalCount}"/>
    </div>

</section>

</body>

</html>
