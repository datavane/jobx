<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="cron" uri="http://www.jobx.org" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="en">
<head>

    <script type="text/javascript" src="${contextPath}/static/js/cron.js?resId=${resourceId}"></script> <!-- jQuery Library -->

    <style type="text/css">
        .block-title {
            margin-bottom: 0px;
            margin-right:-3px;
            background-color: rgba(0,0,0,0.5);
            cursor: pointer;
        }
        .title-active {
            background-color: rgba(0,0,0,0.7);
        }

        .flowJobUl li {
            background-color: rgba(0, 0, 0, 0.3);
            border-radius: 15px;
            height: 33px;
            list-style: outside none none;
            margin-top: -24px;
            margin-bottom: 29px;
            margin-left: 136px;
            padding: 6px 16px;
            width: 100%;
        }

        .circle {
            border-radius: 50%;
            background-color:rgba(0,255,200,0.6);
            width: 20px;
            height: 20px;
            margin: 0 auto;
            line-height: 20px;
            color:red;
            float:left
        }

        .jobnum {
            color: black;
            font-weight: bold;
            margin-left: -14px;
            margin-right: 10px;
            font-size: 14px;
        }

        .message-search{
            height: 26px;
            margin-left: 10px;
            margin-right: 10px;
        }

        .depen-input{
            resize:vertical;
            border-radius: 5px;
            line-height:1.5;
            margin-left: -5px;
        }

        .graph {
            margin-top: -205px;
            float: right;
            height: 200px;
        }

        .node rect,
        .node circle,
        .node ellipse,
        .node polygon {
            stroke: rgba(0,255,200,0.6);
            fill:rgba(235,235,255,.4);
            stroke-width: 1px;
            font-size: 14px;
        }

        .edgePath path {
            stroke: rgba(0,0,0,.7);
            fill: rgba(0,0,0,.7);
            stroke-width: 1px;
        }

        .modal-search {
            background-color: rgba(222, 222, 244, 0.40);
        }

        #existJobModal table tr {
            cursor: pointer;
        }

        .select-tr {
            background-color:rgba(255,255,255,0.1);
        }

        .search{
            border: 0;
            font-size: 16px;
            background-color: transparent;
            background-image: url(/static/img/search.png);
            background-repeat: no-repeat;
            padding-left: 34px;
        }

        .message-search{
            margin-right:0px;
        }

        .tab-content{
            margin-bottom: -5px;
        }
    </style>

    <script type="text/javascript" src="${contextPath}/static/js/job.validata.js"></script>
    <script type="text/javascript" src="${contextPath}/static/js/dagre-d3/d3.v4.min.js" charset="utf-8"></script>
    <script type="text/javascript" src="${contextPath}/static/js/dagre-d3/dagre-d3.min.js"></script>

    <script type="text/javascript">

        $(document).ready(function(){

            window.jobxValidata = new Validata('${contextPath}');

            var platform = $("#agentId").find("option:selected").attr("platform");
            if (platform!=1) {
                $("#execUserDiv").hide();
            }else {
                $("#execUserDiv").show();
            }

            $(".depen-input").change(function () {
                graph();
            });

            $("#agentId").change(function () {
                var platform = $(this).find("option:selected").attr("platform");
                if (platform!=1) {
                    $("#execUserDiv").hide();
                }else {
                    $("#execUserDiv").show();
                }
            });

            $("#sagentId").change(function () {
                changeUrl();
            });

            $("#searchJobName").blur(function () {
                changeUrl();
            });

            $(".jobType").find("a").click(function () {
                $("#jobType").val($(this).attr("type"));
                console.log($("#jobType").val());
            });

        });

        function graph() {

            var depVal = $(".depen-input").val().replace(/[\r\n]/g, "").replace(/\s+/g, "");

            if( depVal.length == 0 ) return;

            depVal = $(".depen-input").val();

            if (depVal.endsWith(">")) {
                alert("表达式错误,必须以任务标记结尾");
                return;
            }

            //语法解析....
            var lineChar = "\n";
            var linkChar = ">";
            var splitChar = ",";

            var lines = depVal.split(lineChar);
            var depObj = {};
            for(var i=0;i<lines.length;i++) {
                var jobs = lines[i].split(linkChar);
                for (var j = 0;j<jobs.length-1;j++) {
                    var snArray = jobs[j].split(splitChar);
                    var to = jobs[j+1].split(splitChar);
                    for (var n = 0;n<snArray.length;n++) {
                        var from = depObj[snArray[n]];
                        if (from == null) {
                            from = to;
                        } else {
                            from = from.concat(to);
                        }
                        depObj[snArray[n]] = from;
                    }
                }
            }

            var g = new dagreD3.graphlib.Graph().setGraph({});

            var states = [];

            for(var k in depObj) {
                var kName = getName(k);
                if (kName == null) {
                    alert("找不到标记为\""+k+"\"的作业!");
                    return;
                }
                states.push(kName);
                var arr = depObj[k];
                for (var i=0;i<arr.length;i++) {
                    var v = arr[i];
                    var vName = getName(v);
                    if (vName == null) {
                        alert("找不到标记为\""+v+"\"的作业!");
                        return;
                    }
                    states.push(vName);
                    g.setEdge(kName,vName, { label: ""});
                    g.setNode(kName,{ shape: "rect","labelStyle":"font: 600 14px 'Helvetica Neue', Helvetica;"});
                    g.setNode(vName,{ shape: "rect","labelStyle":"font: 600 14px 'Helvetica Neue', Helvetica;"});
                }
            }

            g.nodes().forEach(function(v) {
                var node = g.node(v);
                node.rx = node.ry = 10;
            });

            var svg = d3.select("svg"),
                inner = svg.select("g");

            var zoom = d3.zoom().on("zoom", function() {
                inner.attr("transform", d3.event.transform);
            });
            svg.call(zoom);

            // Create the renderer
            var render = new dagreD3.render();

            // Run the renderer. This is what draws the final graph.
            render(inner, g);

            // Center the graph
            var initialScale = 0.90;
            svg.call(zoom.transform, d3.zoomIdentity.translate(($(".graph").width() * initialScale - g.graph().width * initialScale) / 2,20).scale(initialScale));

            svg.attr('height', g.graph().height * initialScale + 50);

        }

        function getName(char) {
            var jobs = $(".jobnum");
            for (var i=0;i<jobs.length;i++) {
                var node = $(jobs[i]);
                var num = node.text();
                var name = node.attr("name");
                if (char.toUpperCase() == num.toUpperCase()) {
                    return name;
                }
            }
            return null;
        }

        function changeUrl() {
            var agentId = $("#sagentId").val();
            var searchJobName = $("#searchJobName").val();
            ajax({
                url:"${contextPath}/job/search.do",
                type: "post",
                data: {
                    "agentId":agentId,
                    "jobName":searchJobName
                }
            },function (pageBean) {
                var data = pageBean.result;
                if (data.length>0) {
                    var html = "";
                    for (var i=0;i<data.length;i++) {
                        var job = data[i];
                        var template = $("#jobTemplate").html();

                        var jobName = job.jobName;
                        if (jobName.length>20) {
                            jobName = jobName.substr(0,15) + "...";
                        }

                        var command = job.command;
                        if (command.length>30) {
                            command = command.substr(0,30) + "...";
                        }

                        html += template
                            .replace(/#title_jobName#/g,job.jobName)
                            .replace(/#jobName#/g,jobName)
                            .replace(/#cronExp#/g,job.cronExp)
                            .replace(/#command#/g,command)
                            .replace(/#title_command#/g,job.command);

                    }
                    $("#jobBody").html(html);
                    $("#existJobModal").find("tr").click(function () {
                        $("#existJobModal").find("tr").removeClass("select-tr")
                        $(this).addClass("select-tr");
                    });
                }else {
                    $("#jobBody").html("");
                }

            });
        }
    </script>

    <script type="text/html" id="jobTemplate">
        <tr>
            <td title="#title_jobName#">#jobName#</td>
            <td >#cronExp#</td>
            <td title="#title_command#">#command#</td>
        </tr>
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
        <li><a href="">添加作业</a></li>
        <li><a href="">简单作业</a></li>
    </ol>
    <h4 class="page-title"><i class="fa fa-plus" aria-hidden="true"></i>&nbsp;添加作业</h4>

    <div style="float: right;margin-top: 5px">
        <a onclick="goback();" class="btn btn-sm m-t-10" style="margin-right: 16px;margin-bottom: -4px"><i
                class="fa fa-mail-reply" aria-hidden="true"></i>&nbsp;返回</a>
    </div>

    <div class="block-area" id="basic">

        <div class="tile p-15 textured">
            <form class="form-horizontal" role="form" id="jobform" action="${contextPath}/job/save.do" method="post"></br>
                <input type="hidden" name="command" id="command">
                <input type="hidden" name="jobType" id="jobType" value="0">

                <div class="form-group">
                    <label for="jobName" class="col-lab control-label wid150"><i class="glyphicon glyphicon-tasks"></i>&nbsp;&nbsp;作业名称&nbsp;&nbsp;<b>*&nbsp;</b></label>
                    <div class="col-md-10">
                        <input type="text" class="form-control input-sm" id="jobName" name="jobName">
                        <span class="tips" tip="必填项,该作业的名称">必填项,该作业的名称</span>
                    </div>
                </div>

                <div class="form-group cronExpDiv">
                    <label for="cronExp" class="col-lab control-label wid150"><i class="glyphicon glyphicon-filter"></i>&nbsp;&nbsp;时间规则&nbsp;&nbsp;<b>*&nbsp;</b></label>
                    <div class="col-md-10">
                        <input type="text" class="form-control input-sm" id="cronExp" name="cronExp">
                        <span class="tips" id="expTip" tip="请采用quartz框架的时间格式表达式">请采用quartz框架的时间格式表达式</span>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-lab control-label wid150"><i class="glyphicon glyphicon-transfer"></i>&nbsp;&nbsp;&nbsp;作业类型&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label>
                    <div class="col-md-10">
                        <ul class="nav nav-tabs wid150 jobType">
                            <li class="active">
                                <a href="#simple" type="0" data-toggle="tab">简单作业</a>
                            </li>
                            <li>
                                <a href="#flow" type="1" data-toggle="tab">工作流</a>
                            </li>
                        </ul>
                    </div>
                </div>

                <div class="form-group tab-content">

                    <div class="tab-pane fade in active" id="simple">
                        <div class="form-group">
                            <label for="agentId" class="col-lab control-label wid150"><i class="glyphicon glyphicon-leaf"></i>&nbsp;&nbsp;执&nbsp;&nbsp;行&nbsp;&nbsp;器&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label>
                            <div class="col-md-10">
                                <c:if test="${empty agent}">
                                    <select id="agentId" class="select input-sm" name="agentId"  multiple data-selected-text-format="count>2">
                                        <c:forEach var="d" items="${agents}">
                                            <option platform=${d.platform} value="${d.agentId}">${d.host}&nbsp;(${d.name})</option>
                                        </c:forEach>
                                    </select>
                                </c:if>
                                <c:if test="${!empty agent}">
                                    <input type="hidden" id="agentId" name="agentId" value="${agent.agentId}">
                                    <input type="text" class="form-control input-sm" value="${agent.name}&nbsp;&nbsp;&nbsp;${agent.host}" readonly>
                                    <label color="red">&nbsp;*只读</label>
                                </c:if>
                                <div class="tips"><b>*&nbsp;</b>要执行作业的目标机器</div></br>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="cmd" class="col-lab control-label wid150"><i class="glyphicon glyphicon-th-large"></i>&nbsp;&nbsp;执行命令&nbsp;&nbsp;<b>*&nbsp;</b></label>
                            <div class="col-md-10">
                                <textarea class="form-control input-sm" id="cmd" style="height:200px;resize:vertical"></textarea>
                                <span class="tips" tip="请采用unix/linux的shell支持的命令">请采用unix/linux的shell支持的命令</span>
                            </div>
                        </div>

                        <div class="form-group" id="execUserDiv">
                            <label for="execUser"  class="col-lab control-label wid150"><i class="fa fa-user" aria-hidden="true"></i>&nbsp;&nbsp;执行身份&nbsp;&nbsp;<b>*&nbsp;</b></label>
                            <div class="col-md-10">
                                <select id="execUser" name="execUser" data-placeholder="执行该作业的用户身份" class="tag-select-limited agentId select input-sm" multiple>
                                    <c:forEach items="${execUser}" var="item">
                                        <option value="${item}">${item}</option>
                                    </c:forEach>
                                </select>
                                <div class="tips"><b>*&nbsp;</b>执行该作业的用户身份</div></br>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="successExit" class="col-lab control-label wid150"><i class="glyphicons glyphicons-tags"></i>&nbsp;&nbsp;成功标识&nbsp;&nbsp;<b>*&nbsp;</b></label>
                            <div class="col-md-10">
                                <input type="text" class="form-control input-sm" id="successExit" name="successExit" value="0">
                                <span class="tips" tip="自定义作业执行成功的返回标识(默认执行成功是0)">自定义作业执行成功的返回标识(默认执行成功是0)</span>
                            </div>
                        </div>
                    </div>

                    <div class="tab-pane fade in" id="flow">
                        <div class="form-group">
                            <span class="flowJob">
                                <label class="col-lab control-label wid150" style="margin-top: -2px;"><i class="glyphicon glyphicon-sort"></i>&nbsp;&nbsp;作业依赖&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label>
                                <div class="col-md-10">
                                    <div class="btn-group">
                                        <button class="btn btn-sm dropdown-toggle" type="button" data-toggle="dropdown">
                                           &nbsp;添加作业依赖&nbsp;<span class="caret"></span>
                                        </button>
                                        <ul class="dropdown-menu" role="menu">
                                            <li><a data-toggle="modal" href="#jobModal" onclick="jobxValidata.flowJob.add()">新增作业</a></li>
                                            <li><a data-toggle="modal" href="#existJobModal">选择已有作业</a></li>
                                            <li><a data-toggle="modal" href="#existFlowModal">选择已有工作流</a></li>
                                        </ul>
                                    </div>
                                </div>
                                <div class="col-md-10" style="top:33px;">
                                    <ul id="flowJobDiv" class="flowJobUl col-md-4"></ul>
                                </div>
                            </span>
                        </div>

                        <div class="form-group">
                            <label for="deps" class="col-lab control-label wid150"><i class="glyphicon glyphicon-th-large"></i>&nbsp;&nbsp;编排依赖&nbsp;&nbsp;<b>*&nbsp;</b></label>
                            <div class="col-md-10">
                                <div class="col-md-4">
                                    <textarea name="deps" id="deps" class="form-control input-sm depen-input" style="height: 200px;width: 100%" placeholder="例如:A>B>C"></textarea>
                                </div>
                            </div>
                        </div>

                        <div class="form-group">
                            <span class="col-md-10" style="margin-left: -50px;">
                                <svg class="col-md-5 graph"><g/></svg>
                            </span>
                        </div>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-lab control-label wid150"><i class="glyphicon  glyphicon glyphicon-forward"></i>&nbsp;&nbsp;失败重跑&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label>
                    <div class="col-md-10">
                        <label for="redo01" class="radio-label"><input type="radio" name="redo" value="1" id="redo01">是&nbsp;&nbsp;&nbsp;</label>
                        <label for="redo00" class="radio-label"><input type="radio" name="redo" value="0" id="redo00" checked>否</label>&nbsp;&nbsp;&nbsp;
                        <br><span class="tips">执行失败时是否自动重新执行</span>
                    </div>
                </div>

                <div class="form-group countDiv">
                    <label for="runCount" class="col-lab control-label wid150"><i class="glyphicon glyphicon-repeat"></i>&nbsp;&nbsp;重跑次数&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label>
                    <div class="col-md-10">
                        <input type="text" class="form-control input-sm" id="runCount" name="runCount">
                        <span class="tips" tip="执行失败时自动重新执行的截止次数">执行失败时自动重新执行的截止次数</span>
                    </div>
                </div>

                <div class="form-group">
                    <label for="timeout" class="col-lab control-label wid150"><i class="glyphicon glyphicon-ban-circle"></i>&nbsp;&nbsp;超时时间&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label>
                    <div class="col-md-10">
                        <input type="text" class="form-control input-sm" id="timeout" name="timeout" value="0">
                        <span class="tips" tip="执行作业允许的最大时间,超过则为超时(0:忽略超时时间,分钟为单位)">执行作业允许的最大时间,超过则为超时(0:忽略超时时间,分钟为单位)</span>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-lab control-label wid150"><i class="glyphicon glyphicon-warning-sign"></i>&nbsp;&nbsp;失败报警&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label>
                    <div class="col-md-10">
                        <label for="warning1" class="radio-label"><input type="radio" name="warning" value="1" id="warning1" checked>是&nbsp;&nbsp;&nbsp;</label>
                        <label for="warning0" class="radio-label"><input type="radio" name="warning" value="0" id="warning0">否</label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        </br><span class="tips" tip="任务执行失败时是否发信息报警">任务执行失败时是否发信息报警</span>
                    </div>
                </div>

                <div class="form-group contact">
                    <label for="mobile" class="col-lab control-label wid150"><i class="glyphicon glyphicon-comment"></i>&nbsp;&nbsp;报警手机&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label>
                    <div class="col-md-10">
                        <input type="text" class="form-control input-sm" id="mobile" name="mobile">
                        <span class="tips" tip="任务执行失败时将发送短信给此手机,多个请以逗号(英文)隔开">任务执行失败时将发送短信给此手机,多个请以逗号(英文)隔开</span>
                    </div>
                </div>

                <div class="form-group contact">
                    <label for="email" class="col-lab control-label wid150"><i class="glyphicon glyphicon-envelope"></i>&nbsp;&nbsp;报警邮箱&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label>
                    <div class="col-md-10">
                        <input type="text" class="form-control input-sm" id="email" name="email">
                        <span class="tips" tip="任务执行失败时将发送报告给此邮箱,多个请以逗号(英文)隔开">任务执行失败时将发送报告给此邮箱,多个请以逗号(英文)隔开</span>
                    </div>
                </div>
                <br>

                <div class="form-group">
                    <label for="comment" class="col-lab control-label wid150"><i class="glyphicon glyphicon-magnet"></i>&nbsp;&nbsp;描&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;述&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label>
                    <div class="col-md-10">
                        <textarea class="form-control input-sm" id="comment" name="comment" style="height: 100px;"></textarea>
                    </div>
                </div>

                <div class="form-group">
                    <div class="col-md-offset-1 col-md-10">
                        <button type="button" id="save-btn" class="btn btn-sm m-t-10"><i class="icon">&#61717;</i>&nbsp;保存</button>&nbsp;&nbsp;
                        <button type="button" onclick="history.back()" class="btn btn-sm m-t-10"><i class="icon">&#61740;</i>&nbsp;取消</button>
                    </div>
                </div>
            </form>
        </div>
    </div>


    <%--添加流程作业弹窗--%>
    <div class="modal fade" id="jobModal" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button class="close btn-float" data-dismiss="modal" aria-hidden="true"><i class="md md-close"></i>
                    </button>
                    <h4 id="subTitle" action="add" tid="">添加作业依赖</h4>
                </div>
                <div class="modal-body">
                    <form class="form-horizontal" role="form" id="subForm"><br>

                        <input type="hidden" id="itemRedo" value="1"/>
                        <div class="form-group">
                            <label for="agentId1" class="col-lab control-label wid100" title="要执行此作业的机器名称和IP地址">执&nbsp;&nbsp;行&nbsp;&nbsp;器&nbsp;&nbsp;&nbsp;</label>
                            <div class="col-md-9">
                                <select id="agentId1" name="agentId1" class="form-control m-b-10 ">
                                    <c:forEach var="d" items="${agents}">
                                        <option value="${d.agentId}">${d.host}&nbsp;(${d.name})</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="jobName1" class="col-lab control-label wid100" title="作业名称必填">作业名称&nbsp;<b>*</b></label>
                            <div class="col-md-9">
                                <input type="text" class="form-control " id="jobName1">
                                <span class="tips" tip="必填项,该作业的名称">必填项,该作业的名称</span>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="cmd1" class="col-lab control-label wid100" title="请采用unix/linux的shell支持的命令">执行命令&nbsp;<b>*</b></label>
                            <div class="col-md-9">
                                <textarea class="form-control" id="cmd1" name="cmd1" style="height:100px;resize:vertical"></textarea>
                                <span class="tips" tip="请采用unix/linux的shell支持的命令">请采用unix/linux的shell支持的命令</span>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="successExit1" class="col-lab control-label wid100">成功标识&nbsp;<b>*</b></label>
                            <div class="col-md-9">
                                <input type="text" class="form-control" id="successExit1" name="successExit1" value="0">
                                <span class="tips" tip="自定义作业执行成功的返回标识(默认执行成功是0)">自定义作业执行成功的返回标识(默认执行成功是0)</span>
                            </div>
                        </div>
                        <br>

                        <div class="form-group">
                            <label class="col-lab control-label wid100" title="执行失败时是否自动重新执行">失败重跑&nbsp;&nbsp;&nbsp;</label>&nbsp;&nbsp;
                            <label for="redo1" class="radio-label"><input type="radio" name="itemRedo" id="redo1" checked> 是&nbsp;&nbsp;&nbsp;</label>
                            <label for="redo0" class="radio-label"><input type="radio" name="itemRedo" id="redo0">否</label><br>
                        </div>
                        <br>
                        <div class="form-group countDiv1">
                            <label for="runCount1" class="col-lab control-label wid100" title="执行失败时自动重新执行的截止次数">重跑次数&nbsp;&nbsp;&nbsp;</label>&nbsp;&nbsp;
                            <div class="col-md-9">
                                <input type="text" class="form-control " id="runCount1"/>
                                <span class="tips" tip="执行失败时自动重新执行的截止次数">执行失败时自动重新执行的截止次数</span>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="timeout1" class="col-lab control-label wid100">超时时间&nbsp;<b>*</b></label>
                            <div class="col-md-9">
                                <input type="text" class="form-control" id="timeout1" value="0">
                                <span class="tips" tip="执行作业允许的最大时间,超过则为超时(0:忽略超时时间,分钟为单位)">执行作业允许的最大时间,超过则为超时(0:忽略超时时间,分钟为单位)</span>
                            </div>
                        </div>
                        <br>

                        <div class="form-group">
                            <label for="comment1" class="col-lab control-label wid100" title="此作业内容的描述">描&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;述&nbsp;&nbsp;&nbsp;</label>&nbsp;&nbsp;
                            <div class="col-md-9">
                                <input type="text" class="form-control " id="comment1"/>&nbsp;
                            </div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <center>
                        <button type="button" class="btn btn-sm" id="flowJob-btn">保存</button>&nbsp;&nbsp;
                        <button type="button" class="btn btn-sm" data-dismiss="modal">关闭</button>
                    </center>
                </div>
            </div>
        </div>
    </div>

    <%--选择已有作业弹窗--%>
    <div class="modal fade" id="existJobModal" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog modal-search">
            <div class="modal-content">
                <div class="modal-header">
                    <button class="close btn-float" data-dismiss="modal" aria-hidden="true"><i class="md md-close"></i></button>
                    <h4>选择已有作业</h4>
                </div>
                <div class="modal-body" style="height: 300px;">
                    <div>
                        <div style="float:left;margin-top: -5px;margin-bottom: 5px;">
                            <label for="sagentId">执行器：</label>
                            <select id="sagentId" name="sagentId" class="select-jobx" style="width: 110px;">
                                <option value="">全部</option>
                                <c:forEach var="d" items="${agents}">
                                    <option value="${d.agentId}" ${d.agentId eq agentId ? 'selected' : ''}>${d.name}</option>
                                </c:forEach>
                            </select>
                            <input class="pull-right message-search" placeholder="根据作业名称搜索...." type="text" id="searchJobName">
                        </div>
                    </div>
                    <table class="table table-condensed table-hover" style="font-size: 13px;margin-top: 5px;">
                        <thead>
                        <tr>
                            <th>名称</th>
                            <th>表达式</th>
                            <th>执行命令</th>
                        </tr>
                        </thead>
                        <tbody id="jobBody"></tbody>
                    </table>
                </div>

            </div>
        </div>
    </div>

    <%--选择已有作业弹窗--%>
    <div class="modal fade bs-example-modal-lg" id="existFlowModal" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <button class="close btn-float" data-dismiss="modal" aria-hidden="true"><i class="md md-close"></i>
                    </button>
                    <h4>选择已有工作流</h4>
                </div>
                <div class="modal-body">


                </div>
            </div>
        </div>
    </div>
    
    <jsp:include page="/WEB-INF/layouts/cron.jsp"/>

</section>

<script type="text/javascript">
    $(document).ready(function(){
        /* Tag Select */
        (function(){
            /* Limited */
            $(".tag-select-limited").chosen({
                max_selected_options: 1
            });
            /* Overflow */
            $('.overflow').niceScroll();
        })();
    });
</script>

</body>

</html>
