<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="cron" uri="http://www.opencron.org" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="en">
<head>

    <script type="text/javascript">

        var validata = {

            status:true,

            pingDone:false,

            init:function () {
                this.status = true;
                this.pingDone = false;
            },

            name:function () {
                var _name = $("#name").val();
                if (!_name){
                    opencron.tipError("#name","执行器名称不能为空!");
                    this.status = false;
                }else{
                    if (_name.length<4 || _name.length>17){
                        opencron.tipError("#name","执行器名称不能小于4个字符并且不能超过16个字符!");
                        this.status = false;
                    }else {
                        var _this = this;
                        $.ajax({
                            headers: {"csrf": "${csrf}"},
                            type: "POST",
                            url: "${contextPath}/agent/checkname.do",
                            data: {
                                "name": _name
                            },
                            success: function (data) {
                                if (!data) {
                                    opencron.tipError("#name","执行器名称已存在!");
                                    _this.status = false;
                                }else{
                                    opencron.tipOk("#name");
                                }
                            }
                        });
                    }
                }
            },

            ip:function () {
                var _ip = $("#ip").val();
                if (!_ip) {
                    opencron.tipError("#ip","请填写机器IP!");
                    this.status = false;
                }else{
                    if (!opencron.testIp(_ip)) {
                        opencron.tipError("#ip","请填写正确的IP地址!");
                        this.status = false;
                    }else{
                        var _this = this;
                        $.ajax({
                            headers: {"csrf": "${csrf}"},
                            type: "POST",
                            url: "${contextPath}/agent/checkhost.do",
                            data: {
                                "ip": _ip
                            },
                            success: function (data) {
                                if (!data){
                                    opencron.tipError("#ip","该执行器IP已存在!不能重复添加!");
                                    _this.status = false;
                                }else{
                                    opencron.tipOk("#ip");
                                }
                            }
                        })
                    }
                }

            },

            password:function () {
                var _password = $("#password").val();
                if (!_password) {
                    opencron.tipError("#password","请填写连接密码!");
                    this.status = false;
                }else{
                    opencron.tipOk("#password");
                }
            },

            port:function () {
                var _port = $("#port").val();
                if (!_port) {
                    opencron.tipError("#port","请填写端口号!");
                    this.status = false;
                }else if(!opencron.testPort(_port)) {
                    opencron.tipError("#port","请填写正确的端口号!");
                    this.status = false;
                }else {
                    validata.ping();
                }
            },

            mobiles:function () {
                var _mobiles = $("#mobiles").val();
                if (!_mobiles) {
                    opencron.tipError("#mobiles","手机号不能为空!");
                    this.status = false;
                }else if(!opencron.testMobile(_mobiles)) {
                    opencron.tipError("#mobiles","手机号码格式错误!");
                    this.status = false;
                }else{
                    opencron.tipOk("#mobiles");
                }
            },

            email:function () {
                var _email = $("#email").val();
                if (!_email) {
                    opencron.tipError("#email","邮箱地址不能为空!");
                    this.status = false;
                }else if(!opencron.testEmail(_email)) {
                    opencron.tipError("#email","邮箱地址错误!");
                    this.status = false;
                }else{
                    opencron.tipOk("#email");
                }
            },

            warning:function () {
                var _warning = $('input[type="radio"][name="warning"]:checked').val();
                if(_warning) {
                    this.mobiles();
                    this.email();
                }

            },

            ping:function() {
                $("#pingResult").html("<img src='${contextPath}/static/img/icon-loader.gif'> <font color='#2fa4e7'>检测中...</font>");
                var _ping = $('input[type="radio"][name="proxy"]:checked').val();
                var proxyId = null;
                if (_ping == 1) {
                    proxyId = $("#proxyAgent").val();
                }
                var _this=this;
                $.ajax({
                    headers: {"csrf": "${csrf}"},
                    type: "POST",
                    url: "${contextPath}/verify/ping.do",
                    data: {
                        "proxy": _ping || 0,
                        "proxyId": proxyId,
                        "ip":$("#ip").val(),
                        "port": $("#port").val(),
                        "password": calcMD5($("#password").val())
                    },
                    dataType:"JSON",
                    success: function (data) {
                        if (data) {
                            $.ajax({
                                headers: {"csrf": "${csrf}"},
                                type: "POST",
                                url: "${contextPath}/verify/guid.do",
                                data: {
                                    "proxy": _ping || 0,
                                    "proxyId": proxyId,
                                    "ip":$("#ip").val(),
                                    "port": $("#port").val(),
                                    "password": calcMD5($("#password").val())
                                },
                                dataType:"html",
                                success:function(data) {
                                    _this.pingDone = true;
                                    $("#machineId").val(data);
                                },
                                error:function () {
                                    _this.pingDone = true;
                                }
                            });
                            opencron.tipOk("#port");
                        } else {
                            opencron.tipError("#port","通信失败");
                            _this.status=false;
                            _this.pingDone = true;
                        }
                    },
                    error: function () {
                        opencron.tipError("#port","通信失败");
                        _this.status=false;
                        _this.pingDone = true;
                    }
                });
            }
        }

        $(document).ready(function () {

            if ($('input[type="radio"][name="proxy"]:checked').val()) {
                $(".proxy").show();
            } else {
                $(".proxy").hide();
            }

            $("#warning0").next().click(function () {
                $(".contact").show();
            });

            $("#warning1").next().click(function () {
                $(".contact").hide();
            });

            $("#proxy0").next().click(function () {
                $(".proxy").hide();
            });

            $("#proxy1").next().click(function () {
                $(".proxy").show();
            });

            $("#saveBtn").click(function () {
                validata.init();
                validata.name();
                validata.ip();
                validata.password();
                validata.port();
                validata.warning();
                var valId = setInterval(function () {
                    if (validata.pingDone) {
                        window.clearInterval(valId);
                        if(validata.status) {
                            $("#agentForm").submit();
                        }
                    }
                },500);
            });

            $("#name").blur(function () {
                validata.name();
            }).focus(function () {
                opencron.tipDefault("#name");
            });

            $("#ip").blur(function () {
                validata.ip();
            }).focus(function () {
                opencron.tipDefault("#ip");
            });

            $("#password").blur(function () {
                validata.password();
            }).focus(function () {
                opencron.tipDefault("#password");
            });

            $("#port").blur(function () {
                validata.port();
            }).focus(function () {
                opencron.tipDefault("#port");
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
            <li><a href="">执行器管理</a></li>
            <li><a href="">添加执行器</a></li>
        </ol>
        <h4 class="page-title"><i aria-hidden="true" class="fa fa-plus"></i>&nbsp;添加执行器</h4>

        <div style="float: right;margin-top: 5px">
            <a onclick="goback();" class="btn btn-sm m-t-10" style="margin-right: 16px;margin-bottom: -4px"><i
                    class="fa fa-mail-reply" aria-hidden="true"></i>&nbsp;返回</a>
        </div>

        <div class="block-area" id="basic">
            <div class="tile p-15">
                <form class="form-horizontal" role="form" id="agentForm" action="${contextPath}/agent/add.do" method="post"></br>
                    <input type="hidden" name="csrf" value="${csrf}">
                    <input type="hidden" name="machineId" id="machineId" value="">
                    <div class="form-group">
                        <label for="name" class="col-lab control-label"><i class="glyphicon glyphicon-leaf"></i>&nbsp;&nbsp;执行器名：</label>
                        <div class="col-md-10">
                            <input type="text" class="form-control input-sm" id="name" name="name">
                            <span class="tips" tip="<b>*&nbsp;</b>必填项,由6-16个任意字符组成"><b>*&nbsp;</b>执行器名称必填,由6-16个任意字符组成</span>
                        </div>
                    </div>
                    <br>

                    <c:if test="${empty connAgents}">
                        <!--默认为直连-->
                        <input type="hidden" name="proxy" value="0">
                    </c:if>
                    <c:if test="${!empty connAgents}">
                        <div class="form-group">
                            <label class="col-lab control-label"><i class="glyphicon glyphicon-transfer"></i>&nbsp;&nbsp;连接类型：</label>&nbsp;&nbsp;&nbsp;
                            <div class="col-md-10">
                                <label for="proxy0" class="radio-label"><input type="radio" name="proxy" value="0" id="proxy0" checked>直连</label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                <label for="proxy1" class="radio-label"><input type="radio" name="proxy" value="1" id="proxy1">代理&nbsp;&nbsp;&nbsp;</label>
                                </br><span class="tips"><b>*&nbsp;</b>直连:直接连接目标执行器,代理:通过其他执行器代理连接目标执行器</span>
                            </div>
                        </div>
                        <br>

                        <div class="form-group proxy" style="display: none;">
                            <label for="proxyAgent" class="col-lab control-label"><i class="glyphicon glyphicon-leaf"></i>&nbsp;&nbsp;代&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;理：</label>
                            <div class="col-md-10">
                                <select id="proxyAgent" name="proxyAgent" class="form-control input-sm">
                                    <c:forEach var="d" items="${connAgents}">
                                        <option value="${d.agentId}">${d.ip}&nbsp;(${d.name})</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        <br>
                    </c:if>

                    <div class="form-group">
                        <label for="ip" class="col-lab control-label"><i class="glyphicon glyphicon-tag"></i>&nbsp;&nbsp;机&nbsp;&nbsp;器&nbsp;&nbsp;IP：</label>
                        <div class="col-md-10">
                            <input type="text" class="form-control input-sm" id="ip" name="ip">
                            <span class="tips" tip="<b>*&nbsp;</b>必填项,执行器IP地址只能为点分十进制方式表示,如192.168.0.1"><b>*&nbsp;</b>必填项,执行器IP地址只能为点分十进制方式表示,如192.168.0.1</span>
                        </div>
                    </div>
                    <br>

                    <div class="form-group">
                        <label for="password" class="col-lab control-label"><i class="glyphicon glyphicon-lock"></i>&nbsp;&nbsp;连接密码：</label>
                        <div class="col-md-10">
                            <input type="text" class="form-control input-sm" id="password" name="password">
                            <span class="tips" tip="<b>*&nbsp;</b>必填项,链接密码是调用执行器的权限依据"><b>*&nbsp;</b>必填项,链接密码是调用执行器的权限依据</span>
                        </div>
                    </div>
                    <br>

                    <div class="form-group">
                        <label for="port" class="col-lab control-label"><i class="glyphicon glyphicon-question-sign"></i>&nbsp;&nbsp;端&nbsp;&nbsp;口&nbsp;&nbsp;号：</label>
                        <div class="col-md-10">
                            <input type="text" class="form-control input-sm" id="port" name="port">
                            <span class="tips" tip="<b>*&nbsp;</b>必填项,执行器端口号为数字,范围从0到65535"><b>*&nbsp;</b>必填项,执行器端口号为数字,范围从0到65535</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            <a href="#" onclick="validata.ping()"><i class="glyphicon glyphicon-signal"></i>&nbsp;检测通信</a>
                        </div>
                    </div>
                    <br>

                    <div class="form-group">
                        <label class="col-lab control-label"><i class="glyphicon glyphicon-warning-sign"></i>&nbsp;&nbsp;失联报警：</label>&nbsp;&nbsp;&nbsp;
                        <div class="col-md-10">
                            <label for="warning1" class="radio-label"><input type="radio" name="warning" value="1" id="warning1" checked>是&nbsp;&nbsp;&nbsp;</label>
                            <label for="warning0" class="radio-label"><input type="radio" name="warning"  value="0" id="warning0">否</label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            </br><span class="tips"><b>*&nbsp;</b>执行器通信不正常时是否发信息报警</span>
                        </div>
                    </div>
                    <br>

                    <div class="form-group contact">
                        <label for="mobiles" class="col-lab control-label"><i class="glyphicon glyphicon-comment"></i>&nbsp;&nbsp;报警手机：</label>
                        <div class="col-md-10">
                            <input type="text" class="form-control input-sm" id="mobiles" name="mobiles">
                            <span class="tips" tip="<b>*&nbsp;</b>执行器通信不正常时将发送短信给此手机"><b>*&nbsp;</b>执行器通信不正常时将发送短信给此手机</span>
                        </div>
                    </div>
                    <br>

                    <div class="form-group contact">
                        <label for="email" class="col-lab control-label"><i class="glyphicon glyphicon-envelope"></i>&nbsp;&nbsp;报警邮箱：</label>
                        <div class="col-md-10">
                            <input type="text" class="form-control input-sm" id="email" name="emailAddress">
                            <span class="tips" tip="<b>*&nbsp;</b>执行器通信不正常时将发送报告给此邮箱"><b>*&nbsp;</b>执行器通信不正常时将发送报告给此邮箱</span>
                        </div>
                    </div>
                    <br>

                    <div class="form-group">
                        <label for="comment" class="col-lab control-label"><i class="glyphicon glyphicon-magnet"></i>&nbsp;&nbsp;描&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;述：</label>
                        <div class="col-md-10">
                            <textarea class="form-control input-sm" id="comment" name="comment"></textarea>
                        </div>
                    </div>
                    <br>

                    <div class="form-group">
                        <div class="col-md-offset-1 col-md-10">
                            <button type="button" id="saveBtn" class="btn btn-sm m-t-10"><i class="icon">&#61717;</i>&nbsp;保存
                            </button>&nbsp;&nbsp;
                            <button type="button" onclick="history.back()" class="btn btn-sm m-t-10"><i class="icon">&#61740;</i>&nbsp;取消
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </div>

    </section>

</body>
</html>