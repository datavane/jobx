<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib prefix="cron"  uri="http://www.opencron.org"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">
<meta name="author" content="author:benjobs,wechat:wolfboys,Created by 2016" />
<head>
    <jsp:include page="/WEB-INF/common/resource.jsp"/>

    <script type="text/javascript">

        function save(){

            var senderEmail = $("#senderEmail").val();
            if (!senderEmail){
                alert("请填写发件邮箱!");
                return false;
            }
            if(!opencron.testEmail(senderEmail)){
                alert("请填写正确的邮箱地址!");
                return false;
            }

            var smtpHost = $("#smtpHost").val();
            if (!smtpHost){
                alert("请填写发件SMTP地址!");
                return false;
            }

            var smtpPort = $("#smtpPort").val();
            if (!smtpPort){
                alert("请填写发件SMTP端口号!");
                return false;
            }else if(!opencron.testNumber(smtpPort)){
                alert("SMTP端口号必须是数字!");
                return false;
            }

            var password = $("#password").val();
            if (!password){
                alert("请填写邮箱密码!");
                return false;
            }
            var sendUrl = $("#sendUrl").val();
            if (!sendUrl){
                alert("请填写发送短信的URL!");
                return false;
            }
            var interval = $("#spaceTime").val();
            if (!interval){
                alert("请填写告警发送的时间间隔!");
                return false;
            }
            if(!opencron.testNumber(interval)){
                alert("时间间隔必须为数字!");
                return false;
            }
            $("#config").submit();
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
        <li><a href="">opencron</a></li>
        <li><a href="">系统设置</a></li>
    </ol>
    <h4 class="page-title"><i class="fa fa-edit" aria-hidden="true" style="font-style: 30px;"></i>&nbsp;修改设置</h4>

    <div style="float: right;margin-top: 5px">
        <a onclick="goback();" class="btn btn-sm m-t-10" style="margin-right: 16px;margin-bottom: -4px"><i class="fa fa-mail-reply" aria-hidden="true"></i>&nbsp;返回</a>
    </div>

    <div class="block-area" id="basic">

        <div class="tile p-15">
            <form class="form-horizontal" role="form"  id="config" action="${contextPath}/config/edit" method="post"><br>
                <input type="hidden" name="csrf" value="${csrf}">
                <div class="form-group">
                    <label for="senderEmail" class="col-lab control-label"><i class="glyphicon glyphicon-envelope"></i>&nbsp;&nbsp;发件邮箱：</label>
                    <div class="col-md-10">
                        <input type="text" class="form-control input-sm" id="senderEmail" name="senderEmail" value="${config.senderEmail}">
                        <span class="tips"><b>*&nbsp;</b>发件人的邮箱地址</span>
                    </div>
                </div><br>

                <div class="form-group">
                    <label for="smtpHost" class="col-lab control-label"><i class="glyphicon glyphicon-map-marker"></i>SMTP地址：</label>
                    <div class="col-md-10">
                        <input type="text" class="form-control input-sm" id="smtpHost" name="smtpHost" value="${config.smtpHost}">
                        <span class="tips"><b>*&nbsp;</b>发件SMTP地址</span>
                    </div>
                </div><br>


                <div class="form-group">
                    <label for="smtpPort" class="col-lab control-label"><i class="glyphicon glyphicon-filter"></i>SMTP端口：</label>
                    <div class="col-md-10">
                        <input type="text" class="form-control input-sm" id="smtpPort" name="smtpPort" value="${config.smtpPort}">
                        <span class="tips"><b>*&nbsp;</b>发件SMTP（SSL协议）端口号</span>
                    </div>
                </div><br>


                <div class="form-group">
                    <label for="password" class="col-lab control-label"><i class="glyphicon glyphicon-lock"></i>&nbsp;&nbsp;邮箱密码：</label>
                    <div class="col-md-10">
                        <input type="text" class="form-control input-sm" id="password" name="password" value="${config.password}">
                        <span class="tips"><b>*&nbsp;</b>发件邮箱账号的密码</span>
                    </div>
                </div><br>

                <div class="form-group">
                    <label for="sendUrl" class="col-lab control-label"><i class="glyphicon glyphicon-font"></i>&nbsp;&nbsp;发信URL：</label>
                    <div class="col-md-10">
                        <textarea class="form-control input-sm" id="sendUrl" name="sendUrl">${config.sendUrl}</textarea>
                        <span class="tips"><b>*&nbsp;</b>短信发送服务所需的URL</span>
                    </div>
                </div><br>

                <div class="form-group">
                    <label for="template" class="col-lab control-label"><i class="glyphicon glyphicon-list-alt"></i>&nbsp;&nbsp;短信模板：</label>
                    <div class="col-md-10">
                        <textarea class="form-control input-sm" id="template" name="template">${config.template}</textarea>
                        <span class="tips"><b>*&nbsp;</b>运营商规定的企业发送短信格式模板</span>
                    </div>
                </div><br>

                <div class="form-group">
                    <label for="spaceTime" class="col-lab control-label"><i class="glyphicon glyphicon-time"></i>&nbsp;&nbsp;发送间隔：</label>
                    <div class="col-md-10">
                        <input type="text" class="form-control input-sm" id="spaceTime" name="spaceTime" value="${config.spaceTime}">
                        <span class="tips"><b>*&nbsp;</b>同一执行器失联后告警邮件和短信发送后到下一次发送的时间间隔(分钟)</span>
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
