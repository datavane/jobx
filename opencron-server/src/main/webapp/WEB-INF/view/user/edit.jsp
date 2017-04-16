<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="cron"  uri="http://www.opencron.org"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/common/resource.jsp"/>

    <script type="text/javascript">

        function save(){

            var userId = $("#userId").val();
            if (!userId){
                alert("页面错误，请刷新重试!");
                return false;
            }
            var userName = $("#userName").val();
            if (!userName){
                alert("页面错误，请刷新重试!");
                return false;
            }
            var realName = $("#realName").val();
            if (!realName){
                alert("请填写用户的真实姓名!");
                return false;
            }
            if ($("#contact").val()){
                if(!opencron.testMobile($("#contact").val())){
                    alert("请填写正确的手机号码!");
                    return false;
                }
            }
            if ($("#email").val()){
                if(!opencron.testEmail($("#email").val())){
                    alert("请填写正确的邮箱地址!");
                    return false;
                }
            }
            if ($("#qq").val()){
                if(!opencron.testQq($("#qq").val())){
                    alert("请填写正确的QQ号码!");
                    return false;
                }
            }
            $("#user").submit();
        }

        $(document).ready(function(){

            $("#checkAllInput").next().attr("id","checkAll");
            $(".each-box").next().addClass("each-btn");

            var array = "${u.agentIds}".split(",");
            for(var i in array) {
                $("#agent_"+array[i]).prop("checked",true)
                $("#agent_"+array[i]).parent().addClass("checked");
                $("#agent_"+array[i]).parent().attr("aria-checked",true);
            }

            $("#role").change(function () {
                if ($("#role").val() == 999){
                    $("#agentsDiv").hide();
                }else {
                    $("#agentsDiv").show();
                }
            });

            $("#checkAll").click(function () {

                if ($("input[type='checkbox'][name='agentIds']").is(':checked')){

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

<!-- Content -->
<section id="content" class="container">

    <!-- Messages Drawer -->
    <jsp:include page="/WEB-INF/common/message.jsp"/>

    <!-- Breadcrumb -->
    <ol class="breadcrumb hidden-xs">
        <li class="icon">&#61753;</li>
        当前位置：
        <li><a href="">opencron</a></li>
        <li><a href="">用户管理</a></li>
        <li><a href="">修改信息</a></li>
    </ol>
    <h4 class="page-title"><i aria-hidden="true" class="fa fa-edit"></i>&nbsp;修改信息</h4>

    <div style="float: right;margin-top: 5px">
        <a onclick="goback();" class="btn btn-sm m-t-10" style="margin-right: 16px;margin-bottom: -4px"><i class="fa fa-mail-reply" aria-hidden="true"></i>&nbsp;返回</a>
    </div>


    <div class="block-area" id="basic">
        <div class="tile p-15">
            <form class="form-horizontal" role="form"  id="user" action="${contextPath}/user/edit" method="post"><br>
                <input type="hidden" name="csrf" value="${csrf}">
                <input type="hidden" id="userId" name="userId" value="${u.userId}">
                <div class="form-group">
                    <label for="userName" class="col-lab control-label"><i class="glyphicon glyphicon-user"></i>&nbsp;&nbsp;用&nbsp;&nbsp;户&nbsp;&nbsp;名：</label>
                    <div class="col-md-10">
                        <input type="text" class="form-control input-sm" id="userName" name="userName" value="${u.userName}" readonly>
                        <span class="tips"><b>*&nbsp;</b>只读</span>
                    </div>
                </div><br>

                <c:if test="${permission eq true}">
                <div class="form-group">
                    <label for="role" class="col-lab control-label"><i class="glyphicon glyphicon-random"></i>&nbsp;&nbsp;用户角色：</label>
                    <div class="col-md-10">
                        <select id="role" name="roleId" class="form-control m-b-10 input-sm">
                            <c:forEach var="r" items="${role}">
                                <option value="${r.roleId}" ${r.roleId eq u.roleId ? 'selected' : ''}>${r.roleName}</option>
                            </c:forEach>
                        </select>
                        <span class="tips"><b>*&nbsp;</b>角色决定用户的操作权限</span>
                    </div>
                </div><br>

                <div class="form-group" id="agentsDiv" style="display: ${u.roleId eq 999 ? 'none' : 'block'}">
                    <label class="col-lab control-label"><i class="fa fa-desktop" aria-hidden="true"></i>&nbsp;执行器组：</label>
                    <div class="col-md-10">
                        <input type="checkbox" id="checkAllInput">全选<span class="tips">&nbsp;&nbsp;&nbsp;<b>*&nbsp;</b>此管理员可操作的执行器操组</span></br>
                        <div class="form-control m-b-10 input-sm" id="agent-content" style="height: 150px;overflow: hidden;">
                            <c:forEach var="w" items="${agents}" varStatus="index">
                                <input type="checkbox" name="agentIds" value="${w.agentId}" id="agent_${w.agentId}" class="each-box form-control input-sm">${w.name}&nbsp;&nbsp;${w.ip}<br>
                            </c:forEach>
                        </div>
                    </div>
                </div>
                </c:if>

                <div class="form-group">
                    <label for="realName" class="col-lab control-label"><i class="glyphicon glyphicon-tag"></i>&nbsp;&nbsp;真实姓名：</label>
                    <div class="col-md-10">
                        <input type="text" class="form-control input-sm" id="realName" name="realName" value="${u.realName}">
                        <span class="tips"><b>*&nbsp;</b>真实姓名必填</span>
                    </div>
                </div><br>


                <div class="form-group">
                    <label for="contact" class="col-lab control-label"><i class="glyphicon glyphicon-comment"></i>&nbsp;&nbsp;联系方式：</label>
                    <div class="col-md-10">
                        <input type="text" class="form-control input-sm" id="contact" name="contact" value="${u.contact}">
                        <span class="tips">选填</span>
                    </div>
                </div><br>

                <div class="form-group">
                    <label for="email" class="col-lab control-label"><i class="glyphicon glyphicon-envelope"></i>&nbsp;&nbsp;电子邮箱：</label>
                    <div class="col-md-10">
                        <input type="text" class="form-control input-sm" style="clear: right;" id="email" name="email" value="${u.email}"><span class="tips">选填</span>

                    </div>
                </div><br>

                <div class="form-group">
                    <label for="qq" class="col-lab control-label"><i class="glyphicon glyphicon-magnet"></i>&nbsp;&nbsp;QQ&nbsp;号&nbsp;码：</label>
                    <div class="col-md-10">
                        <input type="text" class="form-control input-sm" id="qq" name="qq" value="${u.qq}">
                        <span class="tips">选填</span>
                    </div>
                </div><br>

                <div class="form-group">
                    <div class="col-md-offset-1 col-md-10">
                        <button type="button"  onclick="save()" class="btn btn-sm m-t-10"><i class="icon">&#61717;</i>&nbsp;保存</button>&nbsp;&nbsp;
                        <button type="button" onclick="history.back()" class="btn btn-sm m-t-10"><i class="icon">&#61740;</i>&nbsp;取消</button>
                    </div>
                </div>
            </form>
        </div>
    </div>

</section>
<br/><br/>

<jsp:include page="/WEB-INF/common/footer.jsp"/>
