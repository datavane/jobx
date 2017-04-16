<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String port = request.getServerPort() == 80 ? "" : (":"+request.getServerPort());
    String path = request.getContextPath().replaceAll("/$","");
    String contextPath = request.getScheme()+"://"+request.getServerName()+port+path;
    pageContext.setAttribute("contextPath",contextPath);
%>

<!DOCTYPE html>

<!--[if IE 9 ]><html class="ie9"><![endif]-->
<head>
    <meta charset="UTF-8">
    <meta name="keywords" content="opencron,crontab,a better crontab,Let's crontab easy">
    <meta name="author" content="author:benjobs,wechat:wolfboys,Created by  2016" />

    <title>opencron</title>
    <jsp:include page="/WEB-INF/common/resource.jsp"/>
    <script type="text/javascript">

        <c:if test="${!empty opencron_user}">
            window.location.href="${contextPath}/home?csrf=${csrf}";
        </c:if>

        $(document).ready(function(){
            var skin = $.cookie("opencron_skin");
            if(skin) {
                $('body').attr('id', skin);
            }
        });

        var sendpwd = "";
        var flag = false;

        function isremember(){
            return $("#remember").prop("checked");
        }

        $(document).ready(function(){

            loginCookie.init("#username","#password")

            $("#password").change(function(){
                sendpwd = calcMD5($(this).val());
            }).focus(function(){
                $("#userList").remove();
                var history = loginCookie.all();
                var username = $("#username").val();
                if(history){
                    for(var i=0;i<history.length;i++){
                        if(history[i].username==username){
                            sendpwd = history[i].password;
                            if(sendpwd) {
                                flag = true;
                                $("#password").val("88888888");
                                login();
                            }
                        }
                    }
                }
            });

            $("#btnLogin").click(function(){
                login();
            });

            document.onkeydown = function(e){
                var ev = document.all ? window.event : e;
                if(ev.keyCode==13) {
                    login();
                }
            }

            var cookie = loginCookie.last();
            if(cookie){
                var loginCode = cookie.username;
                var pwd = cookie.password;

                if (loginCode) {
                    $("#username").val(loginCode);
                }
                if (pwd) {
                    $("#password").val(pwd.substr(0,12));
                    sendpwd = pwd;
                    flag = true;
                    $("#remember").prop("checked",true);
                    $("#remember").parent().removeClass("checked").addClass("checked");
                    $("#remember").parent().attr("aria-checked",true);
                }
            }

        });

        var loginCookie = {
            //接收输入框
            vars : {
                "nameNode":"",
                "nameInput":"",
                "pwdNode":"",
                "index":-1
            },

            init:function(name,pwd){
                this.vars.nameNode = $(name);
                this.vars.nameInput = $(name).val();
                this.vars.pwdNode = $(pwd);
                this.band();
            },

            get:function(name){
                var obj = $.cookie(name);
                if(obj){
                    try{
                        return eval("("+obj+")");
                    }catch(e){
                        return null;
                    }
                }
                return null;
            },

            last:function(){
                return loginCookie.get("login_last");
            },

            all:function(){
                return loginCookie.get("login_history");
            },

            find:function(name){

                var array = new Array();

                var obj = loginCookie.last;
                if(obj){
                    if( loginCookie.has(obj.username,name) ) {
                        if(obj.username && obj.password) {
                            array.push(obj);
                        }
                    }
                }

                var cookie = loginCookie.all();
                if(cookie) {
                    for(var i=0;i<cookie.length;i++){
                        var obj = cookie[i];
                        var username = obj.username;
                        if( loginCookie.has(username,name) && obj.password) {
                            array.push(obj);
                        }
                    }
                }
                return array;
            },

            set:function(name,password) {

                var cookObj = "{'username':'"+name+"','password':'"+password+"'}";

                $.cookie("login_last", cookObj, {
                    expires : 30,
                    domain:document.domain,
                    path:"/"
                });

                var history = loginCookie.all();
                if(history){
                    var json = "[";
                    var ishas = true;
                    //循环历史的
                    for(var i=0;i<history.length;i++){
                        var obj = history[i];
                        var username = obj.username;
                        var pwd = obj.password;
                        if(username==name) {//发现当前的用户记录在历史里面存在,则拿当前覆盖历史的
                            ishas = false;
                            json+="{'username':'"+name+"','password':'"+password+"'},";
                        }else {//不能存的用户,直接添加
                            json+="{'username':'"+username+"','password':'"+pwd+"'},";
                        }
                    }

                    //把当前的用户名新增的到历史数组里
                    if(ishas) {
                        json+="{'username':'"+name+"','password':'"+password+"'},";
                    }

                    json = json.substring(0,json.length-1)+"]";

                    $.cookie("login_history", json, {
                        expires : 30,
                        domain:document.domain,
                        path:"/"
                    });
                }else {
                    $.cookie("login_history", "["+cookObj+"]", {
                        expires : 30,
                        domain:document.domain,
                        path:"/"
                    });
                }
            },

            clean:function(name) {
                $.cookie("login_last", null, {
                    expires : -1,
                    domain:document.domain,
                    path:"/"
                });
                var history = loginCookie.all();
                if(history){
                    var json = "[";
                    for(var i=0;i<history.length;i++){
                        var obj = history[i];
                        var username = obj.username;
                        var password = obj.password;
                        if( username!=name ) {
                            json+="{'username':'"+username+"','password':'"+password+"'},";
                        }
                    }
                    json = json.substring(0,json.length-1)+"]";
                    $.cookie("login_history", json, {
                        expires : 30,
                        domain:document.domain,
                        path:"/"
                    });
                }
            },

            //模糊查找
            has:function(text,subtext){
                if(text==undefined||subtext==undefined||subtext==null||subtext==""||text.length==0||subtext.length>text.length)
                    return false;
                if(text.substr(0,subtext.length)==subtext)
                    return true;
                else
                    return false;
                return true;
            },

            band:function(){

                loginCookie.vars.nameNode.keyup(function(event){

                    if(event.keyCode==40||event.keyCode==38||event.keyCode==13){
                        return;
                    }

                    $("#userList").remove();

                    if(loginCookie.vars.nameNode.val()==''){
                        loginCookie.vars.pwdNode.val("");
                        return false;
                    }

                    var userVal = loginCookie.vars.nameNode.val();
                    var last = loginCookie.last();

                    if(last&&last.username==userVal){
                        loginCookie.vars.pwdNode.val("88888888");
                        sendpwd = last.password;
                        flag = true;
                        return;
                    }

                    loginCookie.vars.pwdNode.val("");

                    flag = false;

                    var obj = loginCookie.find(userVal);

                    if(obj && obj.length>0) {
                        var oUl = $('<select id="userList" class="form-control" style="border: none; width:'+loginCookie.vars.nameNode.outerWidth()+'px;margin-top:-10px;border-radius: 0px; position: absolute;z-index: 99;background:rgba(30, 30, 40, 0.98) none repeat scroll 0 0" multiple="">');
                        loginCookie.vars.nameNode.after(oUl);

                        for(var i=0;i<obj.length;i++){
                            //匹配已存在的用户名
                            if(loginCookie.has(obj[i].username,userVal)){
                                var oLi = $("<option id='li_"+i+"' data-id='"+obj[i].password+"'>"+obj[i].username+"</option>");
                                $("#userList").append(oLi);
                            }
                        }

                        //点击LI内
                        $("#userList").find("option").on("click",function(){
                            var usertxt =$(this).text();
                            loginCookie.vars.nameNode.val(usertxt);
                            for(var i=0;i<obj.length;i++){
                                if(obj[i].username==usertxt){
                                    loginCookie.vars.pwdNode.val("88888888");
                                    sendpwd = obj[i].password;
                                    flag = true;
                                    login();
                                    break;
                                }
                            }
                            $("#userList").remove();
                        }).hover(function(){
                            var _this = document.getElementById($(this).attr("id"));
                            _this.style.background = "rgba(215,215,215,0.45)";
                        },function(){
                            var _this = document.getElementById($(this).attr("id"));
                            _this.style.background = "rgba(30, 30, 30, 0.98)";
                        });
                    } else {
                        sendpwd = "";
                        loginCookie.vars.pwdNode.val("");
                        $("#userList").remove();
                    }
                });

                loginCookie.vars.nameNode.keydown(function(event) {
                    if(event.keyCode==9){//tab
                        $("#userList").remove();
                        $("#passsword").focus();
                    } else if(event.keyCode==38){//up
                        --loginCookie.vars.index;
                        if(loginCookie.vars.index<0){
                            loginCookie.vars.index = $("#userList option").length-1;
                        }
                        $("#userList option").eq(loginCookie.vars.index).css("background","rgba(215, 215, 215, 0.45)").siblings().css("background","rgba(30, 30, 30, 0.98)");
                    }else if(event.keyCode==40){//down
                        ++loginCookie.vars.index;
                        if( loginCookie.vars.index > $("#userList option").length-1 ){
                            loginCookie.vars.index = 0;
                        }
                        $("#userList option").eq(loginCookie.vars.index).css("background","rgba(215, 215, 215, 0.45)").siblings().css("background","rgba(30, 30, 30, 0.98)");
                    }else if(event.keyCode==13){ //enter
                        var name = $("#userList option").eq(loginCookie.vars.index).text();
                        if(name){
                            loginCookie.vars.nameNode.val(name);
                            sendpwd = $("#userList option").eq(loginCookie.vars.index).attr('data-id');
                            flag = true;
                        }
                        loginCookie.vars.pwdNode.val("88888888");
                        $("#userList").remove();
                    }
                });

            }

        }

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

            if( !flag ) {
                sendpwd = calcMD5($("#password").val());
            }

            var data = {username:username,password:sendpwd};

            $.ajax({
                type: "POST",
                url: "${contextPath}/login",
                data: data,
                success: function (data) {
                    if(data.msg){
                        $("#error_msg").html('<font color="red">'+data.msg+'</font>');
                        $("#btnLogin").prop("disabled",false);
                    } else {
                        if (data.status == "success"){
                            if(isremember()){
                                loginCookie.set(username,sendpwd);
                            }else {
                                loginCookie.clean(username);
                            }
                            window.location.href = "${contextPath}"+data.url;
                        }else {
                            $("#error_msg").html('<font color="red">请修改初始密码</font>');
                            $("#pwdform")[0].reset();
                            $("#id").val(data.userId);
                            $("#csrf").val(data.csrf);
                            $('#pwdModal').modal('show');
                        }
                    }
                    return false;
                },
                error : function (){
                    $("#error_msg").html('<font color="red">网络繁忙请刷新页面重试!</font>');
                    $("#btnLogin").prop("disabled",false);
                }

            });
            return false;
        }

        function savePwd(){
            var id = $("#id").val();
            if (!id){
                alert("页面异常，请刷新重试!");
                return false;
            }

            var pwd1 = $("#pwd1").val();
            if (!pwd1){
                alert("请填新密码!");
                return false;
            }
            if (pwd1.length < 6 || pwd1.length > 15){
                alert("密码长度请在6-15位之间!");
                return false;
            }
            var pwd2 = $("#pwd2").val();
            if (!pwd2){
                alert("请填写确认密码!");
                return false;
            }
            if (pwd2.length < 6 || pwd2.length > 15){
                alert("密码长度请在6-15位之间!");
                return false;
            }
            if (pwd1 != pwd2){
                alert("两密码不一致!");
                return false;
            }
            $.ajax({
                headers:{"csrf":$("#csrf").val()},
                type:"POST",
                url:"${contextPath}/user/editpwd",
                data:{
                    "id":id,
                    "pwd0":calcMD5($("#password").val()),
                    "pwd1":calcMD5(pwd1),
                    "pwd2":calcMD5(pwd2)
                },
                success:function(data){
                    if (data == "success"){
                        $('#pwdModal').modal('hide');
                        alertMsg("修改成功,请重新登录");
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
                },
                error : function() {
                    alert("网络繁忙请刷新页面重试!");
                    return false;
                }
            });
        }

    </script>

</head>
<body id="skin-blur-ocean">

<section id="login">
    <header>
        <h1 style="width: 250px">
            <img src="${contextPath}/img/opencron.png">
        </h1>
        <h4 style="margin-top: 5px;">Welcome to opencron,Let's crontab easy</h4>
    </header>

    <div id="box-login" class="box tile animated active" style="margin-top:30px;">
        <h2 class="m-t-0 m-b-15">登录</h2>
        <input type="text" placeholder="请输入用户名" id="username" class="login-control m-b-10">
        <input type="password" placeholder="请输入密码" id="password" class="login-control">
        <div class="checkbox m-b-20">
            <label style="color: rgb(192,192,192)">
                <input type="checkbox" id="remember"> 记住密码
            </label>
        </div>

        <button id="btnLogin" class="btn btn-sm m-r-5" type="button">登录</button>
        <c:if test="${empty loginMsg}">
            <span id="error_msg" style=" color: rgb(192,192,192)">请输入您的用户名和密码进行登陆 </span>
        </c:if>
        <c:if test="${!empty loginMsg}">
            <span id="error_msg" style=" color: rgb(255,0,0)"> ${loginMsg} </span>
        </c:if>
    </div>

    <div class="modal fade" id="pwdModal" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4>修改默认密码</h4>
                </div>
                <div class="modal-body">
                    <form class="form-horizontal" role="form" id="pwdform">
                        <input type="hidden" id="id">
                        <input type="hidden" id="csrf">
                        <div class="form-group" style="margin-bottom: 20px;">
                            <label for="pwd1" class="col-lab control-label"><i class="glyphicon glyphicon-lock"></i>&nbsp;&nbsp;新&nbsp;&nbsp;密&nbsp;&nbsp;码：</label>
                            <div class="col-md-9">
                                <input type="password" class="form-control " id="pwd1" placeholder="请输入新密码">
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="pwd2" class="col-lab control-label"><i class="glyphicon glyphicon-lock"></i>&nbsp;&nbsp;确认密码：</label>
                            <div class="col-md-9">
                                <input type="password" class="form-control " id="pwd2" placeholder="请输入确认密码"/>&nbsp;&nbsp;<label id="checkpwd"></label>
                            </div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <center>
                        <button type="button" class="btn btn-sm"  onclick="savePwd()">保存</button>&nbsp;&nbsp;
                        <button type="button" class="btn btn-sm"  data-dismiss="modal">关闭</button>
                    </center>
                </div>
            </div>
        </div>
    </div>

</section>


<!-- Older IE Message -->
<!--[if lt IE 9]>
<div class="ie-block">
    <h1 class="Ops">Ooops!</h1>
    <p> 您正在使用一个过时的互联网浏览器，升级到下列任何一个网络浏览器，以访问该网站的最大功能。 </p>
    <ul class="browsers">
        <li>
            <a href="https://www.google.com/intl/en/chrome/browser/">
                <img src="${contextPath}/img/browsers/chrome.png" alt="">
                <div>Google Chrome</div>
            </a>
        </li>
        <li>
            <a href="http://www.mozilla.org/en-US/firefox/new/">
                <img src="${contextPath}/img/browsers/firefox.png" alt="">
                <div>Mozilla Firefox</div>
            </a>
        </li>
        <li>
            <a href="http://www.opera.com/computer/windows">
                <img src="${contextPath}/img/browsers/opera.png" alt="">
                <div>Opera</div>
            </a>
        </li>
        <li>
            <a href="http://safari.en.softonic.com/">
                <img src="${contextPath}/img/browsers/safari.png" alt="">
                <div>Safari</div>
            </a>
        </li>
        <li>
            <a href="http://windows.microsoft.com/en-us/internet-explorer/downloads/ie-10/worldwide-languages">
                <img src="${contextPath}/img/browsers/ie.png" alt="">
                <div>Internet Explorer(New)</div>
            </a>
        </li>
    </ul>
    <p>请升级您的浏览器以便带来更好更好的用户体验 <br/>谢谢...</p>
</div>
<![endif]-->

</body>
</html>