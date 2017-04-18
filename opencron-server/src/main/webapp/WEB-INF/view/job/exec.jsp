<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="cron"  uri="http://www.opencron.org"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/common/resource.jsp"/>

    <link href='${contextPath}/css/jquery.mCustomScrollbar.css?resId=${resourceId}' rel='stylesheet'>
    <script src="${contextPath}/js/jquery.mCustomScrollbar.min.js?resId=${resourceId}"></script>

    <script type="text/javascript">
        var flag = false;

        $(document).ready(function(){
            $("#checkAllInput").next().attr("id","checkAll");
            $(".each-box").next().addClass("each-btn");

            $("#reset").click(function () {
                $("#cmdArea").val("");
            });

            $("#execute").click(function () {

                var cmd = $("#cmdArea").val();
                if (!cmd){
                    alert("请填写命令！");
                    return false;
                }

                var ids = "";
                if ($("input[type='checkbox'][name='agent']").is(':checked')){

                    $('input:checkbox[name=agent]:checked').each(function(){
                        ids+=$(this).val()+";";
                    });
                    if ("" != ids){
                        ids = ids.substring(0,ids.length-1);
                        $.ajax({
                            headers:{"csrf":"${csrf}"},
                            type:"POST",
                            url:"${contextPath}/job/batchexec",
                            data:{
                                "command":$.base64.encode(cmd),
                                "agentIds":ids
                            }
                        });
                        alertMsg( "该作业已启动,正在执行中.");
                    }
                }else {
                    alert("请选择执行器！");
                }
            });

            $("#checkAll").click(function () {

                if ($("input[type='checkbox'][name='agent']").is(':checked')){

                    $("#checkAllInput").prop("checked",false);
                    $("#checkAll").parent().removeClass("checked");
                    $("#checkAll").parent().attr("aria-checked",false);

                    $(".each-box").prop("checked",false);
                    $(".each-box").parent().removeClass("checked");
                    $(".each-box").parent().attr("aria-checked",false);
                } else {

                    $("#checkAllInput").prop("checked",true);
                    $("#checkAll").parent().removeClass("checked").addClass("checked");
                    $("#checkAll").parent().attr("aria-checked",true);

                    $(".each-box").prop("checked",true);
                    $(".each-box").parent().removeClass("checked").addClass("checked");
                    $(".each-box").parent().attr("aria-checked",true);
                    flag = true;
                }

            });

            $(".each-btn").click(function () {
                if (flag){
                    $("#checkAllInput").prop("checked",false);
                    $("#checkAll").parent().removeClass("checked");
                    $("#checkAll").parent().attr("aria-checked",false);
                    flag = false;
                }
            });

            $(window).on("load",function(){
                $("#agent-content").mCustomScrollbar({
                    theme:"dark"
                });
            });
        });

    </script>
</head>

<jsp:include page="/WEB-INF/common/top.jsp"/>

<section id="content" class="container">

    <!-- Messages Drawer -->
    <jsp:include page="/WEB-INF/common/message.jsp"/>

    <!-- Breadcrumb -->
    <ol class="breadcrumb hidden-xs">
        <li class="icon">&#61753;</li>
        当前位置：
        <li><a href="">opencron</a></li>
        <li><a href="">作业管理</a></li>
        <li><a href="">现场执行</a></li>
    </ol>
    <h4 class="page-title"><i class="fa fa-play-circle" aria-hidden="true"></i>&nbsp;现场执行</h4>
    <!-- Deafult Table -->
    <div class="block-area" id="defaultStyle">
        <div>
            <textarea class="form-control m-b-10" id="cmdArea" style="resize:vertical;min-height: 250px;"></textarea>

        </div>

        <div style="float: right">
            <button class="btn btn-sm btn-alt m-r-5" id="reset">&nbsp;重&nbsp;置&nbsp;</button>
            <button class="btn btn-sm btn-alt m-r-5" id="execute">&nbsp;执&nbsp;行&nbsp;</button>
        </div>

        <h3 class="block-title">选择执行器</h3>

        <table class="table table-bordered tile" style="font-size: 12px;margin-bottom: 0;">
            <thead>
            <tr>
                <th width="10%"><input type="checkbox" id="checkAllInput">全选</th>
                <th width="25%">执行器</th>
                <th width="25%">机器IP</th>
                <th width="20%">端口号</th>
                <th width="20%">连接状态</th>
            </tr>
            </thead>
        </table>
        <div id="agent-content" style="height: 600px;overflow: hidden;">
            <table class="table table-bordered tile textured" style="font-size: 12px;margin-bottom: 0;">
                <tbody>
                <c:forEach var="w" items="${agents}" varStatus="index">
                    <c:if test="${w.status eq true}">
                        <tr>
                            <td width="10%">
                                <input type="checkbox" name="agent" value="${w.agentId}" class="each-box">
                            </td>
                            <td width="25%" id="name_${w.agentId}">${w.name}</td>
                            <td width="25%">${w.ip}</td>
                            <td width="20%" id="port_${w.agentId}">${w.port}</td>
                            <td width="20%" id="agent_${w.agentId}">
                                <c:if test="${w.status eq false}">
                                    <span class="label label-danger">&nbsp;&nbsp;失&nbsp;败&nbsp;&nbsp;</span>
                                </c:if>
                                <c:if test="${w.status eq true}">
                                    <span class="label label-success">&nbsp;&nbsp;成&nbsp;功&nbsp;&nbsp;</span>
                                </c:if>
                            </td>
                        </tr>
                    </c:if>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</section>
<br/><br/>

<jsp:include page="/WEB-INF/common/footer.jsp"/>


