<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="cron" uri="http://www.opencron.org" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <style type="text/css">
        .subJobUl li {
            background-color: rgba(0, 0, 0, 0.3);
            border-radius: 1px;
            height: 26px;
            list-style: outside none none;
            margin-top: -27px;
            margin-bottom: 29px;
            margin-left: 100px;
            padding: 4px 15px;
            width: 350px;
        }

        .delSubJob {
            float: right;
            margin-right: 2px
        }
    </style>

    <script type="text/javascript">

        var validata = {

            status: true,

            init: function () {
                this.status = true;
            },

            jobName: function () {
                var elemFix = arguments[0] || "";
                var _jobName = $("#jobName" + elemFix).val();
                if (!_jobName) {
                    opencron.tipError("#jobName" + elemFix, "必填项,作业名称不能为空");
                    this.status = false;
                } else {
                    if (_jobName.length < 4 || _jobName.length > 17) {
                        opencron.tipError("#jobName" + elemFix, "作业名称不能小于4个字符并且不能超过16个字符!");
                        this.status = false;
                    } else {
                        var _this = this;
                        $.ajax({
                            headers: {"csrf": "${csrf}"},
                            type: "POST",
                            url: "${contextPath}/job/checkname.do",
                            data: {
                                "name": _jobName,
                                "agentId": $("#agentId").val()
                            },
                            success: function (data) {
                                if (!data) {
                                    opencron.tipError("#jobName" + elemFix, "作业名称已存在!");
                                    _this.status = false;
                                } else {
                                    opencron.tipOk("#jobName" + elemFix);
                                }
                            },
                            error: function () {
                                opencron.tipError("#jobName" + elemFix, "网络请求错误,请重试!");
                                _this.status = false;
                            }
                        });
                    }
                }
            },

            cronExp: function () {
                var execType = $('input[type="radio"][name="execType"]:checked').val();
                var cronType = $('input[type="radio"][name="cronType"]:checked').val();
                var cronExp = $("#cronExp").val();
                if (execType == 0) {
                    if (!cronExp) {
                        opencron.tipError("#cronExp", "时间规则不能为空!");
                        this.status = false;
                    } else {
                        var _this = this;
                        $.ajax({
                            headers: {"csrf": "${csrf}"},
                            type: "POST",
                            url: "${contextPath}/verify/exp.do",
                            data: {
                                "cronType": cronType,
                                "cronExp": cronExp
                            },
                            success: function (data) {
                                if (data) {
                                    opencron.tipOk("#cronExp");
                                } else {
                                    opencron.tipError("#cronExp", "时间规则语法错误!");
                                    _this.status = false;
                                }
                            }
                        });
                    }
                }
            },

            command: function () {
                var elemFix = arguments[0] || "";
                if ($("#cmd" + elemFix).val().length == 0) {
                    opencron.tipError("#cmd" + elemFix, "执行命令不能为空,请填写执行命令");
                    this.status = false;
                } else {
                    opencron.tipOk("#cmd" + elemFix);
                }
            },

            runAs: function () {
                var elemFix = arguments[0] || "";
                if ($("#runAs" + elemFix).val().length == 0) {
                    opencron.tipError("#runAs" + elemFix, "任务运行身份不能为空,请填写任务运行身份");
                    this.status = false;
                } else {
                    opencron.tipOk("#runAs" + elemFix);
                }
            },

            successExit: function () {
                var elemFix = arguments[0] || "";
                var successExit = $("#successExit" + elemFix).val();
                if (successExit.length == 0) {
                    opencron.tipError("#successExit" + elemFix, "自定义成功标识不能为空");
                    this.status = false;
                } else if (isNaN(successExit)) {
                    opencron.tipError("#successExit" + elemFix, "自定义成功标识必须为数字");
                    this.status = false;
                } else {
                    opencron.tipOk("#successExit" + elemFix);
                }
            },

            runCount: function () {
                var elemFix = arguments[0] || "";
                var redo = elemFix ? $("#itemRedo").val() : $('input[type="radio"][name="redo"]:checked').val();
                console.log(redo);
                var reg = /^[0-9]*[1-9][0-9]*$/;
                if (redo == 1) {
                    var _runCount = $("#runCount" + elemFix).val();
                    if (!_runCount) {
                        opencron.tipError("#runCount" + elemFix, "请填写重跑次数!");
                        this.status = false;
                    } else if (!reg.test(_runCount)) {
                        opencron.tipError("#runCount" + elemFix, "截止重跑次数必须为正整数!");
                        this.status = false;
                    } else {
                        opencron.tipOk("#runCount" + elemFix);
                    }
                }
            },

            subJob: function () {
                if ($('input[name="jobType"]:checked').val() == 1) {
                    if ($("#subJobDiv:has(li)").length == 0) {
                        opencron.tipError($("#jobTypeTip"), "当前是流程作业,请至少添加一个子作业!");
                        this.status = false;
                    }
                }
            },

            mobiles: function () {
                var mobiles = $("#mobiles").val();
                if (!mobiles) {
                    opencron.tipError("#mobiles", "请填写手机号码!");
                    this.status = false;
                }
                var mobs = mobiles.split(",");

                var verify = true;

                for (var i in mobs) {
                    if (!opencron.testMobile(mobs[i])) {
                        opencron.tipError("#mobiles", "请填写正确的手机号码!");
                        this.status = false;
                        verify = false;
                    }
                }
                if (verify) {
                    opencron.tipOk("#mobiles");
                }
            },

            email: function () {
                var emails = $("#email").val();
                if (!emails) {
                    opencron.tipError("#email", "请填写邮箱地址!");
                    this.status = false;
                }

                var emas = emails.split(",");
                var verify = true;
                for (var i in emas) {
                    if (!opencron.testEmail(emas[i])) {
                        opencron.tipError("#email", "请填写正确的邮箱地址!");
                        this.status = false;
                        verify = false;
                    }
                }
                if (verify) {
                    opencron.tipOk("#mobiles");
                }
            },

            timeout: function () {
                var elemFix = arguments[0] || "";
                var timeout = $("#timeout" + elemFix).val();
                if (timeout.length > 0) {
                    if (isNaN(timeout) || parseInt(timeout) < 0) {
                        opencron.tipError("#timeout" + elemFix, "超时时间必须为正整数,请填写正确的超时时间!");
                        this.status = false;
                    } else {
                        opencron.tipOk("#timeout" + elemFix);
                    }
                } else {
                    opencron.tipError("#timeout" + elemFix, "超时时间不能为空,请填写(0:忽略超时时间,分钟为单位!");
                }
            },

            warning: function () {
                var _warning = $('input[type="radio"][name="warning"]:checked').val();
                if (_warning == 1) {
                    this.mobiles();
                    this.email();
                }
            },

            verify: function () {
                this.init();
                this.jobName();
                this.cronExp();
                this.command();
                this.successExit();
                this.runAs();
                this.runCount();
                this.subJob();
                this.timeout();
                this.warning();
                if (this.status) {
                    var cmd = $("#cmd").val();
                    $("#command").val(toBase64(cmd));
                    $("#jobform").submit();
                }
            }
        }

        var toggle = {
            cronExp: function (_toggle) {
                if (_toggle) {
                    $(".cronExpDiv").show();
                    $("#execTypeTip").html("自动模式: 执行器自动执行");
                } else {
                    $(".cronExpDiv").hide();
                    $("#execTypeTip").html("手动模式: 管理员手动执行");
                }
            },
            redo: function (_toggle) {
                $("#itemRedo").val(_toggle);
                console.log(_toggle)
                if (_toggle == 1) {
                    $(".countDiv1").show();
                    $("#redo1").prop("checked", true);
                    $("#redo1").parent().removeClass("checked").addClass("checked");
                    $("#redo1").parent().attr("aria-checked", true);
                    $("#redo1").parent().prop("onclick", "showContact()");
                    $("#redo0").parent().removeClass("checked");
                    $("#redo0").parent().attr("aria-checked", false);
                } else {
                    $(".countDiv1").hide();
                    $("#redo0").prop("checked", true);
                    $("#redo0").parent().removeClass("checked").addClass("checked");
                    $("#redo0").parent().attr("aria-checked", true);
                    $("#redo1").parent().removeClass("checked");
                    $("#redo1").parent().attr("aria-checked", false);
                }
            },
            count: function (_toggle) {
                if (_toggle) {
                    $("#redo").val(1);
                    $(".countDiv").show()
                } else {
                    $("#redo").val(0);
                    $(".countDiv").hide();
                }
            },
            contact: function (_toggle) {
                if (_toggle) {
                    $(".contact").show()
                } else {
                    $(".contact").hide();
                    opencron.tipDefault("#mobiles");
                    opencron.tipDefault("#email");
                }
            },
            cronTip: function (type) {
                if (type == "0") {
                    $("#crontabTip").html("crontab: unix/linux的时间格式表达式 ");
                    $("#quartzTip").html('crontab: 请采用unix/linux的时间格式表达式,如 00 01 * * *');
                }
                if (type == "1") {
                    $("#crontabTip").html('quartz: quartz框架的时间格式表达式');
                    $("#quartzTip").html('quartz: 请采用quartz框架的时间格式表达式,如 0 0 10 L * ?');
                }
                if ($("#cronExp").val().length > 0) {
                    validata.cronExp();
                }
            },

            runModel: function (type) {
                if (type == "0") {
                    $("#runModelTip").html("串行: 流程任务里的每个任务按照定义的顺序依次执行");
                }
                if (type == "1") {
                    $("#runModelTip").html('并行: 流程任务里的所有子任务同时执行');
                }
            },

            subJob: function (_toggle) {
                if (_toggle == "1") {
                    $("#jobTypeTip").html("流程作业: 有多个作业组成一个作业组");
                    $("#subJob").show();
                    $("#runModel").show();
                } else {
                    $("#jobTypeTip").html("单一作业: 当前定义作业为要执行的目前作业");
                    $("#subJob").hide();
                    $("#runModel").hide();
                }
            }
        };

        var subJob = {

            tipDefault: function () {
                opencron.tipDefault("#jobName1");
                opencron.tipDefault("#cmd1");
                opencron.tipDefault("#runAs1");
                opencron.tipDefault("#successExit1");
                opencron.tipDefault("#timeout1");
                opencron.tipDefault("#runCount1");
            },

            add: function () {
                $("#subForm")[0].reset();
                toggle.redo(1);
                this.tipDefault();
                $("#subTitle").html("添加子作业").attr("action", "add");
            },

            edit: function (id) {
                this.tipDefault();
                $("#subTitle").html("编辑子作业").attr("action", "edit").attr("tid", id);
                $("#" + id).find("input").each(function (index, element) {
                    if ($(element).attr("name") == "child.jobName") {
                        $("#jobName1").val(unEscapeHtml($(element).val()));
                    }
                    if ($(element).attr("name") == "child.agentId") {
                        $("#agentId1").val($(element).val());
                    }
                    if ($(element).attr("name") == "child.command") {
                        $("#cmd1").val(passBase64($(element).val()));
                    }

                    if ($(element).attr("name") == "child.runAs") {
                        $("#runAs1").val($(element).val());
                    }

                    if ($(element).attr("name") == "child.successExit") {
                        $("#successExit1").val($(element).val());
                    }

                    if ($(element).attr("name") == "child.redo") {
                        toggle.redo($("#itemRedo").val() || $(element).val());
                    }

                    if ($(element).attr("name") == "child.runCount") {
                        $("#runCount1").val($(element).val());
                    }

                    if ($(element).attr("name") == "child.timeout") {
                        $("#timeout1").val($(element).val());
                    }

                    if ($(element).attr("name") == "child.comment") {
                        $("#comment1").val(unEscapeHtml($(element).val()));
                    }
                });
            },

            remove: function (node) {
                $(node).parent().slideUp(300, function () {
                    this.remove()
                });
            },

            close: function () {
                $("#subForm")[0].reset();
                $('#jobModal').modal('hide');
            },

            verify: function () {
                validata.init();
                validata.jobName("1");
                validata.command("1");
                validata.runAs("1");
                validata.successExit("1");
                validata.timeout("1");
                validata.runCount("1");
                if (validata.status) {

                    //添加
                    var _jobName = $("#jobName1").val();
                    if ($("#subTitle").attr("action") === "add") {
                        var timestamp = Date.parse(new Date());
                        var addHtml =
                            "<li id='" + timestamp + "'>" +
                            "<span onclick='subJob.edit(\"" + timestamp + "\")'>" +
                            "   <a data-toggle='modal' href='#jobModal' title='编辑'>" +
                            "       <i class='glyphicon glyphicon-pencil'></i>&nbsp;&nbsp;" +
                            "       <span id='name_" + timestamp + "'>" + escapeHtml(_jobName) + "</span>" +
                            "   </a>" +
                            "</span>" +
                            "<span class='delSubJob' onclick='subJob.remove(this)'>" +
                            "   <a href='#' title='删除'>" +
                            "       <i class='glyphicon glyphicon-trash'></i>" +
                            "   </a>" +
                            "</span>" +
                            "<input type='hidden' name='child.jobId' value=''>" +
                            "<input type='hidden' name='child.jobName' value='" + escapeHtml(_jobName) + "'>" +
                            "<input type='hidden' name='child.agentId' value='" + $("#agentId1").val() + "'>" +
                            "<input type='hidden' name='child.command' value='" + toBase64($("#cmd1").val()) + "'>" +
                            "<input type='hidden' name='child.redo' value='" + $('#itemRedo').val() + "'>" +
                            "<input type='hidden' name='child.runCount' value='" + $("#runCount1").val() + "'>" +
                            "<input type='hidden' name='child.timeout' value='" + $("#timeout1").val() + "'>" +
                            "<input type='hidden' name='child.runAs' value='" + $("#runAs1").val() + "'>" +
                            "<input type='hidden' name='child.successExit' value='" + $("#successExit1").val() + "'>" +
                            "<input type='hidden' name='child.comment' value='" + escapeHtml($("#comment1").val()) + "'>" +
                            "</li>";
                        console.log(addHtml)
                        $("#subJobDiv").append($(addHtml));
                    } else if ($("#subTitle").attr("action") == "edit") {//编辑
                        var id = $("#subTitle").attr("tid");
                        $("#" + id).find("input").each(function (index, element) {

                            if ($(element).attr("name") == "child.jobName") {
                                $(element).attr("value", _jobName);
                            }

                            if ($(element).attr("name") == "child.redo") {
                                $(element).attr("value", $('#itemRedo').val());
                            }

                            if ($(element).attr("name") == "child.runCount") {
                                $(element).attr("value", $("#runCount1").val());
                            }

                            if ($(element).attr("name") == "child.successExit") {
                                $(element).attr("value", $("#successExit1").val());
                            }

                            if ($(element).attr("name") == "child.runAs") {
                                $(element).attr("value", $("#runAs1").val());
                            }

                            if ($(element).attr("name") == "child.agentId") {
                                $(element).attr("value", $("#agentId1").val());
                            }
                            if ($(element).attr("name") == "child.command") {
                                $(element).attr("value", toBase64($("#cmd1").val()));
                            }

                            if ($(element).attr("name") == "child.timeout") {
                                $(element).attr("value", $("#timeout1").val());
                            }

                            if ($(element).attr("name") == "child.comment") {
                                $(element).attr("value", $("#comment1").val());
                            }
                        });
                        $("#name_" + id).html(escapeHtml(_jobName));
                    }
                    subJob.close();
                }
            }
        };

        $(document).ready(function () {

            $("#execType0").next().click(function () {
                toggle.cronExp(true);
            });
            $("#execType0").parent().parent().click(function () {
                toggle.cronExp(true);
            });
            $("#execType1").next().click(function () {
                toggle.cronExp(false);
            });
            $("#execType1").parent().parent().click(function () {
                toggle.cronExp(false)
            });

            $("#cronType0").next().click(function () {
                toggle.cronTip(0);
            });
            $("#cronType0").parent().parent().click(function () {
                toggle.cronTip(0);
            });
            $("#cronType1").next().click(function () {
                toggle.cronTip(1);
            });
            $("#cronType1").parent().parent().click(function () {
                toggle.cronTip(1);
            });


            $("#runModel0").next().click(function () {
                toggle.runModel(0);
            });
            $("#runModel0").parent().parent().click(function () {
                toggle.runModel(0);
            });
            $("#runModel1").next().click(function () {
                toggle.runModel(1);
            });
            $("#runModel1").parent().parent().click(function () {
                toggle.runModel(1);
            });


            $("#redo01").next().click(function () {
                toggle.count(true)
            });
            $("#redo01").parent().parent().click(function () {
                toggle.count(true)
            });
            $("#redo00").next().click(function () {
                toggle.count(false)
            });
            $("#redo00").parent().parent().click(function () {
                toggle.count(false)
            });


            $("#redo1").next().click(function () {
                toggle.redo(1);
            });
            $("#redo1").parent().parent().click(function () {
                toggle.redo(1);
            })
            $("#redo0").next().click(function () {
                toggle.redo(0);
            });
            $("#redo0").parent().parent().click(function () {
                toggle.redo(0);
            });


            $("#jobType0").next().click(function () {
                toggle.subJob(0);
            });
            $("#jobType0").parent().parent().click(function () {
                toggle.subJob(0);
            });

            $("#jobType1").next().click(function () {
                toggle.subJob(1);
            });
            $("#jobType1").parent().parent().click(function () {
                toggle.subJob(1);
            });

            $("#warning0").next().click(function () {
                toggle.contact(false);
            });
            $("#warning0").parent().parent().click(function () {
                toggle.contact(false);
            });

            $("#warning1").next().click(function () {
                toggle.contact(true);
            });
            $("#warning1").parent().parent().click(function () {
                toggle.contact(true);
            });

            var execType = $('input[type="radio"][name="execType"]:checked').val();
            toggle.cronExp(execType == 0);

            var redo = $('input[type="radio"][name="redo"]:checked').val();
            toggle.count(redo == 1);

            var warning = $('input[type="radio"][name="warning"]:checked').val();
            toggle.contact(warning == 1);

            toggle.subJob($('input[type="radio"][name="jobType"]:checked').val());

            toggle.cronTip($('input[type="radio"][name="cronType"]:checked').val());

            //子作业拖拽
            $("#subJobDiv").sortable({
                delay: 100
            });

            $("#save-btn").click(function () {
                if (validata.verify()) {
                    $("#job").submit();
                }
            });

            $("#subjob-btn").click(function () {
                subJob.verify();
            });


            $("#jobName").blur(function () {
                validata.jobName();
            }).focus(function () {
                opencron.tipDefault("#jobName");
            });

            $("#cronExp").blur(function () {
                validata.cronExp();
            }).focus(function () {
                var type = $('input[type="radio"][name="cronType"]:checked').val();
                if (type == "0") {
                    $("#crontabTip").html("crontab: unix/linux的时间格式表达式 ");
                    $("#quartzTip").html('crontab: 请采用unix/linux的时间格式表达式,如 00 01 * * *');
                }
                if (type == "1") {
                    $("#crontabTip").html('quartz: quartz框架的时间格式表达式');
                    $("#quartzTip").html('quartz: 请采用quartz框架的时间格式表达式,如 0 0 10 L * ?');
                }
            });


            $("#jobName1").blur(function () {
                validata.jobName("1");
            }).focus(function () {
                opencron.tipDefault("#jobName1");
            });

            $("#cmd").blur(function () {
                validata.command();
            }).focus(function () {
                opencron.tipDefault("#cmd");
            });
            $("#cmd1").blur(function () {
                validata.command("1");
            }).focus(function () {
                opencron.tipDefault("#cmd1");
            });

            $("#runAs").blur(function () {
                validata.runAs();
            }).focus(function () {
                opencron.tipDefault("#runAs");
            });
            $("#runAs1").blur(function () {
                validata.runAs("1");
            }).focus(function () {
                opencron.tipDefault("#runAs1");
            });

            $("#runCount").blur(function () {
                validata.runCount();
            }).focus(function () {
                opencron.tipDefault("#runCount");
            });
            $("#runCount1").blur(function () {
                validata.runCount("1");
            }).focus(function () {
                opencron.tipDefault("#runCount1");
            });

            $("#successExit").blur(function () {
                validata.successExit();
            }).focus(function () {
                opencron.tipDefault("#successExit");
            });
            $("#successExit1").blur(function () {
                validata.successExit("1");
            }).focus(function () {
                opencron.tipDefault("#successExit1");
            });

            $("#timeout").blur(function () {
                validata.timeout();
            }).focus(function () {
                opencron.tipDefault("#timeout");
            });
            $("#timeout1").blur(function () {
                validata.timeout("1");
            }).focus(function () {
                opencron.tipDefault("#timeout1");
            });

            $("#mobiles").blur(function () {
                validata.mobiles();
            }).focus(function () {
                opencron.tipDefault("#mobiles");
            });

            $("#email").blur(function () {
                validata.email();
            }).focus(function () {
                opencron.tipDefault("#email");
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
        <li><a href="">opencron</a></li>
        <li><a href="">作业管理</a></li>
        <li><a href="">添加作业</a></li>
    </ol>
    <h4 class="page-title"><i class="fa fa-plus" aria-hidden="true"></i>&nbsp;添加作业</h4>

    <div style="float: right;margin-top: 5px">
        <a onclick="goback();" class="btn btn-sm m-t-10" style="margin-right: 16px;margin-bottom: -4px"><i
                class="fa fa-mail-reply" aria-hidden="true"></i>&nbsp;返回</a>
    </div>

    <div class="block-area" id="basic">
        <div class="tile p-15 textured">
            <form class="form-horizontal" role="form" id="jobform" action="${contextPath}/job/save.do"
                  method="post"></br>
                <input type="hidden" name="csrf" value="${csrf}">
                <input type="hidden" name="command" id="command">
                <div class="form-group">
                    <label for="agentId" class="col-lab control-label wid150"><i class="glyphicon glyphicon-leaf"></i>&nbsp;&nbsp;执&nbsp;&nbsp;行&nbsp;&nbsp;器&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label>
                    <div class="col-md-10">
                        <c:if test="${empty agent}">
                            <select id="agentId" name="agentId" class="form-control m-b-10 input-sm">
                                <c:forEach var="d" items="${agents}">
                                    <option value="${d.agentId}">${d.ip}&nbsp;(${d.name})</option>
                                </c:forEach>
                            </select>
                        </c:if>
                        <c:if test="${!empty agent}">
                            <input type="hidden" id="agentId" name="agentId" value="${agent.agentId}">
                            <input type="text" class="form-control input-sm"
                                   value="${agent.name}&nbsp;&nbsp;&nbsp;${agent.ip}" readonly>
                            <font color="red">&nbsp;*只读</font>
                        </c:if>
                        <span class="tips">&nbsp;&nbsp;要执行此作业的机器名称和IP地址</span>
                    </div>
                </div>
                <br>

                <div class="form-group">
                    <label for="jobName" class="col-lab control-label wid150"><i class="glyphicon glyphicon-tasks"></i>&nbsp;&nbsp;作业名称&nbsp;&nbsp;<b>*&nbsp;</b></label>
                    <div class="col-md-10">
                        <input type="text" class="form-control input-sm" id="jobName" name="jobName">
                        <span class="tips" tip="必填项,该作业的名称">必填项,该作业的名称</span>
                    </div>
                </div>
                <br>

                <div class="form-group">
                    <label class="col-lab control-label wid150"><i class="glyphicon glyphicon-info-sign"></i>&nbsp;&nbsp;运行模式&nbsp;&nbsp;<b>*&nbsp;</b></label>
                    <div class="col-md-10">
                        <label for="execType0" class="radio-label"><input type="radio" name="execType" id="execType0"
                                                                          value="0" checked>自动&nbsp;&nbsp;&nbsp;</label>
                        <label for="execType1" class="radio-label"><input type="radio" name="execType" id="execType1"
                                                                          value="1">手动</label>&nbsp;&nbsp;&nbsp;
                        </br><span class="tips" id="execTypeTip" tip="">自动模式:执行器自动执行</span>
                    </div>
                </div>
                <br>

                <div class="form-group cronExpDiv">
                    <label class="col-lab control-label wid150"><i class="glyphicon glyphicon-bookmark"></i>&nbsp;&nbsp;规则类型&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label>
                    <div class="col-md-10">
                        <label for="cronType0" class="radio-label"><input type="radio" name="cronType" value="0"
                                                                          id="cronType0" checked>crontab&nbsp;&nbsp;&nbsp;</label>
                        <label for="cronType1" class="radio-label"><input type="radio" name="cronType" value="1"
                                                                          id="cronType1">quartz</label>&nbsp;&nbsp;&nbsp;
                        </br><span class="tips" id="crontabTip" tip="crontab: unix/linux的时间格式表达式">crontab: unix/linux的时间格式表达式</span>
                    </div>
                </div>
                <br>

                <div class="form-group cronExpDiv">
                    <label for="cronExp" class="col-lab control-label wid150"><i class="glyphicon glyphicon-filter"></i>&nbsp;&nbsp;时间规则&nbsp;&nbsp;<b>*&nbsp;</b></label>
                    <div class="col-md-10">
                        <input type="text" class="form-control input-sm" id="cronExp" name="cronExp">
                        <span class="tips" id="quartzTip" tip="请采用unix/linux的时间格式表达式,如 00 01 * * *">请采用unix/linux的时间格式表达式,如 00 01 * * *</span>
                    </div>
                </div>
                <br>

                <div class="form-group">
                    <label for="cmd" class="col-lab control-label wid150"><i class="glyphicon glyphicon-th-large"></i>&nbsp;&nbsp;执行命令&nbsp;&nbsp;<b>*&nbsp;</b></label>
                    <div class="col-md-10">
                        <textarea class="form-control input-sm" id="cmd"
                                  style="height:200px;resize:vertical"></textarea>
                        <span class="tips" tip="请采用unix/linux的shell支持的命令">请采用unix/linux的shell支持的命令</span>
                    </div>
                </div>
                <br>

                <%-- <div class="form-group">
                     <label class="col-lab control-label"><i class="glyphicons glyphicons-saw-blade"></i>&nbsp;&nbsp;命令类型：</label>
                     <div class="col-md-10">
                         <label for="script-shell" class="radio-label"><input type="radio" name="scriptType" id="script-shell" value="0" checked>shell&nbsp;&nbsp;&nbsp;</label>
                         <label for="script-python" class="radio-label"><input type="radio" name="scriptType" id="script-python" value="1">python&nbsp;&nbsp;&nbsp;</label>
                         <label for="script-bat" class="radio-label"><input type="radio" name="scriptType" id="script-bat" value="2">bat&nbsp;&nbsp;&nbsp;</label>
                         <label for="script-php" class="radio-label"><input type="radio" name="scriptType" id="script-php" value="3">php&nbsp;&nbsp;&nbsp;</label>
                         <label for="script-powerShell" class="radio-label"><input type="radio" name="scriptType" id="script-powerShell" value="4">powerShell&nbsp;&nbsp;&nbsp;</label>
                         <br><span class="tips"><b>*&nbsp;</b>该命令的类型</span>
                     </div>
                 </div><br>--%>

                <div class="form-group">
                    <label for="runAs" class="col-lab control-label wid150"><i class="glyphicons glyphicons-user"></i>&nbsp;&nbsp;运行身份&nbsp;&nbsp;<b>*&nbsp;</b></label>
                    <div class="col-md-10">
                        <input type="text" class="form-control input-sm" id="runAs" name="runAs" value="root">
                        <span class="tips" tip="该任务以哪个身份执行(默认是root)">该任务以哪个身份执行(默认是root)</span>
                    </div>
                </div>
                <br>

                <div class="form-group">
                    <label for="successExit" class="col-lab control-label wid150"><i
                            class="glyphicons glyphicons-tags"></i>&nbsp;&nbsp;成功标识&nbsp;&nbsp;<b>*&nbsp;</b></label>
                    <div class="col-md-10">
                        <input type="text" class="form-control input-sm" id="successExit" name="successExit" value="0">
                        <span class="tips" tip="自定义作业执行成功的返回标识(默认执行成功是0)">自定义作业执行成功的返回标识(默认执行成功是0)</span>
                    </div>
                </div>
                <br>

                <div class="form-group">
                    <label class="col-lab control-label wid150"><i class="glyphicon  glyphicon glyphicon-forward"></i>&nbsp;&nbsp;失败重跑&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label>
                    <div class="col-md-10">
                        <label for="redo01" class="radio-label"><input type="radio" name="redo" value="1" id="redo01">是&nbsp;&nbsp;&nbsp;</label>
                        <label for="redo00" class="radio-label"><input type="radio" name="redo" value="0" id="redo00"
                                                                       checked>否</label>&nbsp;&nbsp;&nbsp;
                        <br><span class="tips">执行失败时是否自动重新执行</span>
                    </div>
                </div>
                <br>

                <div class="form-group countDiv">
                    <label for="runCount" class="col-lab control-label wid150"><i
                            class="glyphicon glyphicon-repeat"></i>&nbsp;&nbsp;重跑次数&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label>
                    <div class="col-md-10">
                        <input type="text" class="form-control input-sm" id="runCount" name="runCount">
                        <span class="tips" tip="执行失败时自动重新执行的截止次数">执行失败时自动重新执行的截止次数</span>
                    </div>
                </div>
                <br>

                <div class="form-group">
                    <label for="timeout" class="col-lab control-label wid150"><i
                            class="glyphicon glyphicon-ban-circle"></i>&nbsp;&nbsp;超时时间&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label>
                    <div class="col-md-10">
                        <input type="text" class="form-control input-sm" id="timeout" name="timeout" value="0">
                        <span class="tips">执行作业允许的最大时间,超过则为超时(0:忽略超时时间,分钟为单位)</span>
                    </div>
                </div>
                <br>


                <div class="form-group">
                    <label class="col-lab control-label wid150"><i class="glyphicon  glyphicon-random"></i>&nbsp;&nbsp;作业类型&nbsp;&nbsp;<b>*&nbsp;</b></label>
                    <div class="col-md-10">
                        <label for="jobType0" class="radio-label"><input type="radio" name="jobType" value="0"
                                                                         id="jobType0"
                                                                         checked>单一作业&nbsp;&nbsp;&nbsp;</label>
                        <label for="jobType1" class="radio-label"><input type="radio" name="jobType" value="1"
                                                                         id="jobType1">流程作业</label>&nbsp;&nbsp;&nbsp;
                        <br><span class="tips" id="jobTypeTip">单一作业: 当前定义作业为要执行的目标&nbsp;流程作业: 有多个作业组成作业组</span>
                    </div>
                </div>
                <br>

                <div class="form-group">
                    <span id="subJob" style="display: none">
                        <label class="col-lab control-label wid150"><i class="glyphicon glyphicon-tag"></i>&nbsp;&nbsp;子&nbsp;&nbsp;作&nbsp;&nbsp;&nbsp;业&nbsp;&nbsp;&nbsp;&nbsp;</label>
                        <div class="col-md-10">
                            <a data-toggle="modal" href="#jobModal" onclick="subJob.add()" class="btn btn-sm m-t-10">添加子作业</a>
                            <ul id="subJobDiv" class="subJobUl"></ul>
                        </div>
                    </span>
                </div>
                <br>

                <div class="form-group" id="runModel" style="display:none">
                    <label class="col-lab control-label wid150"><i class="glyphicon  glyphicon-sort-by-attributes"></i>&nbsp;&nbsp;运行顺序&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label>
                    <div class="col-md-10">
                        <label for="runModel0" class="radio-label"><input type="radio" name="runModel" value="0"
                                                                          id="runModel0"
                                                                          checked>串行&nbsp;&nbsp;&nbsp;</label>
                        <label for="runModel1" class="radio-label"><input type="radio" name="runModel" value="1"
                                                                          id="runModel1">并行</label>&nbsp;&nbsp;&nbsp;
                        <br><span class="tips" id="runModelTip">串行: 流程任务里的每个任务按照定义的顺序依次执行</span>
                    </div>
                </div>
                <br>

                <div class="form-group">
                    <label class="col-lab control-label wid150"><i class="glyphicon glyphicon-warning-sign"></i>&nbsp;&nbsp;失败报警&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label>
                    <div class="col-md-10">
                        <label for="warning1" class="radio-label"><input type="radio" name="warning" value="1"
                                                                         id="warning1"
                                                                         checked>是&nbsp;&nbsp;&nbsp;</label>
                        <label for="warning0" class="radio-label"><input type="radio" name="warning" value="0"
                                                                         id="warning0">否</label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        </br><span class="tips" tip="任务执行失败时是否发信息报警">任务执行失败时是否发信息报警</span>
                    </div>
                </div>
                <br>

                <div class="form-group contact">
                    <label for="mobiles" class="col-lab control-label wid150"><i
                            class="glyphicon glyphicon-comment"></i>&nbsp;&nbsp;报警手机&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label>
                    <div class="col-md-10">
                        <input type="text" class="form-control input-sm" id="mobiles" name="mobiles">
                        <span class="tips" tip="任务执行失败时将发送短信给此手机,多个请以逗号(英文)隔开">任务执行失败时将发送短信给此手机,多个请以逗号(英文)隔开</span>
                    </div>
                </div>
                <br>

                <div class="form-group contact">
                    <label for="email" class="col-lab control-label wid150"><i class="glyphicon glyphicon-envelope"></i>&nbsp;&nbsp;报警邮箱&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label>
                    <div class="col-md-10">
                        <input type="text" class="form-control input-sm" id="email" name="emailAddress">
                        <span class="tips" tip="任务执行失败时将发送报告给此邮箱,多个请以逗号(英文)隔开">任务执行失败时将发送报告给此邮箱,多个请以逗号(英文)隔开</span>
                    </div>
                </div>
                <br>

                <div class="form-group">
                    <label for="comment" class="col-lab control-label wid150"><i class="glyphicon glyphicon-magnet"></i>&nbsp;&nbsp;描&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;述&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label>
                    <div class="col-md-10">
                        <textarea class="form-control input-sm" id="comment" name="comment" style="height: 50px;"></textarea>
                    </div>
                </div>
                <br>

                <div class="form-group">
                    <div class="col-md-offset-1 col-md-10">
                        <button type="button" id="save-btn" class="btn btn-sm m-t-10"><i class="icon">&#61717;</i>&nbsp;保存
                        </button>&nbsp;&nbsp;
                        <button type="button" onclick="history.back()" class="btn btn-sm m-t-10"><i class="icon">&#61740;</i>&nbsp;取消
                        </button>
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
                    <button class="close btn-float" data-dismiss="modal" aria-hidden="true"><i class="md md-close"></i>
                    </button>
                    <h4 id="subTitle" action="add" tid="">添加子作业</h4>
                </div>
                <div class="modal-body">
                    <form class="form-horizontal" role="form" id="subForm"><br>

                        <input type="hidden" id="itemRedo" value="1"/>
                        <div class="form-group">
                            <label for="agentId1" class="col-lab control-label wid100" title="要执行此作业的机器名称和IP地址">执&nbsp;&nbsp;行&nbsp;&nbsp;器&nbsp;&nbsp;&nbsp;</label>
                            <div class="col-md-9">
                                <select id="agentId1" name="agentId1" class="form-control m-b-10 ">
                                    <c:forEach var="d" items="${agents}">
                                        <option value="${d.agentId}">${d.ip}&nbsp;(${d.name})</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="jobName1" class="col-lab control-label wid100" title="作业名称必填">作业名称&nbsp;<b>*</b></label>
                            <div class="col-md-9">
                                <input type="text" class="form-control " id="jobName1">
                                <span class="tips" tip="必填项,该作业的名称">必填项,该作业的名称</span>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="cmd1" class="col-lab control-label wid100" title="请采用unix/linux的shell支持的命令">执行命令&nbsp;<b>*</b></label>
                            <div class="col-md-9">
                                <textarea class="form-control" id="cmd1" name="cmd1" style="height:100px;resize:vertical"></textarea>
                                <span class="tips" tip="请采用unix/linux的shell支持的命令">请采用unix/linux的shell支持的命令</span>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="runAs1" class="col-lab control-label wid100">运行身份&nbsp;<b>*</b></label>
                            <div class="col-md-9">
                                <input type="text" class="form-control" id="runAs1" name="runAs1" value="root">
                                <span class="tips" tip="该任务以哪个身份执行(默认是root)">该任务以哪个身份执行(默认是root)</span>
                            </div>
                        </div>
                        <br>

                        <div class="form-group">
                            <label for="successExit1" class="col-lab control-label wid100">成功标识&nbsp;<b>*</b></label>
                            <div class="col-md-9">
                                <input type="text" class="form-control" id="successExit1" name="successExit1" value="0">
                                <span class="tips" tip="自定义作业执行成功的返回标识(默认执行成功是0)">自定义作业执行成功的返回标识(默认执行成功是0)</span>
                            </div>
                        </div>
                        <br>

                        <div class="form-group">
                            <label for="timeout1" class="col-lab control-label wid100">超时时间&nbsp;<b>*</b></label>
                            <div class="col-md-9">
                                <input type="text" class="form-control" id="timeout1" value="0">
                                <span class="tips" tip="执行作业允许的最大时间,超过则为超时(0:忽略超时时间,分钟为单位)">执行作业允许的最大时间,超过则为超时(0:忽略超时时间,分钟为单位)</span>
                            </div>
                        </div>
                        <br>

                        <div class="form-group">
                            <label class="col-lab control-label wid100" title="执行失败时是否自动重新执行">失败重跑&nbsp;&nbsp;&nbsp;</label>&nbsp;&nbsp;
                            <label for="redo1" class="radio-label"><input type="radio" name="itemRedo" id="redo1" checked> 是&nbsp;&nbsp;&nbsp;</label>
                            <label for="redo0" class="radio-label"><input type="radio" name="itemRedo" id="redo0">否</label><br>
                        </div>
                        <br>
                        <div class="form-group countDiv1">
                            <label for="runCount1" class="col-lab control-label wid100" title="执行失败时自动重新执行的截止次数">重跑次数&nbsp;&nbsp;&nbsp;</label>&nbsp;&nbsp;
                            <div class="col-md-9">
                                <input type="text" class="form-control " id="runCount1"/>
                                <span class="tips" tip="执行失败时自动重新执行的截止次数">执行失败时自动重新执行的截止次数</span>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="comment1" class="col-lab control-label wid100" title="此作业内容的描述">描&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;述&nbsp;&nbsp;&nbsp;</label>&nbsp;&nbsp;
                            <div class="col-md-9">
                                <input type="text" class="form-control " id="comment1"/>&nbsp;
                            </div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <center>
                        <button type="button" class="btn btn-sm" id="subjob-btn">保存</button>&nbsp;&nbsp;
                        <button type="button" class="btn btn-sm" data-dismiss="modal">关闭</button>
                    </center>
                </div>
            </div>
        </div>
    </div>

</section>

</body>

</html>
