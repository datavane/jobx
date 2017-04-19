<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="cron" uri="http://www.opencron.org" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/common/resource.jsp"/>

    <script type="text/javascript">
        function showContact() {
            $(".contact").show()
        }
        function hideContact() {
            $(".contact").hide()
        }
        function showProxy() {
            $(".proxy").show();
            $("#proxy1").prop("checked", true);
            $("#proxy").val(1);
            $("#proxy1").parent().removeClass("checked").addClass("checked");
            $("#proxy1").parent().attr("aria-checked", true);
            $("#proxy1").parent().prop("onclick", "showContact()");
            $("#proxy0").parent().removeClass("checked");
            $("#proxy0").parent().attr("aria-checked", false);
        }
        function hideProxy() {
            $(".proxy").hide();
            $("#proxy").val(0);
            $("#proxy0").prop("checked", true);
            $("#proxy0").parent().removeClass("checked").addClass("checked");
            $("#proxy0").parent().attr("aria-checked", true);
            $("#proxy1").parent().removeClass("checked");
            $("#proxy1").parent().attr("aria-checked", false);
        }

        $(document).ready(function () {
            $("#size").change(function () {
                var pageSize = $("#size").val();
                window.location.href = "${contextPath}/agent/view?pageSize=" + pageSize+"&csrf=${csrf}";
            });

            setInterval(function () {

                $("#highlight").fadeOut(8000, function () {
                    $(this).show();
                });

                $.ajax({
                    headers:{"csrf":"${csrf}"},
                    type: "POST",
                    url: "${contextPath}/agent/view",
                    data: {
                        "refresh": 1,
                        "pageNo":${pageBean.pageNo},
                        "pageSize":${pageBean.pageSize},
                        "order":"${pageBean.order}",
                        "orderBy":"${pageBean.orderBy}"
                    },
                    dataType: "html",
                    success: function (data) {
                        //解决子页面登录失联,不能跳到登录页面的bug
                        if (data.indexOf("login") > -1) {
                            window.location.href = "${contextPath}";
                        } else {
                            $("#tableContent").html(data);
                        }
                    }
                });
            }, 1000 * 10);

            $("#name").focus(function () {
                $("#checkName").html("");
            });

            $("#pwd0").focus(function () {
                $("#oldpwd").html("");
            });

            $("#pwd2").focus(function () {
                $("#checkpwd").html("");
            });

            $("#name").blur(function () {
                if (!$("#name").val()) {
                    $("#checkName").html("<font color='red'>" + '<i class="glyphicon glyphicon-remove-sign"></i>&nbsp;请填写执行器名' + "</font>");
                    return false;
                }
                $.ajax({
                    headers:{"csrf":"${csrf}"},
                    type: "POST",
                    url: "${contextPath}/agent/checkname",
                    data: {
                        "id": $("#id").val(),
                        "name": $("#name").val()
                    },
                    success: function (data) {
                        if (data == "yes") {
                            $("#checkName").html("<font color='green'>" + '<i class="glyphicon glyphicon-ok-sign"></i>&nbsp;执行器名可用' + "</font>");
                            return false;
                        } else {
                            $("#checkName").html("<font color='red'>" + '<i class="glyphicon glyphicon-remove-sign"></i>&nbsp;执行器名已存在' + "</font>");
                            return false;
                        }
                    },
                    error: function () {
                        alert("网络繁忙请刷新页面重试!");
                        return false;
                    }
                });
            });

            $("#pwd0").blur(function () {
                if (!$("#pwd0").val()) {
                    $("#oldpwd").html("<font color='red'>" + '<i class="glyphicon glyphicon-remove-sign"></i>&nbsp;请输入原密码' + "</font>");
                }
            });

            $("#pwd2").change(function () {
                if ($("#pwd1").val() == $("#pwd2").val()) {
                    $("#checkpwd").html("<font color='green'>" + '<i class="glyphicon glyphicon-ok-sign"></i>&nbsp;两密码一致' + "</font>");
                } else {
                    $("#checkpwd").html("<font color='red'>" + '<i class="glyphicon glyphicon-remove-sign"></i>&nbsp;密码不一致' + "</font>");
                }
            });

            $("#proxy1").attr("onclick", "showProxy()").next().attr("onclick", "showProxy()");
            $("#proxy0").attr("onclick", "hideProxy()").next().attr("onclick", "hideProxy()");

        });

        function edit(id) {
            $.ajax({
                headers:{"csrf":"${csrf}"},
                type: "POST",
                url: "${contextPath}/agent/editpage",
                data: {"id": id},
                success: function (obj) {
                    $("#agentform")[0].reset();
                    if (obj != null) {
                        $("#checkName").html("");
                        $("#pingResult").html("");
                        $("#id").val(obj.agentId);
                        $("#password").val(obj.password);
                        if (obj.status == true) {
                            $("#status").val("1");
                        } else {
                            $("#status").val("0");
                        }
                        $("#name").val(obj.name);
                        $("#ip").val(obj.ip);
                        $("#port").val(obj.port);
                        if (obj.proxy == 1) {
                            showProxy();
                            $("#agent_" + obj.agentId).attr("selected", true);
                        } else {
                            hideProxy();
                        }

                        $("#warning1").next().attr("onclick", "showContact()");
                        $("#warning0").next().attr("onclick", "hideContact()");
                        if (obj.warning == true) {
                            showContact();
                            $("#warning1").prop("checked", true);
                            $("#warning1").parent().removeClass("checked").addClass("checked");
                            $("#warning1").parent().attr("aria-checked", true);
                            $("#warning1").parent().prop("onclick", "showContact()");
                            $("#warning0").parent().removeClass("checked");
                            $("#warning0").parent().attr("aria-checked", false);
                        } else {
                            hideContact();
                            $("#warning0").prop("checked", true);
                            $("#warning0").parent().removeClass("checked").addClass("checked");
                            $("#warning0").parent().attr("aria-checked", true);
                            $("#warning1").parent().removeClass("checked");
                            $("#warning1").parent().attr("aria-checked", false);
                        }
                        $("#mobiles").val(obj.mobiles);
                        $("#email").val(obj.emailAddress);
                        $("#comment").val(obj.comment);
                        $("#agentModal").modal("show");
                        return;
                    }
                },
                error: function () {
                    alert("网络繁忙请刷新页面重试!");
                }
            });
        }

        function save() {
            var id = $("#id").val();
            if (!id) {
                alert("页面异常，请刷新重试！");
                return false;
            }
            var password = $("#password").val();
            if (!password) {
                alert("页面异常，请刷新重试！");
                return false;
            }
            var name = $("#name").val();
            if (!name) {
                alert("请填写执行器名称!");
                return false;
            }
            var proxy = $('input[type="radio"][name="proxy"]:checked').val();
            if (!proxy) {
                alert("页面异常，请刷新重试!");
                return false;
            }
            var ip = $("#ip").val();
            if (!ip) {
                alert("请填写机器IP!");
                return false;
            }
            if (!opencron.testIp(ip)) {
                alert("请填写正确的IP地址!");
                return false;
            }
            var port = $("#port").val();
            if (!port) {
                alert("请填写端口号!");
                return false;
            }
            if (!opencron.testPort(port)) {
                alert("请填写正确的端口号!");
                return false;
            }
            var warning = $('input[type="radio"][name="warning"]:checked').val();
            if (!warning) {
                alert("页面错误，请刷新重试!");
                return false;
            }
            if (warning == 1) {
                var mobiles = $("#mobiles").val();
                if (!mobiles) {
                    alert("请填写手机号码!");
                    return false;
                }
                if (!opencron.testMobile(mobiles)) {
                    alert("请填写正确的手机号码!");
                    return false;
                }
                var email = $("#email").val();
                if (!email) {
                    alert("请填写邮箱地址!");
                    return false;
                }
                if (!opencron.testEmail(email)) {
                    alert("请填写正确的邮箱地址!");
                    return false;
                }
            }
            var status = $("#status").val();
            if (!status) {
                alert("页面异常，请刷新重试！");
                return false;
            }
            $.ajax({
                headers:{"csrf":"${csrf}"},
                type: "POST",
                url: "${contextPath}/agent/checkname",
                data: {
                    "id": id,
                    "name": name
                },
                success: function (data) {
                    if (data == "yes") {
                        if (status == 1) {
                            $.ajax({
                                headers:{"csrf":"${csrf}"},
                                type: "POST",
                                url: "${contextPath}/verify/ping",
                                data: {
                                    headers:{"csrf":"${csrf}"},
                                    "proxy": proxy,
                                    "proxyId": $("#proxyAgent").val(),
                                    "ip": ip,
                                    "port": port,
                                    "password": password
                                },
                                success: function (data) {
                                    if (data == "success") {
                                        canSave(proxy, id, name, port, warning, mobiles, email);
                                        return false;
                                    } else {
                                        alert("通信失败!请检查IP和端口号");
                                    }
                                },
                                error: function () {
                                    alert("网络繁忙请刷新页面重试!");
                                }
                            });
                        } else {
                            canSave(proxy, id, name, port, warning, mobiles, email);
                            return false;
                        }
                    } else {
                        alert("用户已存在!");
                        return false;
                    }
                },
                error: function () {
                    alert("网络繁忙请刷新页面重试!");
                    return false;
                }
            });
        }

        function canSave(proxy, id, name, port, warning, mobiles, email) {
            $.ajax({
                headers:{"csrf":"${csrf}"},
                type: "POST",
                url: "${contextPath}/agent/edit",
                data: {
                    "proxy": proxy,
                    "proxyAgent": $("#proxyAgent").val(),
                    "agentId": id,
                    "name": name,
                    "port": port,
                    "warning": warning,
                    "mobiles": mobiles,
                    "emailAddress": email,
                    "comment":$("#comment").val()
                },
                success: function (data) {
                    if (data == "success") {
                        $('#agentModal').modal('hide');
                        alertMsg("修改成功");
                        $("#name_" + id).html(escapeHtml(name));
                        $("#port_" + id).html(port);
                        if (warning == "0") {
                            $("#warning_" + id).html('<span class="label label-default" style="color: red;font-weight:bold">&nbsp;&nbsp;否&nbsp;&nbsp;</span>');
                        } else {
                            $("#warning_" + id).html('<span class="label label-warning" style="color: white;font-weight:bold">&nbsp;&nbsp;是&nbsp;&nbsp;</span>');
                        }
                        if (proxy == "0") {
                            $("#connType_" + id).html("直连");
                        } else {
                            $("#connType_" + id).html("代理");
                        }
                        flushConnAgents();
                        return false;
                    } else {
                        alert("修改失败");
                    }
                },
                error: function () {
                    alert("网络繁忙请刷新页面重试!");
                    return false;
                }
            });
        }

        function flushConnAgents() {
            $.ajax({
                headers:{"csrf":"${csrf}"},
                type: "POST",
                url: "${contextPath}/agent/getConnAgents",
                success: function (obj) {
                    if (obj != null) {
                        $("#proxyAgent").empty();
                        for (var i in obj) {
                            $("#proxyAgent").append('<option value="' + obj[i].agentId + '" id="agent_' + obj[i].agentId + '">' + obj[i].ip + ' (' + obj[i].name + ')</option>');
                        }
                    }
                }
            });
        }

        function editPwd(id) {
            $.ajax({
                headers:{"csrf":"${csrf}"},
                type: "POST",
                url: "${contextPath}/agent/pwdpage",
                data: {"id": id},
                success: function (obj) {
                    $("#pwdform")[0].reset();
                    if (obj != null) {
                        $("#oldpwd").html("");
                        $("#checkpwd").html("");
                        $("#agentId").val(obj.agentId);
                        $("#pwdModal").modal("show");
                        return;
                    }
                },
                error: function () {
                    alert("网络繁忙请刷新页面重试!");
                }
            });
        }

        function savePwd() {
            var id = $("#agentId").val();
            if (!id) {
                alert("页面异常，请刷新重试!");
                return false;
            }
            var pwd0 = $("#pwd0").val();
            if (!pwd0) {
                alert("请填原密码!");
                return false;
            }
            var pwd1 = $("#pwd1").val();
            if (!pwd1) {
                alert("请填新密码!");
                return false;
            }
            var pwd2 = $("#pwd2").val();
            if (!pwd2) {
                alert("请填写确认密码!");
                return false;
            }
            if (pwd1 != pwd2) {
                alert("两密码不一致!");
                return false;
            }
            $.ajax({
                headers:{"csrf":"${csrf}"},
                type: "POST",
                url: "${contextPath}/agent/editpwd",
                data: {
                    "id": id,
                    "pwd0": pwd0,
                    "pwd1": pwd1,
                    "pwd2": pwd2
                },
                success: function (data) {
                    if (data == "success") {
                        $('#pwdModal').modal('hide');
                        alertMsg("修改成功");
                        return false;
                    }
                    if (data == "failure") {
                        alert("Client密码存在异常!");
                        return false;
                    }
                    if (data == "one") {
                        $("#oldpwd").html("<font color='red'>" + '<i class="glyphicon glyphicon-remove-sign"></i>&nbsp;密码不正确' + "</font>");
                        return false;
                    }
                    if (data == "two") {
                        $("#checkpwd").html("<font color='red'>" + '<i class="glyphicon glyphicon-remove-sign"></i>&nbsp;密码不一致' + "</font>");
                        return false;
                    }

                },
                error: function () {
                    alert("网络繁忙请刷新页面重试!");
                    return false;
                }
            });
        }

        function pingCheck() {

            var ip = $("#ip").val();
            if (!ip) {
                alert("请填写机器IP!");
                return false;
            }
            var proxy = $('input[type="radio"][name="proxy"]:checked').val();
            if (!proxy) {
                alert("页面异常，请刷新重试!");
                return false;
            }
            var password = $("#password").val();
            if (!password) {
                alert("页面异常，请刷新重试！");
                return false;
            }
            if (!opencron.testIp(ip)) {
                alert("请填写正确的IP地址!");
                return false;
            }
            var port = $("#port").val();
            if (!port) {
                alert("请填写端口号!");
                return false;
            }
            if (!opencron.testPort(port)) {
                alert("请填写正确的端口号!");
                return false;
            }

            $("#pingResult").html("<img src='${contextPath}/img/icon-loader.gif'> <font color='#2fa4e7'>检测中...</font>");

            $.ajax({
                headers:{"csrf":"${csrf}"},
                type: "POST",
                url: "${contextPath}/verify/ping",
                data: {
                    "proxy": proxy,
                    "proxyId": $("#proxyAgent").val(),
                    "ip": ip,
                    "port": port,
                    "password": password
                },
                success: function (data) {
                    if (data == "success") {
                        $("#pingResult").html("<font color='green'>" + '<i class="glyphicon glyphicon-ok-sign"></i>&nbsp;通信正常' + "</font>");
                        return;
                    } else {
                        $("#pingResult").html("<font color='red'>" + '<i class="glyphicon glyphicon-remove-sign"></i>&nbsp;通信失败' + "</font>");
                        return;
                    }
                },
                error: function () {
                    alert("网络繁忙请刷新页面重试!");
                }
            });
        }

        function sortPage(field) {
            location.href="${contextPath}/agent/view?pageNo=${pageBean.pageNo}&pageSize=${pageBean.pageSize}&orderBy="+field+"&order="+("${pageBean.order}"=="asc"?"desc":"asc")+"&csrf=${csrf}";
        }

    </script>

    <style type="text/css">
        .visible-md i {
            font-size: 15px;
        }
    </style>

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
    </ol>
    <h4 class="page-title"><i class="fa fa-desktop" aria-hidden="true"></i>&nbsp;执行器管理&nbsp;&nbsp;<span id="highlight"
                                                                                                        style="font-size: 14px"><img
            src='${contextPath}/img/icon-loader.gif' style="width: 14px;height: 14px">&nbsp;通信监测持续进行中...</span></h4>
    <div class="block-area" id="defaultStyle">
        <div>
            <div style="float: left">
                <label>
                    每页 <select size="1" class="select-self" id="size" style="width: 50px;margin-bottom: 8px">
                    <option value="15">15</option>
                    <option value="30" ${pageBean.pageSize eq 30 ? 'selected' : ''}>30</option>
                    <option value="50" ${pageBean.pageSize eq 50 ? 'selected' : ''}>50</option>
                    <option value="100" ${pageBean.pageSize eq 100 ? 'selected' : ''}>100</option>
                </select> 条记录
                </label>
            </div>
            <c:if test="${permission eq true}">
                <div style="float: right;margin-top: -10px">
                    <a href="${contextPath}/agent/addpage?csrf=${csrf}" class="btn btn-sm m-t-10"
                       style="margin-left: 50px;margin-bottom: 8px"><i class="icon">&#61943;</i>添加</a>
                </div>
            </c:if>
        </div>

        <table class="table tile textured table-custom table-sortable">
            <thead>
            <tr>
                <c:choose>
                    <c:when test="${pageBean.orderBy eq 'name'}">
                        <c:if test="${pageBean.order eq 'asc'}">
                            <th  class="sortable sort-alpha sort-asc" style="cursor: pointer" onclick="sortPage('name')" title="点击排序">执行器</th>
                        </c:if>
                        <c:if test="${pageBean.order eq 'desc'}">
                            <th  class="sortable sort-alpha sort-desc" style="cursor: pointer" onclick="sortPage('name')" title="点击排序">执行器</th>
                        </c:if>
                    </c:when>
                    <c:when test="${pageBean.orderBy ne 'name'}">
                        <th  class="sortable sort-alpha" style="cursor: pointer" onclick="sortPage('name')" title="点击排序">执行器</th>
                    </c:when>
                </c:choose>

                <c:choose>
                    <c:when test="${pageBean.orderBy eq 'ip'}">
                        <c:if test="${pageBean.order eq 'asc'}">
                            <th  class="sortable sort-numeric sort-asc" style="cursor: pointer" onclick="sortPage('ip')" title="点击排序">ip</th>
                        </c:if>
                        <c:if test="${pageBean.order eq 'desc'}">
                            <th  class="sortable sort-numeric sort-desc" style="cursor: pointer" onclick="sortPage('ip')" title="点击排序">ip</th>
                        </c:if>
                    </c:when>
                    <c:when test="${pageBean.orderBy ne 'ip'}">
                        <th  class="sortable sort-numeric" style="cursor: pointer" onclick="sortPage('ip')" title="点击排序">ip</th>
                    </c:when>
                </c:choose>
                <c:choose>
                    <c:when test="${pageBean.orderBy eq 'port'}">
                        <c:if test="${pageBean.order eq 'asc'}">
                            <th  class="sortable sort-numeric sort-asc" style="cursor: pointer" onclick="sortPage('port')" title="点击排序">SSH端口</th>
                        </c:if>
                        <c:if test="${pageBean.order eq 'desc'}">
                            <th  class="sortable sort-numeric sort-desc" style="cursor: pointer" onclick="sortPage('port')" title="点击排序">SSH端口</th>
                        </c:if>
                    </c:when>
                    <c:when test="${pageBean.orderBy ne 'port'}">
                        <th  class="sortable sort-numeric" style="cursor: pointer" onclick="sortPage('port')" title="点击排序">SSH端口</th>
                    </c:when>
                </c:choose>
                <th>通信状态</th>
                <th>失联报警</th>
                <th>连接类型</th>
                <th>
                    <center>操作</center>
                </th>
            </tr>
            </thead>

            <tbody id="tableContent">

            <c:forEach var="w" items="${pageBean.result}" varStatus="index">
                <tr>
                    <td id="name_${w.agentId}">${w.name}</td>
                    <td>${w.ip}</td>
                    <td id="port_${w.agentId}">${w.port}</td>
                    <td>
                        <c:if test="${w.status eq false}">
                            <span class="label label-danger">&nbsp;&nbsp;失&nbsp;败&nbsp;&nbsp;</span>
                        </c:if>
                        <c:if test="${w.status eq true}">
                            <span class="label label-success">&nbsp;&nbsp;成&nbsp;功&nbsp;&nbsp;</span>
                        </c:if>
                    </td>
                    <td id="warning_${w.agentId}">
                        <c:if test="${w.warning eq false}"><span class="label label-default"
                                                                 style="color: red;font-weight:bold">&nbsp;&nbsp;否&nbsp;&nbsp;</span> </c:if>
                        <c:if test="${w.warning eq true}"><span class="label label-warning"
                                                                style="color: white;font-weight:bold">&nbsp;&nbsp;是&nbsp;&nbsp;</span> </c:if>
                    </td>
                    <td id="connType_${w.agentId}">
                        <c:if test="${w.proxy eq 0}">直连</c:if>
                        <c:if test="${w.proxy eq 1}">代理</c:if>
                    </td>
                    <td>
                        <center>
                            <div class="visible-md visible-lg hidden-sm hidden-xs action-buttons">

                                <a href="${contextPath}/job/addpage?id=${w.agentId}&csrf=${csrf}" title="新任务">
                                    <i aria-hidden="true" class="fa fa-plus-square-o"></i>
                                </a>&nbsp;&nbsp;
                                <c:if test="${permission eq true}">
                                    <a href="#" onclick="edit('${w.agentId}')" title="编辑">
                                        <i aria-hidden="true" class="fa fa-edit"></i>
                                    </a>&nbsp;&nbsp;
                                    <a href="#" onclick="editPwd('${w.agentId}')" title="修改密码">
                                        <i aria-hidden="true" class="fa fa-lock"></i>
                                    </a>&nbsp;&nbsp;
                                </c:if>
                                <a href="${contextPath}/agent/detail?id=${w.agentId}&csrf=${csrf}" title="查看详情">
                                    <i aria-hidden="true" class="fa fa-eye"></i>
                                </a>
                            </div>
                        </center>
                    </td>
                </tr>
            </c:forEach>

            </tbody>
        </table>

        <cron:pager href="${contextPath}/agent/view?csrf=${csrf}" id="${pageBean.pageNo}" size="${pageBean.pageSize}"
                   total="${pageBean.totalCount}"/>

    </div>

    <!-- 修改执行器弹窗 -->
    <div class="modal fade" id="agentModal" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4>修改执行器</h4>
                </div>
                <div class="modal-body">
                    <form class="form-horizontal" role="form" id="agentform">
                        <input type="hidden" name="csrf" value="${csrf}">
                        <input type="hidden" id="id" name="id"><input type="hidden" id="password" name="password"><input
                            type="hidden" id="status" name="status">
                        <div class="form-group" style="margin-bottom: 4px;">
                            <label for="ip" class="col-lab control-label" title="执行器IP地址只能为点分十进制方式表示">机&nbsp;&nbsp;器&nbsp;&nbsp;IP：</label>
                            <div class="col-md-9">
                                <input type="text" class="form-control " id="ip" readonly>&nbsp;
                            </div>
                        </div>

                        <div class="form-group" style="">
                            <label for="name" class="col-lab control-label" title="执行器名称必填">执行器名：</label>
                            <div class="col-md-9">
                                <input type="text" class="form-control " id="name">&nbsp;&nbsp;<label
                                    id="checkName"></label>
                            </div>
                        </div>


                        <div class="form-group">
                            <label class="col-lab control-label" title="执行器通信不正常时是否发信息报警">连接类型：</label>&nbsp;&nbsp;
                            <label onclick="hideProxy()" for="proxy0" class="radio-label"><input type="radio"
                                                                                                 onclick="hideProxy()"
                                                                                                 name="proxy" value="0"
                                                                                                 id="proxy0">直连</label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            <label onclick="showProxy()" for="proxy1" class="radio-label"><input type="radio"
                                                                                                 onclick="showProxy()"
                                                                                                 name="proxy" value="1"
                                                                                                 id="proxy1">代理&nbsp;&nbsp;&nbsp;
                            </label>
                        </div>

                        <div class="form-group proxy" style="display: none;margin-top: 20px;">
                            <label for="proxyAgent" class="col-lab control-label">代&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;理：</label>
                            <div class="col-md-9">
                                <select id="proxyAgent" name="proxyAgent" class="form-control">
                                    <c:forEach var="d" items="${connAgents}">
                                        <option value="${d.agentId}" id="agent_${d.agentId}">${d.ip}&nbsp;(${d.name})
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        <br>

                        <div class="form-group">
                            <label for="port" class="col-lab control-label" title="执行器端口号只能是数字,且不超过4位">端&nbsp;&nbsp;口&nbsp;&nbsp;号：</label>
                            <div class="col-md-9">
                                <input type="text" class="form-control " id="port" style="margin-bottom: 5px;"/>&nbsp;&nbsp;<a
                                    href="#" onclick="pingCheck()">
                                <i class="glyphicon glyphicon-signal"></i>&nbsp;检测通信</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<label
                                    id="pingResult"></label>
                            </div>
                        </div>
                        <div class="form-group" style="margin-top: 15px;margin-bottom: 20px">
                            <label class="col-lab control-label" title="执行器通信不正常时是否发信息报警">失联报警：</label>&nbsp;&nbsp;
                            <label onclick="showContact()" for="warning1" class="radio-label"><input type="radio"
                                                                                                     name="warning"
                                                                                                     value="1"
                                                                                                     id="warning1">是&nbsp;&nbsp;&nbsp;
                            </label>
                            <label onclick="hideContact()" for="warning0" class="radio-label"><input type="radio"
                                                                                                     name="warning"
                                                                                                     value="0"
                                                                                                     id="warning0">否</label>
                        </div>
                        <div class="form-group contact">
                            <label for="mobiles" class="col-lab control-label" title="执行器通信不正常时将发送短信给此手机">报警手机：</label>
                            <div class="col-md-9">
                                <input type="text" class="form-control " id="mobiles"/>&nbsp;
                            </div>
                        </div>
                        <div class="form-group contact">
                            <label for="email" class="col-lab control-label" title="执行器通信不正常时将发送报告给此邮箱">报警邮箱：</label>
                            <div class="col-md-9">
                                <input type="text" class="form-control " id="email"/>&nbsp;
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="comment" class="col-lab control-label" title="次执行器描述信息">描述信息：</label>
                            <div class="col-md-9">
                                <input type="text" class="form-control " id="comment"/>&nbsp;
                            </div>
                        </div>

                    </form>
                </div>
                <div class="modal-footer">
                    <center>
                        <button type="button" class="btn btn-sm" onclick="save()">保存</button>
                        &nbsp;&nbsp;
                        <button type="button" class="btn btn-sm" data-dismiss="modal">关闭</button>
                    </center>
                </div>
            </div>
        </div>
    </div>

    <!-- 修改密码弹窗 -->
    <div class="modal fade" id="pwdModal" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4>修改密码</h4>
                </div>
                <div class="modal-body">
                    <form class="form-horizontal" role="form" id="pwdform">
                        <input type="hidden" id="agentId">
                        <div class="form-group" style="margin-bottom: 4px;">
                            <label for="pwd0" class="col-lab control-label"><i class="glyphicon glyphicon-lock"></i>&nbsp;&nbsp;原&nbsp;&nbsp;密&nbsp;&nbsp;码：</label>
                            <div class="col-md-9">
                                <input type="password" class="form-control " id="pwd0" placeholder="请输入原密码">&nbsp;&nbsp;<label
                                    id="oldpwd"></label>
                            </div>
                        </div>
                        <div class="form-group" style="margin-bottom: 20px;">
                            <label for="pwd1" class="col-lab control-label"><i class="glyphicon glyphicon-lock"></i>&nbsp;&nbsp;新&nbsp;&nbsp;密&nbsp;&nbsp;码：</label>
                            <div class="col-md-9">
                                <input type="password" class="form-control " id="pwd1" placeholder="请输入新密码">
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="pwd2" class="col-lab control-label"><i class="glyphicon glyphicon-lock"></i>&nbsp;&nbsp;确认密码：</label>
                            <div class="col-md-9">
                                <input type="password" class="form-control " id="pwd2" placeholder="请输入确认密码"/>&nbsp;&nbsp;<label
                                    id="checkpwd"></label>
                            </div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <center>
                        <button type="button" class="btn btn-sm" onclick="savePwd()">保存</button>
                        &nbsp;&nbsp;
                        <button type="button" class="btn btn-sm" data-dismiss="modal">关闭</button>
                    </center>
                </div>
            </div>
        </div>
    </div>

</section>
<br/><br/>

<jsp:include page="/WEB-INF/common/footer.jsp"/>
