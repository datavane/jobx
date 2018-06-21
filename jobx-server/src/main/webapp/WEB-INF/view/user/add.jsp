<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="cron"  uri="http://www.jobx.org"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <link href='${contextPath}/static/css/jquery.mCustomScrollbar.css?resId=${resourceId}' rel='stylesheet'>
    <script src="${contextPath}/static/js/jquery.mCustomScrollbar.min.js?resId=${resourceId}"></script>

    <script type="text/javascript">

        function save(){
            var name = $("#name").val();
            if (!name){
                alert("请填写用户名!");
                return false;
            }
            var password = $("#password").val();
            if (!password){
                alert("请填密码!");
                return false;
            }
            if (password.length < 6 || password.length > 15){
                alert("密码长度请在6-15位之间!");
                return false;
            }
            var pwd = $("#pwd").val();
            if (!pwd){
                alert("请填写确认密码!");
                return false;
            }
            if (pwd.length < 6 || pwd.length > 15){
                alert("密码长度请在6-15位之间!");
                return false;
            }
            if (password != pwd){
                alert("两密码不一致!");
                return false;
            }
            var realName = $("#realName").val();
            if (!realName){
                alert("请填写用户的真实姓名!");
                return false;
            }
            if ($("#contact").val()){
                if(!jobx.testMobile($("#contact").val())){
                    alert("请填写正确的手机号码!");
                    return false;
                }
            }
            if ($("#email").val()){
                if(!jobx.testEmail($("#email").val())){
                    alert("请填写正确的邮箱地址!");
                    return false;
                }
            }
            if ($("#qq").val()){
                if(!jobx.testQq($("#qq").val())){
                    alert("请填写正确的QQ号码!");
                    return false;
                }
            }
            $("#password").val(calcMD5(password));

            ajax({
                type: "post",
                url:"${contextPath}/user/checkname.do",
                data:{
                    "name":name
                }
            },function (data) {
                if (data.status){
                    $("#user").submit();
                    return false;
                }else {
                    alert("用户已存在!");
                    return false;
                }
            });
        }

        $(document).ready(function(){

            $("#name").blur(function(){
                if(!$("#name").val()){
                    $("#checkname").html("<font color='red'>" + '<i class="glyphicon glyphicon-remove-sign"></i>&nbsp;请填写用户名' + "</font>");
                    return false;
                }
                
                ajax({
                    type: "post",
                    url:"${contextPath}/user/checkname.do",
                    data:{
                        "name":$("#name").val()
                    }
                },function (data) {
                    if (data.status){
                        $("#checkname").html("<font color='green'>" + '<i class="glyphicon glyphicon-ok-sign"></i>&nbsp;用户名可用' + "</font>");
                        return false;
                    }else {
                        $("#checkname").html("<font color='red'>" + '<i class="glyphicon glyphicon-remove-sign"></i>&nbsp;用户已存在' + "</font>");
                        return false;
                    }
                });
            });

            $("#name").focus(function(){
                $("#checkname").html('<b>*&nbsp;</b>用户名必填');
            });

            $("#pwd").focus(function(){
                $("#checkpwd").html('<b>*&nbsp;</b>必须与上密码一致');
            });

            $("#password").focus(function(){
                $("#checkpassword").html('<b>*&nbsp;</b>密码必填，长度6-20位');
            });

            $("#password").blur(function(){
                if ($("#password").val().length < 6 || $("#password").val().length > 15){
                    $("#checkpassword").html("<font color='red'>" + '<i class="glyphicon glyphicon-remove-sign"></i>&nbsp;密码长度请在6-15位之间' + "</font>");
                }
            });

            $("#pwd").change(function(){
                if ($("#pwd").val()==$("#password").val()){
                    $("#checkpwd").html("<font color='green'>" + '<i class="glyphicon glyphicon-ok-sign"></i>&nbsp;两密码一致' + "</font>");
                }else {
                    $("#checkpwd").html("<font color='red'>" + '<i class="glyphicon glyphicon-remove-sign"></i>&nbsp;密码不一致' + "</font>");
                }
            });

            $("#role").change(function () {
                if ($("#role").val() == 999){
                    $("#agentsDiv").hide();
                }else {
                    $("#agentsDiv").show();
                }
            });

            var checkAll = function() {
                var viewNode = $("#agentId-select").next().find(".filter-option");
                var iradio = $("#checkAllInput").parent();
                if (iradio.hasClass("checked")) {
                    iradio.removeClass("checked");
                    $("#agentId-select").next().find("li").each(function (i,e) {
                        $(e).removeClass("selected");
                    });
                    viewNode.text("Nothing selected");
                } else {
                    iradio.addClass("checked");
                    $("#agentId-select").next().find("li").each(function (i,e) {
                        if (!$(e).hasClass("selected")) {
                            $(e).addClass("selected");
                        }
                    });

                    var countFilter = $("#agentId-select").attr("data-selected-text-format");
                    if (countFilter) {
                        countFilter = parseInt(countFilter.split(">")[1]);
                    }

                    var optLength = $("#agentId-select").find("option").length;

                    if (countFilter<optLength) {
                        viewNode.text( optLength+" of "+optLength+" selected");
                    }else {
                        var viewText = "";
                        var limit = ", ";
                        $("#agentId-select").find("option").each(function (i,e) {
                            viewText+=$(e).text()+limit;
                        })
                        viewNode.text(viewText.substring(1,viewText.length-2));
                    }
                }
            }

            $(".checkAllInput").click(function () {
                checkAll();
            });

            $(".checkAllInput").find(".iCheck-helper").click(function () {
                checkAll();
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
        <li><a href="">用户管理</a></li>
        <li><a href="">添加用户</a></li>
    </ol>
    <h4 class="page-title"><i class="fa fa-plus" aria-hidden="true"></i>&nbsp;添加用户</h4>

    <div style="float: right;margin-top: 5px">
        <a onclick="goback();" class="btn btn-sm m-t-10" style="margin-right: 16px;margin-bottom: -4px"><i class="fa fa-mail-reply" aria-hidden="true"></i>&nbsp;返回</a>
    </div>

    <div class="block-area" id="basic">
        <div class="tile p-15">
            <form class="form-horizontal" role="form" id="user" action="${contextPath}/user/add.do" method="post"><br>

                <div class="form-group">
                    <label for="name" class="col-lab control-label"><i class="glyphicon glyphicon-user"></i>&nbsp;&nbsp;用&nbsp;&nbsp;户&nbsp;&nbsp;名</label>
                    <div class="col-md-10">
                        <input type="text" class="form-control input-sm" id="name" name="userName">
                        <span class="tips" id="checkname"><b>*&nbsp;</b>用户名必填</span>
                    </div>
                </div><br>

                <div class="form-group">
                    <label for="role" class="col-lab control-label"><i class="glyphicon glyphicon-random"></i>&nbsp;&nbsp;用户角色</label>
                    <div class="col-md-10">
                        <select id="role" name="roleId" class="select input-sm">
                            <c:forEach var="r" items="${role}">
                                <option value="${r.roleId}">${r.roleName}</option>
                            </c:forEach>
                        </select>
                        <div class="tips"><b>*&nbsp;</b>角色决定用户的操作权限</div>
                    </div>
                </div><br>

                <div class="form-group" id="agentsDiv">
                    <label class="col-lab control-label"><i class="fa fa-desktop" aria-hidden="true"></i>&nbsp;执行器组</label>
                    <div class="col-md-10">
                        <select id="agentId-select" class="select input-sm" name="agentIds"  multiple data-selected-text-format="count>3">
                            <c:forEach var="w" items="${agents}" varStatus="index">
                                <option value="${w.agentId}">${w.name}(${w.host})</option>
                            </c:forEach>
                        </select>
                        <%--<label for="checkAllInput" class="radio-label checkAllInput"><input type="radio" id="checkAllInput">全选</label>--%>
                        <div class="tips"><b>*&nbsp;</b>此管理员可操作的执行器操组</div></br>
                    </div>
                </div>

                <div class="form-group" id="userDiv">
                    <label class="col-lab control-label"><i class="fa fa-users" aria-hidden="true"></i>&nbsp;执行用户</label>
                    <div class="col-md-10">
                        <select name="execUser" data-placeholder="选择执行任务的用户" class="tag-select-limited select input-sm" multiple>
                            <c:forEach var="user" items="${execUser}" varStatus="index">
                                <option value="${user}">${user}</option>
                            </c:forEach>
                        </select>
                        <div class="tips"><b>*&nbsp;</b>此管理员可使用的所有的用户身份</div></br>
                    </div>
                </div>

                <div class="form-group">
                    <label for="password" class="col-lab control-label"><i class="glyphicon glyphicon-lock"></i>&nbsp;&nbsp;密&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;码</label>
                    <div class="col-md-10">
                        <input type="password" class="form-control input-sm" id="password" name="password">
                        <span class="tips" id="checkpassword"><b>*&nbsp;</b>密码必填，长度6-20位</span>
                    </div>
                </div><br>

                <div class="form-group">
                    <label for="pwd" class="col-lab control-label"><i class="glyphicon glyphicon-lock"></i>&nbsp;&nbsp;确认密码</label>
                    <div class="col-md-10">
                        <input type="password" class="form-control input-sm" id="pwd">
                        <span class="tips" id="checkpwd"><b>*&nbsp;</b>必须与上密码一致</span>
                    </div>
                </div><br>

                <div class="form-group">
                    <label for="realName" class="col-lab control-label"><i class="glyphicon glyphicon-tag"></i>&nbsp;&nbsp;真实姓名</label>
                    <div class="col-md-10">
                        <input type="text" class="form-control input-sm" id="realName" name="realName">
                        <span class="tips"><b>*&nbsp;</b>真实姓名必填</span>
                    </div>
                </div><br>


                <div class="form-group">
                    <label for="contact" class="col-lab control-label"><i class="glyphicon glyphicon-comment"></i>&nbsp;&nbsp;联系方式</label>
                    <div class="col-md-10">
                        <input type="text" class="form-control input-sm" id="contact" name="contact">
                        <span class="tips">选填</span>
                    </div>
                </div><br>

                <div class="form-group">
                    <label for="email" class="col-lab control-label"><i class="glyphicon glyphicon-envelope"></i>&nbsp;&nbsp;电子邮箱</label>
                    <div class="col-md-10">
                        <input type="text" class="form-control input-sm" id="email" name="email">
                        <span class="tips">选填</span>
                    </div>
                </div><br>

                <div class="form-group">
                    <label for="qq" class="col-lab control-label"><i class="glyphicon glyphicon-magnet"></i>&nbsp;&nbsp;QQ&nbsp;号&nbsp;码</label>
                    <div class="col-md-10">
                        <input type="text" class="form-control input-sm" id="qq" name="qq">
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

<script type="text/javascript">
    $(document).ready(function(){
        /* Tag Select */
        (function(){
            /* Limited */
            $(".tag-select-limited").chosen({
                max_selected_options: 99999
            });
            /* Overflow */
            $('.overflow').niceScroll();
        })();
    });
</script>


</body>

</html>
