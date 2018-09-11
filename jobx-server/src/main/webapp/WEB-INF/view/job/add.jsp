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

    <script type="text/javascript">

        $(document).ready(function(){

            window.jobxValidata = new Validata('${contextPath}');

            var platform = $("#agentId").find("option:selected").attr("platform");
            if (platform!=1) {
                $("#execUserDiv").hide();
            }else {
                $("#execUserDiv").show();
            }

            $("#agentId").change(function () {
                var platform = $(this).find("option:selected").attr("platform");
                if (platform!=1) {
                    $("#execUserDiv").hide();
                    $("#execUser").val('');
                }else {
                    $("#execUserDiv").show();
                }
            });

        });

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
                        <span class="tips" id="expTip" tip="请采用quartz框架的时间格式表达式(双击控件输入)">请采用quartz框架的时间格式表达式(双击控件输入)</span>
                    </div>
                </div>

                <div class="form-group">
                    <label for="agentId" class="col-lab control-label wid150"><i class="glyphicon glyphicon-leaf"></i>&nbsp;&nbsp;执&nbsp;&nbsp;行&nbsp;&nbsp;器&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label>
                    <div class="col-md-10">
                        <c:if test="${empty agent}">
                            <select id="agentId" name="agentId"  class="select input-sm">
                                <c:forEach var="d" items="${agents}">
                                    <option platform="${d.platform}" value="${d.agentId}">${d.host}&nbsp;(${d.name})</option>
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
                        <select id="execUser" name="execUser" data-placeholder="执行该作业的用户身份" class="select input-sm">
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

    <jsp:include page="/WEB-INF/layouts/cron.jsp"/>

</section>

</body>

</html>
