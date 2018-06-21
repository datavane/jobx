<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="cron" uri="http://www.jobx.org" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <script type="text/javascript" src="${contextPath}/static/js/clipboard.js?resId=${resourceId}"></script> <!-- jQuery Library -->

    <script type="text/javascript" src="${contextPath}/static/js/ztree/jquery.ztree.core.min.js?resId=${resourceId}"></script> <!-- jQuery Library -->
    <link rel="stylesheet" href="${contextPath}/static/js/ztree/css/zTreeStyle/zTreeStyle.css" type="text/css">

    <script type="text/javascript">
        var toggle = {
            contact:{
                show:function () {
                    $(".contact").show()
                },
                hide:function () {
                    $(".contact").hide()
                }
            },
            proxy:{
                show:function () {
                    $(".proxy").show();
                    $("#proxy1").prop("checked", true);
                    $("#proxy").val(1);
                    $("#proxy1").parent().removeClass("checked").addClass("checked");
                    $("#proxy1").parent().attr("aria-checked", true);
                    $("#proxy1").parent().bind("click",toggle.contact.show);
                    $("#proxy0").parent().removeClass("checked");
                    $("#proxy0").parent().attr("aria-checked", false);
                },
                hide:function () {
                    $(".proxy").hide();
                    $("#proxy").val(0);
                    $("#proxy0").prop("checked", true);
                    $("#proxy0").parent().removeClass("checked").addClass("checked");
                    $("#proxy0").parent().attr("aria-checked", true);
                    $("#proxy1").parent().removeClass("checked");
                    $("#proxy1").parent().attr("aria-checked", false);
                }
            }

        }

        $(document).ready(function () {

            $("#size").change(function () {
                doUrl()
            });
            $("#agentName").change(function () {
                doUrl()
            });
            $("#agentStatus").change(function () {
                doUrl()
            });

            new Clipboard('#copy-btn').on('success', function(e) {
                e.clearSelection();
                $("#copy-btn").text("已复制");
                setTimeout(function () {
                    $("#copy-btn").text("复制");
                },2000);
            });

            var interId = setInterval(function () {

                $("#highlight").fadeOut(8000, function () {
                    $(this).show();
                });

                ajax({
                    type: "post",
                    url: "${contextPath}/agent/refresh.htm",
                    data: {
                        "pageNo":${pageBean.pageNo},
                        "pageSize":${pageBean.pageSize},
                        "order":"${pageBean.order}",
                        "orderBy":"${pageBean.orderBy}"
                    },
                    dataType:'html'
                },function (data) {
                    //解决子页面登录失联,不能跳到登录页面的bug
                    if (data.indexOf("login") > -1|| data.indexOf("<body") > -1 ) {
                        clearInterval(interId);
                        window.location.href = "${contextPath}";
                    } else {
                        $("#tableContent").html(data);
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
                ajax({
                    type: "post",
                    url: "${contextPath}/agent/checkname.do",
                    data: {
                        "id": $("#id").val(),
                        "name": $("#name").val()
                    }
                },function (data) {
                    if (data.status) {
                        $("#checkName").html("<font color='green'>" + '<i class="glyphicon glyphicon-ok-sign"></i>&nbsp;执行器名可用' + "</font>");
                        return false;
                    } else {
                        $("#checkName").html("<font color='red'>" + '<i class="glyphicon glyphicon-remove-sign"></i>&nbsp;执行器名已存在' + "</font>");
                        return false;
                    }
                })
            });

            $("#pwd0").blur(function () {
                if (!$("#pwd0").val()) {
                    $("#oldpwd").html("<font color='red'>" + '<i class="glyphicon glyphicon-remove-sign"></i>&nbsp;请输入'+(window.errorAgentPwd>=3?"密文":"原密码")+"</font>");
                }
            });

            $("#pwd2").change(function () {
                if ($("#pwd1").val() == $("#pwd2").val()) {
                    $("#checkpwd").html("<font color='green'>" + '<i class="glyphicon glyphicon-ok-sign"></i>&nbsp;两密码一致' + "</font>");
                } else {
                    $("#checkpwd").html("<font color='red'>" + '<i class="glyphicon glyphicon-remove-sign"></i>&nbsp;密码不一致' + "</font>");
                }
            });

            $("#proxy1").bind("click",toggle.proxy.show).next().bind("click",toggle.proxy.show);
            $("#proxy0").bind("click",toggle.proxy.hide).next().bind("click",toggle.proxy.hide);

        });

        function doUrl() {
            var pageSize = $("#size").val()||${pageBean.pageSize};
            var agentName = $("#agentName").val().trim();
            var status = $("#agentStatus").val();
            window.location.href = "${contextPath}/agent/view.htm?pageSize=" + pageSize + "&name=" + agentName + "&status=" + status;
        }

        function upload(agentId) {
            if (!$(".pong_"+agentId).length) {
                alert("执行器失联,请检查执行器连接");
                return;
            }
            $("#fileId").val(agentId);
            $("#fileTitle").text("文件上传");
            $(".file-progress").hide();
            $("#upform")[0].reset();
            $("#upfileModal").modal("show");
            jobx.tipDefault("#savePath");
            jobx.tipDefault("#upfile");

            $(".treePath").hide();
            $("#filebtn").click(function () {
                $(".treePath").hide();
                var ok = true;
                var savePath = $("#savePath").val();
                if (savePath.length == 0) {
                    jobx.tipError("#savePath","目标保存路径不能为空");
                    ok = false;
                }else {
                    jobx.tipOk("#savePath");
                }

                var file = $('input[id=upfile]')[0].files[0];
                if (!file) {
                    jobx.tipError("#upfile","上传文件不能为空");
                    ok = false;
                }else {
                    jobx.tipOk("#upfile");
                }

                if (!ok) return;

                var formData = new FormData();
                formData.append("agentId",agentId);
                formData.append("savePath",savePath);
                formData.append("file",file);
                formData.append("postcmd",$("#postcmd").val());
                $("#fileTitle").text("上传中");
                $(".file-progress").show();

                $.ajax({
                    url: "${contextPath}/agent/upload.do",
                    type: "post",
                    data: formData,
                    processData: false,
                    contentType:false,
                    dataType:"JSON",
                    success:function (data) {
                        if (data.status) {
                            $("#upfileModal").modal("hide");
                            alertMsg("上传成功");
                        }else {
                            alert("上传或后续动作失败");
                        }
                    }
                });
            });
        }

        function listfile() {
            $(".treePath").show();
            var agentId = $("#fileId").val();
            var settings = {
                async: {
                    enable: true,
                    url:"${contextPath}/agent/listpath.do",
                    autoParam:["path"],
                    otherParam: ["agentId",agentId],
                    dataType:"json",
                    dataFilter: function (treeId, parentNode, childNodes) {
                        if (!childNodes||!childNodes.status) return null;
                        childNodes = eval("("+childNodes.path+")");
                        for (var i=0, l=childNodes.length; i<l; i++) {
                            childNodes[i].isParent = childNodes[i].isDirectory == "0";
                            childNodes[i].iconSkin = "icon";
                        }
                        return childNodes;
                    }
                },
                callback: {
                    onClick: function (event, treeId, treeNode) {
                        if (treeNode.isDirectory == "0") {
                            $("#savePath").val(treeNode.path)
                        }
                    }
                }
            }
            var rootNode = [
                {
                    name:"/",
                    isParent:true,
                    isDirectory:"0",
                    path:"/",
                    open:true,
                    iconSkin:"icon"
                }
            ];
            $.fn.zTree.init( $("#treePath"),settings,rootNode);
        }

        function edit(id) {
            ajax({
                type: "post",
                url: "${contextPath}/agent/get.do",
                data: {"id": id}
            },function (obj) {
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
                    $("#host").val(obj.host);
                    $("#port").val(obj.port);
                    if (obj.proxyId) {
                        toggle.proxy.show();
                        $("#agent_" + obj.agentId).attr("selected", true);
                    } else {
                        toggle.proxy.hide();
                    }

                    $("#warning1").next().bind("click",toggle.contact.show);
                    $("#warning0").next().bind("click",toggle.contact.hide);
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
                    $("#mobile").val(obj.mobile);
                    $("#email").val(obj.email);
                    $("#comment").val(obj.comment);
                    $("#agentModal").modal("show");

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

            var host = $("#host").val();
            if (!host) {
                alert("请填写机器Host!");
                return false;
            }
            if (!jobx.testIp(host)) {
                alert("请填写正确的IP地址!");
                return false;
            }
            var port = $("#port").val();
            if (!port) {
                alert("请填写端口号!");
                return false;
            }
            if (!jobx.testPort(port)) {
                alert("请填写正确的端口号!");
                return false;
            }
            var warning = $('input[type="radio"][name="warning"]:checked').val();
            if (!warning) {
                alert("页面错误，请刷新重试!");
                return false;
            }
            if (warning == 1) {
                var mobile = $("#mobile").val();
                if (!mobile) {
                    alert("请填写手机号码!");
                    return false;
                }
                if (!jobx.testMobile(mobile)) {
                    alert("请填写正确的手机号码!");
                    return false;
                }
                var email = $("#email").val();
                if (!email) {
                    alert("请填写邮箱地址!");
                    return false;
                }
                if (!jobx.testEmail(email)) {
                    alert("请填写正确的邮箱地址!");
                    return false;
                }
            }
            var status = $("#status").val();
            if (!status) {
                alert("页面异常，请刷新重试！");
                return false;
            }

            ajax({
                type: "post",
                url: "${contextPath}/agent/checkname.do",
                data: {
                    "id": id,
                    "name": name
                }
            },function (data) {
                if (data.status) {
                    if (status == 1) {
                        ajax({
                            type: "post",
                            url: "${contextPath}/verify/ping.do",
                            data: {
                                "proxy":proxy,
                                "proxyId":$("#proxyId").val(),
                                "host": host,
                                "port": port,
                                "password": password
                            }
                        },function (data) {
                            if (data.status == 1) {
                                canSave(proxy, id, name, port, warning, mobile, email);
                                return false;
                            } else if(data.status == 0){
                                alert("通信失败!请检查主机和端口号");
                            }else {
                                alert("密码错误!请确保连接执行器的密码正确");
                            }
                        });
                    } else {
                        canSave(proxy, id, name, port, warning, mobile, email);
                        return false;
                    }
                } else {
                    alert("执行器名称已存在!");
                    return false;
                }
            })
        }

        function canSave(proxy, id, name, port, warning, mobile, email) {
            var loading = new Loading();
            ajax({
                type: "post",
                url: "${contextPath}/agent/edit.do",
                data: {
                    "proxy": proxy,
                    "proxyId": $("#proxyId").val(),
                    "agentId": id,
                    "name": name,
                    "port": port,
                    "warning": warning,
                    "mobile": mobile,
                    "email": email,
                    "comment":$("#comment").val()
                }
            },function (data) {
                loading.exit(function () {
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
                });
            })
        }

        function flushConnAgents() {
            ajax({
                type: "post",
                url: "${contextPath}/agent/getConnAgents.do"
            },function (obj) {
                if (obj != null) {
                    $("#proxyId").empty();
                    for (var i in obj) {
                        $("#proxyId").append('<option value="' + obj[i].agentId + '" id="agent_' + obj[i].agentId + '">' + obj[i].host + ' (' + obj[i].name + ')</option>');
                    }
                }
            })
        }

        function editPwd(id) {
          /*  if (!$(".pong_"+id).length) {
                alert("执行器失联,请检查执行器连接");
                return;
            }*/
            inputPwd();
            ajax({
                type: "post",
                url: "${contextPath}/agent/get.do",
                data: {"id": id}
            },function (obj) {
                $("#pwdform")[0].reset();
                if (obj != null) {
                    $("#oldpwd").html("");
                    $("#checkpwd").html("");
                    $("#agentId").val(obj.agentId);
                    window.errorAgentPwd = 0;
                    $("#pwdModal").modal("show");
                }
            });
        }

        function remove(id) {
            swal({
                title: "",
                text: "确定要删除这个执行器吗？",
                type: "warning",
                showCancelButton: true,
                closeOnConfirm: false,
                confirmButtonText: "删除"
            }, function() {
                ajax({
                    type: "post",
                    url:"${contextPath}/agent/checkdel.do",
                    data:{"id":id}
                },function (data) {
                    if (data.status){
                        ajax({
                            type: "post",
                            url:"${contextPath}/agent/delete.do",
                            data:{"id":id}
                        },function () {
                            alertMsg("删除执行器成功");
                            location.reload();
                        })
                    }else {
                        alert("删除失败,该执行器上定义了作业");
                    }
                })
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
                $("#oldpwd").html("<font color='red'>" + '<i class="glyphicon glyphicon-remove-sign"></i>&nbsp;请输入'+(window.errorAgentPwd>=3?"密文":"原密码")+"</font>");
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
            ajax({
                type: "post",
                url: "${contextPath}/agent/pwd.do",
                data: {
                    "id": id,
                    "type":window.errorAgentPwd>=3,
                    "pwd0": pwd0,
                    "pwd1": pwd1,
                    "pwd2": pwd2
                }
            },function (data) {
                if ( data == "true" ) {
                    $('#pwdModal').modal('hide');
                    $('#password').val(pwd0);
                    alertMsg("修改成功");
                    return false;
                }
                if (data == "false") {//原密码正确,但是连接失败...
                    ++window.errorAgentPwd;
                    if (window.errorAgentPwd>=3){
                        inputSrcPwd(id);
                    }else {
                        alert("执行器原密码无效连接失败!");
                    }
                    return false;
                }
                if (data == "one") {//原密码错误
                    if( window.agentStarted!=undefined && window.agentStarted == false){
                        $("#oldpwd").html("<font color='red'>" + '<i class="glyphicon glyphicon-remove-sign"></i>&nbsp;错误! 请确保执行器端服务已经启动' + "</font>");
                    }else {
                        if (window.errorAgentPwd!=undefined && window.errorAgentPwd>=3) {
                            $("#oldpwd").html("<font color='red'>" + '<i class="glyphicon glyphicon-remove-sign"></i>&nbsp;执行器密文不正确链接失败,请检查重新输入' + "</font>");
                        }else{
                            ++window.errorAgentPwd;
                            if (window.errorAgentPwd>=3) {
                                inputSrcPwd(id);
                            }else {
                                $("#oldpwd").html("<font color='red'>" + '<i class="glyphicon glyphicon-remove-sign"></i>&nbsp;原密码不正确' + "</font>");
                            }
                        }
                    }
                    return false;
                }
                if (data == "two") {
                    $("#checkpwd").html("<font color='red'>" + '<i class="glyphicon glyphicon-remove-sign"></i>&nbsp;两次密码不一致' + "</font>");
                    return false;
                }
            })
        }

        function pingCheck() {

            var host = $("#host").val();
            if (!host) {
                alert("请填写机器Host!");
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
            if (!jobx.testIp(host)) {
                alert("请填写正确的主机地址!");
                return false;
            }
            var port = $("#port").val();
            if (!port) {
                alert("请填写端口号!");
                return false;
            }
            if (!jobx.testPort(port)) {
                alert("请填写正确的端口号!");
                return false;
            }

            $("#pingResult").html("<img src='${contextPath}/static/img/icon-loader.gif'> <font color='#2fa4e7'>检测中...</font>");

            ajax({
                type: "post",
                url: "${contextPath}/verify/ping.do",
                data: {
                    "proxy":proxy,
                    "proxyId": $("#proxyId").val(),
                    "host": host,
                    "port": port,
                    "password": password
                }
            },function (data) {
                if (data.status == 1) {
                    $("#pingResult").html("<font color='green'>" + '<i class="glyphicon glyphicon-ok-sign"></i>&nbsp;通信正常' + "</font>");
                } else if(data.status == 0){
                    $("#pingResult").html("<font color='red'>" + '<i class="glyphicon glyphicon-remove-sign"></i>&nbsp;通信失败' + "</font>");
                }else {
                    $("#pingResult").html("<font color='red'>" + '<i class="glyphicon glyphicon-remove-sign"></i>&nbsp;密码错误' + "</font>");
                }
            });
        }

        function sortPage(field) {
            location.href="${contextPath}/agent/view.htm?pageNo=${pageBean.pageNo}&pageSize=${pageBean.pageSize}&orderBy="+field+"&order="+("${pageBean.order}"=="asc"?"desc":"asc");
        }

        function inputPwd() {
            window.errorAgentPwd=0;
            $("#pwdlable").html('<i class="glyphicon glyphicon-lock"></i>&nbsp;&nbsp;原&nbsp;&nbsp;密&nbsp;&nbsp;码：');
            $("#pwd0").attr("placeholder","请输入原密码").val('');
            $("#oldpwd").html('');
            $("#pwdReset").hide();
        }

        function inputSrcPwd(id) {
            ajax({
                type: "post",
                url: "${contextPath}/agent/path.do",
                data: { "agentId": id },
                dataType:"html"
            },function (result) {
                if(result&&result.length>0) {
                    $("#pwdPath").val("more " + result);
                    $("#oldpwd").html('');
                    $("#pwdlable").html('<i class="glyphicon glyphicon-lock"></i>&nbsp;&nbsp;密&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;文：');
                    $("#pwd0").attr("placeholder","请输入密文").val('');
                    $("#pwdReset").show().val('');
                }else {
                    window.agentStarted = false;
                    alert("错误! 请确保执行器端服务已经启动");
                }
            })
        }
    </script>

    <style type="text/css">
        .visible-md i {
            font-size: 15px;
        }
        .error_msg {
            color: RED;
        }

        .file-icon {
            position: absolute;
            right: 15px;
            top: 8px;
            font-size: 19px;
            visibility: visible;
        }

        .ztree {
            width: 96.788%;
            height: 210px;
            background-color: rgba(76,77,78,.96);
            position: absolute;
            top: 34px;
            z-index: 99;
            overflow-y: auto;
            overflow-x: auto;
        }

        .ztree li a {
            color: #fff;
        }

        .ztree li a.curSelectedNode{
            background-color: #d4d4d4;
            border: none;
        }

        .treePathClose {
            cursor: pointer;
            position: absolute;
            top: 200px;
            z-index: 200;
            left: 387px;
            font-size: 30px;
        }

        .cleanPath {
            position: absolute;
            cursor: pointer;
            top: 205px;
            z-index: 200;
            left: 350px;
            font-size: 25px;
        }

        .ztree li span.button.icon_ico_open { margin-right:5px;background: url('/static/img/folder-open.png') no-repeat scroll 0 0 transparent; vertical-align:top; *vertical-align:middle}
        .ztree li span.button.icon_ico_close { margin-right:5px;background: url('/static/img/folder-close.png') no-repeat scroll 0 0 transparent; vertical-align:top; *vertical-align:middle}
        .ztree li span.button.icon_ico_docu { margin-right:5px;background: url('/static/img/file.png') no-repeat scroll 0 0 transparent; vertical-align:top; *vertical-align:middle}

    </style>

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
        <li><a href="">执行器管理</a></li>
    </ol>
    <h4 class="page-title"><i class="fa fa-desktop" aria-hidden="true"></i>&nbsp;执行器管理&nbsp;&nbsp;
        <span id="highlight" style="font-size: 14px">
            <img src='${contextPath}/static/img/icon-loader.gif' style="width: 14px;height: 14px">&nbsp;通信监测持续进行中...
        </span>
    </h4>
    <div class="block-area" id="defaultStyle">
        <div>
            <div class="opt-bar">
                <label>执行器名：</label>
                <input type="text" name="agentName" id="agentName" value="${agentName}" class="w120" placeholder="根据名称搜索"/>
                &nbsp;&nbsp;&nbsp;

                <label>通信状态：</label>
                <select id="agentStatus" name="agentStatus" class="select-jobx w80">
                    <option value="">全部</option>
                    <option value="1" ${agentStatus eq 1 ? 'selected' : ''}>成功</option>
                    <option value="0" ${agentStatus eq 0 ? 'selected' : ''}>失联</option>
                </select>
                &nbsp;&nbsp;&nbsp;
                <c:if test="${permission eq true}">
                    <a href="${contextPath}/agent/add.htm" class="btn btn-sm m-t-10"><i class="icon">&#61943;</i>添加</a>
                </c:if>
            </div>
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
                    <c:when test="${pageBean.orderBy eq 'host'}">
                        <c:if test="${pageBean.order eq 'asc'}">
                            <th  class="sortable sort-numeric sort-asc" style="cursor: pointer" onclick="sortPage('host')" title="点击排序">主机</th>
                        </c:if>
                        <c:if test="${pageBean.order eq 'desc'}">
                            <th  class="sortable sort-numeric sort-desc" style="cursor: pointer" onclick="sortPage('host')" title="点击排序">主机</th>
                        </c:if>
                    </c:when>
                    <c:when test="${pageBean.orderBy ne 'host'}">
                        <th  class="sortable sort-numeric" style="cursor: pointer" onclick="sortPage('host')" title="点击排序">主机</th>
                    </c:when>
                </c:choose>
                <c:choose>
                    <c:when test="${pageBean.orderBy eq 'port'}">
                        <c:if test="${pageBean.order eq 'asc'}">
                            <th  class="sortable sort-numeric sort-asc" style="cursor: pointer" onclick="sortPage('port')" title="点击排序">端口</th>
                        </c:if>
                        <c:if test="${pageBean.order eq 'desc'}">
                            <th  class="sortable sort-numeric sort-desc" style="cursor: pointer" onclick="sortPage('port')" title="点击排序">端口</th>
                        </c:if>
                    </c:when>
                    <c:when test="${pageBean.orderBy ne 'port'}">
                        <th  class="sortable sort-numeric" style="cursor: pointer" onclick="sortPage('port')" title="点击排序">端口</th>
                    </c:when>
                </c:choose>
                <th>通信状态</th>
                <th>失联报警</th>
                <th>连接类型</th>
                <th class="text-center">操作</th>
            </tr>
            </thead>

            <tbody id="tableContent">

            <c:forEach var="w" items="${pageBean.result}" varStatus="index">
                <tr>
                    <td id="name_${w.agentId}">${w.name}</td>
                    <td>${w.host}</td>
                    <td id="port_${w.agentId}">${w.port}</td>
                    <td>
                        <c:if test="${w.status eq 0}">
                            <span class="label label-danger">&nbsp;&nbsp;失&nbsp;联&nbsp;&nbsp;</span>
                        </c:if>
                        <c:if test="${w.status eq 1}">
                            <span class="label label-success pong_${w.agentId}">&nbsp;&nbsp;正&nbsp;常&nbsp;&nbsp;</span>
                        </c:if>
                        <c:if test="${w.status eq 2}">
                            <span class="label label-danger">&nbsp;密码错误&nbsp;</span>
                        </c:if>
                    </td>
                    <td id="warning_${w.agentId}">
                        <c:if test="${w.warning eq false}"><span class="label label-default" style="color: red;font-weight:bold">&nbsp;&nbsp;否&nbsp;&nbsp;</span> </c:if>
                        <c:if test="${w.warning eq true}"><span class="label label-warning" style="color: white;font-weight:bold">&nbsp;&nbsp;是&nbsp;&nbsp;</span> </c:if>
                    </td>
                    <td id="connType_${w.agentId}">
                        <c:if test="${w.proxyId eq null}">直连</c:if>
                        <c:if test="${w.proxyId ne null}">代理</c:if>
                    </td>
                    <td class="text-center">
                        <div class="visible-md visible-lg hidden-sm hidden-xs action-buttons">
                            <a href="${contextPath}/job/add.htm?id=${w.agentId}" title="新任务">
                                <i aria-hidden="true" class="fa fa-plus-square-o"></i>
                            </a>&nbsp;&nbsp;
                            <c:if test="${permission eq true}">
                                <a href="#" onclick="upload(${w.agentId})" title="上传文件"><i aria-hidden="true" class="fa fa-upload"></i></a>&nbsp;&nbsp;
                                <a href="#" onclick="edit('${w.agentId}')" title="编辑"><i aria-hidden="true" class="fa fa-edit"></i></a>&nbsp;&nbsp;
                                <a href="#" onclick="editPwd('${w.agentId}')" title="修改密码"><i aria-hidden="true" class="fa fa-lock"></i></a>&nbsp;&nbsp;
                                <a href="#" onclick="remove('${w.agentId}')" title="删除"><i aria-hidden="true" class="fa fa-times"></i></a>&nbsp;&nbsp;
                            </c:if>
                            <a href="${contextPath}/agent/detail/${w.agentId}.htm" title="查看详情">
                                <i aria-hidden="true" class="fa fa-eye"></i>
                            </a>
                        </div>
                    </td>
                </tr>
            </c:forEach>

            </tbody>
        </table>

        <cron:pager href="${contextPath}/agent/view.htm" id="${pageBean.pageNo}" size="${pageBean.pageSize}" total="${pageBean.totalCount}"/>

    </div>

    <!-- 修改执行器弹窗 -->
    <div class="modal fade" id="agentModal" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button class="close btn-float" data-dismiss="modal" aria-hidden="true"><i class="md md-close"></i></button>
                    <h4>修改执行器</h4>
                </div>
                <div class="modal-body">
                    <form class="form-horizontal" role="form" id="agentform">

                        <input type="hidden" id="id" name="id"><input type="hidden" id="password" name="password"><input type="hidden" id="status" name="status">
                        <div class="form-group" style="margin-bottom: 4px;">
                            <label for="host" class="col-lab control-label" title="必填项,执行器Host为IP地址,或者可以连接到该Agent的网址">机器Host：</label>
                            <div class="col-md-9">
                                <input type="text" class="form-control " id="host" readonly>&nbsp;
                            </div>
                        </div>

                        <div class="form-group" style="">
                            <label for="name" class="col-lab control-label" title="执行器名称必填">执行器名：</label>
                            <div class="col-md-9">
                                <input type="text" class="form-control " id="name">&nbsp;&nbsp;<label id="checkName"></label>
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="col-lab control-label" title="执行器通信不正常时是否发信息报警">连接类型：</label>&nbsp;&nbsp;
                            <label onclick="toggle.proxy.hide()" for="proxy0" class="radio-label"><input type="radio" onclick="toggle.proxy.hide()" name="proxy" value="0"  id="proxy0">直连</label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            <label onclick="toggle.proxy.show()" for="proxy1" class="radio-label"><input type="radio" onclick="toggle.proxy.show()" name="proxy" value="1" id="proxy1">代理&nbsp;&nbsp;&nbsp;</label>
                        </div>

                        <div class="form-group proxy" style="display: none;margin-top: 20px;">
                            <label for="proxyId" class="col-lab control-label">代&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;理：</label>
                            <div class="col-md-9">
                                <select id="proxyId" name="proxyId" class="form-control">
                                    <c:forEach var="d" items="${connAgents}">
                                        <option value="${d.agentId}" id="agent_${d.agentId}">${d.host}&nbsp;(${d.name})</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        <br>

                        <div class="form-group">
                            <label for="port" class="col-lab control-label" title="执行器端口号只能是数字,且不超过4位">端&nbsp;&nbsp;口&nbsp;&nbsp;号：</label>
                            <div class="col-md-9">
                                <input type="text" class="form-control " id="port" style="margin-bottom: 5px;"/>&nbsp;&nbsp;<a href="#" onclick="pingCheck()">
                                <i class="glyphicon glyphicon-signal"></i>&nbsp;检测通信</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<label id="pingResult"></label>
                            </div>
                        </div>
                        <div class="form-group" style="margin-top: 15px;margin-bottom: 20px">
                            <label class="col-lab control-label" title="执行器通信不正常时是否发信息报警">失联报警：</label>&nbsp;&nbsp;
                            <label onclick="toggle.contact.show()" for="warning1" class="radio-label"><input type="radio" name="warning" value="1" id="warning1">是&nbsp;&nbsp;&nbsp;</label>
                            <label onclick="toggle.contact.hide()" for="warning0" class="radio-label"><input type="radio" name="warning" value="0" id="warning0">否</label>
                        </div>
                        <div class="form-group contact">
                            <label for="mobile" class="col-lab control-label" title="执行器通信不正常时将发送短信给此手机">报警手机：</label>
                            <div class="col-md-9">
                                <input type="text" class="form-control " id="mobile"/>&nbsp;
                            </div>
                        </div>
                        <div class="form-group contact">
                            <label for="email" class="col-lab control-label" title="执行器通信不正常时将发送报告给此邮箱">报警邮箱：</label>
                            <div class="col-md-9">
                                <input type="text" class="form-control " id="email"/>&nbsp;
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="comment" class="col-lab control-label" title="此执行器描述信息">描述信息：</label>
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
                    <button class="close btn-float" data-dismiss="modal" aria-hidden="true"><i class="md md-close"></i></button>
                    <h4>修改密码</h4>
                </div>
                <div class="modal-body">
                    <form class="form-horizontal" role="form" id="pwdform">
                        <input type="hidden" id="agentId">
                        <label id="pwdReset" style="display: none;text-align: left;color:red;margin-left: 95px;padding-bottom: 10px;" for="pwd0" class="col-lab control-label">
                            <div style="margin-bottom: 10px;">
                                您已经连续三次输入无效的密码,请进入执行器下,执行下面的命令,复制密码原文到秘文输入框,或者重新输入<a href="#"  onclick="inputPwd();" style="color: dodgerblue">原密码</a>
                            </div>
                            <div style="margin-bottom: 5px;">
                                <input class="btn btn-default" id="pwdPath" type="text" style="width: 80%;text-align: left" readonly/>
                               <button class="btn btn-default" type="button" id="copy-btn" data-clipboard-action="copy" data-clipboard-target="#pwdPath" aria-label="已复制" style="width: 15%">复制</button>
                           </div>
                        </label>
                        <div class="form-group" style="margin-bottom: 4px;">
                            <label for="pwd0" id="pwdlable" class="col-lab control-label"><i class="glyphicon glyphicon-lock"></i>&nbsp;&nbsp;原&nbsp;&nbsp;密&nbsp;&nbsp;码：</label>
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
                        <button type="button" class="btn btn-sm"  onclick="inputPwd()" data-dismiss="modal">关闭</button>
                    </center>
                </div>
            </div>
        </div>
    </div>

    <!-- 文件上传 -->
    <div class="modal fade" id="upfileModal" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button class="close btn-float" data-dismiss="modal" aria-hidden="true"><i class="md md-close"></i></button>
                    <h4 id="fileTitle">文件上传</h4>
                </div>

                <div class="progress progress-striped active file-progress">
                    <div class="progress-bar" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 100%"></div><div>上传中</div>
                </div>

                <div class="modal-body">
                    <form class="form-horizontal" role="form" id="upform">
                    <div class="form-group">
                        <label for="savePath" class="col-lab control-label">&nbsp;&nbsp;<i class="glyphicon glyphicon-leaf"></i>&nbsp;保存路径</label>
                        <div class="col-md-9">
                            <input id="fileId" type="hidden">
                            <input type="text" class="form-control" id="savePath" onclick="listfile()" readonly>
                            <span class="tips" tip="目标文件在执行器上的保存路径">目标文件在执行器上的保存路径</span>
                            <ul id="treePath" class="ztree treePath" style="display: none;"></ul>
                            <span class="icon cleanPath treePath" onclick="javascript:$('#savePath').val('')">&#61771;</span>
                            <i class="md md-close treePathClose treePath" onclick="javascript:$('.treePath').hide();"></i>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="upfile" class="col-lab control-label">&nbsp;&nbsp;<i class="glyphicon glyphicon-file"></i>&nbsp;上传文件</label>
                        <div class="col-md-9">
                            <input type="file" class="form-control" data-show-preview="false" id="upfile" value="请点击上传文件" name="upfile" >
                            <span class="tips" tip="要上传到执行器的目标文件">要上传到执行器的目标文件</span>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="postcmd" class="col-lab control-label">&nbsp;&nbsp;<i class="glyphicon glyphicon-th-large"></i>&nbsp;后续动作</label>
                        <div class="col-md-9">
                            <textarea class="form-control" id="postcmd" placeholder="例: tar -xzvf $1"></textarea>
                            <span class="tips">如上传完毕解压之类的指令,非必须(该文件用$1代替)</span>
                        </div>
                        </div>
                    </form>
                </div>

                <div class="modal-footer">
                    <center>
                        <button type="button" class="btn btn-sm" id="filebtn">上传</button>
                        &nbsp;&nbsp;
                        <button type="button" class="btn btn-sm" data-dismiss="modal">关闭</button>
                    </center>
                </div>
            </div>
        </div>
    </div>

</section>

</body>

</html>


