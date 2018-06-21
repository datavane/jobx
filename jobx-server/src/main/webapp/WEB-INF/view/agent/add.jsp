<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="cron" uri="http://www.jobx.org" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="en">
<head>

    <script type="text/javascript">

        var validata = {

            status:true,

            init:function () {
                this.status = true;
            },

            name:function () {
                var _name = $("#name").val();
                if (!_name){
                    jobx.tipError("#name","执行器名称不能为空!");
                    this.status = false;
                }else{
                    if (_name.length<4 || _name.length>50){
                        jobx.tipError("#name","执行器名称不能小于4个字符并且不能超过50个字符!");
                        this.status = false;
                    }else {
                        var _this = this;
                        ajax({
                            url: "${contextPath}/agent/checkname.do",
                            type: "post",
                            data: {
                                "name": _name
                            }
                        },function (data) {
                            if (!data.status) {
                                jobx.tipError("#name","执行器名称已存在!");
                                _this.status = false;
                            }else{
                                jobx.tipOk("#name");
                            }
                        });
                    }
                }
            },

            host:function () {
                var _host = $("#host").val();
                if (!_host) {
                    jobx.tipError("#host","请填写主机地址!");
                    this.status = false;
                }else{
                    if (!jobx.testIp(_host)) {
                        jobx.tipError("#host","请填写正确的Host地址!");
                        this.status = false;
                    }else{
                        var _this = this;
                        ajax({
                            type: "post",
                            url: "${contextPath}/agent/checkhost.do",
                            data: {
                                "host": _host
                            }
                        },function (data) {
                            if (!data.status){
                                jobx.tipError("#host","该执行器Host已存在!不能重复添加!");
                                _this.status = false;
                            }else{
                                jobx.tipOk("#host");
                            }
                        })
                    }
                }

            },

            password:function () {
                var _password = $("#password").val();
                if (!_password) {
                    jobx.tipError("#password","请填写连接密码!");
                    this.status = false;
                }else{
                    jobx.tipOk("#password");
                }
            },

            port:function () {
                var _port = $("#port").val();
                if (!_port) {
                    jobx.tipError("#port","请填写端口号!");
                    this.status = false;
                }else if(!jobx.testPort(_port)) {
                    jobx.tipError("#port","请填写正确的端口号!");
                    this.status = false;
                }else {
                    validata.ping();
                }
            },

            warning:function () {
                var _warning = $('input[type="radio"][name="warning"]:checked').val();
                if ( _warning == "1" ) {
                    this.mobile();
                    this.email();
                }
            },

            mobile:function () {
                var _mobile = $("#mobile").val();
                if (!_mobile) {
                    jobx.tipError("#mobile","手机号不能为空!");
                    this.status = false;
                }else if(!jobx.testMobile(_mobile)) {
                    jobx.tipError("#mobile","手机号码格式错误!");
                    this.status = false;
                }else{
                    jobx.tipOk("#mobile");
                }
            },

            email:function () {
                var _email = $("#email").val();
                if (!_email) {
                    jobx.tipError("#email","邮箱地址不能为空!");
                    this.status = false;
                }else if(!jobx.testEmail(_email)) {
                    jobx.tipError("#email","邮箱地址错误!");
                    this.status = false;
                }else{
                    jobx.tipOk("#email");
                }
            },

            ping:function(callback) {
                $("#pingResult").html("<img src='${contextPath}/static/img/icon-loader.gif'> <font color='#2fa4e7'>检测中...</font>");
                var proxy = $('input[type="radio"][name="proxy"]:checked').val();
                var proxyId = $("#proxyId").val();

                var _this = this;
                ajax({
                    url: "${contextPath}/verify/ping.do",
                    type: "post",
                    dataType:"json",
                    data: {
                        "proxy":proxy,
                        "proxyId": proxyId,
                        "host":$("#host").val(),
                        "port": $("#port").val(),
                        "password": calcMD5($("#password").val())
                    }
                },function (data) {
                    if (data.status == 1) {
                        jobx.tipOk("#port");
                        ajax({
                            url: "${contextPath}/verify/macid.do",
                            type: "post",
                            data: {
                                "proxy":proxy,
                                "proxyId":proxyId,
                                "host":$("#host").val(),
                                "port": $("#port").val(),
                                "password": calcMD5($("#password").val())
                            }
                        },function (data) {
                            if (data.status){
                                $("#machineId").val(data.macId);
                            }
                            if(callback){
                                callback();
                            }
                        });
                    } else if(data.status == 0){
                        jobx.tipError("#port","通信失败");
                        _this.status=false;
                    }else {
                        jobx.tipError("#port","密码错误");
                        _this.status=false;
                    }
                },function () {
                    jobx.tipError("#port","通信失败");
                    _this.status=false;
                });
            },
            
            verify:function () {
                validata.init();
                validata.name();
                validata.host();
                validata.password();
                validata.port();
                validata.warning();
                return this.status;
            }

        }

        $(document).ready(function () {

            if ($('input[type="radio"][name="proxy"]:checked').val()==0) {
                $(".proxy").hide();
            } else {
                $(".proxy").show();
            }

            $("#warning0").next().click(function () {
                $(".contact").hide();
            });
            $("#warning0").parent().parent().click(function () {
                $(".contact").hide();
            });

            $("#warning1").next().click(function () {
                $(".contact").show();
                validata.warning();
            });
            $("#warning1").parent().parent().click(function () {
                $(".contact").show();
                validata.warning();
            });

            $("#proxy0").next().click(function () {
                $(".proxy").hide();
            })
            $("#proxy0").parent().click(function () {
                $(".proxy").hide();
            });

            $("#proxy1").next().click(function () {
                $(".proxy").show();
            })
            $("#proxy1").parent().click(function () {
                $(".proxy").show();
            });

            $("#saveBtn").click(function () {
                //普通字段校验
                var status = validata.verify();
                if(status){
                    //验证连接是否通...
                    validata.ping(function () {
                        $("#agentForm").submit()
                    })
                }
            });

            $("#name").blur(function () {
                validata.name();
            }).focus(function () {
                jobx.tipDefault("#name");
            });

            $("#host").blur(function () {
                validata.host();
            }).focus(function () {
                jobx.tipDefault("#host");
            });

            $("#password").blur(function () {
                validata.password();
            }).focus(function () {
                jobx.tipDefault("#password");
            });

            $("#port").blur(function () {
                validata.port();
            }).focus(function () {
                jobx.tipDefault("#port");
            });

            $("#mobile").blur(function () {
                validata.mobile();
            }).focus(function () {
                jobx.tipDefault("#mobile");
            });

            $("#email").blur(function () {
                validata.email();
            }).focus(function () {
                jobx.tipDefault("#email");
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
            <li><a href="">JobX</a></li>
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

                    <input type="hidden" name="machineId" id="machineId" value="">
                    <div class="form-group">
                        <label for="name" class="col-lab control-label wid150"><i class="glyphicon glyphicon-leaf"></i>&nbsp;&nbsp;执行器名&nbsp;&nbsp;<b>*&nbsp;</b></label>
                        <div class="col-md-10">
                            <input type="text" class="form-control input-sm" id="name" name="name">
                            <span class="tips" tip="必填项,由6-16个任意字符组成">执行器名称必填,由6-16个任意字符组成</span>
                        </div>
                    </div>
                    <br>

                    <c:if test="${empty connAgents}">
                        <!--默认为直连-->
                        <input type="hidden" name="proxy" value="0">
                    </c:if>
                    <c:if test="${!empty connAgents}">
                        <div class="form-group">
                            <label class="col-lab control-label wid150"><i class="glyphicon glyphicon-transfer "></i>&nbsp;&nbsp;连接类型&nbsp;&nbsp;<b>*&nbsp;</b></label>
                            <div class="col-md-10">
                                <label for="proxy0" class="radio-label"><input type="radio" name="proxy" value="0" id="proxy0" checked>直连</label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                <label for="proxy1" class="radio-label"><input type="radio" name="proxy" value="1" id="proxy1">代理&nbsp;&nbsp;&nbsp;</label>
                                </br><span class="tips">直连:直接连接目标执行器,代理:通过其他执行器代理连接目标执行器</span>
                            </div>
                        </div>
                        <br>

                        <div class="form-group proxy">
                            <label for="proxyId" class="col-lab control-label wid150"><i class="glyphicon glyphicon-leaf"></i>&nbsp;&nbsp;代&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;理&nbsp;&nbsp;&nbsp;&nbsp;</label>
                            <div class="col-md-10">
                                <select id="proxyId" name="proxyId" class="form-control input-sm">
                                    <c:forEach var="d" items="${connAgents}">
                                        <option value="${d.agentId}">${d.host}&nbsp;(${d.name})</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        <br>
                    </c:if>

                    <div class="form-group">
                        <label for="host" class="col-lab control-label wid150"><i class="glyphicon glyphicon-tag"></i>&nbsp;&nbsp;主机地址&nbsp;&nbsp;<b>*</b></label>
                        <div class="col-md-10">
                            <input type="text" class="form-control input-sm" id="host" name="host">
                            <span class="tips" tip="必填项,主机地址为IP地址,或者可以连接到该Agent的网址">必填项,执行器Host为IP地址,或者可以连接到该Agent的网址</span>
                        </div>
                    </div>
                    <br>

                    <div class="form-group">
                        <label for="password" class="col-lab control-label wid150"><i class="glyphicon glyphicon-lock"></i>&nbsp;&nbsp;连接密码&nbsp;&nbsp;<b>*</b></label>
                        <div class="col-md-10">
                            <input type="text" class="form-control input-sm" id="password" name="password">
                            <span class="tips" tip="必填项,链接密码是调用执行器的权限依据">必填项,链接密码是调用执行器的权限依据</span>
                        </div>
                    </div>
                    <br>

                    <div class="form-group">
                        <label for="port" class="col-lab control-label wid150 "><i class="glyphicon glyphicon-question-sign"></i>&nbsp;&nbsp;端&nbsp;&nbsp;口&nbsp;&nbsp;号&nbsp;&nbsp;<b>*</b></label>
                        <div class="col-md-10">
                            <input type="text" class="form-control input-sm" id="port" name="port">
                            <span class="tips" tip="必填项,执行器端口号为数字,范围从0到65535">必填项,执行器端口号为数字,范围从0到65535</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            <a href="#" onclick="validata.ping()"><i class="glyphicon glyphicon-signal"></i>&nbsp;检测通信</a>
                        </div>
                    </div>
                    <br>

                    <div class="form-group">
                        <label class="col-lab control-label wid150"><i class="glyphicon glyphicon-warning-sign"></i>&nbsp;&nbsp;失联报警&nbsp;&nbsp;<b>*</b></label>
                        <div class="col-md-10">
                            <label for="warning1" class="radio-label"><input type="radio" name="warning" value="1" id="warning1" checked>是&nbsp;&nbsp;&nbsp;</label>
                            <label for="warning0" class="radio-label"><input type="radio" name="warning"  value="0" id="warning0">否</label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            </br><span class="tips">执行器通信不正常时是否发信息报警</span>
                        </div>
                    </div>
                    <br>

                    <div class="form-group contact">
                        <label for="mobile" class="col-lab control-label wid150"><i class="glyphicon glyphicon-comment"></i>&nbsp;&nbsp;报警手机&nbsp;&nbsp;&nbsp;&nbsp;</label>
                        <div class="col-md-10">
                            <input type="text" class="form-control input-sm" id="mobile" name="mobile">
                            <span class="tips" tip="执行器通信不正常时将发送短信给此手机">执行器通信不正常时将发送短信给此手机</span>
                        </div>
                    </div>
                    <br>

                    <div class="form-group contact">
                        <label for="email" class="col-lab control-label wid150"><i class="glyphicon glyphicon-envelope"></i>&nbsp;&nbsp;报警邮箱&nbsp;&nbsp;&nbsp;&nbsp;</label>
                        <div class="col-md-10">
                            <input type="text" class="form-control input-sm" id="email" name="email">
                            <span class="tips" tip="执行器通信不正常时将发送报告给此邮箱">执行器通信不正常时将发送报告给此邮箱</span>
                        </div>
                    </div>
                    <br>

                    <div class="form-group">
                        <label for="comment" class="col-lab control-label wid150"><i class="glyphicon glyphicon-magnet"></i>&nbsp;&nbsp;描&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;述&nbsp;&nbsp;&nbsp;&nbsp;</label>
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