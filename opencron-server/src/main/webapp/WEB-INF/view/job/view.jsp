<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="cron" uri="http://www.opencron.org" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="en">
<head>

    <style type="text/css">
        .none {
            text-align: center;
        }
    </style>

    <script type="text/javascript">

        var toggle = {
            cronexp:{
                show:function () {
                    $(".cronExpDiv").show();
                },
                hide:function () {
                    $(".cronExpDiv").hide()
                }
            },
            count:{
                show:function () {
                    $(".countDiv").show();
                },
                hide:function () {
                    $(".countDiv").hide();
                }
            },
            contact:{
                show:function () {
                    $(".contact").show()
                },
                hide:function () {
                    $(".contact").hide()
                }
            }
        };

        function editSingle(id) {
            ajax({
                headers: {"csrf": "${csrf}"},
                type: "POST",
                url: "${contextPath}/job/editsingle.do",
                data: {"id": id}
            },function (obj) {
                $("#jobform")[0].reset();
                if (obj != null) {
                    $("#checkJobName").html("");
                    $("#checkcronExp").html("");
                    $("#id").val(obj.jobId);
                    $("#magentId").val(obj.agentId);
                    $("#jobName").val(unEscapeHtml(obj.jobName));
                    $("#agent").val(obj.agentName + "   " + obj.host);
                    $("#cronExp").val(obj.cronExp);
                    $("#cmd").val(obj.command);
                    if (obj.execType == 1) {
                        toggle.cronexp.hide();
                        $("#execType1").prop("checked", true);
                        $("#execType1").parent().removeClass("checked").addClass("checked");
                        $("#execType1").parent().attr("aria-checked", true);
                        $("#execType0").parent().removeClass("checked");
                        $("#execType0").parent().attr("aria-checked", false);
                    } else {
                        toggle.cronexp.show();
                        $("#execType0").prop("checked", true);
                        $("#execType0").parent().removeClass("checked").addClass("checked");
                        $("#execType0").parent().attr("aria-checked", true);
                        $("#execType1").parent().removeClass("checked");
                        $("#execType1").parent().attr("aria-checked", false);
                    }
                    if (obj.cronType == 1) {
                        $("#cronType1").prop("checked", true);
                        $("#cronType1").parent().removeClass("checked").addClass("checked");
                        $("#cronType1").parent().attr("aria-checked", true);
                        $("#cronType0").parent().removeClass("checked");
                        $("#cronType0").parent().attr("aria-checked", false);
                    } else {
                        $("#cronType0").prop("checked", true);
                        $("#cronType0").parent().removeClass("checked").addClass("checked");
                        $("#cronType0").parent().attr("aria-checked", true);
                        $("#cronType1").parent().removeClass("checked");
                        $("#cronType1").parent().attr("aria-checked", false);
                    }
                    if (obj.redo == 1) {
                        toggle.count.show();
                        $("#redo1").prop("checked", true);
                        $("#redo1").parent().removeClass("checked").addClass("checked");
                        $("#redo1").parent().attr("aria-checked", true);
                        $("#redo0").parent().removeClass("checked");
                        $("#redo0").parent().attr("aria-checked", false);
                    } else {
                        toggle.count.hide();
                        $("#redo0").prop("checked", true);
                        $("#redo0").parent().removeClass("checked").addClass("checked");
                        $("#redo0").parent().attr("aria-checked", true);
                        $("#redo1").parent().removeClass("checked");
                        $("#redo1").parent().attr("aria-checked", false);
                    }
                    $("#runCount").val(obj.runCount);
                    if (obj.warning == true) {
                        toggle.contact.show();
                        $("#warning1").prop("checked", true);
                        $("#warning1").parent().removeClass("checked").addClass("checked");
                        $("#warning1").parent().attr("aria-checked", true);
                        $("#warning1").parent().bind("click",toggle.contact.show);
                        $("#warning0").parent().removeClass("checked");
                        $("#warning0").parent().attr("aria-checked", false);
                    } else {
                        toggle.contact.hide();
                        $("#warning0").prop("checked", true);
                        $("#warning0").parent().removeClass("checked").addClass("checked");
                        $("#warning0").parent().attr("aria-checked", true);
                        $("#warning1").parent().removeClass("checked");
                        $("#warning1").parent().attr("aria-checked", false);
                    }
                    $("#mobiles").val(obj.mobiles);
                    $("#email").val(obj.emailAddress);
                    $("#comment").val(unEscapeHtml(obj.comment));
                    $("#runAs").val(obj.runAs);
                    $("#successExit").val(obj.successExit);
                    $("#timeout").val(obj.timeout);
                    $('#jobModal').modal('show');
                    return;
                }
            });
        }

        function save() {
            var jobName = $("#jobName").val();
            if (!jobName) {
                opencron.tipError($("#checkJobName"),"请填写作业名称!");
                return false;
            }
            var jobId = $("#id").val();
            if (!jobId) {
                alert("页面异常，请刷新重试!");
                return false;
            }

            var agentId = $("#magentId").val();
            if (!agentId) {
                alert("页面异常，请刷新重试!");
                return false;
            }

            var execType = $('input[type="radio"][name="execType"]:checked').val();
            if (!execType) {
                alert("页面错误，请刷新重试!");
                return false;
            }
            var cronType = $('input[type="radio"][name="cronType"]:checked').val();
            if (!cronType) {
                alert("页面错误，请刷新重试!");
                return false;
            }
            var cronExp = $("#cronExp").val();
            if (execType == 0) {
                if (!cronExp) {
                    opencron.tipError($("#checkcronExp"),"请填写时间规则!");
                    return false;
                }
            }
            var command = $("#cmd").val();
            if (!command) {
                alert("执行命令不能为空!");
                return false;
            }

            var successExit = $("#successExit").val();
            if( successExit!=null && isNaN(successExit)) {
                opencron.tipError($("#checkcronExp"),"请填写时间规则!");
                alert("自定义成功标识必须为数字");
                return false;
            }

            var timeout = $("#timeout").val();
            if (isNaN(timeout) || parseInt(timeout) < 0) {
                alert("请填写正确的超时时间")
                return false;
            }

            var redo = $('input[type="radio"][name="redo"]:checked').val();
            var runCount = $("#runCount").val();
            if (!redo) {
                alert("页面错误，请刷新重试!");
                return false;
            }
            if (redo == 1) {
                if (!runCount) {
                    alert("请填写重跑次数!");
                    return false;
                }
                if (!opencron.testNumber(runCount)) {
                    alert("截止重跑次数必须为正整数!");
                    return false;
                }
            }

            var warning = $('input[type="radio"][name="warning"]:checked').val();
            if (warning == 1) {
                var mobiles = $("#mobiles").val();
                if (!mobiles) {
                    alert("请填写手机号码!");
                    return false;
                }
                var mobs = mobiles.split(",");
                for (var i in mobs) {
                    if (!opencron.testMobile(mobs[i])) {
                        alert("请填写正确的手机号码!");
                        return false;
                    }
                }

                var emails = $("#email").val();
                if (!emails) {
                    alert("请填写邮箱地址!");
                    return false;
                }

                var emas = emails.split(",");
                for (var i in emas) {
                    if (!opencron.testEmail(emas[i])) {
                        alert("请填写正确的邮箱地址!");
                        return false;
                    }
                }
            }

            var jobObj = {
                "id": jobId,
                "name": jobName,
                "jobId": jobId,
                "cronType": cronType,
                "cronExp": cronExp,
                "agentId": agentId,
                "command": toBase64(command),
                "runAs":$("#runAs").val(),
                "successExit":$("#successExit").val(),
                "timeout": timeout,
                "execType": execType,
                "jobName": jobName,
                "redo": redo,
                "runCount": runCount,
                "warning": warning,
                "mobiles": mobiles,
                "emailAddress": emails,
                "comment": $("#comment").val()
            };

            //手动....
            if (execType == 1) {
                doSave(jobObj);
            } else {//需要验证时间规则...
                ajax({
                    headers: {"csrf": "${csrf}"},
                    type: "POST",
                    url: "${contextPath}/verify/exp.do",
                    data: {
                        "cronType": cronType,
                        "cronExp": cronExp
                    }
                },function (data) {
                    if (data) {
                        doSave(jobObj);
                    } else {
                        alert("时间规则语法错误!");
                        return false;
                    }
                });
            }

        }

        function doSave(job) {
            ajax({
                headers: {"csrf": "${csrf}"},
                type: "POST",
                url: "${contextPath}/job/checkname.do",
                data: {
                    "id": job.jobId,
                    "name": job.jobName
                }
            },function (result) {
                if (result) {
                    ajax({
                        headers: {"csrf": "${csrf}"},
                        type: "POST",
                        url: "${contextPath}/job/edit.do",
                        data: {
                            "jobId": job.jobId,
                            "cronType": job.cronType,
                            "cronExp": job.cronExp,
                            "agentId": job.agentId,
                            "command": job.command,
                            "runAs": job.runAs,
                            "successExit": job.successExit,
                            "execType": job.execType,
                            "jobName": job.jobName,
                            "redo": job.redo,
                            "runCount": job.runCount,
                            "warning": job.warning,
                            "mobiles": job.mobiles,
                            "emailAddress": job.emailAddress,
                            "comment": job.comment,
                            "timeout": job.timeout
                        }
                    },function (data) {
                        if (data) {
                            $('#jobModal').modal('hide');
                            alertMsg("修改成功");

                            $("#jobName_" + job.jobId).html(escapeHtml(job.jobName));
                            $("#command_" + job.jobId).html(escapeHtml(passBase64(job.command)));

                            if (job.execType == "0") {
                                $("#execType_" + job.jobId).html('<span class="text-center" color="green">自动</span>');
                                $("#cronType_" + job.jobId).html(job.cronType == "0" ? '<img class="text-center" width="70px" src="${contextPath}/static/img/crontab_ico.png">' : '<img  class="text-center" width="70px" src="${contextPath}/static/img/quartz_ico.png">');
                                $("#cronExp_" + job.jobId).html(escapeHtml(job.cronExp));
                            } else {
                                $("#execType_" + job.jobId).html('<span class="text-center" color="red">手动</span>');
                                $("#cronType_" + job.jobId).html('<div class="none text-center">--</div>');
                                $("#cronExp_" + job.jobId).html('<div class="none text-center">--</div>');
                            }

                            if (job.redo == "0") {
                                $("#redo_" + job.jobId).html('<span color="green">否</span>');
                            } else {
                                $("#redo_" + job.jobId).html('<span color="red">是</span>');
                            }
                            $("#runCount_" + job.jobId).html(job.runCount);
                        } else {
                            alert("修改失败");
                        }
                        return false;
                    });
                } else {
                    alert("作业名已存在!");
                    return false;
                }
            });
        }

        $(document).ready(function () {
            $("#execType0").next().bind("click",toggle.cronexp.show);
            $("#execType1").next().bind("click",toggle.cronexp.hide);
            $("#redo1").next().bind("click",toggle.count.show);
            $("#redo0").next().bind("click",toggle.count.hide);
            $("#warning1").next().bind("click",toggle.contact.show);
            $("#warning0").next().bind("click",toggle.contact.hide);

            $("#size").change(function () {
                doUrl();
            });
            $("#agentId").change(function () {
                doUrl();
            });
            $("#cronType").change(function () {
                doUrl();
            });
            $("#jobType").change(function () {
                doUrl();
            });
            $("#execType").change(function () {
                doUrl();
            });
            $("#redo").change(function () {
                doUrl();
            });
            $("#jobName").focus(function () {
                $("#checkJobName").html("");
            });
            $("#cronExp").focus(function () {
                $("#checkcronExp").html("");
            });

            $("#jobName").blur(function () {
                if (!$("#jobName").val()) {
                    opencron.tipError($("#checkJobName"),"请填写作业名称");
                    return false;
                }
                ajax({
                    headers: {"csrf": "${csrf}"},
                    type: "POST",
                    url: "${contextPath}/job/checkname.do",
                    data: {
                        "id": $("#id").val(),
                        "name": $("#jobName").val()
                    }
                },function (data) {
                    if (data) {
                        opencron.tipOk($("#checkJobName"),"作业名称可用");
                    } else {
                        opencron.tipError($("#checkJobName"),"作业名称已存在");
                    }
                    return false;
                });
            });

            $("#cronExp").blur(function () {
                var cronType = $('input[type="radio"][name="cronType"]:checked').val();
                if (!cronType) {
                    alert("页面错误，请刷新重试!");
                    return false;
                }
                var cronExp = $("#cronExp").val();
                if (!cronExp) {
                    opencron.tipError($("#checkcronExp"),"时间规则不能为空,请填写时间规则");
                    return false;
                }
                ajax({
                    headers: {"csrf": "${csrf}"},
                    type: "POST",
                    url: "${contextPath}/verify/exp.do",
                    data: {
                        "cronType": cronType,
                        "cronExp": cronExp
                    }
                },function (data) {
                    if (data) {
                        opencron.tipOk($("#checkcronExp"),"时间规则格式正确");
                    } else {
                        opencron.tipOk($("#checkcronExp"),"时间规则格式错误,请填写正确的时间规则");
                    }
                    return;
                });
            });

        });

        function doUrl() {
            var pageSize = $("#size").val();
            var agentId = $("#agentId").val();
            var cronType = $("#cronType").val();
            var jobType = $("#jobType").val();
            var execType = $("#execType").val();
            var redo = $("#redo").val();
            window.location.href = "${contextPath}/job/view.htm?agentId=" + agentId + "&cronType=" + cronType + "&jobType=" + jobType + "&execType=" + execType + "&redo=" + redo + "&pageSize=" + pageSize + "&csrf=${csrf}";
        }

        function pauseJob(id,status) {
            var msg = status?"暂停":"恢复";
            swal({
                title: "",
                text: "您确定要"+msg+"这个作业吗？",
                type: "warning",
                showCancelButton: true,
                closeOnConfirm: false,
                confirmButtonText: msg
            }, function () {
                ajax({
                    headers: {"csrf": "${csrf}"},
                    type: "POST",
                    url: "${contextPath}/job/pause.do",
                    data: {
                        "jobId": id,
                        "pause":status
                    }
                },function (data) {
                    var pauseElem = $("#pause_"+id);
                    if(data) {
                        if (status){
                            pauseElem.attr("title","恢复");
                            pauseElem.click(function () {
                                pauseJob(id,false);
                            });
                            pauseElem.find("i").removeClass("fa-pause-circle-o").addClass("fa-history");
                        }else {
                            pauseElem.attr("title","暂停");
                            pauseElem.click(function () {
                                pauseJob(id,true);
                            });
                            pauseElem.find("i").addClass("fa-pause-circle-o").removeClass("fa-history");
                        }
                        alertMsg(msg+"成功!");
                    }
                })
            });
        }

        function executeJob(id) {
            ajax({
                headers: {"csrf": "${csrf}"},
                type: "POST",
                url: "${contextPath}/job/canrun.do",
                data: {"id": id}
            },function (data) {
                if (!data) {
                    swal({
                        title: "",
                        text: "您确定要执行这个作业吗？",
                        type: "warning",
                        showCancelButton: true,
                        closeOnConfirm: false,
                        confirmButtonText: "执行"
                    }, function () {
                        ajax({
                            headers: {"csrf": "${csrf}"},
                            type: "POST",
                            url: "${contextPath}/job/execute.do",
                            data: {"id": id}
                        });
                        alertMsg("该作业已启动,正在执行中.");
                    });
                } else {
                    alert("当前作业已在运行中,不能重复执行!");
                }
            });
        }

        function showChild(id, flowId) {
            var open = $("#job_" + id).attr("childOpen");
            if (open == "off") {
                $("#icon" + id).removeClass("fa-angle-double-down").addClass("fa-angle-double-up");
                $(".child" + id).show();
                $(".trGroup" + flowId).css("background-color", "rgba(0,0,0,0.1)");
                $("#job_" + id).attr("childOpen", "on");
                $(".name_" + id + "_1").hide();
                $(".name_" + id + "_2").show();
            } else {
                $(".trGroup" + flowId).css("background-color", "");
                $("#icon" + id).removeClass("fa-angle-double-up").addClass("fa-angle-double-down");
                $(".child" + id).hide();
                $("#job_" + id).attr("childOpen", "off");
                $(".name_" + id + "_2").hide();
                $(".name_" + id + "_1").show();
            }
        }

        function editCmd(id) {
            ajax({
                headers: {"csrf": "${csrf}"},
                type: "POST",
                url: "${contextPath}/job/editsingle.do",
                data: {"id": id}
            },function (obj) {
                $("#cmdform")[0].reset();
                if (obj != null) {
                    $("#cmdId").val(obj.jobId);
                    $("#command").val(obj.command);
                    $('#cmdModal').modal('show');
                }
            });
        }

        function saveCmd() {
            var jobId = $("#cmdId").val();
            if (!jobId) {
                alert("页面异常，请刷新重试!");
                return false;
            }

            var command = $("#command").val();
            if (!command) {
                alert("执行命令不能为空!");
                return false;
            }

            ajax({
                headers: {"csrf": "${csrf}"},
                type: "POST",
                url: "${contextPath}/job/editcmd.do",
                data: {
                    "jobId": jobId,
                    "command": toBase64(command)
                }
            },function (data) {
                if (data) {
                    $('#cmdModal').modal('hide');
                    alertMsg("修改成功");
                    $("#command_" + jobId).attr("title", command);
                    if (command.length > 50) {
                        command = command.substring(0, 50) + "...";
                    }
                    $("#command_" + jobId).html(escapeHtml(command));
                } else {
                    alert("修改失败");
                }
            });
        }

        function remove(id) {
            swal({
                title: "",
                text: "确定要删除该作业吗？",
                type: "warning",
                showCancelButton: true,
                closeOnConfirm: false,
                confirmButtonText: "删除"
            }, function () {
                ajax({
                    headers: {"csrf": "${csrf}"},
                    type: "POST",
                    url: "${contextPath}/job/checkdel.do",
                    data: {"id": id}
                },function (data) {
                    if (data == "error") {
                        alert("该作业不存在,删除失败!")
                    } else if (data == "false") {
                        alert("该作业正在运行中,删除失败!")
                    } else {
                        ajax({
                            headers: {"csrf": "${csrf}"},
                            type: "POST",
                            url: "${contextPath}/job/delete.do",
                            data: {"id": id}
                        },function (data) {
                            if (data) {
                                alertMsg("删除作业成功");
                                location.reload();
                            } else {
                                alertMsg("删除作业失败");
                            }
                        });
                    }
                })
            });
        }
    </script>
