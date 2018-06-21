<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!--[if IE 9 ]><html class="ie9"><![endif]-->
<head>
    <meta charset="UTF-8">
    <meta name="keywords" content="JobX,Let's scheduling easy">
    <meta name="author" content="author:benjobs,wechat:wolfboys,Created by  2016" />
    <title>jobx</title>

    <!-- jQuery -->
    <script type="text/javascript" src="${contextPath}/static/js/jquery.js?resId=${resourceId}"></script> <!-- jQuery Library -->
    <script type="text/javascript" src="${contextPath}/static/js/jquery-ui.min.js?resId=${resourceId}"></script> <!-- jQuery UI -->
    <link rel="stylesheet" href="${contextPath}/static/css/style.css?resId=${resourceId}" />
    <link rel="shortcut icon" href="${contextPath}/static/img/favicon.ico?resId=${resourceId}" />
    <!-- Javascript Libraries -->

    <!-- Bootstrap -->
    <script type="text/javascript" src="${contextPath}/static/js/bootstrap.js?resId=${resourceId}"></script>
    <link rel="stylesheet" href="${contextPath}/static/css/bootstrap.css?resId=${resourceId}" />

    <!-- All JS functions -->
    <script id="themeFunctions" src="${contextPath}/static/js/functions.js?${contextPath}&resId=${resourceId}"></script>
    <script type="text/javascript" src="${contextPath}/static/js/testdevice.js?resId=${resourceId}"></script>

    <!-- MD5 -->
    <script type="text/javascript" src="${contextPath}/static/js/md5.js?resId=${resourceId}"></script>
    <script type="text/javascript" src="${contextPath}/static/js/jquery.base64.js?resId=${resourceId}"></script>
    <script type="text/javascript" src="${contextPath}/static/js/jquery.cookie.js?resId=${resourceId}"></script>
    <script type="text/javascript" src="${contextPath}/static/js/jobx.js?resId=${resourceId}"></script>

    <!-- Vendor styles -->
    <link rel="stylesheet" href="${contextPath}/static/fonts/material-design-iconic-font/css/material-design-iconic-font.min.css">
    <link rel="stylesheet" href="${contextPath}/static/css/animate.min.css">
    <!-- App styles -->
    <link rel="stylesheet" href="${contextPath}/static/css/app.min.css">

    <style type="text/css">
        .modal-content label {
            color: rgba(255,255,255,.75);
        }
        .usr_label {
            padding-left: 17px;
            margin-top: -20px;
            color: red!important;
        }
        .form-control, .form-control:focus {
            -webkit-box-shadow:none;
            box-shadow:none;
        }
        .modal-footer{
            margin-top: -20px;
            z-index: 9999;
        }
        .modal-footer .btn:hover{
            border: 1px solid rgba(255, 255, 255, 0.41);
            color: rgba(225,225,225,0.8);
        }
    </style>
    <script type="text/javascript">
        <c:if test="${!empty jobx_user}">
        window.location.href="${contextPath}/dashboard.htm";
        </c:if>

        $(document).ready(function() {
            if ( "${sessionScope.skin}" == "" ) {
                //从session从未读到skin则先从cookie中获取
                var skin = $.cookie("skin");
                if(skin) {
                    $('body').attr('id', skin);
                }else {
                    skin = "skin-4";
                    $('body').attr('id',skin);
                    $.cookie("skin", skin, {
                        expires : 30,
                        domain:document.domain,
                        path:"/"
                    });
                }

                //同步到session中...
                $.ajax({
                    type: "post",
                    url: "${contextPath}/config/skin.do",
                    dataType: "JSON",
                    data:{
                        "skin":skin
                    }
                });
            }
        });

        $(document).ready(function(){
            $("#btnLogin").click(function(){
                login();
            });

            document.onkeydown = function(e){
                var ev = document.all ? window.event : e;
                if(ev.keyCode==13) {
                    login();
                }
            }
        });

        function login(){
            if($("#username").val().length==0){
                $("#error_msg").html('<font color="red">请输入用户名</font>');
                return false;
            }
            if($("#password").val().length==0){
                $("#error_msg").html('<font color="red">请输入密码</font>');
                return false;
            }
            $("#error_msg").html('<font color="green">正在登陆...</font>');
            $("#btnLogin").prop("disabled",true);

            var username = $("#username").val();
            var password = calcMD5($("#password").val());

            var data = {username:username,password:password};

            ajax({
                type: "post",
                url: "${contextPath}/login.do",
                data: data
            },function (data) {
                if(data.msg){
                    $("#error_msg").html('<font color="red">'+data.msg+'</font>');
                    $("#btnLogin").prop("disabled",false);
                } else {
                    if (data.status == "success"){
                        window.location.href = "${contextPath}"+data.url;
                    }else {
                        $("#error_msg").html('<font color="red">请修改初始密码</font>');
                        $("#pwdform")[0].reset();
                        $("#id").val(data.userId);
                        $('#pwdModal').modal('show');
                    }
                }
                return false;
            },function () {
                $("#error_msg").html('<font color="red">网络繁忙请刷新页面重试!</font>');
                $("#btnLogin").prop("disabled",false);
            });
            return false;
        }

        function savePwd(){
            var id = $("#id").val();
            if (!id){
                $(".usr_label").text("页面异常，请刷新重试!");
                return false;
            }

            var pwd1 = $("#pwd1").val();
            if (!pwd1){
                $(".usr_label").text("请填新密码!");
                return false;
            }
            if (pwd1.length < 6 || pwd1.length > 15){
                $(".usr_label").text("密码长度请在6-15位之间!");
                return false;
            }
            var pwd2 = $("#pwd2").val();
            if (!pwd2){
                $(".usr_label").text("请填写确认密码!");
                return false;
            }
            if (pwd2.length < 6 || pwd2.length > 15){
                $(".usr_label").text("密码长度请在6-15位之间!");
                return false;
            }
            if (pwd1 != pwd2){
                $(".usr_label").text("两密码不一致!");
                return false;
            }
            ajax({
                type: "post",
                url:"${contextPath}/user/pwd.do",
                data:{
                    "id":id,
                    "pwd0":calcMD5($("#password").val()),
                    "pwd1":calcMD5(pwd1),
                    "pwd2":calcMD5(pwd2)
                }
            },function (data) {
                if (data == "true"){
                    $('#pwdModal').modal('hide');
                    $("#btnLogin").prop("disabled",false);
                    $("#password").val("").focus();
                    $("#error_msg").html('<font color="green">请重新登录</font>');
                    return false;
                }
                if(data == "one"){
                    $("#oldpwd").html("<font color='red'>" + '<i class="glyphicon glyphicon-remove-sign"></i>&nbsp;密码不正确' + "</font>");
                    return false;
                }
                if(data == "two"){
                    $("#checkpwd").html("<font color='red'>" + '<i class="glyphicon glyphicon-remove-sign"></i>&nbsp;密码不一致' + "</font>");
                    return false;
                }
            });
        }
    </script>

