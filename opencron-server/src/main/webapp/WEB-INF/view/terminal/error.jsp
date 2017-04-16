<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="cron" uri="http://www.opencron.org" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <link rel="shortcut icon" href="${contextPath}/img/terminal.png"/>
    <jsp:include page="/WEB-INF/common/resource.jsp"/>
    <style type="text/css">
        .error_msg {
            color: red;
            margin-top: 5px;
            font-size: 12px;
        }
    </style>
    <title>Terminal Error</title>
</head>
<body>

<c:if test="${!empty terminal}">
<!-- 修改密码弹窗 -->
<div class="modal fade" id="sshModal" tabindex="-1" role="dialog" aria-hidden="false">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" onclick="closeWin();" aria-hidden="true">&times;</button>
                <h4 id="sshTitle" style="color: red;font-size: 13px;">用户名密码错误</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal" role="form" id="sshform">
                    <input type="hidden" id="sshid" value="${terminal.id}">
                    <div class="form-group" style="margin-bottom: 4px;">
                        <label for="sshname" class="col-lab control-label"><i class="glyphicon glyphicon-leaf"></i>&nbsp;&nbsp;名&nbsp;&nbsp;称&nbsp;&nbsp;：</label>
                        <div class="col-md-9">
                            <input type="text" class="form-control " id="sshname" placeholder="请输入终端的实例名称"
                                   value="${terminal.name}">&nbsp;&nbsp;<label class="error_msg"
                                                                               id="sshname_lab"></label>
                        </div>
                    </div>

                    <div class="form-group" style="margin-bottom: 4px;">
                        <label for="sshhost" class="col-lab control-label"><i class="glyphicon glyphicon-tag"></i>&nbsp;&nbsp;地&nbsp;&nbsp;址&nbsp;&nbsp;：</label>
                        <div class="col-md-9">
                            <input type="text" class="form-control " id="sshhost" placeholder="请输入主机地址(IP)"
                                   value="${terminal.host}">&nbsp;&nbsp;<label class="error_msg"
                                                                               id="sshhost_lab"></label>
                        </div>
                    </div>


                    <div class="form-group" style="margin-bottom: 4px;">
                        <label for="sshport" class="col-lab control-label"><i
                                class="glyphicon glyphicon-question-sign"></i>&nbsp;&nbsp;端&nbsp;&nbsp;口&nbsp;&nbsp;：</label>
                        <div class="col-md-9">
                            <input type="text" class="form-control " id="sshport"
                                   placeholder="请输入端口" value="${terminal.port}">&nbsp;&nbsp;<label class="error_msg"
                                                                                                   id="sshport_lab"></label>
                        </div>
                    </div>

                    <div class="form-group" style="margin-bottom: 4px;">
                        <label for="sshuser" class="col-lab control-label"><i class="glyphicon glyphicon-user"></i>&nbsp;&nbsp;帐&nbsp;&nbsp;号&nbsp;&nbsp;：</label>
                        <div class="col-md-9">
                            <input type="text" class="form-control " id="sshuser"
                                   placeholder="请输入账户">&nbsp;&nbsp;<label class="error_msg" id="sshuser_lab"></label>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="sshpwd" class="col-lab control-label"><i class="glyphicon glyphicon-lock"></i>&nbsp;&nbsp;密&nbsp;&nbsp;码&nbsp;&nbsp;：</label>
                        <div class="col-md-9">
                            <input type="password" class="form-control " id="sshpwd"
                                   placeholder="请输入密码"/>&nbsp;&nbsp;<label class="error_msg" id="sshpwd_lab"></label>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <center>
                    <button type="button" class="btn btn-sm" id="sshbtn" onclick="saveSsh()">登陆</button>
                    &nbsp;&nbsp;
                    <button type="button" class="btn btn-sm" onclick="resetForm()">重置</button>
                </center>
            </div>
        </div>
    </div>
</div>
</c:if>

<script type="text/javascript">
    <c:if test="${terminal == null}">
        swal({
            title: "",
            text: "会话已结束本次连接失败,请返回登陆重试",
            type: "warning",
            showCancelButton: false,
            closeOnConfirm: false,
            confirmButtonText: "确定"
        }, function () {
            window.opener = null;
            window.close();
        });
    </c:if>
    <c:if test="${!empty terminal}">
        $("#sshModal").modal("show");
        function saveSsh() {
            $(".error_msg").empty();

            var user = $("#sshuser").val();
            var name = $("#sshname").val();
            var pwd = $("#sshpwd").val();
            var port = $("#sshport").val();
            var host = $("#sshhost").val();
            var falg = true;

            if (!name) {
                $("#sshname_lab").text("终端实例名称不能为空");
                falg = false;
            } else {
                if (name.length > 20) {
                    $("#sshname_lab").text("终端实例名称输入太长不合法");
                    falg = false;
                }
            }

            if (!host) {
                $("#sshhost_lab").text("机器地址不能为空");
                falg = false;
            } else {
                var reg = /^([hH][tT]{2}[pP]:\/\/|[hH][tT]{2}[pP][sS]:\/\/)(([A-Za-z0-9-~]+)\.)+([A-Za-z0-9-~\/])+$/;
                //验证是否为网址
                var objExp = new RegExp(reg);
                if (!objExp.test(host)) {
                    //验证是否为IP
                    reg = /^([0-9]|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.([0-9]|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.([0-9]|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.([0-9]|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])$/;
                    if (!reg.test(host)) {
                        $("#sshhost_lab").text("机器地址不合法,必须你网址或者IP");
                        falg = false;
                    }
                }
            }

            if (!port) {
                $("#sshport_lab").text("连接端口不能为空");
                falg = false;
            } else {
                if (isNaN(port)) {
                    $("#sshport_lab").text("连接端口输入不合法,必须为数字");
                    falg = false;
                } else {
                    port = parseInt(port);
                    if (port < 0) {
                        $("#sshport_lab").text("连接端口输入不合法,不能为负数");
                        falg = false;
                    } else if (port > 65535) {
                        $("#sshport_lab").text("连接端口输入不合法,不能超过65535");
                        falg = false;
                    }
                }
            }

            if (!user) {
                $("#sshuser_lab").text("登陆账号不能为空");
                falg = false;
            } else {
                if (user.length > 255) {
                    $("#sshuser_lab").text("登陆账号太长,不合法");
                    falg = false;
                }
            }

            if (!pwd) {
                $("#sshpwd_lab").text("登陆密码不能为空");
                falg = false;
            }

            if (!falg) return;

            var host = $("#sshhost").val();

            $.ajax({
                headers:{"csrf":"${csrf}"},
                type: "POST",
                url: "${contextPath}/terminal/save",
                data: {
                    "id": ${terminal.id},
                    "name": name,
                    "userName": user,
                    "password": pwd,
                    "port": port,
                    "host": host
                },
                dataType: "html",
                success: function (status) {
                    $("#sshModal").modal("hide");
                    $("#sshform")[0].reset();
                    console.log(status)
                    if (status == "success") {
                        window.location.href="${contextPath}/terminal/ssh2?id=${terminal.id}&csrf=${csrf}";
                    } else {
                        window.setTimeout(function () {
                            $("#sshModal").modal("show");
                        },1000);
                    }
                }
            });
        }

        function closeWin() {
            window.close();
        }

        function resetForm() {
            $(".error_msg").empty();
            $("#sshuser").val('');
            $("#sshuser")[0].focus();
            $("#sshpwd").val('');
        }
    </c:if>
</script>

</body>
</html>