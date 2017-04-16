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
        var name = $("#name").val();
        if (!name){
            alert("请填写执行器名称!");
            return false;
        }
        var ip = $("#ip").val();
        if (!ip){
            alert("请填写机器IP!");
            return false;
        }
        if (!opencron.testIp(ip)){
            alert("请填写正确的IP地址!");
            return false;
        }
        var password = $("#password").val();
        if (!password){
            alert("请填写连接密码!");
            return false;
        }
        var port = $("#port").val();
        if (!port){
            alert("请填写端口号!");
            return false;
        }
        if (!opencron.testPort(port)){
            alert("请填写正确的端口号!");
            return false;
        }

        var warning = $('input[type="radio"][name="warning"]:checked').val();
        if (warning == 1){
            if (!$("#mobiles").val()){
                alert("请填写手机号码!");
                return false;
            }
            if(!opencron.testMobile($("#mobiles").val())){
                alert("请填写正确的手机号码!");
                return false;
            }
            if (!$("#email").val()){
                alert("请填写邮箱地址!");
                return false;
            }
            if(!opencron.testEmail($("#email").val())){
                alert("请填写正确的邮箱地址!");
                return false;
            }
        }

        var proxy = $('input[type="radio"][name="proxy"]:checked').val();
        var proxyId = null;
        if(proxy==1){
            proxyId = $("#proxyAgent").val();
        }

        $.ajax({
            headers:{"csrf":"${csrf}"},
            type:"POST",
            url:"${contextPath}/agent/checkhost",
            data:{
                "ip":ip
            },
            success:function(data){

                if (data == "yes"){
                    $.ajax({
                        headers:{"csrf":"${csrf}"},
                        type:"POST",
                        url:"${contextPath}/agent/checkname",
                        data:{
                            "name":name
                        },
                        success:function(data){
                            if (data == "yes"){
                                $.ajax({
                                    headers:{"csrf":"${csrf}"},
                                    type:"POST",
                                    url:"${contextPath}/verify/ping",
                                    data:{
                                        "proxy":proxy||0,
                                        "proxyId":proxyId,
                                        "ip":ip,
                                        "port":port,
                                        "password":calcMD5(password)
                                    },
                                    success:function(data){
                                        if (data == "success"){
                                            $("#agent").submit();
                                            return;
                                        }else {
                                            alert("通信失败!请检查IP和端口号及密码");
                                        }
                                    },
                                    error : function() {
                                        alert("网络繁忙请刷新页面重试!");
                                    }
                                });
                                return false;
                            }else {
                                alert("执行器名称已存在!");
                                return false;
                            }
                        },
                        error : function() {
                            alert("网络繁忙请刷新页面重试!");
                            return false;
                        }
                    });
                }else {
                    alert("该执行器IP已存在!不能重跑添加");
                    return false;
                }
            },
            error : function() {
                alert("网络繁忙请刷新页面重试!");
                return false;
            }
        })

    }

    function pingCheck(){

        var ip = $("#ip").val();
        if (!ip){
            alert("请填写机器IP!");
            return false;
        }
        var password = $("#password").val();
        if (!password){
            alert("请填写连接密码!");
            return false;
        }
        if (!opencron.testIp(ip)){
            alert("请填写正确的IP地址!");
            return false;
        }
        var port = $("#port").val();
        if (!port){
            alert("请填写端口号!");
            return false;
        }
        if (!opencron.testPort(port)){
            alert("请填写正确的端口号!");
            return false;
        }
        var proxy = $('input[type="radio"][name="proxy"]:checked').val();
        var proxyId = null;
        if(proxy==1){
            proxyId = $("#proxyAgent").val();
        }

        $("#pingResult").html("<img src='${contextPath}/img/icon-loader.gif'> <font color='#2fa4e7'>检测中...</font>");
        $.ajax({
            headers:{"csrf":"${csrf}"},
            type:"POST",
            url:"${contextPath}/verify/ping",
            data:{
                "proxy":proxy||0,
                "proxyId":proxyId,
                "ip":ip,
                "port":port,
                "password":calcMD5(password)
            },
            success:function(data){
                if (data == "success"){
                    $("#pingResult").html("<font color='green'>" + '<i class="glyphicon glyphicon-ok-sign"></i>&nbsp;通信正常' + "</font>");
                    return;
                }else {
                    $("#pingResult").html("<font color='red'>" + '<i class="glyphicon glyphicon-remove-sign"></i>&nbsp;通信失败' + "</font>");
                    return;
                }
            },
            error : function() {
                $("#pingResult").html("<font color='red'>" + '<i class="glyphicon glyphicon-remove-sign"></i>&nbsp;通信失败' + "</font>");
            }
        });

    }

    $(document).ready(function(){
        
        $("#ip").focus(function(){
            $("#pingResult").html("");
            $("#checkIp").html("");
        }).blur(function () {
            if(!$("#ip").val()){
                $("#checkIp").html("<font color='red'>" + '<i class="glyphicon glyphicon-remove-sign"></i>&nbsp;请填写执行器IP' + "</font>");
            }else {
                if (!opencron.testIp($("#ip").val())){
                    $("#checkIp").html("<font color='red'>" + '<i class="glyphicon glyphicon-remove-sign"></i>&nbsp;IP格式错误,请填写正确的IP地址' + "</font>");
                    return false;
                }else {
                    $.ajax({
                        headers:{"csrf":"${csrf}"},
                        type:"POST",
                        url:"${contextPath}/agent/checkhost",
                        data:{
                            "ip":$("#ip").val()
                        },
                        success:function(data){
                            if (data == "yes"){
                                $("#checkIp").html("<font color='green'>" + '<i class="glyphicon glyphicon-ok-sign"></i>&nbsp;执行器IP可用' + "</font>");
                                return false;
                            }else {
                                $("#checkIp").html("<font color='red'>" + '<i class="glyphicon glyphicon-remove-sign"></i>&nbsp;执行器IP已存在' + "</font>");
                                return false;
                            }
                        },
                        error : function() {
                            alert("网络繁忙请刷新页面重试!");
                            return false;
                        }
                    });
                }
            }
        });

        $("#name").blur(function(){
            if(!$("#name").val()){
                $("#checkName").html("<font color='red'>" + '<i class="glyphicon glyphicon-remove-sign"></i>&nbsp;请填写执行器名' + "</font>");
                return false;
            }
            $.ajax({
                headers:{"csrf":"${csrf}"},
                type:"POST",
                url:"${contextPath}/agent/checkname",
                data:{
                    "name":$("#name").val()
                },
                success:function(data){
                    if (data == "yes"){
                        $("#checkName").html("<font color='green'>" + '<i class="glyphicon glyphicon-ok-sign"></i>&nbsp;执行器名可用' + "</font>");
                        return false;
                    }else {
                        $("#checkName").html("<font color='red'>" + '<i class="glyphicon glyphicon-remove-sign"></i>&nbsp;执行器名已存在' + "</font>");
                        return false;
                    }
                },
                error : function() {
                    alert("网络繁忙请刷新页面重试!");
                    return false;
                }
            });
        }).focus(function(){
            $("#checkName").html('<b>*&nbsp;</b>执行器名称必填');
        });

        $("#port").focus(function(){
            $("#pingResult").html("");
        });

        $("#warning1").next().attr("onclick","showContact()");
        $("#warning0").next().attr("onclick","hideContact()");

        $("#proxy1").next().attr("onclick","showProxy()");
        $("#proxy0").next().attr("onclick","hideProxy()");

        var proxy = $('input[type="radio"][name="proxy"]:checked').val();
        if(proxy==1){
            $(".proxy").show();
        }else {
            $(".proxy").hide();
        }

    });

    function showContact(){$(".contact").show()}
    function hideContact(){$(".contact").hide()}
    function showProxy(){$(".proxy").show()}
    function hideProxy(){$(".proxy").hide()}

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
        <li><a href="">执行器管理</a></li>
        <li><a href="">添加执行器</a></li>
    </ol>
    <h4 class="page-title"><i aria-hidden="true" class="fa fa-plus"></i>&nbsp;添加执行器</h4>

    <div style="float: right;margin-top: 5px">
        <a onclick="goback();" class="btn btn-sm m-t-10" style="margin-right: 16px;margin-bottom: -4px"><i class="fa fa-mail-reply" aria-hidden="true"></i>&nbsp;返回</a>
    </div>

    <div class="block-area" id="basic">
        <div class="tile p-15">
            <form  class="form-horizontal" role="form"  id="agent" action="${contextPath}/agent/add" method="post"></br>
                <input type="hidden" name="csrf" value="${csrf}">
                <div class="form-group">
                    <label for="name" class="col-lab control-label"><i class="glyphicon glyphicon-leaf"></i>&nbsp;&nbsp;执行器名：</label>
                    <div class="col-md-10">
                        <input type="text" class="form-control input-sm" id="name" name="name">
                        <span class="tips" id="checkName"><b>*&nbsp;</b>执行器名称必填</span>
                    </div>
                </div><br>

                <c:if test="${empty connAgents}">
                    <!--默认为直连-->
                    <input type="hidden" name="proxy" value="0">
                </c:if>
                <c:if test="${!empty connAgents}">
                    <div class="form-group">
                        <label class="col-lab control-label"><i class="glyphicon glyphicon-transfer"></i>&nbsp;&nbsp;连接类型：</label>&nbsp;&nbsp;&nbsp;
                        <div class="col-md-10">
                            <label  onclick="hideProxy()" for="proxy0" class="radio-label"><input type="radio" name="proxy" value="0" id="proxy0" checked>直连</label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            <label  onclick="showProxy()" for="proxy1" class="radio-label"><input type="radio" name="proxy" value="1" id="proxy1">代理&nbsp;&nbsp;&nbsp;</label>
                            </br><span class="tips"><b>*&nbsp;</b>直连:直接连接目标执行器,代理:通过其他执行器代理连接目标执行器</span>
                        </div>
                    </div><br>

                    <div class="form-group proxy" style="display: none;">
                        <label for="proxyAgent" class="col-lab control-label"><i class="glyphicon glyphicon-leaf"></i>&nbsp;&nbsp;代&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;理：</label>
                        <div class="col-md-10">
                            <select id="proxyAgent" name="proxyAgent" class="form-control input-sm">
                                <c:forEach var="d" items="${connAgents}">
                                    <option value="${d.agentId}">${d.ip}&nbsp;(${d.name})</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div><br>
                </c:if>

                <div class="form-group">
                    <label for="ip" class="col-lab control-label"><i class="glyphicon glyphicon-tag"></i>&nbsp;&nbsp;机&nbsp;&nbsp;器&nbsp;&nbsp;IP：</label>
                    <div class="col-md-10">
                        <input type="text" class="form-control input-sm" id="ip" name="ip">
                        <span class="tips"><b>*&nbsp;</b>执行器IP地址只能为点分十进制方式表示,如192.168.0.1</span>
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="tips" id="checkIp"></span>
                    </div>
                </div><br>

                <div class="form-group">
                    <label for="password" class="col-lab control-label"><i class="glyphicon glyphicon-lock"></i>&nbsp;&nbsp;连接密码：</label>
                    <div class="col-md-10">
                        <input type="text" class="form-control input-sm" id="password" name="password">
                        <span class="tips"><b>*&nbsp;</b>连接密码必填,调用执行器的权限依据</span>
                    </div>
                </div><br>

                <div class="form-group">
                    <label for="port" class="col-lab control-label"><i class="glyphicon glyphicon-question-sign"></i>&nbsp;&nbsp;端&nbsp;&nbsp;口&nbsp;&nbsp;号：</label>
                    <div class="col-md-10">
                        <input type="text" class="form-control input-sm" id="port" name="port">
                        <span class="tips"><b>*&nbsp;</b>执行器端口号只能是数字，范围从0到65535</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        <a href="#" onclick="pingCheck()"><i class="glyphicon glyphicon-signal"></i>&nbsp;检测通信</a>
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="tips" id="pingResult"></span>
                    </div>
                </div><br>

                <div class="form-group">
                    <label class="col-lab control-label"><i class="glyphicon glyphicon-warning-sign"></i>&nbsp;&nbsp;失联报警：</label>&nbsp;&nbsp;&nbsp;
                    <div class="col-md-10">
                        <label  onclick="showContact()" for="warning1" class="radio-label"><input type="radio" name="warning" value="1" id="warning1" checked>是&nbsp;&nbsp;&nbsp;</label>
                        <label  onclick="hideContact()" for="warning0" class="radio-label"><input type="radio" name="warning" value="0" id="warning0">否</label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        </br><span class="tips"><b>*&nbsp;</b>执行器通信不正常时是否发信息报警</span>
                    </div>
                </div><br>

                <div class="form-group contact">
                    <label for="mobiles" class="col-lab control-label"><i class="glyphicon glyphicon-comment"></i>&nbsp;&nbsp;报警手机：</label>
                    <div class="col-md-10">
                        <input type="text" class="form-control input-sm" id="mobiles" name="mobiles">
                        <span class="tips"><b>*&nbsp;</b>执行器通信不正常时将发送短信给此手机</span>
                    </div>
                </div><br>

                <div class="form-group contact">
                    <label for="email" class="col-lab control-label"><i class="glyphicon glyphicon-envelope"></i>&nbsp;&nbsp;报警邮箱：</label>
                    <div class="col-md-10">
                        <input type="text" class="form-control input-sm" id="email" name="emailAddress">
                        <span class="tips"><b>*&nbsp;</b>执行器通信不正常时将发送报告给此邮箱</span>
                    </div>
                </div><br>

                <div class="form-group">
                    <label for="comment" class="col-lab control-label"><i class="glyphicon glyphicon-magnet"></i>&nbsp;&nbsp;描&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;述：</label>
                    <div class="col-md-10">
                        <textarea class="form-control input-sm" id="comment" name="comment"></textarea>
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