</head>

<body>

<section id="content" class="container">

    <!-- Messages Drawer -->
    <jsp:include page="/WEB-INF/layouts/message.jsp"/>

    <!-- Breadcrumb -->
    <ol class="breadcrumb hidden-xs">
        <li class="icon">&#61753;</li>
        当前位置：
        <li><a href="">opencron</a></li>
        <li><a href="">作业管理</a></li>
        <li><a href="">作业列表</a></li>
    </ol>
    <h4 class="page-title"><i class="fa fa-tasks" aria-hidden="true"></i>&nbsp;作业列表</h4>

    <!-- Deafult Table -->
    <div class="block-area" id="defaultStyle">
        <div>
            <div style="float: left">
                <label>
                    每页 <select size="1" class="select-opencron" id="size" style="width: 50px;">
                    <option value="15">15</option>
                    <option value="30" ${pageBean.pageSize eq 30 ? 'selected' : ''}>30</option>
                    <option value="50" ${pageBean.pageSize eq 50 ? 'selected' : ''}>50</option>
                    <option value="100" ${pageBean.pageSize eq 100 ? 'selected' : ''}>100</option>
                </select> 条记录
                </label>
            </div>

            <div style="float: right;margin-top: -9px">
                <label for="agentId">执行器：</label>
                <select id="agentId" name="agentId" class="select-opencron" style="width: 110px;">
                    <option value="">全部</option>
                    <c:forEach var="d" items="${agents}">
                        <option value="${d.agentId}" ${d.agentId eq agentId ? 'selected' : ''}>${d.name}</option>
                    </c:forEach>
                </select>

                &nbsp;&nbsp;&nbsp;
                <label for="cronType">规则类型：</label>
                <select id="cronType" name="cronType" class="select-opencron" style="width: 80px;">
                    <option value="">全部</option>
                    <option value="0" ${cronType eq 0 ? 'selected' : ''}>crontab</option>
                    <option value="1" ${cronType eq 1 ? 'selected' : ''}>quartz</option>
                </select>

                &nbsp;&nbsp;&nbsp;
                <label for="jobType">作业类型：</label>
                <select id="jobType" name="jobType" class="select-opencron" style="width: 90px;">
                    <option value="">全部</option>
                    <option value="0" ${jobType eq 0 ? 'selected' : ''}>单一作业</option>
                    <option value="1" ${jobType eq 1 ? 'selected' : ''}>流程作业</option>
                </select>

                &nbsp;&nbsp;&nbsp;
                <label for="execType">运行模式：</label>
                <select id="execType" name="execType" class="select-opencron" style="width: 80px;">
                    <option value="">全部</option>
                    <option value="1" ${execType eq 1 ? 'selected' : ''}>手动</option>
                    <option value="0" ${execType eq 0 ? 'selected' : ''}>自动</option>
                </select>
                &nbsp;&nbsp;&nbsp;

                <label for="redo">重跑：</label>
                <select id="redo" name="redo" class="select-opencron" style="width: 80px;">
                    <option value="">全部</option>
                    <option value="1" ${redo eq 1 ? 'selected' : ''}>是</option>
                    <option value="0" ${redo eq 0 ? 'selected' : ''}>否</option>
                </select>

                <a href="${contextPath}/job/add.htm?csrf=${csrf}" class="btn btn-sm m-t-10"
                   style="margin-left: 20px;margin-bottom: 8px;margin-top: -3px;"><i class="icon">&#61943;</i>添加</a>
            </div>
        </div>

        <table class="table tile textured" style="font-size: 13px;">
            <thead>
            <tr>
                <th>名称</th>
                <th>执行器</th>
                <th>作业人</th>
                <th>执行命令</th>
                <th>作业类型</th>
                <th>运行模式</th>
                <th>规则类型</th>
                <th>时间规则</th>
                <th class="text-center">
                    <i class="icon-time bigger-110 hidden-480"></i>操作
                </th>
            </tr>
            </thead>
            <tbody>
            <%--父作业--%>
            <c:forEach var="r" items="${pageBean.result}" varStatus="index">
                <tr class="trGroup${r.flowId}">
                    <c:if test="${r.jobType eq 0}">
                        <td id="jobName_${r.jobId}" title="${r.jobName}">${cron:substr(r.jobName, 0,20 ,"..." )}</td>
                    </c:if>
                    <c:if test="${r.jobType eq 1}">
                        <td class="name_${r.flowId}_1" title="${r.jobName}">${cron:substr(r.jobName, 0,20 ,"..." )}</td>
                        <td style="display: none;"
                            class="name_${r.flowId}_2"
                            rowspan="${fn:length(r.children)+1}"
                            title="${r.jobName}">${cron:substr(r.jobName, 0,20 ,"..." )}

                            <c:forEach var="c" items="${r.children}" varStatus="index">
                                <div class="down">
                                    <i aria-hidden="true" style="font-size:14px" class="fa fa-arrow-down"></i>
                                </div>
                                <span title="${c.jobName}">${cron:substr(c.jobName, 0,20 ,"..." )}</span>
                            </c:forEach>

                        </td>
                    </c:if>
                    <td><a href="${contextPath}/agent/detail/${r.agentId}.htm?csrf=${csrf}">${r.agentName}</a></td>
                    <c:if test="${permission eq true}">
                        <td><a href="${contextPath}/user/detail/${r.userId}.htm?csrf=${csrf}">${r.operateUname}</a>
                        </td>
                    </c:if>
                    <c:if test="${permission eq false}">
                        <td>${r.operateUname}</td>
                    </c:if>
                    <td style="width: 25%">
                        <div class="opencron_command">
                            <a href="#" title="${cron:escapeHtml(r.command)}" class="dot-ellipsis dot-resize-update" onclick="editCmd('${r.jobId}')" id="command_${r.jobId}">
                                    ${cron:escapeHtml(r.command)}
                            </a>
                        </div>
                    </td>
                    <td class="text-center">
                        <c:if test="${r.jobType eq 0}">单一作业</c:if>
                        <c:if test="${r.jobType eq 1}">流程作业</c:if>
                    </td>
                    <td id="execType_${r.jobId}" class="text-center">
                        <c:if test="${r.execType eq 1}"><font color="red">手动</font></c:if>
                        <c:if test="${r.execType eq 0}"><font color="green">自动</font></c:if>
                    </td>
                    <td id="cronType_${r.jobId}">
                        <c:choose>
                            <c:when test="${r.execType eq 1}">
                                <div class="none">--</div>
                            </c:when>
                            <c:otherwise>
                                <c:if test="${r.cronType eq 0}">
                                    <img class="text-center" width="70px" src="${contextPath}/static/img/crontab_ico.png">
                                </c:if>
                                <c:if test="${r.cronType eq 1}">
                                    <img class="text-center" width="70px" src="${contextPath}/static/img/quartz_ico.png">
                                </c:if>
                            </c:otherwise>
                        </c:choose>
                    </td>

                    <td id="cronExp_${r.jobId}">
                        <c:choose>
                            <c:when test="${r.execType eq 1}">
                                <div class="none">--</div>
                            </c:when>
                            <c:otherwise>
                                ${r.cronExp}
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td class="text-center">
                        <div class="visible-md visible-lg hidden-sm hidden-xs action-buttons">
                            <c:if test="${r.jobType eq 1}">
                                <a href="#" title="流程作业" id="job_${r.jobId}" childOpen="off" onclick="showChild('${r.jobId}','${r.flowId}')"> <i style="font-size:14px;" class="fa fa-angle-double-down" id="icon${r.jobId}"></i></a>&nbsp;&nbsp;
                            </c:if>
                            <c:if test="${r.jobType eq 0}">
                                <a href="#" title="编辑" onclick="editSingle('${r.jobId}')">
                                    <i class="glyphicon glyphicon-pencil"></i>
                                </a>
                            </c:if>

                            <c:if test="${r.jobType eq 1}">
                                <a title="编辑" href="${contextPath}/job/editflow.htm?id=${r.jobId}&csrf=${csrf}">
                                    <i class="glyphicon glyphicon-pencil"></i>
                                </a>
                            </c:if>&nbsp;

                            <c:if test="${r.execType eq 0}">
                                <c:choose>
                                    <c:when test="${r.pause eq null or r.pause eq false}">
                                        <span>
                                        <a id="pause_${r.jobId}" href="#" title="暂停" onclick="pauseJob('${r.jobId}',true)">
                                           <i aria-hidden="true" class="fa fa-pause-circle-o"></i>
                                        </a>
                                        </span>
                                    </c:when>
                                    <c:otherwise>
                                       <span>
                                        <a id="pause_${r.jobId}" href="#" title="恢复" onclick="pauseJob('${r.jobId}',false)">
                                           <i aria-hidden="true" class="fa fa-history"></i>
                                        </a>
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                                &nbsp;
                            </c:if>

                            <span id="execButton_${r.jobId}">
                                    <a href="#" title="执行" onclick="executeJob('${r.jobId}')">
                                       <i aria-hidden="true" class="fa fa-play"></i>
                                    </a>
                                </span>&nbsp;

                            <a href="#" onclick="remove('${r.jobId}')" title="删除">
                                <i aria-hidden="true" class="fa fa-times"></i>
                            </a>&nbsp;

                            <a href="${contextPath}/job/detail/${r.jobId}.htm?csrf=${csrf}" title="查看详情">
                                <i class="glyphicon glyphicon-eye-open"></i>
                            </a>
                        </div>
                    </td>
                </tr>
                <%--子作业--%>
                <c:if test="${r.jobType eq 1}">
                    <c:forEach var="c" items="${r.children}" varStatus="index">
                        <tr class="child${r.jobId} trGroup${r.flowId}" style="display: none;">
                            <td><a href="${contextPath}/agent/detail/${c.agentId}.htm?csrf=${csrf}">${c.agentName}</a>
                            </td>
                            <c:if test="${permission eq true}">
                                <td>
                                    <a href="${contextPath}/user/detail/${c.userId}.htm?csrf=${csrf}">${c.operateUname}</a>
                                </td>
                            </c:if>
                            <c:if test="${permission eq false}">
                                <td>${c.operateUname}</td>
                            </c:if>
                            <td style="width: 25%">
                                <div class="opencron_command">
                                    <a href="#" title="${cron:escapeHtml(c.command)}" class="dot-ellipsis dot-resize-update" onclick="editCmd('${c.jobId}')" id="command_${c.jobId}">
                                            ${cron:escapeHtml(c.command)}
                                    </a>
                                </div>
                            </td>
                            <td class="text-center">
                                流程作业
                            </td>
                            <td>
                                <div class="none">--</div>
                            </td>
                            <td>
                                <div class="none">--</div>
                            </td>
                            <td>
                                <div class="none">--</div>
                            </td>
                            <td class="text-center">
                                <div class="visible-md visible-lg hidden-sm hidden-xs action-buttons">
                                    <a href="${contextPath}/job/detail/${c.jobId}.htm?csrf=${csrf}" title="查看详情">
                                        <i class="glyphicon glyphicon-eye-open"></i>
                                    </a>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </c:if>
            </c:forEach>
            </tbody>
        </table>

        <cron:pager href="${contextPath}/job/view.htm?agentId=${agentId}&execType=${execType}&redo=${redo}&csrf=${csrf}" id="${pageBean.pageNo}" size="${pageBean.pageSize}" total="${pageBean.totalCount}"/>

    </div>

    <!-- 修改作业弹窗 -->
    <div class="modal fade" id="jobModal" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button class="close btn-float" data-dismiss="modal" aria-hidden="true"><i class="md md-close"></i></button>
                    <h4>修改作业</h4>
                </div>
                <div class="modal-body">
                    <form class="form-horizontal" role="form" id="jobform">
                        <input type="hidden" id="id">
                        <input type="hidden" name="agentId" id="magentId">
                        <div class="form-group">
                            <label for="agent" class="col-lab control-label" title="要执行此作业的机器名称和IP地址">执&nbsp;&nbsp;行&nbsp;&nbsp;器：</label>
                            <div class="col-md-9">
                                <input type="text" class="form-control " id="agent" readonly>&nbsp;
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="jobName" class="col-lab control-label" title="作业名称必填">作业名称：</label>
                            <div class="col-md-9">
                                <input type="text" class="form-control" id="jobName">&nbsp;&nbsp;<label id="checkJobName"></label>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-lab control-label" title="1.手动模式: 管理员手动执行 2.自动模式: 执行器自动执行">运行模式：</label>&nbsp;&nbsp;
                            <label for="execType1" onclick="toggle.cronexp.hide()" class="radio-label"><input type="radio" name="execType" value="1" id="execType1">手动&nbsp;&nbsp;&nbsp;</label>
                            <label for="execType0" onclick="toggle.cronexp.show()" class="radio-label"><input type="radio" name="execType" value="0" id="execType0">自动</label>
                        </div>
                        <div class="form-group cronExpDiv">
                            <label class="col-lab control-label" title="1.crontab: unix/linux的时间格式表达式&nbsp;&nbsp;2.quartz: quartz框架的时间格式表达式">规则类型：</label>&nbsp;&nbsp;
                            <label for="cronType0" class="radio-label"><input type="radio" name="cronType" value="0" id="cronType0">crontab&nbsp;&nbsp;&nbsp;</label>
                            <label for="cronType1" class="radio-label"><input type="radio" name="cronType" value="1" id="cronType1">quartz</label>
                        </div>
                        <br>
                        <div class="form-group cronExpDiv">
                            <label for="cronExp" class="col-lab control-label" title="请采用对应类型的时间格式表达式">时间规则：</label>
                            <div class="col-md-9">
                                <input type="text" class="form-control " id="cronExp"/>&nbsp;&nbsp;<label id="checkcronExp"></label>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="cmd" class="col-lab control-label" title="请采用unix/linux的shell支持的命令">执行命令：</label>
                            <div class="col-md-9">
                                <textarea class="form-control " id="cmd" name="cmd" style="height: 80px;"></textarea>&nbsp;
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="runAs" class="col-lab control-label">运行身份：</label>
                            <div class="col-md-9">
                                <input type="text" class="form-control" id="runAs" name="runAs" value="root">
                                <span class="tips"><b>*&nbsp;</b>该任务以哪个身份执行(默认是root)</span>
                            </div>
                        </div><br>

                        <div class="form-group">
                            <label for="successExit" class="col-lab control-label">成功标识：</label>
                            <div class="col-md-9">
                                <input type="text" class="form-control" id="successExit" name="successExit" value="0">
                                <span class="tips"><b>*&nbsp;</b>自定义作业执行成功的返回标识(默认执行成功是0)</span>
                            </div>
                        </div><br>

                        <div class="form-group">
                            <label for="timeout" class="col-lab control-label"
                                   title="执行作业允许的最大时间,超过则为超时(0:忽略超时时间,分钟为单位)">超时时间：</label>
                            <div class="col-md-9">
                                <input type="text" class="form-control" id="timeout" name="timeout" value="0">
                            </div>
                        </div>
                        <br>

                        <div class="form-group">
                            <label class="col-lab control-label" title="作业失败后是否重新执行此作业">重新执行：</label>&nbsp;&nbsp;
                            <label for="redo1" onclick="toggle.count.show()" class="radio-label"><input type="radio" name="redo" value="1" id="redo1"> 是&nbsp;&nbsp;&nbsp;</label>
                            <label for="redo0" onclick="toggle.count.hide()" class="radio-label"><input type="radio" name="redo" value="0" id="redo0"> 否</label>
                        </div>
                        <br>
                        <div class="form-group countDiv">
                            <label for="runCount" class="col-lab control-label" title="执行失败时自动重新执行的截止次数">重跑次数：</label>
                            <div class="col-md-9">
                                <input type="text" class="form-control " id="runCount"/>&nbsp;
                            </div>
                        </div>

                        <div class="form-group" style="margin-top: 0px;margin-bottom: 22px">
                            <label class="col-lab control-label" title="任务执行失败时是否发信息报警">失败报警：</label>&nbsp;&nbsp;
                            <label onclick="toggle.contact.show()" for="warning1" class="radio-label"><input type="radio" name="warning" value="1" id="warning1">是&nbsp;&nbsp;&nbsp;</label>
                            <label onclick="toggle.contact.hide()" for="warning0" class="radio-label"><input type="radio" name="warning" value="0" id="warning0">否</label>
                        </div>
                        <div class="form-group contact">
                            <label for="mobiles" class="col-lab control-label" title="任务执行失败时将发送短信给此手机">报警手机：</label>
                            <div class="col-md-9">
                                <input type="text" class="form-control " id="mobiles"/>&nbsp;
                            </div>
                        </div>
                        <div class="form-group contact">
                            <label for="email" class="col-lab control-label" title="任务执行失败时将发送报告给此邮箱">报警邮箱：</label>
                            <div class="col-md-9">
                                <input type="text" class="form-control " id="email"/>&nbsp;
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="comment" class="col-lab control-label" title="此作业内容的描述">描&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;述：</label>
                            <div class="col-md-9">
                                <textarea style="height: 50px;" name="comment" id="comment" class="form-control"></textarea>&nbsp;
                            </div>
                        </div>

                    </form>
                </div>
                <div class="modal-footer">
                    <center>
                        <button type="button" class="btn btn-sm" onclick="save()">保存</button>&nbsp;&nbsp;
                        <button type="button" class="btn btn-sm" data-dismiss="modal">关闭</button>
                    </center>
                </div>
            </div>
        </div>
    </div>

    <div class="modal fade" id="cmdModal" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button class="close btn-float" data-dismiss="modal" aria-hidden="true"><i class="md md-close"></i></button>
                    <h4>修改命令</h4>
                </div>
                <div class="modal-body">
                    <form class="form-horizontal" role="form" id="cmdform">
                        <input type="hidden" id="cmdId">
                        <div class="form-group">
                            <label for="command" class="col-lab control-label" title="请采用unix/linux的shell支持的命令">执行命令：</label>
                            <div class="col-md-9">
                                <textarea class="form-control " id="command" name="command" style="height: 120px;"></textarea>&nbsp;
                            </div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <center>
                        <button type="button" class="btn btn-sm" onclick="saveCmd()">保存</button>&nbsp;&nbsp;
                        <button type="button" class="btn btn-sm" data-dismiss="modal">关闭</button>
                    </center>
                </div>
            </div>
        </div>
    </div>

</section>

</body>

</html>