</head>

<body id="${sessionScope.skin}">
<div class="login">
    <!-- Login -->
    <div class="login__block active" id="l-login">
        <div class="login__block__header">
            <h1 style="width: 250px">
                <img src="${contextPath}/static/img/jobx.png" style="width: 200px;">
            </h1>
            Let's scheduling easy
        </div>
        <div class="login__block__body">
            <div class="form-group" style="margin-top: -15px;margin-bottom: 8px;">
                <span id="error_msg" style=" color: rgb(255,0,0)"> ${loginMsg} </span>
            </div>

            <div class="form-group">
                <input type="text" class="form-control text-center" placeholder="Account" id="username">
            </div>
            <div class="form-group">
                <input type="password" class="form-control text-center" placeholder="Password" id="password">
            </div>
            <a href="javascript:void (0)" id="btnLogin" class="btn btn--icon login__block__btn"><i class="zmdi zmdi-long-arrow-right" style="margin-top:10px"></i></a>
        </div>
    </div>

</div>

<div class="modal fade" id="pwdModal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button class="close btn-float" data-dismiss="modal" aria-hidden="true"><i class="md md-close"></i></button>
                <h4>修改默认密码</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal" role="form" id="pwdform">
                    <input type="hidden" id="id">
                    <div class="form-group" style="margin-bottom: 20px;">
                        <label for="pwd1" class="col-lab control-label"><i class="glyphicon glyphicon-lock"></i>&nbsp;&nbsp;新&nbsp;&nbsp;密&nbsp;&nbsp;码</label>
                        <div class="col-md-9">
                            <input type="password" class="form-control " id="pwd1" placeholder="请输入新密码">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="pwd2" class="col-lab control-label"><i class="glyphicon glyphicon-lock"></i>&nbsp;&nbsp;确认密码</label>
                        <div class="col-md-9">
                            <input type="password" class="form-control " id="pwd2" placeholder="请输入确认密码"/>&nbsp;&nbsp;<label id="checkpwd"></label>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="usr_label"></label>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-sm" style="cursor: pointer" onclick="savePwd()">保存</button>&nbsp;&nbsp;
                <button type="button" class="btn btn-sm"  style="cursor: pointer" data-dismiss="modal">关闭</button>
            </div>
        </div>
    </div>
</div>

<!-- Older IE warning message -->
<!--[if IE]>
<div class="ie-warning">
    <h1>Warning!!</h1>
    <p>You are using an outdated version of Internet Explorer, please upgrade to any of the following web browsers to access this website.</p>

    <div class="ie-warning__downloads">
        <a href="http://www.google.com/chrome">
            <img src="img/browsers/chrome.png" alt="">
        </a>

        <a href="https://www.mozilla.org/en-US/firefox/new">
            <img src="img/browsers/firefox.png" alt="">
        </a>

        <a href="http://www.opera.com">
            <img src="img/browsers/opera.png" alt="">
        </a>

        <a href="https://support.apple.com/downloads/safari">
            <img src="img/browsers/safari.png" alt="">
        </a>

        <a href="https://www.microsoft.com/en-us/windows/microsoft-edge">
            <img src="img/browsers/edge.png" alt="">
        </a>

        <a href="http://windows.microsoft.com/en-us/internet-explorer/download-ie">
            <img src="img/browsers/ie.png" alt="">
        </a>
    </div>
    <p>Sorry for the inconvenience!</p>
</div>
<![endif]-->
</body>
</html>