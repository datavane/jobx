<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="cron"  uri="http://www.opencron.org"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/common/resource.jsp"/>

    <style type="text/css">
        .subJobTips {
            width:185px;
            height: 25px;
        }

        .remjob{
            margin-right: 15px;
        }

        .subJobUl li{
            background-color: rgba(0,0,0,0.3);
            border-radius: 4px;
            height: 26px;
            list-style: outside none none;
            margin-top: -27px;
            margin-bottom: 29px;
            margin-left: 100px;
            padding: 4px 15px;
            width: 350px;
        }

        .delSubJob{
            float:right;margin-right:2px
        }
    </style>

    <script type="text/javascript">


        $(document).ready(function(){

            $("#execType0").next().attr("onclick","showCronExp()");
            $("#execType1").next().attr("onclick","hideCronExp()");
            $("#redo01").next().attr("onclick","showCountDiv()");
            $("#redo00").next().attr("onclick","hideCountDiv()");
            $("#redo1").next().attr("onclick","showCountDiv1()");
            $("#redo0").next().attr("onclick","hideCountDiv1()");
            $("#cronType0").next().attr("onclick","changeTips(0)");
            $("#cronType1").next().attr("onclick","changeTips(1)");
            $("#jobType0").next().attr("onclick","subJob(0)");
            $("#jobType1").next().attr("onclick","subJob(1)");
            $("#warning1").next().attr("onclick","showContact()");
            $("#warning0").next().attr("onclick","hideContact()");

            $("#jobName").blur(function(){

                var jobId = $("#jobId").val();
                if (!jobId){
                    alert("页面异常，请刷新重试!");
                    return false;
                }
                var jobName = $("#jobName").val();
                if(!jobName){
                    $("#checkJobName").html("<font color='red'>" + '<i class="glyphicon glyphicon-remove-sign"></i>&nbsp;请填写作业名称' + "</font>");
                    return false;}
                $.ajax({
                    headers:{"csrf":"${csrf}"},
                    type:"POST",
                    url:"${contextPath}/job/checkname",
                    data:{
                        "jobId":jobId,
                        "name":jobName,
                        "agentId":$("#agentId").val()
                    },
                    success:function(data){
                        if (data == "yes"){
                            $("#checkJobName").html("<font color='green'>" + '<i class="glyphicon glyphicon-ok-sign"></i>&nbsp;作业名称可用' + "</font>");
                            return false;
                        }else {
                            $("#checkJobName").html("<font color='red'>" + '<i class="glyphicon glyphicon-remove-sign"></i>&nbsp;作业名称已存在' + "</font>");
                            return false;
                        }
                    },
                    error : function() {
                        alert("网络繁忙请刷新页面重试!");
                        return false;
                    }
                });
            });
            $("#jobName").focus(function(){
                $("#checkJobName").html('<b>*&nbsp;</b>作业名称必填');
            });
            $("#cronExp").focus(function(){
                var cronType = $('input[type="radio"][name="cronType"]:checked').val();
                if (cronType == 0){
                    $("#checkcronExp").html('<b>*&nbsp;</b>请采用unix/linux的时间格式表达式,如 00 01 * * *');
                }else {
                    $("#checkcronExp").html('<b>*&nbsp;</b>请采用quartz框架的时间格式表达式,如 0 0 10 L * ?');
                }
            });
            $("#cronExp").blur(function(){
                var cronType = $('input[type="radio"][name="cronType"]:checked').val();
                var cronExp= $("#cronExp").val();
                if (!cronExp){
                    $("#checkcronExp").html("<font color='red'>" + '<i class="glyphicon glyphicon-remove-sign"></i>&nbsp;请填写时间规则!' + "</font>");
                    return false;
                }
                $.ajax({
                    headers:{"csrf":"${csrf}"},
                    type:"POST",
                    url:"${contextPath}/verify/exp",
                    data:{
                        "cronType":cronType,
                        "cronExp":cronExp
                    },
                    success:function(data){
                        if (data == "success"){
                            $("#checkcronExp").html("<font color='green'>" + '<i class="glyphicon glyphicon-ok-sign"></i>&nbsp;语法正确' + "</font>");
                            return;
                        }else {
                            $("#checkcronExp").html("<font color='red'>" + '<i class="glyphicon glyphicon-remove-sign"></i>&nbsp;语法错误' + "</font>");
                            return;
                        }
                    },
                    error : function() {
                        alert("网络异常，请刷新页面重试!");
                    }
                });
            });

            //子作业拖拽
            $( "#subJobDiv" ).sortable({
                delay: 100
            });

        });

        function showCronExp(){$(".cronExpDiv").show()}
        function hideCronExp(){$(".cronExpDiv").hide()}
        function showCountDiv(){$(".countDiv").show()}
        function hideCountDiv(){$(".countDiv").hide()}
        function showCountDiv1(){$(".countDiv1").show()}
        function hideCountDiv1(){$(".countDiv1").hide()}
        function showContact(){$(".contact").show()}
        function hideContact(){$(".contact").hide()}

        function changeTips(type){
            if (type == "0"){
                $("#checkcronExp").html('<b>*&nbsp;</b>请采用unix/linux的时间格式表达式,如 00 01 * * *');
            }
            if (type == "1"){
                $("#checkcronExp").html('<b>*&nbsp;</b>请采用quartz框架的时间格式表达式,如 0 0 10 L * ?');
            }
        }

        function subJob(flag){
            if (flag=="1"){
                $("#subJob").show();
                $("#runModel").show();

            }else {
                $("#subJob").hide();
                $("#runModel").hide();
            }
        }

        function save(){

            var jobId = $("#jobId").val();
            if (!jobId){
                alert("页面异常，请刷新重试!");
                return false;
            }

            var jobName = $("#jobName").val();
            if (!jobName){
                alert("请填写作业名称!");
                return false;
            }
            if (!$("#agentId").val()){
                alert("页面异常，请刷新重试!");
                return false;
            }
            var execType = $('input[type="radio"][name="execType"]:checked').val();
            var cronType = $('input[type="radio"][name="cronType"]:checked').val();
            var cronExp = $("#cronExp").val();
            if (execType == 0 && !cronExp){
                alert("请填写时间规则!");
                return false;
            }

            if (!$("#command").val()){
                alert("请填写执行命令!");
                return false;
            }

            var timeout = $("#timeout").val();
            if(isNaN(timeout)||parseInt(timeout)<0){
                alert("请填写正确的超时时间")
                return false;
            }

            var redo = $('input[type="radio"][name="redo"]:checked').val();
            if (redo == 1){
                if (!$("#runCount").val()){
                    alert("请填写重跑次数!");
                    return false;
                }
                if(!opencron.testNumber($("#runCount").val())){
                    alert("截止重跑次数必须为正整数!");
                    return false;
                }
            }

            if ($('input[name="jobType"]:checked').val()=="1"){
                if($("#subJobDiv:has(li)").length==0) {
                    alert("当前是流程作业,至少要添加一个子作业!");
                    return false;
                }
            }

            var warning = $('input[type="radio"][name="warning"]:checked').val();
            if (warning == 1){
                var mobiles = $("#mobiles").val();
                if (!mobiles){
                    alert("请填写手机号码!");
                    return false;
                }
                var mobs = mobiles.split(",");
                for (var i in mobs){
                    if(!opencron.testMobile(mobs[i])){
                        alert("请填写正确的手机号码!");
                        return false;
                    }
                }

                var emails = $("#email").val();
                if (!emails){
                    alert("请填写邮箱地址!");
                    return false;
                }

                var emas = emails.split(",");
                for (var i in emas){
                    if(!opencron.testEmail(emas[i])){
                        alert("请填写正确的邮箱地址!");
                        return false;
                    }
                }
            }

            $.ajax({
                headers:{"csrf":"${csrf}"},
                type:"POST",
                url:"${contextPath}/job/checkname",
                data:{
                    "jobId":jobId,
                    "name":jobName,
                    "agentId":$("#agentId").val()
                },
                success:function(data){
                    if (data == "yes"){
                        if (execType == 0 && cronExp){
                            $.ajax({
                                headers:{"csrf":"${csrf}"},
                                type:"POST",
                                url:"${contextPath}/verify/exp",
                                data:{
                                    "cronType":cronType,
                                    "cronExp":cronExp
                                },
                                success:function(data){
                                    if (data == "success"){
                                        codeCommand();
                                        $("#job").submit();
                                        return false;
                                    }else {
                                        alert("时间规则语法错误!");
                                        return false;
                                    }
                                },
                                error : function() {
                                    alert("网络异常，请刷新页面重试!");
                                }
                            });
                            return false;
                        }else {
                            codeCommand();
                            $("#job").submit();
                            return false;
                        }
                        return false;
                    }else {
                        alert("作业名称已存在!");
                        return false;
                    }
                },
                error : function() {
                    alert("网络繁忙请刷新页面重试!");
                    return false;
                }
            });

        }

        function codeCommand() {
            $("#command").val(encode($("#command").val()));
        }

        function addSubJob(){
            $("#subForm")[0].reset();
            $("#subTitle").html("添加子作业").attr("action","add");
        }

        function closeSubJob(){
            $("#subForm")[0].reset();
            $('#jobModal').modal('hide');
        }

        function saveSubJob() {

            var jobName = $("#jobName1").val();
            if (!jobName){
                alert("请填写作业名称!");
                return false;
            }

            if (!$("#agentId1").val()){
                alert("页面异常，请刷新重试!");
                return false;
            }

            if (!$("#command1").val()){
                alert("请填写执行命令!");
                return false;
            }

            var timeout = $("#timeout1").val();
            if(isNaN(timeout)||parseInt(timeout)<0){
                alert("请填写正确的超时时间")
                return false;
            }

            var redo = $('input[type="radio"][name="redo1"]:checked').val();
            if (redo == 1){
                if (!$("#runCount1").val()){
                    alert("请填写重跑次数!");
                    return false;
                }
                if(!opencron.testNumber($("#runCount1").val())){
                    alert("截止重跑次数必须为正整数!");
                    return false;
                }
            }

            $.ajax({
                headers:{"csrf":"${csrf}"},
                type:"POST",
                url:"${contextPath}/job/checkname",
                data:{
                    "jobId":$("#jobId1").val(),
                    "name":jobName,
                    "agentId":$("#agentId1").val()
                },

                success:function(data) {
                    if (data == "no"){
                        alert("作业名称已存在!");
                        return false;
                    }else {
                        //添加
                        if ( $("#subTitle").attr("action") === "add" ) {
                            var id = new Date().getTime();
                            var li = '<li id="' + id + '">' +
                                '<span  onclick="showSubJob(' + id + ')">' +
                                '   <a data-toggle="modal" href="#jobModal" title="编辑"><i class="glyphicon glyphicon-pencil"></i>&nbsp;&nbsp;<span id="name_' + id + '">' + escapeHtml($("#jobName1").val()) + '</span></a>' +
                                '</span>' +
                                '<span class="delSubJob" onclick="removeSubJob(this)">' +
                                '   <a href="javascript:void(0)" title="删除"><i class="glyphicon glyphicon-trash"></i></a>' +
                                '</span>' +
                                "<input type='hidden' name='child.jobId' value=''>"+
                                '<input type="hidden" name="child.jobName" value="' + escapeHtml($("#jobName1").val()) + '">' +
                                '<input type="hidden" name="child.agentId" value="' + $("#agentId1").val() + '">' +
                                '<input type="hidden" name="child.redo" value="'+$("input[type='radio'][name='redo1']:checked").val()+'">'+
                                '<input type="hidden" name="child.runCount" value="' + $("#runCount1").val() + '">' +
                                '<input type="hidden" name="child.command" value="' + encode($("#command1").val()) + '">' +
                                '<input type="hidden" name="child.timeout" value="' + $("#timeout1").val() + '">' +
                                '<input type="hidden" name="child.comment" value="' + escapeHtml($("#comment1").val()) + '">' +
                            '</li>';
                            $("#subJobDiv").append(li);
                        }else if ( $("#subTitle").attr("action") === "edit" ) {//编辑
                            var id = $("#subTitle").attr("tid");
                            $("#"+id).find("input").each(function(i,elem){
                                elem = $(elem);
                                var inputName = elem.attr("name").replace("child.","");
                                elem.val($("#"+inputName+"1").val());

                                if (elem.attr("name") == "child.jobName1") {
                                    elem.val(unEscapeHtml($("#jobName").val()));
                                }

                                if (elem.attr("name") == "child.comment") {
                                    elem.val(unEscapeHtml($("#comment1").val()));
                                }

                                if (elem.attr("name") == "child.command") {
                                    elem.val(encode($("#command1").val()));
                                }
                            });

                            $("#name_"+id).html(escapeHtml(jobName));
                        }
                        closeSubJob();
                    }
                },
                error : function() {
                    alert("网络繁忙请刷新页面重试!");
                    return false;
                }
            });

        }

        function showSubJob(id){
            $("#subTitle").html("编辑子作业").attr("action","edit").attr("tid",id);
            $("#"+id).find("input").each(function(index,elem) {
                elem = $(elem);
                var inputName = elem.attr("name").replace("child.","");
                $("#"+inputName+"1").val(elem.val());
                if ($(elem).attr("name") == "child.redo"){
                    if($(elem).val() == "1"){
                        $(".countDiv1").show();
                        $("#redo1").prop("checked",true);
                        $("#redo1").parent().removeClass("checked").addClass("checked");
                        $("#redo1").parent().attr("aria-checked",true);
                        $("#redo1").parent().prop("onclick","showContact()");
                        $("#redo0").parent().removeClass("checked");
                        $("#redo0").parent().attr("aria-checked",false);
                    }else {
                        $(".countDiv1").hide();
                        $("#redo0").prop("checked",true);
                        $("#redo0").parent().removeClass("checked").addClass("checked");
                        $("#redo0").parent().attr("aria-checked",true);
                        $("#redo1").parent().removeClass("checked");
                        $("#redo1").parent().attr("aria-checked",false);
                    }
                }
                if ($(elem).attr("name") == "child.command") {
                    try {
                        $("#command1").val(decode(elem.val()));
                    }catch (e) {
                    }
                }

            });
        }

        function  removeSubJob(node){
            swal({
                title: "",
                text: "您确定要删除这个子作业?",
                type: "warning",
                showCancelButton: true,
                closeOnConfirm: true,
                confirmButtonText: "删除"
            }, function () {
                $(node).parent().slideUp(300,function(){this.remove()});
            });
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
        <li><a href="">作业管理</a></li>
    </ol>
    <h4 class="page-title">
        <i class="fa fa-edit" aria-hidden="true"></i>
        编辑作业
    </h4>


    <div style="float: right;margin-top: 5px">
        <a onclick="goback();" class="btn btn-sm m-t-10" style="margin-right: 16px;margin-bottom: -4px"><i class="fa fa-mail-reply" aria-hidden="true"></i>&nbsp;返回</a>
    </div>

    <div class="block-area" id="basic">
        <div class="tile p-15">
            <form class="form-horizontal" role="form" id="job" action="${contextPath}/job/save" method="post"><br>
                <input type="hidden" name="csrf" value="${csrf}">
                <input type="hidden" id="jobId" name="jobId" value="${job.jobId}">
                <input type="hidden" name="userId" value="${job.userId}">
                <input type="hidden" id="agentId" name="agentId" class="input-self" value="${job.agentId}">

                <div class="form-group">
                    <label for="jobName" class="col-lab control-label"><i class="glyphicon glyphicon-tasks"></i>&nbsp;&nbsp;作业名称：</label>
                    <div class="col-md-10">
                        <input type="text" class="form-control input-sm" id="jobName" name="jobName" value="${job.jobName}">
                        <span class="tips" id="checkJobName"><b>*&nbsp;</b>作业名称必填</span>
                    </div>
                </div><br>

                <div class="form-group">
                    <label for="agentId" class="col-lab control-label"><i class="glyphicon glyphicon-leaf"></i>&nbsp;&nbsp;执&nbsp;&nbsp;行&nbsp;&nbsp;器：</label>
                    <div class="col-md-10">
                        <input type="text" class="form-control input-sm" value="${job.agentName}&nbsp;&nbsp;&nbsp;${job.ip}" readonly>
                        <font color="red">&nbsp;*只读</font>
                        <span class="tips">&nbsp;&nbsp;要执行此作业的机器名称和IP地址</span>
                    </div>
                </div><br>

                <div class="form-group">
                    <label class="col-lab control-label"><i class="glyphicon glyphicon-info-sign"></i>&nbsp;&nbsp;运行模式：</label>
                    <div class="col-md-10">
                        <label onclick="showCronExp()" for="execType0" class="radio-label"><input type="radio" name="execType" id="execType0" value="0" ${job.execType eq 0 ? 'checked' : ''}>自动&nbsp;&nbsp;&nbsp;</label>
                        <label onclick="hideCronExp()" for="execType1" class="radio-label"><input type="radio" name="execType" id="execType1" value="1" ${job.execType eq 1 ? 'checked' : ''}>手动</label>&nbsp;&nbsp;&nbsp;
                        </br><span class="tips"><b>*&nbsp;</b>自动模式: 执行器自动执行&nbsp;手动模式: 管理员手动执行</span>
                    </div>
                </div><br>

                <div class="form-group cronExpDiv"  style="display: ${job.execType eq 0 ? 'block' : 'none'}">
                    <label class="col-lab control-label"><i class="glyphicon glyphicon-bookmark"></i>&nbsp;&nbsp;规则类型：</label>
                    <div class="col-md-10">
                        <label onclick="changeTips(0)" for="cronType0" class="radio-label"><input type="radio" name="cronType" value="0" id="cronType0" ${job.cronType eq 0 ? 'checked' : ''}>crontab&nbsp;&nbsp;&nbsp;</label>
                        <label onclick="changeTips(1)" for="cronType1" class="radio-label"><input type="radio" name="cronType" value="1" id="cronType1" ${job.cronType eq 1 ? 'checked' : ''}>quartz</label>&nbsp;&nbsp;&nbsp;
                        </br><span class="tips"><b>*&nbsp;</b>1.crontab: unix/linux的时间格式表达式&nbsp;&nbsp;2.quartz: quartz框架的时间格式表达式</span>
                    </div>
                </div><br>

                <div class="form-group cronExpDiv"  style="display: ${job.execType eq 0 ? 'block' : 'none'}">
                    <label for="cronExp" class="col-lab control-label"><i class="glyphicon glyphicon-filter"></i>&nbsp;&nbsp;时间规则：</label>
                    <div class="col-md-10">
                        <input type="text" class="form-control input-sm" id="cronExp" name="cronExp" value="${job.cronExp}">
                        <span class="tips" id="checkcronExp">
                            <c:if test="${job.cronType eq 0}"><b>*&nbsp;</b>请采用unix/linux的时间格式表达式</c:if>
                            <c:if test="${job.cronType eq 1}"><b>*&nbsp;</b>请采用quartz框架的时间格式表达式</c:if>
                        </span>
                    </div>
                </div><br>

                <div class="form-group">
                    <label for="command" class="col-lab control-label"><i class="glyphicon glyphicon-th-large"></i>&nbsp;&nbsp;执行命令：</label>
                    <div class="col-md-10">
                        <textarea class="form-control input-sm" id="command" name="command" style="height:80px;">${job.command}</textarea>
                        <span class="tips"><b>*&nbsp;</b>请采用unix/linux的shell支持的命令</span>
                    </div>
                </div><br>

                <div class="form-group">
                    <label class="col-lab control-label"><i class="glyphicon  glyphicon glyphicon-forward"></i>&nbsp;&nbsp;重新执行：</label>
                    <div class="col-md-10">
                        <label onclick="showCountDiv()" for="redo01" class="radio-label"><input type="radio" name="redo" value="1" id="redo01" ${job.redo eq 1 ? 'checked' : ''}>是&nbsp;&nbsp;&nbsp;</label>
                        <label onclick="hideCountDiv()" for="redo00" class="radio-label"><input type="radio" name="redo" value="0" id="redo00" ${job.redo eq 0 ? 'checked' : ''}>否</label>&nbsp;&nbsp;&nbsp;
                        <br><span class="tips"><b>*&nbsp;</b>执行失败时是否自动重新执行</span>
                    </div>
                </div><br>

                <div class="form-group countDiv" style="display: ${job.redo eq 1 ? 'block' : 'none'}">
                    <label for="runCount" class="col-lab control-label"><i class="glyphicon glyphicon-repeat"></i>&nbsp;&nbsp;重跑次数：</label>
                    <div class="col-md-10">
                        <input type="text" class="form-control input-sm" id="runCount" name="runCount" value="${job.runCount}">
                        <span class="tips"><b>*&nbsp;</b>执行失败时自动重新执行的截止次数</span>
                    </div>
                </div><br>

                <div class="form-group">
                    <label for="timeout" class="col-lab control-label"><i class="glyphicon glyphicon-ban-circle"></i>&nbsp;&nbsp;超时时间：</label>
                    <div class="col-md-10">
                        <input type="text" class="form-control input-sm" id="timeout" name="timeout" value="${job.timeout}">
                        <span class="tips"><b>*&nbsp;</b>执行作业允许的最大时间,超过则为超时(0:忽略超时时间,分钟为单位)</span>
                    </div>
                </div><br>

                <div class="form-group">
                    <label class="col-lab control-label"><i class="glyphicon  glyphicon-random"></i>&nbsp;&nbsp;作业类型：</label>
                    <div class="col-md-10">
                        <label onclick="subJob(0)" for="jobType0" class="radio-label"><input type="radio" name="jobType" value="0" id="jobType0" ${job.jobType eq 0 ? 'checked' : ''}>单一作业&nbsp;&nbsp;&nbsp;</label>
                        <label onclick="subJob(1)" for="jobType1" class="radio-label"><input type="radio" name="jobType" value="1" id="jobType1" ${job.jobType eq 1 ? 'checked' : ''}>流程作业</label>&nbsp;&nbsp;&nbsp;
                        <br><span class="tips"><b>*&nbsp;</b>单作业: 单一作业&nbsp;流程作业: 多个子作业组成作业</span>
                    </div>
                </div><br>

                <div class="form-group" id="subJob">
                    <span>
                        <label class="col-lab control-label"><i class="glyphicon glyphicon-tag"></i>&nbsp;&nbsp;子&nbsp;&nbsp;作&nbsp;&nbsp;业：</label>
                        <div class="col-md-10">
                            <a data-toggle="modal" href="#jobModal" onclick="addSubJob();" class="btn btn-sm m-t-10">添加子作业</a>
                            <ul id="subJobDiv" class="subJobUl">
                            <c:forEach var="c" items="${job.children}">
                                <li id="${c.jobId}" >
                                    <span  onclick="showSubJob('${c.jobId}')">
                                        <a data-toggle="modal" href="#jobModal" title="编辑"><i class="glyphicon glyphicon-pencil"></i>&nbsp;&nbsp;<span id="name_${c.jobId}">${c.jobName}</span></a>
                                    </span>
                                    <span class='delSubJob' onclick='removeSubJob(this)'>
                                        <a href='javascript:void(0)' title='删除'><i class='glyphicon glyphicon-trash'></i></a>
                                    </span>
                                    <input type="hidden" name="child.jobId" value="${c.jobId}">
                                    <input type="hidden" name="child.jobName" value="${c.jobName}">
                                    <input type="hidden" name="child.agentId" value="${c.agentId}">
                                    <input type="hidden" name="child.redo" value="${c.redo}">
                                    <input type="hidden" name="child.runCount" value="${c.runCount}">
                                    <input type="hidden" name="child.command" value="${cron:toBase64(c.command)}">
                                    <input type="hidden" name="child.timeout" value="${c.timeout}">
                                    <input type="hidden" name="child.comment" value="${c.comment}">
                                </li>
                            </c:forEach>
                        </div>
                    </span>
                </div><br>

                <div class="form-group" id="runModel">
                    <label class="col-lab control-label"><i class="glyphicon  glyphicon-sort-by-attributes"></i>&nbsp;&nbsp;运行顺序</label>
                    <div class="col-md-10">
                        <label for="runModel0" class="radio-label" style="margin-left: 14px;"><input type="radio" name="runModel" value="0" id="runModel0" ${job.runModel eq 0 ? 'checked' : ''}>串行&nbsp;&nbsp;&nbsp;</label>
                        <label for="runModel1" class="radio-label"><input type="radio" name="runModel" value="1" id="runModel1" ${job.runModel eq 1 ? 'checked' : ''}>并行</label>&nbsp;&nbsp;&nbsp;
                        <br><span class="tips"><b>*&nbsp;</b>串行: 按顺序依次执行&nbsp;并行: 同时执行</span>
                    </div>
                </div><br>

                <div class="form-group">
                    <label class="col-lab control-label"><i class="glyphicon glyphicon-warning-sign"></i>&nbsp;&nbsp;失败报警：</label>&nbsp;&nbsp;&nbsp;
                    <div class="col-md-10">
                        <label  onclick="showContact()" for="warning1" class="radio-label"><input type="radio" name="warning" value="1" id="warning1" ${job.warning eq true ? 'checked' : ''}>是&nbsp;&nbsp;&nbsp;</label>
                        <label  onclick="hideContact()" for="warning0" class="radio-label"><input type="radio" name="warning" value="0" id="warning0" ${job.warning eq false ? 'checked' : ''}>否</label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        </br><span class="tips"><b>*&nbsp;</b>任务执行失败时是否发信息报警</span>
                    </div>
                </div><br>

                <div class="form-group contact" style="display: ${job.warning eq true ? 'block' : 'none'}">
                    <label for="mobiles" class="col-lab control-label"><i class="glyphicon glyphicon-comment"></i>&nbsp;&nbsp;报警手机：</label>
                    <div class="col-md-10">
                        <input type="text" class="form-control input-sm" id="mobiles" name="mobiles" value="${job.mobiles}">
                        <span class="tips"><b>*&nbsp;</b>任务执行失败时将发送短信给此手机,多个请以逗号(英文)隔开</span>
                    </div>
                </div><br>

                <div class="form-group contact" style="display: ${job.warning eq true ? 'block' : 'none'}">
                    <label for="email" class="col-lab control-label"><i class="glyphicon glyphicon-envelope"></i>&nbsp;&nbsp;报警邮箱：</label>
                    <div class="col-md-10">
                        <input type="text" class="form-control input-sm" id="email" name="emailAddress" value="${job.emailAddress}">
                        <span class="tips"><b>*&nbsp;</b>任务执行失败时将发送报告给此邮箱,多个请以逗号(英文)隔开</span>
                    </div>
                </div><br>

                <div class="form-group">
                    <label for="comment" class="col-lab control-label"><i class="glyphicon glyphicon-magnet"></i>&nbsp;&nbsp;描&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;述：</label>
                    <div class="col-md-10">
                        <textarea class="form-control input-sm" id="comment" name="comment" style="height: 80px;">${job.comment}</textarea>
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

    <%--添加流程作业弹窗--%>
    <div class="modal fade" id="jobModal" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4 id="subTitle" action="add" tid="" >添加子作业</h4>
                </div>
                <div class="modal-body">
                    <form class="form-horizontal" role="form" id="subForm"><br>

                        <input type="hidden" id="jobId1"/>&nbsp;

                        <div class="form-group">
                            <label for="agentId1" class="col-lab control-label" title="执行器名称和IP地址">执&nbsp;&nbsp;行&nbsp;&nbsp;器：</label>
                            <div class="col-md-9">
                                <select id="agentId1" name="agentId1" class="form-control m-b-10 ">
                                    <c:forEach var="d" items="${agents}">
                                        <option value="${d.agentId}">${d.ip}&nbsp;(${d.name})</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="jobName1" class="col-lab control-label" title="作业名称必填">作业名称：</label>
                            <div class="col-md-9">
                                <input type="text" class="form-control " id="jobName1">&nbsp;&nbsp;<label id="checkJobName1"></label>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="command1" class="col-lab control-label" title="请采用unix/linux的shell支持的命令">执行命令：</label>
                            <div class="col-md-9">
                                <textarea style="height:80px;" class="form-control " id="command1"></textarea>&nbsp;
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="timeout1" class="col-lab control-label">超时时间：</label>
                            <div class="col-md-9">
                                <input type="text" class="form-control" id="timeout1" value="0">
                                <span class="tips"><b>*&nbsp;</b>执行作业允许的最大时间,超过则为超时(0:忽略超时时间,分钟为单位)</span>
                            </div>
                        </div><br>

                        <div class="form-group">
                            <label class="col-lab control-label" title="执行失败时是否自动重新执行">重新执行：</label>&nbsp;&nbsp;
                            <label onclick="showCountDiv1()" for="redo1" class="radio-label"><input type="radio" name="redo1" value="1" id="redo1"> 是&nbsp;&nbsp;&nbsp;</label>
                            <label onclick="hideCountDiv1()" for="redo0" class="radio-label"><input type="radio" name="redo1" value="0" id="redo0" checked> 否</label><br>
                        </div><br>
                        <div class="form-group countDiv1" style="display: none">
                            <label for="runCount1" class="col-lab control-label" title="执行失败时自动重新执行的截止次数">重跑次数：</label>
                            <div class="col-md-9">
                                <input type="text" class="form-control " id="runCount1"/>&nbsp;
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="comment1" class="col-lab control-label" title="此作业内容的描述">描&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;述：</label>
                            <div class="col-md-9">
                                <textarea style="height: 50px;" name="comment1" id="comment1" class="form-control"></textarea>&nbsp;
                            </div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <center>
                        <button type="button" class="btn btn-sm"  onclick="saveSubJob()">保存</button>&nbsp;&nbsp;
                        <button type="button" class="btn btn-sm"  data-dismiss="modal">关闭</button>
                    </center>
                </div>
            </div>
        </div>
    </div>

</section>
<br/><br/>

<jsp:include page="/WEB-INF/common/footer.jsp"/>
